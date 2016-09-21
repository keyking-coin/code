package com.joymeng.common.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import com.joymeng.log.GameLog;
import com.joymeng.services.core.JoyServiceApp;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.DataModule;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.world.TaskPool;
import com.joymeng.slg.world.World;

public class MessageSendUtil {
	
	public static final byte TIP_TYPE_NORMAL        = 0;//普通模式
	public static final byte TIP_TYPE_HORIZONTAL    = 1;//水平滚动
	public static final byte TIP_TYPE_VERTICAL      = 2;//垂直滚动
	public static final byte TIP_TYPE_SYSTEM        = 3;//系统临时通知
	public static final byte DATA_NORMAL_MODULE     = 0;//数据普通模式
	public static final byte DATA_ZIP_MODULE        = 1;//数据压缩模式
	public static final byte DATA_SPLIT_MODULE      = 2;//数据压缩并且拆分模式
	public static final int DATA_ZIP_LIMIT          = 2 * 1024;//数据包数据超过5k，就压缩一下
	//public static final int DATA_TRANFORM_MAX_LEN  = 51000;//单包传输字节上限
	public static final int DATA_TRANFORM_MAX_LEN   = 4 * 1024;//单包传输字节上限
	
	public static void sendTip(byte type,String str,UserInfo info,Object... params){
		RespModuleSet resp = new RespModuleSet();
		AbstractClientModule module = new AbstractClientModule(){
			@Override
			public short getModuleType() {
				return NTC_DTCD_MESSAGE_TIP;
			}
		};
		module.add(type);
		module.add(str);
		if (params != null){
			module.add(params.length);
			for (int i = 0 ; i < params.length ; i++){
				Object obj = params[i];
				module.add(obj.toString());
			}
		}else{
			module.add(0);
		}
		resp.addModule(module);
		sendModule(resp,info);
	}

	public static void sendNormalTip(UserInfo info,String str,Object... params){
		sendTip(TIP_TYPE_NORMAL,str,info,params);
	}
	
	public static void sendModule(RespModuleSet resp , Role role){
		sendModule(resp,role.getUserInfo());
	}
	
	public static void tipModule(RespModuleSet rms,byte type,String str,Object... params){
		AbstractClientModule module = new AbstractClientModule(){
			@Override
			public short getModuleType() {
				return NTC_DTCD_MESSAGE_TIP;
			}
		};
		module.add(type);
		module.add(str);
		if (params != null){
			module.add(params.length);
			for (int i = 0 ; i < params.length ; i++){
				Object obj = params[i];
				module.add(obj.toString());
			}
		}else{
			module.add(0);
		}
		rms.addModule(module);
	}
	
	public static void sendModule(RespModuleSet resp,UserInfo info){
		if (info == null){
			return;
		}
		resp.setUserInfo(info);
		JoyServiceApp.getInstance().sendMessage(resp);
	}
	
	public static void sendMessageToOnlineRole(byte type,String str,Object... params){
		RespModuleSet resp = new RespModuleSet();
		AbstractClientModule module = new AbstractClientModule(){
			@Override
			public short getModuleType() {
				return NTC_DTCD_MESSAGE_TIP;
			}
		};
		module.add(type);
		module.add(str);
		if (params != null){
			module.add(params.length);
			for (int i = 0 ; i < params.length ; i++){
				Object obj = params[i];
				module.add(obj.toString());
			}
		}else{
			module.add(0);
		}
		resp.addModule(module);
		List<Role> roles = World.getInstance().getOnlineRoles();
		for (int i = 0 ; i < roles.size() ; i++){
			Role role = roles.get(i);
			sendModule(resp,role);
		}
		GameLog.info("send message to all online player : " + str);
	}
	
	public static void sendMessageToOnlineRole(RespModuleSet resp , Role role){
		long excepte = role == null ? 0 : role.getId();
		List<Role> roles = World.getInstance().getOnlineRoles();
		for (int i = 0 ; i < roles.size() ; i++){
			Role r = roles.get(i);
			if (r.getId() != excepte){
				MessageSendUtil.sendModule(resp,r);
			}
		}
	}
	
	public static void checkAndZip(JoyBuffer out,byte[] datas,UserInfo info,boolean splitPackage) throws Exception{
		if (!splitPackage && datas.length > DATA_ZIP_LIMIT){//非分包数据包才做压缩逻辑
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			GZIPOutputStream gos = new GZIPOutputStream(baos);  
			gos.write(datas,0,datas.length); 
			gos.finish();
			gos.flush();
			gos.close();
			byte[] smallData = baos.toByteArray();
			if (smallData.length > DATA_TRANFORM_MAX_LEN){//压缩后还是大于单包传输字节上限
				out.put(DATA_SPLIT_MODULE);
				int cursor = 0;
				List<Long> ids = new ArrayList<Long>();
				do{
					int len = Math.min(DATA_TRANFORM_MAX_LEN,smallData.length-cursor);
					byte[] data = new byte[len];
					System.arraycopy(smallData,cursor,data,0,len);
					DataModule module = new DataModule(data,info);
					ids.add(module.getId());
					cursor += len;
					TaskPool.getInstance().dataTransform.addModule(module);
				}while(cursor < smallData.length);
				out.putInt(ids.size());
				for (int i = 0 ; i < ids.size() ; i++){
					long dataId = ids.get(i).longValue();
					out.putLong(dataId);
				}
			}else{
				out.put(DATA_ZIP_MODULE);
				out.putInt(smallData.length);
				out.put(smallData);
			}
		}else{
			out.put(DATA_NORMAL_MODULE);
			out.put(datas);
		}
	}
}
