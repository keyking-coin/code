package com.joymeng.slg.domain.actvt.data;

import java.util.ArrayList;
import java.util.List;

public class ActvtTask 
{
	String id;
	boolean isShow;
	String desc;
	String type;
	int num;
	List<String> args = new ArrayList<String>();
	List<String> argsTmp = new ArrayList<String>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isShow() {
		return isShow;
	}
	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public List<String> getArgs() {
		return args;
	}
	public void setArgs(List<String> args) {
		this.args = args;
	}
	
	public boolean check(String type, List<String> argList)
	{
		if (!this.type.equals(type)) {
			return false;
		}
		if (this.args.size() != argList.size()) {
			return false;
		}
		for (int i = 0; i < args.size(); i++) {
			if (!args.get(i).equals(argList.get(i))) {
				return false;
			}
		}
		return true;
	}
	
	public boolean check(String type, Object... args)
	{
		argsTmp.clear();
		for (int i = 0; i < args.length; i++) {
			argsTmp.add(args[i].toString());
		}
		return check(type, argsTmp);
	}
}
