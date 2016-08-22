package com.keyking.coin.service.http.handler.gm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.TransformUserData;
import com.keyking.coin.util.ServerLog;

public class HttpUserList extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		try {
			//页码
			int page = Integer.parseInt(request.getParameter("page"));
			//每一页数量
			int num  = Integer.parseInt(request.getParameter("num"));
			List<TransformUserData> src = CTRL.getCoinUsers();
			Collections.sort(src,new Comparator<TransformUserData>() {
				@Override
				public int compare(TransformUserData o1, TransformUserData o2) {
					return Long.compare(o1.getId(), o2.getId());
				}
			});
			List<TransformUserData> dst = new ArrayList<TransformUserData>();
			int left = CTRL.compute(src,dst,page,num);
			response.put("result","ok");
			response.put("list",dst);
			response.put("page",page);
			response.put("left",left);
		} catch (Exception e) {
			response.put("result","fail");
			ServerLog.error("时间轴添加异常",e);
		}
	}

}
