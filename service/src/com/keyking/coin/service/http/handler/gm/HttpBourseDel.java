package com.keyking.coin.service.http.handler.gm;

import com.keyking.coin.service.dao.TableName;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpBourseDel extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		try {
			response.setContentType("text/plain");
			response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
			String name = request.getParameter("name");
			if (DB.getBourseDao().delete(TableName.TABLE_NAME_BOURSE.getTable(),name)){
				response.put("result","ok");
			}else{
				response.put("result","fail");
			}
		}catch (Exception e) {
			e.printStackTrace();
			response.put("result","fail");
		}
	}

}
