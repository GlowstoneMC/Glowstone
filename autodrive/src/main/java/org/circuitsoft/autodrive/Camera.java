package org.circuitsoft.autodrive;

import java.util.ArrayList;
import java.util.Random;

public class Camera {

    private final double r;
    private final double g;
    private final double b;
    private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    private Random rand = new Random();

    public Camera(double r, double g, double b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public ArrayList<Obstacle> checkForPoints() {
        for (int i = 0; i < 500; i++) {
            double cameraRed = rand.nextDouble() * 255;
            double cameraGreen = rand.nextDouble() * 255;
            double cameraBlue = rand.nextDouble() * 255;
            if (cameraRed > r && cameraGreen > g && cameraBlue > b) {
                obstacles.add(new Obstacle(rand.nextDouble() * 10, rand.nextDouble() * 10));
            }
        }
        return obstacles;
    }
}
