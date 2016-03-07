package com.keyking.coin.service.domain.user;

import com.keyking.coin.service.net.SerializeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.StringUtil;

public class Credit implements SerializeEntity{
	
	float curValue;//已使用额度
	
	float maxValue = 100000;//最大信用额度,每个人一开始就10万额度
	
	float tempMaxValue = 100000;//临时信用额度
	
	float totalDealValue;//总的成交金额
	
	int hp;//好评次数
	
	int zp;//中评次数
	
	int cp;//差评次数
	
	public float getCurValue() {
		return curValue;
	}

	public void setCurValue(float curValue) {
		this.curValue = curValue;
	}

	public float getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}

	public float getTempMaxValue() {
		return tempMaxValue;
	}

	public void setTempMaxValue(float tempMaxValue) {
		this.tempMaxValue = tempMaxValue;
	}

	public float getTotalDealValue() {
		return totalDealValue;
	}

	public void setTotalDealValue(float totalDealValue) {
		this.totalDealValue = totalDealValue;
	}

	public void addDealVale(float value) {
		if (totalDealValue + value> Float.MAX_VALUE){
			totalDealValue = Float.MAX_VALUE;
		}else{
			totalDealValue += value;
		}
	}
	
	public void addNum(int index) {
		if (index == 1){
			cp ++;
		}else if (index == 2){
			zp ++;
		}else if (index == 3){
			hp++;
		}
	}
	
	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getZp() {
		return zp;
	}

	public void setZp(int zp) {
		this.zp = zp;
	}

	public int getCp() {
		return cp;
	}

	public void setCp(int cp) {
		this.cp = cp;
	}

	public String serialize(){
		StringBuffer sb = new StringBuffer();
		sb.append(curValue).append(",")
		.append(maxValue).append(",")
		.append(tempMaxValue).append(",")
		.append(totalDealValue).append(",")
		.append(hp).append(",")
		.append(zp).append(",")
		.append(cp).append(",");
		return sb.toString();
	}
	
	public void deserialize(String str){
		if (StringUtil.isNull(str)){
			return;
		}
		String[] ss = str.split(",");
		curValue       = Float.parseFloat(ss[0]);
		maxValue       = Float.parseFloat(ss[1]);
		tempMaxValue   = Float.parseFloat(ss[2]);
		totalDealValue = Float.parseFloat(ss[3]);
		hp             = Integer.parseInt(ss[4]);
		zp             = Integer.parseInt(ss[5]);
		cp             = Integer.parseInt(ss[6]);
	}
	
	@Override
	public void serialize(DataBuffer buffer) {
		buffer.putUTF(String.valueOf(curValue));
		buffer.putUTF(String.valueOf(maxValue));
		buffer.putUTF(String.valueOf(tempMaxValue));
		buffer.putUTF(String.valueOf(totalDealValue));
		buffer.putInt(hp);
		buffer.putInt(zp);
		buffer.putInt(cp);
	}

	public void copy(Credit credit) {
		curValue       = credit.curValue;
		maxValue       = credit.maxValue;
		tempMaxValue   = credit.tempMaxValue;
		totalDealValue = credit.totalDealValue;
		hp             = credit.hp;
		zp             = credit.zp;
		cp             = credit.cp;
	}
}
