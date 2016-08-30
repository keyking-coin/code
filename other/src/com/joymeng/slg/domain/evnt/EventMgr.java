package com.joymeng.slg.domain.evnt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EventMgr {
	protected Map<String, List<IEvnt>> events = new HashMap<String, List<IEvnt>>();

	public void Listen(String event, IEvnt exe)
	{
		List<IEvnt> evnts;
		if (events.containsKey(event)) {
			evnts = events.get(event);
		}
		else {
			evnts = new ArrayList<IEvnt>();
			events.put(event, evnts);
		}
		if (!evnts.contains(exe)) {
			evnts.add(exe);
		}
	}
	
	public void Remove(String event, IEvnt exe)
	{
		if (!events.containsKey(event)) {
			return;
		}
		
		List<IEvnt> evnts = events.get(event);
		evnts.remove(exe);
	}
	
	public void Notify(String event, String value)
	{
		if (!events.containsKey(event)) {
			return;
		}
		List<IEvnt> evnts = events.get(event);
		for (int i = 0; i < evnts.size(); i++)
		{
			evnts.get(i).execute(event, value);
		}
	}
}
