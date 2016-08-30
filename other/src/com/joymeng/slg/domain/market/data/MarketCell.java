package com.joymeng.slg.domain.market.data;

import java.util.List;
import com.joymeng.Instances;
import com.joymeng.common.util.MathUtils;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.net.SerializeEntity;

public class MarketCell implements SerializeEntity,Instances{
	protected String id;//固化表编号
	protected String itemId;//道具编号
	protected String costKey;//消耗资源的类型
	protected int costNum;//消耗资源的数量
	protected int num;//可购买数量
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getCostKey() {
		return costKey;
	}

	public void setCostKey(String costKey) {
		this.costKey = costKey;
	}

	public int getCostNum() {
		return costNum;
	}

	public void setCostNum(int costNum) {
		this.costNum = costNum;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	public void init(Blackshop bs) {
		id     = bs.getId();
		itemId = bs.getItemid();
		List<String> cs = bs.getCost();
		CostData[] cds = new CostData[cs.size()];
		int[] rates  = new int[cs.size()];
		for (int i = 0 ; i < cs.size(); i++ ){
			String c = cs.get(i);
			String[] css = c.split(":");
			cds[i]   = new CostData(css[0],css[1]);
			rates[i] = Integer.parseInt(css[2]);
		}
		CostData result = MathUtils.getRandomObj(cds,rates);
		costKey = result.key;
		costNum = result.num;
		num     = 1;
	}
	
	@Override
	public void serialize(JoyBuffer out) {
		out.putPrefixedString(id,JoyBuffer.STRING_TYPE_SHORT);
		out.putPrefixedString(itemId,JoyBuffer.STRING_TYPE_SHORT);
		out.putPrefixedString(costKey,JoyBuffer.STRING_TYPE_SHORT);
		out.putInt(costNum);
		out.putInt(num);
	}
	
	public Blackshop shopData(){
		if (id == null){
			return null;
		}
		return dataManager.serach(Blackshop.class,id);
	}
	
	class CostData{
		String key;
		int num;
		public CostData(String key, String num){
			this.key = key;
			this.num = Integer.parseInt(num);
		}
	}
}
