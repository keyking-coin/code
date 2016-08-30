package com.joymeng.common.util;
import java.awt.Color;

//import com.joymeng.arpg.domain.common.Const;
//import com.joymeng.arpg.domain.staticdata.GameDataManager;

/**
 * 处理字符串类
 * 
 * @author xuguangmin
 * 
 */
public class TextUtils {

	public static String WHITE = "255,255,255";// 白 R255 G255 B255
	public static String GREEN = "0,255,0";// 绿 R0 G255 B0
	public static String BULE = "134,242,255";// 蓝 R0 G130 B255
	public static String VIOLET = "202,146,254";// 紫 R169 G28 B250
	public static String ORANGE = "255,175,89";// 橙 R250 G169 B28
	public static String RED = "255,0,0";// 红 R255 G0 B0
	public static String PINK = "255,206,230";
	public static String GOLD = "255,252,0";// 金色
	public static String GRAY = "168,149,160";// 灰色
	public static String COFFEE = "147,106,36";// 咖啡色
	public static String space = " ";
	public static String N = "{/n}";
	public static final String FONT_DEFAULT = "18";
	public static final String FONT_16 = "16";
	public static final String FONT_STYPE = "{f:%d}";
	public static final String C_END = "{/c}";
	public static final String F_END = "{/f}";
	public static final String U_DEFAULT = "1";
	public static final String L_DEFAULT = "2";
	public static final String UL_DEFAULT = U_DEFAULT + "," + L_DEFAULT;
	
	//聊天颜色
	public static final String CHAT_WORLD="0,180,255";
	public static final String CHAT_SCENE="255,206,135";
	public static final String CHAT_ALLY="120,212,0";
	public static final String CHAT_PRIVATE="255,125,195";
	public static final String CHANNEL_SYS="255,240,0";

	public static String getPlayerMerColr(int color) {

		switch (color+1) {
		case 0:
			return WHITE;// 白色
		case 1:
			return GREEN;// 绿色
		case 2:
			return BULE;// 蓝色
		case 3:
			return VIOLET;// 紫色
		case 4:
			return ORANGE;// 橙色
		case 5:
			return GOLD;// 金色
		case 6:
			return PINK;
		default : 
			return PINK;
		}
	}
	
	public static String getColor(int color) {
		switch (color) {
		case 0:
			return WHITE;// 白色
		case 1:
			return GREEN;// 绿色
		case 2:
			return BULE;// 蓝色
		case 3:
			return VIOLET;// 紫色
		case 4:
			return ORANGE;// 橙色
		case 5:
			return GOLD;// 金色
		case 6:
			return PINK;
		}
		return PINK;
	}

	/**
	 * 
	 * @param tab
	 * @param str
	 * @return c 代表颜色，颜色对应的rbg的值，在这里需要拆分一下 颜色必须是上面定义中的颜色，不可为颜色代码 getText("ct",
	 *         WHITE,"kljafdf")
	 * 
	 */
	public static String getText(String tabs, String... str) {
		StringBuffer sb = new StringBuffer();
		sb.append("<S ");
		for (int i = 0; i < tabs.length(); i++) {
			char tab = tabs.charAt(i);
			if (tab == 'c') {
				String color = str[i];
				String[] cs = color.split(",");
				sb.append("r='" + cs[0] + "'").append(space);
				sb.append("g='" + cs[1] + "'").append(space);
				sb.append("b='" + cs[2] + "'").append(space);
			} else {
				sb.append(tab + "='" + str[i] + "'").append(space);
			}
		}
		sb.append("/>");
		return sb.toString();
	}

	public static String getPText(String text) {
		StringBuffer sb = new StringBuffer();
		sb.append("<P>").append(text).append("</P>");
		return sb.toString();
	}

	public static void init() {
		// 资源文件加载
//		GameDataManager.getInstance().load(Const.RES_PATH);
//		StringBuffer sb = new StringBuffer();
//		sb.append("<C>");
	}

	/**
	 * 基于16进制的颜色字符串替换 eg:
	 * getCText("{c:FFFFFF}{f:18}等丰富的非辅导费发送{/f}{/c}{/n}{c:CCCCCC}好啊{/c}")
	 * 
	 * @param text
	 * @return
	 */
	public static String getCTextToHex(String text) {
		// 如果不包含指定转换符号则直接return
		// if (!text.contains("{c:") && !text.contains(N) &&
		// !text.contains("{f:")) {
		// return text;
		// }
		StringBuffer sb = new StringBuffer();
		sb.append("<C>");
		String[] datas = text.split("\\{/n\\}");// 换行
		for (int i = 0 ; i < datas.length ; i++){
			String data = datas[i];
			if (data.length() == 0) {
				continue;
			}
			sb.append("<P>");
			parse(sb, data, null, null);
			sb.append("</P>");
		}
		sb.append("</C>");
		return sb.toString();
	}

	/**
	 * 增加设置字体
	 * 
	 * @param text
	 * @param font
	 * @return
	 */
	public static String getCFontTextToHex(String text, String font) {
		// 如果不包含指定转换符号则直接return
		// if (!text.contains("{c:") && !text.contains(N) &&
		// !text.contains("{f:")) {
		// return text;
		// }
		StringBuffer sb = new StringBuffer();
		sb.append("<C>");
		String[] datas = text.split("\\{/n\\}");// 换行
		for (int i = 0 ; i < datas.length ; i++){
			String data = datas[i];
			if (data.length() == 0) {
				continue;
			}
			sb.append("<P>");
			parse(sb, data, font, null);
			sb.append("</P>");
		}
		sb.append("</C>");
		return sb.toString();
	}

