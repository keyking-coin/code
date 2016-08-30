package com.joymeng.http.handler.impl.info.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.list.NoticeInfo;
import com.joymeng.list.NoticeManager;

public class HttpSendNotice extends AbstractHandler {

	@Override
	public String logic(HttpRequestMessage request) {
		int noticeType = Integer.valueOf(request.getParameter("noticeType"));
		String server = request.getParameter("serverId");
		String noticeContent = request.getParameter("noticeContent");
		String startTime = request.getParameter("startTime");
		List<Integer> serverList = NoticeManager.getInstance().getSeverList(server);
		if (noticeType == 1) { // 滚屏公告 rollNotice
			String endTime = request.getParameter("endTime");
			int timeDelay = Integer.valueOf(request.getParameter("timeDelay"));
			for (int i = 0; i < serverList.size(); i++) {
				int serverId = serverList.get(i);
				NoticeInfo noticeInfo = new NoticeInfo(noticeType, serverId, noticeContent, startTime, endTime,
						timeDelay);
				NoticeManager.getInstance().rollNotice(serverId, noticeInfo);
			}

		} else if (noticeType == 3) { // 定时公告 timeNotice
			for (int i = 0; i < serverList.size(); i++) {
				int serverId = serverList.get(i);
				NoticeInfo noticeInfo = new NoticeInfo(noticeType, serverId, noticeContent, startTime, null, 0);
				NoticeManager.getInstance().timeNotice(serverId, noticeInfo);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", 1);
		map.put("msg", "success");
		return JsonUtil.ObjectToJsonString(map);
	}

}
