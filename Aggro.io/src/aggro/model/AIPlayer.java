package aggro.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.Timer;

import aggro.Coordinate;
/**
 * This class represent a player which movement generates from random values
 * @author grupp8
 *
 *
 */

public class AIPlayer extends Player implements Serializable{
	
	private static final long serialVersionUID = -8460408397317922254L;
	private Coordinate waypoint;
	/**
	* Creates a new AIPlayer and start a a timer to update AIplayer
	* @param id a value identifying player
	*
	*
	*/
	public AIPlayer(int id) {
		super(id);
		waypoint = RandomSingleton.getRandomCoordinate();
		super.setMousePointer(waypoint);
		super.updateAppearance(getAIName(), super.getColor());
		Timer t = new Timer(10000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	timerAction(); //Cannot call super in here. Better way to fix?
            }
        });
        t.start();
	}
	
	private void timerAction() {
		super.update(waypoint, PlayerAction.SPLIT);
	}
	
	private String getAIName(){
		String[] arr = {"KillBot", "EXTERMINATE", "Bender", "BlobBot", "EatsABot", "Totally a human", "01001111", "4572696B", "NomBot", "Not a bot", "Team pls", "E"};
		return arr[RandomSingleton.getRandom().nextInt(0, arr.length - 1)];
	}
	/**
	* If AIplayer is destroyed reset AIplayer
	*
	*
	*
	*/
	public void update(){
		if(super.getCellList().size() == 0) {
			super.update(waypoint, PlayerAction.SPAWN);
		}
		
		
		
		for(Cell c :super.getCellList()) {
			if(c.getPosition().overlaps(waypoint,5)) {
				waypoint = RandomSingleton.getRandomCoordinate();
				super.update(waypoint, PlayerAction.NONE);
			}
		}
	}
}
