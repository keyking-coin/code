package com.joymeng;

import com.joymeng.list.NoticeManager;
import com.joymeng.list.ServerManager;
import com.joymeng.push.PushManager;
import com.joymeng.slg.dao.DBManager;
import com.joymeng.slg.domain.activity.ActivityManager;
import com.joymeng.slg.domain.actvt.ActvtManager;
import com.joymeng.slg.domain.chat.ChatManager;
import com.joymeng.slg.domain.chat.chatdata.ChatDataManager;
import com.joymeng.slg.domain.chat.primary.ChatPrimaryKeyData;
import com.joymeng.slg.domain.data.DataManager;
import com.joymeng.slg.domain.map.BigMapWorld;
import com.joymeng.slg.domain.name.RoleNameManager;
import com.joymeng.slg.domain.object.rank.RankManager;
import com.joymeng.slg.domain.primary.PrimaryKeyData;
import com.joymeng.slg.union.UnionManager;
import com.joymeng.slg.world.TaskPool;
import com.joymeng.slg.world.World;
import com.joymeng.slg.world.WorldStaticInfo;

public interface Instances {
	public static final World world = World.getInstance();
	public static final DBManager dbMgr = DBManager.getInstance();
	public static final TaskPool taskPool = TaskPool.getInstance();
	public static final DataManager dataManager = DataManager.getInstance();
	public static final BigMapWorld mapWorld     = BigMapWorld.getInstance();
	public static final PrimaryKeyData keyData   = PrimaryKeyData.getInstance();
	public static final RoleNameManager nameManager = RoleNameManager.getInstance();
	public static final UnionManager unionManager = UnionManager.getInstance();
	public static final RankManager rankManager = RankManager.getInstance();
	public static final ChatManager chatMgr = ChatManager.getInstance();
	public static final ChatPrimaryKeyData chatKeyData = ChatPrimaryKeyData.getInstance();
	public static final ChatDataManager chatDataManager = ChatDataManager.getInstance();
	public static final ServerManager serverManager = ServerManager.getInstance();
	public static final WorldStaticInfo worldSInfo = WorldStaticInfo.getInstance();
	public static final ActivityManager activityManager = ActivityManager.getInstance();
	public static final ActvtManager actvtMgr = ActvtManager.getInstance();
	public static final PushManager push = PushManager.getInstance();
	public static final NoticeManager notice = NoticeManager.getInstance();
}
