package net.glowstone.io.nbt;

import net.glowstone.GlowOfflinePlayer;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.io.PlayerDataService;
import net.glowstone.io.entity.EntityStorage;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.NBTInputStream;
import net.glowstone.util.nbt.NBTOutputStream;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Standard NBT-based player data storage.
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

    private void readDataImpl(GlowPlayer player, CompoundTag playerTag) {
        EntityStorage.load(player, playerTag);
    }

    @Override
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

            // creating the OfflinePlayer will read the data
            result.add(new GlowOfflinePlayer(server, uuid));
        }

        return result;
    }

    @Override
    public UUID lookupUUID(String name) {
        // todo: caching or something
        for (OfflinePlayer player : getOfflinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player.getUniqueId();
            }
        }
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public PlayerReader beginReadingData(UUID uuid) {
        return new NbtPlayerReader(getPlayerFile(uuid));
    }

    @Override
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

    @Override
    public void writeData(GlowPlayer player) {
        File playerFile = getPlayerFile(player.getUniqueId());
        CompoundTag tag = new CompoundTag();
        EntityStorage.save(player, tag);
        try (NBTOutputStream out = new NBTOutputStream(new FileOutputStream(playerFile))) {
            out.writeTag(tag);
        } catch (IOException e) {
            player.kickPlayer("Failed to save player data!");
            server.getLogger().log(Level.SEVERE, "Failed to write data for " + player.getName() + ": " + playerFile, e);
        }
    }

    private class NbtPlayerReader implements PlayerReader {
        private CompoundTag tag = new CompoundTag();
        private boolean hasPlayed = false;

        public NbtPlayerReader(File playerFile) {
            if (playerFile.exists()) {
                try (NBTInputStream in = new NBTInputStream(new FileInputStream(playerFile))) {
                    tag = in.readCompound();
                    hasPlayed = true;
                } catch (IOException e) {
                    server.getLogger().log(Level.SEVERE, "Failed to read data for player: " + playerFile, e);
                }
            }
        }

        private void checkOpen() {
            if (tag == null) {
                throw new IllegalStateException("cannot access fields after close");
            }
        }

        @Override
        public boolean hasPlayedBefore() {
            return hasPlayed;
        }

        @Override
        public Location getLocation() {
            checkOpen();
            World world = NbtSerialization.readWorld(server, tag);
            if (world != null) {
                return NbtSerialization.listTagsToLocation(world, tag);
            }
            return null;
        }

        @Override
        public Location getBedSpawnLocation() {
            checkOpen();
            // check that all fields are present
            if (!tag.isString("SpawnWorld") || !tag.isInt("SpawnX") || !tag.isInt("SpawnY") || !tag.isInt("SpawnZ")) {
                return null;
            }
            // look up world
            World world = server.getWorld(tag.getString("SpawnWorld"));
            if (world == null) {
                return null;
            }
            // return location
            return new Location(world, tag.getInt("SpawnX"), tag.getInt("SpawnY"), tag.getInt("SpawnZ"));
        }

        @Override
        public long getFirstPlayed() {
            checkOpen();
            if (tag.isCompound("bukkit")) {
                CompoundTag bukkit = tag.getCompound("bukkit");
                if (bukkit.isLong("firstPlayed")) {
                    return bukkit.getLong("firstPlayed");
                }
            }
            return 0;
        }

        @Override
        public long getLastPlayed() {
            checkOpen();
            if (tag.isCompound("bukkit")) {
                CompoundTag bukkit = tag.getCompound("bukkit");
                if (bukkit.isLong("lastPlayed")) {
                    return bukkit.getLong("lastPlayed");
                }
            }
            return 0;
        }

        @Override
        public String getLastKnownName() {
            checkOpen();
            if (tag.isCompound("bukkit")) {
                CompoundTag bukkit = tag.getCompound("bukkit");
                if (bukkit.isString("lastKnownName")) {
                    return bukkit.getString("lastKnownName");
                }
            }
            return null;
        }

        @Override
        public void readData(GlowPlayer player) {
            checkOpen();
            readDataImpl(player, tag);
        }

        @Override
        public void close() {
            tag = null;
        }
    }
}
