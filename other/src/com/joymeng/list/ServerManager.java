package com.joymeng.list;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.http.HtppOprateType;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.JoyServiceApp;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.services.utils.XmlUtils;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.domain.chat.chatdata.RedisConfig;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;
import com.joymeng.slg.world.GameConfig;
import com.joymeng.slg.world.TaskPool;

public class ServerManager implements Instances{
	
	private static ServerManager instance = new ServerManager();
	
    Map<Integer,ServiceState> stateMap = new ConcurrentHashMap<Integer,ServiceState>();
    
    List<Server> servers = new CopyOnWriteArrayList<Server>();
    
    List<ChannelFilter> channels = new CopyOnWriteArrayList<ChannelFilter>();
    
    List<LanguageFilter> languages = new CopyOnWriteArrayList<LanguageFilter>();
    
    JedisPool redisPool;
   
    int useDBIndex;
    
    String  record = "uidLoginServer";
    
	public static ServerManager getInstance() {
		return instance;
	}

	public Map<Integer, ServiceState> getStateMap() {
		return stateMap;
	}
	
	public void record(long uid, String serverId) {
		Jedis jedis = getConnectResource();
        String server = String.valueOf(uid); 
        List<String> serList = new ArrayList<String>();
        String list = jedis.hget(record, server);
		if (!StringUtils.isNull(list)) {
			serList = JsonUtil.JsonToObjectList(jedis.hget(record, server),String.class);
		}
		if (!serList.contains(serverId)) {
			serList.add(serverId);
		}
		jedis.hset(record, server, JsonUtil.ObjectToJsonString(serList));
		release(jedis);
		GameLog.info("player " + uid + " regist " + serverId+ " and record server to redis successful");
	}
		
	public void loadRedis() throws Exception{
		if (redisPool == null){
			Properties properties = new Properties();
			File file = new File(Const.CONF_PATH + "redis.properties");
			properties.load(new FileInputStream(file));
			RedisConfig rc = RedisConfig.load(properties);
			JedisPoolConfig config = new JedisPoolConfig();
            //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；  
            //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。  
            config.setMaxActive(rc.getMaxActive());  
            //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。  
            config.setMaxIdle(rc.getMaxIdle());  
            //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；  
            config.setMaxWait(rc.getMaxWait());  
            //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；  
            config.setTestOnBorrow(rc.isTestOnBorrow());
            useDBIndex = rc.getUseIndex();
			redisPool = new JedisPool(config,rc.getIp(),rc.getPort(),rc.getTimeOut());
		}
	}
	
	/**
	 * 获取连接
	 * @return
	 */
	private Jedis getConnectResource(){
		Jedis jedis = redisPool.getResource();
		if (jedis != null){
			jedis.select(useDBIndex);
		}
		return jedis;
	}
	
	/**
	 * 释放连接
	 * @param jedis
	 */
	private void release(Jedis jedis){
		if (jedis != null){
			redisPool.returnResource(jedis);
		}
	}
	
	public void load() {
		_load();
		update();
		taskPool.scheduleAtFixedRate(null,new Runnable() {
			@Override
			public void run() {
				refresh();
			}
		},10,1*TaskPool.SECONDS_PER_MINTUE,TimeUnit.SECONDS);
	}
	
