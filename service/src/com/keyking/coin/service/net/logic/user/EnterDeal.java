package com.keyking.coin.service.net.logic.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.DateTime;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.data.SearchCondition;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.util.JsonUtil;
import com.keyking.coin.util.TimeUtils;

public class EnterDeal extends AbstractLogic{

	@Override
	public Object doLogic(DataBuffer buffer,String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		resp.setSucces();
		String searchStr = buffer.getUTF();
		List<Deal> deals = null;
		if (searchStr.equals("null")){//普通查询7天内的所有的帖子
			deals = CTRL.getWeekDeals();
		}else{//条件查询
			SearchCondition condition = JsonUtil.JsonToObject(searchStr,SearchCondition.class);
			deals = CTRL.getSearchDeals(condition);
		}
		if (deals.size() > 0){
			List<Deal> issues = new ArrayList<Deal>();
			List<Deal> valides = new ArrayList<Deal>();
			List<Deal> normal = new ArrayList<Deal>();
			for (Deal deal : deals){
				if (deal.isIssueRecently()){
					issues.add(deal);
				}else if (deal.checkValidTime()){
					valides.add(deal);
				}else{
					normal.add(deal);
				}
			}
			Collections.sort(issues,new Comparator<Deal>(){
				@Override
				public int compare(Deal o1, Deal o2) {
					DateTime time1 = TimeUtils.getTime(o1.getLastIssue());
					DateTime time2 = TimeUtils.getTime(o2.getLastIssue());
					if (time1.isBefore(time2)){
						return 1;
					}else{
						return -1;
					}
				}
			});
			Collections.sort(valides,new Comparator<Deal>(){
				@Override
				public int compare(Deal o1, Deal o2) {
					DateTime time1 = TimeUtils.getTime(o1.getValidTime());
					DateTime time2 = TimeUtils.getTime(o2.getValidTime());
					if (time1.isBefore(time2)){
						return 1;
					}else{
						return -1;
					}
				}
			});
			Collections.sort(normal);
			deals.clear();
			for (Deal deal : issues){
				if (!deals.contains(deal)){
					deals.add(deal);
				}
			}
			for (Deal deal : valides){
				if (!deals.contains(deal)){
					deals.add(deal);
				}
			}
			for (Deal deal : normal){
				if (!deals.contains(deal)){
					deals.add(deal);
				}
			}
		}
		resp.add(deals);
		return resp;
	}
}
