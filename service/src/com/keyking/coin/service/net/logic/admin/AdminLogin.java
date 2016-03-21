package com.keyking.coin.service.net.logic.admin;

import java.util.List;

import org.apache.mina.core.session.IoSession;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.data.SearchCondition;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.sys.MustLoginAgain;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.TimeUtils;

public class AdminLogin extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		String account = buffer.getUTF();
		String pwd     = buffer.getUTF();
		UserCharacter user = CTRL.login(account,pwd,resp);
		if (user != null && user.getPermission().isAdmin()){
			String saveKey = user.getSessionAddress();
			if (saveKey != null){
				IoSession save = NET.search(saveKey);
				if (save != null){
					save.write(new MustLoginAgain());
				}
			}
			NET.setAdminSession(session);
			user.setSessionAddress(session.getRemoteAddress().toString());
			SearchCondition condition = new SearchCondition();
			condition.setAgency(true);
			List<Deal> deals = CTRL.getSearchDeals(condition);
			resp.add(deals);
			ServerLog.info("admin<" + account + "> login at " + TimeUtils.nowChStr());
		}
		return resp;
	}

}
