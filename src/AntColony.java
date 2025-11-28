import acm.graphics.GOval;
import acm.graphics.GPolygon;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

/**
 * @author andav
 */
public class AntColony extends GraphicsProgram {
    private final int BG_SIZE = 1000;
    private final int GRID_SIZE = 20; //Number of boxes in each row and column
    private final int SIZE = 40; //The relative size of each element drawn
    private final int OFFSET = 50; //The offset from the edge of the frame
    private boolean mouseVarPressed = false;

    //Factors to change
    //The starting position for the ants
    private final int START_X = 4;
    private final int START_Y = 4;

    //The position of the "food" for the ants
    private final int END_X = 15;
    private final int END_Y = 15;

    private final int POTENCY = 85; //1, 3, 5, 15, 17, 51, 85, and 255 //Numbers represent factors of 255 (for coloring purposes)
    private final int FPS = 60;
    public int decayRate = (int)((.01) * FPS); //The actual value assigned to decayRate has a min of 0 where 0 means it decays once every two frames

    private final int NEG_DIFF = 255/POTENCY;
    private final int DECAY = (decayRate + 1);
    
    public Grid tiles = new Grid();
    public Mark home = new Mark(START_X, START_Y, Color.red);
    public Mark food = new Mark(END_X, END_Y, Color.yellow);
    public Ant[] ants = new Ant[50];

    public class Grid {
        public Box[][] grid = new Box[GRID_SIZE][GRID_SIZE];

        public Grid() {
            this.drawGrid();
        }

