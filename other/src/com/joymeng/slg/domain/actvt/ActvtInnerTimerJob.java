package com.joymeng.slg.domain.actvt;

import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

public class ActvtInnerTimerJob implements InterruptableJob 
{
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException 
	{
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String tag = dataMap.getString("tag");
//		GameLog.info("innerTimer"+tag);
		int actvtId = dataMap.getInt("actvtId");
		Actvt actvt = ActvtManager.getInstance().getActvt(actvtId);
		actvt.Notify("innerTimer"+tag, "");
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		
	}
}
