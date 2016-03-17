package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.service.net.resp.module.ModuleResp;
import com.keyking.coin.util.StringUtil;

public class DealEdit extends AbstractLogic {
	
	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long id   = buffer.getLong();
		byte type = buffer.get();//交割类型
		String bourse = buffer.getUTF();
		String name = buffer.getUTF();
		String priceStr = buffer.getUTF();
		float price = 0;
		if (!StringUtil.isNull(priceStr)){
			price = Float.parseFloat(priceStr);
		}
		int num = buffer.getInt();
		String validTime = buffer.getUTF();
		String other = buffer.getUTF();
		Deal deal = CTRL.tryToSearch(id);
		if (deal != null){
			deal.setType(type);
			deal.setBourse(bourse);
			deal.setName(name);
			deal.setPrice(price);
			deal.setNum(num);
			deal.setValidTime(validTime);
			deal.setOther(other);
			deal.setNeedSave(true);
			resp.add(Module.UPDATE_FLAG);
			resp.add(deal);
			Module module = new Module();
			module.setCode(Module.MODULE_CODE_DEAL);
			module.setFlag(Module.UPDATE_FLAG);
			module.add(deal);
			ModuleResp modules = new ModuleResp();
			modules.addModule(module);
			NET.sendMessageToAllClent(modules,null);
			resp.setSucces();
		}else{
			resp.setError("交易帖子不存在");
		}
		return resp;
	}
}
