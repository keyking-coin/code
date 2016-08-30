package com.joymeng.slg.union.data;

public enum UnionPostType {
	UNION_POST_LEVEL_UP("升级联盟权限"),
	UNION_POST_NOTICE_CHANGE1("修改联盟公告权限"),
	UNION_POST_NOTICE_CHANGE2("修改联盟宣言权限"),
	UNION_POST_RECRUIT_CHANGE("修改公开招募条件权限"),
	UNION_POST_NAME_CHANGE("修改联盟名称权限"),
	UNION_POST_FLAG_CAHNGE("更换联盟旗帜权限"),
	UNION_POST_DISSOLVE("解散联盟权限"),
	UNION_POST_EXIT("退出联盟权限"),
	UNION_POST_INVITE("邀请加入联盟权限"),
	UNION_POST_KICK("踢出联盟权限"),
	UNION_POST_OFFICER_APPOINT("官员任命权限"),
	UNION_POST_ASSISTANT_APPOINT("副盟主任命权限"),
	UNION_POST_DEMISE("禅让盟主权限"),
	UNION_POST_VERIFICATION("入盟申请通过权限"),
	UNION_POST_SEND_EMAIL_ALL("邮件群发权限"),
	UNION_POST_TECHNOLOGY_LEVEL_UP("升级联盟科技权限"),
	UNION_POST_CONVERT_GOODS("联盟积分兑换道具权限"),
	UNION_POST_OCCUPY_CITY("占领城市权限"),
	UNION_POST_DROP_CITY("放弃城市权限"),
	UNION_POST_BUILD_CREATE("建造联盟建筑权限"),
	UNION_POST_BUILD_LEVEL("联盟建筑升级权限"),
	UNION_POST_BUILD_DESTROY("拆除联盟建筑权限"),
	UNION_POST_SPECIAL_BUILD_OPRATION("联盟特殊功能建筑操作权限"),
	UNION_POST_OFFICER_NAME_CHANGE("编辑联盟官员名称权限"),
	UNION_POST_SHORT_NAME_CHANGE("编辑联盟简称权限"),
	UNION_POST_LAUNCH_NB("发射核弹权限")
	;
	
	String desc;
	private UnionPostType(String desc){
		this.desc = desc;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
