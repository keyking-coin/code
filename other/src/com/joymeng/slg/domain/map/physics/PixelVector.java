package com.joymeng.slg.domain.map.physics;

/**
 * 像素点阵
 * @author tanyong
 *
 */
public class PixelVector {
	
	public float x;
	
	public float y;
	
	public PixelVector(float x ,float y){
		this.x = x;
		this.y = y;
	}

	public float distance(PixelVector src){
		if (src != null){
			float result = (float)Math.hypot(x - src.x,y - src.y);
			return result;
		}
		return 0;
	}
	
	public PixelVector center(PixelVector pixel){
		float nx = x + pixel.x;
		float ny = y + pixel.y;
		return new PixelVector(nx/2,ny/2);
	}
}
