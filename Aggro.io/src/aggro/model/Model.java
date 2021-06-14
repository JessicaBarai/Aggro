package aggro.model;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import aggro.Coordinate;
/**
 * The model class of the game, includin all gamelogic and game information. This is the object that is passed to clients.
 * @author grupp8
 * 
 *
 */
public class Model implements Serializable {

	
	private static final long serialVersionUID = 7690565876407211314L;
	private List<Player> playerList;
	private List<AIPlayer> aiPlayerList;
	private SortedSet<Food> foodList;
	private int playerID = 0;
	private List<Virus> virusList;
	private boolean hasWon;
	private int waitCounter = 0;
	private final int winWait = 200;
	private Player winner;
	private ModelConfiguration configuration;
	// Constructors
	/**
	 * Create a new model instance. Sets the variables via the ModelConfiguration class.
	 * Generates all components of the game
	 */
	public Model() {
		configuration = new ModelConfiguration(); //Sets static variables to the ones from the document.
		playerList = new ArrayList<Player>();
		aiPlayerList = new ArrayList<AIPlayer>();
		foodList = new TreeSet<Food>(new FoodComparator());
		virusList = new ArrayList<Virus>();
		
		resetGame(); //Sets food to normal amounts and assigns winner variables etc.
		
		// adding AI players
		for (int i = 0; i < configuration.getPlayerAmount(); i++) {
			addAIPlayer();
		}
		for (int i = 0; i < configuration.getVirusAmount(); i++) {
			addVirus();
		}
	}
	
	/**
	 * get player with a specific id from list
	 * @param playerID the player id of the player to be returned
	 * @return the player with the specific id
	 *
	 */
	public Player getPlayer(int playerID) {
		for (Player p : playerList) {
			if (p.getID() == playerID) {
				return p;
			}
		}
		return null; //returns null if there is no player by that ID.
	}
	/**
	 * get size of the game board from configuration class 
	 * @return the size of game board
	 *
	 */
	public int getMapSize() {
		return configuration.getMapSize();
	}
	/**
	 *Organize players so that we can find directly via ID, otherwise iterate through the list.
	 * @param mousePointer as a coordinate the mouse position
	 *@param playerAction the action of a specific player
	 *@param playerID the Id of the player to be updated 
	 */
	public void updatePlayer(Coordinate mousePointer, PlayerAction playerAction, int playerID) {
		// Organize players so that we can find directly via ID, otherwise iterate
		// through the list.
		Player p = getPlayer(playerID);
		if(p!=null) {
			p.update(mousePointer, playerAction);
		}else {
			System.out.println("Attempt to update player " + playerID + "who doesn't exist.");
		}
	}
	/**
	 *update a player with name and color
	 *@param name the player will have
	 *@param color the player will be
	 *@param playerID the Id of the player to be updated 
	 */
	public void updatePlayerAppearance(String name, Color color, int playerID) {
		for (Player p : playerList) {
			if (p.getID() == playerID) {
				p.updateAppearance(name, color);
				return;
			}
		}

	}
	/**
	 *Add a new player to the game
	 *@return the id of the new player
	 */
	public int addPlayer() {
		int id = getNewPlayerID();
		playerList.add(new Player(id));
		return id;
	}
	/**
	 *Remove the specific player from the list 
	 * @param id the id of the player to be removed from list
	 */
	public void removePlayer(int id) {
		for (Player p : playerList) {
			if (p.getID() == id) {
				playerList.remove(p);
				addAIPlayer();
				return;
			}
		}
	}

	private void generateFood(int amount) {
		for (int i = 0; i < amount; i++) {
			foodList.add(new Food(new Coordinate(RandomSingleton.getRandom().nextDouble() * configuration.getMapSize(),
					(RandomSingleton.getRandom().nextDouble() * configuration.getMapSize()))));
		}
	}
	/**
	 *check if any player won the game, in that case reset. update all component on the board
	 *if amount of food instance is getting to small generate new food. Move all movable.
	 */
	public void update() {
		if (hasWon) {
			if (waitCounter > winWait) {
				waitCounter = 0;
				resetGame();
				System.out.println("Waiting over, time to play again!");
			}
			waitCounter++;
			return;
		}
		// spawn new food
		if (foodList.size() < configuration.getMaximumFoodAmount()) {
			generateFood(5); // How much food is generate each cycle.
		}
		// Update AI actions
		for (AIPlayer ai : aiPlayerList) {
			ai.update();
		}
		// move all cells
		for (Player p : playerList) {
			p.moveAllCells(); // Not what we want at the end of the day. Just for testing.
		}
		// Perform actions (spawn, split)
		for (Player p : playerList) {
			p.performAction();
		}

		for (Virus v : virusList) {
			v.move();
		}
		foodCollision();
		playerCollision();
		virusCollision();

		// Check wincondition
		for (Player p : playerList) {
			if (p.getPlayerSize() > configuration.getWinConditionAmount()) {
				hasWon = true;
				winner = p;
			}
		}
	}

