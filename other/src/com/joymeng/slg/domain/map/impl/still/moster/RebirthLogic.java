package com.joymeng.slg.domain.map.impl.still.moster;

import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.object.AbstractObject;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerOver;

public class RebirthLogic extends AbstractObject implements TimerOver {
	String refreshId;//刷新规则编号
	static long createIndex = 1;
	long id;
	TimerLast timer = null;
	
	public static RebirthLogic create(Object locker,String refreshId){
		synchronized (locker) {
			RebirthLogic rl = new RebirthLogic();
			rl.id = createIndex;
			rl.refreshId = refreshId;
			createIndex++;
			return rl;
		}
	}
	
	@Override
	public long getId() {
		return id;
	}
	
	@Override
	public String table() {
		return null;
	}

	@Override
	public String[] wheres() {
		return null;
	}

	@Override
	public void insertData(SqlData data) {
		
	}

	@Override
	public void loadFromData(SqlData data) {
		
	}

	@Override
	public void saveToData(SqlData data) {
		
	}

	@Override
	public void finish() {
		RefreshRun rr = new RefreshRun(refreshId);
		rr.run();
		remove();
	}

	@Override
	public void registerAll() {
		
	}

	@Override
	public void _tick(long now) {
		if (timer != null && timer.over(now)){
			timer.die();
		}
	}
	
	public void registTimer(TimerLast timer) {
		this.timer = timer;
		timer.registTimeOver(this);
		taskPool.mapTread.addObj(this,timer);
	}
}
