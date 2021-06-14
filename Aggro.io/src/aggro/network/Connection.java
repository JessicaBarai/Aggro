package aggro.network;

import java.io.*;
import java.net.*;
/**
* Handles the connection between clients and the server
* @return a List of all player from model
*/
public class Connection implements Runnable {
	private Socket socket;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private Server server;
	private int id;
	private PacketHandler packetHandler;

	private boolean running = false;
	/**
	* creates a new connection
	* @param socket trying to connect with
	* @param packetHanlder that will send data between server and clients
	* @param server communicating with
	* @param id of this specific connection
	*/
	public Connection(Socket socket, PacketHandler packetHandler, Server server, int id) {
		this.socket = socket;
		this.packetHandler = packetHandler;
		this.server = server;
		this.id = id;
		try {
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	* The main thread of the connection. It reads packets from the stream and sends them to the packethandler
	*/
	@Override
	public void run() {
		try {
			running = true;
			while (running) {
				try {
					Object o = inputStream.readObject();
					packetHandler.receivePacket(o, id);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SocketException e) {
					this.close();
				} catch (EOFException e) {
					this.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			this.close();
		}
	}
	/**
	* remove this connection from connecting with server close all streams and socket
	*/
	public synchronized void close() {
		server.removeConnection(this, id);
		try {
			running = false;
			inputStream.close();
			outputStream.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	* send a object to socket and reset the outputstream
	* @param o to be sent
	*/
	public synchronized void send(Object o) {
		try {
			outputStream.writeObject(o);
			outputStream.flush();
			outputStream.reset();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Closing their connection");
			this.close();
		}
	}
}
