package com.joymeng.slg.union.data;

public class RecordForGm {
	int fight; // 战争次数
	int isWin;// 战胜次数
	int isFail; // 战败次数
	int attWin;// 进攻胜利次数
	int attFail;// 进攻失败次数
	int defWin;// 防御胜利次数
	int defFail;// 防御失败次数
	int mass; // 集结次数

	public int getFight() {
		return fight;
	}

	public void setFight(int fight) {
		this.fight = fight;
	}

	public int getIsWin() {
		return isWin;
	}

	public void setIsWin(int isWin) {
		this.isWin = isWin;
	}

	public int getIsFail() {
		return isFail;
	}

	public void setIsFail(int isFail) {
		this.isFail = isFail;
	}

	public int getAttWin() {
		return attWin;
	}

	public void setAttWin(int attWin) {
		this.attWin = attWin;
	}

	public int getAttFail() {
		return attFail;
	}

	public void setAttFail(int attFail) {
		this.attFail = attFail;
	}

	public int getDefWin() {
		return defWin;
	}

	public void setDefWin(int defWin) {
		this.defWin = defWin;
	}

	public int getDefFail() {
		return defFail;
	}

	public void setDefFail(int defFail) {
		this.defFail = defFail;
	}

	public int getMass() {
		return mass;
	}

	public void setMass(int mass) {
		this.mass = mass;
	}
	
	public void record(boolean win, boolean mas, boolean attOrdef) {
		fight += 1;
		if (attOrdef) { // 进攻
			if (win) {
				isWin += 1;
				attWin += 1;
			} else {
				isFail += 1;
				attFail += 1;
			}
		} else {       //防御
			if (win) {
				isWin += 1;
				defWin += 1;
			} else {
				isFail += 1;
				defFail += 1;
			}
		}
		if (mas) {
			mass += 1;
		}
	}
    
}
