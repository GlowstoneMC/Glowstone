package net.glowstone.io.nbt;

import net.glowstone.GlowOfflinePlayer;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.io.PlayerDataService;
import net.glowstone.io.entity.EntityStoreLookupService;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.NBTInputStream;
import net.glowstone.util.nbt.NBTOutputStream;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Standard NBT-based player data storage
 */
public class NbtPlayerDataService implements PlayerDataService {

    private final GlowServer server;
    private final File playerDir;

    public NbtPlayerDataService(GlowServer server, File playerDir) {
        this.server = server;
        this.playerDir = playerDir;
    }

    private File getPlayerFile(UUID uuid) {
        if (!playerDir.isDirectory() && !playerDir.mkdirs()) {
            server.getLogger().warning("Failed to create directory: " + playerDir);
        }
        return new File(playerDir, uuid + ".dat");
    }

    private static Location readLocation(CompoundTag tag) {
        // todo: copy code from or move this method to EntityStore
        return null;
    }

    private void readDataImpl(GlowPlayer player, CompoundTag playerTag) {
        // todo: move GlowPlayer entity stuff to here
        EntityStoreLookupService.find(GlowPlayer.class).load(player, playerTag);
    }

    private void readOfflineDataImpl(GlowOfflinePlayer player, File file) {
        CompoundTag playerTag;
        try (NBTInputStream in = new NBTInputStream(new FileInputStream(file))) {
            playerTag = in.readCompound();
        } catch (IOException e) {
            server.getLogger().log(Level.WARNING, "Failed to read OfflinePlayer from " + file, e);
            return;
        }

        // todo: the stuff
        player.getName();
    }

    public void readOfflineData(GlowOfflinePlayer player) {
        if (player.getUniqueId() == null) {
            // todo: perform local name -> uuid lookup
            return;
        }

        File file = getPlayerFile(player.getUniqueId());
        if (file.exists()) {
            readOfflineDataImpl(player, file);
        }
    }

    public List<OfflinePlayer> getOfflinePlayers() {
        // list files in directory
        File[] files = playerDir.listFiles();
        if (files == null) {
            return Arrays.asList();
        }

        List<OfflinePlayer> result = new ArrayList<>(files.length);
        for (File file : files) {
            // first, make sure it looks like a player file
            String name = file.getName();
            if (name.length() != 40 || !name.endsWith(".dat")) {
                continue;
            }

            // get the UUID
            UUID uuid;
            try {
                uuid = UUID.fromString(name.substring(0, 36));
            } catch (IllegalArgumentException e) {
                continue;
            }

            // start reading
            GlowOfflinePlayer player = new GlowOfflinePlayer(server, null, uuid);
            readOfflineDataImpl(player, file);
            result.add(player);
        }

        return result;
    }

    public PlayerReader beginReadingData(UUID uuid) {
        return new NbtPlayerReader(getPlayerFile(uuid));
    }

    public void readData(GlowPlayer player) {
        File playerFile = getPlayerFile(player.getUniqueId());
        CompoundTag playerTag = new CompoundTag();
        if (playerFile.exists()) {
            try (NBTInputStream in = new NBTInputStream(new FileInputStream(playerFile))) {
                playerTag = in.readCompound();
            } catch (IOException e) {
                player.kickPlayer("Failed to read player data!");
                server.getLogger().log(Level.SEVERE, "Failed to read data for " + player.getName() + ": " + playerFile, e);
            }
        }
        readDataImpl(player, playerTag);
    }

    public void writeData(GlowPlayer player) {
        File playerFile = getPlayerFile(player.getUniqueId());
        CompoundTag tag = new CompoundTag();
        EntityStoreLookupService.find(GlowPlayer.class).save(player, tag);
        try (NBTOutputStream out = new NBTOutputStream(new FileOutputStream(playerFile))) {
            out.writeTag(tag);
        } catch (IOException e) {
            player.getSession().disconnect("Failed to save player data!");
            server.getLogger().log(Level.SEVERE, "Failed to write data for " + player.getName() + ": " + playerFile, e);
        }
    }

    private class NbtPlayerReader implements PlayerReader {
        private CompoundTag tag = new CompoundTag();
        private boolean hasPlayed = false;
        private Location location = null;

        public NbtPlayerReader(File playerFile) {
            if (playerFile.exists()) {
                try (NBTInputStream in = new NBTInputStream(new FileInputStream(playerFile))) {
                    tag = in.readCompound();

                    hasPlayed = true;
                    location = readLocation(tag);
                } catch (IOException e) {
                    server.getLogger().log(Level.SEVERE, "Failed to read data for player: " + playerFile, e);
                }
            }

            if (location == null) {
                location = server.getWorlds().get(0).getSpawnLocation();
            }
        }

        public boolean hasPlayedBefore() {
            return hasPlayed;
        }

        public Location getLocation() {
            return location;
        }

        public void readData(GlowPlayer player) {
            readDataImpl(player, tag);
        }
    }
}
