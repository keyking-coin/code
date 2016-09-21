package com.joymeng.list;

public enum ItemType {
	fouresources((byte) 1, "四种资源"),
	speedprop((byte) 2, "加速道具"), 
	buffprop((byte) 3, "Buff道具"), 
	chest((byte) 4, "宝箱"),
	movecity((byte) 5, "迁城"), 
	viptime((byte) 6, "VIP时间卡"), 
	vipexp((byte) 7, "VIP经验卡"), 
	functionprop((byte) 8, "功能道具"), 
	equipmater((byte) 9, "装备材料"), 
	gold((byte) 10, "金币"), 
	gem((byte) 11, "金筹码"), 
	copper((byte) 12, "银筹码"), 
	sliver((byte) 13, "银币"), 
	krypton((byte) 14, "氪晶"), 
	roleexp((byte) 15, "指挥官经验卡"), 
	stamina ((byte) 16, "体力药剂"), 
	drawing((byte) 17, "装备图纸"), 
    turntable((byte) 18, "转盘虚拟宝箱"), 
	rate((byte) 99, "九宫格倍率"),
	;
	private byte value;
	private String name;

	ItemType(byte value, String name) {
		this.value = value;
		this.name = name;
	}

	public byte getValue() {
		return value;
	}

	public void setValue(byte value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
