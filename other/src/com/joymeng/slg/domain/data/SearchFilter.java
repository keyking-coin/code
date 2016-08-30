package com.joymeng.slg.domain.data;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public interface SearchFilter<T extends DataKey> {
	public boolean filter(T data);
}
