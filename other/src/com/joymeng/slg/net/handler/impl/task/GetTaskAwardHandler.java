package com.joymeng.slg.net.handler.impl.task;

import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.MissionType;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class GetTaskAwardHandler extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//taskId
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//大分支类型
		
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = world.getRole(info.getUid());
		if (role == null){
			NewLogManager.misTakeLog("GetTaskAwardHandler getRole is null where uid = " + info.getUid());
			resp.fail();
			return resp;
		}
		String missionId = params.get(0);

		String key = params.get(1);
		MissionType type  = MissionType.search(key);
		if(type == null){
			resp.fail();
		}else if(type == MissionType.MS_PRIZE){
			if(!role.getTaskAgent().getCurScheduleMission(role)){
				resp.fail();
			}
		}else{
//			String[] strs = missionId.split("_");
//			if(strs.length != 2){
//				resp.fail();
//				return resp;
//			}
			if(!role.getTaskAgent().getAwordFromMission(role, type/*, strs[0]*/, missionId)){
				resp.fail();
			}
		}
		return resp;
	}

}
