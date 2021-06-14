package aggro;

import java.io.Serializable;
/**
 * This is a class to store 2D coordinate. The class is immutable
 * @author grupp8
 * 
 *
 */
public class Coordinate implements Serializable{

	

	private static final long serialVersionUID = 5805799342793430814L;
	private double x;
	private double y;
	/**
	 * 
	 * Creates a new Coordinate (x,y)
	 * @param x the first double of the coordinate pair 
	 * @param y the second double of the coordinate pair
	 */
	public Coordinate(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns a sum of the two coordinates
	 * @param c one of the coordinate to be added 
	 * @return a new coordinate that is the sum of the two coordinates
	 */
	public Coordinate add(Coordinate c) {
		return new Coordinate(x + c.x, y + c.y);
	}
	/**

	 * Returns a boolean indicating if two coordinate are within a certain distance
	 * @param c one of the coordinates
	 * @param radius the distance to check if the coordinate lies within
	 * @return Returns true if there is overlap, otherwise false.
	 */
	public boolean overlaps(Coordinate c, double radius) {
		return Math.hypot(c.x - this.x, c.y - this.y) < radius;
	}
	/**

	 * Return the difference of the two coordinates
	 * @param c the coordinate which is negated and added to the other coordinate
	 * @return a new coordinate indicating the difference
	 */
	public Coordinate subtract(Coordinate c) {
		return new Coordinate(x-c.x, y-c.y);
	}
	/**
	 * Returns a normalized coordinate
	 * @return A new normalized coordinate. It's length is 1
	 */
	public Coordinate normalized(){
		
		double length = Math.hypot(x, y);
		if(length == 0) {
			return new Coordinate (0,0);
		}
		return new Coordinate(x/length, y/length);
	}
	/**
	 * Returns a coordinate multiplied by a scalar
	 * @param factor the factor the coordinate is to be scaled by 
	 * @return a new coordinate with it's coordinates scaled
	 */
	public Coordinate multiply(double factor) {
		return new Coordinate(this.x * factor, this.y*factor);
	}
	/**
	 * Getter for the first double of the coordinate pair
	 * @return the first double of the coordinate pair
	 */
	public double getX() {
		return x;
	}
	/**
	 * Getter for the second double of the coordinate pair
	 * @return the second double of the coordinate pair
	 */
	public double getY() {
		return y;
	}


/**

 * Returns a string representation of the coordinate
 * @return a string representation of the coordinate in the form (x, y)
 */
	@Override
	public String toString() {
		return "(" + x + ", " + y +")";
	}

	/**
	 * 
	 * Returns a coordinate rotated around (0, 0)
	 * @param angle The angle of the rotation in radians
	 * @return a new rotated coordinate
	 * 
	 */
	public Coordinate rotate(double angle) {
		return new Coordinate(x*Math.cos(angle)- y*Math.sin(angle), x*Math.sin(angle)+ y*Math.cos(angle));
	}
	/**
	 * Returns an int indicating the relationship of the y values of the coordinates.
	 * @param c1 the first coordinate
	 * @param c2 the second coordinate
	 * @return an int indicating the relationship between the coordinates y values. -1 if the first coordinate is less, 0 if the coordinates are equal or 1 otherwise.

	 */
	/**
	 * Return the coordiante (0, 0)
	 * @return The coordinate (0, 0)
	 */
	public static Coordinate Zero() {
		return new Coordinate(0,0);
	}
	
	public static int compare(Coordinate c1, Coordinate c2) {
		if(c1.y < c2.y) {
			return -1;
		}
		if(c1.y == c2.y) {
			return 0;
		}
		return 1;
	}
	
}