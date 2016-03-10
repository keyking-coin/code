package com.keyking.coin.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.RemarkNode;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;

public class HttpDecoderUtil {

	static String URL = "http://www.zgqbyp.com/web/xxpl";

	static HttpClient client;

	public static void main(String[] args) {
		try {
			//getInfoFromNet("http://www.zgqbyp.com/html/2015-7/201571846879.html");
			HttpPost post = new HttpPost("http://127.0.0.1:32104/HttpLogin?account=13856094894&pwd=123456789");
			HttpResponse resp = getHttpResponse(post);
			System.out.println(resp.getEntity().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static HttpResponse getHttpResponse(HttpGet request)
			throws ClientProtocolException, IOException {
		if (client == null) {
			client = new DefaultHttpClient();
		}
		HttpResponse response = client.execute(request);
		return response;
	}

	public static HttpResponse getHttpResponse(HttpPost request)
			throws ClientProtocolException, IOException {
		if (client == null) {
			client = new DefaultHttpClient();
		}
		HttpResponse response = new DefaultHttpClient().execute(request);
		return response;
	}
	
	public static byte[] readFromStream(InputStream in) throws Exception{
		int count;
		byte data[] = new byte[1024]; 
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((count = in.read(data,0,1024)) != -1) {  
			bos.write(data, 0, count);
		}
		return bos.toByteArray();
	}
	
	public static String getHQFromNet(){
		try {
			HttpPost post = new HttpPost("http://180.97.2.74:16929/tradeweb/hq/getHqV_lbData.jsp");
			HttpResponse resp = getHttpResponse(post);
			HttpEntity entity = resp.getEntity();
			byte[] datas = readFromStream(entity.getContent());
			String result = new String(datas,"UTF-8");
			int index = result.indexOf('{');
			result = result.substring(index);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getInfoFromNet(String url){
		try {
			Parser parser = new Parser((HttpURLConnection) (new URL(url)).openConnection());
			NodeFilter filter1 = new TagNameFilter("div");
			NodeFilter filter2 = new HasAttributeFilter("class","neirong");
			NodeFilter filter = new AndFilter(filter1,filter2);
			NodeList nodes = parser.extractAllNodesThatMatch(filter);
			StringBuffer buffer = new StringBuffer();
			if (nodes != null){
				buffer.append("{");
				Node node = nodes.elementAt(0);
				NodeList children = node.getChildren();
				for (int i = 0 ; i < children.size() ; i ++){
					Node child = children.elementAt(i);
					if (child instanceof RemarkNode){
						continue;
					}
					if (child instanceof Div){
						Div div = (Div)child;
						String value = div.getAttribute("class");
						if (value.equals("neirong_title")){
							buffer.append("\"title\":\"" + clearStr(child.toPlainTextString()) + "\",");
						}else if (value.equals("neirong_text")){
							NodeList dcs = div.getChildren();
							int sc = 0;
							StringBuffer sb = new StringBuffer();
							sb.append("[");
							for (int di = 0 ; di < dcs.size() ; di ++){
								Node dc = dcs.elementAt(di);
								String tableStr = hasTableInchild(dc);
								if (tableStr != null){
									sb.append("{\"t\":" + tableStr + "},");
									continue;
								}
								String nr = clearStr(dc.toPlainTextString());
								if (nr == null || nr.equals("")){
									continue;
								}
								if (nr.equals("南京文化艺术产权交易所")){
									sc = 1;
								}
								if (sc > 0){
									sb.append("{\"s\":\"" + nr + "\"},");
									sc ++;
								}else{
									sb.append("{\"p\":\"" + nr + "\"},");
								}
								if (sc == 4){
									break;
								}
							}
							sb.deleteCharAt(sb.length()-1);
							sb.append("]");
							buffer.append("\"content\":" + sb.toString());
							break;
						}
					}
				}
				buffer.append("}");
			}
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static TableTag searchTable(Node node){
		if (node instanceof TableTag){
			return (TableTag)node;
		}
		NodeList children = node.getChildren();
		if (children != null){
			NodeFilter filter = new TagNameFilter("table");
			NodeList nodes = children.extractAllNodesThatMatch(filter);
			if (nodes != null && nodes.size() > 0){
				return (TableTag)nodes.elementAt(0);
			}
		}
		return null;
	}
	
	private static String hasTableInchild(Node node){
		TableTag table = searchTable(node);
		if (table != null){
			TableRow[] rows = table.getRows();
			StringBuffer mBuffer = new StringBuffer();
			mBuffer.append("[");
			for (int i = 0 ; i < rows.length ; i++){
				TableRow row = rows[i];
				TableColumn[] cols = row.getColumns();
				for (int j = 0 ; j < cols.length ; j++){
					TableColumn col = cols[j];
					String dec = clearStr(col.toPlainTextString());
					String rs = col.getAttribute("rowspan");
					String cs = col.getAttribute("colspan");
					int nc = cs == null ? 1 : Integer.parseInt(cs);
					int nr = rs == null ? 1 : Integer.parseInt(rs);
					int nw = comput_width(table,j);
					mBuffer.append("{\"w\":" + nw + ",\"row\":"+ i + ",\"rs\":" + nr + ",\"cs\":" + nc + ",\"nr\":\"" + dec + "\"},");
				}
			}
			mBuffer.deleteCharAt(mBuffer.length()-1);
			mBuffer.append("]");
			return mBuffer.toString();
		}
		return null;
	}
	
	public static int comput_width(TableTag table , int col){
		TableRow[] rows = table.getRows();
		int max = 0;
		for (int i = 0 ; i < rows.length ; i++){
			TableColumn[] columns = rows[i].getColumns();
			if (col >= columns.length){
				continue;
			}
			TableColumn column = columns[col];
			String dec = clearStr(column.toPlainTextString());
			if (dec == null || dec.equals("")){
				continue;
			}
			String cs = column.getAttribute("colspan");
			int nc = cs == null ? 1 : Integer.parseInt(cs);
			if (nc > 1){
				continue;
			}
			String rs = column.getAttribute("rowspan");
			int nr = rs == null ? 1 : Integer.parseInt(rs);
			if (nr > 1){
				continue;
			}
			int len = 0;
			for (int j = 0 ; j < dec.length() ; j++){
				char ch = dec.charAt(j);
				if((ch >= '\u4e00' && ch <= '\u9fa5') || (ch >= '\uf900' && ch <='\ufa2d')){
					len += 12;
				}else{
					len += 24;
				}
			}
			if (len  > max){
				max = len;
			}
		}
		return max;
	}
	
	private static String clearStr(String str){
		StringBuffer sb = new StringBuffer();
		if (sb.length() > 0){
			sb.delete(0, sb.length());
		}
		for (int j = 0 ; j < str.length() ; j++){
			char ch = str.charAt(j);
			if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r'){
				continue;
			}
			sb.append(ch);
		}
		String result = sb.toString();
		result = result.replaceAll("&nbsp;","");
		return result;
	}
}
