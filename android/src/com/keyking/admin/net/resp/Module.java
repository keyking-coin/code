package com.keyking.admin.net.resp;

public class Module {
	public static final byte ADD_FLAG                 = 0;
	public static final byte DEL_FLAG                 = 1;
	public static final byte UPDATE_FLAG              = 2;
	public static final byte MODULE_CODE_USER         = 0;//用户数据部分
	public static final byte MODULE_CODE_DEAL         = 1;//交易帖子
	public static final byte MODULE_CODE_REVERT       = 2;//交易帖子回复
	public static final byte MODULE_CODE_SIMPLE_DEAL  = 3;//推送简单
	public static final byte MODULE_CODE_CREDIT       = 4;//信用模块
	public static final byte MODULE_CODE_BANK_ACCOUNT = 5;//银行卡模块
	public static final byte MODULE_CODE_EMAIL        = 6;//邮件模块
	public static final byte MODULE_CODE_SIMPLE_ORDER = 7;//最进成交模块
	public static final byte MODULE_CODE_FRIEND       = 8;//好友
	public static final byte MODULE_CODE_MESSAGE      = 9;//聊天信息
	public static final byte MODULE_CODE_ORDER        = 10;//成交订单
	public static final byte MODULE_CODE_ADMIN_AGENCY = 11;//管理员的中介管理
	public static final byte MODULE_CODE_ADMIN_SELLER = 12;//管理员的卖家认证
	
}
