package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.domain.user.PermissionType;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class AdminUserCommit extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long adminId   = buffer.getLong();
		String account = buffer.getUTF();
		String nikeName = buffer.getUTF();
		String name = buffer.getUTF();
		String ident = buffer.getUTF();
		String type = buffer.getUTF();
		String title = buffer.getUTF();
		String deposit = buffer.getUTF();
		String deal = buffer.getUTF();
		String credit_c = buffer.getUTF();
		String credit_t = buffer.getUTF();
		String hp = buffer.getUTF();
		String zp = buffer.getUTF();
		String cp = buffer.getUTF();
		String regist = buffer.getUTF();
		String time = buffer.getUTF();
		String breach = buffer.getUTF();
		String fh_reason = buffer.getUTF();
		String fh_time = buffer.getUTF();
		String other  = buffer.getUTF();
		UserCharacter user = CTRL.search(adminId);
		if (user != null && user.getPermission().isAdmin()){
			UserCharacter target = CTRL.search(account);
			if (target != null){
				target.setNikeName(nikeName);
				target.setName(name);
				target.setIdentity(ident);
				if (type.equals("买家")){
					target.getPermission().setPermission(PermissionType.buyer);
				}else{
					target.getPermission().setPermission(PermissionType.seller);
				}
				target.setTitle(title);
				target.getSeller().setDeposit(Float.parseFloat(deposit));
				target.getCredit().setTotalDealValue(Float.parseFloat(deal));
				target.getCredit().setMaxValue(Float.parseFloat(credit_c));
				target.getCredit().setTempMaxValue(Float.parseFloat(credit_t));
				target.getCredit().setHp(Integer.parseInt(hp));
				target.getCredit().setZp(Integer.parseInt(zp));
				target.getCredit().setCp(Integer.parseInt(cp));
				target.setRegistTime(regist);
				target.getPermission().setEndTime(time);
				target.setBreach(Byte.parseByte(breach));
				if (!StringUtil.isNull(fh_reason)){
					if (fh_reason.endsWith("永久封号")){
						target.getForbid().setEndTime(-1);
					}else{
						target.getForbid().setEndTime(TimeUtils.getTime(fh_time).getMillis());
					}
					target.getForbid().setReason(fh_reason);
				}
				target.setOther(other);
				target.setNeedSave(true);
				resp.setSucces();
			}else{
				resp.setError("找不到用户" + account);
			}
		}else{
			resp.setError(account + "不是管理员账号");
		}
		return resp;
	}

}
