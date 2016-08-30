package com.joymeng.list;

public enum BuildOperation {
	creatBuild((byte) 0, "建造建筑"), 
	uplevelBuild((byte) 1, "升级建筑"), 
	removeBuild((byte) 2, "拆除建筑"), 
	moveBuild((byte) 3, "移动建筑"), 
	cancleCreBuild((byte) 4, "取消建造建筑"), 
	cancleUpBuild((byte) 5, "取消升级建筑"), 
	cancleReBuild((byte) 6, "取消拆除建筑"), 
	createFinish((byte)7,"建造建筑完成"),
	upLevelFinish((byte)8,"升级建筑完成"),
	removeFinish((byte)9,"拆除建筑完成"),
	;
	
	byte key;
	String name;

	BuildOperation(byte key, String name) {
		this.key = key;
		this.name = name;
	}

	public byte getKey() {
		return key;
	}

	public void setKey(byte key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
