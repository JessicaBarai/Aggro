package aggro.network;
import aggro.packets.*;
/**
 * This is a class that decodes packets and informs the server of them.
 * @author grupp8
 */

public class PacketHandler {

	private Server server;
	
	/**
	 * Creates a packethandler given the server it will be communicating to.
	 * @param server The server that the packethandler will deliver the decoded packets to.
	 */
	public PacketHandler(Server server) {
		this.server = server;
	}
	
	/**
	 * Identifies the packet and informs the server.
	 * @param packet The packet to be decoded. 
	 * @param id The id value of the connection that received the packet.
	 */
	public void receivePacket(Object packet, int id) {
		if(packet instanceof NewPlayerPacket) {
			NewPlayerPacket p = (NewPlayerPacket)packet;
			server.updatePlayerAppearance(p.name, p.color, id);
		}else if(packet instanceof ActionPacket) {
			ActionPacket p  = (ActionPacket)packet;
			server.updatePlayer(p.mousePointer, p.playerAction, id);
		}
		
	}

} 
