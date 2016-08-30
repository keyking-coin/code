package com.joymeng.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.log.GameLog;
import com.joymeng.services.utils.XmlUtils;

public class Announcement implements Instances {

	private static Announcement instance = new Announcement();

	public static Announcement getInstance() {
		return instance;
	}

	Map<String, Object> map = new HashMap<String, Object>();
	List<TextNews> textNews = new ArrayList<TextNews>();

	public void load() {
		GameLog.info("try to load System Notice");
		synchronized (map) {
			try {
				map.clear();
				textNews.clear();
				Document document = XmlUtils.load(Const.NOC_PATH+ "Announcement.xml");
				Element base = document.getDocumentElement();
				String version = String.valueOf(base.getAttribute("version"));
				Element[] ses = XmlUtils.getChildrenByName(base, "TextNews");
				for (int i = 0; i < ses.length; i++) {
					Element se = ses[i];
					TextNews announ = new TextNews();
					Element title = XmlUtils.getChildByName(se, "Title");
					Element content = XmlUtils.getChildByName(se, "Content");
					announ.setTittle(title.getTextContent());
					announ.setContent(content.getTextContent());
					textNews.add(announ);
				}
				map.put("version", version);
				map.put("textNews", textNews);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String textNews(String channelId) {
		if(channelId.equals("0000565")||channelId.equals("0000694")){
			return "";
		}
		return JsonUtil.ObjectToJsonString(map);
	}

}
