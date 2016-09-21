package com.joymeng.slg.domain.map.impl.still.moster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.joymeng.Instances;
import com.joymeng.common.util.MathUtils;
import com.joymeng.common.util.RateComparator;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.data.Monster;
import com.joymeng.slg.domain.map.data.Monsterrefresh;
import com.joymeng.slg.domain.map.impl.still.TypeRate;
import com.joymeng.slg.domain.map.physics.MapCellType;

public class MonsterRefreshAble implements Runnable,Instances{
	String refreshId;
	boolean delFlag = false;
	
	public MonsterRefreshAble(String refreshId,boolean delFlag){
		this.refreshId = refreshId;
		this.delFlag = delFlag;
	}
	
	@Override
	public void run() {
		Monsterrefresh mf = dataManager.serach(Monsterrefresh.class,refreshId);
		if (mf == null){
			return;
		}
		List<Integer> indexs = MapUtil.getRangeIndexs(mf.getCenterX(),mf.getCenterY(), mf.getRangeX(),mf.getRangeY());
		int count = Math.min(mf.getCount(),indexs.size());
		int have = mapWorld.getRefreshAliveCount(false,refreshId,delFlag);
		if (have >= count){//这个块的数量已饱和
			return;
		}
		count -= have;// 需要刷新的个数
		//if (delFlag){
		//	GameLog.info("refresh monster >>> " + refreshId + " num = " + count);
		//}
		List<String> lis = mf.getNeedDistribution();
		int total = 0;
		List<RateComparator> rcs = new ArrayList<RateComparator>();
		for (int j = 0 ; j < lis.size() ; j++){
			String s = lis.get(j);
			String[] ss = s.split(":");
			int rate = Integer.parseInt(ss[2]);
			total += rate;
			rcs.add(new RateComparator(new TypeRate(ss[0],ss[1]),rate));
		}
		Collections.sort(rcs);
		do {
			RateComparator rc = MathUtils.getRandomObj(rcs,total);
			if (rc == null){
				continue;
			}
			TypeRate tr = rc.object();
			int index    = MathUtils.random(indexs.size());
			int position = indexs.get(index).intValue();
			if (!mapWorld.checkPosition(MapCellType.MAP_CELL_TYPE_MONSTER.getVolume(),position)) {//如果这个位置放不下
				continue;
			}
			MapMonster monster = null;
			if (tr.getKey().equals("monster")){
				Monster monsterData = dataManager.serach(Monster.class,tr.getId());
				if (monsterData == null) {
					GameLog.error("策划SB,把怪物类型字符串填错了");
					break;
				}
				monster = mapWorld.create(MapMonster.class,false);
				monster.setLevel(monsterData.getLevel());
			}else if (tr.getKey().equals("boss")){
				
			}else if (tr.getKey().equals("npc")){
				
			}
			mapWorld.insertObj(monster);
			monster.setKey(tr.getId());
			monster.setRefreshId(refreshId);
			monster.registAutoDie(mf.getSurvivalTime());
			mapWorld.updatePosition(monster,position);//如果能放下就放这里
			mapWorld.clearIndexs(indexs);//移除被占的格子
			count--;
		}while(count > 0 && indexs.size() > 0);
	}
}
