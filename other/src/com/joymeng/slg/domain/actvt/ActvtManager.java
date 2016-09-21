package com.joymeng.slg.domain.actvt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.joda.time.DateTime;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.GameLog;
import com.joymeng.services.utils.XmlUtils;
import com.joymeng.slg.dao.DBManager;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.actvt.Actvt.ActvtState;
import com.joymeng.slg.domain.actvt.data.ActvtCommon;
import com.joymeng.slg.domain.actvt.impl.ArmyRebellion;
import com.joymeng.slg.domain.actvt.impl.RushSevenDay;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.mod.ClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.world.TaskPool;
import com.joymeng.slg.world.World;

public class ActvtManager
{
	private static final String PATH = "./activity/";
	private static final String PATH_LOADS = PATH + "loads/";
	private static final String PATH_LOADS_END = PATH_LOADS + "end/";
	private static final String PATH_LOADS_HISTORY = PATH_LOADS + "history/";
	private static final String ACTIVITY_FILE  = "Activitys";
	private static final String ACTIVITY_TAIL = ".xml";
	private static final String ACTIVITY_CLASS_PATH = "com.joymeng.slg.domain.actvt.impl.";
	
	private Map<Integer, Actvt> actvtsAlive = new HashMap<Integer, Actvt>();
	private Map<Integer, Actvt> actvtsDead = new HashMap<Integer, Actvt>();
	private List<String> loadsPath = new ArrayList<String>(); 

	private static ActvtManager instance = new ActvtManager();
	public static ActvtManager getInstance() {
		return instance;
	}
	
