package aggro.control;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

import aggro.Coordinate;
import aggro.model.PlayerAction;
import aggro.network.Client;
/**
 * This class controls the flow of data from the user
 * @author grupp8
 *
 *
 */
public class Controller extends JFrame implements MouseMotionListener, MouseListener, KeyListener{
	
	private static final long serialVersionUID = -399537685179637577L;
	private Coordinate lastMousePosition;
	private Client client;
	private Menu menu;
	/**
	* Creates a new Controller
	* @param client client instance as the owner of controller
	*
	*/
	public Controller(Client client) {
		super("Aggro.io");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.client = client;
		addKeyListener(this);
		lastMousePosition = new Coordinate(250,250);
		menu = new Menu(client); 
	}
	/**
	* Indicating an action has occurred if space key pressed
	* @param e keyEvent indicating a keystroke
	*
	*/
	@Override
	public void keyPressed(KeyEvent e){  
		int key=e.getKeyCode();
		if(key==KeyEvent.VK_SPACE) {
			client.sendAction(PlayerAction.SPLIT);
		
	
	}
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	@Override
	public void mouseDragged(MouseEvent e) {
	}
	@Override
	/**
	* Assign a new Coordinate with same position as mouse when movement over the panel is detected
	* @param e MouseEvent listening to mouse
	*
	*/
	public void mouseMoved(MouseEvent e) {
		lastMousePosition = new Coordinate(e.getX(), e.getY());
	}
	/**
	* Invoke openMenu method on condition mouse clicked and the player belonging to client instance is dead  
	* @param e MouseEvent listening to mouse
	*
	*/
	@Override
	public void mouseClicked(MouseEvent e) {
		//client.sendAction(PlayerAction.SPAWN);
		if(client.playerIsDead()) {
			openMenu();
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	/**
	 *Returns the position of the mouse as a coordinate. If the mouse is offscreen the last position on screen is used.
	 * @return The last seen position of the mouse as a coordinate
	 */
	public Coordinate getLastMousePosition() {
		return lastMousePosition;
	}
	/**
	* Set the menu visible  
	*
	*/
	public void openMenu() {
		menu.setVisible(true);
	}
	/**
	* 
	* Set the menu invisible  
	*
	*/
	public void closeMenu() {
		menu.setVisible(false);
	}
}
