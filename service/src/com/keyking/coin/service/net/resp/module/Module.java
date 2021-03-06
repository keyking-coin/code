package com.keyking.coin.service.net.resp.module;

import java.util.HashMap;
import java.util.Map;

import com.keyking.coin.service.net.ParametersEntity;
import com.keyking.coin.service.net.SerializeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;

public class Module implements SerializeEntity{
	byte code;//推送逻辑编号
	byte flag;//0 添加 ,1 删除，2修改
	public static final byte ADD_FLAG                 = 0;
	public static final byte DEL_FLAG                 = 1;
	public static final byte UPDATE_FLAG              = 2;
	public static final byte MODULE_CODE_USER         = 0;//用户数据部分
	public static final byte MODULE_CODE_DEAL         = 1;//交易帖子有变化
	public static final byte MODULE_CODE_REVERT       = 2;//交易帖子回复
	public static final byte MODULE_CODE_SIMPLE_DEAL  = 3;//推送简单
	public static final byte MODULE_CODE_CREDIT       = 4;//信用模块
	public static final byte MODULE_CODE_BANK_ACCOUNT = 5;//银行卡模块
	public static final byte MODULE_CODE_EMAIL        = 6;//邮件数量
	public static final byte MODULE_CODE_SIMPLE_ORDER = 7;//最进成交模块
	public static final byte MODULE_CODE_FRIEND       = 8;//好友数量
	public static final byte MODULE_CODE_MESSAGE      = 9;//聊天信息
	public static final byte MODULE_CODE_ORDER        = 10;//成交订单
	public static final byte MODULE_CODE_ADMIN_AGENCY = 11;//管理员的中介管理
	public static final byte MODULE_CODE_ADMIN_SELLER = 12;//管理员的卖家认证
	
	Map<String,Object> datas = new HashMap<String,Object>();

	ParametersEntity params = new ParametersEntity();
	
	public byte getCode() {
		return code;
	}

	public void setCode(byte code) {
		this.code = code;
	}
	
	public byte getFlag() {
		return flag;
	}

	public void setFlag(byte flag) {
		this.flag = flag;
	}
	
	public Map<String,Object> getDatas() {
		return datas;
	}

	public void setDatas(Map<String, Object> datas) {
		this.datas = datas;
	}

	public void add(String key,Object obj){
		datas.put(key,obj);
		params.put(obj);
	}
	
	@Override
	public void serialize(DataBuffer buffer){
		try {
			buffer.put(code);
			buffer.put(flag);
			params.serialize(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
 
