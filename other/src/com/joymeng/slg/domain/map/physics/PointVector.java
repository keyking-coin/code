package com.joymeng.slg.domain.map.physics;

import com.joymeng.slg.world.GameConfig;

/**
 * 格子点阵
 * @author tanyong
 *
 */
public class PointVector {
	
	public float x;
	public float y;
	
	public PointVector(float x ,float y){
		this.x = x;
		this.y = y;
	}
	
	public int getPosition(){
		return (int)y * GameConfig.MAP_WIDTH + (int)x;
	}


	public PointVector center(PointVector point){
		float nx = x + point.x;
		float ny = y + point.y;
		return new PointVector(nx/2,ny/2);
	}

	public float distance(PointVector pixel_s) {
		if (pixel_s != null){
			float result = (float)Math.hypot(x - pixel_s.x,y - pixel_s.y);
			return result;
		}
		return 0;
	}
	
	
}
