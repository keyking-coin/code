package com.joymeng.slg.domain.object.build;

public interface BuildLinkQueue {
	
	/**
	 * 建筑队列被使用
	 * @param buildId
	 */
	public void addBuildQueue(long buildId);
	
	/**
	 * 研究队列被使用
	 * @param buildId
	 */
	public void addResearchQueue(long buildId);
	
	/**
	 * 队列使用完毕
	 * @param buildId
	 */
	public void removeQueue(long buildId);
}
