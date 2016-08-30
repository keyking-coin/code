package com.joymeng.slg.domain.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.joymeng.Const;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.utils.XmlUtils;
import com.joymeng.slg.exp.DataSearchParamError;


public class DataManager {
	
	public interface DataKey {
		public Object key();
	}
	
	private static DataManager instance = new DataManager();
	
	private  Map<Class<? extends DataKey>,Map<Object,DataKey>> datas = new ConcurrentHashMap<Class<? extends DataKey>,Map<Object,DataKey>>();
	
	public static DataManager getInstance() {
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public void load(boolean start){
		try {
			Document document = XmlUtils.load(Const.CONF_PATH + "JsonDatas.xml");
			Element element = document.getDocumentElement();
			Element[] elements = XmlUtils.getChildrenByName(element,"JsonData");
			for (int i = 0; i < elements.length ; i++) {
				String classStr = XmlUtils.getAttribute(elements[i],"class");
				Class<? extends DataKey> clazz = (Class<? extends DataKey>)Class.forName(classStr);
				load(clazz);
			}
		} catch (Exception e) {
			GameLog.error("load json files error",e);
			if (start){
				System.exit(0);
			}
		}
	}
	
	

	public <T extends DataKey> void load(Class<T> clazz) throws Exception{
		Map<Object,DataKey> map = datas.get(clazz);
		if (map == null){
			map = new ConcurrentHashMap<Object,DataKey>();
			datas.put(clazz,map);
		}
		String fileName = clazz.getSimpleName() + ".json";
		GameLog.info("load file >>> " + fileName);
		File file = new File(Const.RES_PATH + fileName);
		InputStream in = new FileInputStream(file);
		JoyBuffer buffer = JoyBuffer.allocate(1024);
		byte[] data = new byte[1024];
		while(true){
			int len = in.read(data);
			if (len == -1){
				break;
			}
			buffer.put(data,0,len);
		}
        in.close();
        String str = new String(buffer.arrayToPosition());
        List<T> temps = JsonUtil.JsonToObjectList(str,clazz);
		for (int i = 0 ; i < temps.size() ; i++){
			T t = temps.get(i);
    		map.put(t.key(),t);
    	}
	}
	
	/**
	 * 使用的时候如果只通过主键查找就params的第一个参数一定要是DataKey的
	 * @param clazz
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends DataKey> T serach(Class<? extends DataKey> clazz ,Object... params){
		if (params == null){
			DataSearchParamError error = new DataSearchParamError();
			GameLog.error(error.getMessage(),error);
			return null;
		}
		Map<Object,DataKey> map = datas.get(clazz);
		if (map == null){
			return null;
		}
		if (params.length == 1 && !(params[0] instanceof SearchFilter)){
			return (T)map.get(params[0]);
		}
		for (Object obj : map.values()){
			boolean flag  = true;
			T t = (T)obj;
			for (int i = 0 ; i < params.length ; i++){
				Object param = params[i];
				if (param instanceof SearchFilter){
					flag = ((SearchFilter<T>)param).filter(t);
				}
			}
			if (flag){
				return t;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends DataKey> List<T> serachList(Class<? extends DataKey> clazz,Object... params){
		Map<Object,DataKey> map = datas.get(clazz);
		if (map == null){
			return null;
		}
		List<T> result = new ArrayList<T>();
		for (DataKey data : map.values()){
			boolean flag  = true;
			if (params != null){
				for (int i = 0 ; i < params.length ; i++){
					Object obj = params[i];
					if (obj instanceof SearchFilter){
						flag = ((SearchFilter<T>)obj).filter((T)data);
					}else{
						flag = data.key().equals(obj);
					}
				}
			}
			if (flag){
				result.add((T)data);
			}
		}
		return result;
	}
}
