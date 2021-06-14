package aggro.packets;

import java.awt.Color;
import java.io.Serializable;

/**
 * This is a class that holds information in the form of a packet to be sent
 * over an ObjectStream. This specific packet contains an appearance change for the player, name and color
 * @author grupp8
 */

public class NewPlayerPacket implements Serializable {
	private static final long serialVersionUID = -1319785656984270036L;
	public String name;
	public Color color;
	
	/**
	 * Creates a newplayerpacket
	 * @param name The name the player will change to
	 * @param color The color the player will change to
	 */
	public NewPlayerPacket(String name, Color color) {
		this.name  = name;
		this.color = color;
	}
}
