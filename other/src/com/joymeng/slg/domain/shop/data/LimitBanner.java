package com.joymeng.slg.domain.shop.data;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.object.role.Role;

public class LimitBanner extends RowElement {

	@Override
	public void _serialize(JoyBuffer out) {
		
	}
	
	@Override
	public RowElement _copy(ShopLayout sl,Role role) {
		return new LimitBanner();
	}
}
