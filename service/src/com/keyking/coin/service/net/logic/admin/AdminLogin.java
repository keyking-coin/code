package com.keyking.coin.service.net.logic.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.joda.time.DateTime;

import com.keyking.coin.service.domain.condition.SearchCondition;
import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AdminResp;
import com.keyking.coin.service.net.resp.sys.MustLoginAgain;
import com.keyking.coin.service.tranform.TransformDealData;
import com.keyking.coin.service.tranform.TransformUserData;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.TimeUtils;

public class AdminLogin extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AdminResp resp = new AdminResp(logicName);
		String account = buffer.getUTF();
		String pwd     = buffer.getUTF();
		UserCharacter user = CTRL.adminLogin(account,pwd,resp);
		if (user != null){
			if (user.getPermission().admin()){
				String saveKey = user.getSessionAddress();
				if (saveKey != null && !saveKey.equals(session.getRemoteAddress().toString())){
					IoSession save = NET.search(saveKey);
					if (save != null){
						save.write(new MustLoginAgain());
					}
				}
				NET.setAdminSession(session);
				session.setAttribute("isAdmin","true");
				user.setSessionAddress(session.getRemoteAddress().toString());
				SearchCondition condition = new SearchCondition();
				condition.setAgency(true);
				List<TransformDealData> deals = getAgencys();
				resp.addKey("deals");
				resp.add(deals);
				List<TransformUserData> sellers = CTRL.getSearchRZ();
				resp.addKey("sellers");
				resp.add(sellers);
				ServerLog.info("admin<" + account + "> login at " + TimeUtils.nowChStr());
			}else{
				resp.setError(account + "不是管理员账号");
			}
		}
		return resp;
	}

	
	private List<TransformDealData> getAgencys(){
		List<TransformDealData> result = new ArrayList<TransformDealData>();
		List<Deal> deals = CTRL.getDeals();
		for (Deal deal : deals){
			for (DealOrder order : deal.getOrders()){
				if (order.getHelpFlag() == 0 || order.checkRevoke()){
					continue;
				}
				if (order.getState() == 1 || order.getState() == 4){
					TransformDealData tdd = new TransformDealData();
					tdd.copy(deal,order);
					result.add(tdd);
				}
			}
		}
		Collections.sort(result,new Comparator<TransformDealData>(){
			@Override
			public int compare(TransformDealData o1, TransformDealData o2) {
				String str1 = o1.getOrders().get(0).getTimes().get(0);
				String str2 = o2.getOrders().get(0).getTimes().get(0);
				DateTime time1 = TimeUtils.getTime(str1);
				DateTime time2 = TimeUtils.getTime(str2);
				if (time1.isBefore(time2)){
					return 1;
				}else{
					return -1;
				}
			}
		});
		return result;
	}
}
