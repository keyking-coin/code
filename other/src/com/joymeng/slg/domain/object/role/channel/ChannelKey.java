package com.joymeng.slg.domain.object.role.channel;

public enum ChannelKey {
	CHANNEL_ID_CODE("0000000"),//程序员渠道
	CHANNEL_ID_DANGLE("0000565"),//当乐
	CHANNEL_ID_VIVO("0001441"),//vivo
	CHANNEL_ID_OPPO("0001142"),//OPPO
	CHANNEL_ID_LENOVO("0000694"),//联想
	CHANNEL_ID_360("0000843"),//360
	CHANNEL_ID_HUAWEI("0000699"),//华为
	CHANNEL_ID_UC("0000066"),//UC
	CHANNEL_ID_WDJ("0001286"),//豌豆荚
	CHANNEL_ID_ANZHI("0000690"),//安智市场
	CHANNEL_ID_JINLI("0001381"),//金立
	CHANNEL_ID_MI("0000842"),//小米
	CHANNEL_ID_COOPI("0000695"),//酷派
	CHANNEL_ID_BAIDU("0000700"),//百度
	CHANNEL_ID_SG("0001395"),//搜狗
	CHANNEL_ID_BJLM("0002514"),//北京乐盟
	CHANNEL_ID_YOUKU("0001671"),//优酷
	CHANNEL_ID_TENCENT("0002514")//腾讯
	;
	
	private String key;
	
	private ChannelKey(String key){
		this.key= key;
	}

	public String getKey() {
		return key;
	}
}
