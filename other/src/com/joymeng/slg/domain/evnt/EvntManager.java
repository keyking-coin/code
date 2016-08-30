package com.joymeng.slg.domain.evnt;


public class EvntManager extends EventMgr {

//	private Map<String, List<IEvnt>> events = new HashMap<String, List<IEvnt>>();
	
	private static EvntManager instance = new EvntManager();
	public static EvntManager getInstance() {
		return instance;
	}
	
	private EvntManager() {
		
	}
	
//	public void Listen(String event, IEvnt exe)
//	{
//		List<IEvnt> evnts;
//		if (events.containsKey(event)) {
//			evnts = events.get(event);
//		}
//		else {
//			evnts = new ArrayList<IEvnt>();
//			events.put(event, evnts);
//		}
//		evnts.add(exe);
//	}
//	
//	public void Remove(String event, IEvnt exe)
//	{
//		if (!events.containsKey(event)) {
//			return;
//		}
//		
//		List<IEvnt> evnts = events.get(event);
//		evnts.remove(exe);
//	}
//	
//	public void Notify(String event, String value)
//	{
//		if (!events.containsKey(event)) {
//			return;
//		}
//		List<IEvnt> evnts = events.get(event);
//		for (int i = 0; i < evnts.size(); i++)
//		{
//			evnts.get(i).execute(event, value);
//		}
//	}
}
