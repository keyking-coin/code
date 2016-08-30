package com.joymeng.slg.domain.shop.data;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.utils.XmlUtils;
import com.joymeng.slg.domain.activity.data.ActivityElement;
import com.joymeng.slg.domain.object.role.Role;

public class ShopLayout extends ActivityElement {
	String id;
	String margin;
	String background;
	String grid;
	List<RowBlock> rows = new ArrayList<RowBlock>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<RowBlock> getRows() {
		return rows;
	}

	public void setRows(List<RowBlock> rows) {
		this.rows = rows;
	}

	public String getMargin() {
		return margin;
	}
	
	public void setMargin(String margin) {
		this.margin = margin;
	}
	
	public String getBackground() {
		return background;
	}
	
	public void setBackground(String background) {
		this.background = background;
	}
	
	public String getGrid() {
		return grid;
	}
	
	public void setGrid(String grid) {
		this.grid = grid;
	}

	@Override
	public void decode(Element element) throws Exception{
		super.decode(element);
		id = element.getAttribute("id");
		margin = element.getAttribute("margin");
		background = element.getAttribute("background");
		grid = element.getAttribute("grid");
		rows.clear();
		Element[] elements = XmlUtils.getChildrenByName(element,"RowBlock");
		for (int i = 0 ; i < elements.length ; i++) {
			Element e = elements[i];
			RowBlock re = new RowBlock();
			re.decode(e);
			rows.add(re);
		}
	}

	@Override
	public void serialize(JoyBuffer out) {
		out.putPrefixedString(activityId,JoyBuffer.STRING_TYPE_SHORT);
		out.putPrefixedString(id,JoyBuffer.STRING_TYPE_SHORT);
		out.putPrefixedString(margin,JoyBuffer.STRING_TYPE_SHORT);
		out.putPrefixedString(grid,JoyBuffer.STRING_TYPE_SHORT);
		out.putInt(rows.size());
		for (int i = 0 ; i < rows.size() ; i++){
			RowBlock rb = rows.get(i);
			rb.serialize(out);
		}
	}

	public void copy(ShopLayout layout, Role role) {
		layout.activityId = activityId;
		layout.id  = id;
		layout.margin = margin;
		layout.background = background;
		layout.grid = grid;
		for (int i = 0 ; i < rows.size() ; i++){
			RowBlock rb = rows.get(i);
			RowBlock nrb = new RowBlock();
			rb.copy(this,nrb,role);
			layout.rows.add(nrb);
		}
	}
	
	public Banner search(String bannerId){
		for (int i = 0 ; i < rows.size() ; i++){
			RowBlock rb = rows.get(i);
			for (int j = 0 ; j < rb.getElements().size() ; j++){
				RowElement re = rb.getElements().get(j);
				Banner banner = re.search(bannerId);
				if (banner != null){
					return banner;
				}
			}
		}
		return null;
	}
}
