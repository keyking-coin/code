package com.joymeng.slg.domain.shop.data;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.joymeng.Instances;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.activity.data.Activity;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;

public abstract class RowElement implements Instances{
	
	String id = "null";
	
	List<RowElement> elements = new ArrayList<RowElement>();
	
	Object father;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<RowElement> getElements() {
		return elements;
	}

	public void setElements(List<RowElement> elements) {
		this.elements = elements;
	}
	
	public Object getFather() {
		return father;
	}

	public void setFather(Object father) {
		this.father = father;
	}

	@SuppressWarnings("unchecked")
	public <T extends RowElement> T tranform(){
		return (T) this;
	}
	
	public static RowElement decode(Element element) throws Exception{
		RowElement re = null;
		String en = element.getNodeName();
		if (en.equals("Banner")){
			re = new Banner();
        }else if (en.equals("LimitBanner")){
        	re = new LimitBanner();
        }else if (en.equals("SlideBanner")){
        	re = new SlideBanner();
        }
		re._decode(element);
		return re;
	}
	
	public void _decode(Element sun) throws Exception{
		NodeList nodes = sun.getChildNodes();
        for(int i = 0 ; i < nodes.getLength(); i++){
        	Node node = nodes.item(i);
        	if (node.getNodeType() != 1){
        		continue;
        	}
            RowElement re = RowElement.decode((Element)node);
            re.setFather(this);
            elements.add(re);
        }
	}

	public void serialize(JoyBuffer out) {
		String type = getClass().getSimpleName();
		out.putPrefixedString(type,JoyBuffer.STRING_TYPE_SHORT);
		out.putPrefixedString(id,JoyBuffer.STRING_TYPE_SHORT);
		_serialize(out);
		out.putInt(elements.size());
		for (int i = 0 ; i < elements.size() ; i++){
			RowElement element = elements.get(i);
			element.serialize(out);
		}
	}
	
	public abstract void _serialize(JoyBuffer out);
	
	public RowElement copy(ShopLayout sl , Role role){
		RowElement element = _copy(sl,role);
		element.id = id;
		if (element instanceof LimitBanner){
			//筛选一个限购banner
			Activity activity = activityManager.searchActivity(sl.getActivityId());
			long start = TimeUtils.getTime(activity.getStartDate()).getMillis() / 1000;
			long now = TimeUtils.nowLong() / 1000;
			for (int i = 0 ; i < elements.size() ; i++){
				RowElement re = elements.get(i);
				Banner banner = re.tranform();
				int last = banner.getLimitTime();
				start += last;
				if (!banner.checkNum(sl,role)){//已经不能购买了
					continue;
				}
				if (start > now){//还在限购时间以内
					RowElement nre = re.copy(sl,role);
					element.elements.add(nre);
					Banner nb = nre.tranform();
					TimerLast timer = new TimerLast(start-last,last,TimerLastType.TIME_SHOP_LIMIT_BUY);
					nb.setTimer(timer);
					break;
				}
			}
			if (element.elements.size() == 0){//全部都过期了
				RowElement nre = elements.get(elements.size() -1).copy(sl,role);
				element.elements.add(nre);
			}
		}else{
			for (int i = 0 ; i < elements.size() ; i++){
				RowElement re = elements.get(i);
				RowElement nre = re.copy(sl,role);
				element.elements.add(nre);
			}
		}
		return element;
	}
	
	public RowElement getLimitBanner(Activity activity){
		if (this instanceof LimitBanner){
			long start = TimeUtils.getTime(activity.getStartDate()).getMillis() / 1000;
			long now = TimeUtils.nowLong() / 1000;
			for (int i = 0 ; i < elements.size() ; i++){
				RowElement re = elements.get(i);
				Banner banner = re.tranform();
				int last = banner.getLimitTime();
				start += last;
				if (start > now){//还在限购时间以内
					return re;
				}
			}
		}
		return null;
	}
	
	public abstract RowElement _copy(ShopLayout sl,Role role);

	public Banner search(String bannerId) {
		if (this instanceof Banner && id.equals(bannerId)){
			return tranform();
		}
		for (int i = 0 ; i < elements.size() ; i++){
			RowElement re = elements.get(i);
			Banner banner = re.search(bannerId);
			if (banner != null){
				return banner;
			}
		}
		return null;
	}
}
