package com.joymeng.slg.domain.actvt;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.UnableToInterruptJobException;
import org.quartz.impl.StdSchedulerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.GameLog;
import com.joymeng.slg.dao.DBManager;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.actvt.data.Activity;
import com.joymeng.slg.domain.actvt.impl.RushSevenDay;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.mod.ClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.world.TaskPool;
import com.joymeng.slg.world.World;

public class ActvtManager extends DTManager
{
	enum ActvtOperate
	{
		KEEP("keep"), NEW("new"), UPDATE("update"), ADD("add"), DELETE("delete");
		
		private String name;
		
		public String getName() {
			return name;
		}
		
		public boolean equals(String type) {
			return name.equals(type);
		}
		
		private ActvtOperate(String name) {
			this.name = name;
		}
	}
	
	private Map<Integer, Actvt> actvts = new HashMap<Integer, Actvt>();
//	private Map<String, List<Activity_reward>> rewards = new HashMap<String, List<Activity_reward>>();
	
	private Scheduler scheduler;
	
	private static ActvtManager instance = new ActvtManager();
	public static ActvtManager getInstance()
	{
		return instance;
	}
	
	private ActvtManager()
	{
		 
	}
	
	private Actvt createActvt(Activity activity)
	{
		String classPath = this.getClass().getName();
		classPath = classPath.substring(0, classPath.lastIndexOf('.')+1) + "impl." + activity.getType();
		
		try {
			@SuppressWarnings("unchecked")
			Class<? extends Actvt> clazz = (Class< ? extends Actvt>)Class.forName(classPath);
			Actvt actvt = clazz.newInstance();
			if (!actvt.init(activity)) {
				return null;
			}
			return actvt;
			
		} catch (Exception e) {
			GameLog.error("ClassNotFoundException: " + activity.getType());
			return null;
		}
	}
	
	public void init()
	{
		load(true);
		
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		
		// arrange reward datas
//		List<Activity_reward>   = ActvtManager.getInstance().serachList(Activity_reward.class, new SearchFilter<Activity_reward>(){
//			@Override
//			public boolean filter(Activity_reward data) {
//				return true;
//			}
//		});
//		for (int i = 0; i < rewardDatas.size(); i++)
//		{
//			Activity_reward item = rewardDatas.get(i);
//			List<Activity_reward> list;
//			if (rewards.containsKey(item.getrID())) {
//				list = rewards.get(item.getrID());
//			}
//			else {
//				list = new ArrayList<Activity_reward>();
//				rewards.put(item.getrID(), list);
//			}
//			list.add(item);
//		}
//		for (Map.Entry<String, List<Activity_reward>> entry : rewards.entrySet())
//		{
//			List<Activity_reward> rewardList = entry.getValue();
//			Collections.sort(rewardList, rewardComparator);
//		}
		
		// init activitys
		List<Activity> datas = ActvtManager.getInstance().serachList(Activity.class, new SearchFilter<Activity>(){
			@Override
			public boolean filter(Activity data) {
				return true;
			}
		});
		
		for (int i = 0; i < datas.size(); i++)
		{
			Activity activity = datas.get(i);
			if (!ActvtOperate.DELETE.equals(activity.getOperate()))
			{
				Actvt actvt = createActvt(datas.get(i));
				if (actvt != null)
				{
					actvts.put(actvt.getId(), actvt);
				}
			}
		}
		
		loadFromDB();
		
		TaskPool.getInstance().scheduleAtFixedRate(null, new Runnable() {
			@Override
			public void run() {
				save();
			}
		}, 1 * TaskPool.SECONDS_PER_MINTUE, 1 * TaskPool.SECONDS_PER_MINTUE, TimeUnit.SECONDS);
	}

	public void hotLoad()
	{
		load(true);
		
		// init activitys
		List<Activity> datas = ActvtManager.getInstance().serachList(Activity.class, new SearchFilter<Activity>(){
			@Override
			public boolean filter(Activity data) {
				return true;
			}
		});
		for (int i = 0; i < datas.size(); i++)
		{
			Activity activity = datas.get(i);
			if  (ActvtOperate.ADD.equals(activity.getOperate())) 
			{
				Actvt actvt = getActvt(activity.getTypeId());
				if (actvt == null) 
				{
					actvt = createActvt(activity);
					if (actvt != null)
					{
						actvts.put(actvt.getId(), actvt);
					}
				}
				else 
				{
					GameLog.error("already exist same activity typeId=" + activity.getTypeId());
				}
			}
			else if (ActvtOperate.UPDATE.equals(activity.getOperate()))
			{
				Actvt actvt = getActvt(activity.getTypeId());
				if (actvt != null && actvt.hotInit(activity)) {
					actvt.load();
				}
			}
			else if (ActvtOperate.DELETE.equals(activity.getOperate()))
			{
				Actvt actvt = getActvt(activity.getTypeId());
				if (actvt != null) {
					actvt.hotLoadEnd();
					actvts.remove(actvt.getId());
				}
			}
		}
	}
	
