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
    private final List<Tag> unknownTags = new ArrayList<Tag>();

    public NbtWorldMetadataService(GlowWorld world, File dir) {
        this.world = world;
        if (!dir.exists())
            dir.mkdirs();
        this.dir = dir;
        server = (GlowServer) Bukkit.getServer();
    }

    public WorldFinalValues readWorldData() throws IOException {
        CompoundTag level = new CompoundTag("", Collections.<String, Tag>emptyMap());

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
                CompoundTag levelTag = (CompoundTag) in.readTag();
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

        Map<String, Tag> unknown = new HashMap<String, Tag>(level.getValue());

        long seed = 0L;
        if (checkKnownTag(level, unknown, "thundering", ByteTag.class)) {
            world.setThundering(level.get("thundering", ByteTag.class) == 1);
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
            world.setTime(level.get("Time", LongTag.class));
        }
        if (checkKnownTag(level, unknown, "SpawnX", IntTag.class) &&
                checkKnownTag(level, unknown, "SpawnY", IntTag.class) &&
                checkKnownTag(level, unknown, "SpawnZ", IntTag.class)) {
            int x = level.get("SpawnX", IntTag.class);
            int y = level.get("SpawnY", IntTag.class);
            int z = level.get("SpawnZ", IntTag.class);
            world.setSpawnLocation(x, y, z);
        }

        unknownTags.addAll(unknown.values());

        if (uid == null) uid = UUID.randomUUID();
        return new WorldFinalValues(seed, uid);
    }

    private boolean checkKnownTag(CompoundTag level, Map<String, Tag> unknown, String key, Class<? extends Tag> clazz) {
        if (level.is(key, clazz)) {
            unknown.remove(key);
            return true;
        }
        return false;
    }

    private void handleWorldException(String file, IOException e) {
        server.unloadWorld(world, false);
        server.getLogger().severe("Unable to access " + file + " for world " + world.getName());
        e.printStackTrace();
    }

    public void writeWorldData() throws IOException {
        List<Tag> out = new LinkedList<Tag>();
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
        out.addAll(unknownTags);
        unknownTags.clear();

        // Normal level data
        out.add(new ByteTag("thundering", (byte) (world.isThundering() ? 1 : 0)));
        out.add(new LongTag("RandomSeed", world.getSeed()));
        out.add(new LongTag("Time", world.getTime()));
        out.add(new ByteTag("raining", (byte) (world.hasStorm() ? 1 : 0)));
        out.add(new IntTag("thunderTime", world.getThunderDuration()));
        out.add(new IntTag("rainTime", world.getWeatherDuration()));
        Location loc = world.getSpawnLocation();
        out.add(new IntTag("SpawnX", loc.getBlockX()));
        out.add(new IntTag("SpawnY", loc.getBlockY()));
        out.add(new IntTag("SpawnZ", loc.getBlockZ()));
        // Format-specific
        out.add(new StringTag("LevelName", world.getName()));
        out.add(new LongTag("LastPlayed", Calendar.getInstance().getTimeInMillis()));
        out.add(new IntTag("version", 19132));

        // Not sure how to calculate this, so ignoring for now
        out.add(new LongTag("SizeOnDisk", 0));

        try {
            NBTOutputStream nbtOut = new NBTOutputStream(new FileOutputStream(new File(dir, "level.dat")));
            nbtOut.writeTag(new CompoundTag("Data", out));
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
                playerTag = (CompoundTag) in.readTag();
                in.close();
            } catch (EOFException e) {
            } catch (IOException e) {
                player.kickPlayer("Failed to read " + player.getName() + ".dat!");
                server.getLogger().severe("Failed to read player.dat for player " + player.getName() + " in world " + world.getName() + "!");
                e.printStackTrace();
            }
        }
        
        if (playerTag == null) playerTag = new CompoundTag("", new HashMap<String, Tag>());
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

        Map<String, Tag> out = EntityStoreLookupService.find(GlowPlayer.class).save(player);
        try {
            NBTOutputStream outStream = new NBTOutputStream(new FileOutputStream(playerFile));
            outStream.writeTag(new CompoundTag("", out));
            outStream.close();
        } catch (IOException e) {
            player.getSession().disconnect("Failed to write player.dat");
            server.getLogger().severe("Failed to write player.dat for player " + player.getName() + " in world " + world.getName() + "!");
        }
    }
}
