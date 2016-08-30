package com.joymeng.slg.domain.object.build;

public enum BuildComponentType {
	BUILD_COMPONENT_ARMYTRAIN("armytrain"),//军队训练组件
	BUILD_COMPONENT_PRODUCTION("production"),//资源生产组件
	BUILD_COMPONENT_ELECTRICAL("electrical"),//电力组件
	BUILD_COMPONENT_RESEARCH("research"),//研究组件
	BUILD_COMPONENT_FORGING("forging"),//锻造组件
	BUILD_COMPONENT_HELP("help"),//帮助组件
	BUILD_COMPONENT_INTELLIGENCE("intelligence"),//情报组件
	BUILD_COMPONENT_DEFENSE("defense"),//防御组件
	BUILD_COMPONENT_CURE("cure"),//治疗组件
	BUILD_COMPONENT_STORAGE("storage"),//储存组件
	BUILD_COMPONENT_DEAL("deal"),//交易组件
	BUILD_COMPONENT_WAR("war"),//战争组件
	BUILD_COMPONENT_GEM("gem"),//生产宝石组件
	BUILD_COMPONENT_WALL("wall"),//城墙组件
	;
	
	String key;
	private BuildComponentType(String key){
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public static BuildComponentType search(String key){
		BuildComponentType[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			BuildComponentType component = datas[i];
			if (component.key.equals(key)){
				return component;
			}
		}
		return null;
	}
}
