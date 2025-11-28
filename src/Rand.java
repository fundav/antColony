package src;

import java.util.ArrayList;
import acm.util.RandomGenerator;

/**
 * My own random class for this project.
 */
public class Rand {
    private static RandomGenerator rgen = RandomGenerator.getInstance();
    private static final int RANDOM = 15; //The greater this value the more likely chance the highest probability score of the set is chosen

    private static int[] getSumNMax(ArrayList<Point> points) {
        int total = 0;
        int max = -1;
        for (int i = 0; i < points.size(); i++) {
            total += points.get(i).chance;
            if (points.get(i).chance > max) {
                max = points.get(i).chance;
            }
        }
        int[] val = {total, max};
        return val;
    }

    //Randomly gets a point factoring each point's probability score
    private static Point norm(ArrayList<Point> points) {
        int sum = Rand.getSumNMax(points)[0];

        int pick = rgen.nextInt(0, sum);

        int start = 0;

        for (int i = 0; i < points.size(); i++) {
            start += points.get(i).chance;
            if (pick < start) {
                return points.get(i);
            }
        }
        return new Point(0, 0);
    }

    //Removes the point with the highest probability score
    private static ArrayList<Point> removeMax(ArrayList<Point> points) {
        ArrayList<Point> newPoints = new ArrayList<Point>();
        int max = Rand.getSumNMax(points)[1];
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).chance != max) {
                newPoints.add(points.get(i));
            }
        }
        if (newPoints.size() != 0) {
            return newPoints;
        } else {
            return points;
        }
    }

    //Chooses the point with the max probability score everytime until the off chance its 0 and it then removes that particular point
    public static Point choose(ArrayList<Point> points) {
        int pick = rgen.nextInt(0, RANDOM);
        if (pick != 0) {
            ArrayList<Point> newPoints = new ArrayList<Point>();
            int max = Rand.getSumNMax(points)[1];
            for (int i = 0; i < points.size(); i++) {
                if (points.get(i).chance == max) {
                    newPoints.add(points.get(i));
                }
            }
            if (newPoints.size() == 1) {
                return newPoints.get(0);
            } else {
                return Rand.norm(newPoints);
            }
        } else {
            ArrayList<Point> revedPoints = Rand.removeMax(points);
            return Rand.choose(revedPoints);
        }
    }
}