package net.glowstone.io.nbt;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.io.WorldMetadataService;
import net.glowstone.io.entity.EntityStoreLookupService;
import net.glowstone.util.nbt.ByteTag;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.IntTag;
import net.glowstone.util.nbt.LongTag;
import net.glowstone.util.nbt.NBTInputStream;
import net.glowstone.util.nbt.NBTOutputStream;
import net.glowstone.util.nbt.StringTag;
import net.glowstone.util.nbt.Tag;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class NbtWorldMetadataService implements WorldMetadataService {
    private final GlowWorld world;
    private final File dir;
    private final GlowServer server;
    private final Map<String, Tag> unknownTags = new HashMap<String, Tag>();

    public NbtWorldMetadataService(GlowWorld world, File dir) {
        this.world = world;
        if (!dir.exists())
            dir.mkdirs();
        this.dir = dir;
        server = (GlowServer) Bukkit.getServer();
    }

    public WorldFinalValues readWorldData() throws IOException {
        Map<String, Tag> level = new HashMap<String, Tag>();

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
                if (levelTag != null) level.putAll(levelTag.getValue());
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
        long seed = 0L;
        if (level.containsKey("thundering")) {
            ByteTag thunderTag = (ByteTag) level.remove("thundering");
            world.setThundering(thunderTag.getValue() == 1);
        }
        if (level.containsKey("raining")) {
            ByteTag rainTag = (ByteTag) level.remove("raining");
            world.setStorm(rainTag.getValue() == 1);

        }
        if (level.containsKey("thunderTime")) {
            IntTag thunderTimeTag = (IntTag) level.remove("thunderTime");
            world.setThunderDuration(thunderTimeTag.getValue());
        }
        if (level.containsKey("rainTime")) {
            IntTag rainTimeTag = (IntTag) level.remove("rainTime");
            world.setWeatherDuration(rainTimeTag.getValue());
        }
        if (level.containsKey("RandomSeed")) {
            LongTag seedTag = (LongTag) level.remove("RandomSeed");
            seed = seedTag.getValue();
        }
        if (level.containsKey("Time")) {
            LongTag timeTag = (LongTag) level.remove("Time");
            world.setTime(timeTag.getValue());
        }
        if (level.containsKey("SpawnX") && level.containsKey("SpawnY") && level.containsKey("SpawnZ")) {
            IntTag spawnXTag = (IntTag) level.remove("SpawnX");
            IntTag spawnYTag = (IntTag) level.remove("SpawnY");
            IntTag spawnZTag = (IntTag) level.remove("SpawnZ");
            world.setSpawnLocation(spawnXTag.getValue(), spawnYTag.getValue(), spawnZTag.getValue());
        }
        unknownTags.putAll(level);
        if (uid == null) uid= UUID.randomUUID();
        return new WorldFinalValues(seed, uid);
    }

    private void handleWorldException(String file, IOException e) {
        server.unloadWorld(world, false);
        server.getLogger().severe("Unable to access " + file + " for world " + world.getName());
        e.printStackTrace();
    }

    public void writeWorldData() throws IOException {
        Map<String, Tag> out = new HashMap<String, Tag>();
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
        out.putAll(unknownTags);
        unknownTags.clear();
        // Normal level data
        out.put("thundering", new ByteTag("thundering", (byte) (world.isThundering() ? 1 : 0)));
        out.put("RandomSeed", new LongTag("RandomSeed", world.getSeed()));
        out.put("Time", new LongTag("Time", world.getTime()));
        out.put("raining", new ByteTag("raining", (byte) (world.hasStorm() ? 1 : 0)));
        out.put("thunderTime", new IntTag("thunderTime", world.getThunderDuration()));
        out.put("rainTime", new IntTag("rainTime", world.getWeatherDuration()));
        Location loc = world.getSpawnLocation();
        out.put("SpawnX", new IntTag("SpawnX", loc.getBlockX()));
        out.put("SpawnY", new IntTag("SpawnY", loc.getBlockY()));
        out.put("SpawnZ", new IntTag("SpawnZ", loc.getBlockZ()));
        // Format-specific
        out.put("LevelName", new StringTag("LevelName", world.getName()));
        out.put("LastPlayed", new LongTag("LastPlayed", Calendar.getInstance().getTimeInMillis()));
        out.put("version", new IntTag("version", 19132));

        if (!out.containsKey("SizeOnDisk"))
            out.put("SizeOnDisk", new LongTag("SizeOnDisk", 0)); // Not sure how to calculate this, so ignoring for now
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

        File playerDir = new File(world.getName(), "players");
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

        File playerDir = new File(world.getName(), "players");
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
            player.getSession().disconnect("Failed to write player.dat", true);
            server.getLogger().severe("Failed to write player.dat for player " + player.getName() + " in world " + world.getName() + "!");
        }
    }
}
