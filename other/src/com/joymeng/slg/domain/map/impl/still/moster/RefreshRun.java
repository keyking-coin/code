package com.joymeng.slg.domain.map.impl.still.moster;

import java.util.List;

import com.joymeng.Instances;
import com.joymeng.common.util.MathUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.data.Monster;
import com.joymeng.slg.domain.map.data.Monsterrefresh;

public class RefreshRun implements Runnable,Instances{
	String refreshId;
	
	public RefreshRun(String refreshId){
		this.refreshId = refreshId;
	}
	
	@Override
	public void run() {
		Monsterrefresh mf = dataManager.serach(Monsterrefresh.class,refreshId);
		if (mf == null){
			return;
		}
		List<Integer> indexs = MapUtil.getRangeIndexs(mf.getCenterX(),mf.getCenterY(), mf.getRangeX(),mf.getRangeY());
		int count = Math.min(mf.getCount(),indexs.size());
		int have = mapWorld.getRefreshAliveCount(false,refreshId);
		if (have >= count){//这个块的数量已饱和
			return;
		}
		if (have > 0){
			
		}
		count -= have;// 需要刷新的个数
		List<String> lis = mf.getNeedDistribution();
		MonsterTypeRate[] mtrs = new MonsterTypeRate[lis.size()];
		int[] rates = new int[lis.size()];
		for (int j = 0 ; j < lis.size() ; j++){
			String s = lis.get(j);
			String[] ss = s.split(":");
			MonsterTypeRate mtr = new MonsterTypeRate();
			mtr.key  = ss[0];
			mtr.id   = ss[1];
			mtrs[j] = mtr;
			rates[j] = Integer.parseInt(ss[2]);
		}
		do {
			MonsterTypeRate mtr = MathUtils.getRandomObj(mtrs,rates);
			if (mtr == null){
				continue;
			}
			int index    = MathUtils.random(indexs.size());
			int position = indexs.get(index).intValue();
			MapMonster monster = null;
			if (mtr.key.equals("monster")){
				Monster monsterData = dataManager.serach(Monster.class,mtr.id);
				if (monsterData == null) {
					GameLog.error("策划SB,把怪物类型字符串填错了");
					break;
				}
				monster = mapWorld.create(MapMonster.class,false);
				monster.setLevel(monsterData.getLevel());
			}else if (mtr.key.equals("boss")){
				
			}else if (mtr.key.equals("npc")){
				
			}
			if (!mapWorld.checkPosition(monster,position)) {//如果这个位置放不下
				continue;
			}
			mapWorld.insertObj(monster);
			monster.setKey(mtr.id);
			monster.setRefreshId(refreshId);
			monster.registAutoDie(mf.getSurvivalTime());
			mapWorld.updatePosition(monster,position);//如果能放下就放这里
			mapWorld.clearIndexs(indexs);//移除被占的格子
			count--;
		}while(count > 0 && indexs.size() > 0);
	}
	
	class MonsterTypeRate{
		String key;
		String id;
	}
}
