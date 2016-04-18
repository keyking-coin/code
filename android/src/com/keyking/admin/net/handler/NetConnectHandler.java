package com.keyking.admin.net.handler;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.keyking.admin.JsonUtil;
import com.keyking.admin.data.DataManager;
import com.keyking.admin.data.deal.Deal;
import com.keyking.admin.net.DataBuffer;
import com.keyking.admin.net.NetUtil;
import com.keyking.admin.net.request.NetLogicName;
import com.keyking.admin.net.resp.Module;
import com.keyking.admin.net.resp.ResultCallBack;

public class NetConnectHandler extends IoHandlerAdapter {

	@SuppressWarnings("unchecked")
	@Override
	public void messageReceived(IoSession session, Object message)throws Exception {
		if (message instanceof DataBuffer){
			DataBuffer data = (DataBuffer)message;
			String logicName = data.getUTF();
			if (logicName.equals(NetLogicName.system_module.getKey())){
				data.getInt();
				String str = data.getUTF();
				List<Object> temps = (List<Object>)JSON.parse(str);
				for (Object obj : temps){
					Map<String,Object> temp = (Map<String,Object>)obj;
					byte moduleCode = Byte.parseByte(temp.get("code").toString());
					byte opration = Byte.parseByte(temp.get("flag").toString());
					switch(moduleCode){
						case Module.MODULE_CODE_ADMIN_AGENCY:{
							List<Deal> deals = DataManager.getInstance().getDeals();
							Map<String,Object> subMap = (Map<String,Object>)temp.get("datas");
							String subStr = subMap.get("deal").toString();
							Deal deal = JsonUtil.JsonToObject(subStr,Deal.class);
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
							Message msg = new Message();
							msg.what = 2;
							NetUtil.getInstance().getActivity().getHandler().handleMessage(msg);
							break;
						}
					}
				}
			}else if (logicName.equals(NetLogicName.system_connect.getKey())){
				int result = data.getInt();
				NetUtil.getInstance().setConnected(result == 0);
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
		session.close(true);
		NetUtil.getInstance().setConnected(false);
	}

	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
	}
}
