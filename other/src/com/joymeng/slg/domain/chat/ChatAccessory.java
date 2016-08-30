package com.joymeng.slg.domain.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.joymeng.log.GameLog;
import com.joymeng.slg.net.ParametersEntity;


public class ChatAccessory {
	private Map<String, Long> itemMap = new HashMap<String, Long>();
	private byte state;//0-未收取，1-已收取
	
	public ChatAccessory(){
		
	}
	public void addItems(Object... params){
		for (int i =0 ; i < params.length ; i += 2){
			String itemId = (String)params[i];
			long value = ((Number)params[i + 1]).longValue();
			if (value <= 0){
				GameLog.error(" add item num must > 0");
				continue;
			}
			itemMap.put(itemId, value);
		}
	}
	
	public void sendToClient(ParametersEntity params){
		params.put(state);
		params.put(itemMap.values().size());
		for(Map.Entry<String, Long> cell : itemMap.entrySet()){
			params.put(cell.getKey());//String key
			params.put(cell.getValue());//long value
		}
	}
	
	public void deserialize(String data){
		if (data == null || data.length() == 0) {
			return;
		}
		String[] strParams = data.split(";");
		if(strParams.length <= 0){
			return;
		}
		state = Byte.parseByte(strParams[0]);
		for (int i = 0 ; i < strParams.length ; i++){
			String value = strParams[i];
			if (value == null || value.length() == 0) {
				continue;
			}
			String[] vals = value.split(",");
			String itemId = vals[0];
			long num = Long.parseLong(vals[1]);
			itemMap.put(itemId, num);
		}
		
	}
	
	public String serialize(){
		StringBuffer sb = new StringBuffer();
		sb.append(state).append(";");
		for (Entry<String, Long> entry : itemMap.entrySet()) {
			sb.append(entry.getKey()).append(",").append(entry.getValue()).append(";");
		}
		if (sb.length() > 0) { //删除最后的";"
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}
}
