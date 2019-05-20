package com.mugui.net;

import com.mugui.Annotation.Module;
import com.mugui.bean.JsonBean;

public class NetBag extends JsonBean {

	/**
	 * 	http连接类型
	 */
	public static final int TYPE_HTTP=2;
	
	/**
	 * 	upd连接类型
	 */
	public static final int TYPE_UDP=1;
	/**
	 * 时间戳
	 */
	private String timestamp;
	private String host;
	private int port;

	/**
	 * 来至哪个host、若包没有被转发则，默认为自己
	 */
	private String from_host;

	/**
	 * 来至哪个端口的消息、若包没有被转发，则为本地绑定的端口号
	 */
	private int from_port;

	/**
	 * 网络连接状态:101超时 200 成功 404 资源错误 503发生异常
	 */
	private int code;
	/**
	 * 包唯一hash
	 */
	private String hash;

	/**
	 * 类型1.udp连接过来的2.http连接过来的
	 */
	private int type;

	/**
	 * 访问的资源连接处理函数名由：模块名.操作类型({@link Module}).函数名组成
	 */
	private String func;

	/**
	 * 数据处理主体
	 */
	private Object data;

	/**
	 * 用户唯一标示
	 * 
	 */
	private String session;

	public String getFrom_host() {
		return from_host;
	}

	public void setFrom_host(String from_host) {
		this.from_host = from_host;
	}

	public int getFrom_port() {
		return from_port;
	}

	public void setFrom_port(int from_port) {
		this.from_port = from_port;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getFunc() {
		return func;
	}

	public void setFunc(String func) {
		this.func = func;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}


}
