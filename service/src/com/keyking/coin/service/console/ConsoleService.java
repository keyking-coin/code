package com.keyking.coin.service.console;

import java.net.ServerSocket;
import java.net.Socket;


public class ConsoleService {
	
	public static ServerSocket server;
	
	public static void addConsole(int port){
		try {
			server = new ServerSocket(port);
			new Thread(){
				@Override
				public void run() {
					try {
						Terminal terminal = new Terminal();
						while (true){
							Socket socket = server.accept();
							if (terminal.logic(socket)){
								socket.shutdownInput();
							}
						}
					} catch (Exception e) {
						//e.printStackTrace();
					}
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
 
 
