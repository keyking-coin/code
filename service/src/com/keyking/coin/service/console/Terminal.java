package com.keyking.coin.service.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.keyking.coin.service.domain.Controler;
import com.keyking.coin.service.http.HttpServer;

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
					writer.close();
					reader.close();
					return true;
				case "close":
					HttpServer.getInstance().stop();
					System.exit(0);
					break;
				case "save":
					writer.println("save ok");
					writer.flush();
					break;
				case "insert":
					if (cmds.length < 3){
						writer.println("please input tel number and nikeName");
						writer.flush();
						return false;
					}
					String result = Controler.getInstance().insertUser(cmds[1],cmds[2]);
					writer.println(result);
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
 
 