	private void virusCollision() {
		List<Cell> cellsToShatter = new ArrayList<Cell>();
		for (Player p : playerList) {
			for (Cell c : p.getCellList()) {
				for (Virus v : virusList) {
					if (c.getPosition().overlaps(v.getPosition(), c.getRadius())) {
						cellsToShatter.add(c);
					}
				}
			}
		}
		for (Cell c : cellsToShatter) {
			c.shatter();
		}
	}

	private void foodCollision() {
		// Perform food collisions.
		for (Player p : playerList) {
			for (Cell c : p.getCellList()) {
				double r = c.getRadius();
				Coordinate pos = c.getPosition();
				if(Double.isNaN(pos.getY())){
					System.out.println(c);
					System.out.println(p.getPlayerSize());
					System.exit(1);
				}
				Iterator<Food> itr = getFoodList(pos.getY() - r, pos.getY() + r).iterator(); // Only check food that is
			  																				 // within valid y ranges

				while (itr.hasNext()) {
					Food f = itr.next();
					if (pos.overlaps(f.getPosition(), r)) {
						c.grow(Food.VALUE);
						itr.remove();
					}
				}
			}
		}

	}
	/**
	 * Returns the winning player. If no player has currently won, this returns null.
	 * @return The player who has won. If no player has currently won, this is null.
	 */
	public Player getWinner() {
		return winner;
	}
	private void resetGame() {
		for (Player p : playerList) {
			p.reset();
		}
		winner = null;
		foodList.clear();
		generateFood(configuration.getMaximumFoodAmount()/4);
		hasWon = false;
	}

	private void playerCollision() {
		// behåll den här logen men få in när samma spelares celler träffar varandra, då
		// ska samma sak hända som nu
		for (int i = 0; i < playerList.size(); i++) {

			// Internal Player collisions first.
			playerList.get(i).checkInternalCollisions();
			// skapar iterator till playerList
			Iterator<Cell> itr1 = playerList.get(i).getCellList().iterator();
			// Still getting concurrentmodification errors with the code below.
			boolean itr1Safe;
			while (itr1.hasNext()) {
				Cell c1 = itr1.next();
				itr1Safe = true;
				Coordinate pos = c1.getPosition();
				double radius1 = c1.getRadius();
				for (int j = i + 1; (j < playerList.size() && itr1Safe); j++) {
					Iterator<Cell> itr2 = playerList.get(j).getCellList().iterator();
					// skapar en till iterator till playerList med början på ett annat index
					// dvs denna iterator refererar aldrig till samma player som den andra eftersom
					// index ökar med ett från i hela tiden.
					while (itr2.hasNext()) {
						Cell c2 = itr2.next();
						Coordinate pos1 = c2.getPosition();
						double radius2 = c2.getRadius();
						// testa om koorinaterna för cellerna överlappar varandra, metoden overlaps körs
						// från
						// bägge cellernas position.
						if (pos.overlaps(pos1, radius1) || pos1.overlaps(pos, radius2)) {
							// om c1:s radie är mindre än c2 radera c1 via iterator remove,
							// öka c2:s
							if (radius1 < radius2) {
								c2.grow(c1.getSize());
								c1.grow(-c1.getSize());
								itr1.remove();
								itr1Safe = false; // Blir illegalstate utan denna koll.
								break;
								// om c2:s radie är mindre än c1 radera c2 via iterator remove
								// öka c1:s storlek
							} else if (radius1 > radius2) {
								c1.grow(c2.getSize());
								c2.grow(-c2.getSize());
								// removeCell(j,c2);
								itr2.remove();
							}

						}

					}
				}

			}

		}

	}

	private void addVirus() {
		Virus vi = new Virus();
		virusList.add(vi);
	}

	private void addAIPlayer() {
		AIPlayer aip = new AIPlayer(getNewPlayerID());
		playerList.add(aip);
		aiPlayerList.add(aip);

	}
	/**
	 *get all the player on the board
	 *@return all player on the board as a List
	 */
	public List<Player> getPlayerList() {
		return this.playerList;
	} 
	/**
	 *get all the virus on the board
	 *@return all virus on the board as a List
	 */
	public List<Virus> getVirusList() {
		return this.virusList;
	}
	/**
	 *get all food on the board
	 *@return food on the board as a Set in order
	 */
	public SortedSet<Food> getFoodList() {
		return this.foodList;
	}
	/**
	 *Return a portion fo food available based on y-position of the food
	 *@param minY the minimal y-value
	 *@param minX the minimal x-value
	 *@return a cast from sortedSet to TreeSet, the sorted food
	 */
	public SortedSet<Food> getFoodList(double minY, double maxY) {
		Food f1 = new Food(new Coordinate(0, minY));
		Food f2 = new Food(new Coordinate(0, maxY));
		return foodList.subSet(f1, f2);
	}
	/**
	 *get a new player's id
	 *@return the value next player will be assigned
	 */
	public int getNewPlayerID() {
		return playerID++;
	}
}
