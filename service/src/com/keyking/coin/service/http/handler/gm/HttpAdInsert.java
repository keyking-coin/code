package com.keyking.coin.service.http.handler.gm;

import com.keyking.coin.service.dao.TableName;
import com.keyking.coin.service.domain.ad.ADEntity;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpAdInsert extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		try {
			response.setContentType("text/plain");
			response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
			String url     = request.getParameter("ad_url");
			String picName = request.getParameter("ad_pic");
			ADEntity ade = new ADEntity();
			long id = PK.key(TableName.TABLE_NAME_AD);
			ade.setId(id);
			ade.setUrl(url);
			ade.setPic(picName);
			if (DB.getAdDao().save(ade)){
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
