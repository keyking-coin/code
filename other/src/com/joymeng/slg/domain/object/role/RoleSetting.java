package com.joymeng.slg.domain.object.role;

import java.util.ArrayList;
import java.util.List;

public class RoleSetting {
	boolean soundeffect = true; // true 开 false关
	boolean music = true;// true 开 false关
	String language = "CN";
	List<String> megNotice = new ArrayList<>();

	public RoleSetting() {
	}

	public RoleSetting(boolean soundeffect, boolean music, String language, List<String> megNotice) {
		this.soundeffect = soundeffect;
		this.music = music;
		this.language = language;
		this.megNotice = megNotice;
	}

	public boolean isSoundeffect() {
		return soundeffect;
	}

	public void setSoundeffect(boolean soundeffect) {
		this.soundeffect = soundeffect;
	}

	public boolean isMusic() {
		return music;
	}

	public void setMusic(boolean music) {
		this.music = music;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<String> getMegNotice() {
		return megNotice;
	}

	public void setMegNotice(List<String> megNotice) {
		this.megNotice = megNotice;
	}

	public void updateRoleSetting(RoleSetting roleSetting) {
		this.soundeffect = roleSetting.isSoundeffect();
		this.music = roleSetting.isMusic();
		this.language = roleSetting.getLanguage();
		this.megNotice = roleSetting.getMegNotice();
	}
}
