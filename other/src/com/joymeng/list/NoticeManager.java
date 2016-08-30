package com.joymeng.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.http.HtppOprateType;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.JoyServiceApp;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;
import com.joymeng.slg.world.GameConfig;
import com.joymeng.slg.world.thread.NoticeThread;

public class NoticeManager implements Instances {
	private static NoticeManager instance = new NoticeManager();

	public static NoticeManager getInstance() {
		return instance;
	}
	long tipTime = 0;
	Map<Integer, NoticeInfo> timeNotice = new HashMap<Integer, NoticeInfo>(); //定时公告

	Map<Integer, NoticeInfo> rollNotice = new HashMap<Integer, NoticeInfo>(); //滚屏公告
	
	NoticeThread noticeThread = new NoticeThread();

	public void start() {
		noticeThread.start();
	}

	public void tick(long now) {
		synchronized (timeNotice) {
			for (Integer inter : timeNotice.keySet()) {
				NoticeInfo notice = timeNotice.get(inter);
				if (notice != null) {
					int serverId = notice.getServerId();
					String noticeContent = notice.getNoticeContent();
					String startTime = notice.getStartTime();
					if (now >= TimeUtils.getTimes(startTime)) {
						tryToTail(serverId, noticeContent);
						timeNotice.remove(inter);
					}
				}
			}
		}
		synchronized (rollNotice) {
			for (Integer inter : rollNotice.keySet()) {
				NoticeInfo notice = rollNotice.get(inter);
				if (notice != null) {
					int serverId = notice.getServerId();
					String noticeContent = notice.getNoticeContent();
					String startTime = notice.getStartTime();
					String endTime = notice.getEndTime();
					int timeDelay = notice.getTimeDelay();
					long start = TimeUtils.getTimes(startTime);
					if (StringUtils.isNull(endTime)) {
						if (now >= start) {
							long time = TimeUtils.nowLong() - tipTime;
							if (time >= timeDelay * Const.SECOND) {
								tryToTail(serverId, noticeContent);
								tipTime = TimeUtils.nowLong();
							}
						}
					} else {
						long end = TimeUtils.getTimes(endTime);
						if (now >= start && now <= end) {
							long time = TimeUtils.nowLong() - tipTime;
							if (time >= timeDelay * Const.SECOND) {
								tryToTail(serverId, noticeContent);
								tipTime = TimeUtils.nowLong();
							}
						}
					}
				}
			}
		}

	}

	public List<Integer> getSeverList(String server) {
		List<Integer> serverList = new ArrayList<Integer>();
		if (server.equals("all")) {
			serverList = serverManager.ServersWorking();
		} else {
			String[] ser = server.split(",");
			for (int i = 0; i < ser.length; i++) {
				serverList.add(Integer.valueOf(ser[i]));
			}
		}
		return serverList;
	}
	
	public void rollNotice(int serverId, NoticeInfo noticeInfo) {		
		rollNotice.put(serverId, noticeInfo);
	}

	public void timeNotice(int serverId, NoticeInfo noticeInfo) {
		timeNotice.put(serverId, noticeInfo);
	}

	public void tryToTail(int server, String noticeContent) {
		int serverId = server + GameConfig.SERVER_LIST_ID;
		List<Integer> serverList = serverManager.ServersWorking();
		if (serverList.contains(serverId)) {
			GameLog.info("tell server issued a notice");
			int protocolId = 0x0000009D;
			ServiceHandler handler = ServiceHandler.REQUEST_HANDLERS.get(protocolId);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(GameConfig.SYSTEM_TRANFOEM_ID);
			targetInfo.setCid(serverId);
			TransmissionResp resp = new TransmissionResp();
			resp.setUserInfo(targetInfo);
			resp.getParams().put(protocolId); // 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_REQUEST.ordinal());//
			resp.getParams().put(ServiceApp.instanceId);// 从哪里来的
			resp.getParams().put(GameConfig.SYSTEM_TRANFOEM_ID);
			resp.getParams().put(serverId);
			resp.getParams().put(noticeContent);
			JoyServiceApp.getInstance().sendMessage(resp);
			RefreshList rl = new RefreshList(serverId);
			handler.addNextDo(GameConfig.SYSTEM_TRANFOEM_ID, rl);
		}

	}
	
	class RefreshList implements NeedContinueDoSomthing {
		int serviceId = 0;

		public RefreshList(int serviceId) {
			this.serviceId = serviceId;
		}

		@Override
		public int getId() {
			return serviceId;
		}

		@Override
		public JoyProtocol succeed(UserInfo info, ParametersEntity params) {
			synchronized (this) {
				int comeFrom = params.get(2);
				String data = params.get(3);
				if (!StringUtils.isNull(data)) {
					GameLog.info("tell server " + comeFrom + " issued a notice succeed");
				}
				return null;
			}
		}
		
		@Override
		public JoyProtocol fail(UserInfo info, ParametersEntity params) {
			return null;
		}

	}	
	
}
