package aggro.network;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;

import javax.swing.Timer;

import aggro.Coordinate;
import aggro.control.Controller;
import aggro.model.*;
import aggro.packets.ActionPacket;
import aggro.packets.NewPlayerPacket;
import aggro.view.Camera;
import aggro.view.View;
/**
 * This class Client classes are feature of the network. Users of a client class allow users
 * to receive data from server 
 * @author grupp8
 */
public class Client {
	private Model model;
	private ObjectInputStream inStream;
	private ObjectOutputStream outStream;
	private Socket socket;
	private Timer updateViewTimer;
	private Timer updateMouseTimer;
	private Controller controller;
	// private String name;
	private int playerID;
	private View view;
	/**
	* create a Client connecting to IP address of server and a port number. Get data from configuration
	* file. Creates a controller instance and a view instance for this client
	*/
	public Client() {
		String IP;
		int port;

		try (FileInputStream propInput = new FileInputStream("clientconfig.properties");) {
			Properties prop = new Properties();
			prop.load(propInput);

			IP = prop.getProperty("ServerIP");
			port = Integer.parseInt(prop.getProperty("ServerPort"));

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Loading configurations failed. Running locally.");
			IP = "localhost";
			port = 7777;
		}

		try {
			socket = new Socket(IP, port); // 78.82.28.156
			outStream = new ObjectOutputStream(socket.getOutputStream());
			inStream = new ObjectInputStream(socket.getInputStream());
			Object o = inStream.readObject();
			this.model = (Model) o;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		playerID = model.getPlayerList().get(model.getPlayerList().size() - 1).getID(); // Grabs the player ID of the
																						// last added player directly
																						// after the player is added.

		this.controller = new Controller(this);
		view = new View(this, controller, playerID);

		this.updateViewTimer = new Timer(25, e -> getInfo());
		this.updateViewTimer.start();

		this.updateMouseTimer = new Timer(100, e -> sendAction(PlayerAction.NONE));
		this.updateMouseTimer.start();
	}
	/**
	* get the model from the stream
	*/
	public void getInfo() {
		try {
			model = (Model) inStream.readObject();
		} catch (IOException e) {
			System.out.println(e);
		} catch (ClassNotFoundException e) {
			System.out.println(e);
			System.exit(1);
		}
	}
	
	public static void main(String[] args) {
		new Client();
	}
	/**
	* Registered new player to socket
	* @param name of player to be registered
	* @param color of player to be registered
	*/
	public void sendAppearance(String name, Color color) {
		try {
			outStream.writeObject(new NewPlayerPacket(name, color));
			outStream.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	* Send a package with data after a player action has been done
	* @param pa the action of player 
	*/
	public void sendAction(PlayerAction pa) {
		Player p = getCurrentPlayer();
		if(p == null) {
			System.out.println("Player null in sendAction");
			System.exit(1);
		}
		Camera cam = view.getLastCamera();
		int mapSize = getMapSize();
		if(cam == null) {
			cam = new Camera(Coordinate.Zero(), mapSize, mapSize);
		}
		int frameWidth = view.getWidth();
		int frameHeight = view.getHeight();
		
		Coordinate mousePosition = controller.getLastMousePosition().subtract(new Coordinate((double)(frameWidth)/2,(double)(frameHeight)/2 )).multiply(1/view.getLastZoomFactor()).add(cam.getCenter()); 
		
		// clamp the mouse position
		if (mousePosition.getX() < 0) {
			mousePosition = new Coordinate(0, mousePosition.getY());
		}
		if (mousePosition.getX() > getMapSize()) {
			mousePosition = new Coordinate(getMapSize(), mousePosition.getY());
		}
		if (mousePosition.getY() < 0) {
			mousePosition = new Coordinate(mousePosition.getX(), 0);
		}
		if (mousePosition.getY() > getMapSize()) {
			mousePosition = new Coordinate(mousePosition.getX(), getMapSize());
		}

		try {
			outStream.writeObject(new ActionPacket(mousePosition, pa));
			outStream.reset();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	/**
	* Closes the connection to server
	*/
	public void close() {
		updateViewTimer.stop();
		try {
			inStream.close();
			outStream.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**
	* getter for client's size of game board
	* @return the size of the map in model instance
	*/
	public int getMapSize() {
		return model.getMapSize();
	}
	/**
	* get information about min and max food value from model
	* @param minY the minimum value of Y
	* @param maxY the maximum value of Y
	* @return a TreeSet, a sorted set, of food
	*/
	public SortedSet<Food> getFoodInfo(double minY, double maxY) {
		Food f1 = new Food(new Coordinate(0, minY));
		Food f2 = new Food(new Coordinate(0, maxY));
		return model.getFoodList().subSet(f1, f2); // Cast from NavigableSet to TreeSet
	}
	/**
	* Get information of all players in the model 
	* @return a List of all player from model
	*/
	public List<Player> getPlayerInfo() {
		return model.getPlayerList();
	}
	/**
	* Get information of all viruses in the model 
	* @return a List of all viruses from model
	*/
	public List<Virus> getVirusInfo() {
		return model.getVirusList();
	}
	/**
	* Get winner of the game  
	* @return the winner as Player instance
	*/
	public Player getWinner() {
		return model.getWinner();
	}
	/**
	* get the player of this client
	* @return player of this playerId
	*/
	public Player getCurrentPlayer() {
		return model.getPlayer(playerID);
	}
	/**
	* get information of current player
	* @return true if this player is dead
	*/
	public boolean playerIsDead() {
		return getCurrentPlayer().isDead();
	}
}
