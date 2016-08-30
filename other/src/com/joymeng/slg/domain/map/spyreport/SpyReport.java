package com.joymeng.slg.domain.map.spyreport;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.Instances;
import com.joymeng.slg.domain.map.fight.result.FightVersus;

public class SpyReport implements Instances {
	byte spyType; // 被侦查的类型
	byte spyResult; // 0--侦查保护(失败) 1--未保护(成功)
	String time;// 发生时间
	int position;// 发生的坐标
	FightVersus aimRole = new FightVersus();
	List<String> content = new ArrayList<String>();

	public SpyReport() {
	}

	public byte getSpyType() {
		return spyType;
	}

	public void setSpyType(byte spyType) {
		this.spyType = spyType;
	}

	public byte getSpyResult() {
		return spyResult;
	}

	public void setSpyResult(byte spyResult) {
		this.spyResult = spyResult;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public FightVersus getAimRole() {
		return aimRole;
	}

	public void setAimRole(FightVersus aimRole) {
		this.aimRole = aimRole;
	}

	public List<String> getContent() {
		return content;
	}

	public void setContent(List<String> content) {
		this.content = content;
	}

}
