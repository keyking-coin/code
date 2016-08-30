package com.joymeng.slg.domain.shop.data;

import org.w3c.dom.Element;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.object.role.Role;


public class SlideBanner extends RowElement {
	String name = "防止错误";
	String changeDelay = "10";
	
	@Override
	public void _decode(Element sun) throws Exception{
		name = sun.getAttribute("name");
		changeDelay = sun.getAttribute("changeDelay");
		super._decode(sun);
	}
	
	@Override
	public void _serialize(JoyBuffer out) {
		out.putPrefixedString(changeDelay,JoyBuffer.STRING_TYPE_SHORT);
	}

	@Override
	public RowElement _copy(ShopLayout sl,Role role) {
		SlideBanner sb = new SlideBanner();
		sb.name = name;
		sb.changeDelay = changeDelay;
		return sb;
	}
}