	private ActvtManager()
	{
		new File(PATH_LOADS_END).mkdirs();
		new File(PATH_LOADS_HISTORY).mkdirs();
		
		try {
			StdSchedulerFactory.getDefaultScheduler().start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	
	public boolean idExist(int id)
	{
		if (actvtsAlive.containsKey(id)) {
			return true;
		}
		if (actvtsDead.containsKey(id)) {
			return true;
		}
		return false;
	}
	
	public void hideActvt(int id)
	{
		if (!actvtsAlive.containsKey(id)) {
			return;
		}
		Actvt actvt = actvtsAlive.get(id);
		actvtsDead.put(actvt.getId(), actvt);
		actvtsAlive.remove(id);
		
		String fname = String.format("%s_%d%s", actvt.getCommonData().getType(), id, ACTIVITY_TAIL);
		File[] files = new File(PATH_LOADS).listFiles();
		for (int i = 0; i < files.length; i++)
		{
			File f = files[i];
			if (!f.isDirectory() && f.getName().startsWith(fname))
			{
				File dst = new File(PATH_LOADS_END + f.getName());
				f.renameTo(dst);
			}
		}
	}
	
	private void backupLoads()
	{
		String times = DateTime.now().toString("yyMMddHHmmss");
		
		new File(PATH_LOADS_HISTORY + times).mkdirs();
		for (int i = 0; i < loadsPath.size(); i++)
		{
			String srcPath = loadsPath.get(i);
			String dstPath = PATH_LOADS_HISTORY + times + "/" + Paths.get(srcPath).getFileName();
			File src = new File(srcPath);
			File dst = new File(dstPath);
			try {
				Files.copy(src.toPath(), dst.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (i == 0) {
				src.delete();
			}
		}
		
		for (int i = 1; i < loadsPath.size(); i++)
		{
			String srcPath = loadsPath.get(i);
			String dstPath = PATH_LOADS + Paths.get(srcPath).getFileName();
			File src = new File(srcPath);
			File dst = new File(dstPath);
			if (dst.exists()) {
				dst.renameTo(new File(dstPath+".bak" + times));
			}
			src.renameTo(new File(dstPath));
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean load(boolean hotLoad)
	{
		try {
			String actvtPath = PATH + ACTIVITY_FILE + ACTIVITY_TAIL;
			if (!new File(actvtPath).exists()) {
				return false;
			}
			
			loadsPath.clear();
			loadsPath.add(actvtPath);
			Document document = XmlUtils.load(actvtPath);
			Element element = document.getDocumentElement();
			Element[] elements = XmlUtils.getChildrenByName(element,"Actvt");
			for (int i = 0; i < elements.length; i++)
			{
				Element eleActvt = elements[i];
				int id = Integer.parseInt(eleActvt.getAttribute("id"));
				String tp = eleActvt.getAttribute("type");
				
				String operate = eleActvt.getAttribute("operate");
				
				if (operate.equals(ActvtOperateType.NEW.getName()))
				{
					if (idExist(id)) {
						throw new Exception("id="+id+" already exist");
					}
					
					String filePath = String.format("%s%s_%d%s", PATH,tp,id,ACTIVITY_TAIL);
					File f = new File(filePath);
					if (!f.exists() || f.isDirectory()) {
						throw new Exception("id="+id+" file="+tp+" not exist");
					}
					loadsPath.add(filePath);
					
					Document doc = XmlUtils.load(filePath);
					Element eleNew = doc.getDocumentElement();
					String type = eleNew.getAttribute("type");
					Class<? extends Actvt> clazz = (Class<? extends Actvt>)Class.forName(ACTIVITY_CLASS_PATH+type);
					
					Actvt actvt = clazz.newInstance();
					actvt.load(eleNew);
					if (actvt.getId() != id) {
						throw new Exception("id not same with id1="+id+" id2="+actvt.getId());
					}
					actvtsAlive.put(actvt.getId(), actvt);
					actvt.init();
				}
				else if (operate.equals(ActvtOperateType.UPDATE.getName())) 
				{
					Actvt actvt = getActvt(id);
					if (actvt == null) {
						throw new Exception("actvt id="+id+" not exist");
					}
					
					String filePath = String.format("%s%s_%d%s", PATH,tp,id,ACTIVITY_TAIL);
					File f = new File(filePath);
					if (!f.exists() || f.isDirectory()) {
						throw new Exception("id="+id+" file="+tp+" not exist");
					}
					loadsPath.add(filePath);
					
					Document doc = XmlUtils.load(filePath);
					Element eleNew = doc.getDocumentElement();
					actvt.load(eleNew);
					actvt.init();
				}
				else if (operate.equals(ActvtOperateType.DELETE.getName())) {
					Actvt actvt = getActvt(id);
					if (actvt == null) {
						throw new Exception("actvt id="+id+" not exist");
					}
					actvt.end();
					actvt.hide();
					actvtsAlive.remove(id);
					actvtsDead.put(id, actvt);
				}
				else {
					throw new Exception("id="+id+" operate="+operate+" not exist");
				}
				
				GameLog.info("load activity file id="+id+" file="+tp+" done");
			}
		} 
		catch (Exception e) {
			GameLog.error("load activity files error ", e);
			return false;
		}
		
		backupLoads();
		return true;
	}
	
	public void init()
	{
		loadFromDB();
		
		if (!load(false)) {
			return;
		}
		
		long savePeriod = 10 * TaskPool.SECONDS_PER_MINTUE;
		TaskPool.getInstance().scheduleAtFixedRate(null, new Runnable() {
			@Override
			public void run() {
				save();
			}
		}, savePeriod, savePeriod, TimeUnit.SECONDS);
		save();
	}

	public boolean hotLoad()
	{
		boolean ret = load(false);
		if (ret) {
			save();
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public void loadFromDB() 
	{
		List<SqlData> datas = DBManager.getInstance().getGameDao().getDatas(DaoData.TABLE_RED_ALERT_ACTVT);
		if (datas == null) {
			return;
		}
		
		try 
		{
			for (int i = 0 ; i < datas.size() ; i++)
			{
				SqlData data = datas.get(i);
	
				int id = data.getInt(DaoData.RED_ALERT_GENERAL_ID);
				if (idExist(id)) {
					throw new Exception("id="+id+" already exist");
				}
				
				String tp = data.getString(DaoData.RED_ALERT_GENERAL_TYPE);			
				String filePath = String.format("%s%s_%d%s", PATH_LOADS, tp, id, ACTIVITY_TAIL);
				File f = new File(filePath);
				if (!f.exists() || f.isDirectory()) {
					throw new Exception("id="+id+" file="+tp+" not exist");
				}
	
				Document doc = XmlUtils.load(filePath);
				Element eleNew = doc.getDocumentElement();
				String type = eleNew.getAttribute("type");
				Class<? extends Actvt> clazz = (Class<? extends Actvt>)Class.forName(ACTIVITY_CLASS_PATH+type);
				
				Actvt actvt = clazz.newInstance();
				actvt.load(eleNew);
				if (actvt.getId() != id) {
					throw new Exception("id not same with id1="+id+" id2="+actvt.getId());
				}
				int st = data.getInt(DaoData.RED_ALERT_GENERAL_STATE);
				if (st < 0 || st > ActvtState.HIDE.ordinal()) {
					throw new Exception("id ="+id+" state="+st+" is illegal");
				}
				ActvtState state = ActvtState.values()[st];
				if (state == ActvtState.HIDE) 
				{
					actvtsDead.put(actvt.getId(), actvt);
					actvt.loadFromData(data);
				}
				else
				{
					actvtsAlive.put(actvt.getId(), actvt);
					actvt.init();
					actvt.loadFromData(data);
				}
				GameLog.info("load activity from db id="+id+" type="+tp+" done");
			}
		}
		catch (Exception e) {
			
		}
	}
	
	public Actvt getFirstActvt(String type)
	{
		for(Map.Entry<Integer, Actvt> entry : actvtsAlive.entrySet())
		{
			Actvt actvt = entry.getValue();
			if (actvt.getCommonData().getType().equals(type)) {
				return actvt;
			}
		}
		return null;
	}

	public Actvt getActvt(int actvtId) {
		if (actvtsAlive.containsKey(actvtId)) {
			return actvtsAlive.get(actvtId);
		}
		return null;
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
		for (Map.Entry<Integer, Actvt> entry : actvtsAlive.entrySet())
		{
			Actvt actvt = entry.getValue();
			if (!actvt.canShow(joyId)) {
				continue;
			}
			
			ActvtCommon commonData = actvt.getCommonData();
			module.add(commonData.getId());
			module.add(commonData.getType());
			module.add(commonData.getName());
			module.add(commonData.getIcon());
			module.add(actvt.getStateOdinal());
			module.add(commonData.getBriefDesc());
			module.add(actvt.getDestDesc());
			
			module.add(commonData.getType()+commonData.getId());
			module.add(actvt.getStartSeconds());
			module.add(actvt.getLastSeconds());
			module.add(actvt.getNowSeconds());
		}
//		System.out.println(module.getParams().toString());
		return module;
	}

	private ClientModule makeUpActvtDetailModule(Role role, int actvtId)
	{
		ClientMod module = new ClientMod(ClientModule.NTC_DTCD_GET_ACTVT_DETAIL);
		Actvt act = getActvt(actvtId);
		if (act != null) {
			act.makeUpDetailModule(module, role);
			return module;
		}
		return null;
	}
	
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
		for (Map.Entry<Integer, Actvt> entry : actvtsAlive.entrySet())
		{
			if (entry.getValue().canShow(joyId)) {
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
		module.add(getShowActvtsNum(joyId));
		for (Map.Entry<Integer, Actvt> entry : actvtsAlive.entrySet())
		{
			Actvt actvt = entry.getValue();
			if (!actvt.canShow(joyId)) {
				continue;
			}
			
			ActvtCommon commonData = actvt.getCommonData();
			module.add(commonData.getId());
			module.add(commonData.getType());
			module.add(commonData.getType()+commonData.getId());

//			int num = actvt.getReceiveableNum(role.getId());
			int num = 0;
			module.add(num);
			module.add(commonData.getType().equals("RushSevenDay")?1:0);
			
			module.add(commonData.getId()+"_tip");
			if (commonData.getType().equals("RushSevenDay"))
			{
				RushSevenDay rsd = (RushSevenDay)actvt;
				module.add(0L);
				module.add(rsd.isRun(joyId)?rsd.getOneDaySeconds():0L);
				module.add(rsd.isRun(joyId)?(long)rsd.getSeconds(joyId)%rsd.getOneDaySeconds():0L);
			}
			else 
			{
				module.add(actvt.getStartSeconds());
				module.add(actvt.getLastSeconds());
				module.add(actvt.getNowSeconds());
			}
			
		}
//		System.out.println(module.getParams().toString());
		sendReq(role, module);
	}
	
	public void sendActvtListReq(Role role) {
		sendReq(role, makeUpActvtListModule(role.getId()));
	}
	
	public void sendRebellionInfo(Role role, int actvtId) {
		Actvt actvt = ActvtManager.getInstance().getActvt(actvtId);
		if (actvt == null || !actvt.getCommonData().getType().equals("ArmyRebellion")) {
			return;
		}
		ArmyRebellion ar = (ArmyRebellion)actvt;
		ClientMod module = new ClientMod(ClientModule.NTC_DTCD_REBELLION_INFO);
		ar.makeUpInfoModule(module, role);
		sendReq(role, module);
	}
	
	public boolean sendActvtDetailReq(Role role, int actvtId)
	{
		ClientModule mod1 = makeUpActvtDetailModule(role, actvtId);
		if (mod1 == null) {
			return false;
		}
		
		ClientModule mod2 = makeUpActvtRankListModule(role, actvtId);
		if (mod2 != null) {
			sendReq(role, mod1, mod2);
		}
		else {
			sendReq(role, mod1);
		}
		return true;
	}
	
	public boolean sendActvtRankListReq(Role role, int actvtId)	{
		ClientModule mod = makeUpActvtRankListModule(role, actvtId);
		if (mod == null) {
			return false;
		}
		sendReq(role, mod);
		return true;
	}
	
	public boolean receiveActvtReward(Role role, int actvtId, int index)
	{
		Actvt actvt = getActvt(actvtId);
		if (actvt == null) {
			return false;
		}
		if (!actvt.receiveReward(role, index)) {
			return false;
		}
		sendActvtDetailReq(role, actvtId);
		return true;
	}
	
	public boolean manualStartActvt(Role role, int actvtId)
	{
		Actvt actvt = getActvt(actvtId);
		if (actvt == null) {
			return false;
		}
		if (!actvt.manualStart(role)) {
			return false;
		}
		sendRebellionInfo(role, actvtId);
		return true;
	}

	public void save() {
		for (Map.Entry<Integer, Actvt> entry : actvtsAlive.entrySet()) {
			Actvt actvt = entry.getValue();
			actvt.save();
		}
	}
	
	public static void main(String[] args) 
	{
		List<Integer> ints = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++) {
			ints.add(i);
		}
		for (int i = 0; i < ints.size(); ) {
			if (ints.get(i)%2 == 0) {
				ints.remove(i);
			}
			else {
				i++;
			}
		}
		System.out.println(ints);
//		try {
//			int id = 123456;
//			String tag = "tick";
//			JobDetail job = JobBuilder.newJob(ActvtInnerTimerJob.class)
//					.withIdentity(MessageFormat.format("job_{0}_{1}", id, tag), "actvt")
//					.usingJobData("actvtId", id)
//					.usingJobData("tag", tag).build();
//	
//			CronTrigger trigger = TriggerBuilder.newTrigger()
//					.withIdentity(MessageFormat.format("trigger_{0}_{1}", id, tag), "actvt")
//					.withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?")).build();
//
//			StdSchedulerFactory.getDefaultScheduler().scheduleJob(job, trigger);
//			
//			boolean ret = StdSchedulerFactory.getDefaultScheduler().deleteJob(JobKey.jobKey(MessageFormat.format("job_{0}_{1}", 123456, "tick"), "actvt"));
//			System.out.println(ret);
//		} 
//		catch (SchedulerException e) {
//			e.printStackTrace();
//		}
	}
}
