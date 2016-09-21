package com.joymeng.slg.domain.object.resource;

/**
 * 
 * @explain: 掠夺资源比例
 * @package: com.joymeng.slg.domain.object.resource
 * @ClassName: ResourcePredatoryRatio 
 * @author xufangliang
 * @date 2016年9月12日 上午9:50:25
 * @Description:  更新为攻击基地胜利后资源掠夺按照比例（16:16:4:1）进行，某类资源不够时按照食品、金属、石油和合金的顺序优先级掠夺；
 *					可掠夺的资源>=负重时：每项资源的掠夺量=负重量*比例系数/37；单项资源不够时按照资源优先级顺序进行弥补；
 *					可掠夺的资源<负重时：全部掠夺
 *
 */
public enum ResourcePredatoryRatio {
	RESOURCE_TYPE_FOOD("food",16),//粮食
	RESOURCE_TYPE_METAL("metal",16),//金属
	RESOURCE_TYPE_OIL("oil",4),//石油
	RESOURCE_TYPE_ALLOY("alloy",1),//钛合金
	;
	
	String key;
	int value;
	private ResourcePredatoryRatio(String key,int value){
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	//总比例
	public static int totalProportion(){
		int total = 0;
		ResourcePredatoryRatio[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			ResourcePredatoryRatio type = datas[i];
			total += type.getValue();
		}
		return total;
	}
	//得到某一类比例
	public static int searchProportion(String key){
		ResourcePredatoryRatio[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			ResourcePredatoryRatio type = datas[i];
			if (type.key.equals(key)){
				return type.getValue();
			}
		}
		return 0;
	}
	
	public static ResourcePredatoryRatio search(String key){
		ResourcePredatoryRatio[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			ResourcePredatoryRatio type = datas[i];
			if (type.key.equals(key)){
				return type;
			}
		}
		return null;
	}
	
	public static ResourcePredatoryRatio search(int code){
		ResourcePredatoryRatio[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			ResourcePredatoryRatio type = datas[i];
			if (type.ordinal() == code){
				return type;
			}
		}
		return null;
	}
}
