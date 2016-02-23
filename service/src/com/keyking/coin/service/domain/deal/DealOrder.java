package com.keyking.coin.service.domain.deal;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.keyking.coin.service.domain.data.EntitySaver;
import com.keyking.coin.service.domain.user.Credit;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.util.JsonUtil;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class DealOrder extends EntitySaver implements Comparable<DealOrder>{
	
	private static final byte ORDER_FINISH_RUKU    = 3;
	
	private static final byte ORDER_FINISH_XIANHUO = 5;
	
	long id;
	
	long dealId;
	
	long buyId;

	int num;
	
	float price;

	byte helpFlag;//0未使用,1使用
	
	DealAppraise sellerAppraise = new DealAppraise();//卖家评价
	
	DealAppraise buyerAppraise = new DealAppraise();//买家评价
	
	byte state;
	//非中介模式:0买家下单,1买家已付款,2卖家已发货(入库),3买家确认收货() (互评-> 交易完成);
	//中介模式：0买家下单,1买家付款给中介,2中介已收款,3卖家发货,4买家确认收货 ,5中介给卖家付款(互评-> 交易完成);
	
	boolean revoke;//是否撤销了
	
	List<String> times = new ArrayList<String>();
	
	public void _serialize(DataBuffer buffer) {
		buffer.putLong(id);
		buffer.putLong(dealId);
		buffer.putLong(buyId);
		UserCharacter user = CTRL.search(buyId);
		buffer.putUTF(user.getNikeName());
		buffer.putInt(num);
		buffer.putUTF(price + "");
		buffer.put(state);
		buffer.put(helpFlag);
		for (byte i = 0  ; i <= state ; i++){
			buffer.putUTF(times.get(i));
		}
		sellerAppraise.serialize(buffer);
		buyerAppraise.serialize(buffer);
	}
	
	public void serialize(DataBuffer buffer) {
		buffer.put(state);
		for (byte i = 0  ; i <= state ; i++){
			buffer.putUTF(times.get(i));
		}
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDealId() {
		return dealId;
	}

	public void setDealId(long dealId) {
		this.dealId = dealId;
	}

	public long getBuyId() {
		return buyId;
	}

	public void setBuyId(long buyId) {
		this.buyId = buyId;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}
	
	public DealAppraise getSellerAppraise() {
		return sellerAppraise;
	}

	public void setSellerAppraise(DealAppraise sellerAppraise) {
		this.sellerAppraise = sellerAppraise;
	}

	public DealAppraise getBuyerAppraise() {
		return buyerAppraise;
	}

	public void setBuyerAppraise(DealAppraise buyerAppraise) {
		this.buyerAppraise = buyerAppraise;
	}

	public boolean isRevoke() {
		return revoke;
	}

	public void setRevoke(boolean revoke) {
		this.revoke = revoke;
	}

	public String appraiseSerialize(){
		return sellerAppraise.serialize() + "|" + buyerAppraise.serialize();
	}
	
	public void appraiseDeserialize(String str) {
		if (StringUtil.isNull(str)){
			return;
		}
		String[] ss = str.split("\\|");
		sellerAppraise.deserialize(ss[0]);
		buyerAppraise.deserialize(ss[1]);
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public byte getHelpFlag() {
		return helpFlag;
	}

	public void setHelpFlag(byte helpFlag) {
		this.helpFlag = helpFlag;
	}

	public String timesTostr(){
		return JsonUtil.ObjectToJsonString(times);
	}
	
	public void strToTimes(String str){
		if (StringUtil.isNull(str)){
			return;
		}
		times = JsonUtil.JsonToObjectList(str,String.class);
	}
	
	@Override
	public int compareTo(DealOrder o) {
		DateTime time1 = TimeUtils.getTime(times.get(0));
		DateTime time2 = TimeUtils.getTime(o.times.get(0));
		if (time1.isBefore(time2)){
			return 1;
		}else{
			return -1;
		}
	}

	public void save() {
		if (needSave){
			DB.getDealOrderDao().save(this);
			needSave = false;
		}
	}
	
	public void addTimes(byte state){
		String str = TimeUtils.formatYear(TimeUtils.now());
		times.add(state,str);
		this.state = state;
		if ((state == ORDER_FINISH_RUKU && helpFlag == 0) || (state == ORDER_FINISH_XIANHUO && helpFlag == 1)){
			//释放买家信用,提升双方信用积分
			float total_value = num * price;
			UserCharacter buyer = CTRL.search(buyId);
			if (buyer != null){
				Credit credit = buyer.getCredit();
				credit.addDealVale(total_value);
				buyer.setNeedSave(true);
			}
			Deal deal = CTRL.tryToSearch(dealId);
			if (deal != null){
				UserCharacter seller = CTRL.search(deal.getUid());
				if (seller != null){
					Credit credit = seller.getCredit();
					credit.addDealVale(total_value);
					seller.setNeedSave(true);
				}
			}
		}
	}
	
	public String getDealTime(){
		return times.get(0);
	}
	
	public String getSellerName(){
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			UserCharacter user = CTRL.search(deal.getUid());
			if (deal.sellFlag == 1){
				user = CTRL.search(deal.getUid());
			}else{
				user = CTRL.search(buyId);
			}
			if (user != null){
				return user.getNikeName();
			}
		}
		return "";
	}
	
	public String getBuyerName(){
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			UserCharacter user = CTRL.search(deal.getUid());
			if (deal.sellFlag == 0){
				user = CTRL.search(deal.getUid());
			}else{
				user = CTRL.search(buyId);
			}
			if (user != null){
				return user.getNikeName();
			}
		}
		return "";
	}
	
	public String getDealName(){
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			return deal.getName();
		}
		return "";
	}
	
	public void getSimpleDes(Module module){
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			String str = null;
			String[] ss = deal.getBourse().split(",");
			if ((helpFlag == 0 && state == ORDER_FINISH_RUKU) || (helpFlag == 1 && state == ORDER_FINISH_XIANHUO)){
				str = ss[1] + "[0000ff](" + (deal.getType() == 0 ? "入库" : "现货") + ")[-][ff0000]" + deal.getName() + "[-]已经成交[ff0000]" + num + "[-]" + deal.getMonad();
			}else{
				str = ss[1] + "[0000ff](" + (deal.getType() == 0 ? "入库" : "现货") + ")[-][ff0000]" + deal.getName() + "[-]正在交易[ff0000]" + num + "[-]" + deal.getMonad();
			}
			module.add(dealId);
			module.add(id);
			module.add(str);
			String time = times.get(0);
			module.add(time);
		}
	}
	
	public boolean Appraise(){
		return sellerAppraise.isCompleted() && buyerAppraise.isCompleted();
	}
}
