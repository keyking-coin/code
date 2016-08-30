package com.joymeng.slg.domain.actvt.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.GameLog;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.actvt.Actvt;
import com.joymeng.slg.domain.actvt.ClientMod;
import com.joymeng.slg.domain.actvt.DTManager.SearchFilter;
import com.joymeng.slg.domain.actvt.data.Activity;
import com.joymeng.slg.domain.actvt.data.Activity_newserverbuff;
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
	
	private static Map<String,NewServerBuff> buffInstances = new HashMap<String, NewServerBuff>();
	
//	private static NewServerBuff instance = null;
	private List<Activity_newserverbuff> buffs;
	
	public NewServerBuff() {
		super();
//		instance = this;
	}
	
	@Override
	public boolean init(Activity activity)
	{
		if (!super.init(activity)) {
			return false;
		}
		load();
//		buffs = actvtMgr.serachList(Activity_newserverbuff.class, new SearchFilter<Activity_newserverbuff>(){
//			@Override
//			public boolean filter(Activity_newserverbuff data) {
//				return true;
//			}
//		});
		buffInstances.put(activity.getTypeId(), this);
		return true;
	}
	
	public static int iGetBuff(BuffTag type)
	{
		for (Map.Entry<String, NewServerBuff> entry : buffInstances.entrySet())
		{
			NewServerBuff instance = entry.getValue();
			if (instance != null && instance.isRuning())
			{
				int buff = instance.getBuff(type);
				if (buff > 0) {
					return buff;
				}
			}
		}
		return 0;
	}
	
	public int getBuff(BuffTag type)
	{
		for (int i = 0; i < buffs.size(); i++)
		{
			Activity_newserverbuff buff = buffs.get(i);
			if (buff.getType().equals(type.getName()))
			{
				return buff.getNumber();
			}
		}
		return 0;
	}
	
	public void toggleNewServerBuff(String value, String data)
	{
		GameLog.info("NewServerBuff: " + value);
	}
	
	@Override
	public void start()
	{
		super.start();
		
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
		boolean flag = false;
		ClientMod module = new ClientMod(ClientModule.NTC_DTCD_NEW_SERVER_BUFF);
		
		int num = 0;
		for (Map.Entry<String, NewServerBuff> entry : buffInstances.entrySet())
		{
			NewServerBuff instance = entry.getValue();
			if (instance != null && instance.isRuning()) {
				num += instance.buffs.size();
			}
		}
		
		module.add(num);
		for (Map.Entry<String, NewServerBuff> entry : buffInstances.entrySet())
		{
			NewServerBuff instance = entry.getValue();
			if (instance == null || !instance.isRuning()) {
				continue;
			}
			
			List<Activity_newserverbuff> buffs = instance.buffs;
			for (int i = 0; i < buffs.size(); i++)
			{
				flag = true;
				module.add(buffs.get(i).getType());
				if (end) {
					module.add(0);
				}
				else {
					module.add(buffs.get(i).getNumber());
				}
			}
		}
		if (flag) {
			return module;
		}
		return null;
	}
	
	@Override
	public void makeUpDetailModule(ClientMod module, Role role)
	{
		Activity activity = getActivity();
		module.add(activity.getType());
		module.add(activity.getName());
		module.add(activity.getDetailDesc());
		module.add(buffs.size());
		for (int i = 0; i < buffs.size(); i ++)
		{
			Activity_newserverbuff buff = buffs.get(i);
			String content = buff.getContent();
			int number = buff.getNumber();
			module.add(String.format(content, number));
		}
		
		module.add(activity.getTypeId());
		module.add(getStartSeconds());
		module.add(getLastSeconds());
		module.add(getNowSeconds(role.getId()));
	}

	@Override
	public void loadFromData(SqlData data) {
		
	}

	@Override
	public void load() 
	{
		buffs = actvtMgr.serachList(Activity_newserverbuff.class, new SearchFilter<Activity_newserverbuff>(){
			@Override
			public boolean filter(Activity_newserverbuff data) {
				return data.getTypeId().equals(getActivity().getTypeId());
			}
		});
	}

}
