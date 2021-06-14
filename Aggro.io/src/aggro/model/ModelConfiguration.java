package aggro.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;
/**
 *read values for the game from file 
 *@author grupp8
 */
public class ModelConfiguration implements Serializable {

	private static final long serialVersionUID = -3617245348687695462L;
	private int mapSize;
	private int virusAmount;
	private int playerAmount;
	private int winConditionAmount;
	private int maximumFoodAmount;
	/**
	 *Created a ModelConfiguration reads values from file with help of FileInputStream
	 */
	public ModelConfiguration() {
		try (FileInputStream propInput = new FileInputStream("config.properties");) {
			Properties prop = new Properties();
			prop.load(propInput);

			// These were final before the file loading. Any way to make them constants?
			virusAmount = Integer.parseInt(prop.getProperty("virusAmount"));
			playerAmount = Integer.parseInt(prop.getProperty("playerAmount"));
			mapSize = Integer.parseInt(prop.getProperty("mapSize"));
			winConditionAmount = Integer.parseInt(prop.getProperty("winConditionAmount"));
			maximumFoodAmount = Integer.parseInt(prop.getProperty("maximumFoodAmount"));
			
		} catch (IOException e) {
			System.out.println(
					"Loading configurations failed. Add a config.properties file or add the relevant parameters to current file.");
			e.printStackTrace();
		}
		RandomSingleton.setMapSize(mapSize);
	}
	/**
	 *get the size of map 
	 *@return size of map as an int
	 */
	public int getMapSize() {
		return mapSize;
	}
	/**
	 *get the amount of virus in game 
	 *@return amount of virus as int
	 */
	public int getVirusAmount() {
		return virusAmount;
	}
	/**
	 *get the amount of player 
	 *@return amount of virus as int
	 */
	public int getPlayerAmount() {
		return playerAmount;
	}
	/**
	 *get the condition a player must fulfill to be able to win
	 *@return the condition as a int value
	 */
	public int getWinConditionAmount() {
		return winConditionAmount;
	}
	/**
	 *get the amount the maximum amount of food in the game
	 *@return amount as an int value
	 */
	public int getMaximumFoodAmount() {
		return maximumFoodAmount;
	}
	
}


/*		try {
			propInput = new FileInputStream("config.properties");
			prop.load(propInput);
			
			//These were final before the file loading. Any way to make them constants?
			virusAmount = Integer.parseInt(prop.getProperty("virusAmount"));
			playerAmount = Integer.parseInt(prop.getProperty("playerAmount"));
			mapSizeX = Integer.parseInt(prop.getProperty("mapSizeX"));
			mapSizeY = Integer.parseInt(prop.getProperty("mapSizeY"));
			winConditionAmount = Integer.parseInt(prop.getProperty("winConditionAmount"));
			initialFoodAmount = Integer.parseInt(prop.getProperty("initialFoodAmount"));
			maximumFoodAmount = Integer.parseInt(prop.getProperty("maximumFoodAmount"));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (propInput != null) {
				try {
					propInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}'
		*/