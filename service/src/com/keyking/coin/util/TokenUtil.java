package com.keyking.coin.util;

import java.util.HashMap;
import java.util.Map;

public class TokenUtil {
	
	private static TokenUtil instance = new TokenUtil();
	
	Map<String,String> codes = new HashMap<String, String>();
	
	public static TokenUtil getInstance(){
		return instance;
	}
	
	/**
	 * 生成验证�?
	 * @param key
	 * @return
	 */
	public String create(String key){
		StringBuffer sb = new StringBuffer();
		for (int i = 0 ; i < 5 ; i ++){
			int num = MathUtils.random(10);
			sb.append(num);
		}
		long now = TimeUtils.nowLong();
		codes.put(key,now + "," + sb.toString());
		return sb.toString();
	}
	
	/**
	 * 验证验证�?
	 * @param key
	 * @param value
	 * @return
	 */
	public int check(String key,String value){
		String code = codes.get(key);
		if (code != null){
			String[] ss = code.split(",");
			long time = Long.parseLong(ss[0]);
			if (time + 5 * 60 * 1000 < TimeUtils.nowLong()){
				return 2;
			}
			if (!StringUtil.isNull(value) && value.equals(ss[1])){
				return 0;
			}
		}
		return 1;
	}
	
	public String check(String key){
		String code = codes.get(key);
		if (code != null){
			String[] ss = code.split(",");
			long time = Long.parseLong(ss[0]);
			if (time + 5 * 60 * 1000 < TimeUtils.nowLong()){
				return null;
			}
			return ss[1];
		}
		return null;
	}
	
	public void remove(String key){
		codes.remove(key);
	}
}
 
