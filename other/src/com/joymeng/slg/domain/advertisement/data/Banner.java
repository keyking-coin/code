package com.joymeng.slg.domain.advertisement.data;

import org.w3c.dom.Element;

import com.joymeng.common.util.StringUtils;
import com.joymeng.slg.net.mod.AbstractClientModule;

public class Banner  implements Comparable<Banner>{
	//id="b1" order="1" explain="" image="shop/advertise1" targetType="" targetId="0"
	//id
	String id;
	//排序
	int order = 0;
	//说明
	String explain;
	//图片
	String image;
	//目标类型
	int targetType = 0;
	//目标id
	String targetId;
	//临时id
	String tempId="";
	
	public void _serialize(AbstractClientModule module) {
//		out.putPrefixedString(tempId, JoyBuffer.STRING_TYPE_SHORT);
//		out.putPrefixedString(explain, JoyBuffer.STRING_TYPE_SHORT);
//		out.putPrefixedString(image, JoyBuffer.STRING_TYPE_SHORT);
//		out.putPrefixedString(targetType, JoyBuffer.STRING_TYPE_SHORT);
//		out.putPrefixedString(targetId, JoyBuffer.STRING_TYPE_SHORT);
		module.add(explain);
		module.add(image);
		module.add(targetType);
		module.add(targetId);
	}
	
	public void _decode(Element sun) {
		id = sun.getAttribute("id");
		explain = sun.getAttribute("explain");
		image = sun.getAttribute("image");
		String target = sun.getAttribute("targetType");
		if(StringUtils.isNumber(target))
			targetType = Integer.parseInt(target);
		targetId = sun.getAttribute("targetId");
		String index = sun.getAttribute("order");
		if(StringUtils.isNumber(index))
			order = Integer.parseInt(index);
	}
	
	public void setTempId(String tempId) {
		this.tempId = tempId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getExplain() {
		return explain;
	}
	public void setExplain(String explain) {
		this.explain = explain;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}

	public int getTargetType() {
		return targetType;
	}

	public void setTargetType(int targetType) {
		this.targetType = targetType;
	}

	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	@Override
	public int compareTo(Banner o) {
		return this.order - o.getOrder();
	}

	
}
