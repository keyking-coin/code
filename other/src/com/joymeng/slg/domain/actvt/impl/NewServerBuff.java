package com.joymeng.slg.domain.actvt.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.services.utils.XmlUtils;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.actvt.Actvt;
import com.joymeng.slg.domain.actvt.ClientMod;
import com.joymeng.slg.domain.actvt.data.ActvtCommon;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.ClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;

public class NewServerBuff extends Actvt {
	
	public enum BuffTag 
	{
		ADD_TROOP_MOVE_SPEED("C_ReduArmyMoveTime"),
		ADD_TROOP_GATHER_SPEED("A_ImpColl"),
		REDUCE_HURT_SOLDIE_RRATE("A_ReduDeathRate"),
		REDUCE_TREAT_SOLDIER_TIME("C_ReduSoldCureTime"),
		REDUCE_TRAIN_SOLDIER_TIME("A_ReduProdTime"),
		REDUCE_RESEARCH_SCIENCE_TIME("C_ImpResSpeed"),
		REDUCE_BUILD_TIME("C_ImpBuildSpeed")
		;
	
		private String name;
		
		public String getName() {
			return name;
		}
		
		private BuffTag(String name){
			this.name = name;
		}
	}
	
	class NSBuff
	{
		String type;
		String desc;
		int num;
	}
	
	private static Map<String, NSBuff> sBuffs = new HashMap<String, NSBuff>();
	
	private Map<String, NSBuff> buffs = new HashMap<String, NSBuff>();
	
	@Override
	public void load(Element element) throws Exception
	{
		super.load(element);
		
		Element eleSpecial = XmlUtils.getChildByName(element, "Special");
		Element[] eles = XmlUtils.getChildrenByName(eleSpecial, "Buff");
		for (int i = 0; i < eles.length; i++)
		{
			Element ele = eles[i];
			NSBuff buff = new NSBuff();
			buff.type = ele.getAttribute("type");
			buff.desc = ele.getAttribute("desc");
			buff.num = Integer.parseInt(ele.getAttribute("num"));
			buffs.put(buff.type, buff);
		}
	}
	
	public static int iGetBuff(BuffTag type)
	{
		for (Map.Entry<String, NSBuff> entry : sBuffs.entrySet())
		{
			NSBuff buff = entry.getValue();
			if (buff.type.equals(type.getName())) {
				return buff.num;
			}
		}
		return 0;
	}
	
	@Override
	public void start()
	{
		super.start();
		
		for (Map.Entry<String, NSBuff> entry : buffs.entrySet())
		{
			NSBuff buff = entry.getValue();
			sBuffs.put(buff.type, buff);
		}
		
		List<Role> roles = world.getOnlineRoles();
		AbstractClientModule mod = getNewServerBuffMod(false);
		for (int i = 0; i < roles.size(); i++)
		{
			Role role = roles.get(i);
			actvtMgr.sendReq(role, mod);
			
			RespModuleSet rms = new RespModuleSet();
			role.sendArmyMobiBuff(rms);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
		}
	}
	
	@Override
	public void end()
	{
		for (Map.Entry<String, NSBuff> entry : buffs.entrySet())
		{
			NSBuff buff = entry.getValue();
			sBuffs.remove(buff.type);
		}
		
		List<Role> roles = world.getOnlineRoles();
		AbstractClientModule mod = getNewServerBuffMod(true);
		for (int i = 0; i < roles.size(); i++)
		{
			Role role = roles.get(i);
			actvtMgr.sendReq(role, mod);
			
			RespModuleSet rms = new RespModuleSet();
			role.sendArmyMobiBuff(rms);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
		}
		
		super.end();
	}
	
	public static AbstractClientModule getNewServerBuffMod(boolean end) 
	{
		ClientMod module = new ClientMod(ClientModule.NTC_DTCD_NEW_SERVER_BUFF);

		module.add(sBuffs.size());
		for (Map.Entry<String, NSBuff> entry : sBuffs.entrySet())
		{
			NSBuff buff = entry.getValue();
			module.add(buff.type);
			if (end) {
				module.add(0);
			}
			else {
				module.add(buff.num);
			}
		}
		
//		if (!sBuffs.isEmpty()) {
//			return module;
//		}
		return module;
	}
	
	@Override
	public void makeUpDetailModule(ClientMod module, Role role)
	{
		ActvtCommon commonData = getCommonData();
		module.add(commonData.getType());
		module.add(commonData.getName());
		module.add(commonData.getDetailDesc());
		module.add(buffs.size());
		for (Map.Entry<String, NSBuff> entry : buffs.entrySet())
		{
			NSBuff buff = entry.getValue();
			module.add(String.format(buff.desc, buff.num));
		}
		
		module.add(commonData.getType()+getId());
		module.add(getStartSeconds());
		module.add(getLastSeconds());
		module.add(getNowSeconds());
	}

	@Override
	public void loadFromData(SqlData data) {}
}
