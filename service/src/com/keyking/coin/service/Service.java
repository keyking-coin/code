package com.keyking.coin.service;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.keyking.coin.service.console.ConsoleService;
import com.keyking.coin.service.net.codec.MessageCodecFactory;
import com.keyking.coin.util.Instances;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.XmlUtils;

public class Service implements Instances{
	
	public static String URL;
	
	private static int PORT;
	
	private static int CONSOLE;
	
	private static MainLoop looper = null;
	
	public static void main(String[] args) {
        try {
        	ServerLog.init();
        	load();
        	ServerLog.info("DB init");
        	DB.init();
        	PK.load();
        	ServerLog.info("SMS init");
        	SMS.init();
        	SocketAcceptor acceptor = new NioSocketAcceptor();  
            SocketSessionConfig config = acceptor.getSessionConfig();  
            config.setReadBufferSize(1024*1024*100);//100M
            config.setIdleTime(IdleStatus.BOTH_IDLE,10);
            DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
            chain.addLast("codec",new ProtocolCodecFilter(new MessageCodecFactory()));
            acceptor.setHandler(NET);
            InetAddress address = InetAddress.getByName(URL);
			acceptor.bind(new InetSocketAddress(address,PORT));
			ConsoleService.addConsole(CONSOLE);
        	looper = new MainLoop();
        	looper.start();
		} catch (Exception e) {
			e.printStackTrace();
			ServerLog.error("start fail besause of " + e.getMessage());
			System.exit(0);
		}
	}
	
	public static void load() throws Exception{
		File file = new File("conf/service.xml");
		Document document = XmlUtils.load(file);
		Element element   = document.getDocumentElement();
		URL = XmlUtils.getAttribute(element,"url");
		Element service = XmlUtils.getChildByName(element,"service");
		if (service != null){
			PORT    = Integer.parseInt(XmlUtils.getAttribute(service,"port"));
			CONSOLE = Integer.parseInt(XmlUtils.getAttribute(service,"console"));
		}
	}
	
	public static void stop(){
		looper.isRunning = false;
		ServerLog.info("service start closing");
		new Thread(){
			@Override
			public void run() {
				CTRL.save();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ServerLog.info("service closed");
				System.exit(0);
			}
		}.start();
		
	}
}
 
 
 
