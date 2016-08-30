package com.joymeng.slg.domain.object.task;


public enum MissionType {
	MS_ALL(0,"all"),
	MS_MAIN(1,"Main"),
	MS_BASE(2,"Base"),
	MS_ALLIANCE(3,"Alliance"),
	MS_KINDOM(4,"Country"),
	MS_GLORY(5,"Glory"),
	MS_PRIZE(99,"prize"),;
	int key;
	String name;
	private MissionType(int key, String name){
		this.key = key;
		this.name = name;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static MissionType valueof(int key){
		MissionType[] datas = values();
		for (int i = 0 ; i < datas.length; i++){
			MissionType mType = datas[i];
			if(mType.key == key){
				return mType;
			}
		}
		return null;
	}
	
	public static MissionType search(String key){
		MissionType[] datas = values();
		for (int i = 0 ; i < datas.length; i++){
			MissionType mType = datas[i];
			if (mType.name.equals(key)){
				return mType;
			}
		}
		return null;
	}
}
