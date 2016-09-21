package com.joymeng.slg.domain.map.impl.still;

import java.util.concurrent.atomic.AtomicLong;

import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.map.impl.still.moster.MonsterRefreshAble;
import com.joymeng.slg.domain.map.impl.still.res.ResourceRefreshAble;
import com.joymeng.slg.domain.object.AbstractObject;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerOver;

public class RebirthLogic extends AbstractObject implements TimerOver {
	public static final int REBIRTH_TYPE_MONSTER =  1;
	public static final int REBIRTH_TYPE_RESOUCE =  2;
	static AtomicLong idCreater = new AtomicLong(1);
	String refreshId;//刷新规则编号
	long id;
	int type;
	TimerLast timer = null;
	
	public static RebirthLogic create(int type,String refreshId){
		RebirthLogic rl = new RebirthLogic();
		rl.id        = idCreater.incrementAndGet();
		rl.refreshId = refreshId;
		rl.type      = type;
		return rl;
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
		if (type == REBIRTH_TYPE_MONSTER){
			new MonsterRefreshAble(refreshId,false).run();
		}else{
			new ResourceRefreshAble(refreshId,false).run();
		}
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
