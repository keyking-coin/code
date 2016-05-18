package com.keyking.coin.service.domain.deal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.keyking.coin.service.Service;
import com.keyking.coin.service.domain.user.RankEntity;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.SerializeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.service.net.resp.module.ModuleResp;
import com.keyking.coin.service.tranform.page.deal.TransformDealDetail;
import com.keyking.coin.util.Instances;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class Deal implements Instances,SerializeEntity,Comparable<Deal>{
	public static final byte DEAL_TYPE_BUY  = 0;
	public static final byte DEAL_TYPE_SELL = 1;
	long id;//编号
	long uid;//用户编号
	byte sellFlag = DEAL_TYPE_BUY;//出售帖还是求购帖
	byte type;//类型0入库，1现货
	byte helpFlag;//可以使用中介服务;0未开启，1开启。
	String bourse;//文交所名称
	String name;//藏品名称
	float price;//藏品单价
	String monad;//单位
	int num;//藏品数量
	String validTime = "永久";//有效时间
	String createTime;//创建时间
	String other = "";//其他描述
	boolean revoke;//是否撤销了
	boolean lock;//被管理员锁定
	List<Revert> reverts    = new ArrayList<Revert>();//回复内容列表
	List<DealOrder> orders  = new ArrayList<DealOrder>();//订单
	float needDeposit = 0;
	String lastIssue  = "null";//离最近的推送时间
	
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

	public List<Revert> getReverts() {
		return reverts;
	}

	public List<DealOrder> getOrders() {
		return orders;
	}

	public boolean isLock() {
		return lock;
	}

	public void setLock(boolean lock) {
		this.lock = lock;
	}
	
	public void addRevert(Revert revert){
		reverts.add(revert);
		compare_r();
	}
	
	public void addOrder(DealOrder order){
		orders.add(order);
		compare_o();
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
	
	public boolean checkSeller(long uid) {
	    return sellFlag == 1 && this.uid == uid;
	}

	public boolean checkBuyer(long uid) {
	    return sellFlag == 0 && this.uid == uid;
	}
	
	public int getLeftNum(){
		return Math.max(0,num - orderNum());
	}
	
	public DealOrder getRecentOrder(){
		return orders.size() > 0 ? orders.get(0) : null;
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
		buffer.putInt(getLeftNum());
		buffer.putUTF(String.valueOf(price));
		buffer.putUTF(other == null ? "" : other);
		buffer.put(helpFlag);
		buffer.put((byte)(revoke?1:0));
		buffer.put((byte)(lock?1:0));
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
	}
	
	public void save(){
		DB.getDealDao().save(this);
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
			if (order.checkRevoke() || order.getState() == DealOrder.ORDER_FINISH_NORMAL){//非中介模式
				result += order.getPrice() * order.getNum();
			}
		}
		return result;
	}
	
	public float notCompleteDeposit(){
		float result = 0;
		for (DealOrder order : orders){
			if (order.checkRevoke()){
				continue;
			}
			if (order.getState() < DealOrder.ORDER_FINISH_NORMAL){
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
			if (order.checkRevoke() || order.getState() == DealOrder.ORDER_FINISH_NORMAL){//非中介模式
				result += order.getPrice() * order.getNum();
			}
		}
		return result;
	}
	
	public float notCompleteDeposit(long uid){
		float result = 0;
		for (DealOrder order : orders){
			if (order.getBuyId() != uid || order.checkRevoke()){
				continue;
			}
			if (order.getState() < DealOrder.ORDER_FINISH_NORMAL){//非中介模式
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
	
	public void read(){
		List<Revert> lis = DB.getRevertDao().search(id);
		if (lis != null){
			for (Revert revert : lis){
				reverts.add(revert);
			}
		}
		compare_r();
		List<DealOrder> temps = DB.getDealOrderDao().search(id);
		if (temps != null){
			orders.addAll(temps);
		}
		compare_o();
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
			if ((order.getHelpFlag()== 0 && order.getState() < DealOrder.ORDER_FINISH_NORMAL) || (order.getHelpFlag()==1 && order.getState() < DealOrder.ORDER_FINISH_HELP)){
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
	
	public int orderNum(){
		int num = 0;
		for (DealOrder order : orders){
			if (order.checkRevoke()){
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
		TransformDealDetail tdd = new TransformDealDetail();
		tdd.copy(this);
		module.add("deal",tdd);
		resp.addModule(module);
		return resp;
	}

	public ModuleResp pushMessage(){
		PushDealModule module = new PushDealModule();
		//TransformDealListInfo tdli = new TransformDealListInfo();
		//tdli.copy(this);
		//module.add("push",tdli);
		module.add("push",this);
		ModuleResp modules = new ModuleResp();
		modules.addModule(module);
		return modules;
	}
	
	public boolean isIssueRecently() {
		if (!StringUtil.isNull(lastIssue)){
			long time = TimeUtils.getTime(lastIssue).getMillis() / 1000 + Service.IUSSUE_TIME * 3600;
			long now = TimeUtils.now().getMillis() / 1000;
			if (now < time){
				return true;
			}
		}
		return false;
	}

	public synchronized boolean tryToRevert(long tid, String content) {
		Revert revrt = new Revert();
		revrt.setUid(uid);
		revrt.setTar(tid);
		revrt.setDependentId(id);
		revrt.setContext(content);
		revrt.setCreateTime(TimeUtils.formatYear(TimeUtils.now()));
		long rid = PK.key("deal_revert");
		revrt.setId(rid);
		addRevert(revrt);
		revrt.save();
		//NET.sendMessageToAllClent(clientMessage(Module.UPDATE_FLAG),null);
		return true;
	}
}
