package src;

/**
 * Simple point class for direction (each direction can store a probability score of being chosen).
 */
public class Point {
    public int x;
    public int y;
    public int chance;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }
}