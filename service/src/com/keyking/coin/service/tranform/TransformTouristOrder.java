package com.keyking.coin.service.tranform;

import org.joda.time.DateTime;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.util.TimeUtils;

public class TransformTouristOrder implements Comparable<TransformTouristOrder>{
	long dealId;
	long orderId;
	String des;
	String time;
	
	public TransformTouristOrder(){
		
	}
	
	public TransformTouristOrder(Deal deal,DealOrder order){
		dealId = deal.getId();
		orderId = order.getId();
		String[] ss = deal.getBourse().split(",");
		StringBuffer sb = new StringBuffer();
		sb.append(ss[1]);
		sb.append("<span style='color: #CC3366'>");
		sb.append(deal.getType() == 0 ? "(入库)" : "(现货)");
		sb.append("</span>");
		sb.append("<span style='color: #6699CC'>");
		sb.append(deal.getName());
		sb.append("</span>");
		sb.append(order.getPrice() + "元");
		sb.append("成交");
		sb.append("<span style='color: #009933'>");
		sb.append(order.getNum());
		sb.append("</span>");
		sb.append(deal.getMonad());
		des = sb.toString();
		time = order.getTimes().get(0);
	}
	public long getDealId() {
		return dealId;
	}
	
	public void setDealId(long dealId) {
		this.dealId = dealId;
	}
	
	public long getOrderId() {
		return orderId;
	}
	
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	
	public String getDes() {
		return des;
	}
	
	public void setDes(String des) {
		this.des = des;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public int compareTo(TransformTouristOrder o) {
		DateTime d1 = TimeUtils.getTime(time);
		DateTime d2 = TimeUtils.getTime(o.time);
		if (d1.isBefore(d2)){
			return 1;
		}
		return -1;
	}
}
