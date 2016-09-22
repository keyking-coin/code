package com.keyking.coin.service.http.handler.gm;

import com.keyking.coin.service.dao.TableName;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.ServerLog;

public class HtppAdDel extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		try {
			response.setContentType("text/plain");
			response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
			long id = Long.parseLong(request.getParameter("id"));
			if (DB.getAdDao().delete(TableName.TABLE_NAME_AD.getTable(), id)){
				response.put("result","ok");
				ServerLog.info("delete ad where id=" + id);
			}else{
				response.put("result","fail");
			}
		}catch (Exception e) {
			e.printStackTrace();
			response.put("result","fail");
		}
	}

}
