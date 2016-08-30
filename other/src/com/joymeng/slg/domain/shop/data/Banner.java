package com.joymeng.slg.domain.shop.data;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.joymeng.common.util.StringUtils;
import com.joymeng.log.LogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.activity.data.Activity;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.bag.data.Equip;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.shop.RoleShopAgent;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.net.mod.RespModuleSet;

public class Banner extends RowElement {
	String name;
	String image;
	String data;
	String price;
	String limitDuration = "null";
	String personLimit = "null";
	String serviceLimit = "null";
	String productId = "null";
	int showNum;//显示数量
	TimerLast timer = null;//倒计时
	
	public void setTimer(TimerLast timer) {
		this.timer = timer;
	}

	public String getPersonLimit() {
		return personLimit;
	}

	public String getServiceLimit() {
		return serviceLimit;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Override
	public void _decode(Element sun) {
		id = sun.getAttribute("id");
		name = sun.getAttribute("name");
		image = sun.getAttribute("image");
		data = sun.getAttribute("data");
		price = sun.getAttribute("price");
		limitDuration = sun.getAttribute("limitDuration");
		personLimit = sun.getAttribute("personLimit");
		serviceLimit = sun.getAttribute("serviceLimit");
		productId = sun.getAttribute("productId");
	}

	@Override
	public void _serialize(JoyBuffer out) {
		out.putPrefixedString(name,JoyBuffer.STRING_TYPE_SHORT);
		out.putPrefixedString(image,JoyBuffer.STRING_TYPE_SHORT);
		out.putPrefixedString(price,JoyBuffer.STRING_TYPE_SHORT);
		out.putPrefixedString(showNum + "",JoyBuffer.STRING_TYPE_SHORT);
		if (timer != null){
			out.putInt(1);
			timer.serialize(out);
		}else{
			out.putInt(0);
		}
		out.putPrefixedString(productId,JoyBuffer.STRING_TYPE_SHORT);
		out.putPrefixedString(data,JoyBuffer.STRING_TYPE_SHORT);
	}
	
	public String getBuyKey(String aid,String sid){
		return aid + "@" + sid + "@" + id;
	}
	
	@Override
	public RowElement _copy(ShopLayout sl,Role role) {
		Banner banner = new Banner();
		banner.name = name;
		banner.image = image;
		banner.data = data;
		banner.price = price;
		banner.limitDuration = limitDuration;
		banner.personLimit = personLimit;
		banner.serviceLimit = serviceLimit;
		banner.productId = productId;
		RoleShopAgent shop = role.getShopAgent();
		String key = getBuyKey(sl.getActivityId(),sl.getId());
		if (!StringUtils.isNull(personLimit)){
			int max = Integer.parseInt(personLimit);
			banner.showNum = shop.computPersonLimitNum(key,max);
		}else if (!StringUtils.isNull(serviceLimit)){
			int max = Integer.parseInt(serviceLimit);
			banner.showNum = shop.computPersonLimitNum(key,max);
		}else{
			banner.showNum = 1;
		}
		return banner;
	}
	
	public boolean checkTime(Activity activity){
		if (father instanceof LimitBanner){//我是限购banner
			LimitBanner lb = (LimitBanner)father;
			RowElement re = lb.getLimitBanner(activity);
			return re == null ? false : re.equals(this);
		}
		return true;
	}
	
	public boolean isPersonLimitNum(){
		if (father instanceof LimitBanner && !StringUtils.isNull(personLimit)){
			return Integer.parseInt(personLimit) > 0;
		}
		return false;
	}
	
	public boolean isServiceLimitNum(){
		if (father instanceof LimitBanner && !StringUtils.isNull(serviceLimit)){
			return Integer.parseInt(serviceLimit) > 0;
		}
		return false;
	}
	
	public boolean checkNum(ShopLayout sl,Role role){
		if (father instanceof LimitBanner){
			int num = 0;
			RoleShopAgent shop = role.getShopAgent();
			String key = getBuyKey(sl.getActivityId(),sl.getId());
			if (!StringUtils.isNull(personLimit)){//个人限购
				int max = Integer.parseInt(personLimit);
				num = shop.computPersonLimitNum(key,max);
				if (num == 0){
					return false;
				}
			}
			if (!StringUtils.isNull(serviceLimit)){//服务器限购
				int max = Integer.parseInt(serviceLimit);
				num = shop.computServiceLimitNum(key,max);
				if (num == 0){
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean checkPersonNum(ShopLayout sl,Role role){
		if (father instanceof LimitBanner){
			RoleShopAgent shop = role.getShopAgent();
			String key = getBuyKey(sl.getActivityId(),sl.getId());
			if (!StringUtils.isNull(personLimit)){//个人限购
				int max = Integer.parseInt(personLimit);
				int num = shop.computPersonLimitNum(key,max);
				return num > 0;
			}
		}
		return true;
	}
	
	public boolean checkServiceNum(ShopLayout sl,Role role){
		if (father instanceof LimitBanner){
			RoleShopAgent shop = role.getShopAgent();
			String key = getBuyKey(sl.getActivityId(),sl.getId());
			if (!StringUtils.isNull(serviceLimit)){//个人限购
				int max = Integer.parseInt(serviceLimit);
				int num = shop.computServiceLimitNum(key,max);
				return num > 0;
			}
		}
		return true;
	}
	
	public int getLimitTime(){
		if (father instanceof LimitBanner && !StringUtils.isNull(limitDuration)){
			int hi = limitDuration.lastIndexOf("h");
			int mi = limitDuration.lastIndexOf("m");
			int si = limitDuration.lastIndexOf("s");
			int h = 0 , m = 0 , s = 0;
			if (hi > 0){
				int bi = 0;
				int ei = hi;
				String temp = limitDuration.substring(bi,ei);
				h = Integer.parseInt(temp);
			}
			if (mi > 0){
				int bi = hi > 0 ? hi + 1 : 0;
				int ei = mi;
				String temp = limitDuration.substring(bi,ei);
				m = Integer.parseInt(temp);
			}
			if (si > 0){
				int bi = mi > 0 ? mi + 1 : 0;
				int ei = si;
				String temp = limitDuration.substring(bi,ei);
				m = Integer.parseInt(temp);
			}
			return h * 3600 + m * 60 + s;
		}
		return 0;
	}

	public static boolean _buyOk(String data,Role role,RespModuleSet rms){
		if (!StringUtils.isNull(data)){
			String[] ds = data.split(",");
			List<Object> res = new ArrayList<Object>();
			List<ItemCell> cells = new ArrayList<ItemCell>();
			for (int i = 0 ; i < ds.length ; i++){
				String s = ds[i];
				String[] ss = s.split(":");
				ResourceTypeConst type = ResourceTypeConst.search(ss[0]);
				switch (type){
					case RESOURCE_TYPE_FOOD:
					case RESOURCE_TYPE_METAL:
					case RESOURCE_TYPE_OIL:
					case RESOURCE_TYPE_ALLOY:{
						res.add(type);
						res.add(Long.parseLong(ss[1]));
						break;
					}
					case RESOURCE_TYPE_ITEM:{
						int num = Integer.parseInt(ss[2]);
						List<ItemCell> temp = role.getBagAgent().addGoods(ss[1],num);
						if (temp.size() > 0){
							cells.addAll(temp);
						}
						LogManager.itemOutputLog(role, num, "_buyOk", ss[1]);
						break;
					}
					case RESOURCE_TYPE_EQUIP:{
						int num = Integer.parseInt(ss[2]);
						List<ItemCell> temp = role.getBagAgent().addEquip(ss[1],num);
						if (temp.size() > 0){
							cells.addAll(temp);
						}
						LogManager.itemOutputLog(role, num, "_buyOk", ss[1]);
						Equip  equip = dataManager.serach(Equip.class, ss[1]);
						LogManager.equipLog(role, equip.getEquipType(), equip.getBeizhuname(), "充值购买");
						break;
					}
					case RESOURCE_TYPE_MATERIAL:{
						int num = Integer.parseInt(ss[2]);
						List<ItemCell> temp = role.getBagAgent().addOther(ss[1],num);
						if (temp.size() > 0){
							cells.addAll(temp);
						}
						LogManager.itemOutputLog(role, num, "_buyOk", ss[1]);
						break;
					}
					case RESOURCE_TYPE_GOLD:{
						int money = Integer.parseInt(ss[1]);
						role.addRoleMoney(money);
						role.sendRoleToClient(rms);
						LogManager.goldOutputLog(role, money, "_buyOk");
						break;
					}
					case RESOURCE_TYPE_MONTH_CARD:{//月卡逻辑
						
						break;
					}
					default:{
						break;
					}
				}
			}
			if (res.size() > 0){
				role.addResourcesToCity(false,rms,0,res.toArray());
			}
			if (cells.size() > 0){
				role.getBagAgent().sendItemsToClient(rms,cells);
			}
			return true;
		}
		return false;
	}
	
	public boolean buyOk(Role role,RespModuleSet rms) {
		return _buyOk(data,role,rms);
	}
}
