package org.circuitsoft.autodrive;

import java.util.ArrayList;

public class Car {

    private static boolean running = true;
    private static double distance = 0.0;

    public static void main(String... args) {
        Camera cam = new Camera(230, 230, 0);
        while (running) {
            ArrayList<Obstacle> obstacles = cam.checkForPoints();
            for (Obstacle ob : obstacles) {
                System.out.println("Obstacle at: " + ob.relX + ", " + ob.relY);
            }
            distance++;
            System.out.println(distance);
            if (distance > 500) {
                running = false;
            }
        }
    }
}
