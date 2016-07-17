package com.keyking.coin.service.http.handler.gm;

import com.keyking.coin.service.domain.time.TimeLine;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpUpdateTimeLine extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String id     = request.getParameter("id");
		String type   = request.getParameter("type");
		String flag   = request.getParameter("flag");
		String bourse = request.getParameter("bourse");
		String url    = request.getParameter("_url");
		String title  = request.getParameter("title");
		String startTime = request.getParameter("start");
		String endTime = request.getParameter("end");
		TimeLine timeLine = new TimeLine();
		timeLine.setBourse(bourse);
		timeLine.setTitle(title);
		timeLine.setStartTime(startTime);
		timeLine.setEndTime(endTime);
		timeLine.setType(Byte.parseByte(type));
		timeLine.setUrl(url);
		timeLine.setBourseFlag(Byte.parseByte(flag));
		timeLine.setId(Long.parseLong(id));
		timeLine.save();
		response.put("result","ok");
	}

}
