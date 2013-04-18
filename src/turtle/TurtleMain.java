package turtle;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import javax.swing.*;

public class TurtleMain extends JFrame {

	public static final int SX=600, SY=600;
	public static final int DELAY = 100;
	private Turtle turtle;
	private TurtlePanel turtlePanel = new TurtlePanel();
	private StringTokenizer tokenizer;
	private ArrayList<String> prevCmds = new ArrayList<String>();
	private int prevCmdsIndex = 0;
	
	public TurtleMain() {
		init();
	}
	public TurtleMain(File toParse) {
		init();
		parseFile(toParse);
	}
	private void init() {
		setTitle("Turtle Graphics");
		setSize(SX, SY);
		createGUI();
		
		turtle = new Turtle(0, 0);
		
		Thread updater = new Thread(new Runnable() {
			public void run() {
				while(true) {
					turtlePanel.repaint();
					
					try {
						Thread.sleep(DELAY);
					} catch (InterruptedException e) {
						System.out.println("Update thread was rudely interrupted");
					}
				}
			}
		});
		updater.start();
		
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	private void createGUI() {
		setLayout(new BorderLayout());
		add(turtlePanel, BorderLayout.CENTER);
		JPanel inputPanel = new JPanel();
		final JTextField inField = new JTextField(50);
		inField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] commands = {inField.getText()};
				prevCmds.add(inField.getText());
				turtle.runCommands(commands);
				inField.setText("");
				prevCmdsIndex = prevCmds.size()-1;
			}
		});
		Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				
				if(!prevCmds.isEmpty()) inField.setText(prevCmds.get(prevCmdsIndex));
				prevCmdsIndex = (prevCmdsIndex>0)? prevCmdsIndex-1 : 0;
			}
		};
		String keyStrokeAndKey = "UP";
		KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeAndKey);
		inField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, keyStrokeAndKey);
		inField.getActionMap().put(keyStrokeAndKey, action);
		inputPanel.add(inField);
		add(inputPanel, BorderLayout.SOUTH);
	}
	
	public static String[] parseFile(File file) {
		ArrayList<String> cmds = new ArrayList<String>();
		String[] commands = null;
		try {
			Scanner scanner = new Scanner(file);
			
			while(scanner.hasNextLine()) {
				cmds.add(scanner.nextLine());
			}
			commands = new String[cmds.size()];
			for(int i=0; i<commands.length; i++) {
				commands[i] = cmds.get(i);
				//System.out.println(commands[i]);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find file");
		}
		return commands;
	}
	
	class TurtlePanel extends JPanel {
		public void paint(Graphics g) {
			g.drawImage(turtle.getImage(), 0, 0, null);
			g.setColor(Color.blue);
			g.fillRect((int)turtle.getX(), (int)turtle.getY(), 5, 5);
		}
	}
	
	
	public static void main(String[] args) {
		new TurtleMain();
	}

}
