package com.joymeng.slg.domain.activity.data;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.joymeng.slg.domain.object.role.Role;

public class ActivityEntity {
	String name;
	String module;
	boolean isrepeat;
	List<ActivityElement> elements = new ArrayList<ActivityElement>();
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getModule() {
		return module;
	}
	
	public void setModule(String module) {
		this.module = module;
	}
	
	public boolean isIsrepeat() {
		return isrepeat;
	}
	
	public void setIsrepeat(boolean isrepeat) {
		this.isrepeat = isrepeat;
	}
	
	public List<ActivityElement> getElements() {
		return elements;
	}

	public void setElements(List<ActivityElement> elements) {
		this.elements = elements;
	}

	@SuppressWarnings("unchecked")
	public <T extends ActivityElement> T searchElement(String id){
		for (int i = 0 ; i < elements.size() ; i++){
			ActivityElement element = elements.get(i);
			if (element.getId().equals(id)){
				return (T)element;
			}
		}
		return null;
	}
	
	public void load(String id, Element element) throws Exception{
		name      = element.getAttribute("name");
		module    = element.getAttribute("module");
		isrepeat  = element.getAttribute("isrepeat").equals("true");
		NodeList nodes = element.getChildNodes();
        for(int i = 0 ; i < nodes.getLength(); i++){
        	Node node = nodes.item(i);
        	if (node.getNodeType() != 1){
        		continue;
        	}
        	Element sun = (Element)node;
        	ActivityElement ae = ActivityElement.create(id,sun);
        	elements.add(ae);
        }
	}
	
	public <T extends ActivityElement> List<T> getCurrents(Role role){
		return null;
	}
}
