package aggro.model;
import java.awt.Color;
import java.io.Serializable;

import aggro.Coordinate;

/**
* Representing the food generating on game board
* @author grupp8
*/
public class Food implements Comparable<Coordinate>, Serializable{
	
	
	private static final long serialVersionUID = 4508044341017137468L;
	private Coordinate position;
	/**
	* gets the position of food instance
	* @return the position of the food on the board as a coordinate
	*/
	public Coordinate getPosition() {
		return position;
	}
	
	public final static double VALUE = 10;
	private Color color;
	/**
	* gets the color of food instance
	* @return the color of the food on the board
	*/
	public Color getColor(){
		return color;
	}
	
	/**
	* create food instance
	* @param position generate the food instance on this position on the board
	*/
	public Food(Coordinate position) {
		this.position = position;
		this.color = Color.PINK; //Just a temporary setting
	}
	/**
	* Returns a string representation of the food
	* @return a string representation of the food with info of the food's position
	*/
	@Override
	public String toString() {
		return "Food: " + position.toString();
	}
	
	/**
	* compare the food's position with another coordinate
	* @param o the coordinate which will be compared to with food's
	* @return an integer indicating the relationship between the coordinates y values. -1 if the first coordinate is less, 0 if the coordinates are equal or 1 otherwise.
	*/
	@Override
	public int compareTo(Coordinate o) {
		return Coordinate.compare(position, (Coordinate)o);
	}
	
	
}