	public Actvt getActvt(String typeId)
	{
		for (Map.Entry<Integer, Actvt> entry : actvts.entrySet())
		{
			Actvt actvt = entry.getValue();
			if (actvt.getActivity().getTypeId().equals(typeId)) {
				return actvt;
			}
		}
		return null;
	}
	
	public void loadFromDB() {
		List<SqlData> datas = DBManager.getInstance().getGameDao().getDatas(DaoData.TABLE_RED_ALERT_ACTVT);
		if (datas != null)
		{
			for (int i = 0 ; i < datas.size() ; i++)
			{
				SqlData data = datas.get(i);
				for (Map.Entry<Integer, Actvt> entry : actvts.entrySet())
				{
					Actvt actvt = entry.getValue();
					if (actvt.getActivity().getTypeId().equals(data.getString(DaoData.RED_ALERT_GENERAL_TYPE)))
					{
						actvt.loadFromData(data);
						break;
					}
				}
			}
		}
	}
	
//	public List<Activity_reward> getReward(String id)
//	{
//		if (!rewards.containsKey(id)) {
//			return null;
//		}
//		return rewards.get(id);
//	}
	
	public Actvt getActvt(int actvtId) {
		if (actvts.containsKey(actvtId)) {
			return actvts.get(actvtId);
		}
		return null;
	}
	
	//SN 活动全局ID生成规则
	private int sActvtId = 0;
	public int GenerateActvtId()
	{
		return ++sActvtId;
	}
	
