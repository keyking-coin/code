package com.joymeng.slg.domain.actvt;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.joymeng.log.GameLog;

public class ActvtTickJob implements Job 
{
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException 
	{
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		int actvtId = dataMap.getInt("actvtId");
		Actvt actvt = ActvtManager.getInstance().getActvt(actvtId);
		if (actvt != null) {
			actvt.tick();
		}
		else {
			GameLog.error("ActvtTickJob actvtId="+actvtId+" not exist");
		}
	}
}
