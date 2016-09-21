package com.joymeng.slg.domain.actvt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.w3c.dom.Element;

import com.joymeng.Instances;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.Yotils;
import com.joymeng.log.GameLog;
import com.joymeng.services.utils.XmlUtils;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.actvt.data.ActvtCommon;
import com.joymeng.slg.domain.actvt.data.ActvtReward;
import com.joymeng.slg.domain.actvt.data.ActvtTask;
import com.joymeng.slg.domain.chat.MsgTitleType;
import com.joymeng.slg.domain.evnt.IEvnt;
import com.joymeng.slg.domain.object.bag.BriefItem;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.mod.RespModuleSet;

public abstract class Actvt implements IEvnt, Instances, DaoData {
	public enum ActvtState {
		PREPARE("未开始"), RUNING("进行中"), END("已结束"), HIDE("已隐藏");

		private String name;

		public String getName() {
			return name;
		}

		private ActvtState(String name) {
			this.name = name;
		}
	}

	protected static final String STATE_STR_SPLIT_CH = "#";
	private DateTimeFormatter jodaFormatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");

	protected ActvtState state;
	protected ActvtCommon commonData = new ActvtCommon();
	protected Map<String, ActvtReward> rewardMap = new HashMap<String, ActvtReward>();
	protected List<ActvtReward> rewardList = new ArrayList<ActvtReward>();
	protected Map<String, ActvtTask> taskMap = new HashMap<String, ActvtTask>();
	protected List<ActvtTask> taskList = new ArrayList<ActvtTask>();

	private DateTime showTime;
	private DateTime startTime;
	private DateTime endTime;
	private DateTime hideTime;
	boolean savIng = false;

	protected Timer startTimer;
	protected Timer endTimer;
	protected Timer hideTimer;

	public boolean isRuning() {
		return state == ActvtState.RUNING;
	}

	public String getStateName() {
		return state.getName();
	}

	public int getStateOdinal() {
		return state.ordinal();
	}

	public long getStartSeconds() {
		if (state == ActvtState.END || state == ActvtState.HIDE) {
			return 0L;
		} else if (state == ActvtState.PREPARE) {
			return showTime.getMillis() / 1000;
		}
		return startTime.getMillis() / 1000;
	}

	public long getLastSeconds() {
		if (state == ActvtState.END || state == ActvtState.HIDE) {
			return 0L;
		} else if (state == ActvtState.PREPARE) {
			return startTime.getMillis() / 1000 - showTime.getMillis() / 1000;
		}
		return endTime.getMillis() / 1000 - startTime.getMillis() / 1000;
	}

