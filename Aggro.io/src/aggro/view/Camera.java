package aggro.view;

import aggro.Coordinate;
/**
 * This is a class that holds information on what the player can see, a field of view. 
 * It basically conatins a rectangle, a corner and its dimensions
 * @author grupp8
 */


public class Camera {
	private Coordinate bottomLeftCorner;
	private double width;
	private double height;
	
	/**
	 * Creates a camera.
	 * @param bottomLeftCorner A coordinate set to the lowest values of both dimensions.
	 * @param width A double indicating how large the camera is in the x axis
	 * @param height A double indicating how large the camera is in the y axis
	 */
	public Camera(Coordinate bottomLeftCorner, double width, double height) {
		this.bottomLeftCorner = bottomLeftCorner;
		this.width = width;
		this.height = height;
		
	}
	/**
	 * Returns the coordinate indicating the center of the camera
	 * @return The coordinate indicating the center of the camera
	 */
	public Coordinate getCenter(){
		return new Coordinate(bottomLeftCorner.getX() + width/2, bottomLeftCorner.getY() + height/2);
	}
	/**
	 * Returns the width of the camera
	 * @return The width, as a double, of the camera
	 */
	public double getWidth() {
		return width;
	}
	/**
	 * Returns the height of the camera
	 * @return The height, as a double, of the camera
	 */
	public double getHeight() {
		return height;
	}
	/**
	 * Returns the lowest value on the y-axis that is part of the camera
	 * @return The lowest value on the y-axis that is part of the camera as a double
	 */
	public double getMinY() {
		return bottomLeftCorner.getY();
	}
	/**
	 * Returns the highest value on the y-axis that is part of the camera
	 * @return The highest value on the y-axis that is part of the camera as a double
	 */
	public double getMaxY() {
		return bottomLeftCorner.getY() + height;
	}
	/**
	 * Returns the lowest value on the x-axis that is part of the camera
	 * @return The lowest value on the x-axis that is part of the camera as a double
	 */
	public double getMinX() {
		return bottomLeftCorner.getX();
	}
	/**
	 * Returns the highest value on the x-axis that is part of the camera
	 * @return The highest value on the x-axis that is part of the camera as a double
	 */
	public double getMaxX() {
		return bottomLeftCorner.getX() + width;
	}
	/**
	 * Returns a string representation of the camera
	 * @return a string representation of the camera
	 */
	@Override
	public String toString() {
		return("Rectangle of width " + width + " and height " + height + " with the lower left corner at " + bottomLeftCorner);
	}
}
