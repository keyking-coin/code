package com.joymeng.slg.domain.actvt;

import com.joymeng.slg.net.mod.AbstractClientModule;

public class ClientMod extends AbstractClientModule 
{
	private short moduleType;
	
	public ClientMod(short moduleType) {
		this.moduleType = moduleType;
	}

	@Override
	public short getModuleType() {
		return moduleType;
	}
	
	public ClientMod() {}
}
