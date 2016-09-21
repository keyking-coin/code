package com.joymeng.slg.union.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Alliance implements DataKey {
	String id;
	int level;
	String need;
	int expend;
	int num;
	List<String> members;
	//固定任务类型
	List<String> fixedtask;
	//随机数量
	int randomnumber;
	//随机权重
	List<String> randomtask;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getNeed() {
		return need;
	}

	public void setNeed(String need) {
		this.need = need;
	}

	public int getExpend() {
		return expend;
	}

	public void setExpend(int expend) {
		this.expend = expend;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public List<String> getMembers() {
		return members;
	}

	public void setMembers(List<String> members) {
		this.members = members;
	}

	@Override
	public Object key() {
		return id;
	}

}
