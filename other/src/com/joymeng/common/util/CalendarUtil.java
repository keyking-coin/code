package com.joymeng.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.joymeng.Const;

public class CalendarUtil {
	private static SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat formatter4 = new SimpleDateFormat("MM-dd");
	/** yyyy/MM/dd HH:mm */
	private static SimpleDateFormat formatter3 = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	private static SimpleDateFormat formatter6 = new SimpleDateFormat(
			"yyyy年MM月dd日");
	private static SimpleDateFormat formatter7 = new SimpleDateFormat(
	"HH:mm:ss");
	private static Calendar calendar = Calendar.getInstance();
	private static TimeZone timeZone = calendar.getTimeZone();
	private static SimpleDateFormat formatter8 = new SimpleDateFormat("HH:mm");
	private static SimpleDateFormat formatterMMdd1 = new SimpleDateFormat("MM月dd日");
	private static SimpleDateFormat formatterMMddHHmm = new SimpleDateFormat("MM月dd日HH时mm分");
	

	public static synchronized void setDateFormat(String paramString) {
		formatter = new SimpleDateFormat(paramString);
	}

	/** yyyy/MM/dd HH:mm */
	public static synchronized String format3(long time) {
		calendar.setTimeInMillis(time);
		return formatter3.format(calendar.getTime());
	}
	
