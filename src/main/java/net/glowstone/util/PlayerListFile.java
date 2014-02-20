package net.glowstone.util;

import net.glowstone.GlowServer;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * Utility class for storing lists of player names.
 */
public final class PlayerListFile {

    /**
     * The list as we currently know it.
     */
    private final ArrayList<String> list = new ArrayList<String>();

    /**
     * The file the list is associated with.
     */
    private final File file;

    /**
     * Initialize the player list from the given file.
     * @param file The file to use for this list.
     */
    public PlayerListFile(File file) {
        this.file = file;
    }

    /**
     * Reloads from the file.
     */
    public void load() {
        list.clear();
        try {
            Scanner input = new Scanner(file);
            while (input.hasNextLine()) {
                String line = input.nextLine().trim().toLowerCase();
                if (line.length() > 0 && !list.contains(line)) {
                    list.add(line);
                }
            }
            Collections.sort(list);
        } catch (FileNotFoundException ex) {
            // ignore
        }
        save();
    }

    /**
     * Saves to the file.
     */
    private void save() {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(file));
            for (String str : list) {
                out.println(str);
            }
            out.flush();
            out.close();
        } catch (IOException ex) {
            GlowServer.logger.log(Level.SEVERE, "Error saving to " + file, ex);
        }
    }

    /**
     * Add a player to the list.
     */
    public void add(String player) {
        if (!contains(player)) list.add(player.trim().toLowerCase());
        Collections.sort(list);
        save();
    }

    /**
     * Remove a player from the list.
     */
    public void remove(String player) {
        list.remove(player.trim());
        save();
    }

    /**
     * Check if a player is in the list.
     */
    public boolean contains(String player) {
        for (String str : list) {
            if (str.equalsIgnoreCase(player.trim()))
                return true;
        }
        return false;
    }

    public List<String> getContents() {
        return list;
    }

}
