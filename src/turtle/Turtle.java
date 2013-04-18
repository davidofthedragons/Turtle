package turtle;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.JFrame;

import parser.MathParser;
import parser.MathParser.MathSyntaxException;

public class Turtle{

	private double x, y;
	private double angle = 0;
	private boolean pen = false; //true=down; false=up;
	private BufferedImage img;
	private Color color = Color.black;
	
	public Turtle(int x, int y) {
		this.x = x; this.y = y;
		img = new BufferedImage(TurtleMain.SX, TurtleMain.SY, BufferedImage.TYPE_INT_RGB);
		img.getGraphics().fillRect(0, 0, TurtleMain.SX, TurtleMain.SY);
	}
	
	public void move(double d) {
		double px=x, py=y;
		x += Math.cos(Math.toRadians(angle))*d;
		y += Math.sin(Math.toRadians(angle))*d;
		if (pen) {
			Graphics g = img.getGraphics();
			g.setColor(color);
			g.drawLine((int)px, (int)py, (int)x, (int)y);
			g.dispose();
		}
	}
	public void rot(double d) {
		angle += d;
		//System.out.println(angle);
	}
	public void penUp() {
		pen = false;
	}
	public void penDown() {
		pen = true;
	}
	public double getX() {return x;}
	public double getY() {return y;}
	public double getAngle() {return angle;}
	public boolean isPen() {return pen;}
	public BufferedImage getImage() {return img;}
	
	public void runCommands(String[] commands) {
		StringTokenizer tokenizer;
		for(int i=0; i<commands.length; i++) {
			if(commands[i]==null || commands[i].contains("//")) {
				//System.out.println("continuing...");
				continue;
			}
			if(commands[i].contains("RAND")) {
				tokenizer = new StringTokenizer(commands[i]);
				while (tokenizer.hasMoreTokens() && !tokenizer.nextToken().equals("RAND"));
				String token = tokenizer.nextToken();
				//System.out.println(token);
				Random rand = new Random();
				int t = Integer.parseInt(token);
				int r = (t>0)? rand.nextInt(t) : 0;
				//System.out.println(r);
				commands[i] = commands[i].replace("RAND " + token, Integer.toString(r));
				//System.out.println(commands[i]);
			}
			//System.out.println(commands[i]);
			if(commands[i].contains("MOVE")) {
				try {
					commands[i] = MathParser.parse(commands[i]);
					//System.out.println(commands[i]);
				} catch (NumberFormatException e) {} 
				catch (StringIndexOutOfBoundsException e) {} 
				catch (MathParser.MathSyntaxException e) {
					System.out.println("Cannot evaluate command");
				}
				tokenizer = new StringTokenizer(commands[i]);
				tokenizer.nextToken();
				try {
					move(Double.parseDouble(tokenizer.nextToken()));
				} catch (NumberFormatException e) {
					System.out.println("Cannot parse number");
				}
			}
			else if(commands[i].contains("ROT")) {
				try {
					commands[i] = MathParser.parse(commands[i]);
					tokenizer = new StringTokenizer(commands[i]);
					tokenizer.nextToken();
					rot(Double.parseDouble(tokenizer.nextToken()));
					//System.out.println(commands[i]);
				} catch (NumberFormatException e) {
					System.out.println("Cannot parse number");
				} 
				catch (StringIndexOutOfBoundsException e) {
					System.out.println("String index out of bounds for some reason...");
				} 
				catch (MathParser.MathSyntaxException e) {
					System.out.println("Cannot evaluate command");
				}
			}
			else if(commands[i].contains("PEN")) {
				if(commands[i].contains("UP")) penUp();
				else if(commands[i].contains("DOWN")) penDown();
				else {
					if(isPen()) System.out.println("DOWN");
					else System.out.println("UP");
				}
			}
			else if(commands[i].contains("RUN")) {
				tokenizer = new StringTokenizer(commands[i]);
				tokenizer.nextToken();
				runCommands(TurtleMain.parseFile(new File(tokenizer.nextToken(" "))));
			}
			else if(commands[i].contains("RESET")) {
				angle = 0;
				if(commands[i].contains("POS")) {
					x=0; y=0;
				}
				//x=0; y=0;
			}
			else if(commands[i].contains("ANGLE")) {
				System.out.println(angle);
			}
			else if(commands[i].contains("POS")) {
				System.out.println("(" + x + ", " + y + ")");
			}
			else if(commands[i].contains("PRINT")) {
				System.out.println(commands[i].replace("PRINT", ""));
			}
			else if(commands[i].contains("CLEAR")) {
				Graphics g = img.getGraphics();
				g.setColor(Color.white);
				g.fillRect(0, 0, TurtleMain.SX, TurtleMain.SY);
			}
			else if(commands[i].contains("LOOP")) {
				tokenizer = new StringTokenizer(commands[i]);
				tokenizer.nextToken();
				int iterations = Integer.parseInt(tokenizer.nextToken());
				//System.out.println("LOOP " + iterations);
				String[] cmds = new String[commands.length];
				int s = i+1;
				for(int j=s; !commands[j].equals("END"); j++) {
					cmds[j-s] = commands[j];
				}
				for(int k=0; k<iterations; k++) {
					String array[] = new String[cmds.length];
					for(int l=0; l<cmds.length && cmds[l]!=null; l++) {
						//System.out.println("l=" + l + ", k=" + k + ", cmds[" + l + "]='" + cmds[l] + "'");
						array[l] = cmds[l].replaceAll("i", Integer.toString(k));
					}
					runCommands(array);
				}
				while(!commands[i].equals("END")) {
					i++;
				}
				//i++;
				//System.out.println(i);
			}
			else if(commands[i].contains("COLOR")) {
				tokenizer = new StringTokenizer(commands[i]);
				tokenizer.nextToken();
				color = new Color(Integer.parseInt(tokenizer.nextToken()),
						Integer.parseInt(tokenizer.nextToken()), Integer.parseInt(tokenizer.nextToken()));
			}
			else System.out.println("Did not recognize command: " + commands[i]);
		}
	}
	
	

}
