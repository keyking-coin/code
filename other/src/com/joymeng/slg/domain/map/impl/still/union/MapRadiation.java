package com.joymeng.slg.domain.map.impl.still.union;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerOver;

public class MapRadiation extends MapObject implements TimerOver{
	TimerLast dieTimer;//消失倒计时
	public void registDieTimer(TimerLast timer){
		dieTimer = timer;
		dieTimer.registTimeOver(this);
		taskPool.mapTread.addObj(this,dieTimer);
	}
	
	@Override
	public MapCellType cellType() {
		return MapCellType.MAP_CELL_NUCLEARSILO_RADIATION;
	}
	
	@Override
	public void saveToData(SqlData data) {
		super.saveToData(data);
		String str = getClass().getName();
		data.put(RED_ALERT_GENERAL_TYPE,str);
		if (dieTimer != null){
			str = JsonUtil.ObjectToJsonString(dieTimer);
			data.put(RED_ALERT_GENERAL_OTHER,str);
		}
	}
	
	@Override
	public void _tick(long now) {
		if (dieTimer != null && dieTimer.over(now)){
			dieTimer.die();
		}
	}

	@Override
	public void loadFromData(SqlData data) {
		super.loadFromData(data);
		String str = data.getString(RED_ALERT_GENERAL_OTHER);
		dieTimer = JsonUtil.JsonToObject(str,TimerLast.class);
		if (dieTimer != null){
			dieTimer.registTimeOver(this);
			taskPool.mapTread.addObj(this,dieTimer);
		}
	}
	
	@Override
	public void troopsArrive(ExpediteTroops expedite) {
		expedite.goBackToCome();
	}

	@Override
	public boolean _couldAttack(ExpediteTroops expedite) {
		return false;
	}

	@Override
	public void finish() {
		dieTimer = null;
		remove();
	}

	@Override
	public void serialize(JoyBuffer out) {
		super.serialize(out);
		if (dieTimer != null){
			 out.putInt(1);
			 dieTimer.serialize(out);
		}else{
			 out.putInt(0);
		}
	}

	@Override
	public int getLevel() {
		return 1;
	}
}
