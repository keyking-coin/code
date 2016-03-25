package com.keyking.coin.service.domain.deal;

import com.keyking.coin.service.net.SerializeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;



public class DealAppraise implements SerializeEntity{
	
	boolean isCompleted = false;//是否完成评价
	
	byte star;//星级评价 3好评;2中评;1差评
	
	String detail = "null";//详细描述
	
	String time = "null";//时间
	
	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public byte getStar() {
		return star;
	}

	public void setStar(byte star) {
		this.star = star;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String serialize(){
		return (isCompleted ? "1" : "0") + "," + star + "," + detail + "," + time;
	}
	
	public void deserialize(String str) {
		if (!StringUtil.isNull(str)){
			String[] ss = str.split(",");
			isCompleted = ss[0].equals("1");
			star       = Byte.parseByte(ss[1]);
			detail      = ss[2];
			time        = ss[3];
		}
	}
	
	public void serialize(DataBuffer buffer) {
		buffer.put(isCompleted ? (byte)1 : 0);
		buffer.put(star);
		buffer.putUTF(detail);
		buffer.putUTF(time);
	}
	
	public void appraise(byte star,String detail){
		isCompleted = true;
		this.star = star;
		if (!StringUtil.isNull(detail)){
			this.detail = detail;
		}
		time = TimeUtils.nowChStr();
	}
}
