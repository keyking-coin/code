package com.keyking.coin.util;

import java.sql.Timestamp;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeUtils {

	public static final DateTimeZone tz = DateTimeZone.forTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));//设置时区为北京
	
	private static final DateTimeFormatter FORMAT_MONTH   = DateTimeFormat.forPattern("yyyyMM");
	
	private static final DateTimeFormatter FORMAT_YEAR    = DateTimeFormat.forPattern("yyyy");
	
	private static final DateTimeFormatter FORMAT_DAY     = DateTimeFormat.forPattern("yyyy-MM-dd");
	
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
	
	public static synchronized String enDate(long time) {
		return getTime(time).toString(FORMAT_EN_YEAR);
	}
	
	public static synchronized String chDate(long time) {
		return getTime(time).toString(FORMAT_CH_YEAR);
	}
	
	/**
	 * 是否是同一天
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean isSameDay(long start,long end){
		DateTime startDate = TimeUtils.getTime(start);
		DateTime endDate   = TimeUtils.getTime(end); 
		String d1 = formatDay(startDate);
		String d2 = formatDay(endDate);
		return d1.equals(d2);
	}
	
	
	/**
	 * 是否和系统时间是同一天
	 * @param time
	 * @return
	 */
	public static boolean isSameDay(long time){
		return isSameDay(now().getMillis(),time);
	}
	
	/**
	 * 是否是同一周
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean isSameWeek(long time1 ,long time2){
		DateTime startDate = TimeUtils.getTime(time1);
		int year1  = startDate.getYear();
		int month1 = startDate.getMonthOfYear();
		int week1  = startDate.getWeekOfWeekyear();
		DateTime endDate   = TimeUtils.getTime(time2);
		int year2  = endDate.getYear();
		int month2 = endDate.getMonthOfYear();
		int week2  = endDate.getWeekOfWeekyear();
		return year1 == year2 && month1 == month2 && week1 == week2;
	}
	
	/**
	 * 与现在是否同一周
	 * @param end
	 * @return
	 */
	public static boolean isSameWeek(long time){
		DateTime startDate = now();
		int year1  = startDate.getYear();
		int month1 = startDate.getMonthOfYear();
		int week1  = startDate.getWeekOfWeekyear();
		DateTime endDate   = TimeUtils.getTime(time);
		int year2  = endDate.getYear();
		int month2 = endDate.getMonthOfYear();
		int week2  = endDate.getWeekOfWeekyear();
		return year1 == year2 && month1 == month2 && week1 == week2;
	}
	
	/**
	 * 是不是和系统时间是同一个月
	 * @param end
	 * @return
	 */
	public static boolean isSameMonth(long time){
		DateTime startDate = now();
		DateTime endDate   = TimeUtils.getTime(time); 
		return startDate.toString(FORMAT_MONTH).equals(endDate.toString(FORMAT_MONTH));
	}
	
	/**
	 * 判断现在是否在指定的时间
	 * @param start  小时
	 * @param end	  小时
	 * @return
	 */
	public static boolean isInside(int start,int end){
		int now = now().getHourOfDay();
		return now >= start && now < end;
	}
	
	/**
	 * 获得周几
	 * @return
	 */
	public static int getWeek(){
		return now().getDayOfWeek();
	}
	
	/**
	 * 在指定的周几
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
	public static DateTime getTime(String str){
		return  DateTime.parse(str,FORMAT_CH_YEAR);  
	}
	
	/**
	 * 获取系统当时间的字符串
	 * @return
	 */
	public static String nowChStr(){
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
	 * 返回两个日期之间的全部日期字符串
	 * @param start
	 * @param end
	 * @return
	 */
	public static String[] getDays(String start,String end){
		
		DateTime startDate = TimeUtils.getTime(start);
		DateTime endDate   = TimeUtils.getTime(end);
		Days _days = Days.daysBetween(startDate,endDate);
		int num = _days.getDays();
		String days[] = new String[num+1];
		days[0] = start;
		for(int i = 1 ; i < days.length ; i++){
			DateTime d = startDate.plusDays(i);
			days[i]  = d.toString(FORMAT_DAY);
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
	 * 增加秒数
	 * @param time
	 * @return
	 */
	public static synchronized Timestamp addSecond(long paramLong , int paramInt) {
		return new Timestamp(paramLong + paramInt*1000);
	}	
	
	public static void main(String[] args) {
		System.out.println(isWeekDay(1,6));
	}
}
 
