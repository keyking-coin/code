package com.keyking.coin.service.domain.user;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.net.SerializeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.JsonUtil;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class BankAccount implements SerializeEntity{

	List<Account> accounts = new ArrayList<Account>();
	
	public String serialize(){
		return JsonUtil.ObjectToJsonString(accounts);
	}
	
	public void deserialize(String str){
		if (StringUtil.isNull(str)){
			return;
		}
		accounts = JsonUtil.JsonToObjectList(str,Account.class);
	}
	
	@Override
	public void serialize(DataBuffer buffer) {
		buffer.putInt(accounts.size());
		for (Account account : accounts){
			account._serialize(buffer);
		}
	}
	
	public void add(String name,String num,String address,String peopleName){
		Account account = new Account();
		account.setAddTime(TimeUtils.nowChStr());
		account.setName(name);
		account.setAccount(num);
		account.setOpenAddress(address);
		account.setOpenName(peopleName);
		accounts.add(account);
	}
}
