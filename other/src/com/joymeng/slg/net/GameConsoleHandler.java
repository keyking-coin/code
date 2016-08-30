package com.joymeng.slg.net;

import com.joymeng.Instances;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.console.ConsoleService;

/**
 * @author Dream
 * 
 */
public class GameConsoleHandler implements ConsoleService, Instances {

	private static ConsoleService instance = new GameConsoleHandler();

	@Override
	public String handle(String msg) {
		try {
			char[] chars = msg.toCharArray();
			StringBuffer buf = new StringBuffer(msg.length());
			int arrLen = chars.length;
			char c;
			for (int i = 0; i < arrLen; i++) {
				c = chars[i];
				if (c == '') { // 去除光标的按键
					i++;
					i++;
					continue;
				}
				if (c != 8) {
					buf.append(c);
				}else {//Backspace键，则删除前一个字符
					int idx = buf.length() - 1;
					if (idx >= 0){
						buf.deleteCharAt(idx);
					}	
				}
			}
			String[] cmds = buf.toString().split(" ");
			String out = null;
			switch(cmds[0]){
			    case "scan":{
			    	serverManager.scan();
		    		out = "try to save";
		    		break;
			    }
				default: {
					out = "invalid cmd";
				}
			}
			return out;
		} catch (Throwable e) {
			GameLog.error(e);
			return e.toString();
		}
	}

	public static ConsoleService getInstance() {
		return instance;
	}
}
