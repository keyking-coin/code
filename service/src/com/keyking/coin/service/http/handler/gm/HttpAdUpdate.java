package com.keyking.coin.service.http.handler.gm;

import com.keyking.coin.service.domain.ad.ADEntity;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpAdUpdate extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		try {
			response.setContentType("text/plain");
			response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
			long id = Long.parseLong(request.getParameter("id"));
			String url = request.getParameter("url");
			int rank       = Integer.parseInt(request.getParameter("rank"));
			ADEntity ad = DB.getAdDao().search(id);
			if (ad != null){
				ad.setUrl(url);
				ad.setRank(rank);
				DB.getAdDao().save(ad);
				response.put("result","修改成功");
			}else{
				response.put("result","修改失败");
			}
		}catch (Exception e) {
			e.printStackTrace();
			response.put("result","修改失败");
		}
	}

}
