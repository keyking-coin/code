package com.joymeng.slg.domain.object;

import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.domain.event.GameEvent;

public interface IObject extends DaoData{
	/**
	 * 获取编号
	 * @return
	 */
	public long getId();
	/**
	 * 注册事件逻辑处理handler
	 * @param event
	 * @param code
	 */
	public void registerEventHandler(GameEvent event,short code);
	
	/**
	 * 通知事件机制准备处理事件
	 * @param params
	 */
	public void handleEvent(Object... params);
	
	/**
	 * 主线程时间片段逻辑
	 */
	public void tick(long now);
	
	/**
	 * 设置正在被移除
	 */
	public void removing();
	
	/**
	 * 是否正在被移除
	 * @return
	 */
	public boolean isRemoving();
	
	/**
	 * 移除
	 */
	public void remove();
	
	/**
	 * 移除线程
	 * @return
	 */
	public boolean needRemoveAtMapThread();
}
