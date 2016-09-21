package com.joymeng.gm2;

import com.joymeng.gm2.net.NetManager;
import com.joymeng.gm2.net.message.GMResponse;

/**
 * GM后台服务器
 * 
 * @author ShaoLong Wang
 *
 */
public class GMServer {

	private static GMServer instance ;

	public static GMServer getInstance() {
		if(instance == null) {
			instance = new GMServer();
		}
		return instance;
	}

	private NetManager netMgr;;

	private int port;

	public NetManager getNetMgr() {
		return netMgr;
	}



	public void setNetMgr(NetManager netMgr) {
		this.netMgr = netMgr;
	}



	private GMServer() {
		this.netMgr = new NetManager(this);
	}

	
	
	public boolean boardcast(GMResponse message)
	{
		netMgr.boardcast(message);
		return true;
	}
	
	

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void start(int port) {
		this.port = port;
		try {
			this.netMgr.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		//new GMServer().start(5555);
	}
}
