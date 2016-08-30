package com.joymeng.slg.domain.activity.data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.joymeng.Instances;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;

public class Activity implements DaoData,Instances{
	String id;
	String description;
	String fileName;
	boolean deseno;//是否限时
	String startDate;
	String endDate;
	String operation;
	ActivityEntity entity;
	boolean savIng = false;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public boolean isDeseno() {
		return deseno;
	}

	public void setDeseno(boolean deseno) {
		this.deseno = deseno;
	}

	public String getStartDate() {
		return startDate;
	}
	
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	public String getEndDate() {
		return endDate;
	}
	
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public String getOperation() {
		return operation;
	}
	
	public void setOperation(String operation) {
		this.operation = operation;
	}

	public ActivityEntity getEntity() {
		return entity;
	}

	public void setEntity(ActivityEntity entity) {
		this.entity = entity;
	}
	
	public <T extends ActivityElement> T searchElement(String eid) {
		return entity.searchElement(eid);
	}
	
	public boolean isAlive(){
		try {
			DateTime now = TimeUtils.now();
			DateTime start = TimeUtils.getTime(startDate);
			DateTime end = TimeUtils.getTime(endDate);
			return now.isAfter(start) && now.isBefore(end);
		} catch (Exception e) {
			GameLog.error("activity date error",e);
			return false;
		}
	}
	
	/**
	 * 所有激活的和还未激活的活动
	 * @return
	 */
	public boolean couldAlive(){
		if (!deseno){
			return true;
		}
		try {
			DateTime now = TimeUtils.now();
			DateTime end = TimeUtils.getTime(endDate);
			return now.isBefore(end);
		} catch (Exception e) {
			GameLog.error("activity date error",e);
			return false;
		}
	}
	
	public void decode(Element element) throws Exception{
		id          = element.getAttribute("id");
		description = element.getAttribute("description");
		fileName    = element.getAttribute("fileName");
		deseno      = element.getAttribute("deseno").equals("true");
		startDate   = element.getAttribute("startDate");
		endDate     = element.getAttribute("endDate");
		operation   = element.getAttribute("operation");
	}
	
	@Override
	public String table() {
		return TABLE_RED_ALERT_ACTIVITY;
	}

	@Override
	public String[] wheres() {
		return new String[]{RED_ALERT_GENERAL_ID};
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
		if (savIng){
			return;
		}
		savIng = true;
		Map<String, Object> map = new HashMap<String, Object>();
		dbMgr.getGameDao().saveDaoData(this,map);//活动是及时保存，不进保存队列
	}

	@Override
	public void loadFromData(SqlData data) {
		id  = data.getString(RED_ALERT_GENERAL_ID);
		description  = data.getString(RED_ALERT_ACTIVITY_DESCRIPTION);
		fileName     = data.getString(RED_ALERT_ACTIVITY_FILENAME);
		deseno       = data.getByte(RED_ALERT_ACTIVITY_DESENO) == 1;
		startDate    = data.getTimestamp(RED_ALERT_ACTIVITY_STARTDATE).toString().substring(0,19);
		endDate      = data.getTimestamp(RED_ALERT_ACTIVITY_ENDDATE).toString().substring(0,19);
	}

	@Override
	public void saveToData(SqlData data) {
		data.put(RED_ALERT_GENERAL_ID,id);
		data.put(RED_ALERT_ACTIVITY_DESCRIPTION, description);
		data.put(RED_ALERT_ACTIVITY_FILENAME, fileName);
		data.put(RED_ALERT_ACTIVITY_DESENO,deseno ? 1 : 0);
		data.put(RED_ALERT_ACTIVITY_STARTDATE, startDate);
		data.put(RED_ALERT_ACTIVITY_ENDDATE, endDate);
	}

	@Override
	public void over() {
		savIng = false;
	}

	
	@Override
	public boolean saving() {
		return savIng;
	}

	public void copy(Activity activity) {
		id           = activity.id;
		description  = activity.description;
		fileName     = activity.fileName;
		startDate    = activity.startDate;
		endDate      = activity.endDate;
		operation    = activity.operation;
	}
	
	/**
	 * 加载活动实体
	 * @throws Exception 
	 */
	public void loadEntity() throws Exception {
		entity = new ActivityEntity();
		Document document = activityManager.getDocumentAndClose(new File("./activity/run/" + fileName));
		Element base = document.getDocumentElement();
		entity.load(id,base);
	}
	
	
	
	public void moveFileRun() {
		File src = new File("./activity/prepare/" + fileName);
		if (src.exists()){
			File out = new File("./activity/run/" + fileName);
			if (out.exists()){//目标文件已存在
				//将目标文件加时间戳，放到历史里面去
				String tail = TimeUtils.nowStr().replaceAll(":","-");
				File his = new File("./activity/history/" + fileName + ".run." + tail);
				activityManager.copyFile(out,his);
			}
			activityManager.copyFile(src,out);
			src.delete();
		}
	}
	
	public void moveFileHistory() {
		String tail = TimeUtils.nowStr().replaceAll(":","-");
		File src1 = new File("./activity/prepare/" + fileName);
		File src2 = new File("./activity/run/" + fileName);
		File out1 = new File("./activity/history/" + fileName + ".prepare." + tail);
		File out2 = new File("./activity/history/" + fileName + ".run." + tail);
		if (src1.exists()){
			activityManager.copyFile(src1,out1);
			src1.delete();//删除源文件
		}
		if (src2.exists()){
			activityManager.copyFile(src2,out2);
			src2.delete();//删除源文件
		}
	}
}
