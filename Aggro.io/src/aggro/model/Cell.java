package aggro.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import aggro.Coordinate;

public class Cell implements Moveable, Serializable {
	

	private static final long serialVersionUID = 1712024563519094939L;
	private Coordinate position;
	private double size;
	private double speed;
	private Player player;
	private double radius;
	private boolean forcedMovement;
	private Coordinate forcedMovementDirection;
	private double forcedMovementSpeed;

	//constructors
	/**
	* Creates a new Cell with default size
	* @param position indicates where the cell is as a coordinate
	* @param p the player whom the cell belongs to
	*
	*/
	public Cell(Coordinate position, Player p) {
		this.player = p;
		this.position = position;
		size = 100;
		this.updateRadius();
		this.updateSpeed();
		forcedMovement = false;
	}
	/**
	* Creates a new Cell with given size
	* @param position indicates where the cell is as a coordinate
	* @param p the player whom the cell belongs to
	* @param size the cell's size
	*/
	public Cell(Coordinate position, Player p, double size) {
		this.player = p;
		this.position = position;
		this.size = size;
		this.updateRadius();
		this.updateSpeed();
		forcedMovement = false;
	}
	/**
	* This moves the cell appropriately - whether forced movement or not.
	*
	*/
	@Override
	public void move() {
		if(forcedMovement) {
			if(speed > forcedMovementSpeed) {
				forcedMovement = false;
			}else {
				position = position.add(forcedMovementDirection.multiply(forcedMovementSpeed));
				forcedMovementSpeed -= 0.2; //The deceleration that the cell experiences.
				return;
			}
		}
		Coordinate c = player.getMousePointer().subtract(this.position).normalized(); //direction vector for the movement.
		position = position.add(c.multiply(speed));
	}
	
	/**
	* Returns the radius of the cell
	* @return The radius of the cell
	*
	*/
	
	public double getRadius() {
		return radius;
	}

	private void updateRadius() {
		radius = Math.sqrt(0.25*size);
	}

	private void updateSpeed(){
		speed =  200/Math.pow(size, 1.05) + .3;
	}
	
	/**
	* Returns the position of the cell
	* @returns the position of the cell as a coordinate
	*/
	public Coordinate getPosition(){
		return this.position;
	}
	/**
	* Changes the size of the cell, updating playersize, radius and speed.
	* @param A double to be added to cell's size, can be negative
	*/
	public void grow(double value) {
		size += value;
		player.changePlayerTotalSize(value);
		updateRadius();
		updateSpeed();

	}
	/**
	* Returns the size of the cell
	* @return The size of the cell as a double
	*/
	public double getSize(){
		return size;

	}
	/**
	* split the cell if size is big enough. Decrease size of cell and creates new cells
	* @return a new cell whose movement can't be controlled temporarily
	*/
	public Cell Split() {
		if(size < 200) {
			return null;
		}
		size = size*0.5;
		this.addForcedMovement(player.getMousePointer().subtract(this.position), 5);
		Coordinate newCellPosition = this.position.subtract(forcedMovementDirection.multiply(radius*0.5)); //Moving the cells away so that they cannot join immediately. .5 of the radius wasn't enough so added a bit more to the thrown cell
		this.position = this.position.add(forcedMovementDirection.multiply(radius*0.6));
		updateRadius();
		updateSpeed();
		return(new Cell(newCellPosition,player , size));

	}
	
	/**
	* Add movement to cell that can't be controlled by user, given direction and speed of the movement
	* @param direction The coordinate of the direction, does not need to be normalized.
	* @param speed The speed of the forced movement
	*/
	
	public void addForcedMovement(Coordinate direction, double speed) {
		forcedMovement = true;
		forcedMovementDirection = direction.normalized();
		forcedMovementSpeed = speed;
	}
	
	/**
	* Splits cell into many smaller cells, spreading them outward and forcing their movement for a while.
	*/
	public void shatter(){
		if(size < 300) { //Triple split is minimum.
			return;
		}
		List<Cell>cellsToAdd = new ArrayList<Cell>();
		//Figure out how many cells are to be made
		int count = ((int)size)/100 - 1;
		double splitAngle = Math.PI*2/count;
		double newCellSize = this.size/count;
		
		//Set this cells stats correctly.
		size = newCellSize;
		updateRadius();
		updateSpeed();
		
		//Create the new cells in appropriate locations
		//and add forced movement to those cells.
		Coordinate randomDirection = new Coordinate(RandomSingleton.getRandom().nextDouble(-1, 1), RandomSingleton.getRandom().nextDouble(-1, 1)).normalized(); 
		for(int i = 0; i < count;i++) {
			Coordinate dir = randomDirection.rotate(splitAngle*i).multiply(radius);
			Cell c = new Cell(this.position.add(dir),this.player, newCellSize);
			c.addForcedMovement(randomDirection.rotate(splitAngle*i), RandomSingleton.getRandom().nextDouble(4, 10));
			cellsToAdd.add(c);
		}
		//Finally add them to the player.
		for(Cell c: cellsToAdd) {
			player.addCell(c); 
		}
		
	}
	/**
	* check if cell is controlled by user or not
	* @return true if not controlled by user
	*/
	public boolean checkIfForced() {
		return forcedMovement;
	}
	/**
	* Returns a string representation of the cell
	* @return a string representation of the cell indicating position, size and to which player it belongs to
	*/
	public String toString() {
		return "Cell at position " + position + " of size " + size + " from player number" + player.getID();
	}
}
