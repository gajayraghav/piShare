package com.rts.transfer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class RaspberryPi extends Thread {

	private ServerSocket server;
	private int  SERVER_PORT = 0;

	public RaspberryPi(int port) {
		SERVER_PORT = port;
	}

	public void run() {
		int i = 0;
		try {
			server = new ServerSocket(SERVER_PORT, 10);
			server.setReuseAddress(true);
			//server.bind(new InetSocketAddress(SERVER_PORT));
			while (i++ < 10) {
				System.out.println("Server Accepting");
				Socket client = server.accept();
				System.out.println("Server Accepted");
				if (client != null) {
					System.out.println("Client Exists");
					ServerRead read = new ServerRead(client);
					read.start();
					ServerWrite write = new ServerWrite(client);
					write.start();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class ServerRead extends Thread {

		private Socket clientHandle;
		private BufferedInputStream serverInputStream;

		public ServerRead(Socket client) throws IOException {
			// TODO Auto-generated constructor stub
			clientHandle = client;
			if (clientHandle != null) {
				serverInputStream = new BufferedInputStream(
						clientHandle.getInputStream());
			}
		}

		public void run() {

			if (serverInputStream != null) {
				while (true) {
					byte[] fromClient = new byte[1024];
					try {
						System.out.println("Server Reading");
						if (serverInputStream.read(fromClient) > 0) {
							System.out.println("Server Read");
							String message = new String(fromClient, "UTF-8");
							System.out
									.println("From Client :" + message.trim());
						} else {
							System.out.println("Server Error reading "
									+ fromClient);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				System.out.println("Server Error reading");
			}
		}

	}

	class ServerWrite extends Thread {
		private Socket clientHandle;
		private BufferedOutputStream serverOutputStream;

		public ServerWrite(Socket client) throws IOException {
			// TODO Auto-generated constructor stub
			clientHandle = client;
			if (clientHandle != null) {
				serverOutputStream = new BufferedOutputStream(
						clientHandle.getOutputStream());
			}
		}

		public void run() {
			int i = 0;
			System.out.println("Server Writing");

			// while (i < 10) {
			// Calendar cal = Calendar.getInstance();
			// String time = cal.getTime().toString();
			// try {
			// serverOutputStream.write(time.getBytes());
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// long start = System.currentTimeMillis();
			// while ((start + 3000) > System.currentTimeMillis())
			// ; // Delay
			// }
		}
	}

}
