package com.joymeng.slg.domain.object.bag;

public class BriefItem {
	String itemType = "";
	String itemId = "";
	int num = 0;

	public BriefItem() {
	}

	public BriefItem(String itemType, String itemId, int num) {
		this.itemType = itemType;
		this.itemId = itemId;
		this.num = num;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

}
