package com.joymeng.slg.union.impl;

public class UnionRecords {
	// 个人在联盟商店购买记录
	public static final byte UNION_SHOP_RECORD_TYPE_ROLE = 1;
	// 联盟兑换商品记录
	public static final byte UNION_SHOP_RECORD_TYPE_UNION = 2;
	// 联盟战斗记录
	public static final byte UNION_BATTLE_RECORD_TYPE_UNION = 3;
	// 个人向联盟商店出售物品
	public static final byte UNION_ROLE_REMOVE_ITEM = 4;
	// 联盟常规记录
	public static final byte UNION_GENERAL_RECORD = 5;
	
	public static final byte UNION_GENERAL_RECORD_COLOR_RED = 1;
	public static final byte UNION_GENERAL_RECORD_COLOR_GREEN = 2;
	public static final byte UNION_GENERAL_RECORD_COLOR_BLUE = 3;
	
	/*
	 * .........................................................................
	 * ..
	 */

	// 个人在联盟商店购买记录
	public static final String CONTENT_TYPE_ALLIAN_SHOP = "alliance_shop_txt";
	// 联盟兑换商品记录
	public static final String CONTENT_TYPE_ALLIAN_SHOP_UNION = "alliance_shop_txt1";
	// 联盟战斗记录
	public static final String CONTENT_TYPE_ALLIAN_BATTLE_UNION = "alliance_battle_txt";
	// 联盟战斗记录--侦查
	public static final String CONTENT_TYPE_ALLIAN_BATTLE_UNION_SPY = "inv_info_AllianceAffiliationBuilding";
	// 个人向联盟商店出售物品
	public static final String CONTENT_TYPE_ALLIAN_REMOVE_ITEM = "alliance_remove_item_txt";
	// {0}加入了联盟
	public static final String CONTENT_TYPE_ALLIAN_ADD_MEMBER = "alliance_add_member";
	// {0}创建了联盟
	public static final String CONTENT_TYPE_MEMBER_CREATE_UNION = "member_create_union";
	// {0}退出联盟
	public static final String CONTENT_TYPE_ALLIAN_EXIT_MEMBER = "alliance_exit_member";
	// {0}被{1}踢出联盟
	public static final String CONTENT_TYPE_ALLIAN_FIRE_MEMBER = "alliance_fire_member";
	// {0}的军衔被{1}提升为{2}
	public static final String CONTENT_TYPE_ALLIAN_RAISE_RANK = "alliance_raise_rank";
	// {0}的军衔被{1}提升为{2} 联盟日志
	public static final String CONTENT_TYPE_ALLIAN_RAISE_RANK1 = "alliance_raise_rank1";
	// {0}将盟主禅让给了{1}
	public static final String CONTENT_TYPE_ALLIAN_ABDICATION = "alliance_abdication";
	// 联盟名称修改为{0}
	public static final String CONTENT_TYPE_ALLIAN_CHANGE_NAME = "alliance_change_name";
	// 联盟简称修改为{0}
	public static final String CONTENT_TYPE_ALLIAN_CHANGE_SHORTNAME = "alliance_change_shortname";
	// 联盟宣言修改为{0}
	public static final String CONTENT_TYPE_ALLIAN_CHANGE_NOTICE = "alliance_change_notice";
	// 联盟公告修改为{0}
	public static final String CONTENT_TYPE_ALLIAN_CHANGE_IN_NOTICE = "alliance_change_in_notice";
	//联盟占领{0}级城市{1}座  TODO
	public static final String CONTENT_TYPE_ALLIAN_OCP_CITY = "alliance_ocp_city";
	//联盟总杀兵数{0} TODO
	public static final String CONTENT_TYPE_ALLIAN_KILL_SOLD = "alliance_kill_sold";
	
	byte recordType;// 1-个人 2-联盟
	byte colorType;//红蓝绿
	String unionRecordContent;
	String unionRecordPara;
	long unionRecordTime;

	public UnionRecords() {
	}
	
	public UnionRecords(String unionRecordContent,String unionRecordPara) {
		this.unionRecordContent = unionRecordContent;
		this.unionRecordPara = unionRecordPara;
	}
	
	public UnionRecords(byte recordType, String unionRecordContent, String unionRecordPara, long unionRecordTime) {
		this.recordType = recordType;
		this.colorType = 0;
		this.unionRecordContent = unionRecordContent;
		this.unionRecordPara = unionRecordPara;
		this.unionRecordTime = unionRecordTime;
	}
	
	public UnionRecords(byte recordType,byte colorType, String unionRecordContent, String unionRecordPara, long unionRecordTime) {
		this.recordType = recordType;
		this.colorType = colorType;
		this.unionRecordContent = unionRecordContent;
		this.unionRecordPara = unionRecordPara;
		this.unionRecordTime = unionRecordTime;
	}

	public byte getRecordType() {
		return recordType;
	}

	public void setRecordType(byte recordType) {
		this.recordType = recordType;
	}

	public String getUnionRecordContent() {
		return unionRecordContent;
	}

	public String getUnionRecordPara() {
		return unionRecordPara;
	}

	public void setUnionRecordPara(String unionRecordPara) {
		this.unionRecordPara = unionRecordPara;
	}

	public byte getColorType() {
		return colorType;
	}

	public void setColorType(byte colorType) {
		this.colorType = colorType;
	}

	public void setUnionRecordContent(String unionRecordContent) {
		this.unionRecordContent = unionRecordContent;
	}

	public long getUnionRecordTime() {
		return unionRecordTime;
	}

	public void setUnionRecordTime(long unionRecordTime) {
		this.unionRecordTime = unionRecordTime;
	}

}
