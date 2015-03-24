package com.szboc.platform.component.communication.server.handler;

/**
 * 服务端处理器接口
 * 
 * @author likw
 * 
 */
public interface IHandler {

	/**
	 * 处理器组件初始化
	 */
	public void init();

	/**
	 * 处理器组件销毁
	 */
	public void destory();

	/**
	 * 处理器组件执行task
	 * 
	 * @param task
	 *            待处理的任务
	 */
	public void execute(IHandleTask task);
}
