package com.joymeng.slg.domain.map.fight.obj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.joymeng.common.util.MathUtils;

public class PriorityQ implements Comparator<FightTroops>{
	
	List<FightTroops> heap = new ArrayList<FightTroops>();
	
	public void Push(FightTroops troops) {
		if (troops != null && !heap.contains(troops)){
			heap.add(troops);
		}
	}
	
	public FightTroops Pop() {
		if (heap.size() > 0) {
			FightTroops troops = heap.get(0);
			heap.remove(0);
			return troops;
		}
		return null;
	}

	public void sort() {
		try {
			Collections.sort(heap,this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void clear(){
		heap.clear();
	}
	
	@Override
	public int compare(FightTroops t1 , FightTroops t2) {
		int s1 = t1.getSpeed();
		int s2 = t2.getSpeed();
		if (s1 == s2) {
			int a = MathUtils.random(100);
			if (a < 40){
				return -1;
			}else if (a > 40 && a <= 60){
				return 0;
			}else{
				return 1;
			}
		}else if (s1 > s2) {
			return -1;
		} else {
			return 1;
		}
	}
}
