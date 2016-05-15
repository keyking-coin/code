package com.keyking.coin.service.net.logic.app;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.Revert;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.tranform.page.deal.TransformRevert;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.StringUtil;

public class AppRevert extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid       = buffer.getLong();
		long tid       = buffer.getLong();//回复目标
		long dealId    = buffer.getLong();//在那个帖子回复
		String content = buffer.getUTF();//回复的内容
		UserCharacter user = CTRL.search(uid);
		if (user == null){
			resp.setError("系统错误");
			return resp;
		}
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			resp.setError("您已经被封号原因是:" + forbidStr);
			return resp;
		}
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal == null){
			resp.setError("此帖已撤销");
			return resp;
		}
		if (StringUtil.isNull(content)){
			resp.setError("不能回复空的内容");
			return resp;
		}
		if (deal.tryToRevert(tid,content)){
			resp.setSucces();
			List<TransformRevert> trs = new ArrayList<TransformRevert>();
			for (Revert revert : deal.getReverts()){
				TransformRevert tr = new TransformRevert();
				tr.copy(revert);
				trs.add(tr);
			}
			resp.setSucces();
			resp.put("reverts",trs);
			ServerLog.info(user.getAccount() + " revert deal id = " + deal.getId() + " context = " + content);
		}else{
			resp.setError("回复失败");
		}
		return resp;
	}

}
