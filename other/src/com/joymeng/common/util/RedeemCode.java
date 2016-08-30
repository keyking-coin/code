package com.joymeng.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RedeemCode {

	public static void getCode(String channelid, String code)
			throws IOException {

		URL url = new URL(
				"http://netunion.joymeng.com/index.php?m=Api&c=Index&a=getCode&appid=1001&channelid="
						+ channelid + "&code=" + code + "");
		HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
		httpUrl.setRequestMethod("GET");
		httpUrl.connect();
		InputStream in = httpUrl.getInputStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[2048];
		int length = 0;
		while ((length = in.read(buffer)) != -1) {
			bos.write(buffer, 0, length);
		}
		in.close();
		String s = new String(bos.toByteArray(), "UTF-8");
		// Map<String,Object> map =JsonUtil.JsonToObject(s, Map.class);
		System.out.println(s);
	}

	public static void getCodeByPost(String channelid, String code)
			throws IOException {

		// http://netunion.joymeng.com/index.php?m=Api&c=Index&a=getCode&appid=1001&channelid=0000001&code=yTSmbo

		try {
			URL url = new URL(
					"http://netunion.joymeng.com/index.php?m=Api&c=Index&a=getCode&appid=1001");
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);// 是否输入参数
			StringBuffer params = new StringBuffer();
			params.append("&").append("appid").append("=").append("1001")
					.append("&").append("channelid").append("=")
					.append(channelid).append("&").append("code").append("=")
					.append(code);
			byte[] bypes = params.toString().getBytes();
			connection.getOutputStream().write(bypes);// 输入参数

			InputStream in = connection.getInputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];
			int length = 0;
			while ((length = in.read(buffer)) != -1) {
				bos.write(buffer, 0, length);
			}
			in.close();
			String s = new String(bos.toByteArray(), "GBK");
			System.out.println(s);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public static void back(long codeid, long uid, long serverid)
			throws IOException {

		try {
			URL url = new URL(
					"http://netunion.joymeng.com/index.php?m=Api&c=Index&a=markCode&codeid="
							+ codeid + "&use=1&uid=" + uid + "&serverid="
							+ serverid + "");
			HttpURLConnection httpUrl = (HttpURLConnection) url
					.openConnection();
			httpUrl.connect();
			InputStream in = httpUrl.getInputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];
			int length = 0;
			while ((length = in.read(buffer)) != -1) {
				bos.write(buffer, 0, length);
			}
			in.close();
			String s = new String(bos.toByteArray(), "UTF-8");
			System.out.println(s);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
