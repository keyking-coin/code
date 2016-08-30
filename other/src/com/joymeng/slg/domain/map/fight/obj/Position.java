package com.joymeng.slg.domain.map.fight.obj;

public class Position {
	byte col;
    byte row;

	public Position() {
	}

    public void init(String pos){
        String[] ss = pos.split(",");
		row = Byte.parseByte(ss[0]);
		col = Byte.parseByte(ss[1]);
    }

	public byte getCol() {
		return col;
	}

	public void setCol(byte col) {
		this.col = col;
	}

	public byte getRow() {
		return row;
	}

	public void setRow(byte row) {
		this.row = row;
	}
}
