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
import com.keyking.coin.service.thread.UserThread;
import com.keyking.coin.util.Instances;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.XmlUtils;

public class Service implements Instances{
	
	public static String URL;
	
	private static int PORT;
	
	private static int HTTP_PORT;
	
	private static int CONSOLE;
	
	public static int IUSSUE_TIME = 6;
	
	public static void main(String[] args) {
        try {
        	ServerLog.init();
        	load();
        	DB.init();
        	PK.load();
        	SMS.init();
        	CTRL.load();
        	SocketAcceptor acceptor = new NioSocketAcceptor();  
            SocketSessionConfig config = acceptor.getSessionConfig();  
            config.setReadBufferSize(1024*1024*100);//100M
            config.setIdleTime(IdleStatus.BOTH_IDLE,10);
            DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
            chain.addLast("codec",new ProtocolCodecFilter(new MessageCodecFactory()));
            acceptor.setHandler(NET);
            InetAddress address = InetAddress.getByName(URL);
			acceptor.bind(new InetSocketAddress(address,PORT));
			HTTP.run(HTTP_PORT);
			PUSH.init();
			new UserThread().start();
			ConsoleService.addConsole(CONSOLE);
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
		IUSSUE_TIME = Integer.parseInt(XmlUtils.getAttribute(element,"issue"));
		Element service = XmlUtils.getChildByName(element,"service");
		if (service != null){
			HTTP_PORT = Integer.parseInt(XmlUtils.getAttribute(service,"http"));
			PORT      = Integer.parseInt(XmlUtils.getAttribute(service,"port"));
			CONSOLE   = Integer.parseInt(XmlUtils.getAttribute(service,"console"));
		}
	}
}
 
 
 
