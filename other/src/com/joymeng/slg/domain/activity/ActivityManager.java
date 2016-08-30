package com.joymeng.slg.domain.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.joymeng.Instances;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.services.utils.XmlUtils;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.activity.data.Activity;
import com.joymeng.slg.domain.activity.data.ActivityElement;
import com.joymeng.slg.domain.activity.data.ActivityEntity;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.shop.data.ShopLayout;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.world.TaskPool;

public class ActivityManager implements Instances {
	private static ActivityManager instance = new ActivityManager();
	List<Activity> activitys = new CopyOnWriteArrayList<Activity>();
	
	public static ActivityManager getInstance(){
		return instance;
	}
	
	public void loadFromDataBase() throws Exception{
		GameLog.info("load all alive activity from database");
		List<Map<String, Object>> datas = dbMgr.getGameDao().getDatasByTableName(DaoData.TABLE_RED_ALERT_ACTIVITY);
		if (datas != null) {
			for (Map<String, Object> map : datas) {
				Activity activity = new Activity();
				activity.loadFromData(new SqlData(map));
				if (activity.couldAlive()){
					activity.loadEntity();
					activitys.add(activity);
				}
			}
		}
		//预加载活动系统
		taskPool.scheduleAtFixedRate(null,new Runnable() {
			@Override
			public void run() {
				loadFromFile();
			}
		}, 0 , 2 * TaskPool.SECONDS_PER_HOUR , TimeUnit.SECONDS);
	}
	
	public Document getDocumentAndClose(File file) throws Exception{
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
	
	public void loadFromFile(){
		try {
			File file = new File("./activity/prepare/Activity.xml");
			if (!file.exists()){
				return;
			}
	        Document document = getDocumentAndClose(file);
			Element base = document.getDocumentElement();
			Element[] elements = XmlUtils.getChildrenByName(base,"Activity");
			for (int i = 0 ; i < elements.length ; i++) {
				Element element = elements[i];
				Activity activity = new Activity();
				activity.decode(element);
				ActivityOperationType aot = ActivityOperationType.search(activity.getOperation());
				switch(aot){
					case ACTIVITY_OPERATION_TYPE_INSERT:{
						if (activity.couldAlive()){
							activity.moveFileRun();
							activity.loadEntity();
							activitys.add(activity);
						}
						break;
					}
					case ACTIVITY_OPERATION_TYPE_DELETE:{
						Activity del = searchActivity(activity.getId());
						if (del != null){
							activity.moveFileHistory();
							activitys.remove(del);
						}
						break;
					}
					case ACTIVITY_OPERATION_TYPE_UPDATE:{
						Activity ole = searchActivity(activity.getId());
						ole.copy(activity);
						activity.moveFileRun();
						ole.loadEntity();
						break;
					}
				}
			}
			save();
			moveFileHistory();
		} catch (Exception e) {
			GameLog.error("load activity from file error",e);
		}
	}
	
	public void copyFile(File src,File out){
		try {
			FileInputStream fis   = new FileInputStream(src);
			FileOutputStream fos = new FileOutputStream(out);
			byte[] result = new byte[fis.available()];
			fis.read(result);
			fos.write(result);
			fis.close();
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void moveFileHistory(){
		String tail = TimeUtils.nowStr().replaceAll(":","-");
		File src = new File("./activity/prepare/Activity.xml");
		File out = new File("./activity/history/Activity.xml." + tail);
		copyFile(src,out);
		src.delete();
	}
	
	public Activity searchActivity(String id){
		for (int i = 0 ; i < activitys.size() ; i++){
			Activity activity = activitys.get(i);
			if (activity.getId().equals(id)){
				return activity;
			}
		}
		return null;
	}
	
	
	public <T extends ActivityElement> T searchActivityElement(String aId,String eid){
		Activity activity = searchActivity(aId);
		if (activity != null){
			return activity.searchElement(eid);
		}
		return null;
	}
	
	/**
	 * 当前激活的活动
	 * @return
	 */
	public List<ActivityEntity> getAlives(){
		List<ActivityEntity> alives = new ArrayList<ActivityEntity>();
		for (int i = 0 ; i < activitys.size() ; i++){
			Activity activity = activitys.get(i);
			if (activity.isAlive()){
				alives.add(activity.getEntity());
			}
		}
		return alives;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ActivityElement> List<T> searchElements(String module){
		List<T> result = new ArrayList<T>();
		List<ActivityEntity> alives = getAlives();
		for (int i = 0 ; i < alives.size() ; i++){
			ActivityEntity entity = alives.get(i);
			if (entity.getModule().equals(module)){
				for (int j = 0 ; j < entity.getElements().size() ; j++){
					ActivityElement element = entity.getElements().get(j);
					result.add((T)element);
				}
			}
		}
		return result;
	}
	
	public void sendShopLayoutToClient(RespModuleSet rms , Role role){
		List<ShopLayout> sls = searchElements(ActivityModule.ACTIVITY_MODULE_NAME_SHOP.getKey());
		for (int i = 0 ; i < sls.size() ; i++){
			ShopLayout sl = sls.get(i);
			if (sl.check(role)){//找到合适这个玩家的商店活动
				ShopLayout nsl = new ShopLayout();
				sl.copy(nsl,role);
				AbstractClientModule module = new AbstractClientModule(){
					@Override
					public short getModuleType() {
						return NTC_DTCD_ROLE_SHOP_LAYOUT;
					}
				};
				module.add(nsl);
				rms.addModule(module);
				break;
			}
		}
	}
	
	public void save(){
		for (int i = 0 ; i < activitys.size() ; i++){
			Activity activity = activitys.get(i);
			activity.save();
		}
	}
}
