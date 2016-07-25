package com.keyking.coin.service.net.logic.app;

import java.io.FileInputStream;
import java.util.Map;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.util.JsonUtil;

public class AppLoadAd extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		try {
			FileInputStream fis = new FileInputStream("./ad.data");
			DataBuffer db = DataBuffer.allocate(1024);
			byte[] temp = new byte[1024];
			do {
				int len = fis.read(temp);
				if (len == -1){
					break;
				}
				db.put(temp,0,len);
			}while(true);
			fis.close();
			String str = new String(db.arrayToPosition(),"UTF-8");
			Map<String,String> map = JsonUtil.JsonToObjectMap(str,String.class,String.class);
			for (String key : map.keySet()){
				resp.put(key,map.get(key));
			}
			resp.setSucces();
		} catch (Exception e) {
			e.printStackTrace();
			resp.setError("后台没有配置");
		}
		return resp;
	}

}
