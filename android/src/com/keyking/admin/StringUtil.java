package com.keyking.admin;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public final class StringUtil {

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

	public static void append(final StringBuilder sbString,
			final String... strings) {
		sbString.ensureCapacity(sbString.length() + getLength(strings));

		for (final String string : strings) {
			sbString.append(string);
		}
	}

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

	public static boolean isInteger(String str) {
		if (!isNull(str)) {
			Pattern pattern = Pattern.compile("^(-)?\\d+(\\d+)?$");
			return pattern.matcher(str).matches();
		}
		return false;
	}

	public static boolean isNull(String str) {
		if (str == null || str.equals("") || str.equals("null")) {
			return true;
		}
		return false;
	}

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

	public static String removeFromStr(String oldStr, int id, String split,
			String newStr) {
		if (oldStr == null || oldStr.length() == 0) {
			return "";
		}
		String ids[] = oldStr.split(split);
		for (int i = 0; i < ids.length; i++) {
			if (id == Integer.parseInt(ids[i])) {
				ids[i] = newStr;
				break;
			}
		}
		String str = recoverNewStr(ids, split);
		return str;
	}

	public static String recoverNewStr(String oldStr[], String reg) {
		String str = "";
		int m = 0;
		for (int i = 0; i < oldStr.length; i++) {
			if (!oldStr[i].equals("") && oldStr[i].indexOf(":0,") == -1) {
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

	public static String recoverNewSkillStr(String oldStr[], String reg) {
		String str = "";
		int m = 0;
		for (int i = 0; i < oldStr.length; i++) {
			if ("0".equals(oldStr[i])) {
				continue;
			}
			if (!oldStr[i].equals("") && oldStr[i].indexOf(":0,") == -1) {
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

	public static String[] changeToString(int ids[]) {
		String str[] = new String[ids.length];
		for (int i = 0; i < ids.length; i++) {
			str[i] = String.valueOf(ids[i]);
		}
		return str;
	}

	public static int[] addNew(int array[], int id) {
		int[] newArray = new int[array.length + 1];
		System.arraycopy(array, 0, newArray, 0, array.length);
		newArray[newArray.length - 1] = id;
		return newArray;
	}

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

	public static String toHexString(byte[] byteArray) {
		if (byteArray == null || byteArray.length < 1) {
			return null;
		}
		final StringBuilder hexString = new StringBuilder();
		for (int i = 0; i < byteArray.length; i++) {
			if ((byteArray[i] & 0xff) < 0x10)
				hexString.append("0");
			hexString.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return hexString.toString().toLowerCase(Locale.US);
	}

}
