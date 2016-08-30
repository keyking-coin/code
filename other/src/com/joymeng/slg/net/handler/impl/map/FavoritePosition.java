package com.joymeng.slg.net.handler.impl.map;

import java.util.List;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.PositionInfo;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;

public class FavoritePosition extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		byte type = in.get();
		params.put(type);//byte 0添加收藏;1修改;2删除收藏
		params.put(in.getInt());//int 坐标格子位置
		if (type < 2){
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//string 名称
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//iconId
			params.put(in.get());//byte 收藏类型
		}
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)
			throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		byte opration    = params.get(0);
		int postion      = params.get(1);
		List<PositionInfo> infos = role.getPosFavorites();
		if (opration <= 1){
			String name      = params.get(2);
			String iconId 	 = params.get(3);
			byte posType     = params.get(4);
			PositionInfo cur_info = search(postion,infos);
			if (cur_info == null){
				cur_info = new PositionInfo();
				infos.add(cur_info);
			}
			cur_info.setPos(postion);
			cur_info.setName(name);
			cur_info.setIconId(iconId);
			cur_info.setType(posType);
		}else{
			PositionInfo cur_info = search(postion,infos);
			if (cur_info == null){
				resp.fail();
				MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_MAP_FAVORITE_POSITION_NOT_FIND);
				return resp;
			}
			infos.remove(cur_info);
		}
		AbstractClientModule module = new AbstractClientModule(){
			@Override
			public short getModuleType() {
				return NTC_DTCD_MAP_FAVORITE_POSITION;
			}
		};
		module.add(infos);
		RespModuleSet rms = new RespModuleSet();
		rms.addModule(module);
		MessageSendUtil.sendModule(rms,info);
		return resp;
	}
	
	private PositionInfo search(int pos , List<PositionInfo> infos){
		for (int i = 0 ; i < infos.size() ; i++){
			PositionInfo info = infos.get(i);
			if (info.getPos() == pos){
				return info;
			}
		}
		return null;
	}
}
