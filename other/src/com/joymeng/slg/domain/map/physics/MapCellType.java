package com.joymeng.slg.domain.map.physics;

/**
 * 大地图固定对象类型枚举
 * @author tanyong
 */
public enum MapCellType {
	MAP_CELL_TYPE_NONE(0,false),//空地
	MAP_CELL_TYPE_RESIST(0,false),//阻挡层
	MAP_CELL_TYPE_ROLE_CITY(1,false),//玩家城市
	MAP_CELL_GARRISON(0,false),//玩家据点
	MAP_CELL_TYPE_UNION(1,false),//联盟建筑
	MAP_CELL_TYPE_ECTYPE(1,false),//副本建筑
	MAP_CELL_TYPE_TRIMMING(0,false),//装饰物基本没逻辑
	MAP_CELL_TYPE_RESOURCE(0,false),//资源点。
	MAP_CELL_TYPE_MONSTER(0,false),//怪物。
	MAP_CELL_TYPE_CITY_MOVE(1,false),//玩家迁城点
	MAP_CELL_TYPE_FORTRESS(0,false),//玩家要塞
	MAP_CELL_TYPE_MOVE_PROXY(1,true),//建造迁城点代理,站位用
	MAP_CELL_TYPE_FORTRESS_PROXY(0,true),//建造要塞代理,站位用
	MAP_CELL_TYPE_BARRACKS(0,false),//玩家军营
	MAP_CELL_TYPE_UINON_CITY(2,false),//联盟城市
	MAP_CELL_TYPE_UINON_WAREHOUSE(1,false),//联盟仓库
	MAP_CELL_TYPE_UINON_RESOURCE(1,false),//联盟资源
	MAP_CELL_TYPE_UINON_TOWER(0,false),//联盟防御塔
	MAP_CELL_TYPE_UINON_SATELLITE(0,false),//联盟间谍卫星
	MAP_CELL_TYPE_UINON_NUCLEARSILO(0,false),//联盟核弹发射井
	MAP_CELL_TYPE_UINON_OTHER(0,false),//联盟BUFF类型的建筑
	MAP_CELL_NUCLEARSILO_RADIATION(0,false),//核弹辐射对象
	MAP_CELL_TYPE_BOSS(0,false),//大地图Boss
	MAP_CELL_TYPE_NPC(0,false),//大地图NPC
	MAP_CELL_TYPE_UINON_DETECTOR(0,false)//联盟心灵探测仪
	;
	
	private MapCellType(int volume,boolean proxy){
		this.volume = volume;
		this.proxy  = proxy;
	}
	
	int volume;
	
	boolean proxy;
	
	public int getVolume(){
		return volume;
	}
	
	public boolean isProxy() {
		return proxy;
	}
	
	public static MapCellType search(String name){
		MapCellType[] cells = values();
		for (int i = 0 ; i < cells.length ; i++){
			MapCellType cell = cells[i];
			if (cell.name().equals(name)){
				return cell;
			}
		}
		return null;
	}
	
}
