package com.joymeng.slg.domain.map.union;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.map.fight.result.FightVersus;
import com.joymeng.slg.domain.map.impl.dynamic.GridType;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.net.SerializeEntity;

public class UnionFightTransformData implements SerializeEntity{
	public static final byte UNION_FIGHT_TYPE_NORMAL = 0;
	public static final byte UNION_FIGHT_TYPE_MASS   = 1;
	public static final byte UNION_FIGHT_STATE_GOING = 0;
	public static final byte UNION_FIGHT_STATE_STILL = 1;
	byte type;//0普通战斗,1集结战斗
	byte state;//0行军，1 集结状态
	int maxMassNum;//最大集结数量
	FightVersus defender = new FightVersus();//防守方
	GridType[] grids = null;//攻击方战斗单位映射
	TimerLast massTimer;
	
	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public TimerLast getMassTimer() {
		return massTimer;
	}

	public void setMassTimer(TimerLast massTimer) {
		this.massTimer = massTimer;
	}

	public FightVersus getDefender() {
		return defender;
	}

	public void setDefender(FightVersus defender) {
		this.defender = defender;
	}

	public GridType[] getGrids() {
		return grids;
	}

	public void setGrids(GridType[] grids) {
		this.grids = grids;
	}

	public int getMaxMassNum() {
		return maxMassNum;
	}

	public void setMaxMassNum(int maxMassNum) {
		this.maxMassNum = maxMassNum;
	}

	@Override
	public void serialize(JoyBuffer out) {
		out.put(type);//byte 0普通战斗,1集结战斗
		out.put(state);//byte 0行军,1 集结状态
		out.putInt(maxMassNum);//int 最大集结数量
		if (massTimer != null){
			out.putInt(1);
			massTimer.serialize(out);
		}else{
			out.putInt(0);
		}
		defender.serialize(out);//目标信息
		out.putInt(grids.length);//int 格子数
		for (int i  = 0 ; i < grids.length ; i++){
			GridType grid = grids[i];
			if (grid != null && grid.object() != null){
				out.putInt(1);//1 格子有数据
				grid.serialize(out);//格子里面具体数据
			}else{
				out.putInt(0);//0 空格子
			}
		}
	}
}
