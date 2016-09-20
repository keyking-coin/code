package com.keyking.coin.service.net.logic.app;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.api.common.DeviceType;

import com.keyking.coin.service.Service;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.data.LoginData;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.push.PushType;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.StringUtil;

public class AppLogin extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		String account  = buffer.getUTF();//登录账号
		String pwd      = buffer.getUTF();//登录密码
		String pushId   = buffer.getUTF();//单独推送编号
		String platform = buffer.getUTF();//平台编号android或者ios
		String version  = null;
		try {
			version     = buffer.getUTF();
		} catch (Exception e) {
			//e.printStackTrace();
		}
		if (version == null && platform.equals(DeviceType.Android.value())){//旧版本
			resp.setError("您的版本过低,请到官网下载最新应用。");
			return resp;
		}
		if (version == null || !version.equals(Service.VERSION)){//正常的版本检查
			resp.put("version","版本过低,需要更新是否继续？");
		}else{
			resp.put("version","ok");
		}
		resp.put("androidUrl",Service.APK_URL);
		if (StringUtil.isNull(pushId)){
			resp.setError("推送注册Id为空");
			return resp;
		}
		UserCharacter user = CTRL.search(account);
		if (user == null){//不存在账号是account
			resp.setError("账号:" + account + "不存在");
		}else{
			if (user.getPwd().equals(pwd)){
				String preId = user.getPushId();
				if (preId != null && !preId.equals(pushId)){
					Map<String, String> temp = new HashMap<String, String>();
					temp.put("type",PushType.PUSH_TYPE_KICK.toString());
					temp.put("tip","您的账号在别处登录,如果不是你本人操作请立即修改你的密码。");
					String prePlatform = user.getPlatform();
					if (prePlatform != null){
						PUSH.push("异端登录","您的账号在别处登录",prePlatform,temp,preId);
					}
				}
				resp.put("user",new LoginData(user));
				resp.put("deals",CTRL.getRecentOrders());
				user.setPlatform(platform);
				user.setPushId(pushId);
				resp.setSucces();
				user.setSessionAddress(session.getRemoteAddress().toString());
				ServerLog.info(account + " login from " + session.getRemoteAddress());
			}else{//密码错误
				resp.setError("密码错误");
			}
		}
		return resp;
	}

}
