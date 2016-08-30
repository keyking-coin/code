package com.joymeng.slg.domain.code;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.domain.chat.MsgTitleType;
import com.joymeng.slg.domain.object.bag.BriefItem;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.world.GameConfig;

public class CodeManager implements Instances{

	static int serverId = ServiceApp.instanceId - GameConfig.SERVER_LIST_ID;
	static int appid = 1001;
	
	public static String getCodeByPost(String channelid, String code, long uid)
			throws IOException {

		try {
			URL url = new URL(
					"http://netunion.joymeng.com/index.php?m=Api&c=Index&a=verifyCode");
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);// 是否输入参数
			StringBuffer params = new StringBuffer();
			params.append("&appid=").append(appid).append("&channelid=")
					.append(channelid).append("&code=").append(code)
					.append("&use=1").append("&uid=").append(uid)
					.append("&serverid=").append(serverId);
			byte[] bypes = params.toString().getBytes();
			connection.getOutputStream().write(bypes);// 输入参数

			InputStream in = connection.getInputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];
			int length = 0;
			while ((length = in.read(buffer)) != -1) {
				bos.write(buffer, 0, length);
			}
			in.close();
			return new String(bos.toByteArray(), "GBK");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public boolean sendMail(Role role, String message) {
		Map<String, Object> map = JsonUtil.JsonToObject(message, Map.class);
		if (map == null) {
			return false;
		}
		int status = (int) map.get("status");
		String msg = (String) map.get("msg");
		List<BriefItem> annex = new ArrayList<BriefItem>();
		if (status != 1) {
			if (msg.equals("兑换码不存在")) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_USE_CODE_NOEXIST);
			} else if (msg.equals("兑换码已过期")) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_USE_CODE_OUTDATA);
			} else if (msg.equals("兑换码已使用")) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_USE_CODE_USED);
			} else if (msg.equals("渠道ID不存在")||msg.equals("不支持该渠道")) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_USE_CODE_SUPPORT);
			} else if (msg.equals("已使用过该类型激活码")) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_USE_CODE_NOTYPE);
			} else {
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_USE_CODE_FAIL);
			}
			return false;
		} else {
			Map<String, Object> data = (Map<String, Object>) map.get("data");
			List<String> items = (List<String>) data.get("data");
			if (items == null || items.size() == 0) {
				return false;
			}
			for (int i = 0; i < items.size(); i++) {
				String item = items.get(i); // Item_box_100_5_1
				if (StringUtils.isNull(item)) {
					return false;
				}
				int first = item.indexOf("_");
				int last = item.lastIndexOf("_");
				String tp = item.substring(0,first);
				String it = item.substring(first + 1, last);
				String num = item.substring(last + 1, item.length());
				BriefItem bri = new BriefItem(tp, it, Integer.valueOf(num));
				annex.add(bri);
			}
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_USE_CODE_SUCCEED);
			String content = "兑换码使用成功,恭喜你获得以下礼包:";
			chatMgr.creatSystemEmail(MsgTitleType.MSG_TITLE_GENERAL_SYSTEM,content, annex, role.getId());
			return true;
		}
	}
	
}