	public static Document getDocumentAndClose(File file) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringComments(false);
		dbf.setIgnoringElementContentWhitespace(false);
		dbf.setValidating(false);
		dbf.setCoalescing(true);
        DocumentBuilder documentbuilder = dbf.newDocumentBuilder();
        InputSource is = new InputSource(new FileInputStream(file));
        Document document = documentbuilder.parse(is);
        if (is.getByteStream() != null){
        	is.getByteStream().close();//关闭解析的流
        }
        if (is.getCharacterStream() != null){
        	is.getCharacterStream().close();//关闭解析的流
        }
		return document;
	}
	
	public static void invoke(String method, String args)
	{
		
	}

	public void sendReq(Role role, ClientModule... modules)
	{
		RespModuleSet rms = new RespModuleSet();
		rms.addModules(Arrays.asList(modules));
		MessageSendUtil.sendModule(rms, role.getUserInfo());
	}
	
	private ClientModule makeUpActvtListModule(long joyId)
	{
		ClientMod module = new ClientMod(ClientModule.NTC_DTCD_GET_ACTVT_LIST);

		module.add(getShowActvtsNum(joyId));
		for (Map.Entry<Integer, Actvt> entry : actvts.entrySet())
		{
			Actvt a = entry.getValue();
			if (a == null) {
				//SN 错误处理
				continue;
			}
			if (!a.canShow(joyId)) {
				continue;
			}
			Activity activity = a.getActivity();
			module.add(a.getId());
			module.add(activity.getType());
			module.add(activity.getName());
			module.add(activity.getIcon());
			module.add(a.getStateOdinal(joyId));
			module.add(activity.getBriefDesc());
			module.add(a.getDestDesc());
			
			module.add(activity.getTypeId());
			module.add(a.getStartSeconds());
			module.add(a.getLastSeconds());
			module.add(a.getNowSeconds(joyId));
		}
//		System.out.println(module.getParams().toString());
		return module;
	}

	//SN 如果为null的错误处理
	private ClientModule makeUpActvtDetailModule(Role role, int actvtId)
	{
		ClientMod module = new ClientMod(ClientModule.NTC_DTCD_GET_ACTVT_DETAIL);
		Actvt act = getActvt(actvtId);
		if (act != null) {
			act.makeUpDetailModule(module, role);
		}
		return module;
	}
	
	//SN 如果为null的错误处理
	private ClientModule makeUpActvtRankListModule(Role role, int actvtId)
	{
		ClientMod module = new ClientMod(ClientModule.NTC_DTCD_GET_ACTVT_RANK_LIST);
		Actvt act = getActvt(actvtId);
		if (act == null || !act.makeUpActvtRankListModule(module, role)) {
			return null;
		}
		return module;
	}
	
	private int getShowActvtsNum(long joyId)
	{
		int num = 0;
		for (Map.Entry<Integer, Actvt> entry : actvts.entrySet())
		{
			if (entry.getValue().canShow(joyId)) {
				num++;
			}
		}
		return num;
	}
	
	private int getStartActvtsNum()
	{
		int num = 0;
		for (Map.Entry<Integer, Actvt> entry : actvts.entrySet())
		{
			if (entry.getValue().isRuning()) {
				num++;
			}
		}
		return num;
	}
	
	public void sendActvtTip(long joyId)
	{
		Role role = World.getInstance().getOnlineRole(joyId);
		if (role == null) {
			return;
		}
		
		ClientMod module = new ClientMod(ClientModule.NTC_DTCD_ACTIVITY_TIP);
		module.add(getStartActvtsNum());
		for (Map.Entry<Integer, Actvt> entry : actvts.entrySet())
		{
			Actvt actvt = entry.getValue();
			if (!actvt.isRuning()) {
				continue;
			}
			Activity activity = actvt.getActivity();
			module.add(actvt.getId());
			module.add(activity.getType());
			module.add(activity.getTypeId());
			int num = actvt.getReceiveableNum(role.getId());
			num = 0;
			module.add(num);
			module.add(activity.getType().equals("RushSevenDay")?1:0);
			
			module.add(activity.getTypeId()+"_tip");
			if (activity.getType().equals("RushSevenDay"))
			{
				RushSevenDay rsd = (RushSevenDay)actvt;
				module.add(0L);
				module.add(rsd.isRun(joyId)?(long)RushSevenDay.ONE_DAY_SECONDS:0L);
				module.add(rsd.isRun(joyId)?(long)(rsd.getSeconds(joyId)%RushSevenDay.ONE_DAY_SECONDS):0L);
			}
			else 
			{
				module.add(actvt.getStartSeconds());
				module.add(actvt.getLastSeconds());
				module.add(actvt.getNowSeconds(joyId));
			}
			
		}
//		System.out.println(module.getParams().toString());
		sendReq(role, module);
	}
	
	//SN 返回多个值，标记请求是否成功
	public boolean sendActvtListReq(Role role) {
		sendReq(role, makeUpActvtListModule(role.getId()));
		return true;
	}
	
	public boolean sendActvtDetailReq(Role role, int actvtId)
	{
		ClientModule mod1 = makeUpActvtDetailModule(role, actvtId);
		ClientModule mod2 = makeUpActvtRankListModule(role, actvtId);
		if (mod2 != null) {
			sendReq(role, mod1, mod2);
		}
		else {
			sendReq(role, mod1);
		}
		return true;
	}
	
	public boolean sendActvtRankListReq(Role role, int actvtId)
	{
		sendReq(role, makeUpActvtRankListModule(role, actvtId));
		return true;
	}
	
	public boolean receiveActvtReward(Role role, int actvtId, int index)
	{
		Actvt actvt = getActvt(actvtId);
		if (actvt == null) {
			return false;
		}
		actvt.receiveReward(role, index);
		sendActvtDetailReq(role, actvtId);
		return true;
	}
	
	public boolean manualStartActvt(Role role, int actvtId)
	{
		Actvt actvt = getActvt(actvtId);
		if (actvt == null) {
			return false;
		}
		actvt.manualStart(role);
		sendActvtDetailReq(role, actvtId);
		return true;
	}
	
	public boolean scheduleJob(JobDetail job, Trigger trigger)
	{
		try {
			scheduler.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			GameLog.error("scheduleJob exception: " + ExceptionUtils.getMessage(e));
			return false;
		}
		return true;
	}
	
	public void stopJob(String jobKey, String groupKey)
	{
		try {
			scheduler.interrupt(JobKey.jobKey(jobKey, groupKey));
		} catch (UnableToInterruptJobException e) {
			GameLog.error("stopJob exception: " + ExceptionUtils.getMessage(e));
		}
	}
	
	public void save() 
	{
		for (Map.Entry<Integer, Actvt> entry : actvts.entrySet())
		{
			entry.getValue().save();
		}
	}
	
	//SN 活动XML合法性检测测试方法
	public static void main(String[] args) 
	{
		int flag = 1;
		for (int i = 0; i < 34; i++) {
			System.out.println(Actvt.getBit(flag, i));
		}
		
		
//		Map<Long, Integer> flags = new HashMap<Long, Integer>();
//		for (int i = 0; i < 5000; i++) {
//			flags.put(209889076L+i, 340210);
//		}
//		
//		String s = JSON.toJSONString(flags);
//		
//		try {
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			GZIPOutputStream gos = new GZIPOutputStream(baos);  
//			gos.write(s.getBytes(),0,s.length()); 
//			gos.finish();
//			gos.flush();
//			gos.close();
//			
//			byte[] smallData = baos.toByteArray();
//			 
//			PrintWriter out = new PrintWriter("filename.txt");
//			out.println(Hex.encodeHexString(smallData));
//		}
//		catch (Exception e) {
//			
//		}

//		System.out.println(JSON.toJSONString(flags));
		
//		String[] dd = {"dd_1", "dd_r1", "d_1r", "dd_11", "dd_2123123123", "dd_rr11dd", "dd_12rr1"};
//		for (int i = 0; i < dd.length; i++)
//		{
//			System.out.println(dd[i].matches("dd_[0-9]+$"));
//		}
		
		
//		final Random rand = new Random();
//		final String[] keys = {"useItem", "trainSoldier", "killSoldier", "killNpc", "gatherResource", "robResource", "treatSoldier", "finishActivityInstance"};
//	
//		new Thread(){
//			@Override
//			public void run() {
//				while(true) {
//					try {
//						Thread.sleep(10000);
//						
//						String key = keys[rand.nextInt(8)];
//						EvntManager.getInstance().Notify(key, "1");
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
////					GameLog.info("main thread sleep");
//				}
//				
//			}
//		}.start();
	}
}
