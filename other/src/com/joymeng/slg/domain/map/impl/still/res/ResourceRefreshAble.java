package com.joymeng.slg.domain.map.impl.still.res;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.joymeng.Instances;
import com.joymeng.common.util.MathUtils;
import com.joymeng.common.util.RateComparator;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.data.Resourcerefresh;
import com.joymeng.slg.domain.map.physics.MapCellType;

public class ResourceRefreshAble implements Runnable,Instances{
	String refreshId;
	boolean delFlag = false;
	
	public ResourceRefreshAble(String refreshId,boolean delFlag){
		this.refreshId = refreshId;
		this.delFlag = delFlag;
	}
	
	@Override
	public void run() {
		Resourcerefresh rf = dataManager.serach(Resourcerefresh.class,refreshId);
		if (rf == null){
			return;
		}
		List<Integer> indexs = MapUtil.getRangeIndexs(rf.getCenterX(),rf.getCenterY(), rf.getRangeX(), rf.getRangeY());
		int count = Math.min(rf.getCount(), indexs.size());
		int have = mapWorld.getRefreshAliveCount(true,refreshId,delFlag);
		if (have >= count) {
			return;
		}
		count -= have;// 需要刷新的个数
		//if (delFlag){
			//GameLog.info("refresh resource >>> " + refreshId + " num = " + count);
		//}
		List<String> lis = rf.getNeedDistribution();
		int total1 = 0,total2 =0;
		List<RateComparator> rcs1 = new ArrayList<RateComparator>();
		for (int i = 0 ; i < lis.size() ; i++){
			String s = lis.get(i);
			String[] ss = s.split(":");
			int rate = Integer.parseInt(ss[1]);
			total1 += rate;
			rcs1.add(new RateComparator(ss[0],rate));
		}
		Collections.sort(rcs1);
		lis = rf.getNeedProbavility();
		List<RateComparator> rcs2 = new ArrayList<RateComparator>();
		for (int i = 0 ; i < lis.size() ; i++){
			String s = lis.get(i);
			String[] ss = s.split(":");
			int rate = Integer.parseInt(ss[1]);
			total2 += rate;
			rcs2.add(new RateComparator(ss[0],rate));
		}
		Collections.sort(rcs2);
		do {
			RateComparator rc1 = MathUtils.getRandomObj(rcs1,total1);
			if (rc1 == null){
				continue;
			}
			RateComparator rc2 = MathUtils.getRandomObj(rcs2,total2);
			if (rc2 == null){
				continue;
			}
			int index    = MathUtils.random(indexs.size());
			int position = indexs.get(index).intValue();
			if (!mapWorld.checkPosition(MapCellType.MAP_CELL_TYPE_RESOURCE.getVolume(),position)) {//如果这个位置放不下
				continue;
			}
			MapResource res = mapWorld.create(MapResource.class,true);
			String typeStr  = rc1.object();
			String levelStr = rc2.object();
			res.setLevel(Integer.parseInt(levelStr));
			res.setKey(typeStr);
			res.setRefreshId(refreshId);
			res.initOutPut();
			res.registAutoDie(rf.getSurvivalTime());
			mapWorld.updatePosition(res,position);//如果能放下就放这里
			mapWorld.clearIndexs(indexs);//移除被占的格子
			count--;
		}while(count > 0 && indexs.size() > 0);
	}
}
