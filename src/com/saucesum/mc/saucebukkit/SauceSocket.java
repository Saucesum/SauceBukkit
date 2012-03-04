package com.saucesum.mc.saucebukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class SauceSocket {

	private Socket socket;

	private SauceListener listener;

	private String host;
	private int port;

	private PrintWriter out;

	private volatile boolean connected;

	private Logger log = Logger.getLogger("Minecraft");

	public SauceSocket(String host, int port) {
		setupSocket(host, port);
	}

	public void setSauceListener(SauceListener listener) {
		this.listener = listener;
	}

	private void setupSocket(String host, int port) {
		this.host = host;
		this.port = port;
		connected = true;
		new Thread(new Runnable() {
			@Override
			public void run() {	
				while (connected) {
					if (!isConnected()) {
						try {
							connect(SauceSocket.this.host, SauceSocket.this.port);
						} catch (Exception e) { }
					}
					delay();
				}
			}
		}).start();
	}

	public void connect(String host, int port) throws IOException {
		disconnect();
		this.host = host;
		this.port = port;

		socket = new Socket(host, port);
		out = new PrintWriter(socket.getOutputStream(), false);
		startReader();
		log.info("SauceBukkit Connected to SauceServer on " + socket.getRemoteSocketAddress());
	}

	private void startReader() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					while (connected) {
						String str = reader.readLine();
						if (str == null) break;
						handleInput(str);
					}
				} catch (Exception e) {
				} finally {
					disconnect();
				}
			}
		}).start();
	}

	private void handleInput(String line) {
		Emit e = Emit.fromJson(line);
		if (listener != null) {
			listener.emitReceived(e);
		}
	}

	public void stop() {
		connected = false;
		disconnect();
	}

	public void disconnect() {
		if (socket == null) return;
		try {
			socket.close();
		} catch (Exception e) { }
		socket = null;
	}

	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}

	private void delay() {
		try {
			Thread.sleep(2000);
		} catch (Exception e) { }
	}

	public void send(Emit e) {
		if (!isConnected()) return;
		try {
			out.println(e.toJson());
			out.flush();
		} catch (Exception ex) {
			disconnect();
		}
	}

}
