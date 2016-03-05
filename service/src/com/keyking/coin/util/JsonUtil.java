package com.keyking.coin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonUtil {
	/**
	 * //详细地址:
	 * http://code.alibabatech.com/wiki/display/FastJSON/Tutorial      
	 */
	
	public static final SerializerFeature[] features = { 
//      SerializerFeature.UseSingleQuotes,//使用单引号  
//		SerializerFeature.UseISO8601DateFormat,//Date使用ISO8601格式输出
//		SerializerFeature.WriteMapNullValue, // 空格是否输出。大多数情况
//      SerializerFeature.WriteNullListAsEmpty, // list字段如果为null，输出为[],不是null
//      SerializerFeature.WriteNullNumberAsZero, // 数子字段如果为null，输出为0,不是null
//      SerializerFeature.WriteNullBooleanAsFalse, // Boolean字段如果为null，输出为false,不是null
//      SerializerFeature.WriteNullStringAsEmpty, // 字符类型字段如果为null，输出为"",不是null
        SerializerFeature.PrettyFormat,//漂亮的格式
//      SerializerFeature.WriteTabAsSpecial, //把\t做转义输出
//      SerializerFeature.WriteClassName,//显示class信息
     };
	/**
	 * 还原成对象list
	 * 
	 * @param jsonString
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> JsonToObjectList(String jsonString, Class<T> clazz) {
		try {
			if (StringUtil.isNull(jsonString)) {
				ServerLog.info("jsonString=" + jsonString + " 错误");
				return new ArrayList<T>();
			} else {
				if (null == JSONArray.parseArray(jsonString,clazz)) {
					ServerLog.info("jsonString=" + jsonString + " 不符合json格式");
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

	/**
	 * 返回对应对象
	 * 
	 * @param jsonString
	 * @param clazz
	 * @return
	 */
	public static <T> T JsonToObject(String jsonString, Class<T> clazz) {
		try {
			if (StringUtil.isNull(jsonString)) {
				ServerLog.info("jsonString=" + jsonString + " 错误");
				return null;
			} else {
				if (JSON.parseObject(jsonString,clazz) != null) {
					return JSON.parseObject(jsonString,clazz);
				} else {
					ServerLog.info("jsonString=" + jsonString + " 不符合json格式");
					return null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	/**
	 * 返回特殊map
	 * @param jsonString
	 * @param clazz1
	 * @param clazz2
	 * @param clazz3
	 * @return
	 */
	public static <K,E,V> Map<K,Map<E,V>> JsonToObjectMap_Map(String jsonString,Class<K> clazz1,Class<E> clazz2,Class<V> clazz3){
		if (StringUtil.isNull(jsonString)) {
			ServerLog.info("jsonString=" + jsonString + " 不是json对象");
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
	
	/**
	 * 返回特殊map
	 * @param jsonString
	 * @param clazz1
	 * @param clazz2
	 * @return
	 */
	public static <K,V> Map<K,List<V>> JsonToObjectMap_List(String jsonString,Class<K> clazz1,Class<V> clazz2){
		if (StringUtil.isNull(jsonString)) {
			ServerLog.info("jsonString=" + jsonString + " 不是json对象");
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
	
	/**
	 * 返回map类型的对像
	 * @param jsonString
	 * @param clazz1
	 * @param clazz2
	 * @return
	 */
	public static <K,V> Map<K,V> JsonToObjectMap(String jsonString,Class<K> clazz1,Class<V> clazz2){
		if (StringUtil.isNull(jsonString)) {
			ServerLog.info("jsonString=" + jsonString + " 不是json对象");
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
	
	/**
	 * 将对象编程字符串
	 * @param clazz
	 * @return
	 */
	public static String ObjectToJsonString(Object obj) {
		if (obj == null) {
			return "";
		}
		return JSON.toJSONString(obj);
	}

	public static void main(String[] args) {
		
	}
}
