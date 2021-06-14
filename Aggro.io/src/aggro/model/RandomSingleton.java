package aggro.model;
import java.util.SplittableRandom;

import aggro.Coordinate;
/**
* generate a random coordinate with help of design pattern singleTon
* @author grupp8 
*/
public enum RandomSingleton {
	;
	private static final SplittableRandom rng = new SplittableRandom();
	private static int mapSize;
	/**
	* getters for field splittableRandom 
	* @return splittableRandom for this enumerator
	*/
	public static SplittableRandom getRandom() {
		return rng;
	}
	/**
	* generate a random coordinate with help of splittableRandom
	* @return a random coordinate as (x,y)
	*/
	public static Coordinate getRandomCoordinate() {
		Coordinate c = new Coordinate(rng.nextDouble(0, mapSize), rng.nextDouble(0, mapSize));
		return c;
	}
	protected static void setMapSize(int size) {
		mapSize = size;
	}
	
}
