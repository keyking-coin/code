package com.joymeng.slg.domain.actvt;

public enum ActvtRewardType
{
	MONEY("money"), ITEM("item"), EQUIP("equip"), MATERIAL("material"), UNION_SCORE("unionScore");

	private String name;
	
	public String getName() {
		return name;
	}
	
	private ActvtRewardType(String name) {
		this.name = name;
	}
	
	public boolean equals(String type) {
		return name.equals(type);
	}
	
	public static ActvtRewardType fromString(String name) {
	    if (name != null) {
	      for (ActvtRewardType b : ActvtRewardType.values()) {
	        if (name.equalsIgnoreCase(b.name)) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }
}
