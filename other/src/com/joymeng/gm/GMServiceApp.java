package com.joymeng.gm;

import org.w3c.dom.Element;

import com.joymeng.http.HttpServer;
import com.joymeng.list.Announcement;
import com.joymeng.list.LoginJudge;
import com.joymeng.list.NoticeManager;
import com.joymeng.list.ServerManager;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.JoyServiceApp;
import com.joymeng.services.core.context.RemoteService;
import com.joymeng.services.core.plugin.Plugin;
import com.joymeng.services.utils.XmlUtils;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.world.TaskPool;

public class GMServiceApp {
	public static JoyServiceApp SERVICE_APP;
	
	public static void main(String[] args) {
		try {
			Element element = XmlUtils.load("./conf/RemoteServices.xml").getDocumentElement();
			ServiceApp.instanceId = Integer.parseInt(XmlUtils.getAttribute(element,"instanceID").toLowerCase().replaceFirst("0x",""),16);
			ServiceApp.moduleId = Integer.parseInt(XmlUtils.getAttribute(element,"moduleID").toLowerCase().replaceFirst("0x",""),16);
			HttpServer.getInstance().run(12121);
			ServiceHandler.registerHandlers();
			ServerManager.getInstance().load();
			Announcement.getInstance().load();
			ServerManager.getInstance().loadRedis();
			LoginJudge.getInstance().load();
			SERVICE_APP = JoyServiceApp.getInstance();
			//注册所需的回调函数
			SERVICE_APP.registPlugin(new Plugin() {
				@Override
				public void onAppLogin(RemoteService service) throws Exception {
					GameLog.info(">>>>>>>>      GAME LOGIN SUCCESSFUL      <<<<<<<<<");
				}

				@Override
				public void onAppStart() throws Exception {
					GameLog.info("service start successful");
				}

				@Override
				public void onAppStop() throws Exception {
					TaskPool.getInstance().stop();
					HttpServer.getInstance().stop();
					GameLog.info("demo app stop...");
				}
			});
			SERVICE_APP.start();//启动服务
			ServiceApp.FREEZE = false;
			NoticeManager.getInstance().start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
