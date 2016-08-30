package com.joymeng.common.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class StringUtils {
	public static String byteArrayTo111(byte[] bytes) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0 ; i < bytes.length ; i++){
		    byte b = bytes[i];
			buff.append(b);
		}
		return buff.toString();
	}
	public static void fillStr111ToBytes(String _111, byte[] bytes) {
		if (_111 == null || _111.trim().isEmpty()) return;
		for (int i = 0; i < _111.length() && i < bytes.length; ++i) {
			bytes[i] = (byte) (_111.charAt(i) - '0');
		}
	}
	
	public static boolean isNull(String str){
		if (str == null || str.equals("") || str.equals("null")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 统计字符串字符数
	 * @param value
	 * @return
	 */
	public static int countStringLength(String value) {
		int valueLength = 0;
		String chinese = "[\u0391-\uFFE5]";
		for (int i = 0; i < value.length(); i++) {
			String temp = value.substring(i, i + 1);
			if (temp.matches(chinese)) {
				valueLength += 2;
			} else {
				valueLength += 1;
			}
		}
		return valueLength;
	}
	
	public static <T, E> List<T> mapTransitionList(Map<E, T> map) {
		List<T> list = new ArrayList<T>();
		for (E t : map.keySet()) {
			list.add(map.get(t));
		}
		return list;
	}
	
	/**
	 * 移除list重复项  保持原有的顺序
	 * @param list
	 * @return
	 */
	public static <T> List<T> removeDuplicateWithOrder(List<T> list)  
    {  
        HashSet<T> hashSet = new HashSet<T>();  
        List<T> newlist = new ArrayList<T>();  
        for (int i = 0 ; i < list.size() ; i++){
        	T element = (T) list.get(i);  
            if (hashSet.add(element)){  
                newlist.add(element);  
            }  
        }  
        list.clear();  
        list.addAll(newlist);  
        return list;  
    } 
	
	/**
	 * RC4 数据加密
	 * @param aInput(加解密为同一方法)
	 * @param aKey
	 * @return
	 */
	public static String HloveyRC4(String aInput, String aKey) {
		int[] iS = new int[256];
		byte[] iK = new byte[256];
		for (int i = 0; i < 256; i++)
			iS[i] = i;
		int j = 1;
		for (short i = 0; i < 256; i++) {
			iK[i] = (byte) aKey.charAt((i % aKey.length()));
		}
		j = 0;
		for (int i = 0; i < 255; i++) {
			j = (j + iS[i] + iK[i]) % 256;
			int temp = iS[i];
			iS[i] = iS[j];
			iS[j] = temp;
		}
		int i = 0;
		j = 0;
		char[] iInputChar = aInput.toCharArray();
		char[] iOutputChar = new char[iInputChar.length];
		for (short x = 0; x < iInputChar.length; x++) {
			i = (i + 1) % 256;
			j = (j + iS[i]) % 256;
			int temp = iS[i];
			iS[i] = iS[j];
			iS[j] = temp;
			int t = (iS[i] + (iS[j] % 256)) % 256;
			int iY = iS[t];
			char iCY = (char) iY;
			iOutputChar[x] = (char) (iInputChar[x] ^ iCY);
		}
		return new String(iOutputChar);
	}
}
