package com.joymeng.slg.domain.actvt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.TriggerBuilder;

import com.alibaba.fastjson.JSON;
import com.joymeng.Instances;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.GameLog;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.actvt.DTManager.SearchFilter;
import com.joymeng.slg.domain.actvt.data.Activity;
import com.joymeng.slg.domain.actvt.data.Activity_cmd;
import com.joymeng.slg.domain.actvt.data.Activity_reward;
import com.joymeng.slg.domain.evnt.EventMgr;
import com.joymeng.slg.domain.evnt.EvntManager;
import com.joymeng.slg.domain.evnt.IEvnt;
import com.joymeng.slg.domain.object.bag.BriefItem;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.mod.RespModuleSet;

public abstract class Actvt extends EventMgr implements IEvnt, Instances, DaoData {
	public enum ActvtState {
		PREPARE("未开始"), RUNING("进行中"), END("已结束"), ENDING("正在结束");

		private String name;

		public String getName() {
			return name;
		}

		private ActvtState(String name) {
			this.name = name;
		}
	}

	protected static final String SPCH = "#";

	private int id = 0;
	protected ActvtState state = ActvtState.PREPARE;

	private Activity activity;
	private List<Activity_cmd> cmds;

	private DateTimeFormatter jodaFormatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");

	private DateTime showTime;
	private DateTime startTime;
	private DateTime endTime;
	private DateTime hideTime;
	boolean savIng = false;
	
	private Timer startTimer;
	private Timer endTimer;

	class RewardComparator implements Comparator<Activity_reward> 
	{
		@Override
		public int compare(Activity_reward reward1, Activity_reward reward2) 
		{
			return reward1.getsID().compareTo(reward2.getsID());
		}
	}
	protected RewardComparator rewardComparator = new RewardComparator();
	
	public Activity getActivity() {
		return activity;
	}

	public int getId() {
		return id;
	}

	public boolean isRuning() {
		return state == ActvtState.RUNING;
	}

	public String getStateName() {
		return state.getName();
	}

	public int getStateOdinal(long joyId) {
		return state.ordinal();
	}

	public long getStartSeconds() {
		if (state == ActvtState.END) {
			return 0L;
		} else if (state == ActvtState.PREPARE) {
			return showTime.getMillis() / 1000;
		}
		return startTime.getMillis() / 1000;
	}

	public long getLastSeconds() {
		if (state == ActvtState.END) {
			return 0L;
		} else if (state == ActvtState.PREPARE) {
			return startTime.getMillis() / 1000 - showTime.getMillis() / 1000;
		}
		return endTime.getMillis() / 1000 - startTime.getMillis() / 1000;
	}

	public long getNowSeconds(long joyId) {
		if (state == ActvtState.END) {
			return 0L;
		}
		return DateTime.now().getMillis() / 1000;
	}

	public boolean canShow(long joyId) {
		return DateTime.now().isAfter(showTime) && DateTime.now().isBefore(hideTime);
	}

	public String getTimeDesc() {
		return state.getName();
	}

	public String getDestDesc() {
		return "";
	}

	private DateTime parseTime(String timestr) {
		if (timestr.equals("now")) {
			return DateTime.now();
		} else if (timestr.equals("forever")) {
			return new DateTime(Long.MAX_VALUE);
		} else {
			return jodaFormatter.parseDateTime(timestr);
		}
	}

