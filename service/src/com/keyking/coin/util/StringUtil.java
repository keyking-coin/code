
package com.keyking.coin.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {
	private StringUtil() {
	}

	/**
	 * Concatenates strings.
	 * 
	 * @param strings
	 *            strings to be concatenated
	 * @return concatenated string
	 * @see StringUtil
	 */
	// public static String concat(final String... strings)
	// {
	// final TextBuilder sbString = TextBuilder.newInstance();
	//
	// for (final String string : strings)
	// {
	// sbString.append(string);
	// }
	//
	// String result = sbString.toString();
	// TextBuilder.recycle(sbString);
	// return result;
	// }

	/**
	 * Creates new string builder with size initializated to
	 * <code>sizeHint</code>, unless total length of strings is greater than
	 * <code>sizeHint</code>.
	 * 
	 * @param sizeHint
	 *            hint for string builder size allocation
	 * @param strings
	 *            strings to be appended
	 * @return created string builder
	 * @see StringUtil
	 */
	public static StringBuilder startAppend(final int sizeHint,
			final String... strings) {
		final int length = getLength(strings);
		final StringBuilder sbString = new StringBuilder(
				sizeHint > length ? sizeHint : length);

		for (final String string : strings) {
			sbString.append(string);
		}

		return sbString;
	}

	/**
	 * Appends strings to existing string builder.
	 * 
	 * @param sbString
	 *            string builder
	 * @param strings
	 *            strings to be appended
	 * @see StringUtil
	 */
	public static void append(final StringBuilder sbString,
			final String... strings) {
		sbString.ensureCapacity(sbString.length() + getLength(strings));

		for (final String string : strings) {
			sbString.append(string);
		}
	}

	/**
	 * Counts total length of all the strings.
	 * 
	 * @param strings
	 *            array of strings
	 * @return total length of all the strings
	 */
	private static int getLength(final String[] strings) {
		int length = 0;

		for (final String string : strings) {
			if (string == null) {
				length += 4;
			} else {
				length += string.length();
			}
		}

		return length;
	}

	// public static String getTraceString(StackTraceElement[] trace)
	// {
	// final TextBuilder sbString = TextBuilder.newInstance();
	// for (final StackTraceElement element : trace)
	// {
	// sbString.append(element.toString()).append('\n');
	// }
	//
	// String result = sbString.toString();
	// TextBuilder.recycle(sbString);
	// return result;
	// }

	/**
	 * Converts a line of text into an array of lower case words. Words are
	 * delimited by the following characters: , .\r\n:/\+
	 * <p>
	 * In the future, this method should be changed to use a
	 * BreakIterator.wordInstance(). That class offers much more fexibility.
	 * 
	 * @param text
	 *            a String of text to convert into an array of words
	 * @return text broken up into an array of words.
	 */
	public static final String[] toStringArray(String text) {
		if (text == null || text.length() == 0) {
			return new String[0];
		}
		StringTokenizer tokens = new StringTokenizer(text, ",\r\n/\\");
		String[] words = new String[tokens.countTokens()];
		for (int i = 0; i < words.length; i++) {
			words[i] = tokens.nextToken();
		}
		return words;
	}

	/**
	 * * Converts a line of text into an array of lower case words. Words are
	 * delimited by the following characters: , .\r\n:/\+
	 * <p>
	 * In the future, this method should be changed to use a
	 * BreakIterator.wordInstance(). That class offers much more fexibility.
	 * 
	 * @param text
	 *            a String of text to convert into an array of words
	 * @param token
	 *            String
	 * @return String[]broken up into an array of words.
	 */
	public static final String[] toStringArray(String text, String token) {
		if (text == null || text.length() == 0) {
			return new String[0];
		}
		StringTokenizer tokens = new StringTokenizer(text, token);
		String[] words = new String[tokens.countTokens()];
		for (int i = 0; i < words.length; i++) {
			words[i] = tokens.nextToken();
		}
		return words;
	}

	/**
	 * 鏄惁鏁村舰
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		if (!isNull(str)) {
			Pattern pattern = Pattern.compile("^(-)?\\d+(\\d+)?$");
			return pattern.matcher(str).matches();
		}
		return false;
	}

	/**
	 * 
	 * @param str
	 * @param message
	 * @return
	 */
	public static boolean isNull(String str) {
		if (str == null || str.equals("") || str.equals("null")) {
			return true;
		}
		return false;
	}

	/**
	 * 灏嗗瓧绗︿覆瑙ｆ瀽鎴恑nt鏁扮粍
	 * 
	 * @param str
	 *            锟�锟斤拷瑙ｆ瀽鐨勫瓧绗︿覆
	 * @param split
	 *            鍒嗛殧绗﹀彿
	 * @return
	 */
	public static int[] changeToInt(String str, String split) {
		if (str == null || str.equals("")) {
			return null;
		}

		int data[] = null;
		try {
			String s[] = str.split(split);
			data = new int[s.length];
			for (int i = 0; i < s.length; i++) {
				data[i] = Integer.parseInt(s[i]);
			}
		} catch (Exception ex) {
			return null;
		}
		return data;
	}

	/**
	 * 浠庡瓧绗︿覆涓Щ闄や竴锟�
	 * 
	 * @param oldStr
	 *            锟�锟斤拷澶勭悊鐨勫瓧绗︿覆
	 * @param id
	 *            鏌ユ壘鐨勫瓧绗︿覆
	 * @param split
	 *            鍒嗛殧绗﹀彿
	 * @param newStr
	 *            锟�锟斤拷鏇挎崲鐨勫瓧锟�
	 * @return
	 */
	public static String removeFromStr(String oldStr, int id, String split,
			String newStr) {
		if (oldStr == null || oldStr.length() == 0) {
			return "";
		}
		String ids[] = oldStr.split(split);
		// 鎵惧埌鐩稿悓鐨刬d鍒欒缃负鈥滐拷?
		for (int i = 0; i < ids.length; i++) {
			if (id == Integer.parseInt(ids[i])) {
				ids[i] = newStr;
				break;
			}
		}
		// 缁勫悎涓烘柊鐨勫瓧绗︿覆
		String str = recoverNewStr(ids, split);
		// String str="";
		// int m=0;
		// for(int i=0;i<ids.length;i++){
		// if(!ids[i].equals("")){
		// if(m==0){
		// str+=ids[i];
		// }else{
		// str+=split+ids[i];
		// }
		// m++;
		// }
		// }
		return str;
	}

	/**
	 * 杩樺師鏁扮粍 涓哄瓧绗︿覆锛屽幓鎺夋暟缁勪腑鐨勭┖瀛楃锟�锟�{0,1,2} 锟�0锟�锟�
	 * 
	 * @param oldStr
	 * @param reg
	 * @return
	 */
	public static String recoverNewStr(String oldStr[], String reg) {
		String str = "";
		int m = 0;
		for (int i = 0; i < oldStr.length; i++) {
			if (!oldStr[i].equals("") && oldStr[i].indexOf(":0,") == -1) {// 涓嶄负绌烘病锟�鐨勫＋锟�
				if (m == 0) {
					str += oldStr[i];
				} else {
					str += reg + oldStr[i];
				}
				m++;
			}
		}
		return str;
	}

	/**
	 * 杩樺師skill鏁扮粍 涓哄瓧绗︿覆
	 * 
	 * @param oldStr
	 * @param reg
	 * @return
	 */
	public static String recoverNewSkillStr(String oldStr[], String reg) {
		String str = "";
		int m = 0;
		for (int i = 0; i < oldStr.length; i++) {
			if ("0".equals(oldStr[i])) {
				continue;
			}
			if (!oldStr[i].equals("") && oldStr[i].indexOf(":0,") == -1) {// 涓嶄负绌烘病锟�鐨勫＋锟�
				if (m == 0) {
					str += oldStr[i];
				} else {
					str += reg + oldStr[i];
				}
				m++;
			}
		}
		return str;
	}

	/**
	 * 灏唅nt鏁扮粍杞寲涓哄瓧绗︿覆鏁扮粍
	 * 
	 * @param ids
	 * @return
	 */
	public static String[] changeToString(int ids[]) {
		String str[] = new String[ids.length];
		for (int i = 0; i < ids.length; i++) {
			str[i] = String.valueOf(ids[i]);
		}
		return str;
	}

	/**
	 * 鍚戞暟缁勪腑澧炲姞锟�锟斤拷鏂板厓锟�
	 * 
	 * @param array
	 * @param id
	 * @return
	 */
	public static int[] addNew(int array[], int id) {
		int[] newArray = new int[array.length + 1];
		System.arraycopy(array, 0, newArray, 0, array.length);
		newArray[newArray.length - 1] = id;
		return newArray;
	}

	/**
	 * 杩斿洖瀛楃涓蹭腑姹夊瓧锛屽瓧姣嶇殑鏁伴噺
	 * 
	 * @param str
	 * @return
	 */
	public static int[] isChinese(String str) {
		int[] len = new int[] { 0, 0, 0 };
		if (str == null) {
			return len;
		}
		str = str.trim();
		int count = 0;
		String regEx = "[\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		while (m.find()) {
			for (int i = 0; i <= m.groupCount(); i++) {
				count = count + 1;
			}
		}
		System.out.println("鍏辨湁 " + str.length() + "涓瓧锟�");
		System.out.println("鍏辨湁 " + count + "涓眽锟�");
		System.out.println("鍏辨湁 " + (str.length() - count) + "涓瓧锟�");
		len[0] = str.length();
		len[1] = count;
		len[2] = str.length() - count;
		return len;
	}

	/**
	 * 鑾峰緱鍩瑰吇娆℃暟
	 * 
	 * @param str
	 * @return
	 */
	public static int getFosterNum(String str) {
		if ("".equals(str) || str == null) {
			return 0;
		}
		String[] arr = str.split(",");
		int num = 0;
		try {
			num = Integer.parseInt(arr[0]);
		} catch (Exception e) {
		}
		return num;
	}

	/**
	 * 鑾峰緱鍩瑰吇鏃堕棿
	 * 
	 * @param str
	 * @return
	 */
	public static long getFosterTime(String str) {
		if ("".equals(str) || str == null) {
			return 0;
		}
		String[] arr = str.split(",");
		Long time = 0l;
		try {
			time = Long.parseLong(arr[1]);
		} catch (Exception e) {
		}
		return time;

	}

	public static String changeToStr(int[] arr, String reg) {
		if (arr == null) {
			return "";
		}
		String str = "";
		for (int i = 0; i < arr.length - 1; i++) {
			str += arr[i] + reg;
		}
		str += arr[arr.length - 1];
		return str;
	}

	

	public static byte[] getBytes(char[] chars) {
		Charset cs = Charset.forName("UTF-8");
		CharBuffer cb = CharBuffer.allocate(chars.length);
		cb.put(chars);
		cb.flip();
		ByteBuffer bb = cs.encode(cb);

		return bb.array();
	}

	public static char[] getChars(byte[] bytes) {
		Charset cs = Charset.forName("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);
		return cb.array();
	}
	
//	public static String ByteArrayToHexString(byte[] data)
//    {
//        StringBuilder sb = new StringBuilder(data.length * 3);
//        for(byte b : data){
//            sb.Append(Convert.ToString(b,16).PadLeft(2, '0').PadRight(3, ' '));
//        }
//        return sb.toString();
//    }

	public static String toHexString(byte[] byteArray) {
		  if (byteArray == null || byteArray.length < 1){
			  return null;
		  }
		  // throw new IllegalArgumentException("this byteArray must not be null or empty");
		 
		  final StringBuilder hexString = new StringBuilder();
		  for (int i = 0; i < byteArray.length; i++) {
		   if ((byteArray[i] & 0xff) < 0x10)//0~F前面不零
		    hexString.append("0");
		   hexString.append(Integer.toHexString(0xFF & byteArray[i]));
		  }
		  return hexString.toString().toLowerCase();
		 }
	
	public static void main(String[] args) {
		
		//System.out.println(toHexString(new int[]{137, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 36}));
	}
	
}
