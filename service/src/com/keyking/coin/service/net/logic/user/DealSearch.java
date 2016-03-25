package com.keyking.coin.service.net.logic.user;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.domain.condition.SearchCondition;
import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class DealSearch extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		int type  = buffer.getInt();
		long uid  = buffer.getLong();
		UserCharacter user = CTRL.search(uid);
		List<Deal> deals = null;
		resp.add(type);
		switch(type){
		case 1:
			deals = searchSells(user);//ddjy 我所发的所有在有效期的帖子
			break;
		case 2:
			deals = searchDealing(user);//zzjy 有两人参与的帖子，未到评分这一步的
			break;
		case 3:
			deals = searchConfirmOrders(user);//ddpj 已经完成收货确认,但没有互评的（这一步未完成也应该释放信用额度）
			break;
		case 4:
			deals = searchDealOver(user);//ywcjy 已经完成评分的，包括交割失败的
			break;
		case 5:
			deals = searchDealRevert(user);//wdct 所有我发布的到期系统自动撤销的帖子或者我自己撤销的帖子
			break;
		case 6:
			deals = searchFavorite(user);//我的收藏夹
			break;
		}
		if (deals != null){
			resp.setSucces();
			resp.add(deals);
		}
		return resp;
	}
	
	private List<Deal> searchSells(UserCharacter user){
		SearchCondition condition = new SearchCondition();
		condition.setSeller(user.getNikeName());
		condition.setValid("到目前有效");
		List<Deal> deals = CTRL.getSearchDeals(condition);
		return deals;
	}
	
	private List<Deal> searchDealing(UserCharacter user){
		SearchCondition condition = new SearchCondition();
		condition.setDealing(true);
		List<Deal> deals = CTRL.getSearchDeals(condition);
		long uid = user.getId();
		for (int i = 0  ; i < deals.size() ;){
			Deal deal = deals.get(i);
			if (!deal.checkJoin(uid)){
				deals.remove(i);
			}else{
				i++;
			}
		}
		return deals;
	}
	
	private List<Deal> searchConfirmOrders(UserCharacter user){
		SearchCondition condition = new SearchCondition();
		condition.setConfirming(true);
		List<Deal> deals = CTRL.getSearchDeals(condition);
		long uid = user.getId();
		for (int i = 0  ; i < deals.size() ;){
			Deal deal = deals.get(i);
			if (!deal.checkJoin(uid)){
				deals.remove(i);
			}else{
				i++;
			}
		}
		return deals;
	}
	
	private List<Deal> searchDealOver(UserCharacter user){
		SearchCondition condition = new SearchCondition();
		condition.setOver(true);
		List<Deal> deals = CTRL.getSearchDeals(condition);
		long uid = user.getId();
		for (int i = 0  ; i < deals.size() ;){
			Deal deal = deals.get(i);
			if (!deal.checkJoin(uid)){
				deals.remove(i);
			}else{
				i++;
			}
		}
		return deals;
	}
	
	private List<Deal> searchDealRevert(UserCharacter user){
		SearchCondition condition = new SearchCondition();
		condition.setSeller(user.getNikeName());
		condition.setValid("到目前无效");
		return CTRL.getSearchDeals(condition);
	}
	
	private List<Deal> searchFavorite(UserCharacter user){
		List<Deal> result = new ArrayList<Deal>();
		List<Long> favorites = user.getFavorites();
		for (Long favorite : favorites){
			Deal deal = CTRL.tryToSearch(favorite.longValue());
			if (deal != null){
				result.add(deal);
			}
		}
		return result;
	}
}
