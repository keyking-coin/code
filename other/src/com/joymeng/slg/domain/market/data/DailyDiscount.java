package com.joymeng.slg.domain.market.data;

import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;



public class DailyDiscount extends MarketCell{
	String refreshTime;//刷新时间

	public String getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(String refreshTime) {
		this.refreshTime = refreshTime;
	}
	
	public void init(Blackshop bs) {
		super.init(bs);
		refreshTime = TimeUtils.formatDay(TimeUtils.now());
	}
	
	public boolean check(){
		if (!StringUtils.isNull(refreshTime)){
			String str = TimeUtils.formatDay(TimeUtils.now());
			return refreshTime.equals(str);
		}
		return false;
	}

	public void copy(DailyDiscount dd) {
		id          = dd.id;
		itemId      = dd.itemId;
		costKey     = dd.costKey;
		costNum     = dd.costNum;
		num         = dd.num;
		refreshTime = dd.refreshTime;
	}
	
}
