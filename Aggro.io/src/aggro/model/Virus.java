package aggro.model;

import java.io.Serializable;

import aggro.Coordinate;
/**
* representing virus on this game board 
* @author grupp8
*/
public class Virus implements Moveable, Serializable
{
	private static final long serialVersionUID = 3285990833472680592L;
	private Coordinate position;
    private double speed =1;
    private Coordinate waypoint;
    /**
	* create a virus with random way point and random position on the board
	*/
    public Virus() {
        waypoint = RandomSingleton.getRandomCoordinate();
        position = RandomSingleton.getRandomCoordinate();
    }
    /**
	* getters for the virus coordinate 
	* @return the position of this virus as a coordinate
	*/
    public Coordinate getPosition(){
        return this.position;
    }
    /**
	* move pattern for virus. With help of normalized vector in coordinate class
	* virus move with random values
	*/
    @Override
    public void move() {
        Coordinate c = waypoint.subtract(this.position).normalized(); //direction vector for the movement.
        position = position.add(c.multiply(speed));
         if(position.overlaps(waypoint,1)) {
                waypoint = RandomSingleton.getRandomCoordinate();
            }
    }

}