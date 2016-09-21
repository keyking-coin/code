package com.joymeng.common.util;


public class RateComparator implements Comparable<RateComparator>{
	public Object value;
	public int rate;
	
	public RateComparator(Object value ,int rate){
		this.value = value;
		this.rate  = rate;
	}
	
	@Override
	public int compareTo(RateComparator o) {
		return rate - o.rate;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T object(){
		if (value != null){
			return (T) value;
		}
		return null;
	}
}
