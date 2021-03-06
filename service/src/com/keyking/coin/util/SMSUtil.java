package com.keyking.coin.util;

import java.util.HashMap;
import java.util.Map;

import com.cloopen.rest.sdk.CCPRestSmsSDK;

public class SMSUtil {

	private static SMSUtil instance = new SMSUtil();

	private CCPRestSmsSDK sms = new CCPRestSmsSDK();
	
	private Map<String,Long> lastSends = new HashMap<String,Long>();
	
	public static SMSUtil getInstance() {
		return instance;
	}

	public void init() {
		//******************************注释*********************************************
		//*初始化服务器地址和端口                                                       *
		//*沙盒环境（用于应用开发调试）：restAPI.init("sandboxapp.cloopen.com", "8883");*
		//*生产环境（用户应用上线使用）：restAPI.init("app.cloopen.com", "8883");       *
		//*******************************************************************************
		sms.init("app.cloopen.com","8883");

		//******************************注释*********************************************
		//*初始化主帐号和主帐号令牌,对应官网开发者主账号下的ACCOUNT SID和AUTH TOKEN     *
		//*ACOUNT SID和AUTH TOKEN在登陆官网后，在“应用-管理控制台”中查看开发者主账号获取*
		//*参数顺序：第一个参数是ACOUNT SID，第二个参数是AUTH TOKEN。                   *
		//*******************************************************************************
		sms.setAccount("8a48b5514d32a2a8014d7587d4983060","574787ac769a4097812f32d5605690b8");
		//******************************注释*********************************************
		//*初始化应用ID                                                                 *
		//*测试开发可使用“测试Demo”的APP ID，正式上线需要使用自己创建的应用的App ID     *
		//*应用ID的获取：登陆官网，在“应用-应用列表”，点击应用名称，看应用详情获取APP ID*
		//*******************************************************************************
		sms.setAppId("8a48b5515388ec1501538d18a0bb04d3");
	}
	
	//******************************注释****************************************************************
	//*调用发送模板短信的接口发送短信                                                                  *
	//*参数顺序说明：                                                                                  *
	//*第一个参数:是要发送的手机号码，可以用逗号分隔，一次最多支持100个手机号                          *
	//*第二个参数:是模板ID，在平台上创建的短信模板的ID值；测试的时候可以使用系统的默认模板，id为1。    *
	//*系统默认模板的内容为“【云通讯】您使用的是云通讯短信模板，您的验证码是{1}，请于{2}分钟内正确输入”*
	//*第三个参数是要替换的内容数组。																														       *
	//**************************************************************************************************
	
	//**************************************举例说明***********************************************************************
	//*假设您用测试Demo的APP ID，则需使用默认模板ID 1，发送手机号是13800000000，传入参数为6532和5，则调用方式为           *
	//*result = restAPI.sendTemplateSMS("13800000000","1" ,new String[]{"6532","5"});																		  *
	//*则13800000000手机号收到的短信内容是：【云通讯】您使用的是云通讯短信模板，您的验证码是6532，请于5分钟内正确输入     *
	//*********************************************************************************************************************
	public boolean sendToken(String num,String code){
		Map<?,?> datas = sms.sendTemplateSMS(num,"74501",new String[]{code,"5"});
		if ("000000".equals(datas.get("statusCode"))) {
			lastSends.put(num,TimeUtils.nowLong());
			return true;
		}
		return false;
	}
	
	public boolean sendPassword(String num,String code){
		Map<?,?> datas = sms.sendTemplateSMS(num,"74839",new String[]{code});
		if ("000000".equals(datas.get("statusCode"))) {
			lastSends.put(num,TimeUtils.nowLong());
			return true;
		}
		return false;
	}
	
	public boolean couldSend(String num){
		if (lastSends.containsKey(num)){
			long time = lastSends.get(num).longValue();
			if (time + 2 * 60 * 1000 > TimeUtils.nowLong()){
				return false;
			}
		}
		return true;
	}
}
 
 
