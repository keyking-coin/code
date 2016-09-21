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
		return getPosition(x,y);
	}

	public static int getPosition(float x,float y){
		return getPosition((int)x,(int)y);
	}
	
	public static int getPosition(int x,int y){
		return y * GameConfig.MAP_WIDTH + x;
	}
	
	public static int getX(float pos){
		return getX((int)pos);
	}
	
	public static int getX(int pos){
		return pos % GameConfig.MAP_WIDTH;
	}
	
	public static int getY(float pos){
		return getY((int)pos);
	}
	
	public static int getY(int pos){
		return pos / GameConfig.MAP_WIDTH;
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
