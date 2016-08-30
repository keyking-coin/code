package com.joymeng.slg.net.handler.impl.role;

import com.joymeng.Instances;
import com.joymeng.log.GameLog;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.object.bag.RoleBagAgent;
import com.joymeng.slg.domain.object.bag.data.Item;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.shop.data.Shop;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class ChangeRoleIconHandler extends ServiceHandler implements Instances {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt());	//用户头像 修改的类型  0-系统  1-自定义
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT)); // 用户修改的IconId或者IconName
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			resp.fail();
			return resp;
		}
		int type = params.get(0);
		String iconName = params.get(1);
		RoleBagAgent bagAgent = role.getBagAgent();
		if (bagAgent == null) {
			resp.fail();
			return resp;
		}
		String itemId = "modify_userImage";
		byte itemType = bagAgent.getItemNumFromBag(itemId) > 0 ? (byte) 0 : (byte) 1;
		Item itemdata = dataManager.serach(Item.class, itemId);
		if (itemdata == null) {
			GameLog.error("item: " + itemId + "static data not found.");
			resp.fail();
			return resp;
		}
		if (!bagAgent.useItem(role, itemId, 1, itemType, 0)) { // 扣除资源
			resp.fail();
			return resp;
		}
		if (!role.changeRoleImage(type,iconName)) { // 修改形象
			resp.fail();
			return resp;
		}
		if(itemType==0){
			try {
				NewLogManager.baseEventLog(role, "change_face",itemId);
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
		}else{
			Shop shop = dataManager.serach(Shop.class, new SearchFilter<Shop>(){
				@Override
				public boolean filter(Shop data) {
					return data.getItemid().equals("modify_userImage");
				}
			});
			try {
				NewLogManager.baseEventLog(role, "change_face",shop.getSaleSprice());
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
		}
		return resp;
	}

}
