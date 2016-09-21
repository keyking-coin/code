package com.joymeng.slg.domain.forbidden;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.object.role.Role;

public class ForbiddenManager implements Instances{

	private static ForbiddenManager instance = new ForbiddenManager();	
	boolean savIng = false;

	public static ForbiddenManager getInstance() {
		return instance;
	}
    Map<Long,Map<Byte, Forbidden>> forbidden = new HashMap<Long, Map<Byte,Forbidden>>();
    
    List<String> uuidList = new ArrayList<String>();
 
	public Map<Long, Map<Byte, Forbidden>> getForbidden() {
		return forbidden;
	}

     public List<String> getUuidList() {
		return uuidList;
	}

	public void setUuidList(List<String> uuidList) {
		this.uuidList = uuidList;
	}



	/**
	 * 角色禁封
	 */	
	public boolean roleForbidden(Role role, byte type) {
		Map<Byte, Forbidden> map = forbidden.get(role.getId());
		if (map == null) {
			return true;
		}
		Forbidden foridden = map.get(type);
		if (foridden == null) {
			return true;
		}
		if (StringUtils.isNull(foridden.getEndTime())) {
			return false;
		}
		return TimeUtils.nowLong() > TimeUtils.getTimes(foridden.getEndTime()) ? true : false;
	}
	/**
	 * 根据uuid找到禁封信息
	 * @return 
	 */	
	public Forbidden getForbiddenByUuid(String uuid) {
		for (Long lg : forbidden.keySet()) {
			Map<Byte, Forbidden> map = forbidden.get(lg);
			for (Forbidden fb : map.values()) {
				if (fb.getUuid().equals(uuid) && fb.getType() == (byte) 4) {
					return fb;
				}
			}
		}
		return null;
	}
	/**
	 * 判断是否禁封重写
	 * 
	 * @return
	 */
	public boolean judgmentBan(String uuid, UserInfo info, byte type, String temporary, String permanent) {
		Forbidden foridden = getForbiddenByUuid(uuid);
		if (foridden != null) {
			String startTime = foridden.getStartTime();
			String endTime = foridden.getEndTime();
			if (TimeUtils.getTimes(startTime) < TimeUtils.nowLong()) {
				if (StringUtils.isNull(endTime)) {
					MessageSendUtil.sendNormalTip(info, temporary);
					return false;
				}
				float time = (float) ((TimeUtils.getTimes(endTime) - TimeUtils.getTimes(startTime))
						/ (float) (Const.ONE_HOUR_TIME * 1000));
				if ((TimeUtils.getTimes(endTime) - TimeUtils.nowLong()) > 0) {
					float pass = (float) ((TimeUtils.getTimes(endTime) - TimeUtils.nowLong())
							/ (float) (Const.ONE_HOUR_TIME * 1000));
					BigDecimal b = new BigDecimal(pass);
					float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
					MessageSendUtil.sendNormalTip(info, permanent, time, f1);
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * 判断是否禁封
	 * 
	 * @return
	 */
	public boolean judgmentBan(UserInfo info, byte type, String temporary, String permanent) {
		Forbidden foridden = forbidden.get(info.getUid()).get(type);
		String startTime = foridden.getStartTime();
		String endTime = foridden.getEndTime();
		if (TimeUtils.getTimes(startTime) < TimeUtils.nowLong()) {
			if (StringUtils.isNull(endTime)) {
				MessageSendUtil.sendNormalTip(info, temporary);
				return false;
			}
			float time = (float) ((TimeUtils.getTimes(endTime) - TimeUtils.getTimes(startTime))
					/ (float) (Const.ONE_HOUR_TIME * 1000));
			if ((TimeUtils.getTimes(endTime) - TimeUtils.nowLong()) > 0) {
				float pass = (float) ((TimeUtils.getTimes(endTime) - TimeUtils.nowLong())
						/ (float) (Const.ONE_HOUR_TIME * 1000));
				BigDecimal b = new BigDecimal(pass);
				float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
				MessageSendUtil.sendNormalTip(info, permanent, time, f1);
				return false;
			}
		}
		return true;
	}
	
	public void loadForbidden() {
		List<SqlData> datas = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_PLAY_BAN);
		if (datas == null) {
			return;
		}
		for (int i = 0; i < datas.size(); i++) {
			SqlData data = datas.get(i);
			loadFromData(data);
		}
	}

	public void loadFromData(SqlData data) {
		long uid = data.getLong(DaoData.RED_ALERT_GENERAL_UID);
		byte type = data.getByte(DaoData.RED_ALERT_FORBIDDEN_TYPE);
		String startTime = data.getString(DaoData.RED_ALERT_STARTTIME);
		String endTime = data.getString(DaoData.RED_ALERT_ENDTIME);
		String uuid = data.getString(DaoData.RED_ALERT_ROLE_UUID);
		if (type == (byte) 4 && !StringUtils.isNull(uuid)) {
			uuidList.add(uuid);
		}
		Forbidden fb = new Forbidden(uid, type, startTime, endTime, uuid);
		Map<Byte, Forbidden> map = forbidden.get(uid);
		if (map == null) {
			map = new HashMap<Byte, Forbidden>();
		}
		map.put(type, fb);
		forbidden.put(uid, map);
	}
	
	public void save() {
		for (Long lg : forbidden.keySet()) {
			Map<Byte, Forbidden> map = forbidden.get(lg);
			for (Forbidden fb : map.values()) {
				fb.save();
			}
		}
	}

}
