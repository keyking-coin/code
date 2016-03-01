package com.keyking.coin.service.domain.deal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.keyking.coin.service.domain.data.EntitySaver;
import com.keyking.coin.service.domain.user.RankEntity;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.service.net.resp.module.ModuleResp;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class Deal extends EntitySaver implements Comparable<Deal>{
	
	long id;//编号
	
	long uid;//用户编号
	
	byte sellFlag;//出售帖还是求购帖
	
	byte type;//类型0入库，1现货
	
	byte helpFlag;//可以使用中介服务;0未开启，1开启。
	
	String bourse;//文交所名称
	
	String name;//藏品名称
	
	float price;//藏品单价
	
	String monad;//单位
	
	int num;//藏品数量
	
	String validTime = "永久";//有效时间
	
	String createTime;//创建时间
	
	String other;//其他描述

	boolean revoke;//是否撤销了
	
	List<Revert> reverts = new ArrayList<Revert>();//回复内容列表
	
	List<Revert> delReverts = new ArrayList<Revert>();
	
	List<DealOrder> orders = new ArrayList<DealOrder>();//订单
	
	float needDeposit = 0;
	
	String lastIssue = "null";//离最近的推送时间
	
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

	public byte getSellFlag() {
		return sellFlag;
	}

	public void setSellFlag(byte sellFlag) {
		this.sellFlag = sellFlag;
	}

	public byte getHelpFlag() {
		return helpFlag;
	}

	public void setHelpFlag(byte helpFlag) {
		this.helpFlag = helpFlag;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getBourse() {
		return bourse;
	}

	public void setBourse(String bourse) {
		this.bourse = bourse;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}


	public String getMonad() {
		return monad;
	}

	public void setMonad(String monad) {
		this.monad = monad;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getValidTime() {
		return validTime;
	}

	public void setValidTime(String validTime) {
		this.validTime = validTime;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public boolean isRevoke() {
		return revoke;
	}

	public void setRevoke(boolean revoke) {
		this.revoke = revoke;
	}

	public void setReverts(List<Revert> reverts) {
		this.reverts = reverts;
	}
	public void setOrders(List<DealOrder> orders) {
		this.orders = orders;
	}
	
	public float getNeedDeposit() {
		return needDeposit;
	}

	public void setNeedDeposit(float needDeposit) {
		this.needDeposit = needDeposit;
	}

	public String getLastIssue() {
		return lastIssue;
	}

	public void setLastIssue(String lastIssue) {
		this.lastIssue = lastIssue;
	}

	public void delRevert(Revert revert){
		if (reverts.contains(revert)){
			reverts.remove(revert);
			delReverts.add(revert);
		}
	}
	
	public void addRevert(Revert revert){
		reverts.add(revert);
		//compare_r();
	}
	
	public void addOrder(DealOrder order){
		orders.add(order);
		//compare_o();
	}
	
	private void compare_r(){
		if (reverts.size() > 0){
			Collections.sort(reverts);
		}
	}
	
	private void compare_o(){
		if (orders.size() > 0){
			Collections.sort(orders);
		}
	}
	
	public void compare(){
		compare_r();
		compare_o();
	}
	
	public void serialize(DataBuffer buffer) {
		buffer.putLong(id);
		buffer.put(sellFlag);
		buffer.putLong(uid);
		UserCharacter user = CTRL.search(uid);
		buffer.putUTF(user.getNikeName());
		buffer.putUTF(user.getFace());
		buffer.putUTF(createTime);
		buffer.putUTF(validTime);
		buffer.put(type);
		buffer.putUTF(bourse);
		buffer.putUTF(name);
		buffer.putUTF(monad);
		buffer.putInt(num - orderNum());
		buffer.putUTF(String.valueOf(price));
		buffer.putUTF(other == null ? "" : other);
		buffer.put(helpFlag);
		buffer.putInt(reverts.size());
		for (Revert revert : reverts){
			revert.serialize(buffer);
		}
		buffer.putInt(orders.size());
		for (DealOrder order : orders){
			order.serialize(buffer);
		}
	}

	@Override
	public int compareTo(Deal arg0) {
		DateTime time1 = TimeUtils.getTime(createTime);
		DateTime time2 = TimeUtils.getTime(arg0.createTime);
		if (time1.isBefore(time2)){
			return 1;
		}else{
			return -1;
		}
	}
	
	public boolean checkValidTime(){
		if (validTime.equals("永久")){
			return true;
		}
		if (!StringUtil.isNull(validTime)){
			if (TimeUtils.now().isBefore(TimeUtils.getTimes(validTime))){
				return true;
			}
		}
		return false;
	}
	
	public Revert searchRevert(long id){
		for (Revert revert : reverts){
			if (revert.getId() == id){
				return revert;
			}
		}
		return null;
	}
	
	public DealOrder searchOrder(long id){
		for (DealOrder order : orders){
			if (order.getId() == id){
				return order;
			}
		}
		return null;
	}
	
	public void delete(){
		revoke = true;
		needSave = true;
	}
	
	public void save(){
		for (Revert revert : reverts){
			revert.save();
		}
		for (Revert revert : delReverts){
			revert.save();
		}
		for (DealOrder order : orders){
			order.save();
		}
		if (needSave){
			DB.getDealDao().save(this);
			needSave = false;
		}
	}
	

	public boolean checkBuyerName(String buyer) {
		for (DealOrder order : orders){
			long uid = order.getBuyId();
			UserCharacter user = CTRL.search(uid);
			if (user.getNikeName().equals(buyer)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkBuyerId(long id) {
		for (DealOrder order : orders){
			if (id == order.getBuyId()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 我是否参与交易
	 * @param id
	 * @return
	 */
	public boolean checkJoin(long id) {
		if(uid == id){
			return true;
		}
		return checkBuyerId(id);
	}
	
	public float completeDeposit(){
		float result = 0;
		for (DealOrder order : orders){
			if (order.getHelpFlag() == 0 && order.getState() == DealOrder.ORDER_FINISH_NORMAL){//非中介模式
				result += order.getPrice() * order.getNum();
			}else if (order.getHelpFlag() == 1 && order.getState() == DealOrder.ORDER_FINISH_HELP){//中介模式
				result += order.getPrice() * order.getNum();
			}
		}
		return result;
	}
	
	public float notCompleteDeposit(){
		float result = 0;
		for (DealOrder order : orders){
			if (order.getHelpFlag() == 0 && order.getState() < DealOrder.ORDER_FINISH_NORMAL){//非中介模式
				result += order.getPrice() * order.getNum();
			}else if (order.getHelpFlag() == 1 && order.getState() < DealOrder.ORDER_FINISH_HELP){//中介模式
				result += order.getPrice() * order.getNum();
			}
		}
		return result;
	}
	
	public float completeDeposit(long uid){
		float result = 0;
		for (DealOrder order : orders){
			if (order.getBuyId() != uid){
				continue;
			}
			if (order.getHelpFlag() == 0 && order.getState() == DealOrder.ORDER_FINISH_NORMAL){//非中介模式
				result += order.getPrice() * order.getNum();
			}else if (order.getHelpFlag() == 1 && order.getState() == DealOrder.ORDER_FINISH_HELP){//中介模式
				result += order.getPrice() * order.getNum();
			}
		}
		return result;
	}
	
	public float notCompleteDeposit(long uid){
		float result = 0;
		for (DealOrder order : orders){
			if (order.getBuyId() != uid){
				continue;
			}
			if (order.getHelpFlag() == 0 && order.getState() < DealOrder.ORDER_FINISH_NORMAL){//非中介模式
				result += order.getPrice() * order.getNum();
			}else if (order.getHelpFlag() == 1 && order.getState() < DealOrder.ORDER_FINISH_HELP){//中介模式
				result += order.getPrice() * order.getNum();
			}
		}
		return result;
	}
	
	public String couldDel(){
		boolean flag = checkValidTime();
		for (DealOrder order : orders){
			DealAppraise sa = order.getSellerAppraise();
			DealAppraise ba = order.getBuyerAppraise();
			if (!sa.isCompleted() && ba.isCompleted()){
				return "您还有未确认收款的订单无法撤销";
			}
			if (!ba.isCompleted() && flag){
				return "还有买家的订单未完成无法撤销";
			}
		}
		return null;
	}
	
	public void read(List<DealOrder> recents){
		List<Revert> lis = DB.getRevertDao().search(id);
		if (lis != null){
			for (Revert revert : lis){
				if (revert.isRevoke()){
					delReverts.add(revert);
				}else{
					reverts.add(revert);
				}
			}
		}
		List<DealOrder> temps = DB.getDealOrderDao().search(id);
		if (temps != null){
			for (DealOrder order : temps){
				String orderTime = order.getDealTime();
				long time = TimeUtils.getTime(orderTime).getMillis();
				if (!TimeUtils.isSameDay(time)){
					if (recents.size() >= 20){//如果列表大于20了
						DealOrder remove = null;
						for (DealOrder rec : recents){
							if (order.compareTo(rec) == -1){
								remove = rec;
							}
						}
						if (remove != null){
							recents.remove(remove);
							recents.add(order);
						}
					}else{
						recents.add(order);
					}
				}
				orders.add(order);
			}
		}
	}
	
	public Map<Long,RankEntity> compute(){
		Map<Long,RankEntity> result = new HashMap<Long, RankEntity>();
		for (DealOrder order : orders){
			if (order.over()){//已完成交易
				float worth = order.getPrice() * order.getNum();
				RankEntity entity = result.get(uid);
				if (entity == null){
					entity = new RankEntity(uid);
					result.put(entity.getUid(),entity);
				}
				entity.addCount(1);
				entity.addWorth(worth);
				entity = result.get(order.getBuyId());
				if (entity == null){
					entity = new RankEntity(order.getBuyId());
					result.put(entity.getUid(),entity);
				}
				entity.addCount(1);
				entity.addWorth(worth);
			}
		}
		return result;
	}

	public boolean isDealing() {
		for (DealOrder order : orders){
			if ((order.getHelpFlag()==0 && order.getState() < DealOrder.ORDER_FINISH_NORMAL) || (order.getHelpFlag()==1 && order.getState() < DealOrder.ORDER_FINISH_HELP)){
				return true;
			}
		}
		return false;
	}

	public boolean isConfirming() {
		for (DealOrder order : orders){
			if (order.getHelpFlag()== 0 && order.getState() == DealOrder.ORDER_FINISH_NORMAL && !order.Appraise()){
				return true;
			}
			if (order.getHelpFlag()== 1 && order.getState() == DealOrder.ORDER_FINISH_HELP && !order.Appraise()){
				return true;
			}
		}
		return false;
	}
	
	public boolean isOver() {
		for (DealOrder order : orders){
			if (order.getHelpFlag()== 0 && order.getState() == DealOrder.ORDER_FINISH_NORMAL && order.Appraise()){
				return true;
			}
			if (order.getHelpFlag()== 1 && order.getState() == DealOrder.ORDER_FINISH_HELP && order.Appraise()){
				return true;
			}
		}
		return false;
	}
	
	private int orderNum(){
		int num = 0;
		for (DealOrder order : orders){
			if (order.checkRevoke(DealOrder.ORDER_REVOKE_ALL)){
				continue;
			}
			num += order.getNum();
		}
		return num;
	}
	
	public boolean couldGrab(int grabNum) {
		int orderNum = orderNum();
		if (orderNum + grabNum > num){
			return false;
		}
		return true;
	}
	
	public ModuleResp clientMessage(byte type){
		ModuleResp modules = new ModuleResp();
		clientMessage(type,modules);
		return modules;
	}
	
	public ModuleResp clientMessage(byte type,ModuleResp resp){
		Module module = new Module();
		module.setCode(Module.MODULE_CODE_DEAL);
		module.setFlag(type);
		module.add(this);
		resp.addModule(module);
		return resp;
	}
	
	public ModuleResp pushMessage(){
		PushDealModule module = new PushDealModule();
		module.add(this);
		ModuleResp modules = new ModuleResp();
		modules.addModule(module);
		return modules;
	}

	public boolean isIssueRecently() {
		
		return false;
	}
}
