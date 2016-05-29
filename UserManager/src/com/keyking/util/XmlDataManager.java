package com.keyking.util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlDataManager implements Instances{
	
	private static String savePath = null;
	
	public static void load(String path) {
		if (savePath == null){
			savePath = path;
		}
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
			ServerLog.error("game data load error!",e);
			System.exit(0);
		}
	}
	
	public static String getPath() {
		return savePath;
	}

	public static void reload() {
		clear();
		load(savePath);
	}
	
	public static List<Object> loadData(String path, Class<?> clazz,boolean readVersion) throws ClassNotFoundException {
		String fileName = clazz.getSimpleName();
		List<Class<?>> classes = searchAllClassFromMe(clazz);
		ServerLog.info("loadData from " + fileName + "<<<<<<<<<<<<<<<<<<<<<<<<<<");
		File file = new File(path + "/" + fileName + ".xml");
		Document d;
		List<Object> list = new ArrayList<Object>();
		try {
			d = XmlUtils.load(file);
			Element[] elements = XmlUtils.getChildrenByName(d.getDocumentElement(),fileName);
			for (Element element : elements) {
				Object data = tryToGetObject(element,clazz,classes);
				list.add(data);
			}
		} catch (Exception e) {
			ServerLog.error("error in load " + fileName , e);
			//e.printStackTrace();
			System.exit(0); 
		}
		return list;
	}
	
	private static List<Class<?>> searchAllClassFromMe(Class<?> clazz){
		List<Class<?>> result = new ArrayList<Class<?>>();
		Field[] fields  = clazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			Class<?> type = field.getType();
			if  (!type.isPrimitive() && type != String.class){
				List<Class<?>> temps = searchAllClassFromMe(type);
				for (Class<?> t : temps) {
					if (!result.contains(t)){;
						result.add(t);
					}
				}
				if (!result.contains(type)){;
				result.add(type);
				}
			}
		}
		return result;
	}
	
	private static boolean isInClassList(Class<?> T , List<Class<?>> classes){
		for (Class<?> clazz : classes){
			if (T == clazz){
				return true;
			}
		}
		return false;
	}
	
	private static Object tryToGetObject(Element element , Class<?> T , List<Class<?>> classes) throws Exception{
		Object data = T.newInstance();
		Field[] fs  = T.getDeclaredFields();
		for (int i = 0; i < fs.length; i++) {
			Field field = fs[i];
			field.setAccessible(true);
			Class<?> type = field.getType();
			try {
				if (isInClassList(type,classes)){
					Element child = XmlUtils.getChildByName(element,type.getSimpleName());
					if (child == null){
						continue;
					}
					Object obj = tryToGetObject(child,type,classes);
					field.set(data,obj);
				}else{
					String str = XmlUtils.getAttribute(element,field.getName());
					if (StringUtil.isNull(str)) {
						continue ;
					}
					if (type == int.class || type == Integer.class) {
						field.set(data,Integer.parseInt(str));
					} else if (type == long.class || type == Long.class) {
						field.set(data,Long.parseLong(str));
					} else if (type == boolean.class || type == Boolean.class) {
						field.set(data,Boolean.parseBoolean(str));
					} else if (type == byte.class || type == Byte.class) {
						field.set(data,Byte.parseByte(str));
					} else if (type == String.class) {
						field.set(data,str);
					} else if (type == Short.class || type == short.class) {
						field.set(data,Short.parseShort(str));
					} else if (type == float.class || type == Float.class) {
						field.set(data,Float.parseFloat(str));
					}else if (type == double.class || type == double.class) {
						field.set(data,Double.parseDouble(str));
					}
				}
			} catch (Exception e) {
				String head = "<" + T.getSimpleName() + ">";
				String end  = " </" + T.getSimpleName() + ">";
				ServerLog.info("load " + head + " where key = " + field.getName() + end);
				throw e;
			}
		}
		return data;
	}
	
	private static void clear(){
		
	}
}
