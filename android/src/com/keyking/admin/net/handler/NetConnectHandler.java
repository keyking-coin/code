package com.keyking.admin.net.handler;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.alibaba.fastjson.JSON;
import com.keyking.admin.JsonUtil;
import com.keyking.admin.data.DataManager;
import com.keyking.admin.data.deal.Deal;
import com.keyking.admin.net.DataBuffer;
import com.keyking.admin.net.NetUtil;
import com.keyking.admin.net.resp.Module;
import com.keyking.admin.net.resp.ResultCallBack;

public class NetConnectHandler extends IoHandlerAdapter {

	@SuppressWarnings("unchecked")
	@Override
	public void messageReceived(IoSession session, Object message)throws Exception {
		if (message instanceof DataBuffer){
			DataBuffer data = (DataBuffer)message;
			String logicName = data.getUTF();
			if (logicName.equals("Module")){
				String str = data.getUTF();
				Map<String,Object> temp = (Map<String,Object>)JSON.parse(str);
				byte moduleCode = Byte.parseByte(temp.get("code").toString());
				byte opration = Byte.parseByte(temp.get("flag").toString());
				switch(moduleCode){
					case Module.MODULE_CODE_ADMIN_AGENCY:{
						List<Deal> deals = DataManager.getInstance().getDeals();
						Deal deal = JsonUtil.JsonToObject(temp.get("deal").toString(),Deal.class);
						long id = deal.getOrders().get(0).getId();
						if (opration == Module.ADD_FLAG){
							deals.add(deal);
						}else if (opration == Module.DEL_FLAG){
							Iterator<Deal> iter = deals.iterator();
							while(iter.hasNext()){
								Deal d = iter.next();
								if (d.getOrders().get(0).getId() == id){
									iter.remove();
								}
							}
						}
						break;
					}
				}
			}else{
				ResultCallBack callBack = NetUtil.getInstance().getResultCallBack(logicName);
				if (callBack != null){
					int result = data.getInt();
					if (result == 0){
						callBack.succ(logicName,data);
					}else{
						callBack.fail(logicName,data);
					}
				}
			}
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
	}
	
}
