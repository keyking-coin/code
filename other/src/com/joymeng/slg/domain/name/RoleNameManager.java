package com.joymeng.slg.domain.name;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.common.util.StringUtils;
import com.joymeng.slg.dao.DaoData;

public class RoleNameManager implements Instances {

	private static RoleNameManager instance = new RoleNameManager();

	Map<String, Long> usedNames = new HashMap<String, Long>();

	Map<Integer, List<String>> disallows = new HashMap<Integer, List<String>>();

	public static RoleNameManager getInstance() {
		return instance;
	}

	public synchronized void change(long uid, String name, String newName) {
		if (usedNames.containsKey(name)) {
			usedNames.remove(name);
		}
		usedNames.put(newName, uid);
	}

	public byte check(String name) {
		if (usedNames.containsKey(name)) {
			return 0;
		}
		return 1;
	}

	public void loadDisallow() {
		synchronized (disallows) {
			disallows.clear();
			try {
				FileReader reader = new FileReader("./conf/disallow");
				BufferedReader br = new BufferedReader(reader);
				String s = null;
				while ((s = br.readLine()) != null) {
					if (StringUtils.isNull(s)) {
						continue;
					}
					s = s.replace(" ", "");
					int len = s.length();
					List<String> lis = disallows.get(len);
					if (lis == null) {
						lis = new ArrayList<String>();
						disallows.put(len, lis);
					}
					lis.add(s);
				}
				br.close();
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void load() {
		try {
			String sql = "select " + DaoData.RED_ALERT_ROLE_ID + "," + DaoData.RED_ALERT_GENERAL_NAME + " from "
					+ DaoData.TABLE_RED_ALERT_ROLE;
			List<Map<String, Object>> datas = dbMgr.getGameDao().getDatasBySql(sql);
			for (int i = 0; i < datas.size(); i++) {
				Map<String, Object> map = datas.get(i);
				String name = map.get(DaoData.RED_ALERT_GENERAL_NAME).toString();
				Long value = Long.valueOf(map.get(DaoData.RED_ALERT_ROLE_ID).toString());
				usedNames.put(name, value);
			}
			loadDisallow();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isNameLegal(String name) {
		synchronized (disallows) {
			int len = name.length();
			while (len > 0) {
				List<String> lis = disallows.get(len);
				if (lis != null) {
					for (int i = 0; i < lis.size(); i++) {
						String str = lis.get(i);
						if (name.contains(str)) {
							return false;
						}
					}
				}
				len--;
			}
			return !StringUtils.isNull(name);
		}
	}

	public String chatWordsValid(String text) {
		synchronized (disallows) {
			String string = text.replace(" ", "");
			int len = string.length();
			while (len > 0) {
				List<String> lis = disallows.get(len);
				if (lis != null) {
					for (int i = 0; i < lis.size(); i++) {
						String str = lis.get(i);
						text = charShielding(text, str);
					}
				}
				len--;
			}
			return text;
		}
	}
	
	/**
	 * List<Character> 转字符串的方法
	 * @param in
	 * @return
	 */
	public static String characterListToString(List<Character> in) {
		String result = "";
		for (char b : in) {
			result += b;
		}
		return result;
	}

	/**
	 * 从content的startIndex的位置开始向后处理含有mode的子字符串
	 * @param content
	 * @param startIndex
	 * @param mode
	 * @return
	 */
	public static Object[] next(String content, int startIndex, String mode) {
		Object[] result = new Object[3];
		char[] ss = content.toCharArray();
		List<Character> all = new ArrayList<>();
		List<Character> in = new ArrayList<>();
		for (int i = startIndex; i < ss.length; i++) {
			all.add(ss[i]);
			if (ss[i] == ' ') {// 空格不做处理(可以扩展到任意不做处理的字符,还需要修改的地方已标记(★))
				continue;
			}
			in.add(ss[i]);
			if (in.size() == mode.length()) {
				String temp = characterListToString(in);
				if (temp.contains(mode)) {
					result[0] = true;// 是否含有非法子字符串
					result[1] = all;// 在原字符串中的子字符串
					result[2] = i;// 结束的位置(作为下次的起始点)
					return result;
				} else {
					while (all.get(0) == ' ') {// here★
						all.remove(0);
					}
					all.remove(0);
					in.remove(0);
				}
			}
		}
		result[0] = false;
		return result;
	}

	/**
	 * 处理content字符串 ,mode为非法字符串
	 * @param content
	 * @param mode
	 * @return
	 */
	public static String charShielding(String content, String mode) {
		if (mode.length() > content.length()) {// 非法字符长度大于内容时不作处理
			return content;
		}
		int startIndex = 0;
		while (1 != 0) {// 循环处理
			Object[] result = next(content, startIndex, mode);
			if ((boolean) result[0]) {
				@SuppressWarnings("unchecked")
				List<Character> all = (List<Character>) result[1];
				char[] rc = new char[all.size()];
				for (int i = 0; i < rc.length; i++) {
					rc[i] = '*';
				}
				String rs = new String(rc);
				content = content.replace(characterListToString(all), rs);
				startIndex = (int) result[2] + 1;// 此次结尾的位置+1 即为下次的开始位置
			} else {
				break;
			}
		}
		return content;
	}
	
}