	/**
	 * 增加下划线设置
	 * 
	 * @param text
	 * @param font
	 * @return
	 */
	public static String getCULTextToHex(String text, String ul) {
		// 如果不包含指定转换符号则直接return
		StringBuffer sb = new StringBuffer();
		sb.append("<C>");
		String[] datas = text.split("\\{/n\\}");// 换行
		for (int i = 0 ; i < datas.length ; i++){
			String data = datas[i];
			if (data.length() == 0) {
				continue;
			}
			sb.append("<P>");
			parse(sb, data, null, ul);
			sb.append("</P>");
		}
		sb.append("</C>");
		return sb.toString();
	}

	public static String getCULFontTextToHex(String text, String font, String ul) {
		// 如果不包含指定转换符号则直接return
		StringBuffer sb = new StringBuffer();
		sb.append("<C>");
		String[] datas = text.split("\\{/n\\}");// 换行
		for (int i = 0 ; i < datas.length ; i++){
			String data = datas[i];
			if (data.length() == 0) {
				continue;
			}
			sb.append("<P>");
			parse(sb, data, font, ul);
			sb.append("</P>");
		}
		sb.append("</C>");
		return sb.toString();
	}

	public static String getCtext(String text) {
		StringBuffer sb = new StringBuffer();
		sb.append("<C>").append(text).append("</C>");
		return sb.toString();
	}


	/**
	 * 
	 * @param sb
	 * @param msg
	 * @param datas
	 *            字体大小;下划线属性A;下划线属性B;
	 */
	public static void parse(StringBuffer sb, String msg, String fontSet,
			String ulSet) {
		String font = FONT_DEFAULT;
		String u = U_DEFAULT;
		String l = L_DEFAULT;
		boolean isFont = false;
		boolean isUL = false;
		if (fontSet != null) {
			font = fontSet;
			isFont = true;
		}
		if (ulSet != null) {
			String[] datas = ulSet.split(",");
			if (datas.length > 1) {
				u = datas[0];
				l = datas[1];
				isUL = true;
			}
		}

		int beginIndex = msg.indexOf("{c:");
		int endIndex = msg.indexOf(C_END);
		if (beginIndex < 0) {
			if (msg.length() > 0) {
				if (isUL && isFont) {
					sb.append(getText("sult", font, u, l, msg));
				} else if (isUL) {
					sb.append(getText("ult", u, l, msg));
				} else if (isFont) {
					sb.append(getText("st", font, msg));
				} else {
					sb.append(getText("t", msg));
				}

			}
			return;
		}
		if (beginIndex > 0) {
			if (isUL && isFont) {
				sb.append(getText("sult", font, u, l,
						msg.substring(0, beginIndex)));
			} else if (isUL) {
				sb.append(getText("ult", u, l, msg.substring(0, beginIndex)));
			} else if (isFont) {
				sb.append(getText("st", font, msg.substring(0, beginIndex)));
			} else {
				sb.append(getText("t", msg.substring(0, beginIndex)));
			}
		}
		String colStr = msg.substring(beginIndex + 3, beginIndex + 9);
		String data = msg.substring(beginIndex + 10, endIndex);
		Color color = new Color(Integer.parseInt(colStr.toUpperCase(), 16));
		int red = color.getRed();
		int blue = color.getBlue();
		int green = color.getGreen();
		// 设置了字体
		if (data.trim().startsWith("{f:") && data.trim().endsWith(F_END)) {
			font = data.substring(data.indexOf("{f:") + 3, data.indexOf("}"));
			data = data.substring(data.indexOf("}") + 1, data.indexOf(F_END));
			isFont = true;
		}
		if (isUL && isFont) {
			sb.append(getText("csult", red + "," + green + "," + blue, font, u,
					l, data));
		} else if (isUL) {
			sb.append(getText("cult", red + "," + green + "," + blue, u, l,
					data));
		} else if (isFont) {
			sb.append(getText("cst", red + "," + green + "," + blue, font, data));
		} else {
			sb.append(getText("ct", red + "," + green + "," + blue, data));
		}
		// sb.append(getText("ct", red+","+green+","+blue,data));
		parse(sb, msg.substring(endIndex + 4), font, ulSet);

		// System.out.println(color.getBlue()+":"+color.getRed()+":"+color.getGreen());
		// color.getRGB();
		// color.getAlpha();
		// color.getRed()
	}

	/**
	 * 创建指定长度的半角空格
	 * 
	 * @param len
	 * @return
	 */
	public static String createSpaceSinglebyte(int len) {
		StringBuffer buff = new StringBuffer(len);
		for (int i = 0; i < len; i++) {
			buff.append(' ');
		}
		return buff.toString();
	}

	public static boolean isValid(String str) {
		for (int i = 0; i < str.length(); i ++) {
			char b = str.charAt(i);
			String s = String.valueOf(b);
			byte[] bytes = s.getBytes();
			if (bytes.length == 1 && bytes[0] == 63) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 将一些emoji表情，转化为X
	 * @param str
	 * @return
	 */
	public static String toValidString(String str) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < str.length(); i ++) {
			char b = str.charAt(i);
			String s = String.valueOf(b);
			byte[] bytes = s.getBytes();
			if (bytes.length == 1 && bytes[0] == 63) {
				buff.append('X');
			}
			else {
				buff.append(b);
			}
		}
		return buff.toString();
	}	
}

