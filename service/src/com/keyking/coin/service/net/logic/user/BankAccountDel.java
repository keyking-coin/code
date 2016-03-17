package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.user.BankAccount;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.service.net.resp.module.ModuleResp;

public class BankAccountDel extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long uid = buffer.getLong();
		String account = buffer.getUTF();
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			BankAccount bank = user.getBankAccount();
			if (bank.remove(account)){
				ModuleResp modules = new ModuleResp();
				Module module = new Module();
				module.setCode(Module.MODULE_CODE_BANK_ACCOUNT);
				module.add(bank);
				modules.addModule(module);
				NET.sendMessageToClent(modules,user);
				resp.setSucces();
			}else{
				resp.setError("未找到要删除的银行卡记录");
			}
		}else{
			resp.setError("系统错误");
		}
		return resp;
	}

}
