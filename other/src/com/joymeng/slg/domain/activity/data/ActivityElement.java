package com.joymeng.slg.domain.activity.data;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.joymeng.services.utils.XmlUtils;
import com.joymeng.slg.domain.activity.ActivityElementName;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.shop.data.ShopLayout;
import com.joymeng.slg.net.SerializeEntity;


public abstract class ActivityElement implements SerializeEntity{
	protected String activityId;
	String id;
	List<Condition> conditions = new ArrayList<Condition>();
	
	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ActivityElement> T tranform(){
		return (T) this;
	}
	
	public void decode(Element element) throws Exception{
		Element[] elements = XmlUtils.getChildrenByName(element,"Condition");
		for (int i = 0 ; i < elements.length ; i++) {
			Element e = elements[i];
			Condition cond = new Condition();
			cond.decode(e);
			conditions.add(cond);
		}
	}
	
	public boolean check(Role role){
		for (int i = 0 ; i < conditions.size() ; i++){
			Condition condition = conditions.get(i);
			if (!condition.check(role)){
				return false;
			}
		}
		return true;
	}
	
	public static ActivityElement create(String activityId, Element element) throws Exception{
		ActivityElement ae = null;
		String en = element.getNodeName();
		ActivityElementName aen = ActivityElementName.search(en);
		switch (aen){
			case ACTIVITY_ELEMENT_NAME_SHOP:{
				ae  = new ShopLayout();
				ae.decode(element);
				break;
			}
		}
		if (ae != null){
			ae.setActivityId(activityId);
		}
		return ae;
	}
}
