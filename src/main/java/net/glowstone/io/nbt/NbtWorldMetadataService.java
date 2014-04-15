package net.glowstone.io.nbt;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.io.WorldMetadataService;
import net.glowstone.io.entity.EntityStoreLookupService;
import net.glowstone.util.nbt.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.*;
import java.util.*;


public class NbtWorldMetadataService implements WorldMetadataService {
    private final GlowWorld world;
    private final File dir;
    private final GlowServer server;

    private CompoundTag unknownTags;

    public NbtWorldMetadataService(GlowWorld world, File dir) {
        this.world = world;
        if (!dir.exists())
            dir.mkdirs();
        this.dir = dir;
        server = (GlowServer) Bukkit.getServer();
    }

    public WorldFinalValues readWorldData() throws IOException {
        CompoundTag level = new CompoundTag();

        // please fix this mess

        File levelFile = new File(dir, "level.dat");
        if (!levelFile.exists()) {
            try {
                levelFile.createNewFile();
            } catch (IOException e) {
                handleWorldException("level.dat", e);
            }
        } else {
            try {
                NBTInputStream in = new NBTInputStream(new FileInputStream(levelFile));
                CompoundTag levelTag = (CompoundTag) in.readCompound();
                in.close();
                if (levelTag != null) level = levelTag;
            } catch (EOFException e) {
            } catch (IOException e) {
                handleWorldException("level.dat", e);
            }
        }
        UUID uid = null;
        File uuidFile = new File(dir, "uid.dat");
        if (!uuidFile.exists()) {
            try {
                uuidFile.createNewFile();
            } catch (IOException e) {
                handleWorldException("uid.dat", e);
            }
        } else {
            DataInputStream str = null;
            try {
                str = new DataInputStream(new FileInputStream(uuidFile));
                uid = new UUID(str.readLong(), str.readLong());
            } catch (EOFException e) {
            } finally {
                if (str != null) {
                    str.close();
                }
            }
        }

        // TODO: ugggghhhhhh
        long seed = 0L;
        /*if (level.isByte("thundering")) {
            world.setThundering(level.getByte("thundering") == 1);
            level.remove("thundering");
        }
        if (checkKnownTag(level, unknown, "raining", ByteTag.class)) {
            world.setStorm(level.get("raining", ByteTag.class) == 1);
        }
        if (checkKnownTag(level, unknown, "thunderTime", IntTag.class)) {
            world.setThunderDuration(level.get("thunderTime", IntTag.class));
        }
        if (checkKnownTag(level, unknown, "rainTime", IntTag.class)) {
            world.setWeatherDuration(level.get("rainTime", IntTag.class));
        }
        if (checkKnownTag(level, unknown, "RandomSeed", LongTag.class)) {
            seed = level.get("RandomSeed", LongTag.class);
        }
        if (checkKnownTag(level, unknown, "Time", LongTag.class)) {
            world.setFullTime(level.get("Time", LongTag.class));
        }
        if (checkKnownTag(level, unknown, "DayTime", LongTag.class)) {
            world.setTime(level.get("DayTime", LongTag.class));
        }
        if (checkKnownTag(level, unknown, "SpawnX", IntTag.class) &&
                checkKnownTag(level, unknown, "SpawnY", IntTag.class) &&
                checkKnownTag(level, unknown, "SpawnZ", IntTag.class)) {
            int x = level.get("SpawnX", IntTag.class);
            int y = level.get("SpawnY", IntTag.class);
            int z = level.get("SpawnZ", IntTag.class);
            world.setSpawnLocation(x, y, z);
        }

        if (checkKnownTag(level, unknown, "GameRules", CompoundTag.class)) {
            CompoundTag gameRules = level.getTag("GameRules", CompoundTag.class);
            for (String key : gameRules.getValue().keySet()) {
                if (gameRules.is(key, StringTag.class)) {
                    world.setGameRuleValue(key, gameRules.get(key, StringTag.class));
                }
            }
        }

        unknownTags.addAll(unknown.values());*/

        if (uid == null) uid = UUID.randomUUID();
        return new WorldFinalValues(seed, uid);
    }

