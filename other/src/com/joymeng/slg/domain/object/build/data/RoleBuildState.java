package com.joymeng.slg.domain.object.build.data;

public enum RoleBuildState {
	
	COND_NORMAL((byte) 0),          // 正常状态
	COND_UPGRADE((byte) 1),        // 建筑升级中
	COND_UPGRADEFREE((byte) 2),   // 建筑免费升级中
	COND_DISMANTLE((byte) 5),    // 拆除状态
	COND_WORKING((byte) 6),     // 医院、维修厂工作中
	COND_TRADE((byte) 7),      // 贸易中心资源交换中
	COND_DELETED((byte) 100), // 已删除建筑
	COND_STAYBY((byte) 101)  // 等待删除状态
	; 
	
	byte key;
	
	private RoleBuildState(byte key){
		this.key = key;
	}

	public byte getKey() {
		return key;
	}

	public void setKey(byte key) {
		this.key = key;
	}

	public static RoleBuildState valueof(byte key){
		RoleBuildState[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			RoleBuildState roleState = datas[i];
			if(roleState.key == key){
				return roleState;
			}
		}
		return null;
	}

}
