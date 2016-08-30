package com.joymeng.slg.net.handler.impl.gm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.http.HtppOprateType;
import com.joymeng.list.OperationButton;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.physics.PointVector;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.imp.RoleStaticData;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionMember;

public class QueryBasicInfo  extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());// 从哪个服务器来的请求
			long uid = in.getLong();
			int serverId = in.getInt();
			params.put(uid);
			params.put(serverId);
		} else {
			params.put(in.get());// 判断结果
			params.put(in.getInt());// 从哪个服务器返回的数据
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT)); // 拼接的字符串
		}
	}

	@Override
	public JoyProtocol handle(final UserInfo info, final ParametersEntity params)
			throws Exception {
		int type = params.get(0);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {// 请求
			int fromId = params.get(1);// 从哪个服务器来的请求,回到哪去
			long uid = params.get(2);
			int serverId = params.get(3);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x0000005C;
			TransmissionResp resp = newTransmissionResp(targetInfo);
			Role role = world.getRole(uid);

			resp.getParams().put(protocolId);// 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
			resp.getParams().put(TransmissionResp.JOY_RESP_SUCC);
			resp.getParams().put(serverId);
			if (role == null) {
				Map<String, Object> cMap = new HashMap<String, Object>();
				cMap.put("status", 0);
				cMap.put("msg", "用户不存在");
				String backMsg = JsonUtil.ObjectToJsonString(cMap);
				resp.getParams().put(backMsg);
				return resp;
			}
            //url   logic=HttpModifyEcu&playerUid=1741123&playerServerId=12293&playerProject=
            //url   logic=HttpModifyBasic&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=&playerParameter=
			List<Object> list =new ArrayList<Object>();
			if(role.isOnline()){
				OperationButton ob =new OperationButton();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("title", "state");
				map.put("value", "在线");
				ob.setButton("踢出");
				ob.setUrl("logic=HttpModifyEcu&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=state");
				List<Object> button =new ArrayList<Object>();
				button.add(ob);
				map.put("operationButton", button);
				list.add(map);
				
			}else{
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("title", "state");
				map.put("value", "离线");
				List<Object> button =new ArrayList<Object>();
				map.put("operationButton", button);
				list.add(map);
				
			}
			
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("title", "name");
			map1.put("value", role.getName());
			OperationButton ob1 =new OperationButton();
			ob1.setButton("修改");
			ob1.setChangeOrmodify("修改为");
			ob1.setInput("");
			ob1.setUrl("logic=HttpModifyBasic&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=name");
			List<Object> button1 =new ArrayList<Object>();
			button1.add(ob1);
			map1.put("operationButton", button1);
			list.add(map1);

			Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("title", "icon");
			map2.put("value",role.getIcon().getIconId());
			OperationButton ob2 =new OperationButton();
			ob2.setButton("重置");
			ob2.setUrl("logic=HttpModifyEcu&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=icon");	
			List<Object> button2 =new ArrayList<Object>();
			button2.add(ob2);
			map2.put("operationButton", button2);
			list.add(map2);
			
			Map<String, Object> map3 = new HashMap<String, Object>();
			map3.put("title", "money");
			map3.put("value", role.getMoney());
			OperationButton ob3 =new OperationButton();
			ob3.setButton("修改");
			ob3.setChangeOrmodify("变化量");
			ob3.setInput("");
			ob3.setUrl("logic=HttpModifyBasic&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=money");
			List<Object> button3 =new ArrayList<Object>();
			button3.add(ob3);
			map3.put("operationButton", button3);
			list.add(map3);
					
			Map<String, Object> map4 = new HashMap<String, Object>();
			map4.put("title", "level");
			map4.put("value", role.getLevel());
			OperationButton ob4 =new OperationButton();
			ob4.setButton("修改");
			ob4.setChangeOrmodify("修改为");
			ob4.setInput("");
			ob4.setUrl("logic=HttpModifyBasic&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=level");
			List<Object> button4 =new ArrayList<Object>();
			button4.add(ob4);
			map4.put("operationButton", button4);
			list.add(map4);	
			
			Map<String, Object> map5 = new HashMap<String, Object>();
			map5.put("title", "exp");
			map5.put("value", role.getExp());
			OperationButton ob5 =new OperationButton();
			ob5.setButton("修改");
			ob5.setChangeOrmodify("变化量");
			ob5.setInput("");
			ob5.setUrl("logic=HttpModifyBasic&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=exp");
			List<Object> button5 =new ArrayList<Object>();
			button5.add(ob5);
			map5.put("operationButton", button5);
			list.add(map5);
			
			Map<String, Object> map6 = new HashMap<String, Object>();
			boolean active =role.getVipInfo().isActive();
			map6.put("title", "vip");
			map6.put("value", role.getVipInfo().getVipLevel());
			if(!active){ //未激活
				OperationButton ob6 =new OperationButton();
				OperationButton ob6l =new OperationButton();
				ob6l.setButton("激活");
				ob6l.setUrl("logic=HttpModifyEcu&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=vipLevel");	
				ob6.setButton("修改");
				ob6.setChangeOrmodify("修改为");
				ob6.setInput("");
				ob6.setUrl("logic=HttpModifyBasic&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=vipLevel");
				List<Object> button6 =new ArrayList<Object>();
				button6.add(ob6);
				button6.add(ob6l);	
				map6.put("operationButton", button6);
			}else{
				OperationButton ob6 =new OperationButton();
				ob6.setButton("修改");
				ob6.setChangeOrmodify("修改为");
				ob6.setInput("");
				ob6.setUrl("logic=HttpModifyBasic&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=vipLevel");
				List<Object> button6 =new ArrayList<Object>();
				button6.add(ob6);	
				map6.put("operationButton", button6);
			}
			list.add(map6);
			
			Map<String, Object> map7 = new HashMap<String, Object>();
			map7.put("title", "vipExp");
			map7.put("value", role.getVipInfo().getVipExp());
			OperationButton ob7 =new OperationButton();
			ob7.setButton("修改");
			ob7.setChangeOrmodify("变化量");
			ob7.setInput("");
			ob7.setUrl("logic=HttpModifyBasic&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=vipExp");
			List<Object> button7 =new ArrayList<Object>();
			button7.add(ob7);
			map7.put("operationButton", button7);
			list.add(map7);
			
			Map<String, Object> map8 = new HashMap<String, Object>();
			map8.put("title", "stamina");
			map8.put("value", role.getRoleStamina().getCurStamina());
			OperationButton ob8 =new OperationButton();
			ob8.setButton("修改");
			ob8.setChangeOrmodify("变化量");
			ob8.setInput("");
			ob8.setUrl("logic=HttpModifyBasic&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=stamina");
			List<Object> button8 =new ArrayList<Object>();
			button8.add(ob8);
			map8.put("operationButton", button8);
			list.add(map8);
			
			Map<String, Object> map9 = new HashMap<String, Object>();
			int position =role.getCity(0).getPosition();
			PointVector ps =MapUtil.getPointVector(position);
			int x=(int) ps.x;
			int y=(int) ps.y;
			StringBuilder sb =new StringBuilder();
			sb.append(String.valueOf(x));
			sb.append(":");
			sb.append(String.valueOf(y));
			String s = sb.toString();
			map9.put("title", "position");
			map9.put("value", s);
			OperationButton ob9 =new OperationButton();
			ob9.setButton("修改");
			ob9.setChangeOrmodify("修改为");
			ob9.setInput("");
			ob9.setUrl("logic=HttpModifyBasic&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=position");
			List<Object> button9 =new ArrayList<Object>();
			button9.add(ob9);
			map9.put("operationButton", button9);
			list.add(map9);
	
			UnionBody unionBody = unionManager.search(role.getUnionId());
			if (unionBody == null) {   
				Map<String, Object> map10 = new HashMap<String, Object>();
				map10.put("title", "union");
				map10.put("value", "没有联盟");
				OperationButton ob10 =new OperationButton();
				List<Object> button10 =new ArrayList<Object>();
				button10.add(ob10);
				map10.put("operationButton", button10);
				list.add(map10);
				
				Map<String, Object> map11 = new HashMap<String, Object>();
				map11.put("title", "score");
				map11.put("value", 0);
				OperationButton ob11 =new OperationButton();
				List<Object> button11 =new ArrayList<Object>();
				button11.add(ob11);
				map11.put("operationButton", button11);
				list.add(map11);
			
			} else {
				UnionMember member = unionBody.searchMember(role.getId());
				Map<String, Object> map10 = new HashMap<String, Object>();
				map10.put("title", "union");
				map10.put("value", unionBody.getName());
				OperationButton ob10 =new OperationButton();
				List<Object> button10 =new ArrayList<Object>();
				ob10.setButton("退盟");
				ob10.setUrl("logic=HttpModifyEcu&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=union");	
				button10.add(ob10);
				map10.put("operationButton", button10);
				list.add(map10);				
			    
				Map<String, Object> map11 = new HashMap<String, Object>();
				map11.put("title", "score");
				map11.put("value", member.getScore());
				OperationButton ob11 =new OperationButton();
				List<Object> button11 =new ArrayList<Object>();
				ob11.setButton("修改");
				ob11.setChangeOrmodify("变化量");
				ob11.setInput("");
				ob11.setUrl("logic=HttpModifyBasic&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=score");
				button11.add(ob11);
				map11.put("operationButton", button11);
				list.add(map11);
				
			}
			List<RoleStaticData> infoList = RoleStaticData.getDetailList(role,0);
			for (int i = 0 ; i < infoList.size() ; i++){
				RoleStaticData roleStat = infoList.get(i);
				Map<String, Object> map12 = new HashMap<String, Object>();
				OperationButton ob12 =new OperationButton();
				List<Object> button12 =new ArrayList<Object>();
                 String name=roleStat.getHfss().getStatisticName();
				if(name.equals("statistic_destroyTroop_num")){
					map12.put("title", "statistic_destroyTroop_num");
					map12.put("value", roleStat.getNum());
					ob12.setButton("修改");
					ob12.setChangeOrmodify("变化量");
					ob12.setInput("");
					ob12.setUrl("logic=HttpModifyBasic&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=destroyTroop");
					button12.add(ob12);
					map12.put("operationButton", button12);
					list.add(map12);
				}
				
				if(name.equals("statistic_troopLose_num")){
					map12.put("title", "statistic_troopLose_num");
					map12.put("value", roleStat.getNum());
					ob12.setButton("修改");
					ob12.setChangeOrmodify("变化量");
					ob12.setInput("");
					ob12.setUrl("logic=HttpModifyBasic&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=troopLose");
					button12.add(ob12);
					map12.put("operationButton", button12);
					list.add(map12);
				}
				
				if(name.equals("statistic_attackWin_num")){
					map12.put("title", "statistic_attackWin_num");
					map12.put("value", roleStat.getNum());
					ob12.setButton("修改");
					ob12.setChangeOrmodify("变化量");
					ob12.setInput("");
					ob12.setUrl("logic=HttpModifyBasic&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=attackWin");
					button12.add(ob12);
					map12.put("operationButton", button12);
					list.add(map12);
				}
				
				if(name.equals("statistic_attackFail_num")){
					map12.put("title", "statistic_attackFail_num");
					map12.put("value", roleStat.getNum());
					ob12.setButton("修改");
					ob12.setChangeOrmodify("变化量");
					ob12.setInput("");
					ob12.setUrl("logic=HttpModifyBasic&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=attackFail");
					button12.add(ob12);
					map12.put("operationButton", button12);
					list.add(map12);
				}
				
				if(name.equals("statistic_defenceWin_num")){
					map12.put("title", "statistic_defenceWin_num");
					map12.put("value", roleStat.getNum());
					ob12.setButton("修改");
					ob12.setChangeOrmodify("变化量");
					ob12.setInput("");
					ob12.setUrl("logic=HttpModifyBasic&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=defenceWin");
					button12.add(ob12);
					map12.put("operationButton", button12);
					list.add(map12);
				}
				
				if(name.equals("statistic_defenceFail_num")){
					map12.put("title", "statistic_defenceFail_num");
					map12.put("value", roleStat.getNum());
					ob12.setButton("修改");
					ob12.setChangeOrmodify("变化量");
					ob12.setInput("");
					ob12.setUrl("logic=HttpModifyBasic&playerUid="+uid+"&playerServerId="+serverId+"&playerProject=defenceFail");
					button12.add(ob12);
					map12.put("operationButton", button12);
					list.add(map12);
				}
				
			}
			        
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> bMap = new HashMap<String, Object>();
			Map<String, Object> one = new HashMap<String, Object>();
			one.put("1", list);
			bMap.put("ModifyPlayerBasic", one);
			map.put("status", 1);
			map.put("msg", "success");
			map.put("data", bMap);
			String bMsg = JsonUtil.ObjectToJsonString(map);
			resp.getParams().put(bMsg);
			return resp;
		} else {
			byte result = params.get(1);
			int sid = params.get(2);
			NeedContinueDoSomthing next = search(info.getUid(), sid);
			if (next != null) {
				if (result == TransmissionResp.JOY_RESP_SUCC) {
					next.succeed(info, params);
				} else {
					next.fail(info, params);
				}
				removeNextDo(info.getUid(), next);
			}
			return null;
		}
	}
}
