package com.joymeng.list;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.log.GameLog;
import com.joymeng.services.utils.XmlUtils;

public class LoginJudge  implements Instances{
	private static LoginJudge instance = new LoginJudge();

	public static LoginJudge getInstance() {
		return instance;
	}

	Map<String, String> map = new HashMap<String, String>();

	public void load() {
		GameLog.info("try to load login judge");
		try {
			Document document = XmlUtils.load(Const.CONF_PATH + "LoginJudge.xml");
			Element base = document.getDocumentElement();
			Element[] ses = XmlUtils.getChildrenByName(base, "Conditions");
			for (int i = 0; i < ses.length; i++) {
				Element se = ses[i];
				Element[] snes = XmlUtils.getChildrenByName(se, "parameter");
				for (int j = 0; j < snes.length; j++) {
					Element sne = snes[j];
					String key = sne.getAttribute("key");
					String value = sne.getAttribute("value");
					map.put(key, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public boolean allowLogin(String memory, String version, String country,
			String language) {
		String me = map.get("memory");
		String ve = map.get("version");
		String co = map.get("country");
		String la = map.get("language");

		if (Integer.valueOf(memory) < Integer.valueOf(me)) {
			return false;
		}
		if(ve.equals("Android7.0")){
			
		}
		if (!co.equals(country)) {
			return false;
		}
		if (!la.equals(language)) {
			return false;
		}
		return true;
	}
	

}
