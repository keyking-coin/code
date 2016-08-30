package com.joymeng.slg.net.handler.impl.gm;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.http.HtppOprateType;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.chat.MsgTitleType;
import com.joymeng.slg.domain.object.bag.BriefItem;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;

public class GmSendMail extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());
			long uid = in.getLong();
			int serverId = in.getInt();
			String playerUid = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			String mailId = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			String mailTitle = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			String mailContent = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			String mailAward = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			String mailAttach = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			params.put(uid);
			params.put(serverId);
			params.put(playerUid);
			params.put(mailId);
			params.put(mailTitle);
			params.put(mailContent);
			params.put(mailAward);
			params.put(mailAttach);
			
		} else {
			params.put(in.get());// 判断结果
			params.put(in.getInt()); // 从哪个服务器来的
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT)); // 拼接的字符串
		}
	}

	@Override
	public JoyProtocol handle(final UserInfo info, final ParametersEntity params)
			throws Exception {
		int type = params.get(0);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {// 请求
			int fromId = params.get(1);// 从哪个服务器来的请求,回到哪去
			int serverId = params.get(3);
			String playerUid = params.get(4); 
			String mailContent = params.get(7);
			String mailAward = params.get(8);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setEid(serverId);
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x00000084;
			TransmissionResp resp = newTransmissionResp(targetInfo);
			resp.getParams().put(protocolId);// 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
			resp.getParams().put(TransmissionResp.JOY_RESP_SUCC);
			resp.getParams().put(serverId);
			List<BriefItem> annex = new ArrayList<BriefItem>();
			if (!mailAward.equals("")) {
				List<String> items = JsonUtil.JsonToObjectList(mailAward,String.class);
				for (int i = 0; i < items.size(); i++) {
					String item = items.get(i); // Item_box_100_5_1
					if (StringUtils.isNull(item)) {
						return resp;
					}
					int first = item.indexOf("_");
					int last = item.lastIndexOf("_");
					String tp = item.substring(0,first);
					String it = item.substring(first + 1, last);
					String num = item.substring(last + 1, item.length());
					BriefItem bri = new BriefItem(tp, it,Integer.valueOf(num));
					annex.add(bri);
				}
			}
			String[] rl = new String[]{};
			if (playerUid.equals("all")) {
				String sql = "select joy_id from role";
				List<SqlData> datas = dbMgr.getGameDao().getSqlDatas(sql);
				for (int i = 0; i < datas.size(); i++) {
					SqlData data = datas.get(i);
					long uid = data.getLong(DaoData.RED_ALERT_ROLE_ID);
					chatMgr.creatSystemEmail(MsgTitleType.MSG_TITLE_GENERAL_SYSTEM, mailContent, annex, uid);
				}
			} else {
				rl = playerUid.split(",");
				for (int i = 0; i < rl.length; i++) {
					String uid = rl[i];
					Role role = world.getRole(Long.valueOf(uid));
					if (role == null) {
						continue;
					}
					chatMgr.creatSystemEmail(MsgTitleType.MSG_TITLE_GENERAL_SYSTEM, mailContent, annex, role.getId());
				}
			}
			resp.getParams().put("1");
			return resp;
		
		} else {
			byte result = params.get(1);
			int sid = params.get(2);
			NeedContinueDoSomthing next = search(info.getUid(),sid);
			if (next != null) {
				if (result == TransmissionResp.JOY_RESP_SUCC) {
					next.succeed(info, params);
				} else {
					next.fail(info, params);
				}
				removeNextDo(info.getUid(),next);
			}
			return null;
		}
	}
}
