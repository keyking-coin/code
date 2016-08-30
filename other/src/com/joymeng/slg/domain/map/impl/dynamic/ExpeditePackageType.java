package com.joymeng.slg.domain.map.impl.dynamic;



public enum ExpeditePackageType {
	PACKAGE_TYPE_GOODS,
	PACKAGE_TYPE_EQUIP,
	PACKAGE_TYPE_STONE,
	PACKAGE_TYPE_RESOURCE,
	PACKAGE_TYPE_EXP,
	PACKAGE_TYPE_GOLD
	;
	public byte getType() {
		return (byte)ordinal();
	}
	
	public static ExpeditePackageType search(byte code){
		ExpeditePackageType[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			ExpeditePackageType type = datas[i];
			if (type.ordinal() == code){
				return type;
			}
		}
		return null;
	}
}
