package com.joymeng.http.handler;

import java.io.FileInputStream;
import java.io.InputStream;

import com.joymeng.Instances;
import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.http.response.HttpResponseMessage;

public abstract class HttpHandler implements Instances{
	public abstract boolean handle(HttpRequestMessage request,HttpResponseMessage response); 

	public void message(HttpResponseMessage response,String tips){
		response.appendBody("{\"result\":\"" + tips + "\"}");
	}
	
	public void dispatcher(String fileName,HttpResponseMessage response){
		try {
			InputStream in = new FileInputStream("web/" + fileName);
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = in.read(buffer)) != -1) {
				response.appendBody(buffer,0,len);
			}
			in.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
