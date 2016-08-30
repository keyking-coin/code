package com.joymeng.list;

public class EquipPosRarQue {

	public static String getEquipPosition(int type) {
		String position = null;
		switch (type) {
		case 1:
			position = "武器";
			break;
		case 2:
			position = "服装";
			break;
		case 3:
			position = "头部";
			break;
		case 4:
			position = "手套";
			break;
		case 5:
			position = "鞋子";
			break;
		case 6:
			position = "面部";
			break;
		case 7:
			position = "饰品";
			break;
		case 8:
			position = "勋章";
			break;
		default:
			break;
		}
		return position;
	}
	
	
	public static String getEquipRarity(int type) {
		String rarity = null;
		switch (type) {
		case 1:
			rarity = "1星";
			break;
		case 2:
			rarity = "2星";
			break;
		case 3:
			rarity = "3星";
			break;
		case 4:
			rarity = "4星";
			break;
		case 5:
			rarity = "5星";
			break;
		default:
			break;
		}
		return rarity;
	}
	
	public static String getEquipQuality(int type){
		String  quality = null;//白绿蓝紫金5级
		switch (type) {
		case 1:
			quality ="白";
			break;
		case 2:
			quality ="绿";
			break;
		case 3:
			quality ="蓝";
			break;
		case 4:
			quality ="紫";
			break;
		case 5:
			quality ="金";
			break;
		default:
			break;
		}
		return quality;
	}
	
	
	public static String getEquipState(byte type){
		String state  = null;
		switch (type) {
		case 0:
			state = "空闲中";
			break;
		case 1:
			state = "装备中";
			break;
		case 2:
			state = "升级中";
			break;
		default:
			break;
		}
		return state;
	}

}
