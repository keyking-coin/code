package com.keyking.um.console;

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
						while (true){
							Socket socket = server.accept();
							Terminal terminal = new Terminal();
							terminal.logic(socket);
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
 
 
