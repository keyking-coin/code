package com.keyking.coin.service.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import com.keyking.coin.service.Service;
import com.keyking.coin.util.ServerLog;

public class Terminal {
	public boolean logic(Socket socket){
		try {
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer.println("**************************************");
			writer.println("welcome to console");
			writer.println("**************************************");
			writer.println("please input command:");
			writer.flush();
			String cmd = null;
			while ((cmd = reader.readLine()) != null){
				StringBuffer buf = new StringBuffer(cmd.length());
				char[] chars = cmd.toCharArray();
				int arrLen = chars.length;
				char c;
				for (int i = 0; i < arrLen; i++) {
					c = chars[i];
					if (c == '') {
						i++;
						i++;
						continue;
					}
					if (c != 8) {
						buf.append(c);
					} else {
						int idx = buf.length() - 1;
						if (idx >= 0) {
							buf.deleteCharAt(idx);
						}
					}
				}
				cmd = buf.toString();
				String cmds[] = cmd.split(" ");
				switch(cmds[0]){
				case "exit":
					writer.println("exit console system");
					writer.flush();
					return true;
				case "close":
					if (cmds.length < 1){
						writer.println("please input close time");
						writer.flush();
						return false;
					}
					long time = Long.parseLong(cmds[1]);
					ServerLog.info("server will be closed in " + time + " second");
					Timer timer = new Timer();
			        timer.schedule(new TimerTask(){
			            public void run() {
			            	Service.stop();
			            }
			        },time*1000);
			        writer.println("server will be closed in " + time + " second");
			        writer.flush();
					break;
				default:
					writer.println("invalid cmd");
					writer.flush();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
 
 
