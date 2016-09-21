package com.joymeng.slg.union.task;

import java.util.ArrayList;
import java.util.List;

public abstract class AlliTask {
	//属性
	// 任务id
	long allId;
	// 领奖名单 = uid+进度
	List<String> list = new ArrayList<String>();

	//更新进度
	protected abstract int updateProgress(int count);
	//是否开启
	protected abstract boolean isOpen();
	//是否需要刷新
	protected abstract boolean isRefresh();
	//是否可以领奖huoz
	protected abstract boolean isFinish();
	//是否领奖
	protected boolean collected(long uid,int schedule){
		return list.contains(uid+"-"+schedule);
	}

}
