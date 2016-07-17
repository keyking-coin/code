package com.keyking.coin.service.http.handler.gm;

import com.keyking.coin.service.dao.TableName;
import com.keyking.coin.service.domain.time.TimeLine;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.ServerLog;

public class HttpAddTime extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		try {
			String typeCount = request.getParameter("typeCount");
			String flag = request.getParameter("flag");
			String bourse = request.getParameter("bourse");
			String url    = request.getParameter("_url");
			String title  = request.getParameter("timeTitle");
			int tc = Integer.parseInt(typeCount);
			for (int i = 1 ; i <= tc ; i++){
				String typeName = request.getParameter("t_type" + i);
				String stk = request.getParameter("timeStart" + i);
				String etk = request.getParameter("timeEnd" + i);
				TimeLine timeLine = new TimeLine();
				timeLine.setBourse(bourse);
				timeLine.setTitle(title);
				timeLine.setStartTime(stk);
				timeLine.setEndTime(etk);
				timeLine.setType(Byte.parseByte(typeName));
				timeLine.setUrl(url);
				timeLine.setBourseFlag(Byte.parseByte(flag));
				long tlid = PK.key(TableName.TABLE_NAME_TIME_LINE);
				timeLine.setId(tlid);
				timeLine.save();
			}
			response.put("result","ok");
		} catch (Exception e) {
			response.put("result","fail");
			ServerLog.error("时间轴添加异常",e);
		}
	}
}