    private void handleWorldException(String file, IOException e) {
        server.unloadWorld(world, false);
        server.getLogger().severe("Unable to access " + file + " for world " + world.getName());
        e.printStackTrace();
    }

    public void writeWorldData() throws IOException {
        File uuidFile = new File(dir, "uid.dat");
        if (!uuidFile.exists()) {
            try {
                uuidFile.createNewFile();
            } catch (IOException e) {
                handleWorldException("uid.dat", e);
            }
        } else {
            UUID uuid = world.getUID();
            DataOutputStream str = new DataOutputStream(new FileOutputStream(uuidFile));
            str.writeLong(uuid.getLeastSignificantBits());
            str.writeLong(uuid.getMostSignificantBits());
            str.close();
        }

        CompoundTag out = unknownTags;
        unknownTags = new CompoundTag();

        // Normal level data
        out.putLong("RandomSeed", world.getSeed());
        out.putLong("Time", world.getFullTime());
        out.putLong("DayTime", world.getTime());
        out.putBool("thundering", world.isThundering());
        out.putBool("raining", world.hasStorm());
        out.putInt("thunderTime", world.getThunderDuration());
        out.putInt("rainTime", world.getWeatherDuration());

        Location loc = world.getSpawnLocation();
        out.putInt("SpawnX", loc.getBlockX());
        out.putInt("SpawnY", loc.getBlockY());
        out.putInt("SpawnZ", loc.getBlockZ());
        // Format-specific
        out.putString("LevelName", world.getName());
        out.putLong("LastPlayed", Calendar.getInstance().getTimeInMillis());
        out.putInt("version", 19132);

        // Game rules
        CompoundTag gameRules = new CompoundTag();
        String[] gameRuleKeys = world.getGameRules();
        for (String key : gameRuleKeys) {
            gameRules.putString(key, world.getGameRuleValue(key));
        }
        out.putCompound("GameRules", gameRules);

        // Not sure how to calculate this, so ignoring for now
        out.putLong("SizeOnDisk", 0);

        try {
            NBTOutputStream nbtOut = new NBTOutputStream(new FileOutputStream(new File(dir, "level.dat")));
            nbtOut.writeTag(out);
            nbtOut.close();
        } catch (IOException e) {
            handleWorldException("level.dat", e);
        }
    }

    public void readPlayerData(GlowPlayer player) {
        CompoundTag playerTag = null;
        // Map<PlayerData, Object> ret = new HashMap<PlayerData, Object>();

        File playerDir = new File(dir, "players");
        if (!playerDir.exists())
            playerDir.mkdirs();

        File playerFile = new File(playerDir, player.getName() + ".dat");
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                NBTInputStream in = new NBTInputStream(new FileInputStream(playerFile));
                playerTag = (CompoundTag) in.readCompound();
                in.close();
            } catch (EOFException e) {
            } catch (IOException e) {
                player.kickPlayer("Failed to read " + player.getName() + ".dat!");
                server.getLogger().severe("Failed to read player.dat for player " + player.getName() + " in world " + world.getName() + "!");
                e.printStackTrace();
            }
        }

        if (playerTag == null) playerTag = new CompoundTag();
        EntityStoreLookupService.find(GlowPlayer.class).load(player, playerTag);
    }

    public void writePlayerData(GlowPlayer player) {
        File playerDir = new File(dir, "players");
        if (!playerDir.exists())
            playerDir.mkdirs();

        File playerFile = new File(playerDir, player.getName() + ".dat");
        if (!playerFile.exists()) try {
            playerFile.createNewFile();
        } catch (IOException e) {
            player.getSession().disconnect("Failed to access player.dat");
            server.getLogger().severe("Failed to access player.dat for player " + player.getName() + " in world " + world.getName() + "!");
        }

        CompoundTag tag = new CompoundTag();
        EntityStoreLookupService.find(GlowPlayer.class).save(player, tag);
        try {
            NBTOutputStream outStream = new NBTOutputStream(new FileOutputStream(playerFile));
            outStream.writeTag(tag);
            outStream.close();
        } catch (IOException e) {
            player.getSession().disconnect("Failed to write player.dat");
            server.getLogger().severe("Failed to write player.dat for player " + player.getName() + " in world " + world.getName() + "!");
        }
    }
}