        public void drawGrid() {
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    this.grid[i][j] = new Box(i, j);
                }
            }
        }

        public class Box extends MouseAdapter {
            private boolean mouseVarEntered = false;
            private int x;
            private int y;
            
            public GRect obj;
            public GRect backgroundObj;
            public int trailH = 0; //The trail left behind by a home ant (red) (0 means none)
            public int trailF = 0; //The trail left behind by a food ant (yellow)

            public Box(int i, int j) {
                this.x = i;
                this.y = j;
                this.obj = new GRect((OFFSET + (i * SIZE)), (OFFSET + (j * SIZE)), SIZE, SIZE);
                this.backgroundObj = new GRect((OFFSET + (i * SIZE)), (OFFSET + (j * SIZE)), SIZE, SIZE);
                this.backgroundObj.addMouseListener(this);
            }

            public void updateColor() {
                int red = 255;
                int green = 255;
                int blue = 255;

                if (this.trailH >= this.trailF) {
                    green = 255 - (this.trailH * NEG_DIFF);
                    blue = 255 - (this.trailH * NEG_DIFF);
                } else {
                    blue = 255 - (this.trailF * NEG_DIFF);
                }

                this.obj.setFillColor(new Color(red,green,blue));
                this.obj.setFilled(true);
            }

            //Keeps the Potency at the start(home) and end(food) the same throughout
            public void lock() {
                if (this.x == START_X && this.y == START_Y) {
                    this.trailH = POTENCY;
                }
                if (this.x == END_X && this.y == END_Y) {
                    this.trailF = POTENCY;
                }
            }

            /**
             * Updates the value of a trail based on which which ant (pathToFood) is currently on it
             * @param smell the pontency that the ant currently has
             * @param pathToFood the type of ant sending the pontency
             */
            public void setTrail(int smell, boolean pathToFood) {
                if (pathToFood) {
                    this.trailH = Math.max(this.trailH, smell);
                } else {
                    this.trailF = Math.max(this.trailF, smell);
                }
                this.lock();
                this.updateColor();
            }
            
            public void decay() {
                if (decayRate == 0) {
                    this.trailH--;
                    this.trailH = Math.max(0, this.trailH);
                    this.trailF--;
                    this.trailF = Math.max(0, this.trailF);
                    this.lock();
                    this.updateColor();
                }
            }

            //Everything under this controls making obstacles (the graphics part)
            public void mousePressed(MouseEvent e) {
                mouseVarPressed = true;
                this.blackOut(e);
            }

            public void mouseReleased(MouseEvent e) {
                mouseVarPressed = false;
            }
            
            public void mouseEntered(MouseEvent e) {
                mouseVarEntered = true;
                this.blackOut(e);
            }

            public void mouseExited(MouseEvent e) {
                mouseVarEntered = false;
            }

            public void switchState(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    this.backgroundObj.setColor(Color.black);
                    this.backgroundObj.setFilled(true);
                } else {
                    this.backgroundObj.setFilled(false);
                }
            }

            public void blackOut(MouseEvent e) {
                if (mouseVarPressed && mouseVarEntered) {
                    switchState(e);
                }
            }
        }
    }

    public class Mark {
        private final int MY_SIZE = (SIZE - 10); //10 accounts for the spacing of 5 on both sides.
        private final int MY_OFFSET = (OFFSET + 5); //Leaves a inside spacing of 5
        
        public GPolygon obj = new GPolygon();

        public Mark(int x, int y, Color c) {
            //Creates the appropiate starting point
            x *= SIZE;
            y *= SIZE;

            int y1 = (y + this.MY_OFFSET + this.MY_SIZE); //The bottom
            int y2 = (y + this.MY_OFFSET); //The top

            int x1 = (x + this.MY_OFFSET); //The left
            int x2 = (x1 + (this.MY_SIZE / 2)); //the center
            int x3 = (x1 + this.MY_SIZE); //The right

            this.obj.addVertex(x1, y1); //Bottom Left
            this.obj.addVertex(x2, y2); //Top
            this.obj.addVertex(x3, y1); //Bottom Right

            this.obj.setFillColor(c);
            this.obj.setFilled(true);
        }
    }

    public class Ant {
        private final int MY_SIZE = (SIZE - 10); //10 accounts for the spacing of 5 on both sides.
        private final int MY_OFFSET = (OFFSET + 5); //Leaves a inside spacing of 5
        private final Color MY_COLOR = Color.gray;
        
        public GOval body;
        public int x;
        public int y;
        public boolean pathToFood = true; //All ants begin their path to food
        public int smell = POTENCY; //All begin with max potency

        public Ant(int x, int y) {
            //Human starting point
            this.x = x;
            this.y = y;

            //Creates the appropiate starting point
            x *= SIZE;
            y *= SIZE;

            int x1 = (x + this.MY_OFFSET); //The left
            int y1 = (y + this.MY_OFFSET); //The top

            this.body = new GOval(x1, y1, this.MY_SIZE, this.MY_SIZE);
            this.body.setFillColor(this.MY_COLOR);
            this.body.setFilled(true);
        }

        public void wander() {
            Point[] direction = {new Point(-1,0), new Point(1,0), new Point(0,-1), new Point(0,1)};
            ArrayList<Point> points = new ArrayList<Point>();
            for (int i = 0; i < direction.length; i++) {
                int tempX = (direction[i].x + this.x);
                int tempY = (direction[i].y + this.y);
                //Confirm that going in a certain direction is valid (not out of bounds or an obstacle)
                if (!((tempX < 0) || (tempX >= GRID_SIZE) || (tempY < 0) || (tempY >= GRID_SIZE)) && !(tiles.grid[tempX][tempY].backgroundObj.isFilled())) {
                    //If on path to food then tiles that have "food pheromones" (trailF) are good
                    //While those that don't (or even have "food pheromones" (trailH)) are bad
                    if (this.pathToFood) {
                        int good = tiles.grid[tempX][tempY].trailF;
                        int bad = tiles.grid[tempX][tempY].trailH;
                        int chance = ((POTENCY - bad) * (POTENCY - bad)) + (100 * (good * good)) + 1;
                        direction[i].setChance(chance);
                        
                    } else {
                        int good = tiles.grid[tempX][tempY].trailH;
                        int bad = tiles.grid[tempX][tempY].trailF;
                        int chance = ((POTENCY - bad) * (POTENCY - bad)) + (100 * (good * good)) + 1;
                        direction[i].setChance(chance);
                    }
                    points.add(direction[i]);
                }
            }
            Point pickedDirection = Rand.choose(points);

            int dx = (pickedDirection.x * SIZE);
			int dy = (pickedDirection.y * SIZE);

            this.x = pickedDirection.x + this.x;
            this.y = pickedDirection.y + this.y;

            this.smell -= 2; //Also a factor that can be changed.
            this.smell = Math.max(0, this.smell);

            this.placeSmell();

            this.body.move(dx, dy);

            this.checkHome();
            this.checkFood();
            this.pickSmell(); //remove if bugs
            this.updateColor();
        }

        public void placeSmell() {
            tiles.grid[this.x][this.y].setTrail(this.smell, this.pathToFood);
        }

        public void pickSmell() {
            if (this.pathToFood) {
                this.smell = Math.max(tiles.grid[this.x][this.y].trailH, this.smell);
            } else {
                this.smell = Math.max(tiles.grid[this.x][this.y].trailF, this.smell);
            }
            
        }

        public void checkHome() {
            if ((this.x == START_X) && (this.y == START_Y)) {
                this.smell = POTENCY;
                this.pathToFood = true;
            }
        }

        public void checkFood() {
            if ((this.x == END_X) && (this.y == END_Y)) {
                this.smell = POTENCY;
                this.pathToFood = false;
            }
        }

        public void updateColor() {
            int red = 255;
            int green = 255;
            int blue = 255;

            if (this.pathToFood) {
                green = 0;
                blue = 0;
            } else {
                blue = 0;
            }

            this.body.setFillColor(new Color(red,green,blue));
            this.body.setFilled(true);
        }
    }

    public void wander() {
        while (true) {
            this.pause(1000/this.FPS);

            for (int i = 0; i < this.tiles.grid.length; i++) {
                for (int j = 0; j < this.tiles.grid.length; j++) {
                    this.tiles.grid[i][j].decay();
                }
            }
            this.decayRate = ((this.decayRate + 1) % this.DECAY);

            for (int i = 0; i < this.ants.length; i++) {
                Ant ant = this.ants[i];
                ant.wander();
            }
        }
    }

    public void addAnts() {
        for (int i = 0; i < this.ants.length; i++) {
            this.ants[i] = new Ant(START_X, START_Y);
            this.add(this.ants[i].body);
        }
    }

    public void addGrid() {
        for (int i = 0; i < this.tiles.grid.length; i++) {
            for (int j = 0; j < this.tiles.grid.length; j++) {
                this.add(this.tiles.grid[i][j].obj);
                this.add(this.tiles.grid[i][j].backgroundObj);
            }
        }
    }

    //Items that are initialized before run is called
    public void init() {
        this.setTitle("AntColony");
        this.setSize(this.BG_SIZE, this.BG_SIZE);
        this.waitForClick();
        this.addGrid();
        this.addAnts();
        this.add(this.home.obj);
        this.add(this.food.obj);
    }

    public void run() {
        this.wander();
    }

    public static void main(String[] args) {
        new AntColony().start(args);
    }
}