package com.joymeng.slg.domain.actvt.impl;

import org.w3c.dom.Element;

import com.joymeng.services.utils.XmlUtils;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.actvt.Actvt;
import com.joymeng.slg.domain.actvt.ClientMod;
import com.joymeng.slg.domain.actvt.data.ActvtCommon;
import com.joymeng.slg.domain.object.role.Role;


public class GroupBuy extends Actvt 
{
	class GroupItem
	{
		int type;
		int price;
		
	}
	private float eff1 = 2000;
	private float eff2 = 1;
	
	
	@Override
	public void load(Element element) throws Exception {
		super.load(element);
		
		Element eleSpecial = XmlUtils.getChildByName(element, "Special");
		Element[] eles = XmlUtils.getChildrenByName(eleSpecial, "Buff");
	}
	
	@Override
	public void start()
	{
		super.start();

	}
	
	@Override
	public void end()
	{
		super.end();
	}
	
	@Override
	public void makeUpDetailModule(ClientMod module, Role role)
	{
		ActvtCommon commonData = getCommonData();
		module.add(commonData.getType());
		module.add(commonData.getName());
		module.add(commonData.getDetailDesc());
	}

	@Override
	public void loadFromData(SqlData data) {}
}