	public boolean init(Activity actvt) {
		this.activity = actvt;
		id = actvtMgr.GenerateActvtId();

		showTime = parseTime(activity.getShowTime());
		startTime = parseTime(activity.getStartTime());
		endTime = parseTime(activity.getEndTime());
//		if (activity.getType().equals("ScoreRush")) {
//			endTime = DateTime.now();
//			int idx = Integer.parseInt(activity.getTypeId().substring(9));
//			endTime.plusMinutes(2*idx);
//		}
		hideTime = parseTime(activity.getHideTime());
		if (!showTime.isBefore(startTime) || !startTime.isBefore(endTime) || !endTime.isBefore(hideTime)) {
			GameLog.error("actvt: " + activity.getTypeId() + " time have error");
			return false;
		}

		cmds = actvtMgr.serachList(Activity_cmd.class, new SearchFilter<Activity_cmd>() {
			@Override
			public boolean filter(Activity_cmd data) {
				return data.getActivity().equals(activity.getType()) || data.getActivity().equals(activity.getTypeId());
			}
		});
		for (int i = 0; i < cmds.size(); i++) {
			Activity_cmd cmd = cmds.get(i);
			EvntManager.getInstance().Listen(cmd.getEvents(), this);
			Listen(cmd.getEvents(), this);
		}

		state = ActvtState.PREPARE;
		if (endTime.isBefore(DateTime.now())) {
			state = ActvtState.END;
		} else {
			startTimer = new Timer();
			startTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					start();
				}
			}, startTime.toDate());

			endTimer = new Timer();
			endTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					end();
				}
			}, endTime.toDate());
		}

		return true;
	}
	
	public boolean hotInit(Activity actvt)
	{
		this.activity = actvt;
		
		for (int i = 0; i < cmds.size(); i++) {
			Activity_cmd cmd = cmds.get(i);
			EvntManager.getInstance().Remove(cmd.getEvents(), this);
			Remove(cmd.getEvents(), this);
		}
		
		if (startTimer != null) {
			startTimer.cancel();
		}
		if (endTimer != null) {
			endTimer.cancel();
		}
		
		showTime = parseTime(activity.getShowTime());
		startTime = parseTime(activity.getStartTime());
		endTime = parseTime(activity.getEndTime());
		hideTime = parseTime(activity.getHideTime());
		
		if (!showTime.isBefore(startTime) || !startTime.isBefore(endTime) || !endTime.isBefore(hideTime)) {
			GameLog.error("actvt: " + activity.getTypeId() + " time have error");
			return false;
		}

		cmds = actvtMgr.serachList(Activity_cmd.class, new SearchFilter<Activity_cmd>() {
			@Override
			public boolean filter(Activity_cmd data) {
				return data.getActivity().equals(activity.getType()) || data.getActivity().equals(activity.getTypeId());
			}
		});
		for (int i = 0; i < cmds.size(); i++) {
			Activity_cmd cmd = cmds.get(i);
			EvntManager.getInstance().Listen(cmd.getEvents(), this);
			Listen(cmd.getEvents(), this);
		}

		state = ActvtState.PREPARE;
		if (endTime.isBefore(DateTime.now())) {
			state = ActvtState.END;
		} else {
			startTimer = new Timer();
			startTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					start();
				}
			}, startTime.toDate());

			endTimer = new Timer();
			endTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					end();
				}
			}, endTime.toDate());
		}
		return true;
	}
	
	public void hotLoadEnd()
	{
		state = ActvtState.END;

		for (int i = 0; i < cmds.size(); i++) {
			Activity_cmd cmd = cmds.get(i);
			EvntManager.getInstance().Remove(cmd.getEvents(), this);
			Remove(cmd.getEvents(), this);
		}
		
		if (startTimer != null) {
			startTimer.cancel();
		}
		if (endTimer != null) {
			endTimer.cancel();
		}
	}

	public void start() {
		GameLog.info("actvt start: " + activity.getName());
		state = ActvtState.RUNING;
		Notify("start", "");
	}
	
	public abstract void load();

	public void end() {
		state = ActvtState.END;
		Notify("end", "");

		for (int i = 0; i < cmds.size(); i++) {
			Activity_cmd cmd = cmds.get(i);
			EvntManager.getInstance().Remove(cmd.getEvents(), this);
			Remove(cmd.getEvents(), this);
		}
		
		if (startTimer != null) {
			startTimer.cancel();
		}
		if (endTimer != null) {
			endTimer.cancel();
		}
	}

	public void innerTimer(String value, String data) {
		GameLog.info("innerTimer, " + value + ", " + data);

		String[] values = value.split("#");
		if (values.length < 2) {
			GameLog.error("actvt type=" + activity.getTypeId() + " innerTimer value=" + value);
			return;
		}

		String tag = values[0];
		String times = values[1];

		JobDetail job = JobBuilder.newJob(ActvtInnerTimerJob.class)
				.withIdentity(String.format("job_%d_%s", getId(), tag), "actvt").usingJobData("actvtId", getId())
				.usingJobData("tag", tag).build();

		CronTrigger trigger = TriggerBuilder.newTrigger()
				.withIdentity(String.format("trigger_%d_%s", getId(), tag), "actvt")
				.withSchedule(CronScheduleBuilder.cronSchedule(times)).build();

		actvtMgr.scheduleJob(job, trigger);
	}
	
	public void stopInnerTimer(String value, String data)
	{
		String[] values = value.split("#");
		for (int i = 0; i < values.length; i++)
		{
			String tag = values[i];
			actvtMgr.stopJob(String.format("job_%d_%s", getId(), tag), "actvt");
		}
	}

	public void logActvt(String arg, String data) {

	}
	
	public void sendEmail(long joyId, String content, List<Activity_reward> rewards)
	{
		if (rewards != null && rewards.size() > 0)
		{
			List<BriefItem> annex = new ArrayList<BriefItem>();
			for (int i = 0; i < rewards.size(); i++)
			{
				Activity_reward reward = rewards.get(i);
				BriefItem bri = new BriefItem(reward.getType(),reward.getsID(),reward.getNum());
				annex.add(bri);
			}
			chatMgr.creatSystemEmail(content, annex, joyId);
		}
		else 
		{
			chatMgr.creatSystemEmail(content, joyId);
		}
	}

	public Role getRole(String data) {
		try {
			int joyId = Integer.valueOf(data);
			return world.getRole(joyId);
		} catch (Exception e) {
			// SN 错误处理
			return null;
		}
	}

	public void startTick(String value, String data) {
		GameLog.info("startTick, " + value + ", " + data);

		String times = value;

		JobDetail job = JobBuilder.newJob(ActvtTickJob.class)
				.withIdentity(String.format("job_tick_%d", getId()), "actvt").usingJobData("actvtId", getId()).build();

		CronTrigger trigger = TriggerBuilder.newTrigger()
				.withIdentity(String.format("trigger_tick_%d", getId()), "actvt")
				.withSchedule(CronScheduleBuilder.cronSchedule(times)).build();

		actvtMgr.scheduleJob(job, trigger);
	}

	public void rewardPlayer(long joyId, String reward) {
		Role role = world.getRole(joyId);
		if (role != null) {
			rewardPlayer(role, reward);
		} else {
			// SN 错误处理
		}
	}

