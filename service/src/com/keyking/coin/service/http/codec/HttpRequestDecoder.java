package com.keyking.coin.service.http.codec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import com.keyking.coin.service.http.request.HttpRequestMessage;

public class HttpRequestDecoder extends MessageDecoderAdapter {
	private static final byte[] CONTENT_LENGTH = new String("Content-Length:").getBytes();
	public static String defaultEncoding;
	
	private CharsetDecoder decoder;

	public CharsetDecoder getDecoder() {
		return decoder;
	}

	public void setEncoder(CharsetDecoder decoder) {
		this.decoder = decoder;
	}

	private HttpRequestMessage request = null;

	public HttpRequestDecoder() {
		decoder = Charset.forName(defaultEncoding).newDecoder();
	}
	
	@Override
	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		try {
			return messageComplete(in) ? MessageDecoderResult.OK : MessageDecoderResult.NEED_DATA;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return MessageDecoderResult.NOT_OK;
	}

	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer in,ProtocolDecoderOutput out) throws Exception {
		HttpRequestMessage m = decodeBody(in);
		if (m == null) {
			return MessageDecoderResult.NEED_DATA;
		}
		out.write(m);
		return MessageDecoderResult.OK;
	}

	/*
	 * 判断HTTP请求是否完整，若格式有错误直接抛出异常
	 */
	private boolean messageComplete(IoBuffer in) {
		int last = in.remaining() - 1;
		if (in.remaining() < 4) {
			return false;
		}

		// to speed up things we check if the Http request is a GET or POST
		if (in.get(0) == (byte) 'G' && in.get(1) == (byte) 'E'
				&& in.get(2) == (byte) 'T') {
			// Http GET request therefore the last 4 bytes should be 0x0D 0x0A
			// 0x0D 0x0A
			return in.get(last) == (byte) 0x0A
					&& in.get(last - 1) == (byte) 0x0D
					&& in.get(last - 2) == (byte) 0x0A
					&& in.get(last - 3) == (byte) 0x0D;
		} else if (in.get(0) == (byte) 'P' && in.get(1) == (byte) 'O'
				&& in.get(2) == (byte) 'S' && in.get(3) == (byte) 'T') {
			// Http POST request
			// first the position of the 0x0D 0x0A 0x0D 0x0A bytes
			int eoh = -1;
			for (int i = last; i > 2; i--) {
				if (in.get(i) == (byte) 0x0A && in.get(i - 1) == (byte) 0x0D
						&& in.get(i - 2) == (byte) 0x0A
						&& in.get(i - 3) == (byte) 0x0D) {
					eoh = i + 1;
					break;
				}
			}
			if (eoh == -1) {
				return false;
			}
			for (int i = 0; i < last; i++) {
				boolean found = false;
				for (int j = 0; j < CONTENT_LENGTH.length; j++) {
					if (in.get(i + j) != CONTENT_LENGTH[j]) {
						found = false;
						break;
					}
					found = true;
				}
				if (found) {
					// retrieve value from this position till next 0x0D 0x0A
					StringBuilder contentLength = new StringBuilder();
					for (int j = i + CONTENT_LENGTH.length; j < last; j++) {
						if (in.get(j) == 0x0D) {
							break;
						}
						contentLength.append(new String(new byte[] { in.get(j) }));
					}
					return Integer.parseInt(contentLength.toString().trim())+ eoh == in.remaining();
				}
			}
		}

		// the message is not complete and we need more data
		return false;

	}

	private HttpRequestMessage decodeBody(IoBuffer in) {
		request = new HttpRequestMessage();
		try {
			request.setHeaders(parseRequest(new StringReader(in.getString(decoder))));
			return request;
		} catch (CharacterCodingException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private Map<String, String[]> parseRequest(StringReader is) {
		Map<String, String[]> map = new HashMap<String, String[]>();
		BufferedReader rdr = new BufferedReader(is);

		try {
			// Get request URL.
			String line = rdr.readLine();
			String[] url = line.split(" ");
			if (url.length < 3) {
				return map;
			}

			map.put("URI", new String[] { line });
			map.put("Method", new String[] { url[0].toUpperCase() });
			map.put("Context", new String[] { url[1].substring(1) });
			map.put("Protocol", new String[] { url[2] });
			//Read header
			while ((line = rdr.readLine()) != null && line.length() > 0) {
				String[] tokens = line.split(": ");
				map.put(tokens[0], new String[] { tokens[1] });
			}
			int idx = url[1].indexOf('?');
			if (idx != -1) {
				map.put("Context",new String[] { url[1].substring(1, idx) });
				line = url[1].substring(idx + 1);
			} else {
				line = null;
			}
			if (line != null) {
				/*
				String[] match = line.split("\\&");
				for (String element : match) {
					String[] params = new String[1];
					String[] tokens = element.split("=");
					switch (tokens.length) {
					case 0:
						map.put("@".concat(element), new String[] {});
						break;
					case 1:
						map.put("@".concat(tokens[0]), new String[] {});
						break;
					default:
						String name = "@".concat(tokens[0]);
						if (map.containsKey(name)) {
							params = map.get(name);
							String[] tmp = new String[params.length + 1];
							for (int j = 0; j < params.length; j++) {
								tmp[j] = params[j];
							}
							params = null;
							params = tmp;
						}
						String str = URLDecoder.decode(tokens[1].trim(),"UTF-8");
						params[params.length - 1] = str;
						map.put(name, params);
					}
				}*/
				parseParam(line,map);
			}
			if (url[0].equalsIgnoreCase("POST")) {
				char[] buf = new char[1024];
				int blen = 0;
				StringBuffer sb = new StringBuffer();
				while((blen = rdr.read(buf)) > 0){
					for (int i = 0 ; i < blen ; i++){
						sb.append(buf[i]);
					}
				}
				String[] params = new String[1];
				params[0] = sb.toString();
				map.put("$value", params);
			} 
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return map;
	}

	private void parseParam(String str,Map<String, String[]> map){
		int index = 0;
		do{
			index = str.indexOf("&");
			String s1 = null;
			if (index == -1){
				s1 = str;
			}else{
				s1 = str.substring(0,index);
			}
			int s = s1.indexOf("[u_s]");
			int e  = str.indexOf("[u_e]");
			if (s >= 0 && e >= 0){//特殊的url地址参数
				s1 = str.substring(s+5,e);
				if (e + 6 < str.length()){
					str = str.substring(e + 6,str.length());
				}
			}else{
				str = str.substring(index + 1,str.length());
			}
			int ei = s1.indexOf("=");
			if (ei > 0){
				String key   = s1.substring(0,ei);
				if (ei + 1 == s1.length()){
					map.put("@".concat(key),new String[] {});
				}else{
					String value = s1.substring(ei+1,s1.length());
					insert(key,value,map);
				}
			}else{
				map.put("@".concat(s1),new String[] {});
			}
		}while(index > 0);
	}
	
	private void insert(String key,String vaule,Map<String, String[]> map){
		String name = "@".concat(key);
		String[] params = new String[1];
		if (map.containsKey(name)) {
			params = map.get(name);
			String[] tmp = new String[params.length + 1];
			for (int j = 0; j < params.length; j++) {
				tmp[j] = params[j];
			}
			params = null;
			params = tmp;
		}
		String str = null;
		try {
			str = URLDecoder.decode(vaule.trim(),"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		params[params.length - 1] = str;
		map.put(name,params);
	}
}
