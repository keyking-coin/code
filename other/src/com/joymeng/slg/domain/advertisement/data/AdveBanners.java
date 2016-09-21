package com.joymeng.slg.domain.advertisement.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.net.mod.AbstractClientModule;

public class AdveBanners {
	// id
	String id;
	// 频率
	int freq = 10;
	// 开始时间
	String start;
	// 结束时间
	String end;
	List<Banner> banners = new ArrayList<Banner>();

	/**
	 * 
	* @Title: isInvalid 
	* @Description: 是否失效
	* 
	* @return boolean
	* @param now
	* @return
	 */
	public boolean isInvalid(long now){
		long startTime = 0;
		long endTime = 0;
		if(!StringUtils.isNull(start)){
			startTime = TimeUtils.getTimes(start);
		}
		if(!StringUtils.isNull(end)){
			endTime = TimeUtils.getTimes(end);
		}
		if(now <= startTime || now >= endTime){
			return true;
		}
		return false;
	}
	/**
	 * 
	* @Title: isNoEffect 
	* @Description: 未生效
	* 
	* @return boolean
	* @param now
	* @return
	 */
	public boolean isNoEffect(long now){
		long startTime = 0;
		if(!StringUtils.isNull(start)){
			startTime = TimeUtils.getTimes(start);
		}
		if(now <= startTime){
			return true;
		}
		return false;
	}
	/**
	 * 
	* @Title: isEffect 
	* @Description: 是否生效
	* 
	* @return void
	 */
	public boolean isEffect(long now){
		long startTime = 0;
		long endTime = 0;
		if(!StringUtils.isNull(start)){
			startTime = TimeUtils.getTimes(start);
		}
		if(!StringUtils.isNull(end)){
			endTime = TimeUtils.getTimes(end);
		}
		if(now >=startTime && now < endTime){
			return true;
		}
		return false;
	}
	
	public void _decode(Element sun) throws Exception {
		id = sun.getAttribute("id");
		String fq = sun.getAttribute("freq");
		start = sun.getAttribute("start");
		end = sun.getAttribute("end");
		if (StringUtils.isNumber(fq))
			freq = Integer.parseInt(fq);

		NodeList nodes = sun.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			try {
				Node node = nodes.item(i);
				if (node.getNodeType() != 1) {
					continue;
				}
				Element al= (Element) node;
				Banner ner = new Banner();
				ner._decode(al);
				banners.add(ner);
			} catch (Exception e) {
				GameLog.error(e);
				continue;
			}

			
		}

	}

	public void _serialize(AbstractClientModule module) {
		module.add(freq);
		module.add(banners.size());
		if (banners.size() > 0)
			Collections.sort(banners);
		for (Banner adv : banners) {
			//module.add(id + adv.getId());
			adv._serialize(module);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

}
