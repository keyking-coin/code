package com.keyking.coin.service.net.logic;

import java.util.List;

import com.keyking.coin.service.domain.time.TimeLine;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class TimeLineEvent extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		int year  = buffer.getInt();
		int month = buffer.getInt();
		int nextYear  = year;
		int nextMonth = month + 1;
		if (nextMonth > 12){
			nextMonth = 1;
			nextYear ++;
		}
		String start = year + "-" + month + "-01 00:00:00";
		String end   = nextYear + "-" + nextMonth + "-01 00:00:00";
		List<TimeLine> times = DB.getTimeDao().search(start,end);
		resp.add(times);
		resp.setSucces();
		return resp;
	}
}
