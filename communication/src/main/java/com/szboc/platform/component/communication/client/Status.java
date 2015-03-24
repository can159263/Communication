package com.szboc.platform.component.communication.client;

/**
 * 请求的状态<br>
 * @author 刺客
 *
 */
public enum Status {
	/**
	 * S0-初始状态：发送之前的状态
	 */
	INITIAL,

	/**
	 * S1-请求成功：本次与服务器交互成功
	 */
	SUCCESS,

	/**
	 * S2-请求失败：本次与服务器交互失败，包括通信前的异常，以及通信后响应报文的解析异常
	 */
	FAILURE,

	/**
	 * S3-状态未明：与服务器交互时发生异常，状态无法明确
	 */
	NOT_SURE,
	
	/**
	 * S4-服务无法访问: 服务访问服务器，状态明确失败，此状态下系统可以自动重发此交易
	 */
	UNABLE_ACCESS,
	
	/**
	 * S5-已异步回盘
	 */
	ASYN_BACK;
	

	private String value = null;

	private Status() {
		this.value = "S" + this.ordinal();
	}

	public String value() {
		return this.value;
	}

	public static Status fromValue(String value) {
		for (Status s : Status.values()) {
			if (value.equals(s.value())) {
				return s;
			}
		}
		throw new IllegalArgumentException(value);
	}
}
