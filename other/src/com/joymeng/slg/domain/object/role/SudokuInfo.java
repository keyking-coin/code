package com.joymeng.slg.domain.object.role;

public class SudokuInfo {
	int pos;
	String itemId;

	public SudokuInfo() {
	}

	public SudokuInfo(int pos, String itemId) {
		this.pos = pos;
		this.itemId = itemId;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

}
