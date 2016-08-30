package com.joymeng.slg.net.mod;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyResponse;

/**
 * 客户端模块集合的通用response
 * 
 * 
 */
public class RespModuleSet extends JoyResponse {
	private static final long serialVersionUID = -2818349231873263902L;

	List<ClientModule> moduleList = new ArrayList<ClientModule>();
	
	public RespModuleSet() {
		super(1111);
	}

	public void clearAll() {
		moduleList.clear();
	}

	public RespModuleSet addModule(ClientModule mod) {
		moduleList.add(mod);
		return this;
	}

	public RespModuleSet addModules(Collection<? extends ClientModule> mods) {
		moduleList.addAll(mods);
		return this;
	}

	public List<ClientModule> getModuleList() {
		return moduleList;
	}
	
	@Override
	public void _serialize(JoyBuffer out) {
		try {
			JoyBuffer temp = JoyBuffer.allocate(1024);
			temp.put((byte) moduleList.size());
			for (int i = 0 ; i < moduleList.size() ; i++){
				ClientModule resp = moduleList.get(i);
				temp.putShort(resp.getModuleType());
				resp.serialize(temp);
			}
			MessageSendUtil.checkAndZip(out,temp.arrayToPosition());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void _deserialize(JoyBuffer arg0) {

	}
}
