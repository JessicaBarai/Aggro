package aggro.packets;

import java.io.Serializable;

import aggro.Coordinate;
import aggro.model.PlayerAction;

/**
 * This is a class that holds information in the form of a packet to be sent
 * over an ObjectStream. This specific packet is used to update players
 * mousepointer and their action
 * @author grupp8
 */
public class ActionPacket implements Serializable {
	private static final long serialVersionUID = -7605098431164666145L;
	public PlayerAction playerAction;
	public Coordinate mousePointer;

	/**
	 * Creates an actionpacket
	 * @param mousepointer Where the player's mousepointer is
	 * @param playerAction What action the player will perform. NONE indicates no change from previous action
	 *
	 */
	public ActionPacket(Coordinate mousePointer, PlayerAction playerAction) {
		this.playerAction = playerAction;
		this.mousePointer = mousePointer;
	}
}
