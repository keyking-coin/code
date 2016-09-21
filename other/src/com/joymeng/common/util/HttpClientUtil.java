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
	
	static HttpClient client;

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
