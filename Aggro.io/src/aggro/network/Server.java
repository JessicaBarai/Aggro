package aggro.network;

import aggro.Coordinate;
import aggro.model.Model;
import aggro.model.PlayerAction;
import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * This is a class which allows connections to clients, handling those connections and grabbing information to send through those connections. 
 * It also dictates the speed at which the model updates.
 * @author grupp8
 */
public class Server implements Runnable {
	private Model model;
	private ServerSocket serverSocket;
	private PacketHandler packetHandler;
	private final static int PORT = 7777;
	private List<Connection> connections;
	
	private boolean running = false;

	
	/**
	 * Creates a new server, including an internal model and begins the game.
	 */
	public Server() {
		model = new Model();
		packetHandler = new PacketHandler(this);
		connections = new ArrayList<Connection>(); 
		
		
		Timer updateStreamTimer = new Timer();
		updateStreamTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				sendInfo();
			}
		}, 0, 50);

		Timer updateTimer = new Timer();
		updateTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				updateModel();
			}
		}, 1000, 50);
		
		
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread(this).start();
	}

	public static void main(String args[]) {
		new Server();
	}
	/**
	 * Sends the model through all connections.
	 */
	public synchronized void sendInfo() {
		Model m = getModel();
		for (Connection c : new ArrayList<Connection>(connections)) {
			c.send(m);
		}

	}
	/**
	 * Updates the model
	 */
	private synchronized void updateModel() {
		model.update();
	}
	/**
	 * Updates the model
	 */
	public Model getModel() {
		return model;
	}
	/**
	 * Removes a connection from being notified by the server further.
	 * @param connection The connection, between server and client, to be removed
	 * @param id The ID of the connection and player.
	 */
	public synchronized void removeConnection(Connection connection, int id) {
		model.removePlayer(id);
		connections.remove(connection);
	}
	/**
	 * Updates the location of the player's mousepointer and updates the player's action.
	 * @param mousepointer Where the player's mousepointer is
	 * @param playerAction What action the player will perform. NONE indicates no change from previous action.
	 * @param id The ID of the connection and player
	 */
	public synchronized void updatePlayer(Coordinate mousepointer, PlayerAction playerAction, int id) {
		model.updatePlayer(mousepointer, playerAction, id);
	}
	/**
	 * Changes the player's appearance, name and color
	 * @param name The name the player will change to
	 * @param color The color the player will change to
	 * @param id The ID of the connection and player
	 */
	public synchronized void updatePlayerAppearance(String name, Color color, int id) {
		model.updatePlayerAppearance(name, color, id);
	}
	/**
	 * Creates a new player within the model and returns that player's ID.
	 * @return The new player's ID
	 */
	public synchronized int getNewPlayerID() {
		return model.addPlayer();
	}
	/**
	 * The main thread of the server. It accepts new connections from new clients and assigns then new players
	 */
	@Override
	public void run() {
		running = true;

		while (running) {
			try {
				Socket socket = serverSocket.accept();
				Connection c = new Connection(socket, packetHandler, this, getNewPlayerID());
				connections.add(c);
				new Thread(c).start();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}
