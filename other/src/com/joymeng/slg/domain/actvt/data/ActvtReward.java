package com.joymeng.slg.domain.actvt.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.Yotils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.actvt.ActvtRewardType;
import com.joymeng.slg.domain.data.DataManager;
import com.joymeng.slg.domain.object.bag.data.Item;

public class ActvtReward 
{
	public static final String ITEMS_SPLIT = ",";
	public static final String IDNUM_SPLIT = "#";
	
	String id;
	String tag;
	String items;
	List<String[]> itemStrs = new ArrayList<String[]>();;
	Map<String,Integer> rewardMap = new HashMap<String,Integer>();
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getItems() {
		return items;
	}
	
	public void setItems(String items) {
		this.items = items;
	}
	
	public String getItemId(int index) {
		return itemStrs.get(index)[1];
	}
	
	public int getItemNum(int index) {
		String itemId = getItemId(index);
		if (rewardMap.containsKey(itemId)) {
			return rewardMap.get(itemId);
		}
		GameLog.error("actvt get reward num error, id="+id+" items="+items);
		return 1;
	}
	
	public Map<String, Integer> getRewardMap() {
		return rewardMap;
	}

	public boolean checkValide() {
		String[] itStrs = items.split(ITEMS_SPLIT);
		for (int i = 0; i < itStrs.length; i++)
		{
			String str = itStrs[i];
			if (str.isEmpty()) {
				return false;
			}
			String[] strs = str.split(IDNUM_SPLIT);
			if (strs.length < 3) {
				return false;
			}
			if (!ActvtRewardType.ITEM.getName().equals(strs[0])) {
				return false;
			}
			String itemId = strs[1];
			Item it = DataManager.getInstance().serach(Item.class, itemId);
			if (it == null) {
				return false;
			}
			
			if (!Yotils.isNumeric(strs[2])) {
				return false;
			}
			int num = Integer.parseInt(strs[2]);
			if (num <= 0) {
				return false;
			}
			rewardMap.put(itemId, num);
			itemStrs.add(strs);
		}
		return true;
	}
}
