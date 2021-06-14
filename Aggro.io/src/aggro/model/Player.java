package aggro.model;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import aggro.Coordinate;
/**
 *Representing a user as one or more cells
 *@author grupp8
 */
public class Player implements Serializable {

	private static final long serialVersionUID = 2755855534039122597L;
	private List<Cell> cellList;
	private Coordinate mousePointer;
	private int id;
	private double totalSize;
	private Color color;
	private PlayerAction playerAction;
	private String playerName;
	private int doubleCheckSizeWait = 0;
	// Getters & Setters
	/**
	 *get total size of all cells in the list representing a player
	 *@return total size as a double
	 */
	public double getPlayerSize() {
		return totalSize; 
	}
	/**
	 *get the name of a player
	 *@return player name as a String
	 */
	public String getPlayerName() {
		return playerName;
	}
	/**
	 *set mouse pointer as a coordinate
	 *@param c the mouse position
	 */
	public void setMousePointer(Coordinate c) {
		this.mousePointer = c;
	}
	/**
	 *get the coordinate of mouse pointer
	 *@return coordinate as mouse pointer
	 */
	public Coordinate getMousePointer() {
		return mousePointer;
	}

	/**
	 *create a Player instance with a specific id, store the player's cells in a list set default size 0
	 *@param id that will be assigned the player instance
	 */
	public Player(int id) {
		playerName = "";
		cellList = new ArrayList<Cell>();
		totalSize = 0;
		this.id = id;
		mousePointer = new Coordinate(0,0);
		color = new Color((float) RandomSingleton.getRandom().nextDouble(),
				(float) RandomSingleton.getRandom().nextDouble(), (float) RandomSingleton.getRandom().nextDouble());
	}
	/**
	 *reset the player. Remove all cell from the player and set default size 0
	 */
	public void reset() {
		cellList.clear();
		totalSize = 0;
	}
	/**
	 *Spawns the player, giving them a cell of size 100 in a random location on the gameboard
	 */
	public void spawn() {
		if (this.isDead()) {
			addCell(new Cell(RandomSingleton.getRandomCoordinate(), this));
			totalSize = 100;
		}
	}
	/**
	 *change a player's totalSize after sum all cells of the player
	 *@param change as a double to be added to total size of player
	 */
	public void changePlayerTotalSize(double change) {
		
		//Added a double check here because the game sometimes give negative values for player total size after many splits.
		doubleCheckSizeWait++;
		if(doubleCheckSizeWait > 100) { 
			doubleCheckSizeWait = 0;
			totalSize = 0;
			for(Cell c: cellList) {
				totalSize += c.getSize();
			}
			return;
		}
	
		totalSize += change;
	}
	/**
	 *Adds a cell to the player
	 *@param cell to be added to list belonging to player
	 */
	public void addCell(Cell c) {
		cellList.add(c);
	}
	/**
	 *Removes a cell from the player
	 *@param c cell to be removed from list
	 */
	public void removeCell(Cell c) {
		if (!cellList.remove(c)) {
			System.out.println("Tried to remove cell from player that does not have it");
			System.out.println(c + " was the failed to be removed from " + this);
		}
	}
	/**
	 *get the id of a player
	 *@return id of player
	 */
	public int getID() {
		return id;
	}
	/**
	 *get all the cells from a player
	 *@return all cells belonging to a player as a List
	 */
	public List<Cell> getCellList() {
		return cellList;
	}
	/**
	*move all cell belonging to player
	 */
	public void moveAllCells() {
		// For testing purposes
		for (Cell c : cellList) {
			c.move();
		}

	}
	/**
	 *get color from player
	 *@return color of the player
	 */
	public Color getColor() {
		return color;
	}
	/**
	 * Returns how far a single cell of the player can see
	 * @return double value representing the range a cell of the player can see
	 */
	public double getCameraRange() {
		double n = (Math.pow(totalSize*.25, 0.55) + 25);
		if (n > 250) {
			n = 250;
		}
		if (cellList.size() == 1) {
			n += cellList.get(0).getRadius()/2;
		}
		return n;
	}
	/**
	 *check if player own any cells
	 *@return bool true if player id dead
	 */
	public boolean isDead() {
		return cellList.size() == 0;
	}
	/**
	 *get the center of the player as a coordinate based on all position of the cells,
	 *make sure player can view all their cells on the board
	 *@return coordinate as the center of the player
	 */
	public Coordinate getPlayerCenter() {
		if (this.isDead()) {
			return new Coordinate(0,0);
		}
		double xLow = Double.MAX_VALUE;
		double xHigh = 0;
		double yLow = Double.MAX_VALUE;
		double yHigh = 0;
		for (Cell c : cellList) {
			double x = c.getPosition().getX();
			double y = c.getPosition().getY();
			if (x < xLow) {
				xLow = x;
			}
			if (x > xHigh) {
				xHigh = x;
			}
			if (y < yLow) {
				yLow = y;
			}
			if (y > yHigh) {
				yHigh = y;
			}
		}
		return new Coordinate((xLow + xHigh) / 2, (yLow + yHigh) / 2);
	}
	/**
	 *listen to users interaction and perform right action on player and its cell
	 */
	public void performAction() {
		if (playerAction == PlayerAction.NONE) {
			return;
		} else if (playerAction == PlayerAction.SPLIT) {
			splitAllCells();
			playerAction = PlayerAction.NONE;
		} else if (playerAction == PlayerAction.SPAWN) {
			spawn();
			playerAction = PlayerAction.NONE;
		}

	}

	private void splitAllCells() {
		for (Cell c : new ArrayList<Cell>(cellList)) {
			Cell toAdd = c.Split();
			if (toAdd != null) {
				this.addCell(toAdd);
			}
		}
	}
	/**
	 *update player's mouse pointer if action occurs 
	 *@param c coordinate to be assigned to mouse pointer 
	 *@param pa the player action that occurs
	 */
	public void update(Coordinate c, PlayerAction pa) {
		if (pa != PlayerAction.NONE) {
			playerAction = pa;
		}
		mousePointer = c;

	}
	/**
	 *check if any cells within same list collide with the one cell that can be controlled by mouse pointer
	 *if cells not controlled by the user collide- continue. If the controlled cell collide with
	 *another cell. The cell grows 
	 *@return id of player
	 */
	public void checkInternalCollisions() {

		for (int i = 0; i < cellList.size() - 1; i++) {
			Cell c1 = cellList.get(i);
			if (c1.checkIfForced()) {
				continue;
			}
			for (int j = i + 1; j < cellList.size(); j++) {
				Cell c2 = cellList.get(j);
				if (c2.checkIfForced()) {
					continue;
				}
				if (c1.getPosition().overlaps(c2.getPosition(), c1.getRadius())) {
					c1.grow(c2.getSize());
					c2.grow(-c2.getSize());
					removeCell(c2);

				} else if (c2.getPosition().overlaps(c1.getPosition(), c2.getRadius())) {
					c2.grow(c1.getSize());
					c1.grow(-c1.getSize());
					removeCell(c1);
					break; // Fetch new c1 if c1 eaten.
				}
			}
		}

	}
	/**
	 *update player with name and color
	 *@param name of the player
	 *@param color of the player
	 */
	public void updateAppearance(String name, Color color) {
		this.playerName = name;
		this.color = color;
	}
	/**
	* Returns a string representation of the player
	* @return value representing player´s id
	*/
	@Override
	public String toString() {
		return Integer.toString(id);
	}

}
