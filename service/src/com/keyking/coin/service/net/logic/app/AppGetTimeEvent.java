package com.keyking.coin.service.net.logic.app;

import java.util.List;

import com.keyking.coin.service.domain.time.TimeLine;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppGetTimeEvent extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp  = new AppResp(logicName);
		int year   = buffer.getInt();//年份
		int month  = buffer.getInt();//月份
		int nextYear  = year;
		int nextMonth = month + 1;
		if (nextMonth > 12){
			nextMonth = 1;
			nextYear ++;
		}
		String start = year + "-" + month + "-01";
		String end   = nextYear + "-" + nextMonth + "-01";
		List<TimeLine> times = DB.getTimeDao().search(start,end);
		if (times == null){
			resp.put("times","[]");
		}else{
			resp.put("times",times);
		}
		resp.setSucces();
		return resp;
	}

}
