package com.joymeng.slg.domain.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.GeneralEvent;
import com.joymeng.slg.exp.GameEventParamError;

public abstract class AbstractObject implements IObject , Instances{
	
	Map<Short,List<GameEvent>> handlers = new HashMap<Short,List<GameEvent>>();
	
	protected boolean removing = false;
	
	boolean deleteFlag = false;
	
	boolean mapThreadFlag = false;
	
	protected boolean savIng = false;
	
	public AbstractObject (){
		_registerAll();
	}
	
	@Override
	public void registerEventHandler(GameEvent event, short code) {
		List<GameEvent> events = handlers.get(code);
		if (events == null){
			events = new ArrayList<GameEvent>();
			handlers.put(code,events);
		}
		if (!events.contains(event)){
			events.add(event);
		}
	}

	@Override
	public void handleEvent(Object... params){
		if (params == null || params.length < 1){
			GameEventParamError error = new GameEventParamError(this);
			GameLog.error(error.getMessage(),error);
			return ;
		}
		List<GameEvent> events = handlers.get(params[0]);
		if (events != null){
			for (int i = 0 ; i < events.size() ; i++){
				GameEvent event = events.get(i);
				event.handle(this,params);
			}
		}
	}
	
	@Override
	public boolean isRemoving() {
		return removing;
	}

	@Override
	public void tick(long now) {
		for (List<GameEvent> events : handlers.values()){
			for (int i = 0 ; i < events.size() ; i++){
				GameEvent event = events.get(i);
				event.tick();
			}
		}
		if (!removing){
			_tick(now);
		}
	}
	
	public void _registerAll(){
		GeneralEvent general = new GeneralEvent();
		registerEventHandler(general,GameEvent.ADD_LIST);
		registerEventHandler(general,GameEvent.REMOVE_LIST);
		registerEventHandler(general,GameEvent.SAVE_MYSELF);
		registerAll();
	}
	
	@Override
	public void remove(){
		handleEvent(GameEvent.REMOVE_LIST);
	}
	
	@Override
	public void save() {
		if (savIng){
			return;
		}
		savIng = true;
		handleEvent(GameEvent.SAVE_MYSELF);
	}
	
	public void addSelf(){
		handleEvent(GameEvent.ADD_LIST);
	}
	
	public void removing(){
		removing = true;
	}
	
	public void setDeleteFlag(boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	@Override
	public boolean delete() {
		return deleteFlag;
	}
	
	public void setMapThreadFlag(boolean mapThreadFlag) {
		this.mapThreadFlag = mapThreadFlag;
	}

	@Override
	public boolean needRemoveAtMapThread(){
		return mapThreadFlag;
	}
	
	@Override
	public void over() {
		savIng = false;
	}

	@Override
	public long getId() {
		return 0;
	}

	@Override
	public boolean saving() {
		return savIng;
	}

	/**
	 * 注册与自己有关的事件
	 */
	public abstract void registerAll();
	
	public abstract void _tick(long now);
}