//	public void rewardPlayer(String rewardId, String data) {
//		if (!Yotils.isNumeric(data)) {
//			GameLog.error(
//					"actvt id=" + id + " type=" + activity.getTypeId() + " rewardPlayer data=" + data + " is not number");
//			return;
//		}
//
//		long joyId = Long.parseLong(data);
//		Role role = world.getRole(joyId);
//		if (role != null) {
//	List<Activity_reward> rewardList = actvtMgr.getReward(rewardId);
//			rewardPlayer(role, Activity_reward.toString(rewardList));
//		} else {
//			GameLog.error("actvt id=" + id + " type=" + activity.getTypeId() + " rewardPlayer data=" + data
//					+ " role doesn't exist");
//		}
//	}

	public void rewardPlayer(Role role, String reward) {
		RespModuleSet rms = new RespModuleSet();
		String[] values = reward.split(",");
		for (int i = 0; i < values.length; i++) {
			String[] valuess = values[i].split("#");
			String type = valuess[0];
			String rid = valuess[1];
			int num = Integer.parseInt(valuess[2]);

			List<ItemCell> itemCells = null;

			if (ActvtRewardType.MONEY.equals(type)) {
				role.addRoleMoney(num);
				role.sendRoleToClient(rms);
			} else if (ActvtRewardType.ITEM.equals(type)) {
				itemCells = role.getBagAgent().addGoods(rid, num);
				role.getBagAgent().sendItemsToClient(rms, itemCells);
			} else if (ActvtRewardType.EQUIP.equals(type)) {
				itemCells = role.getBagAgent().addEquip(rid, num);
				role.getBagAgent().sendItemsToClient(rms, itemCells);
			} else if (ActvtRewardType.MATERIAL.equals(type)) {
				itemCells = role.getBagAgent().addOther(rid, num);
				role.getBagAgent().sendItemsToClient(rms, itemCells);
			} else if (ActvtRewardType.UNION_SCORE.equals(type)) {

			} else {
				GameLog.error("actvt id=" + id + " type=" + activity.getTypeId() + " rewardPlayer joyId=" + role.getId()
						+ " reward=" + reward + " doesn't exist");
			}
		}
		MessageSendUtil.sendModule(rms, role.getUserInfo());
	}

	public boolean receiveReward(Role role, int index) {
		return false;
	}

	public boolean manualStart(Role role) {
		return false;
	}

	public abstract void makeUpDetailModule(ClientMod module, Role role);

	public boolean makeUpActvtRankListModule(ClientMod module, Role role) {
		return false;
	}

	public void taskEvent(String value, String data) {
	}

	public void tick() {
	}

	@Override
	public String toString() {
		return JSON.toJSONString(activity);
	}

	public static long getBit(long value, int position) {
		return (value >> position) & 1L;
	}

	public static long setBit(long value, int position, int bit) {
		if (getBit(value, position) == bit) {
			return value;
		}
		return ~(value ^ (~(1L << position)));
	}

	// SN 预获取反射方法，存储，提高效率
	// SN 处理各种报错
	@Override
	public void execute(String event, String data) {
		for (int i = 0; i < cmds.size(); i++) {
			Activity_cmd cmd = cmds.get(i);
			if (cmd.getEvents().equals(event)) {
				String methodName = cmd.getCmd();
				String value = cmd.getArgs();

				try {
					Method m = this.getClass().getMethod(methodName, String.class, String.class);
					m.invoke(this, value, data);
				} catch (Exception e) {
					GameLog.error("Actvt execute event="+event+" data="+data+" "+ExceptionUtils.getMessage(e));
				}
			}
		}
	}
	
	public int getReceiveableNum(long joyId)
	{
		return 0;
	}

	public String getStateStr() {
		return "";
	}
	
	public String getStateStrZip() 
	{
		try {
			String str = getStateStr();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			GZIPOutputStream gos = new GZIPOutputStream(baos);  
			gos.write(str.getBytes(),0,str.length()); 
			gos.finish();
			gos.flush();
			gos.close();
			
			byte[] smallData = baos.toByteArray();
			return Hex.encodeHexString(smallData);
		}
		catch (Exception e) {
			GameLog.error("Actvt type="+activity.getTypeId()+" getStateStrZip exception="+ExceptionUtils.getMessage(e));
			return "";
		}
	}
	
	@Override
	public void saveToData(SqlData data) {
		data.put(RED_ALERT_GENERAL_ID, id);
		data.put(RED_ALERT_GENERAL_TYPE, activity.getTypeId());
		data.put(RED_ALERT_GENERAL_STATE, getStateStrZip());
	}

	@Override
	public String table() {
		return TABLE_RED_ALERT_ACTVT;
	}

	@Override
	public String[] wheres() {
		return new String[] { RED_ALERT_GENERAL_ID };
	}

	@Override
	public boolean delete() {
		return false;
	}

	@Override
	public void insertData(SqlData data) {
		saveToData(data);
	}

	@Override
	public void save() {
		if (savIng) {
			return;
		}
		savIng = true;

		dbMgr.getGameDao().saveDaoData(this,new HashMap<String, Object>());
	}

	@Override
	public void over() {
		savIng = false;
	}

	@Override
	public boolean saving() {
		return savIng;
	}
	
	public static byte[] compress(String string) throws IOException {
	    ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
	    GZIPOutputStream gos = new GZIPOutputStream(os);
	    gos.write(string.getBytes());
	    gos.close();
	    byte[] compressed = os.toByteArray();
	    os.close();
	    return compressed;
	}

	public static String decompress(byte[] compressed) throws IOException {
	    final int BUFFER_SIZE = 32;
	    ByteArrayInputStream is = new ByteArrayInputStream(compressed);
	    GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
	    StringBuilder string = new StringBuilder();
	    byte[] data = new byte[BUFFER_SIZE];
	    int bytesRead;
	    while ((bytesRead = gis.read(data)) != -1) {
	        string.append(new String(data, 0, bytesRead));
	    }
	    gis.close();
	    is.close();
	    return string.toString();
	}

	String[] tmpStateStrs = {""};
	protected String[] getStateStrs(SqlData data) 
	{
		try {
			String stateStr = data.getString(RED_ALERT_GENERAL_STATE);
			stateStr = decompress(Hex.decodeHex(stateStr.toCharArray()));
			return stateStr.split(SPCH);
		} 
		catch (Exception e) {
			return tmpStateStrs;
		}
	}
}
