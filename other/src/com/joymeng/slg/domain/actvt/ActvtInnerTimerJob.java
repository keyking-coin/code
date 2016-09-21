package com.joymeng.slg.domain.actvt;

import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

import com.joymeng.log.GameLog;

public class ActvtInnerTimerJob implements InterruptableJob 
{
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException 
	{
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String tag = dataMap.getString("tag");
		int actvtId = dataMap.getInt("actvtId");
		Actvt actvt = ActvtManager.getInstance().getActvt(actvtId);
		if (actvt != null) {
			actvt.innerTimerCB(tag);
		}
		else {
			GameLog.error("ActvtInnerTimerJob: actvtId="+actvtId+" not exist");
		}
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		System.out.println("interrupt");
	}
}
