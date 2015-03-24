package com.szboc.platform.component.communication.server.connector;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.szboc.platform.component.communication.server.handler.IHandleTask;
import com.szboc.platform.component.communication.server.handler.IHandler;

/**
 * 抽象的socket服务器
 * 
 * @version v1.0
 * @author likw
 * @date 2014-7-25
 * 
 */
public abstract class AbstractSocketServer extends Thread implements IServer {

	private static Logger logger = LoggerFactory
			.getLogger(AbstractSocketServer.class);

	/**
	 * 处理器
	 */
	private IHandler handler = null;

	/**
	 * 服务器名称
	 */
	private String serverName = null;

	/**
	 * 监听端口号
	 */
	private int port = 0;

	/**
	 * ServerSocket
	 */
	private ServerSocket serverSocket = null;

	@Override
	public void run() {
		try {
			logger.info("[" + getServerName() + "]连接器组件启动成功");

			while (!this.isInterrupted()) {
				Socket client = serverSocket.accept();

				logger.info("[" + getServerName() + "]接收到IP["
						+ client.getInetAddress().getHostAddress() + "]请求");

				// 将请求交给报文处理器处理
				this.handler.execute(newTask(client));

			}
		} catch (Exception e) {
			logger.error("[" + getServerName() + "]在运行过程中发生异常：" + e, e);
		}

		logger.info("[" + getServerName() + "]连接器组件运行结束");
	}

	public void startServer() {

		try {
			logger.info("[" + getServerName() + "]启动...");

			// 1. 初始化处理器组件
			this.handler.init();

			// 2. 初始化连接器组件
			serverSocket = new ServerSocket(this.getPort());
			this.setName("Thread-" + getServerName());
			this.start();

		} catch (IOException e) {
			logger.error("[" + getServerName() + "]启动过程中发生异常：" + e, e);
		}

	}

	public void shutdown() {
		// 1. 中断连接器组件
		try {
			logger.info("尝试中断连接器组件所在的线程...");
			this.interrupt();
		} catch (Exception e) {
			logger.error("中断连接器组件所在的线程时发生异常:" + e, e);
		}

		if (this.serverSocket != null && !this.serverSocket.isClosed()) {
			try {
				logger.info("关闭ServerSocket，释放绑定的端口");
				this.serverSocket.close();
			} catch (IOException e) {
				logger.error("关闭ServerSocket时发生异常:" + e, e);
			}
		}

		// 2. 销毁处理器组件
		try {
			if (null != handler) {
				this.handler.destory();
			}
		} catch (Exception e) {
			logger.error("销毁处理器组件时发生异常:" + e, e);
		}

	}

	/**
	 * 创建待处理任务
	 * @param socket
	 * @return
	 */
	protected abstract IHandleTask newTask(Socket socket);

	public IHandler getHandler() {
		return handler;
	}

	public void setHandler(IHandler handler) {
		this.handler = handler;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
