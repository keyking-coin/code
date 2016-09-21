package com.joymeng.list;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.joymeng.common.util.StringUtils;
import com.joymeng.services.utils.XmlUtils;

public class Server {
	int serverId;
	int newNum;
	int normalNum;
	int fullNum;
	int priority;
	String openTime;
	String openTimeShow;
	List<String> filters = new ArrayList<String>();
	List<String> languages = new ArrayList<String>();
	
	public int getServerId() {
		return serverId;
	}
	
	public int getNewNum() {
		return newNum;
	}
	
	public int getNormalNum() {
		return normalNum;
	}
	
	public int getFullNum() {
		return fullNum;
	}
	
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public String getOpenTimeShow() {
		return openTimeShow;
	}

	public void setOpenTimeShow(String openTimeShow) {
		this.openTimeShow = openTimeShow;
	}

	public void decode(Element server) {
		serverId  = Integer.parseInt(server.getAttribute("serverId"));
		newNum    = Integer.parseInt(server.getAttribute("newNum"));
		normalNum = Integer.parseInt(server.getAttribute("normalNum"));
		fullNum   = Integer.parseInt(server.getAttribute("fullNum"));
		if (StringUtils.isNull(server.getAttribute("priority"))) {
			priority = 0;
		} else {
			priority = Integer.parseInt(server.getAttribute("priority"));
		}
		openTime  = server.getAttribute("openTime");
		openTimeShow  = server.getAttribute("openTimeShow");
		Element[] fes = XmlUtils.getChildrenByName(server,"ChannelFilter");
		for (int i = 0 ; i < fes.length ; i++){
			Element filter = fes[i];
			filters.add(filter.getTextContent());
		}
		Element[] les = XmlUtils.getChildrenByName(server,"LanguageFilter");
		for (int i = 0 ; i < les.length ; i++){
			Element language = les[i];
			languages.add(language.getTextContent());
		}
	}

	public boolean check(String channelId,List<ChannelFilter> cfs) {
		for (int i = 0 ; i < filters.size() ; i++){
			String str = filters.get(i);
			for (int j = 0 ; j < cfs.size() ; j++){
				ChannelFilter cf = cfs.get(j);
				if (cf.check(str,channelId)){
					return true;
				}
			}
		}
		return false;
	}

	public ServerStatus checkMustShow(String channelId,List<ChannelFilter> cfs) {
		for (int i = 0 ; i < filters.size() ; i++){
			String str = filters.get(i);
			for (int j = 0 ; j < cfs.size() ; j++){
				ChannelFilter cf = cfs.get(j);
				if (cf.check(str,channelId)){
					return cf.checkMustShow();
				}
			}
		}
		return null;
	}

	public String currentName(String language, List<LanguageFilter> filters) {
		if (languages.contains(language)){
			for (int i = 0 ; i < filters.size() ; i++){
				LanguageFilter filter = filters.get(i);
				if (filter.id.equals(language)){
					return filter.check(serverId);
				}
			}
		}
		return "";
	}
	
}
