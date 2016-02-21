package com.keyking.coin.service.domain.data;

import com.keyking.coin.service.net.SerializeEntity;
import com.keyking.coin.util.Instances;


public abstract class EntitySaver implements Instances , SerializeEntity{
	
	protected boolean needSave = true;
	
	public boolean isNeedSave() {
		return needSave;
	}

	public void setNeedSave(boolean needSave) {
		this.needSave = needSave;
	}
}
