package com.joymeng.common.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.joymeng.Const;

/**
 * 基于joda-time的工具方法
 * 参考资料 
 * http://blog.csdn.net/dhdhdh0920/article/details/7415359
 * http://persevere.iteye.com/blog/1755237
 * timezone 资料http://joda-time.sourceforge.net/timezones.html
 * @author admin
 *
 */
public class TimeUtils {

	public static final DateTimeZone tz =DateTimeZone.forTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));//设置时区为北京时间
	
	private static final DateTimeFormatter FORMAT_MONTH= DateTimeFormat.forPattern("yyyyMM");//自定义日期格式
	private static final DateTimeFormatter FORMAT_YEAR= DateTimeFormat.forPattern("yyyy");//自定义日期格式
	private static final DateTimeFormatter FORMAT_DAY= DateTimeFormat.forPattern("yyyy-MM-dd");//自定义日期格式
	private static final DateTimeFormatter FORMAT_CH_YEAR = DateTimeFormat .forPattern("yyyy-MM-dd HH:mm:ss");  
	private static final DateTimeFormatter FORMAT_EN_YEAR = DateTimeFormat.forPattern("HH:mm:ss yyyy-MM-dd");//自定义日期格式
	
	public static String formatDay(DateTime dt){
		return dt.toString(FORMAT_DAY);
	}
	
	public static String formatDay(long time){
		return formatDay(getTime(time));
	}
	
	public static String formatYear(DateTime dt){
		return dt.toString(FORMAT_CH_YEAR);
	}
	
	public static String formatYear(long time){
		return formatYear(getTime(time));
	}
	
	public static String formatAnnum(long time){
		return formatAnnum(getTime(time));
	}
	
	public static String formatAnnum(DateTime dt){
		return dt.toString(FORMAT_YEAR);
	}
	
	/**
	 * 检测是否是同一天
	 * @param time
	 * @return
	 */
	public static boolean isSameDay(long start,long end){
		DateTime startDate =TimeUtils.getTime(start);
		DateTime endDate = TimeUtils.getTime(end); 
		return isSameDay(startDate,endDate);
	}
	
	
	/***
	 * 检测是否是同一天
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean isSameDay(DateTime time1 ,DateTime time2){
		String d1= formatDay(time1);
		String d2= formatDay(time2);
		if(d1.equals(d2)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 检测是否是同一天
	 * @param time
	 * @return
	 */
	public static boolean isSameDay(long time){
		return isSameDay(now().getMillis(),time);
	}
	
	/**
	 * 检测是否是同一天
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean isSameWeek(long start,long end){
		DateTime startDate =TimeUtils.getTime(start);
		DateTime endDate = TimeUtils.getTime(end); 
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTimeInMillis(start);
		cal2.setTimeInMillis(end);
		//判断是否同年同月
		if(startDate.toString(FORMAT_MONTH).equals(endDate.toString(FORMAT_MONTH))){
			if(cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)){
				return true;
			}
			return false;
		}else{
			return false;
		}
		
	}
	
	/**
	 * 与现在是否同一周
	 * @param end
	 * @return
	 */
	public static boolean isSameWeek(long time){
		DateTime startDate = now();
		DateTime endDate = TimeUtils.getTime(time); 
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal2.setTimeInMillis(time);
		//判断是否同年
		if(startDate.toString(FORMAT_YEAR).equals(endDate.toString(FORMAT_YEAR))){
			if(cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)){
				return true;
			}
			return false;
		}else{
			return false;
		}
		
	}
	
	/**
	 * 与现在是否同一月
	 * @param end
	 * @return
	 */
	public static boolean isSameMonth(long time){
		DateTime startDate = now();
		DateTime endDate = TimeUtils.getTime(time); 
		//判断是否同年同月
		if(startDate.toString(FORMAT_MONTH).equals(endDate.toString(FORMAT_MONTH))){			
			return true;	
		}else{
			return false;
		}
		
	}
	
	/**
	 * 判断现在是否在指定的时间段
	 * @param start  小时
	 * @param end	  小时
	 * @return
	 */
	public static boolean isInside(int start,int end){
		int now = now().getHourOfDay();
		if(now >= start && now <end){
			return true;
		}
		return false;
	}
	
	/**
	 * 获得每周周几
	 * @return
	 */
	public static int getWeek(){
		return now().getDayOfWeek();
	}
	
	/**
	 * 在指定的周几时间内
	 * @param par
	 * @return
	 */
	public static boolean isWeekDay(int... par){
		int wd = getWeek();
		for(int i : par){
			if(wd == i){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 根据long获得时间
	 * @param time
	 * @return
	 */
	public static DateTime getTime(long time){
		return new DateTime(time);
	}
	
	/**
	 * 根据字符串获得时间
	 * @param str
	 * @return
	 */
	public static DateTime getDayTime(String str){
		return  DateTime.parse(str,FORMAT_DAY);  
	}
	
	/**
	 * 根据字符串获得时间
	 * @param str
	 * @return
	 */
	public static DateTime getTime(String str){
		return  DateTime.parse(str, FORMAT_CH_YEAR);  
	}
	
	/**
	 * 获得当前时间
	 * @return
	 */
	public static String nowStr(){
		return chDate(nowLong());
	}
	
	/**
	 * 获得当前时间
	 * @return
	 */
	public static DateTime now(){
		return DateTime.now(tz);
	}
	
	/**
	 * 获得当前时间
	 * @return
	 */
	public static long nowLong(){
		return now().getMillis();
	}
	
	/**
	 * 返回两个日期之间的全部日期
	 * @param start
	 * @param end
	 * @return
	 */
	public static String[]getDays(String start,String end){
		DateTime startDate=TimeUtils.getTime(start);
		DateTime endDate=TimeUtils.getTime(end);
		Days _days=Days.daysBetween(startDate, endDate);
		int num=_days.getDays();
		String days[]=new String[num+1];
		days[0]=start;
		for(int i=1;i<days.length;i++){
			DateTime d=startDate.plusDays(i);
			days[i]=d.toString(FORMAT_DAY);
		}
		return days;
	}
	
	/**
	 * 将字符串时间转化为long
	 * @param time
	 * @return
	 */
	public static long getTimes(String time){
		return getTime(time).getMillis();
	}
	
	/**
	 * 英文时间显示
	 * @param time
	 * @return
	 */
	public static synchronized String enDate(long time) {
		return getTime(time).toString(FORMAT_EN_YEAR);
	}
	/**
	 * 中文时间显示
	 * @param time
	 * @return
	 */
	public static synchronized String chDate(long time) {
		return getTime(time).toString(FORMAT_CH_YEAR);
	}
	/**
	 * 争夺战开始结束时间
	 * @param time
	 * @return
	 */
	public static int getWarTime(String hour){
		String day = now().toString(FORMAT_DAY);
		String all = day + hour;
		return (int)(getTime(all).getMillis()/1000);
	}
	
	//public static int getOnly
	
	
	
	/**
	 * 增加秒数
	 * @param time
	 * @return
	 */
	public static synchronized Timestamp addSecond(long paramLong,int paramInt) {
		return new Timestamp(paramLong + paramInt*1000);
	}
	
	/**
	 * 判断当前时间是否在两个时间段内 
	 * @param time
	 * @return
	 */
	public static boolean timeIsBetween(String start, String end) {
		long startTime = getTimes(start);
		long endTime = getTimes(end);
		long now = nowLong();
		if (now >= startTime && now <= endTime) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 得到当前时间整点2016-03-29 01:00:00
	 * @param time
	 * @return
	 */
	public static String getIntegral() {
		long now = nowLong();
		DateTime time = now();
		int minute = time.getMinuteOfHour();
		int second = time.getSecondOfMinute();
		return chDate(now - Const.MINUTE * minute - Const.SECOND * second);
	}
	
	
	/**
	 * 返回昨天的日期 2016-07-03
	 * @param time
	 * @return
	 */
	public static String yesterday() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		String yesterday = new SimpleDateFormat("yyyy-MM-dd ").format(cal
				.getTime());
		return yesterday;
	}
	
}
