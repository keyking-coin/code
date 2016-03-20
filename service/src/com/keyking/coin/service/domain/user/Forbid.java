package com.keyking.coin.service.domain.user;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class Forbid {
	
	String reason;
	
	long endTime;//-1永久封号，0正常状态,>0表示封号截止时间。
	
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public void tick(){
		if (endTime > 0){
			long now = TimeUtils.nowLong() / 1000;
			if (now > endTime){//解除封禁
				endTime = 0;
				reason  = null;
			}
		}
	}
	
	public String serialize(){
		return endTime + "|" + reason;
	}
	
	public void deserialize(String str){
		if (StringUtil.isNull(str)){
			return;
		}
		String[] ss = str.split("\\|");
		endTime = Long.parseLong(ss[0]);
		if (endTime == 0){
			reason = null;
		}else{
			reason  = ss[1];
		}
	}
	
	public void serialize(DataBuffer buffer) {
		buffer.putUTF(reason==null?"":reason);
		if (endTime == -1){
			buffer.putUTF("forever");
		}else if (endTime == 0){
			buffer.putUTF("null");
		}else{
			String str = TimeUtils.chDate(endTime);
			buffer.putUTF(str);
		}
	}
}
