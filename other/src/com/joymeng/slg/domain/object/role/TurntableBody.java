package com.joymeng.slg.domain.object.role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MathUtils;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.object.bag.data.Item;
import com.joymeng.slg.domain.object.bag.data.ItemType;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.turntable.Lottery;
import com.joymeng.slg.domain.turntable.Lotterypool;
import com.joymeng.slg.domain.turntable.Turntable;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;

public class TurntableBody implements Instances {
	long uid; // 用户UID
	int turntableNum = 0; // 九宫格洗牌次数
	String turntableId = "null"; // 大转盘ID
	int turnSum = 0;// 大转盘转动次数
	int turntableState = 0; // 大转盘的状态 0:大转盘 1:九宫格未洗牌 2:九宫格已洗牌
	String sudokuId = "null";// 九宫格Id
	String[][] sudokuItems = new String[9][9]; // 九宫格的内容
	int nextMultiple = 1;// 下次的倍数
	List<SudokuInfo> sudokuInfos = new ArrayList<>(); // 九宫格已经翻起的内容
	List<String> randomItems = new ArrayList<>();// 每次随机得到的物品表
	long saveTime ;//存储时间
	
	public TurntableBody() {
	}

	public TurntableBody(long uid) {
		this.uid = uid;
	}

	public long getUid() {
		return uid;
	}

	public int getTurnSum() {
		return turnSum;
	}

	public void setTurnSum(int turnSum) {
		this.turnSum = turnSum;
	}
	
	public long getSaveTime() {
		return saveTime;
	}

	public void setSaveTime(long saveTime) {
		this.saveTime = saveTime;
	}

	public int getNextMultiple() {
		return nextMultiple;
	}

