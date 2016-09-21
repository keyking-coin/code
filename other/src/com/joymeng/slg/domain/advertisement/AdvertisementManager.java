package com.joymeng.slg.domain.advertisement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.advertisement.data.AdveBanners;
import com.joymeng.slg.domain.advertisement.data.Advertisement;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;

public class AdvertisementManager implements Instances {

	private static AdvertisementManager instance = new AdvertisementManager();

	public static AdvertisementManager getInstance() {
		if (instance == null)
			instance = new AdvertisementManager();
		return instance;
	}
	
	private long refresh_time = 0;

	// 全部banners
	List<AdveBanners> adveBanners = new ArrayList<AdveBanners>();
	// 已生效
	ConcurrentHashMap<String, AdveBanners> effectedMap = new ConcurrentHashMap<String, AdveBanners>();
	// 未生效
	ConcurrentHashMap<String, AdveBanners> noEffectedMap = new ConcurrentHashMap<String, AdveBanners>();

	/**
	 * 
	 * @Title: update
	 * @Description: 刷新
	 * 
	 * @return void
	 */
	public void update() {
		// 已生效
		ConcurrentHashMap<String, AdveBanners> effectedMapCopy = new ConcurrentHashMap<String, AdveBanners>();
		// 未生效
		ConcurrentHashMap<String, AdveBanners> noEffectedMapCopy = new ConcurrentHashMap<String, AdveBanners>();
		long now = TimeUtils.nowLong();
		for (AdveBanners advebanners : adveBanners) {
			if (advebanners.isEffect(now)) {
				effectedMapCopy.put(advebanners.getId(), advebanners);
			} else if (advebanners.isNoEffect(now)) {
				noEffectedMapCopy.put(advebanners.getId(), advebanners);
			}
		}
		effectedMap = effectedMapCopy;
		noEffectedMap = noEffectedMapCopy;
	}

	/**
	 * 
	 * @Title: tickEffect
	 * @Description: 加入生效的
	 * 
	 * @return boolean
	 * @return
	 */
	public boolean tickEffect(long now) {
		List<AdveBanners> delKeys = new ArrayList<AdveBanners>();
		for (AdveBanners advebanners : noEffectedMap.values()) {
			if (advebanners.isEffect(now)) {
				delKeys.add(advebanners);
			}
		}
		if (delKeys.size() > 0) {
			for (AdveBanners key : delKeys) {
				noEffectedMap.remove(key.getId());
				effectedMap.put(key.getId(), key);
			}
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @Title: tickInvalid
	 * @Description: 删除失效的
	 * 
	 * @return boolean
	 * @return
	 */
	public boolean tickInvalid(long now) {
		List<String> delKeys = new ArrayList<String>();
		for (AdveBanners advebanners : effectedMap.values()) {
			if (advebanners.isInvalid(now)) {
				delKeys.add(advebanners.getId());
			}
		}
		if (delKeys.size() > 0) {
			for (String key : delKeys) {
				effectedMap.remove(key);
			}
			return true;
		}
		return false;
	}

	public void _tick() {
		//每10秒刷新一次
		long now = TimeUtils.nowLong();
		if(now - refresh_time > 0){
			boolean isEff = tickEffect(now);
			boolean isInv = tickInvalid(now);
			if (isEff || isInv)
				sendToClient();
			
			refresh_time = now + Const.SECOND*10;
		}
		
	}

	public void sendToClient() {
		RespModuleSet rms = new RespModuleSet();
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ADVERTISEMENT;
			}
		};
		_serialize(module);
		rms.addModule(module);
		List<Role> roles = world.getOnlineRoles();
		for (int i = 0; i < roles.size(); i++) {
			Role role = roles.get(i);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
		}
	}

	/**
	 * 加载xml活动实体
	 * 
	 * @throws Exception
	 */
	public void load() throws Exception {
		Advertisement entity = new Advertisement();
		Document document = activityManager
				.getDocumentAndClose(new File(Const.NOC_ADV+ entity.getClass().getSimpleName()+".xml"));
		Element base = document.getDocumentElement();
		load(base);
		update();
	}

	public void load(Element element) throws Exception {
		List<AdveBanners> adves = new ArrayList<AdveBanners>();
		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			try {
				Node node = nodes.item(i);
				if (node.getNodeType() != 1) {
					continue;
				}
				Element sun = (Element) node;
				AdveBanners adv = new AdveBanners();
				adv._decode(sun);
				adves.add(adv);
			} catch (Exception e) {
				GameLog.error(e);
				continue;
			}
		}
		adveBanners = adves;
	}

	// 下发
	public void _serialize(AbstractClientModule module) {
		module.add(effectedMap.size());
		for (AdveBanners adv : effectedMap.values()) {
			adv._serialize(module);
		}
	}

}
