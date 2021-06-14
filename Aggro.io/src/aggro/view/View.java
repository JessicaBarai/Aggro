package aggro.view;

import java.awt.*;
import java.util.ArrayList;
import java.awt.event.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import javax.swing.*;

import aggro.Coordinate;
import aggro.control.Controller;
import aggro.model.Cell;
import aggro.model.Food;
import aggro.model.Player;
import aggro.model.Virus;
import aggro.network.Client;
/**
 * This is a class that draws the representation of the model for Aggro.io. It uses information that it recives from the model within the client specified.
 * @author grupp8
 *
 */
public class View extends JPanel {

	private static final long serialVersionUID = -8372741319543131369L;
	private Client client;
	private double foodRadius = 3;
	private double virusSpin = 0;
	private boolean virusSpinOn = true;
	private double cameraBoundaryLimit = 10;
	private Camera lastCamera;
	private double lastZoomFactor = 1;
	
	/**
	 * Creates a view using the information provided by the client, allows the controller to listen to it and following the player indicated by playerID.
	 * @param client The client that will communicate information to this
	 * @param controller The controller that will listen to this panel
	 * @param playerID The ID of the player that the view will follow
	 */
	public View(Client client, Controller controller, int playerID) {
		this.client = client;

		controller.add(this);
		controller.pack();
		controller.setVisible(true);
		controller.setSize(517, 540); // Makes 500/500 for the actual frame.
		addMouseMotionListener(controller);
		addMouseListener(controller);

		JPanel p = this;
		Timer t = new Timer(50, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				p.repaint();
			}
		});
		t.start();

	}

	protected int panelWidth() {
		return getWidth();
	}

	protected int panelHeight() {
		return getHeight();
	}

	/**
	 * Calls the UI delegate's paint method, if the UI delegate is non-null. We pass
	 * the delegate a copy of the Graphics object to protect the rest of the paint
	 * code from irrevocable changes (for example, Graphics.translate). If you
	 * override this in a subclass you should not make permanent changes to the
	 * passed in Graphics. For example, you should not alter the clip Rectangle or
	 * modify the transform. If you need to do these operations you may find it
	 * easier to create a new Graphics from the passed in Graphics and manipulate
	 * it. Further, if you do not invoker super's implementation you must honor the
	 * opaque property, that is if this component is opaque, you must completely
	 * fill in the background in a non-opaque color. If you do not honor the opaque
	 * property you will likely see visual artifacts.
	 * 
	 * The passed in Graphics object might have a transform other than the identify
	 * transform installed on it. In this case, you might get unexpected results if
	 * you cumulatively apply another transform.
	 * 
	 * @param g the Graphics object to protect
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Player thisPlayer = client.getCurrentPlayer();
		if (thisPlayer == null) {
			System.out.println("Player was null in view");
			return;
		}
		// get the view area
		double aspectRatio = (double) getHeight() / (double) getWidth();
		int mapSize = client.getMapSize();
		double zoomFactor;
		Camera camera;
		double frameWidth = getWidth();
		double frameHeight = getHeight();
		Coordinate frameCenter = new Coordinate(frameWidth / 2, frameHeight / 2);
		if (thisPlayer.isDead()) {
			if (aspectRatio > 1) {
				double cameraWidth = mapSize + 10;
				double cameraHeight = (mapSize + 10) * aspectRatio;
				camera = new Camera(new Coordinate(250 - cameraWidth / 2, 250 - cameraHeight / 2), cameraWidth,
						cameraHeight);
				zoomFactor = frameWidth / camera.getWidth();
			} else {
				double cameraWidth = (mapSize + 10) / aspectRatio;
				double cameraHeight = mapSize + 10;
				camera = new Camera(new Coordinate(250 - cameraWidth / 2, 250 - cameraHeight / 2), cameraWidth,
						cameraHeight);
				zoomFactor = frameHeight / camera.getHeight();
			}

		} else { // If the player is alive we have to assign a different camera.
			double cameraRange = thisPlayer.getCameraRange();

			double xLow = Double.POSITIVE_INFINITY;
			double xHigh = Double.NEGATIVE_INFINITY;
			double yLow = Double.POSITIVE_INFINITY;
			double yHigh = Double.NEGATIVE_INFINITY;

			for (Cell c : new ArrayList<Cell>(thisPlayer.getCellList())) {
				double x = c.getPosition().getX();
				double y = c.getPosition().getY();
				if (x < xLow) {
					xLow = x;
				}
				if (x > xHigh) {
					xHigh = x;
				}
				if (y < yLow) {
					yLow = y;
				}
				if (y > yHigh) {
					yHigh = y;
				}
			}
			// Finding the edges of the playercam
			xLow = xLow - cameraRange;
			xHigh = xHigh + cameraRange;
			yLow = yLow - cameraRange;
			yHigh = yHigh + cameraRange;

			// Bounding the edges of the player cam.
			xLow = Double.max(xLow, -cameraBoundaryLimit);
			yLow = Double.max(yLow, -cameraBoundaryLimit);
			xHigh = Double.min(xHigh, mapSize + cameraBoundaryLimit);
			yHigh = Double.min(yHigh, mapSize + cameraBoundaryLimit);

			if (aspectRatio > 1) {
				double cameraWidth = xHigh - xLow;
				double cameraHeight = (yHigh - yLow) * aspectRatio;
				camera = new Camera(
						new Coordinate((xLow + xHigh) / 2 - cameraWidth / 2, (yLow + yHigh) / 2 - cameraHeight / 2),
						cameraWidth, cameraHeight);
				zoomFactor = frameWidth / camera.getWidth();
			} else {
				double cameraWidth = (xHigh - xLow) / aspectRatio;
				double cameraHeight = yHigh - yLow;
				camera = new Camera(
						new Coordinate((xLow + xHigh) / 2 - cameraWidth / 2, (yLow + yHigh) / 2 - cameraHeight / 2),
						cameraWidth, cameraHeight);
				zoomFactor = frameHeight / camera.getHeight();
			}
			lastCamera = camera;
			lastZoomFactor = zoomFactor;
		}

		Coordinate cameraCenter = camera.getCenter();
		double fr = foodRadius * zoomFactor;
		for (Food f : new ArrayList<Food>(client.getFoodInfo(camera.getMinY() - fr, camera.getMaxY() + fr))) {
			g.setColor(f.getColor());
			Coordinate fPos = f.getPosition().subtract(cameraCenter).multiply(zoomFactor).add(frameCenter);
			Shape foodShape = new Ellipse2D.Double(fPos.getX() - fr, fPos.getY() - fr, 2 * fr, 2 * fr);
			g2d.fill(foodShape);
		}

		for (Player p : new ArrayList<Player>(client.getPlayerInfo())) {
			String name = p.getPlayerName();

			for (Cell cell : new ArrayList<Cell>(p.getCellList())) {
				g.setColor(p.getColor());
				double radius = cell.getRadius() * zoomFactor;
				Coordinate position = cell.getPosition().subtract(cameraCenter).multiply(zoomFactor).add(frameCenter);
				Shape cellShape = new Ellipse2D.Double((position.getX() - radius), (position.getY() - radius),
						2 * radius, 2 * radius);
				g2d.fill(cellShape);

				g2d.setColor(Color.black);
				Font font = g2d.getFont();
				font = font.deriveFont(Font.PLAIN, fontSizeDeterminer(cell, zoomFactor, name));
				g2d.setFont(font);
				FontMetrics fontMetrics = g2d.getFontMetrics();
				g2d.drawString(name, (float) position.getX() - (fontMetrics.stringWidth(name)) / 2,
						(float) position.getY() + fontMetrics.getHeight() / 4);
			}
		}

		for (Virus v : new ArrayList<Virus>(client.getVirusInfo())) {
			g.setColor(Color.GREEN);
			if (virusSpinOn) {
				virusSpin += 3 % 360;
			}
			double radius = 7 * zoomFactor;
			Coordinate virusPos = v.getPosition().subtract(cameraCenter).multiply(zoomFactor).add(frameCenter);
			Shape virusShape = new Arc2D.Double((virusPos.getX() - radius), (virusPos.getY() - radius), 2 * radius,
					2 * radius, virusSpin, 60, Arc2D.PIE);
			g2d.fill(virusShape);
			virusShape = new Arc2D.Double((virusPos.getX() - radius), (virusPos.getY() - radius), 2 * radius,
					2 * radius, 90 + virusSpin, 60, Arc2D.PIE);
			g2d.fill(virusShape);
			virusShape = new Arc2D.Double((virusPos.getX() - radius), (virusPos.getY() - radius), 2 * radius,
					2 * radius, 180 + virusSpin, 60, Arc2D.PIE);
			g2d.fill(virusShape);
			virusShape = new Arc2D.Double((virusPos.getX() - radius), (virusPos.getY() - radius), 2 * radius,
					2 * radius, 270 + virusSpin, 60, Arc2D.PIE);
			g2d.fill(virusShape);
		}

		Player winner = client.getWinner();
		if (winner != null) {
			g2d.setColor(Color.black);
			Font font = g2d.getFont();
			font = font.deriveFont(Font.PLAIN, 50);
			g2d.setFont(font);
			FontMetrics fontMetrics = g2d.getFontMetrics();
			String playerName = winner.getPlayerName();
			g2d.drawString(playerName, 250 - (fontMetrics.stringWidth(playerName) / 2), 220);
			g2d.drawString("HAS WON!", 250 - (fontMetrics.stringWidth("HAS WON!") / 2), 270);
		}
	}
	/**
	 * Returns the last camera that was rendered
	 * @return The camera that was last displayed to the player
	 */
	public Camera getLastCamera() {
		return lastCamera;
	}
	/**
	 * Returns what last zoomfactor was used in the latest rendering
	 * @return The zoomfactor that was used in the latest rendering
	 */
	public double getLastZoomFactor() {
		return lastZoomFactor;
	}
	/**
	 * Determines the fontsize to use based on cellsize, zoomFactor and namelength
	 * @param cell The cell that will be written over
	 * @param zoomFactor The zoomfactor of the rendering
	 * @param name The string that will be written over the cell
	 * @return the size, in an int, of the font to be used. 0 if it is too small
	 */
	private int fontSizeDeterminer(Cell cell, double zoomFactor, String name) {
		int fontsize = (int) (cell.getRadius() * zoomFactor / Integer.max(3, name.length()) * 5);
		if (fontsize < 6) {
			return 0;
		}
		return fontsize;
	}
}
