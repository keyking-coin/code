package com.keyking.coin.service.net.resp.module;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.RespEntity;
import com.keyking.coin.util.JsonUtil;

public class ModuleResp extends RespEntity {
	
	List<Module> modules = new ArrayList<Module>();
	
	public ModuleResp() {
		super("Module");
		setSucces();
	}

	@Override
	public void _serialize_ok(DataBuffer buffer) {
		String str = JsonUtil.ObjectToJsonString(modules);
		buffer.putUTF(str);
		//buffer.putInt(modules.size());
		//for (Module module : modules){
			
		//}
	}
	
	public void addModule(Module module) {
		if (module == null){
			return;
		}
		if (!modules.contains(module)){
			modules.add(module);
		}
	}
	
	public void addModules(List<Module> modules) {
		for (Module module : modules){
			addModule(module);
		}
	}
	
	public boolean send(){
		return modules.size() > 0;
	}
}
 
