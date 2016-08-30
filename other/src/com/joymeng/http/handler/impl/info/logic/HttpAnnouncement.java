package com.joymeng.http.handler.impl.info.logic;

import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.list.Announcement;

public class HttpAnnouncement extends AbstractHandler{
	@Override
	public String logic(HttpRequestMessage request) {
		String channelId = request.getParameter("channelId");
		return Announcement.getInstance().textNews(channelId);
	}
}
