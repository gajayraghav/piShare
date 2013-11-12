package com.rts.transfer;

import java.io.BufferedInputStream;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;

import android.graphics.Bitmap;

public class AndroidMe extends Thread {

	private Socket client = null;
	private String clientName;
	private int port = 0;
	private ClientRead read = null;
	private ClientWrite write = null;

	public AndroidMe(int port, String name) {
		this.port = port;
		clientName = name;
	}

	public ClientRead getReader() throws IOException
	{
		if(read == null)
		{
			read = new ClientRead(client);
		}
		return read;
	}
	public ClientWrite getWriter() throws IOException {
		if ( write == null)
		{
			write = new ClientWrite(client);
		}
		return write;
	}
	
	public void run() {
		System.out.println("Client Starting");

		try {
			while (client == null) {
				client = new Socket("localhost", port);
				System.out.println("Client Started");
				if (client != null) {
					// ClientRead read = new ClientRead(client);
					// read.start();
					// ClientWrite write = new ClientWrite(client);
					// write.run();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	public class ClientWrite extends Thread {
		private Socket clientHandle;
		private BufferedOutputStream clientOutputStream;
		Bitmap screen = null;

		public ClientWrite(Socket client) throws IOException {
			// TODO Auto-generated constructor stub
			clientHandle = client;
			if (clientHandle != null) {
				clientOutputStream = new BufferedOutputStream(
						clientHandle.getOutputStream());
			}
		}

		public void run() {
			int i = 9; // write once for now
			while (i++ < 10) {
				Calendar cal = Calendar.getInstance();
				String time = clientName + " " + cal.getTime().toString();
				try {
					synchronized ("lock") {
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
						
						clientOutputStream.write(time.getBytes());
						clientOutputStream.flush();
						
						System.out.println(clientName + " Writing "
								+ time.getBytes().length + " Bytes");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long start = System.currentTimeMillis();
				while ((start + 500) > System.currentTimeMillis())
					; // Delay
			}
		}
	}

	public class ClientRead extends Thread {

		Socket clientHandle;
		BufferedInputStream clientInputStream;

		public ClientRead(Socket client) throws IOException {
			// TODO Auto-generated constructor stub
			clientHandle = client;
			if (clientHandle != null) {
				clientInputStream = new BufferedInputStream(
						clientHandle.getInputStream());
			}
		}

		public void run() {
			System.out.println("Client Reading");

			if (clientInputStream != null) {
				while (true) {
					byte[] fromServer = new byte[1024];
					try {
						if (clientInputStream.read(fromServer) > 0) {
							System.out.println("From Server :"
									+ fromServer.toString());
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

}
