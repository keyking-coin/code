package com.joymeng.slg.union.task;

import java.util.concurrent.ConcurrentHashMap;

import com.joymeng.Instances;
import com.joymeng.slg.domain.event.AbstractGameEvent;
import com.joymeng.slg.domain.object.IObject;

public class UnionTaskAgent extends AbstractGameEvent implements Instances {
	//tasks
	ConcurrentHashMap<Long, AlliTask> dailyTasks = new ConcurrentHashMap<Long, AlliTask>();

	@Override
	public void _handle(IObject trigger, Object[] params) {
		
	}
	//随机任务
//	public void randomTasks()
	
	//更新任务进度
//	public void motifyTaskSchedule();
	
	//创建任务
//	public void createTask();
	
	//固定任务
//	public void fixedTask
	
	//领奖
//	public void collectedAward()
	
	//下发数据
//	public void sendToClient()
	
	//保存数据
//	public void saveData()
	
	//还原数据
//	public void restoreData()

}
