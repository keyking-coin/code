package com.joymeng.slg.domain.turntable;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Lottery implements DataKey {
	String id;
	List<String> dropList1;
	List<String> dropList2;
	List<String> dropList3;
	List<String> dropList4;
	List<String> dropList5;
	List<String> dropList6;
	List<String> dropList7;
	List<String> dropList8;
	List<String> dropList9;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getDropList1() {
		return dropList1;
	}

	public void setDropList1(List<String> dropList1) {
		this.dropList1 = dropList1;
	}

	public List<String> getDropList2() {
		return dropList2;
	}

	public void setDropList2(List<String> dropList2) {
		this.dropList2 = dropList2;
	}

	public List<String> getDropList3() {
		return dropList3;
	}

	public void setDropList3(List<String> dropList3) {
		this.dropList3 = dropList3;
	}

	public List<String> getDropList4() {
		return dropList4;
	}

	public void setDropList4(List<String> dropList4) {
		this.dropList4 = dropList4;
	}

	public List<String> getDropList5() {
		return dropList5;
	}

	public void setDropList5(List<String> dropList5) {
		this.dropList5 = dropList5;
	}

	public List<String> getDropList6() {
		return dropList6;
	}

	public void setDropList6(List<String> dropList6) {
		this.dropList6 = dropList6;
	}

	public List<String> getDropList7() {
		return dropList7;
	}

	public void setDropList7(List<String> dropList7) {
		this.dropList7 = dropList7;
	}

	public List<String> getDropList8() {
		return dropList8;
	}

	public void setDropList8(List<String> dropList8) {
		this.dropList8 = dropList8;
	}

	public List<String> getDropList9() {
		return dropList9;
	}

	public void setDropList9(List<String> dropList9) {
		this.dropList9 = dropList9;
	}

	@Override
	public Object key() {
		return id;
	}

}
