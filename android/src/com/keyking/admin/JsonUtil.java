package com.keyking.admin;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonUtil {
	/**
	 * //璇︾粏鍦板潃:
	 * http://code.alibabatech.com/wiki/display/FastJSON/Tutorial      
	 */
	
	public static final SerializerFeature[] features = { 
        SerializerFeature.PrettyFormat
     };
	
	public static <T> List<T> JsonToObjectList(String jsonString, Class<T> clazz) {
		try {
			if (StringUtil.isNull(jsonString)) {
				Log.i("JSON","jsonString=" + jsonString + " 格式不对");
				return new ArrayList<T>();
			} else {
				if (null == JSONArray.parseArray(jsonString,clazz)) {
					return new ArrayList<T>();
				} else {
					return JSONArray.parseArray(jsonString,clazz);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> T JsonToObject(String jsonString, Class<T> clazz) {
		try {
			if (StringUtil.isNull(jsonString)) {
				Log.i("JSON","jsonString=" + jsonString + " 格式不对");
				return null;
			} else {
				return JSON.parseObject(jsonString,clazz);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public static <K,E,V> Map<K,Map<E,V>> JsonToObjectMap_Map(String jsonString,Class<K> clazz1,Class<E> clazz2,Class<V> clazz3){
		if (StringUtil.isNull(jsonString)) {
			Log.i("JSON","jsonString=" + jsonString + " 格式不对");
			return new HashMap<K,Map<E,V>>();
		}
		Map<K,Map<E,V>> map = null;
		try {
			map = JSON.parseObject(jsonString,new TypeReference<Map<K,Map<E,V>>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
		
	}
	
	
	public static <K,V> Map<K,List<V>> JsonToObjectMap_List(String jsonString,Class<K> clazz1,Class<V> clazz2){
		if (StringUtil.isNull(jsonString)) {
			Log.i("JSON","jsonString=" + jsonString + " 格式不对");
			return new HashMap<K,List<V>>();
		}
		Map<K,List<V>> map = null;
		try {
			map = JSON.parseObject(jsonString,new TypeReference<Map<K,List<V>>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
		
	}
	
	
	public static <K,V> Map<K,V> JsonToObjectMap(String jsonString,Class<K> clazz1,Class<V> clazz2){
		if (StringUtil.isNull(jsonString)) {
			Log.i("JSON","jsonString=" + jsonString + " 格式不对");
			return new HashMap<K, V>();
		}
		Map<K,V> result = null;
		try {
			result = JSON.parseObject(jsonString,new TypeReference<Map<K,V>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String ObjectToJsonString(Object obj) {
		if (obj == null) {
			return "";
		}
		return JSON.toJSONString(obj);
	}
}
