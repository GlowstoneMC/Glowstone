package net.glowstone.shiny;

import com.google.common.base.Throwables;

import java.io.IOException;

import static com.google.common.base.Throwables.*;

public class ShinySponge {

    private ShinyGame game;

    public void load() {
        try {
            System.out.println("Loading ShinySponge...");

            System.out.println("Loading plugins...");

            // TODO: Guice

            //game.getPluginManager()

        } catch (Exception e) {
            throw propagate(e);
        }
    }
}
