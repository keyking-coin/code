package com.keyking.coin.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import org.joda.time.DateTime;

import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class PictureUtil {
	
	public static String tryWriteToDisk(byte[] datas) throws Exception {
		StringBuffer sb = new StringBuffer();
		DateTime time = TimeUtils.now();
		sb.append("pics/" + time.getYear());
		sb.append("/" + time.getMonthOfYear());
		sb.append("/" + time.getDayOfMonth() + "/");
		String path = sb.toString();
		sb.append("pic-" + time.getHourOfDay());
		sb.append("-" + time.getMinuteOfHour());
		sb.append("-" + time.getSecondOfMinute());
		sb.append(".png");
		File file = new File(path);
		if (!file.exists()){
			file.mkdirs();
		}
		file = new File(sb.toString());
		if (!file.exists()){
			file.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(datas);
		fos.flush();
		fos.close();
		return sb.toString();
	}
	
	public static byte[] tryLoadPicData(String name) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		File file = new File(name);
		FileInputStream in = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int i = -1;
		do
		{
			i = in.read(buffer);
			if (i == -1){
				break;
			}
			out.write(buffer,0,i);
		}while(true);
		in.close();
		byte[] result = out.toByteArray();
		out.close();
		return result;
	}
	
	public static void tryInitPicWH(String name,GeneralResp resp) throws Exception {
		BufferedImage image = ImageIO.read(new File(name));
		resp.add(image.getWidth());
		resp.add(image.getHeight());
	}
	
	public static void main(String[] args) throws Exception{
		FileOutputStream fos = new FileOutputStream("test");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String str = "测试代码123abc";
		byte[] datas = str.getBytes("UTF-8");
		out.write(datas.length);
		out.write(datas);
		out.writeTo(fos);
	}
}
