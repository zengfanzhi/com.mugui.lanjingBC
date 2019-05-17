package com.mugui.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;

import com.mugui.http.udp.UDPServer;
import com.mugui.http.udp.UdpHandle;
import com.mugui.http.udp.UDPServer.Pinglistener;
import com.mugui.tool.HttpTool;
import com.mugui.tool.Other;

public class UDPSocket {
	private UDPServer server = null;
	private UdpHandle tcpHandle = null;

	public UDPServer getServer() {
		return server;
	}

	public UDPSocket(int port) {
		this(port, null);

	}

	public UDPSocket() {
		this(-1);
	}

	public UDPSocket(int port, UdpHandle tcpHandle) {
		server = new UDPServer(port);
		this.tcpHandle = tcpHandle;
	}

	private boolean isTrue = false;

	private Thread listenerthread = null;

	/**
	 * 开始监听
	 */
	public void receive() {
		if (listenerthread == null || !listenerthread.isAlive()) {
			listenerthread = new Thread(new Runnable() {
				// 创建一个用于接收的
				public void run() {
					isTrue = true;
					udpAccpetThreadsSIze = 0;
					while (server.isClose() && isTrue) {
						try {
							DatagramPacket dPacket = new DatagramPacket(new byte[1024], 1024);
							dPacket = server.receive(dPacket);
							if (dPacket == null)
								continue;
							UDPAccpetThread thread = null;
							synchronized (udpAccpetThreads) {
								while ((thread = udpAccpetThreads.pollFirst()) == null) {
									if (udpAccpetThreadsSIze < 50) {
										thread = new UDPAccpetThread();
										thread.start();
										udpAccpetThreadsSIze++;
										break;
									} else {
										udpAccpetThreads.wait();
									}
								}
							}
							thread.setDPacket(dPacket);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			});
			listenerthread.start();
		}
	}

	private LinkedList<UDPAccpetThread> udpAccpetThreads = new LinkedList<UDPAccpetThread>();
	private int udpAccpetThreadsSIze = 0;

	class UDPAccpetThread extends Thread {
		private DatagramPacket dPacket = null;
		private Object sem = new Object();

		public void close() {
			isTrue = false;
		}

		public void setDPacket(DatagramPacket dPacket) {
			synchronized (sem) {
				this.dPacket = dPacket;
				sem.notifyAll();
			}
		}

		@Override
		public void run() {
			while (isTrue) {
				try {
					synchronized (sem) {
						while (dPacket == null && isTrue) {
							sem.wait();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!isTrue)
					return;
				byte[] data = dPacket.getData();
				Accpet(data, dPacket);
				dPacket = null;
				synchronized (udpAccpetThreads) {
					udpAccpetThreads.add(UDPAccpetThread.this);
					udpAccpetThreads.notifyAll();
				}

			}

		}

		int dq_len = 0;
		public void Accpet(byte[] body, DatagramPacket dPacket) {}
	}

	
	public synchronized void send(byte[] b, String host, int port) {
		DatagramPacket dPacket = new DatagramPacket(new byte[b.length], b.length);
		dPacket.setAddress(HttpTool.getInetAddress(host));
		dPacket.setPort(port);
		dPacket.setData(b);
		server.send(dPacket);
	}

	public void sendPing(NetBag bag, Pinglistener listerer) throws IOException {
		if (bag.getHost().equals(server.getLocalHost()) && bag.getPort() == server.getLocalPost())
			throw new IOException("Prohibit sending to yourself");
		
		DatagramPacket packet = new DatagramPacket(new byte[5], 5);
		packet.setAddress(HttpTool.getInetAddress(bag.getHost()));
		packet.setPort(bag.getPort());
		server.ping(packet, listerer);
	}



	public void send(NetBag bag) throws IOException {
		if (bag.getHost().equals(server.getLocalHost()) && bag.getPort() == server.getLocalPost())
			throw new IOException("don't send youself");

		if (StringUtils.isBlank(bag.getFunc())) {
			throw new IOException("udpbag bag_id donot NULL");
		}
		try {
			byte[] b = bag.toJsonObject().toString().getBytes();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			out.write(Other.intToByteArray(b.length));
			out.write(b);
			send(out.toByteArray(), bag.getHost(), bag.getPort());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		isTrue = false;
		if (server != null)
			server.close();
	}

	public boolean isClose() {
		return isTrue && server.isClose();
	}
}
