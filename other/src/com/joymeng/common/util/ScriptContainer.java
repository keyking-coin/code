/**
 * 
 */
package com.joymeng.common.util;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Dream
 */
public class ScriptContainer {
	public final static ScriptContainer instance = new ScriptContainer();
	public HashMap<String, Script> scripts = new HashMap<String, Script>();
	public HashMap<String, ScheduledFuture<?>> scriptTasks = new HashMap<String, ScheduledFuture<?>>();
}