	public static long format_yyyyMMdd(String timeStr) {
		try {
			return formatter2.parse(timeStr).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/** yyyy/MM/dd HH:mm */
	public static synchronized String format5(long time) {
		calendar.setTimeInMillis(time);
		return formatter5.format(calendar.getTime());
	}

	public static synchronized String format(long paramLong) {
		calendar.setTimeInMillis(paramLong);
		return formatter.format(calendar.getTime());
	}
	
	public static synchronized String formatMMdd(long paramLong) {
		calendar.setTimeInMillis(paramLong);
		return formatterMMdd1.format(calendar.getTime());
	}
	
	public static synchronized String formatMMddHHmm(long paramLong) {
		calendar.setTimeInMillis(paramLong);
		return formatterMMddHHmm.format(calendar.getTime());
	}

	public static synchronized String formatSimpleDate(long dateLong) {
		calendar.setTimeInMillis(dateLong);
		return formatter2.format(calendar.getTime());
	}
	public static synchronized String formatSimpleDate1(long dateLong) {
		calendar.setTimeInMillis(dateLong);
		return formatter6.format(calendar.getTime());
	}
	public static synchronized String formatSimpleDate7(long dateLong) {
		calendar.setTimeInMillis(dateLong);
		return formatter7.format(calendar.getTime());
	}
	public static synchronized String formatSimpleMonthDate(long dateLong) {
		calendar.setTimeInMillis(dateLong);
		return formatter4.format(calendar.getTime());
	}

	public static synchronized String formatAddYear(long paramLong, int paramInt) {
		calendar.setTimeInMillis(paramLong);
		calendar.add(1, paramInt);
		return formatter.format(calendar.getTime());
	}

	public static synchronized String formatAddMonth(long paramLong,
			int paramInt) {
		calendar.setTimeInMillis(paramLong);
		calendar.add(2, paramInt);
		return formatter.format(calendar.getTime());
	}

	public static synchronized String formatAddDay(long paramLong, int paramInt) {
		calendar.setTimeInMillis(paramLong);
		calendar.add(5, paramInt);
		return formatter.format(calendar.getTime());
	}

	public static synchronized String formatAddHour(long paramLong, int paramInt) {
		calendar.setTimeInMillis(paramLong);
		calendar.add(11, paramInt);
		return formatter.format(calendar.getTime());
	}

	public static synchronized String formatAddMinute(long paramLong,
			int paramInt) {
		calendar.setTimeInMillis(paramLong);
		calendar.add(12, paramInt);
		return formatter.format(calendar.getTime());
	}

	public static synchronized String formatAddSecond(long paramLong,
			int paramInt) {
		calendar.setTimeInMillis(paramLong);
		calendar.add(13, paramInt);
		return formatter.format(calendar.getTime());
	}

	public static synchronized String formatDayStart(long paramLong) {
		calendar.setTimeInMillis(paramLong);
		calendar.set(11, 0);
		calendar.set(12, 0);
		calendar.set(13, 0);
		return formatter.format(calendar.getTime());
	}

	public static synchronized String formatDayEnd(long paramLong) {
		calendar.setTimeInMillis(paramLong);
		calendar.set(11, 23);
		calendar.set(12, 59);
		calendar.set(13, 59);
		return formatter.format(calendar.getTime());
	}

	public static synchronized String formatMonthStart(long paramLong) {
		calendar.setTimeInMillis(paramLong);
		calendar.set(5, 1);
		calendar.set(11, 0);
		calendar.set(12, 0);
		calendar.set(13, 0);
		return formatter.format(calendar.getTime());
	}

	public static synchronized String formatMonthEnd(long paramLong) {
		calendar.setTimeInMillis(paramLong);
		calendar.set(5, calendar.getActualMaximum(5));
		calendar.set(11, 23);
		calendar.set(12, 59);
		calendar.set(13, 59);
		return formatter.format(calendar.getTime());
	}

	public static synchronized String formatYearStart(long paramLong) {
		calendar.setTimeInMillis(paramLong);
		calendar.set(2, 0);
		calendar.set(5, 1);
		calendar.set(11, 0);
		calendar.set(12, 0);
		calendar.set(13, 0);
		return formatter.format(calendar.getTime());
	}

	public static synchronized String formatYearEnd(long paramLong) {
		calendar.setTimeInMillis(paramLong);
		calendar.set(2, 11);
		calendar.set(5, 31);
		calendar.set(11, 23);
		calendar.set(12, 59);
		calendar.set(13, 59);
		return formatter.format(calendar.getTime());
	}

	public static synchronized long leftSecondsToNextHour(long paramLong) {
		calendar.setTimeInMillis(paramLong);
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTimeInMillis(paramLong);
		localCalendar.set(12, 0);
		localCalendar.set(13, 0);
		localCalendar.add(11, 1);
		return TimeUnit.MILLISECONDS.toSeconds(localCalendar.getTimeInMillis()
				- calendar.getTimeInMillis());
	}

	public static synchronized long convert(String paramString) {
		try {
			calendar.setTime(formatter.parse(paramString));
			return calendar.getTimeInMillis();
		} catch (ParseException localParseException) {
			localParseException.printStackTrace();
		}
		return 0L;
	}

	/**
	 * 将 yyyy/MM/dd hh:mm 装换成毫秒
	 * 
	 * @param paramString
	 * @return
	 */
	public static synchronized long convert3(String paramString) {
		try {
			calendar.setTime(formatter3.parse(paramString));
			return calendar.getTimeInMillis();
		} catch (ParseException localParseException) {
			localParseException.printStackTrace();
		}
		return 0L;
	}

	/**
	 * 将 yyyy-MM-DD 装换成毫秒
	 * 
	 * @param paramString
	 * @return
	 */
	public static synchronized long convert6(String paramString) {
		try {
			calendar.setTime(formatter2.parse(paramString));
			return calendar.getTimeInMillis();
		} catch (ParseException localParseException) {
			localParseException.printStackTrace();
		}
		return 0L;
	}

	private static SimpleDateFormat formatter5 = new SimpleDateFormat(
			"MM-dd HH:mm");

	/**
	 * 将 yyyy-MM-dd hh:mm 装换成毫秒
	 * 
	 * @param paramString
	 * @return
	 */
	public static synchronized long convert5(String paramString) {
		try {
			calendar.setTime(formatter5.parse(paramString));
			return calendar.getTimeInMillis();
		} catch (ParseException localParseException) {
			localParseException.printStackTrace();
		}
		return 0L;
	}

	public static long getLocalTimeZoneRawOffset() {
		return timeZone.getRawOffset();
	}

	public static long getTimeInMillisWithoutDay(long paramLong) {
		return ((paramLong + getLocalTimeZoneRawOffset()) % 86400000L);
	}

	/**
	 * 将hh:mm转换成毫秒
	 */
	public static long convert7(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		String tim = year+"-"+month+"-"+day+" "+time+":00";
		try {
			return sdf.parse(tim).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0L;
	}

	/**
	 * 返回星期几 英文习惯是：日,1；一,2；...六,7
	 * 
	 * @return 星期一，1；...星期日，7
	 */
	public static int getDayOfWeek() {
		Calendar calendar = Calendar.getInstance();
		Date date = new Date();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_WEEK);
	}
	
	/**
	 * 是周几的凌晨
	 * @param theDayOfWeek {@link Calendar#MONDAY} and so on
	 * @param now
	 * @return
	 */
	public static boolean isDay00(int theDayOfWeek, long now) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(now);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == theDayOfWeek) {
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 1);
			long sunday00 = cal.getTimeInMillis();
			if (Math.abs(now - sunday00) < Const.HOUR) {
				return true;
			}
		}
		else if (dayOfWeek == theDayOfWeek-1 || (theDayOfWeek == Calendar.SUNDAY && dayOfWeek == Calendar.SATURDAY)) {
			//误差几秒内都属于正常
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			long sunday00 = cal.getTimeInMillis();
			if (Math.abs(now - sunday00) < Const.HOUR) {
				return true;
			}
		}
		return false;
	}
	
	public static String formatSimpleDate8(long dateLong){
		calendar.setTimeInMillis(dateLong);
		return formatter8.format(calendar.getTime());
	}
}