	public void setNextMultiple(int nextMultiple) {
		this.nextMultiple = nextMultiple;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public int getTurntableNum() {
		return turntableNum;
	}

	public void setTurntableNum(int turntableNum) {
		this.turntableNum = turntableNum;
	}

	public String getSudokuId() {
		return sudokuId;
	}

	public void setSudokuId(String sudokuId) {
		this.sudokuId = sudokuId;
	}

	public String getTurntableId() {
		return turntableId;
	}

	public void setTurntableId(String turntableId) {
		this.turntableId = turntableId;
	}

	public int getTurntableState() {
		return turntableState;
	}

	public void setTurntableState(int turntableState) {
		this.turntableState = turntableState;
	}

	public List<String> getRandomItems() {
		return randomItems;
	}

	public void setRandomItems(List<String> randomItems) {
		this.randomItems = randomItems;
	}

	public String[][] getSudokuItems() {
		return sudokuItems;
	}

	public void setSudokuItems(String[][] sudokuItems) {
		this.sudokuItems = sudokuItems;
	}

	public List<SudokuInfo> getSudokuInfos() {
		return sudokuInfos;
	}

	public void setSudokuInfos(List<SudokuInfo> sudokuInfos) {
		this.sudokuInfos = sudokuInfos;
	}

	/**
	 * 更新大转盘Id
	 */
	public void updateTurntableId(Role role) {
		if (role == null){
			role = world.getRole(uid);
		}
		List<RoleBuild> roleBuilds = role.getBuildsByBuildId(0, "CityCenter");
		if (roleBuilds == null || roleBuilds.size() < 1) {
			GameLog.error("getBuildsByBuildId is fail");
			return;
		}
		final byte cityLevel = roleBuilds.get(0).getLevel();
		final int num = turntableNum;
		List<Turntable> turntables = dataManager.serachList(Turntable.class, new SearchFilter<Turntable>() {
			@Override
			public boolean filter(Turntable data) {
				if (Byte.parseByte(data.getBuildingLevel().get(0)) <= cityLevel
						&& Byte.parseByte(data.getBuildingLevel().get(1)) >= cityLevel
						&& data.getResetNumber() <= num) {
					return true;
				}
				return false;
			}
		});
		String[] turntableIds = new String[turntables.size()];
		int[] rates = new int[turntables.size()];
		for (int index = 0; index < turntables.size(); index++) {
			Turntable temp = turntables.get(index);
			if (temp == null) {
				continue;
			}
			turntableIds[index] = temp.getId();
			rates[index] = temp.getWeight();
		}
		
		String result = MathUtils.getRandomObj(turntableIds, rates);
		if (StringUtils.isNull(result)) {
			GameLog.error("update turntable is fail");
			result = "TurnTable1_9_0";
		}
		turntableId = result;
	}

	/**
	 * 更新九宫格内容
	 */
	public void updateSudokuItems() {
		// 填充sudokuItems
		sudokuItems = new String[10][9];// 清除数据
		randomItems.clear();
		int sudokuIndex = 0;	//加头用于标示
		Lottery lottery = dataManager.serach(Lottery.class, sudokuId);
		if (lottery == null) {
			GameLog.error("read lottery is fail");
			return;
		}
		sudokuItems[0][0] = String.valueOf(lottery.getDropList1().size());
		if (lottery.getDropList1() != null) {
			for (int index = 0; index < lottery.getDropList1().size(); index++) {
				String turntableItem = randomTurntableItem(lottery.getDropList1().get(index));
				randomItems.add(turntableItem);
				if (turntableItem == null) {
					GameLog.error("createTurntableItem is fail");
					continue;
				}
				sudokuItems[0][index + 1] = String.valueOf(sudokuIndex) + "|" + turntableItem;
				sudokuIndex++;
			}
		}
		sudokuItems[1][0] = String.valueOf(lottery.getDropList2().size());
		if (lottery.getDropList2() != null) {
			for (int index = 0; index < lottery.getDropList2().size(); index++) {
				String turntableItem = randomTurntableItem(lottery.getDropList2().get(index));
				randomItems.add(turntableItem);
				if (turntableItem == null) {
					GameLog.error("createTurntableItem is fail");
					continue;
				}
				sudokuItems[1][index + 1] = String.valueOf(sudokuIndex) + "|" + turntableItem;
				sudokuIndex++;
			}
		}
		sudokuItems[2][0] = String.valueOf(lottery.getDropList3().size());
		if (lottery.getDropList3() != null) {
			for (int index = 0; index < lottery.getDropList3().size(); index++) {
				String turntableItem = randomTurntableItem(lottery.getDropList3().get(index));
				randomItems.add(turntableItem);
				if (turntableItem == null) {
					GameLog.error("createTurntableItem is fail");
					continue;
				}
				sudokuItems[2][index + 1] = String.valueOf(sudokuIndex) + "|" + turntableItem;
				sudokuIndex++;
			}
		}
		sudokuItems[3][0] = String.valueOf(lottery.getDropList4().size());
		if (lottery.getDropList4() != null) {
			for (int index = 0; index < lottery.getDropList4().size(); index++) {
				String turntableItem = randomTurntableItem(lottery.getDropList4().get(index));
				randomItems.add(turntableItem);
				if (turntableItem == null) {
					GameLog.error("createTurntableItem is fail");
					continue;
				}
				sudokuItems[3][index + 1] = String.valueOf(sudokuIndex) + "|" + turntableItem;
				sudokuIndex++;
			}
		}
		sudokuItems[4][0] = String.valueOf(lottery.getDropList5().size());
		if (lottery.getDropList5() != null) {
			for (int index = 0; index < lottery.getDropList5().size(); index++) {
				String turntableItem = randomTurntableItem(lottery.getDropList5().get(index));
				randomItems.add(turntableItem);
				if (turntableItem == null) {
					GameLog.error("createTurntableItem is fail");
					continue;
				}
				sudokuItems[4][index + 1] = String.valueOf(sudokuIndex) + "|" + turntableItem;
				sudokuIndex++;
			}
		}
		sudokuItems[5][0] = String.valueOf(lottery.getDropList6().size());
		if (lottery.getDropList6() != null) {
			for (int index = 0; index < lottery.getDropList6().size(); index++) {
				String turntableItem = randomTurntableItem(lottery.getDropList6().get(index));
				randomItems.add(turntableItem);
				if (turntableItem == null) {
					GameLog.error("createTurntableItem is fail");
					continue;
				}
				sudokuItems[5][index + 1] = String.valueOf(sudokuIndex) + "|" + turntableItem;
				sudokuIndex++;
			}
		}
		sudokuItems[6][0] = String.valueOf(lottery.getDropList7().size());
		if (lottery.getDropList7() != null) {
			for (int index = 0; index < lottery.getDropList7().size(); index++) {
				String turntableItem = randomTurntableItem(lottery.getDropList7().get(index));
				randomItems.add(turntableItem);
				if (turntableItem == null) {
					GameLog.error("createTurntableItem is fail");
					continue;
				}
				sudokuItems[6][index + 1] = String.valueOf(sudokuIndex) + "|" + turntableItem;
				sudokuIndex++;
			}
		}
		sudokuItems[7][0] = String.valueOf(lottery.getDropList8().size());
		if (lottery.getDropList8() != null) {
			for (int index = 0; index < lottery.getDropList8().size(); index++) {
				String turntableItem = randomTurntableItem(lottery.getDropList8().get(index));
				randomItems.add(turntableItem);
				if (turntableItem == null) {
					GameLog.error("createTurntableItem is fail");
					continue;
				}
				sudokuItems[7][index + 1] = String.valueOf(sudokuIndex) + "|" + turntableItem;
				sudokuIndex++;
			}
		}
		sudokuItems[8][0] = String.valueOf(lottery.getDropList9().size());
		if (lottery.getDropList9() != null) {
			for (int index = 0; index < lottery.getDropList9().size(); index++) {
				String turntableItem = randomTurntableItem(lottery.getDropList9().get(index));
				randomItems.add(turntableItem);
				if (turntableItem == null) {
					GameLog.error("createTurntableItem is fail");
					continue;
				}
				sudokuItems[8][index + 1] = String.valueOf(sudokuIndex) + "|" + turntableItem;
				sudokuIndex++;
			}
		}
	}

	/**
	 * 随机出一个item
	 * 
	 * @param dropId
	 * @return
	 */
	private String randomTurntableItem(String lotteryPoolId) {
		String result = "";
		String[] val = lotteryPoolId.split(":");
		if (val.length != 2) {
			GameLog.error("固化表配置错误!");
			return null;
		}
		Lotterypool lotteryPool = dataManager.serach(Lotterypool.class, val[0]);
		if (lotteryPool == null) {
			GameLog.error("get lotteryPool base data is fail");
			return null;
		}
		List<String> lItems = lotteryPool.getItemlist();
		String[] values = new String[lItems.size()];
		int[] rates = new int[lItems.size()];
		for (int index = 0; index < lItems.size(); index++) {
			String[] str = lItems.get(index).split(":");
			if (str.length < 1) {
				continue;
			}
			values[index] = str[0];
			rates[index] = Integer.parseInt(str[1]);
		}
		if (values.length == 0 || rates.length == 0) {
			GameLog.error(" MathUtils.getRandomObj(values, rates) is  null");
			return null;
		}
		result = MathUtils.getRandomObj(values, rates);
		if (result != null) {
			result += ":" + val[1];
		}
		return result;
	}

	/**
	 * 发送大转盘数据
	 * 
	 * @param rms
	 */
	public void sendTurntableToClient(RespModuleSet rms) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_TURNTABLE;
			}
		};
		module.add(turntableNum); // int  大转盘洗牌次数
		module.add(turntableId); // String 大转盘ID
		module.add(turnSum); //int 大转盘转动次数
		module.add(turntableState); // int 大转盘的状态 0:大转盘 1:九宫格未洗牌 2:九宫格已洗牌
		List<String> sudokuItemLs = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				String temp = sudokuItems[i][j];
				if (!StringUtils.isNull(temp) && temp.split(":")[0].length() > 1) {
					sudokuItemLs.add(temp.split(":")[0]);
				}
			}
		}
		//打乱顺序
		Collections.sort(sudokuItemLs, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.length() == o2.length() ? 0 : (o1.length() > o2.length() ? 1 : -1);
			}
		});
		int size = sudokuItemLs.size();
		module.add(size); // int
		for (int i = 0; i < size; i++) {
			module.add(sudokuItemLs.get(i)); // String
		}
		int size1 = sudokuInfos.size();
		module.add(size1); // int
		for (int i = 0; i < size1; i++) {
			module.add(sudokuInfos.get(i).getPos());// int
			module.add(sudokuInfos.get(i).getItemId());// String
		}
		rms.addModule(module);
	}

	/**
	 * 翻开九宫格
	 * 
	 * @param role
	 * @param pos
	 * @return
	 */
	public boolean sudokuOpen(Role role, int pos) {
		// 获取打开的物品
		String id = randomItemFromSudokuItems();
		if (StringUtils.isNull(id) || id.split("\\|").length < 2) {
			GameLog.error("randomItemFromSudokuItems is fail");
			return false;
		}
		int lastMultiple = 1;
		String itemId = id.split("\\|")[1];
		// 加个背包
		Item item = dataManager.serach(Item.class, itemId);
		if (item == null) {
			GameLog.error("read item base data is fail!");
			return false;
		}
		if (item.getItemType() == ItemType.TYPE_SUDOKU_MULTI) {
			nextMultiple = Integer.parseInt(item.getEffectAfterUse());
		} else {
			lastMultiple = nextMultiple;
			if (item.getMaterialType() == 0) { // 物品
				role.getBagAgent().addGoods(itemId, nextMultiple);
			} else { // 材料
				role.getBagAgent().addOther(itemId, nextMultiple);
			}
			String event = "sudokuOpen";
			String itemst  = itemId;
			LogManager.itemOutputLog(role, nextMultiple, event, itemst);
			nextMultiple = 1;
		}
		// 加入已经打开的list
		SudokuInfo info = new SudokuInfo(pos, id);
		sudokuInfos.add(info);
		//下发全服通知
		if (sudokuInfos.size() == 9 && item.getItemType() != ItemType.TYPE_SUDOKU_MULTI) {
			chatMgr.addStringContentNotice(0, false, I18nGreeting.TURNTABLE_GOT_REWARD, "0$" + role.getName(),
					"0$" + lastMultiple, "1$" + item.getItemName());
		}
		// 全部翻开重置
		if (sudokuInfos.size() >= 9) {
			turntableState = 0;
		}
		// 发送背包 + 九宫格消息
		RespModuleSet rms = new RespModuleSet();
		role.getBagAgent().sendBagToClient(rms);
		sendTurntableToClient(rms);
		MessageSendUtil.sendModule(rms, role);
		return true;
	}

	/**
	 * 重置初始状态
	 */
	public void resetSudoku() {
		nextMultiple = 1;
		sudokuId = "null";
		sudokuItems = new String[9][9];
		sudokuInfos = new ArrayList<SudokuInfo>();
	}

	private String randomItemFromSudokuItems() {
		String result = "";
		int size = sudokuInfos.size();
		String[] vaList = null;
		int totle = 0;
		for (int index = 0; index < sudokuItems.length; index++) {
			if (size < Integer.parseInt(sudokuItems[index][0] == null ? "0" : sudokuItems[index][0]) + totle) {
				vaList = sudokuItems[index];
				break;
			}
			totle += Integer.parseInt(sudokuItems[index][0]);
		}
		String[] values = new String[Integer.parseInt(vaList[0])];
		int[] rates = new int[Integer.parseInt(vaList[0])];
		for (int i = 1; i <= Integer.parseInt(vaList[0]); i++) {
			String[] string = vaList[i].split(":");
			if (string.length < 1) {
				GameLog.error("vaList[i] is fail");
				continue;
			}
			values[i - 1] = string[0];
			if (SudokuInfoIsExist(totle,string[0])) {
				rates[i - 1] = 0;
			}else{
				rates[i - 1] = Integer.parseInt(string[1]);
			}
		}
		result = MathUtils.getRandomObj(values, rates);
		return result;
	}

	/**
	 * 判断翻过的ItemID是已经翻过
	 * @param totle 
	 * @param string
	 * @return
	 */
	private boolean SudokuInfoIsExist(int start, String string) {
		if (start >= sudokuInfos.size()) {
			return false;
		}
		for (int index = start; index < sudokuInfos.size(); index++) {
			SudokuInfo sudokuInfo = sudokuInfos.get(index);
			if (sudokuInfo == null) {
				continue;
			}
			if (sudokuInfo.getItemId().equals(string)) {
				return true;
			}
		}
		return false;
	}

	public boolean isOpened(int pos) {
		for (int i = 0 ; i < sudokuInfos.size() ; i++){
			SudokuInfo sudokuInfo = sudokuInfos.get(i);
			if (sudokuInfo == null) {
				continue;
			}
			if (sudokuInfo.getPos() == pos) {
				return true;
			}
		}
		return false;
	}
}
