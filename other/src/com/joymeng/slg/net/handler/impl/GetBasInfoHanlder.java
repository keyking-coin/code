package com.joymeng.slg.net.handler.impl;

import java.util.List;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.bag.impl.EquipItem;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.imp.RoleStaticData;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.union.UnionBody;

public class GetBasInfoHanlder extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getLong());//其他玩家id
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		long otherId = params.get(0);//
		Role otherRole = world.getRole(otherId);
		if(otherRole == null){
			resp.fail();
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_INEXISTENCE);
			return resp;
		}
		resp.add(otherRole.getName());//玩家名称 String
		resp.add(otherRole.getIcon().getIconType());// byte 头像类型0-系统(byte)，1-自定义(string)
		resp.add(otherRole.getIcon().getIconType() == 1 ? otherRole.getIcon().getIconName() : otherRole.getIcon().getIconId());
		resp.add(otherRole.getLevel());//byte 等级
		resp.add(otherRole.getExp());//long 当前经验
		String unionName = "0";
		String allianceKey = "0";
		UnionBody ubody = unionManager.search(otherRole.getUnionId());
		if(ubody!= null){
			unionName = ubody.getName();
			allianceKey = ubody.getUnionMemberById(otherId).getAllianceKey();
		}
		resp.add(unionName);//String 联盟名称 0-无
		resp.add(allianceKey);//String 军衔  0-无
		List<EquipItem> equips = otherRole.getBagAgent().getEquipByEquipState((byte)1);
		if(equips == null || equips.size() == 0){
			resp.add(0);
		}else{
			resp.add(equips.size());//int 装备数量
			for(EquipItem equip : equips){
				resp.add(equip.getKey());//String 装备Id
			}
		}
		resp.add(otherRole.getRoleStatisticInfo().getRoleFight());//int  战斗力
		resp.add(otherRole.getRoleStatisticInfo().getKillSoldsNum());//int  杀敌数
		float compRate = 0;
		if(otherRole.getHonorAgent().getMedalCount(true) != 0){
			compRate = (float)otherRole.getHonorAgent().getMedalCount(false)/otherRole.getHonorAgent().getMedalCount(true);
		}
		resp.add(String.valueOf(compRate));//string 成就完成度
		resp.add(otherRole.getHonorAgent().getMedalCount(false));//int 勋章收集数量
		//战斗信息统计
		List<RoleStaticData> infoList = RoleStaticData.getOtherDetailList(otherRole);
		resp.add(infoList.size()); //列表大小
		for(RoleStaticData roleData : infoList){
			resp.add(roleData.getHfss().getId());//String id
			resp.add(roleData.getHfss().getStatisticName());//String 名字
			resp.add(roleData.getHfss().getType());//String 所属类
			resp.add(roleData.getHfss().getVlType());//byte 值类型，0-整数，1-小数
			resp.add(String.valueOf(roleData.getNum()));//String
		}
		//荣誉勋章列表
		List<String> medalList = otherRole.getHonorAgent().getMedalList();
		resp.add(medalList.size()); //int 数量
		if(medalList.size() > 0){
			for(String medalId : medalList){
				resp.add(medalId);//String 勋章Id
			}
		}
		return resp;
	}
}
