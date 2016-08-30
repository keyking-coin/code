package com.joymeng.list;

public class TroopState {

	public static String getTroopStr(String state) {
		String strState = null;

		switch (state) {
		case "MAPCOLLECT":
			strState = "在大地图采集";
			break;
		case "GOTOGARRISON":
			strState = "去大地图驻防";
			break;
		case "MAPGARRISON":
			strState = "在大地图驻防";
			break;
		case "GOTOSTATION":
			strState = "去大地图驻扎";
			break;
		case "MAPSTATION":
			strState = "在大地图驻扎";
			break;
		case "BACKCITY":
			strState = "部队返回主城";
			break;
		case "BACKFORTRESS":
			strState = "部队返回要塞";
			break;
		case "CREATEMOVE":
			strState = "去空地建造迁城点";
			break;
		case "CREATEFORTRESS":
			strState = "去空地建造要塞";
			break;
		case "GOTOFIGHT":
			strState = "去战斗";
			break;
		case "GOTOSPY":
			strState = "去侦查";
			break;
		case "MAP_MASS":
			strState = "在大地图集结";
			break;
		case "MAP_MASS_END":
			strState = "集结结束";
			break;
		case "GO_TO_MASS":
			strState = "去集结";
			break;
		default:
			strState = "遗漏部队状态";
			break;
		}
		return strState;

	}
}
