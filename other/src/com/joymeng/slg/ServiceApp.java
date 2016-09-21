package com.joymeng.slg;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.joymeng.common.util.HttpClientUtil;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.gm2.GMServer;
import com.joymeng.http.HttpServer;
import com.joymeng.http.dao.WebDB;
import com.joymeng.list.ServerStatus;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.JoyServiceApp;
import com.joymeng.services.core.console.ConsoleService;
import com.joymeng.services.core.context.RemoteService;
import com.joymeng.services.core.plugin.Plugin;
import com.joymeng.services.utils.XmlUtils;
import com.joymeng.slg.net.AndroidChargeNotifier;
import com.joymeng.slg.net.GameConsoleHandler;
import com.joymeng.slg.world.GameConfig;
import com.joymeng.slg.world.World;

/**
 * 示例程序
 * 
 * @author ShaoLong Wang, Dream
 */
public class ServiceApp {
	
	public static boolean FREEZE = true;
	public static JoyServiceApp SERVICE_APP;
	public static int moduleId = 0;
	public static int instanceId = 0;
	public static String service_account;
	public static String service_pwd;
	public static long service_uid;
	public static void main(String[] args) {
		try {
			_load();
			GameConfig.load(); 
			HttpServer.getInstance().run(20000 + instanceId);
			if (GameConfig.USE_DATA_EDIT_DB){
				WebDB.getInstance().init();
			}
			World.getInstance().init();
			GameLog.info(">>>>>>>> START GAME WORLD     <<<<<<<<");
			//获取框架实例
			final JoyServiceApp app = JoyServiceApp.getInstance();
			SERVICE_APP = app;
			//注册所需的回调函数
			app.registPlugin(new Plugin() {
				@Override
				public void onAppLogin(RemoteService service) throws Exception {
					GameLog.info(">>>>>>>>      GAME LOGIN SUCCESSFUL      <<<<<<<<<");
					World.getInstance().notifyList(ServerStatus.SERVER_STATUS_NORMAL);
				}
				@Override
				public void onAppStart() throws Exception {
					GameLog.info("demo app start...");
					app.registChargeHandler(AndroidChargeNotifier.getInstance());
					ConsoleService consoleService = GameConsoleHandler.getInstance();
					// 添加控制台处理程序
					app.registConsole(instanceId + 10000,consoleService);
					GameLog.info(">>>>>>>>      CONCOLE    START SUCCESSFUL      <<<<<<<<<");
					GameLog.info(">>>>>>>>      CONCOLE  PORT:   " + (10000+instanceId) + "<<<<<<<<");
					GameLog.info(">>>>>>>>      GAME WORLD START SUCCESSFUL      <<<<<<<<<");
				}

				@Override
				public void onAppStop() throws Exception {
					World.getInstance().gameServerShutDown();
					GameLog.info("demo app stop...");
				}
			});
			//启动服务
			app.start();
			//启动GM服务 taoxing
			GMServer.getInstance().start(5555);
		} catch (Exception e) {
			e.printStackTrace();
			GameLog.error(e);
			System.exit(0);
		}
	}
	
	private static void _load() throws Exception{
		String fileName = "./conf/RemoteServices.xml";
		Document document = XmlUtils.load(fileName);
		Element base = document.getDocumentElement();
		instanceId = Integer.parseInt(XmlUtils.getAttribute(base, "instanceID").toLowerCase().replaceFirst("0x", ""),16);
		moduleId = Integer.parseInt(XmlUtils.getAttribute(base, "moduleID").toLowerCase().replaceFirst("0x", ""),16);
		Element serverAccount = XmlUtils.getChildByName(base,"ServerAccount");
		if (serverAccount == null){//先自动注册自己
			serverAccount = XmlUtils.createChild(document,base,"ServerAccount");
			String account = "redalert_" + instanceId;
			String pwd     = "admin_" + instanceId;
			int count = 0;
			do {
				String url     = "http://netuser.joymeng.com/user/reg?nname=redalert&uname=" + account + "&password=" + pwd;
				HttpResponse resp = HttpClientUtil.getHttpResponse(new HttpPost(url));
				byte[] datas = HttpClientUtil.readFromStream(resp.getEntity().getContent());
				String str = new String(datas,"UTF-8");
				ServerAutoRegist result = JsonUtil.JsonToObject(str,ServerAutoRegist.class);
				if (result.status == 1){
					serverAccount.setAttribute("account",account);
					serverAccount.setAttribute("pwd",pwd);
					serverAccount.setAttribute("uid",result.getContent().getUid() + "");
					XmlUtils.save(fileName,document);
					service_uid = result.getContent().getUid();
					service_account = account;
					service_pwd = pwd;
					break;
				}else{
					account = "redalert_" + count + "_" + instanceId;
					count++;
				}
			}while(true);
		}else{
			service_account = XmlUtils.getAttribute(serverAccount,"account");
			service_pwd     = XmlUtils.getAttribute(serverAccount,"pwd");
			service_uid     = Integer.parseInt(XmlUtils.getAttribute(serverAccount,"uid"));
		}
	}
	static class ServerAutoRegist{
		byte status;
		String msg;
		RegistResult content;
		public byte getStatus() {
			return status;
		}
		public void setStatus(byte status) {
			this.status = status;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public RegistResult getContent() {
			return content;
		}
		public void setContent(RegistResult content) {
			this.content = content;
		}
	}
	
	static class RegistResult{
		long uid;
		String uname;
		String nname;
		String password;
		String reg_date;
		public long getUid() {
			return uid;
		}
		public void setUid(long uid) {
			this.uid = uid;
		}
		public String getUname() {
			return uname;
		}
		public void setUname(String uname) {
			this.uname = uname;
		}
		public String getNname() {
			return nname;
		}
		public void setNname(String nname) {
			this.nname = nname;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getReg_date() {
			return reg_date;
		}
		public void setReg_date(String reg_date) {
			this.reg_date = reg_date;
		}
	}
}
