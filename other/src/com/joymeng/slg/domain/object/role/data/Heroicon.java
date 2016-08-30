package com.joymeng.slg.domain.object.role.data;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Heroicon implements DataKey {

	String id;
	String heroIcon;
	String heropic;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHeroIcon() {
		return heroIcon;
	}

	public void setHeroIcon(String heroIcon) {
		this.heroIcon = heroIcon;
	}

	public String getHeropic() {
		return heropic;
	}

	public void setHeropic(String heropic) {
		this.heropic = heropic;
	}

	@Override
	public Object key() {
		return id;
	}

}
