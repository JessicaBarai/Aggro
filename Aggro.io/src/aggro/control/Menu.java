package aggro.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import aggro.model.PlayerAction;
import aggro.network.Client;

/**
 * This class provides user with start menu
 * 
 * @author grupp8
 *
 *
 */
public class Menu extends JDialog {

	private static final long serialVersionUID = -8775916006014540991L;
	private final JLabel nameLabel = new JLabel("Select name");
	private final JTextField nameField = new JTextField(15);
	JTextField redT, greenT, blueT;
	private String name;
	private Color color;
	private JFrame frame;
	private Random random;
	/**
	 * Creates a new Menu
	 * 
	 * @param client a client instance as the viewer of menu
	 *
	 *
	 */
	public Menu(Client client) {
		random = new Random();
		frame = new JFrame();
		frame.setTitle("Menu");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setSize(500, 300);
		try (FileInputStream propInput = new FileInputStream("clientconfig.properties");) {
			Properties prop = new Properties();
			prop.load(propInput);
			name = prop.getProperty("Name");
			int r = Integer.parseInt(prop.getProperty("ColorR"));
			int g = Integer.parseInt(prop.getProperty("ColorG"));
			int b = Integer.parseInt(prop.getProperty("ColorB"));
			color = new Color(r, g, b);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
			color = getRandomColor();
			saveColor();
			
		}
		client.sendAppearance(name, color);
		
		JPanel p4 = new JPanel(new GridLayout(1, 2));
		p4.add(nameLabel);

		JPanel p3 = new JPanel(new GridLayout(1, 2));
		p3.add(nameField);

		JPanel p1 = new JPanel();
		p1.add(p4);
		p1.add(p3);

		setLayout(new BorderLayout());
		add(p1, BorderLayout.CENTER);
		pack();

		frame.add(p1);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JButton startButton = new JButton("Start");
		p1.setLayout(new FlowLayout());
		p1.add(startButton);

		JButton colorChangeButton = new JButton("Change Color"); // Create new button
		JPanel Panel = new JPanel(); // Create JPanel for the button

		Panel.add(colorChangeButton); // Add button to the JFrame
		add(Panel, BorderLayout.SOUTH); // Set layout of the button to bottom of the window
		JLabel red, green, blue;

		red = new JLabel("Red"); // Create label Red
		green = new JLabel("Green"); // Create Label Green
		blue = new JLabel("Blue"); // Create Label Blue

		redT = new JTextField(5); // Create text field for input for red value
		greenT = new JTextField(5); // Create text field for input green value
		blueT = new JTextField(5); // Create text field for input blue value

		Panel.add(red);
		Panel.add(redT);
		Panel.add(blue);
		Panel.add(blueT);
		Panel.add(green);
		Panel.add(greenT);

		frame.add(Panel, BorderLayout.NORTH);
		
		colorChangeButton.addActionListener(e -> {
			if (!(redT.getText().isEmpty() || greenT.getText().isEmpty() || blueT.getText().isEmpty())) { //If none of the fields are empty.

				int r = Integer.parseInt(redT.getText());
				int g = Integer.parseInt(greenT.getText());
				int b = Integer.parseInt(blueT.getText());
				redT.setText("");
				greenT.setText("");
				blueT.setText("");
				color = new Color(r, g, b);
			} else {
				color  = getRandomColor();
			}
			client.sendAppearance(name, color);
			saveColor();
			colorChangeButton.setBackground(color);
			colorChangeButton.setOpaque(true);
			colorChangeButton.setBorderPainted(false);

		});

		startButton.addActionListener(e -> {
			if (!(nameField.getText().isEmpty())) {
				name = nameField.getText();
				nameField.setText("");
				saveName();
				client.sendAppearance(name, color);
			}
			frame.setVisible(false);
			client.sendAction(PlayerAction.SPAWN);

		});
		frame.setVisible(true);
	}
	
	/**
	 * Saves the current name into clientconfig.properties
	 */
	public void saveName() {
		try (FileInputStream in = new FileInputStream("clientconfig.properties");
				){

			Properties propout = new Properties();
			// propout.write(propOutput);
			propout.load(in);
			propout.setProperty("Name", name);
			FileOutputStream out = new FileOutputStream("clientconfig.properties");
			propout.store(out, null);
			
		} catch (IOException error) {
			error.printStackTrace();
		}
		
	}
	/**
	 * Saves the current color into clientconfig.properties
	 */
	public void saveColor() {
		
		try (FileInputStream in = new FileInputStream("clientconfig.properties");
				){

			Properties propout = new Properties();
			// propout.write(propOutput);
			propout.load(in);
			propout.setProperty("ColorR", String.valueOf(color.getRed()));
			propout.setProperty("ColorG", String.valueOf(color.getGreen()));
			propout.setProperty("ColorB", String.valueOf(color.getBlue()));
			FileOutputStream out = new FileOutputStream("clientconfig.properties");
			propout.store(out, null);
			
		} catch (IOException error) {
			error.printStackTrace();
		}
		
	}

	/**
	 * Return a random color
	 * @return a random color
	 */
	public Color getRandomColor() {
		return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
	}

	/**
	 * set menu visible
	 * @param bool if true menu visible
	 *
	 */
	public void setVisible(boolean bool) {
		frame.setVisible(bool);
	}

	/**
	 * Gets the name chosen by the user
	 * @return name user selected
	 *
	 */
	public String getPlayerName() {
		return name;
	}

	/**
	 * Gets the color chosen by the user
	 * @return color user selected
	 *
	 */
	public Color getPlayerColor() {
		return color;
	}

}