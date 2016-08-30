package com.joymeng.list;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.joymeng.common.util.StringUtils;
import com.joymeng.services.utils.XmlUtils;

public class ChannelFilter {
	String id;
	String name;
	String must;
	List<Channel> channels = new ArrayList<Channel>();
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<Channel> getChannels() {
		return channels;
	}

	public void decode(Element filter) {
		id   = filter.getAttribute("id");
		name = filter.getAttribute("name");
		must = filter.getAttribute("must");
		Element[] ces = XmlUtils.getChildrenByName(filter,"ChannelID");
		for (int i = 0 ; i < ces.length ; i++){
			Element ce = ces[i];
			Channel channel = new Channel();
			channel.decode(ce);
			channels.add(channel);
		}
	}

	public boolean check(String str,String channelId) {
		if (!id.equals(str)){
			return false;
		}
		for (int i = 0 ; i < channels.size() ; i++){
			Channel channel = channels.get(i);
			if (channel.id.equals(channelId)){
				return true;
			}
		}
		return false;
	}
	
	public ServerStatus checkMustShow(){
		if (!StringUtils.isNull(must)){
			return ServerStatus.valueof(Byte.parseByte(must));
		}
		return null;
	}
}
