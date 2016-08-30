package com.joymeng.slg.domain.actvt;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ActvtTickJob implements Job 
{
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException 
	{
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		int actvtId = dataMap.getInt("actvtId");
		Actvt actvt = ActvtManager.getInstance().getActvt(actvtId);
		actvt.tick();
	}
	
}
