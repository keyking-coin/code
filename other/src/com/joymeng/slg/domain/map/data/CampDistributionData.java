package com.joymeng.slg.domain.map.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;
/**
 * 军营刷新固化标
 * @author tanyong
 *
 */
public class CampDistributionData implements DataKey {
	String id;
	String name;
	byte type;
	int centerX;
	int centerY;
	int rangeX;
	int rangeY;
	int count;
	List<MapEntiy> needDistribution;
	List<MapEntiy> needProbavility;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public int getCenterX() {
		return centerX;
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}

	public int getRangeX() {
		return rangeX;
	}

	public void setRangeX(int rangeX) {
		this.rangeX = rangeX;
	}

	public int getRangeY() {
		return rangeY;
	}

	public void setRangeY(int rangeY) {
		this.rangeY = rangeY;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<MapEntiy> getNeedDistribution() {
		return needDistribution;
	}

	public void setNeedDistribution(List<MapEntiy> needDistribution) {
		this.needDistribution = needDistribution;
	}

	public List<MapEntiy> getNeedProbavility() {
		return needProbavility;
	}

	public void setNeedProbavility(List<MapEntiy> needProbavility) {
		this.needProbavility = needProbavility;
	}
	
	public int[][] computeLevelIndexs(){
		int[][] result = new int[2][needProbavility.size()];
		for (int i = 0 ; i < needProbavility.size() ; i++){
			MapEntiy entity = needProbavility.get(i);
			result[0][i] = i;
			result[1][i] = Integer.parseInt(entity.getpValue());
		}
		return result;
	}
	
	@Override
	public Object key() {
		return id;
	}
}
