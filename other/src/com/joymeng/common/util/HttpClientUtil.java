package com.joymeng.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;

public class HttpClientUtil {

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
			client = HttpClients.createDefault();
		}
		HttpResponse response = client.execute(request);
		return response;
	}

	public static HttpResponse getHttpResponse(HttpPost request)
			throws ClientProtocolException, IOException {
		if (client == null) {
			client = HttpClients.createDefault();
		}
		HttpResponse response = client.execute(request);
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
}
