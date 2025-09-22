import java.awt.*;
import java.awt.event.*;
import java.util.*;
import acm.graphics.*;
import acm.program.*;
import acm.util.*;
/**
 * 
 */

/**
 * @author andav
 *
 */
public class antColony extends GraphicsProgram {
	
	private static int APPLICATION_WIDTH = 900;//1900
	private static int APPLICATION_HEIGHT = 900;//900
	private static double size;
	private static int Homex = 4; // actual position is +1 for Homexy and Foodxy
	private static int Homey = 4;
	private static int Foodx = 15;
	private static int Foody = 15;
	private static int fps = 60;
	private static RandomGenerator rgen = RandomGenerator.getInstance();
	private static boolean start = false;
	Ant[] ants = new Ant[20];
	Grid[][] tiles = new Grid[20][20];
	
	private class MyCompListener extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent e) {
			APPLICATION_WIDTH = e.getComponent().getWidth();
			APPLICATION_HEIGHT = e.getComponent().getHeight();
			double x = (APPLICATION_WIDTH-100) / 20;
			double y = (APPLICATION_HEIGHT-100) / 20;
			size = Math.min(x, y);
			if (start) {
				antColon();
			}
		}
	}
	
	/*private GObject getCollidingObject(GObject ant) {
		GObject prect = this.getElementAt(ant.getX(), ant.getY());
		return prect;
	}*/
	
	private static class Ant {
		private static double mysize = size-10; //Minus x //size
		private static double offset = 50+5; //add (x/2) //50
		
		public double xpos;
		public double ypos;
		private double shiftx;
		private double shifty;
		private Color c = Color.gray;
		public GOval antBod = new GOval(mysize, mysize);
		private boolean pathF = true;
		public int potency = 15;
		private int count = 0;
		//public GLabel show = new GLabel(""+potency);
		
		public Ant() {
			xpos = Homex;
			ypos = Homey;
			shiftx = (xpos*size);
			shifty = (ypos*size);
			antBod.setBounds((offset+shiftx), (offset+shifty), mysize, mysize);
			//show.setLocation((offset+shiftx), (offset+shifty));
			antBod.setColor(c);
			antBod.setFilled(true);
		}
		
		public void update() {
			mysize = size-10;
			shiftx = (xpos*size);
			shifty = (ypos*size);
			antBod.setBounds((offset+shiftx), (offset+shifty), mysize, mysize);
			//show.setLabel(""+potency);
			//show.setLocation((offset+shiftx), (offset+shifty));
		}
		
		public void move(int dx, int dy) {
			/*int dx = rgen.nextInt(-1, 1);
			int dy = rgen.nextInt(-1, 1);
			//(dx == 0) && (dy == 0)
			while (((Math.abs(dx) == Math.abs(dy)) && (0 == 0)) || ((xpos + dx) < 0) || ((ypos + dy) < 0)  || ((xpos + dx) > 19) || ((ypos + dy) > 19)){
				dx = rgen.nextInt(-1, 1);
				dy = rgen.nextInt(-1, 1);
			}*/
			antBod.move(size*dx, size*dy);
			//show.move(size*dx, size*dy);
			//show.setLabel(""+potency);
			xpos += dx;
			ypos += dy;
			if (potency != 0 && count >= 1) {
				potency--;
				count = 0;
			} else {
				count++;
			}
		}
		
	}
	
	private static class Grid {
		private static double offset = 50;
		
		public double xpos;
		public double ypos;
		private double shiftx;
		private double shifty;
		private Color c = Color.white;
		private boolean filled = false;
		public GRect tile = new GRect(size, size);
		public int potency = 0;
		public boolean pathF = true;
		
		public Grid(int x, int y) {
			xpos = x;
			ypos = y;
			shiftx = (xpos*size);
			shifty = (ypos*size);
			tile.setBounds((offset+shiftx), (offset+shifty), size, size);
			
		}
		
		public void update() {
			shiftx = (xpos*size);
			shifty = (ypos*size);
			setColor(c);
			tile.setBounds((offset+shiftx), (offset+shifty), size, size);
		}
		
		public void setColor(Color c) {
			int red = 255;
			int green = 255;
			int blue = 255;
			if (c == Color.red) {
				pathF = true;
				green = 255 - (potency*17);
				blue = 255 - (potency*17);
			} else if (c == Color.yellow) {
				pathF = false;
				blue = 255 - (potency*17);
			}
			this.c = new Color(red,green,blue);
			tile.setFillColor(this.c);
			filled = true;
			tile.setFilled(filled);
		}
		
	}
	
	/*private void grid() {
		int Ocount = 0;
		for (int i = 50; i < (APPLICATION_HEIGHT-50); i+=size) {
			int Icount = 0;
			for (int j = 50; j < (APPLICATION_WIDTH-50); j+=size) {
				Icount++;
				GRect part = new GRect(j,i,size,size);
				this.add(part);
				if (Icount == 20) {
					break;
				}
			}
			Ocount++;
			if (Ocount == 20) {
				break;
			}
		}
	}*/
	
	private void placeTri(double x, double y, Color c) {
		GPolygon Home = new GPolygon();
		double shiftx = (x*size);
		double shifty = (y*size);
		double mysize = size-10; //Minus x //size
		double offset = 50+5; //add (x/2) //50
		double[] top = {((mysize/2)+offset+shiftx),(offset+shifty)};
		double[] left = {(offset+shiftx),(mysize+offset+shifty)};
		double[] right = {(mysize+offset+shiftx),(mysize+offset+shifty)};
		Home.addVertex(top[0],top[1]);
		Home.addVertex(left[0],left[1]);
		Home.addVertex(right[0],right[1]);
		Home.setFillColor(c);
		Home.setFilled(true);
		this.add(Home);
	}
	
	/*private void delete() {
		this.removeAll();
	}*/
	
	private void antColon() {
		this.removeAll();
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles.length; j++) {
				tiles[i][j].update();
				this.add(tiles[i][j].tile);
			}
		}
		placeTri(Homex,Homey,Color.red);
		placeTri(Foodx,Foody,Color.yellow);
		for (int i = 0; i < ants.length; i++) {
			ants[i].update();
			this.add(ants[i].antBod);
			//this.add(ants[i].show);
		}
	}
	
	private int[] gamble(Ant ant) {
		ArrayList<Integer> chanceP = new ArrayList<Integer>();
		ArrayList<String> direction = new ArrayList<String>();
		boolean Apath = ant.pathF;
		int s = 195;
		
		int x = (int) (ant.xpos-1);
		if (x >= 0){
			int pot = tiles[x][(int) ant.ypos].potency;
			boolean Tpath = tiles[x][(int) ant.ypos].pathF;
			if (Apath != Tpath) {
				int form = (50-(3*(15-pot)));
				chanceP.add(form+s);
				direction.add("left");
			} else {
				int form = (50-(3*(pot)));
				chanceP.add(form);
				direction.add("left");
			}
		}
		
		x = (int) (ant.xpos+1);
		if (x < 20){
			int pot = tiles[x][(int) ant.ypos].potency;
			boolean Tpath = tiles[x][(int) ant.ypos].pathF;
			if (Apath != Tpath) {
				int form = (50-(3*(15-pot)));
				chanceP.add(form+s);
				direction.add("right");
			} else {
				int form = (50-(3*(pot)));
				chanceP.add(form);
				direction.add("right");
			}
		}
		
		int y = (int) (ant.ypos-1);
		if (y >= 0){
			int pot = tiles[(int) ant.xpos][y].potency;
			boolean Tpath = tiles[(int) ant.xpos][y].pathF;
			if (Apath != Tpath) {
				int form = (50-(3*(15-pot)));
				chanceP.add(form+s);
				direction.add("up");
			} else {
				int form = (50-(3*(pot)));
				chanceP.add(form);
				direction.add("up");
			}
		}
		
		y = (int) (ant.ypos+1);
		if (y < 20){
			int pot = tiles[(int) ant.xpos][y].potency;
			boolean Tpath = tiles[(int) ant.xpos][y].pathF;
			if (Apath != Tpath) {
				int form = (50-(3*(15-pot)));
				chanceP.add(form+s);
				direction.add("down");
			} else {
				int form = (50-(3*(pot)));
				chanceP.add(form);
				direction.add("down");
			}
		}
		
		while (true) {
			int i = rgen.nextInt(0, (chanceP.size()-1));
			//for (int i = 0; i < chanceP.size(); i++) {
				int rollDice = rgen.nextInt(0,100);
				if (rollDice < chanceP.get(i)) {
					String WINNER = direction.get(i);
					if (WINNER.equals("left")) {
						int[] prize = {-1,0};
						return prize;
					} else if (WINNER.equals("right")) {
						int[] prize = {1,0};
						return prize;
					} else if (WINNER.equals("up")) {
						int[] prize = {0,-1};
						return prize;
					} else if (WINNER.equals("down")) {
						int[] prize = {0,1};
						return prize;
					}
				}
			//}//
		}
	}
	
	private void huntntrail() {
		int count = 0;
		while (true) {
			this.pause(1000/fps);
			for (int i = 0; i < ants.length; i++) {
				Ant ant = ants[i];
				Grid tile = tiles[(int) ants[i].xpos][(int) ants[i].ypos];
				
				int[] move = gamble(ant);
				ant.move(move[0],move[1]);
				
				if ((ant.xpos == Homex) && (ant.ypos == Homey)) {
					ant.potency = 15;
					ant.pathF = true;
					ant.antBod.setColor(Color.red);
				} else if ((ant.xpos == Foodx) && (ant.ypos == Foody)) {
					ant.potency = 15;
					ant.pathF = false;
					ant.antBod.setColor(Color.yellow);
				} else if ((ant.potency < tile.potency) && (ant.pathF == tile.pathF)){ //(Path == Path)
					ant.potency = tile.potency;
				} else if (ant.potency > tile.potency) {
					tile.potency = ant.potency;
					if (ant.pathF) { //In the loop at the end not the else statement
						tile.setColor(Color.red);
					} else {
						tile.setColor(Color.yellow);
					}
				}
			}
			if (count >= 100) {//25
				for (int i = 0; i < tiles.length; i++) {
					for (int j = 0; j < tiles.length; j++) {
						if (tiles[i][j].potency != 0) {
							tiles[i][j].potency--;
							if (tiles[i][j].pathF) {
								tiles[i][j].setColor(Color.red);
							} else {
								tiles[i][j].setColor(Color.yellow);
							}
						}
					}
				}
				count = 0;
			} else {
				count++;
			}
		}
	}
	
	public void run() {
		String Hcoords = "";//readLine("Home coords (x-y): ");
		if (Hcoords.equals("")) {
			//
		} else {
			String[] temp = Hcoords.split("-");
			Homex = (Integer.parseInt(temp[0]) - 1);
			Homey = (Integer.parseInt(temp[1]) - 1);
		}
		Hcoords = "";//readLine("Food coords (x-y): ");
		if (Hcoords.equals("")) {
			//
		} else {
			String[] temp = Hcoords.split("-");
			Foodx = (Integer.parseInt(temp[0]) - 1);
			Foody = (Integer.parseInt(temp[1]) - 1);
		}
		this.setSize(APPLICATION_WIDTH,APPLICATION_HEIGHT);
		this.addComponentListener(new MyCompListener());
		this.waitForClick();
		start = true;
		double x = (APPLICATION_WIDTH-100) / 20;
		double y = (APPLICATION_HEIGHT-100) / 20;
		size = Math.min(x, y);
		for (int i = 0; i < ants.length; i++) {
			ants[i] = new Ant();
		}
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles.length; j++) {
				tiles[i][j] = new Grid(i,j);
			}
		}
		antColon();
		huntntrail();
	}
	public static void main(String[] args) {
		new antColony().start(args);
	}
}
