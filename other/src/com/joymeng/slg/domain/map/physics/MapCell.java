package com.joymeng.slg.domain.map.physics;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.object.IObject;

/**
 * 地图静止的格子信息
 * @author tanyong
 *
 */
public class MapCell {
	
	MapCellType  type;//格子类型
	
	long id;//索引的时候用
	
	Class<? extends IObject> typeKey;//索引的时候用
	
	boolean slow;//是否是减速块
	
	List<Long> garrisons;//被那些部队驻防
	
	List<Long> expedites;//行军部队从我出发或者到达
	
	public MapCell(MapObject obj){
		init(obj);
	}

	public MapCell(MapCellType type){
		this.type = type;
	}
	
	public void init(MapObject obj){
		type = obj.cellType();
		id   = obj.getId();
		typeKey = obj.getClass();
	}
	
	public void clear(MapCellType type){
		this.type = type;
		id   = 0;
		typeKey = null;
	}
	
	public MapCellType getType() {
		return type;
	}

	public void setType(MapCellType type) {
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Class<? extends IObject> getTypeKey() {
		return typeKey;
	}

	public void setTypeKey(Class<? extends IObject> typeKey) {
		this.typeKey = typeKey;
	}

	public boolean isSlow() {
		return slow;
	}

	public List<Long> getGarrisons() {
		return garrisons;
	}

	public List<Long> getExpedites() {
		return expedites;
	}

	public void setSlow(boolean slow) {
		this.slow = slow;
	}
	
	public synchronized void occupyer(long id){
		if (garrisons == null){
			garrisons = new ArrayList<Long>();
		}
		if (!garrisons.contains(id)){
			garrisons.add(id);
		}
	}
	
	public synchronized void removeOccupyer(long id){
		if (garrisons == null){
			garrisons = new ArrayList<Long>();
		}
		if (garrisons.contains(id)){
			garrisons.remove(id);
		}
	}
	
	public synchronized void expedite(long id){
		if (expedites == null){
			expedites = new ArrayList<Long>();
		}
		if (!expedites.contains(id)){
			expedites.add(id);
		}
	}
	
	public synchronized void removeExpedite(long id){
		if (expedites == null){
			expedites = new ArrayList<Long>();
		}
		if (expedites.contains(id)){
			expedites.remove(id);
		}
	}
}