	public void _load() {
		try {
			servers.clear();
			channels.clear();
			languages.clear();
			Document document = XmlUtils.load(Const.CONF_PATH + "NewServersList.xml");
			Element base = document.getDocumentElement();
			Element[] ses = XmlUtils.getChildrenByName(base,"Server");
			for (int i = 0 ; i < ses.length ; i++){
				Element se = ses[i];
				Server s = new Server();
				s.decode(se);
				servers.add(s);
			}
			Element[] fes = XmlUtils.getChildrenByName(base,"ChannelFilter");
			for (int i = 0 ; i < fes.length ; i++){
				Element fe = fes[i];
				ChannelFilter cf = new ChannelFilter();
				cf.decode(fe);
				channels.add(cf);
			}
			Element[] les = XmlUtils.getChildrenByName(base,"LanguageFilter");
			for (int i = 0 ; i < les.length ; i++){
				Element le = les[i];
				LanguageFilter lf = new LanguageFilter();
				lf.decode(le);
				languages.add(lf);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 扫描
	 */
	public void scan() {
		GameLog.info("try to scan list file");
		_load();
		update();
	}
	
	private void update(){
		for (int i = 0 ; i < servers.size() ; i++){
			Server server = servers.get(i);
			ServiceState state = stateMap.get(server.getServerId());
			if (state == null){
				state = new ServiceState(server.getServerId());
				stateMap.put(server.getServerId(), state);
			}
		}
	}
	
	/*
	 * 根据不同渠道显示
	 */
	public String serverToChan(String channelId, String language,long uid) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Server> couldLook = search(channelId);
		Jedis jedis = getConnectResource();
        String ser = String.valueOf(uid); 
        List<String> serList = new ArrayList<String>();
        if(jedis.hget(record, ser) != null){
    	    serList = JsonUtil.JsonToObjectList(jedis.hget(record, ser), String.class);
        }
		for (int i = 0 ; i < couldLook.size() ; i++){
			Server server = couldLook.get(i);
			ServiceState state = stateMap.get(server.getServerId());
			if (state == null){
				continue;
			}
			String name = server.currentName(language,languages);
			ServerStatus must = server.checkMustShow(channelId,channels);
            byte record;
            if(serList==null){
            	record= 0;
            }else if(serList.contains(String.valueOf(server.getServerId()))){
            	record= 1;
            }else{
            	record= 0;
            }
            
            byte st;
            if(must != null){
            	st = must.getKey();
            }else{
            	st =state.getState();
            }
			long time = TimeUtils.nowLong();
			String tipTime = server.getOpenTime();
			String tip = "";
			if (!StringUtils.isNull(tipTime)) {
				long tm = TimeUtils.getTimes(tipTime);
				if (tm > time) {
					String tail = StringUtils.isNull(server.getOpenTimeShow())? tipTime : server.getOpenTimeShow();
					tip = "服务器待开放,开放时间:" + tail;
					st = (byte) 7;
				}
			}
			TranformState tfs = new TranformState(name,server.getServerId(),st,record,tip);
			map.put(tfs.getName(),tfs);
		}
		release(jedis);
		return JsonUtil.ObjectToJsonString(map);
	}
	
	/*
	 * 玩家有角色的服务器(取消不用，暂时保留)
	 */
	public String serverRecord(long uid) {
		Map<String, Object> map = new HashMap<String, Object>();
		Jedis jedis = getConnectResource();
		List<String> serList = JsonUtil.JsonToObjectList(jedis.hget(record, String.valueOf(uid)), String.class);
		if (serList == null) {
			map.put(String.valueOf(uid),"[]");
		} else {
			map.put(String.valueOf(uid),serList);
		}
		return JsonUtil.ObjectToJsonString(map);
	}
	
	/*
	 * 演示服务器
	 */
	public String outPutOnly() {
		Map<String,Object> map = new HashMap<String,Object>();
		ServiceState state = stateMap.get(12298);
		if (state != null){
			String name = searchName(12298,"zh");
			TranformState tfs = new TranformState(name,12298,state.getState(),(byte)0,"");
			map.put(name,tfs);
		}
		return JsonUtil.ObjectToJsonString(map);
	}
	
	/*
	 *运维展示的服务器列表 
	 */
	public String outPutToOp() {
		List<Object> list = new ArrayList<Object>();
		for (ServiceState state : stateMap.values()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("serverId", state.getServiceId());
			map.put("name",searchName(state.getServiceId(),"zh"));
			map.put("state",state.getState());
			map.put("onLineNum",state.getOnLineNum());
			Server server = searchServer(state.getServiceId());
			map.put("maxNum",server.getFullNum());
			list.add(map);
		}
		Map<String, Object> bMap = new HashMap<String, Object>();
		Map<String, Object> aMap = new HashMap<String, Object>();
		bMap.put("serverList",list);
		aMap.put("status",1);
		aMap.put("msg","success");
		aMap.put("data",bMap);
		return JsonUtil.ObjectToJsonString(aMap);
	}
	
	/*
	 *找出一个能通讯的服务器
	 */
	public int getOnlineServerId() {
		for (ServiceState ss : stateMap.values()) {
			if (ss.getState() != ServerStatus.SERVER_STATUS_CLOSE.getKey()
					&& ss.getState() != ServerStatus.SERVER_STATUS_MAINTEN.getKey()
					&& ss.getState() != ServerStatus.SERVER_STATUS_ONREADY.getKey()) {
				return ss.getServiceId();
			}
		}
		return 0;
	}
	
	/*
	 * 所有可以通讯的服务器
	 */
	public List<Integer> ServersWorking() {
		List<Integer> list = new ArrayList<Integer>();
		for (ServiceState ss : stateMap.values()) {
			if (ss.getState() != ServerStatus.SERVER_STATUS_CLOSE.getKey()
					&& ss.getState() != ServerStatus.SERVER_STATUS_MAINTEN.getKey()
					&& ss.getState() != ServerStatus.SERVER_STATUS_ONREADY.getKey()) {
				if (ss.getServiceId() != 12299 && ss.getServiceId() != 12304 && ss.getServiceId() != 12303) { // 把不更新代码的服务器排除
					list.add(ss.getServiceId());
				}
			}
		}
		return list;
	}
	
	
	public void refresh(){
		GameLog.info("try to get server list states");
		int protocolId = 0x00000056;
		ServiceHandler handler = ServiceHandler.REQUEST_HANDLERS.get(protocolId);
		for (ServiceState state : stateMap.values()){
			state.setState(ServerStatus.SERVER_STATUS_CLOSE.getKey());
			state.setOnLineNum(0);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(GameConfig.SYSTEM_TRANFOEM_ID);
			targetInfo.setCid(state.getServiceId());
			TransmissionResp resp = new TransmissionResp();
			resp.setUserInfo(targetInfo);
			resp.getParams().put(protocolId); //指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_REQUEST.ordinal());//
			resp.getParams().put(ServiceApp.instanceId);// 从哪里来的
			resp.getParams().put(GameConfig.SYSTEM_TRANFOEM_ID);
			resp.getParams().put(state.getServiceId());
			JoyServiceApp.getInstance().sendMessage(resp);
			RefreshList rl = new RefreshList(state.getServiceId());
			handler.addNextDo(GameConfig.SYSTEM_TRANFOEM_ID,rl);
		}
	}
	
	public void setState(int serverId , byte state){
		ServiceState service = stateMap.get(serverId);
		if (service != null){
			GameLog.info("serverId = " + serverId + " change state from " + service.getState() + " to " + state);
			service.setState(state);
		}
	}
	
	public String searchName(int serverId , String languageFalg){
		for (int i = 0 ; i < languages.size() ; i++){
			LanguageFilter language = languages.get(i);
			if (language.id.equals(languageFalg)){
				return language.check(serverId);
			}
		}
		return null;
	}
	
	public List<Server> search(String channelId){
		List<Server> result = new ArrayList<Server>();
		for (int i = 0 ; i < servers.size() ; i++){
			Server server = servers.get(i);
			if (server.check(channelId,channels)){
				result.add(server);
			}
		}
		return result;
	}
	
	private Server searchServer(int serviceId){
		for (int i = 0 ; i < servers.size() ; i++){
			Server server = servers.get(i);
			 if(server.getServerId() == serviceId){
				 return server;
			 }
		 }
		return null;	
	}
	
	class RefreshList implements NeedContinueDoSomthing{
		int serviceId = 0;
		public RefreshList(int serviceId){
			this.serviceId = serviceId;
		}
		@Override
		public int getId() {
			return serviceId;
		}
		@Override
		public JoyProtocol succeed(UserInfo info, ParametersEntity params) {
			synchronized(this){
			    String data = params.get(3);
			    int comeFrom = params.get(2);
			    if (!StringUtils.isNull(data)){
			    	ServiceState state = stateMap.get(comeFrom);
	    			ServiceState newState = JsonUtil.JsonToObject(data,ServiceState.class);
	    			Server server = searchServer(comeFrom);
	    			if (server == null){
	    				return null;
	    			}
	    			state.setOnLineNum(newState.getOnLineNum());
	    			int  count = newState.getOnLineNum();
	    			if (count < server.getNewNum()) {
	    				state.setState(ServerStatus.SERVER_STATUS_NORMAL.getKey());//正常服务器
					} else if (count >= server.getNewNum() && count < server.getNormalNum()) {
						state.setState(ServerStatus.SERVER_STATUS_BUSY.getKey());//服务器繁忙状态
					} else if (count >= server.getNormalNum() && count < server.getFullNum()) {
						state.setState(ServerStatus.SERVER_STATUS_FULL.getKey());//服务器爆满状态
					} else {
						state.setState(ServerStatus.SERVER_STATUS_ONREADY.getKey());//服务器爆满状态,老玩家可进
					}
			    	GameLog.info("[serverId = " + comeFrom + " onlineNum = " + newState.getOnLineNum() + "  state = " + state.getState() + "]");
			    }
				return null;
			}
		}

		@Override
		public JoyProtocol fail(UserInfo info, ParametersEntity params) {
			return null;
		}
		
	}
}
