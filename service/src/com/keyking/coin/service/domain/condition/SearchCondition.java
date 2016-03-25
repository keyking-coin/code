package com.keyking.coin.service.domain.condition;

import org.joda.time.DateTime;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.util.Instances;
import com.keyking.coin.util.TimeUtils;

public class SearchCondition implements Instances{
	String type   = "null";
	String flag   = "null";
	String bourse = "null";
	String title  = "null";
	String seller = "null";
	String buyer  = "null";
	String valid  = "null";//是否有效
	boolean agency = false;//是否是中介服务
	boolean dealing = false;//正在交易，未到评分这一步的。
	boolean confirming = false;//已经完成收货确认，但没有互评的（这一步未完成也应该释放信用额度）
	boolean over = false;//已经完成评分的，包括交割失败的
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getBourse() {
		return bourse;
	}

	public void setBourse(String bourse) {
		this.bourse = bourse;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}
	
	public String getBuyer() {
		return buyer;
	}

	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public boolean isAgency() {
		return agency;
	}

	public void setAgency(boolean agency) {
		this.agency = agency;
	}

	public boolean isDealing() {
		return dealing;
	}

	public void setDealing(boolean dealing) {
		this.dealing = dealing;
	}

	public boolean isConfirming() {
		return confirming;
	}

	public void setConfirming(boolean confirming) {
		this.confirming = confirming;
	}

	public boolean isOver() {
		return over;
	}

	public void setOver(boolean over) {
		this.over = over;
	}

	public boolean legal(Deal deal){
		if (!type.equals("null")){
			if ((type.equals("入库") && deal.getType() == 1) || (type.equals("现货") && deal.getType() == 0)){
				return false;
			}
		}
		if (!flag.equals("null")){
			if ((flag.equals("买家") && deal.getSellFlag() == 1) || (flag.equals("卖家") && deal.getSellFlag() == 0)){
				return false;
			}
		}
		if (!bourse.equals("null") && !bourse.equals("全部文交所")){
			String[] ss = deal.getBourse().split(",");
			if (!bourse.equals(ss[1])){
				return false;
			}
		}
		if (!title.equals("null")){
			if (!title.equals(deal.getName())){
				return false;
			}
		}
		if (!seller.equals("null")){
			String name = CTRL.search(deal.getUid()).getNikeName();
			if (!seller.equals(name)){
				return false;
			}
		}
		if (!buyer.equals("null")){
			if (!deal.checkBuyerName(buyer)){
				return false;
			}
		}
		//有效时间发帖时间
		if (!valid.equals("null")){
			DateTime now = TimeUtils.now();
			DateTime dealTime = TimeUtils.getTime(deal.getValidTime());
			if (valid.equals("到目前有效")){
				if (now.isBefore(dealTime)){
					return false;
				}
			}else if (valid.equals("到目前无效")){
				if (dealTime.isBefore(now)){
					return false;
				}
			}
		}
		if (agency && deal.getHelpFlag() == 0){
			return false;
		}
		if (dealing){
			return deal.isDealing();
		}
		if (confirming){
			return deal.isConfirming();
		}
		if (over){
			return deal.isOver();
		}
		return true;
	}
}
