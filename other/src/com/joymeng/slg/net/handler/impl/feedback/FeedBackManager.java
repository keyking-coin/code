package com.joymeng.slg.net.handler.impl.feedback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.domain.chat.chatdata.RedisConfig;
import com.joymeng.slg.world.GameConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class FeedBackManager implements Instances {
	private static FeedBackManager instance = new FeedBackManager();

	static int serverId = ServiceApp.instanceId - GameConfig.SERVER_LIST_ID;
	static int appid = 1001;
	
	Map<Long, FeedBack> map = new HashMap<Long, FeedBack>();

	JedisPool redisPool;

	int useDBIndex;

	String feedback = "RedAlert_" + String.valueOf(ServiceApp.instanceId) + "_"+"playerFeedback";

	public static FeedBackManager getInstance() {
		return instance;
	}

	public static void setInstance(FeedBackManager instance) {
		FeedBackManager.instance = instance;
	}

	public void record(long uid, String channelId,String come,String content) {
		long time = TimeUtils.nowLong();
		Jedis jedis = getConnectResource();
        String feed = String.valueOf(uid); 
        List<String> serList = new ArrayList<String>();
        String list = jedis.hget(feedback, feed);
        FeedBack back = new FeedBack(uid, channelId, TimeUtils.chDate(time), come,content);
		if (!StringUtils.isNull(list)) {
			serList = JsonUtil.JsonToObjectList(jedis.hget(feedback, feed),String.class);
		}
		serList.add(JsonUtil.ObjectToJsonString(back));
		jedis.hset(feedback, feed, JsonUtil.ObjectToJsonString(serList));
		release(jedis);
	}
		
	public void load() throws Exception{
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
	/*
	 *gm后他记录玩家反馈 
	 */
	
	public void postFeedback(long uid, String channelId, String come,
			String content) throws IOException {
		long time = TimeUtils.nowLong();
		List<Object> list = new ArrayList<Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("app_id", appid);
		map.put("server_id", serverId);
		map.put("channel_id", channelId);
		map.put("uid", uid);
		map.put("content", content);
		map.put("time", TimeUtils.chDate(time));
		map.put("come", come);
		list.add(map);	
		try {
			URL url = new URL("http://netunion.joymeng.com/index.php?m=Api&c=Others&a=index");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true); // 是否输入参数
			StringBuffer params = new StringBuffer();
			params.append("&api_type=").append("feedback").append("&api_data=")
					.append(java.net.URLEncoder.encode(JsonUtil.ObjectToJsonString(list), "UTF-8"));
			byte[] bypes = params.toString().getBytes();
			connection.getOutputStream().write(bypes);// 输入参数
			InputStream in = connection.getInputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];
			int length = 0;
			while ((length = in.read(buffer)) != -1) {
				bos.write(buffer, 0, length);
			}
			in.close();
			String str = new String(bos.toByteArray(), "UTF-8");
			GameLog.info(str);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
}
