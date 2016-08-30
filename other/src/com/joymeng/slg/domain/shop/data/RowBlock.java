package com.joymeng.slg.domain.shop.data;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.SerializeEntity;

public class RowBlock implements SerializeEntity{
	String id;
	String name;
	int posx;
	int posy;
	int width;
	int height;
	String padding;
	List<RowElement> elements = new ArrayList<RowElement>();
	
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public int getPosx() {
		return posx;
	}


	public void setPosx(int posx) {
		this.posx = posx;
	}


	public int getPosy() {
		return posy;
	}


	public void setPosy(int posy) {
		this.posy = posy;
	}


	public int getWidth() {
		return width;
	}


	public void setWidth(int width) {
		this.width = width;
	}


	public int getHeight() {
		return height;
	}


	public void setHeight(int height) {
		this.height = height;
	}


	public String getPadding() {
		return padding;
	}


	public void setPadding(String padding) {
		this.padding = padding;
	}


	public List<RowElement> getElements() {
		return elements;
	}


	public void setElements(List<RowElement> elements) {
		this.elements = elements;
	}


	public void decode(Element element) throws Exception{
		id = element.getAttribute("id");
		name = element.getAttribute("name");
		posx = Integer.parseInt(element.getAttribute("posx"));
		posy = Integer.parseInt(element.getAttribute("posy"));
		width = Integer.parseInt(element.getAttribute("width"));
		height = Integer.parseInt(element.getAttribute("height"));
		padding = element.getAttribute("padding");
		NodeList nodes = element.getChildNodes();
        for(int i = 0 ; i < nodes.getLength(); i++){
        	Node node = nodes.item(i);
        	if (node.getNodeType() != 1){
        		continue;
        	}
            Element sun = (Element)node;
            RowElement re = RowElement.decode(sun);
            re.setFather(this);
            elements.add(re);
        }
	}


	@Override
	public void serialize(JoyBuffer out) {
		out.putPrefixedString(id,JoyBuffer.STRING_TYPE_SHORT);
		out.putPrefixedString(name,JoyBuffer.STRING_TYPE_SHORT);
		out.putInt(posx);
		out.putInt(posy);
		out.putInt(width);
		out.putInt(height);
		out.putPrefixedString(padding,JoyBuffer.STRING_TYPE_SHORT);
		out.putInt(elements.size());
		for (int i = 0 ; i < elements.size() ; i++){
			RowElement re = elements.get(i);
			re.serialize(out);
		}
	}


	public void copy(ShopLayout sl,RowBlock rb, Role role) {
		rb.id = id;
		rb.name = name;
		rb.posx = posx;
		rb.posy = posy;
		rb.width = width;
		rb.height = height;
		rb.padding = padding;
		for (int i = 0 ; i < elements.size() ; i++){
			RowElement re = elements.get(i);
			RowElement nre = re.copy(sl,role);
			rb.elements.add(nre);
		}
	}
}
