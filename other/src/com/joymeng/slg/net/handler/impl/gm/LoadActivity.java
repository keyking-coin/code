package com.joymeng.slg.net.handler.impl.gm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.joymeng.http.HtppOprateType;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;

public class LoadActivity extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());// 从哪个服务器来的请求
			long uid = in.getLong();
			int serverId = in.getInt();
			String loaderTime = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			String loaderUrl = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			params.put(uid);
			params.put(serverId);
			params.put(loaderTime);
			params.put(loaderUrl);
		} else {
			params.put(in.get());// 判断结果
			params.put(in.getInt()); // 从哪个服务器来的
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT)); // 拼接的字符串
		}
	}

	@Override
	public JoyProtocol handle(final UserInfo info, final ParametersEntity params)
			throws Exception {
		int type = params.get(0);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {// 请求
			int fromId = params.get(1);// 从哪个服务器来的请求,回到哪去
			int serverId = params.get(3);
//			String loaderTime = params.get(4);  //更新时间
			String loaderUrl = params.get(5);   // 下载路径
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setEid(serverId);
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x00000083;
			TransmissionResp resp = newTransmissionResp(targetInfo);
			resp.getParams().put(protocolId);// 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
			resp.getParams().put(TransmissionResp.JOY_RESP_SUCC);
			resp.getParams().put(serverId);
			
			ZipInputStream zin; // 创建ZipInputStream对象
			try {
				URL url = new URL(loaderUrl);
				HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
				httpUrl.connect();
				zin = new ZipInputStream(httpUrl.getInputStream());// 实例化对象，指明要进行解压的文件
				ZipEntry entry = null; // 获取下一个ZipEntry
				while ((entry = zin.getNextEntry()) != null) {// 如果entry不为空
					byte[] datas = new byte[(int) entry.getSize()];
					zin.read(datas, 0, datas.length);
					String sName = entry.getName();
					if (sName.contains("Activity.xml")|| sName.contains("ServerActivities")) {
						int index = sName.lastIndexOf("/");
						String tail = sName.substring(index + 1, sName.length());
						if (tail.equals("")) {
							continue;
						}
						File file = new File("./activity/prepare/" + tail); // 获取文件目录
						if(!file.getParentFile().exists()){
							file.getParentFile().mkdirs();
						}
						if (!file.exists()) {
							file.createNewFile();
						}  
					  
						OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
						BufferedWriter writer = new BufferedWriter(write);
						String res = new String(datas, "GBK");
						writer.write(res);
						writer.flush();
						writer.close();
					}
				}
				zin.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			resp.getParams().put("1");
			return resp;
		
		} else {
			byte result = params.get(1);
			int sid = params.get(2);
			NeedContinueDoSomthing next = search(info.getUid(),sid);
			if (next != null) {
				if (result == TransmissionResp.JOY_RESP_SUCC) {
					next.succeed(info, params);
				} else {
					next.fail(info, params);
				}
				removeNextDo(info.getUid(),next);
			}
			return null;
		}
	}
}