	public long getNowSeconds() {
		if (state == ActvtState.END || state == ActvtState.HIDE) {
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

	public ActvtCommon getCommonData() {
		return commonData;
	}

	public List<ActvtReward> getRewardList() {
		return rewardList;
	}

	public List<ActvtTask> getTaskList() {
		return taskList;
	}

	public ActvtReward getReward(String id) {
		if (rewardMap.containsKey(id)) {
			return rewardMap.get(id);
		}
		return null;
	}

	public ActvtTask getTask(String id) {
		if (taskMap.containsKey(id)) {
			return taskMap.get(id);
		}
		return null;
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

	public void init() {
		state = ActvtState.PREPARE;

		if (startTimer != null) {
			startTimer.cancel();
			startTimer = null;
		}

		startTimer = new Timer();
		startTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				start();
			}
		}, startTime.toDate());

		if (endTimer != null) {
			endTimer.cancel();
			endTimer = null;
		}

		endTimer = new Timer();
		endTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				end();
			}
		}, endTime.toDate());

		if (hideTimer != null) {
			hideTimer.cancel();
			hideTimer = null;
		}

		hideTimer = new Timer();
		hideTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				hide();
			}
		}, hideTime.toDate());
	}

	public void start() {
		state = ActvtState.RUNING;
		GameLog.info("actvt start name=" + commonData.getName() + " id=" + getId());
	}

	public void end() {
		state = ActvtState.END;
		GameLog.info("actvt end name=" + commonData.getName() + " id=" + getId());
	}

	public void hide() {
		if (startTimer != null) {
			startTimer.cancel();
			startTimer = null;
		}
		if (endTimer != null) {
			endTimer.cancel();
			endTimer = null;
		}
		if (hideTimer != null) {
			hideTimer.cancel();
			hideTimer = null;
		}
		state = ActvtState.HIDE;
		GameLog.info("actvt hide name=" + commonData.getName() + " id=" + getId());
		ActvtManager.getInstance().hideActvt(getId());
	}

	public void load(Element element) throws Exception {
		commonData.setId(Integer.parseInt(element.getAttribute("id")));
		commonData.setType(element.getAttribute("type"));
		commonData.setName(element.getAttribute("name"));
		commonData.setIcon(element.getAttribute("icon"));
		commonData.setShowTime(element.getAttribute("showTime"));
		commonData.setStartTime(element.getAttribute("startTime"));
		commonData.setEndTime(element.getAttribute("endTime"));
		commonData.setHideTime(element.getAttribute("hideTime"));
		commonData.setBriefDesc(element.getAttribute("briefDesc"));
		commonData.setDetailDesc(element.getAttribute("detailDesc"));

		showTime = parseTime(commonData.getShowTime());
		startTime = parseTime(commonData.getStartTime());
		endTime = parseTime(commonData.getEndTime());
		hideTime = parseTime(commonData.getHideTime());
		if (showTime.isAfter(startTime) || startTime.isAfter(endTime) || endTime.isAfter(hideTime)) {
			throw new Exception("id =" + commonData.getId() + " time order is error");
		}
		if (!endTime.isAfter(DateTime.now())) {
			throw new Exception("id =" + commonData.getId() + " endTime=" + commonData.getEndTime() + " already ended");
		}

		rewardList.clear();
		rewardMap.clear();
		Element eleRewards = XmlUtils.getChildByName(element, "Rewards");
		if (eleRewards != null) {
			if (eleRewards.hasChildNodes()) {
				Element[] eles = XmlUtils.getChildrenByName(eleRewards, "Reward");
				for (int i = 0; i < eles.length; i++) {
					ActvtReward rd = new ActvtReward();
					Element ele = eles[i];
					rd.setId(ele.getAttribute("id"));
					if (ele.hasAttribute("tag")) {
						rd.setTag(ele.getAttribute("tag"));
					}
					rd.setItems(ele.getAttribute("items"));
					if (!rd.checkValide()) {
						throw new Exception("id =" + commonData.getId() + " rewardId=" + rd.getId() + " error");
					}
					if (rewardMap.containsKey(rd.getId())) {
						throw new Exception(
								"id =" + commonData.getId() + " rewardId=" + rd.getId() + " id already exist");
					}
					rewardList.add(rd);
					rewardMap.put(rd.getId(), rd);
				}
			}
		}

		taskList.clear();
		taskMap.clear();
		Element eleTasks = XmlUtils.getChildByName(element, "Tasks");
		if (eleTasks != null) {
			if (eleTasks.hasChildNodes()) {
				Element[] eles = XmlUtils.getChildrenByName(eleTasks, "Task");
				for (int i = 0; i < eles.length; i++) {
					Element ele = eles[i];
					ActvtTask td = new ActvtTask();
					td.setId(ele.getAttribute("id"));
					if (!ele.hasAttribute("isShow") || ele.getAttribute("isShow").equals("1")) {
						td.setShow(true);
					} else {
						td.setShow(false);
					}
					td.setDesc(ele.getAttribute("desc"));
					td.setType(ele.getAttribute("type"));

					int n = 1;
					List<String> args = td.getArgs();
					while (true) {
						String name = "arg" + n++;
						if (ele.hasAttribute(name)) {
							args.add(ele.getAttribute(name));
						} else {
							break;
						}
					}

					String nums = ele.getAttribute("num");
					if (!Yotils.isNumeric(nums) || Integer.parseInt(nums) <= 0) {
						throw new Exception("id =" + commonData.getId() + " taskId=" + td.getId() + " num error");
					}
					td.setNum(Integer.parseInt(nums));
					taskList.add(td);
					taskMap.put(td.getId(), td);
				}
			}
		}
	}

	public void startInnerTimer(String cron, String tag) {
		try {
			JobDetail job = JobBuilder.newJob(ActvtInnerTimerJob.class)
					.withIdentity(MessageFormat.format("job_{0}_{1}", getId(), tag), "actvt")
					.usingJobData("actvtId", getId()).usingJobData("tag", tag).build();

			CronTrigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(MessageFormat.format("trigger_{0}_{1}", getId(), tag), "actvt")
					.withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();

			StdSchedulerFactory.getDefaultScheduler().scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			GameLog.error("startInnerTimer exception:", e);
		}
	}

	public void innerTimerCB(String tag) {

	}

	public void stopInnerTimer(String tag) {
		try {
			StdSchedulerFactory.getDefaultScheduler()
					.deleteJob(JobKey.jobKey(MessageFormat.format("job_{0}_{1}", getId(), tag), "actvt"));
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public int getId() {
		return commonData.getId();
	}

	public void sendEmail(long joyId, String content, ActvtReward reward) {
		if (reward != null) {
			List<BriefItem> annex = new ArrayList<BriefItem>();
			Map<String, Integer> items = reward.getRewardMap();
			for (Map.Entry<String, Integer> entry : items.entrySet()) {
				String itemId = entry.getKey();
				int num = entry.getValue();
				BriefItem bri = new BriefItem("test", itemId, num);
				annex.add(bri);
			}
			chatMgr.creatSystemEmail(MsgTitleType.MSG_TITLE_GENERAL_SYSTEM, content, annex, joyId);
		} else {
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
		try {
			String times = value;

			JobDetail job = JobBuilder.newJob(ActvtTickJob.class)
					.withIdentity(String.format("job_tick_%s", commonData.getId()), "actvt")
					.usingJobData("actvtId", commonData.getId()).build();

			CronTrigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(String.format("trigger_tick_%s", commonData.getId()), "actvt")
					.withSchedule(CronScheduleBuilder.cronSchedule(times)).build();

			StdSchedulerFactory.getDefaultScheduler().scheduleJob(job, trigger);
		} catch (Exception e) {
			GameLog.error("startTick exception:", e);
		}
	}

	public void rewardPlayer(long joyId, String reward) {
		Role role = world.getRole(joyId);
		if (role != null) {
			rewardPlayer(role, reward);
		} else {
			// SN 错误处理
		}
	}

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
				GameLog.error("actvt id=" + commonData.getId() + " type=" + commonData.getType()
						+ " rewardPlayer joyId=" + role.getId() + " reward=" + reward + " doesn't exist");
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

	public void taskEvent(Object... datas) {
	}

	public void tick() {
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

	@Override
	public void execute(String event, Object... datas) {

	}

	public int getReceiveableNum(long joyId) {
		return 0;
	}

	public String getStateStr() {
		return "";
	}

	public String getStateStrZip() {
		try {
			String str = getStateStr();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			GZIPOutputStream gos = new GZIPOutputStream(baos);
			gos.write(str.getBytes(), 0, str.length());
			gos.finish();
			gos.flush();
			gos.close();

			byte[] smallData = baos.toByteArray();
			return Hex.encodeHexString(smallData);
		} catch (Exception e) {
			GameLog.error(
					"Actvt type=" + commonData.getId() + " getStateStrZip exception=" + ExceptionUtils.getMessage(e));
			return "";
		}
	}

	@Override
	public void saveToData(SqlData data) {
		data.put(RED_ALERT_GENERAL_ID, commonData.getId());
		data.put(RED_ALERT_GENERAL_TYPE, commonData.getType());
		data.put(RED_ALERT_GENERAL_STATE, state.ordinal());
		data.put(TABLE_RED_ALERT_ACTVT_STATES, getStateStrZip());
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
		
//		Map<String, Object> map = new HashMap<String, Object>();
//		dbMgr.getGameDao().saveDaoData(this,map);//活动是及时保存，不进保存队列
		taskPool.saveThread.addSaveData(this);
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

	String[] tmpStateStrs = { "" };

	protected String[] getStateStrs(SqlData data) {
		try {
			String stateStr = data.getString(TABLE_RED_ALERT_ACTVT_STATES);
			stateStr = decompress(Hex.decodeHex(stateStr.toCharArray()));
			return stateStr.split(STATE_STR_SPLIT_CH);
		} catch (Exception e) {
			return tmpStateStrs;
		}
	}
}
