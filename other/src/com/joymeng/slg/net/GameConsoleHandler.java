package com.joymeng.slg.net;

import com.joymeng.Instances;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.console.ConsoleService;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.union.UnionBody;

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
			    case "stop":{
			    	world.gameServerShutDown();
			    	System.exit(0);
			    	out = "save ok!";
			    }
			    case "load_union_buff":{
			    	long uid =Long.parseLong(cmds[1]);
			    	Role role = world.getOnlineRole(uid);
			    	if(role!= null && role.getUnionId() > 0){
			    		UnionBody ub = unionManager.search(role.getUnionId());
			    		if(ub != null){
			    			ub.removeMemberAllUnionCityBuff(role);
			    			ub.updateUnionCityBuff(role,true);
			    		}
			    			
			    	}
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
