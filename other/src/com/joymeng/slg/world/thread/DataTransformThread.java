package com.joymeng.slg.world.thread;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.GameLog;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.net.DataModule;
import com.joymeng.slg.net.mod.RespModuleSet;

public class DataTransformThread extends Thread {
	
	ConcurrentLinkedQueue<DataModule> modules = new ConcurrentLinkedQueue<DataModule>();
	
	public void addModule(DataModule module) {
		if (module == null){
			return;
		}
		if (!modules.contains(module)) {
			modules.add(module);
		}
	}
	
	@Override
	public void run() {
		while (!ServiceApp.FREEZE) {
			try {
				long delay = 100;
				if (modules.size() > 0){
					delay = 0;
				}
				if (delay > 0){
					Thread.sleep(delay);
				}
				DataModule module = modules.poll();
				if (module == null) {
					continue;
				} else {
					RespModuleSet rms = new RespModuleSet();
					rms.addModule(module);
					rms.setSplitPackage(true);
					MessageSendUtil.sendModule(rms, module.getUserInfo());
				}
			} catch (Exception e) {
				e.printStackTrace();
				GameLog.error(e);
			}	
		}
	}
}
