package com.joymeng.slg.domain.map.impl.still.union;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.union.UnionBody;

/**
 * 征服者
 * @author tanyong
 *
 */
public class Conquerer implements Instances{
	String unionName = "";
	String leaderName = "";
	List<AttackerDamage> damagers = new ArrayList<AttackerDamage>();
	
	public Conquerer(){
		
	}
	
	public Conquerer(UnionBody union , Map<Long,Integer> damages){
		unionName  = union.getName();
		leaderName = union.getLeaderName();
		List<AttackerDamage> temp = new ArrayList<AttackerDamage>();
		for (Long key : damages.keySet()){
			Role role = world.getRole(key.longValue());
			if (role.getUnionId() == union.getId()){
				AttackerDamage ad = new AttackerDamage();
				ad.setUid(key.longValue());
				ad.setNum(damages.get(key).intValue());
				ad.setName(role.getName());
				temp.add(ad);
			}
		}
		Collections.sort(temp);
		int max = Math.min(3,temp.size());
		for (int i = 0 ; i < max ; i++){
			AttackerDamage ad = temp.get(i);
			damagers.add(ad);
		}
	}
	
	public void serialize(JoyBuffer out) {
		out.putInt(1);
		out.putPrefixedString(unionName,JoyBuffer.STRING_TYPE_SHORT);
		out.putPrefixedString(leaderName,JoyBuffer.STRING_TYPE_SHORT);
		out.putInt(damagers.size());
		for (int j = 0 ; j < damagers.size() ; j++){
			AttackerDamage ad = damagers.get(j);
			ad.serialize(out);
		}
	}
	
	public void serialize(SqlData data){
		JoyBuffer buffer = JoyBuffer.allocate(1024);
		serialize(buffer);
		data.put(DaoData.RED_ALERT_NPC_CITY_CONQUERER,buffer.arrayToPosition());
	}
	
	public void deserialize(JoyBuffer buffer){
		buffer.getInt();
		unionName  = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		leaderName = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		int size = buffer.getInt();
		for (int i = 0 ; i < size ; i++){
			AttackerDamage ad = new AttackerDamage();
			ad.deserialize(buffer);
			damagers.add(ad);
		}
	}
}
