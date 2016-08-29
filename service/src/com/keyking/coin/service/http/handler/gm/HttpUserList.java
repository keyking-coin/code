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
import com.keyking.coin.util.TimeUtils;

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
			int type = Integer.parseInt(request.getParameter("rank"));
			final int sort = Integer.parseInt(request.getParameter("sort"));
			Comparator<TransformUserData> comparator = null;
			if (type == 1){
				comparator = new Comparator<TransformUserData>() {
					@Override
					public int compare(TransformUserData o1, TransformUserData o2) {
						long time1 = TimeUtils.getTimes(o1.getRegistTime());
						long time2 = TimeUtils.getTimes(o2.getRegistTime());
						if (sort == 1){
							return Long.compare(time1,time2);
						}else{
							return Long.compare(time2,time1);
						}
					}
				};
			}else if (type == 2){
				comparator = new Comparator<TransformUserData>() {
					@Override
					public int compare(TransformUserData o1, TransformUserData o2) {
						if (sort == 1){
							return Integer.compare(o1.getCredit().getHp(),o2.getCredit().getHp());
						}else{
							return Long.compare(o2.getCredit().getHp(),o1.getCredit().getHp());
						}
					}
				};
			}else if (type == 3){
				comparator = new Comparator<TransformUserData>() {
					@Override
					public int compare(TransformUserData o1, TransformUserData o2) {
						if (sort == 1){
							return Integer.compare(o1.getCredit().getZp(),o2.getCredit().getZp());
						}else{
							return Long.compare(o2.getCredit().getZp(),o1.getCredit().getZp());
						}
					}
				};
			}else if (type == 4){
				comparator = new Comparator<TransformUserData>() {
					@Override
					public int compare(TransformUserData o1, TransformUserData o2) {
						if (sort == 1){
							return Integer.compare(o1.getCredit().getCp(),o2.getCredit().getCp());
						}else{
							return Long.compare(o2.getCredit().getCp(),o1.getCredit().getCp());
						}
					}
				};
			}else if (type == 5){
				comparator = new Comparator<TransformUserData>() {
					@Override
					public int compare(TransformUserData o1, TransformUserData o2) {
						if (sort == 1){
							return Integer.compare(o1.getBreach(),o2.getBreach());
						}else{
							return Long.compare(o2.getBreach(),o1.getBreach());
						}
					}
				};
			}else if (type == 6){
				comparator = new Comparator<TransformUserData>() {
					@Override
					public int compare(TransformUserData o1, TransformUserData o2) {
						if (sort == 1){
							return Float.compare(o1.getMoney(),o2.getMoney());
						}else{
							return Float.compare(o2.getMoney(),o1.getMoney());
						}
					}
				};
			}else{
				response.put("result","错误的排序类型");
				return;
			}
			List<TransformUserData> src = CTRL.getCoinUsers();
			Collections.sort(src,comparator);
			List<TransformUserData> dst = new ArrayList<TransformUserData>();
			int left = CTRL.compute(src,dst,page,num);
			response.put("result","ok");
			response.put("list",dst);
			response.put("page",page);
			response.put("left",left);
		} catch (Exception e) {
			response.put("result","获取用户列表异常");
			ServerLog.error("获取用户列表异常",e);
		}
	}

}
