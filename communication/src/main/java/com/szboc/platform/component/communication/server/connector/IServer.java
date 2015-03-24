package com.szboc.platform.component.communication.server.connector;

/**
 * 服务端接口
 * 
 * @author 刺客
 * 
 */
public interface IServer {
	/**
	 * 启动服务器<br>
	 * 说明:实现资源的初始化<br>
	 */
	public void startServer();

	/**
	 * 停止服务器<br>
	 * 说明:在此方法中释放服务器占用的资源<br>
	 */
	public void shutdown();

}
