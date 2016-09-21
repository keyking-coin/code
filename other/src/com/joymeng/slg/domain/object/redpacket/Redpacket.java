package com.joymeng.slg.domain.object.redpacket;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MathUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.chat.MsgTitleType;
import com.joymeng.slg.domain.object.bag.BriefItem;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.RoleIcon;
import com.joymeng.slg.net.SerializeEntity;
import com.joymeng.slg.world.GameConfig;

public class Redpacket implements SerializeEntity,Instances {
	long id;// 红包记录ID
	long time;//红包创建的时间
	long uid;// 红包发送者的Uid
	String name;//红包发送者的名字
	RoleIcon icon = new RoleIcon();//头像对象
	String itemId;//红包道具Id
	RedpacketState state;// 红包的状态 0-正常 1-已返还
	byte type;// 红包的类型即位置 1:世界 2:联盟
	long unionId = 0;// 联盟Id
	String greetings;// 红包祝福语
	int redpacketGold;// 红包金额
	int redpacketNum;// 红包个数
	long[] redpacketList;// 红包列表
	List<SmallRedpacket> gotRoles = new ArrayList<>();// 领取红包的用户列表

	public Redpacket() {
	}

	public Redpacket(long redpacketId, Role role, String itemId, RedpacketState state, byte type, long unionId, String greetings,
			int redpacketGold, int redpacketNum) {
		this.id = redpacketId;
		this.time = TimeUtils.nowLong();
		this.uid = role.getId();
		this.name = role.getName();
		this.icon = role.getIcon();
		this.itemId = itemId;
		this.state = state;
		this.type = type;
		this.unionId = unionId;
		this.greetings = greetings;
		this.redpacketGold = redpacketGold;
		this.redpacketNum = redpacketNum;
		generateRedpacketList();
	}

	/**
	 * 生成小红包列表
	 */
	public void generateRedpacketList() {
		int max = (int) (redpacketGold / redpacketNum * (1.0f + GameConfig.ROLE_REDPACKET_GOLD_FACTOR));
		int min = (int) (redpacketGold / redpacketNum * (1.0f - GameConfig.ROLE_REDPACKET_GOLD_FACTOR));
		redpacketList = generate(redpacketGold, redpacketNum, max, min);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RoleIcon getIcon() {
		return icon;
	}

	public void setIcon(RoleIcon icon) {
		this.icon = icon;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public RedpacketState getState() {
		return state;
	}

	public void setState(RedpacketState state) {
		this.state = state;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public long getUnionId() {
		return unionId;
	}

	public void setUnionId(long unionId) {
		this.unionId = unionId;
	}

	public String getGreetings() {
		return greetings;
	}

	public void setGreetings(String greetings) {
		this.greetings = greetings;
	}

	public int getRedpacketGold() {
		return redpacketGold;
	}

	public void setRedpacketGold(int redpacketGold) {
		this.redpacketGold = redpacketGold;
	}

	public int getRedpacketNum() {
		return redpacketNum;
	}

	public void setRedpacketNum(int redpacketNum) {
		this.redpacketNum = redpacketNum;
	}

	public List<SmallRedpacket> getGotRoles() {
		return gotRoles;
	}

	public void setGotRoles(List<SmallRedpacket> gotRoles) {
		this.gotRoles = gotRoles;
	}

	public long[] getRedpacketList() {
		return redpacketList;
	}

	public void setRedpacketList(long[] redpacketList) {
		this.redpacketList = redpacketList;
	}

	/**
	 * 生成红包数组
	 * 
	 * @param total
	 *            红包总额
	 * @param count
	 *            红包个数
	 * @param max
	 *            每个小红包的最大额
	 * @param min
	 *            每个小红包的最小额
	 * @return 存放生成的每个小红包的值的数组
	 */
	public long[] generate(long total, int count, long max, long min) {
		long[] result = new long[count];
		long average = total / count;
		for (int i = 0; i < result.length; i++) {
			if (nextLong(min, max) > average) {
				long temp = min + xRandom(min, average);
				result[i] = temp;
				total -= temp;
			} else {
				long temp = max - xRandom(average, max);
				result[i] = temp;
				total -= temp;
			}
		}
		while (total > 0) {
			for (int i = 0; i < result.length; i++) {
				if (total > 0 && result[i] < max) {
					result[i]++;
					total--;
				}
			}
		}
		while (total < 0) {
			for (int i = 0; i < result.length; i++) {
				if (total < 0 && result[i] > min) {
					result[i]--;
					total++;
				}
			}
		}
		return result;
	}

	private long xRandom(long min, long max) {
		return sqrt(MathUtils.random((int) sqr(max - min)));
	}

	private long sqrt(long n) {
		return (long) Math.sqrt(n);
	}

	private long sqr(long n) {
		return n * n;
	}

	private long nextLong(long min, long max) {
		return MathUtils.random((int) (max - min + 1)) + min;
	}

	@Override
	public void serialize(JoyBuffer out) {
		out.putLong(id);
		out.putLong(time);
		out.putLong(uid);
		out.putPrefixedString(name, JoyBuffer.STRING_TYPE_SHORT);
		icon.serialize(out);
		out.putPrefixedString(itemId, JoyBuffer.STRING_TYPE_SHORT);
		out.put(state.getState());
		out.put(type);
		out.putLong(unionId);
		out.putPrefixedString(greetings, JoyBuffer.STRING_TYPE_SHORT);
		out.putInt(redpacketGold);
		out.putInt(redpacketNum);
		out.putPrefixedString(JsonUtil.ObjectToJsonString(gotRoles), JoyBuffer.STRING_TYPE_SHORT);
	}

	/**
	 * 是否已被领取完为空
	 * 
	 * @return
	 */
	public boolean isGotOver() {
		return gotRoles.size() >= redpacketNum;
	}

	/**
	 * 获取一个小的红包金额
	 * 
	 * @return
	 */
	public int computeRedpacketRoleGold() {
		if (gotRoles.size() >= redpacketList.length) {
			GameLog.error("get gold from redpacket is fail! redpacket:" + JsonUtil.ObjectToJsonString(this));
			return 0;
		}
		return (int) redpacketList[gotRoles.size()];
	}

	/**
	 * 加入一个领取者
	 * 
	 * @param gotRole
	 */
	public void addGotRole(SmallRedpacket gotRole) {
		gotRoles.add(gotRole);
	}

	/**
	 * 是否已经领取
	 * 
	 * @param uid
	 * @return
	 */
	public boolean containRole(long uid) {
		for (int i = 0; i < gotRoles.size(); i++) {
			SmallRedpacket temp = gotRoles.get(i);
			if (temp == null) {
				continue;
			}
			if (temp.getUid() == uid) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 返回剩余的部分
	 */
	public void returnRest() {
		if (isGotOver()) {// 已被领取完
			return;
		}
		int rest = 0;
		for (int i = gotRoles.size(); i < redpacketList.length; i++) {
			rest += redpacketList[i];
		}
		BriefItem briefItem = new BriefItem("Resources", ResourceTypeConst.RESOURCE_TYPE_GOLD.getKey(), rest);
		List<BriefItem> bList = new ArrayList<>();
		bList.add(briefItem);
		chatMgr.creatSystemEmail(MsgTitleType.MSG_TITLE_REDPACKET_RETURN, "红包返还", bList, uid);
	}
}
