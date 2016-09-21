package com.joymeng.slg.union.task;

import com.joymeng.common.util.TimeUtils;

public class AlliDailyTask extends AlliTask {
	//进度
	int schedule;
	//开启时间
	long openTime;
	//完成持续时间
	long continued = 0;
	
	@Override
	protected int updateProgress(int count) {
		if(count > 0)
			schedule += count;
		return schedule;
	}

	@Override
	protected boolean isOpen() {
		return TimeUtils.nowLong() >= openTime;
	}

	@Override
	protected boolean isRefresh() {
		//日常任务每天刷新
		return false;
	}

	@Override
	protected boolean isFinish() {
		return TimeUtils.nowLong() >= openTime + continued;
	}

}
