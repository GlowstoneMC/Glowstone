package net.glowstone.io.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;
import java.util.logging.Level;
import net.glowstone.GlowWorld;
import net.glowstone.GlowWorldBorder;
import net.glowstone.ServerProvider;
import net.glowstone.io.WorldMetadataService;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.NbtInputStream;
import net.glowstone.util.nbt.NbtOutputStream;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.WorldType;

public class NbtWorldMetadataService implements WorldMetadataService {

    private final GlowWorld world;
    private final File dir;
    private final Server server;

    private CompoundTag unknownTags;

    /**
     * Creates the instance for the given world's metadata.
     *
     * @param world the world
     * @param dir the world's metadata folder, containing uid.dat and level.dat if the world has
     *         been previously saved; if this folder doesn't exist, it is created
     */
    public NbtWorldMetadataService(GlowWorld world, File dir) {
        this.world = world;
        this.dir = dir;
        server = ServerProvider.getServer();

        if (!dir.isDirectory() && !dir.mkdirs()) {
            server.getLogger().warning("Failed to create directory: " + dir);
        }
    }

    @Override
    public WorldFinalValues readWorldData() {
        // determine UUID of world
        UUID uid = null;
        File uuidFile = new File(dir, "uid.dat");
        if (uuidFile.exists()) {
            try (DataInputStream in = new DataInputStream(new FileInputStream(uuidFile))) {
                uid = new UUID(in.readLong(), in.readLong());
            } catch (IOException e) {
                handleWorldException("uid.dat", e);
            }
        }
        if (uid == null) {
            uid = UUID.randomUUID();
        }

        // read in world information
        CompoundTag level = new CompoundTag();
        File levelFile = new File(dir, "level.dat");
        if (levelFile.exists()) {
            try (NbtInputStream in = new NbtInputStream(new FileInputStream(levelFile))) {
                level = in.readCompound();
                CompoundTag data = level.tryGetCompound("Data");
                if (data != null) {
                    level = data;
                } else {
                    server.getLogger().warning(
                        "Loading world \"" + world.getName() + "\": reading from root, not Data");
                }
            } catch (IOException e) {
                handleWorldException("level.dat", e);
            }
        }

        // seed
        final long[] seed = {0L};
        if (level.readLong(x -> seed[0] = x, "RandomSeed")) {
            level.remove("RandomSeed");
        }

        // time of day and weather status
        if (level.readBoolean(world::setThundering, "thundering")) {
            level.remove("thundering");
        }
        if (level.readBoolean(world::setStorm, "raining")) {
            level.remove("raining");
        }
        if (level.readInt(world::setThunderDuration, "thunderTime")) {
            level.remove("thunderTime");
        }
        if (level.readInt(world::setWeatherDuration, "rainTime")) {
            level.remove("rainTime");
        }
        if (level.readLong(world::setFullTime, "Time")) {
            level.remove("Time");
        }
        if (level.readLong(world::setTime, "DatTime")) {
            level.remove("DayTime");
        }
        if (level.readString(
            generatorName -> world.setWorldType(WorldType.getByName(generatorName)),
                "generatorName")) {
            level.remove("generatorName");
        }

        // spawn position
        if (level.isInt("SpawnX") && level.isInt("SpawnY") && level.isInt("SpawnZ")) {
            world.setSpawnLocation(level.getInt("SpawnX"), level.getInt("SpawnY"),
                level.getInt("SpawnZ"), false);
            level.remove("SpawnX");
            level.remove("SpawnY");
            level.remove("SpawnZ");
        }

        // game rules
        if (level.readCompound(gameRules ->
                gameRules.getValue().keySet().stream().filter(gameRules::isString)
                    .forEach(key -> world.setGameRuleValue(key, gameRules.getString(key))),
                "GameRules")) {
            level.remove("GameRules");
        }

        // world border
        Location borderCenter = new Location(world, 0, 0, 0);
        if (level.readDouble(borderCenter::setX, "BorderCenterX")) {
            level.remove("BorderCenterX");
        }
        if (level.readDouble(borderCenter::setZ, "BorderCenterZ")) {
            level.remove("BorderCenterZ");
        }
        GlowWorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setCenter(borderCenter);
        if (level.readDouble(worldBorder::setSize, "BorderSize")) {
            level.remove("BorderSize");
        }
        if (level.isDouble("BorderSizeLerpTarget") && level.isLong("BorderSizeLerpTime")) {
            worldBorder.setSize(level.getDouble("BorderSizeLerpTarget"),
                level.getLong("BorderSizeLerpTime"));
            level.remove("BorderSizeLerpTarget");
            level.remove("BorderSizeLerpTime");
        }
        if (level.readDouble(worldBorder::setDamageBuffer, "BorderSafeZone")) {
            level.remove("BorderSafeZone");
        }
        if (level.readDouble(time -> worldBorder.setWarningTime((int) time),
                "BorderWarningTime")) {
            level.remove("BorderWarningTime");
        }
        if (level.readDouble(distance -> worldBorder.setWarningDistance((int) distance),
                "BorderWarningBlocks")) {
            level.remove("BorderWarningBlocks");
        }
        if (level.readDouble(worldBorder::setDamageAmount, "BorderDamagePerBlock")) {
            level.remove("BorderDamagePerBlock");
        }

        // strip single-player Player tag if it exists
        if (level.isCompound("Player")) {
            server.getLogger()
                .warning("World \"" + world.getName() + "\": removing single-player Player tag");
            level.remove("Player");
        }

        // save unknown tags for later
        unknownTags = level;

        return new WorldFinalValues(seed[0], uid);
    }

    private void handleWorldException(String file, IOException e) {
        server.unloadWorld(world, false);
        server.getLogger()
            .log(Level.SEVERE, "Unable to access " + file + " for world " + world.getName(), e);
    }

    @Override
    public void writeWorldData() throws IOException {
        File uuidFile = new File(dir, "uid.dat");
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(uuidFile))) {
            UUID uuid = world.getUID();
            out.writeLong(uuid.getMostSignificantBits());
            out.writeLong(uuid.getLeastSignificantBits());
        }

        // start with unknown tags from reading
        CompoundTag out = new CompoundTag();
        if (unknownTags != null) {
            out.getValue().putAll(unknownTags.getValue());
        }

        // Seed and core information
        out.putString("LevelName", world.getName());
        out.putInt("version", 19133);
        out.putLong("LastPlayed", Calendar.getInstance().getTimeInMillis());
        out.putLong("RandomSeed", world.getSeed());

        // Normal level data
        out.putLong("Time", world.getFullTime());
        out.putLong("DayTime", world.getTime());
        out.putBool("thundering", world.isThundering());
        out.putBool("raining", world.hasStorm());
        out.putInt("thunderTime", world.getThunderDuration());
        out.putInt("rainTime", world.getWeatherDuration());
        out.putString("generatorName", world.getWorldType().getName().toLowerCase());

        // Spawn location
        Location loc = world.getSpawnLocation();
        out.putInt("SpawnX", loc.getBlockX());
        out.putInt("SpawnY", loc.getBlockY());
        out.putInt("SpawnZ", loc.getBlockZ());

        // World border
        out.putDouble("BorderCenterX", world.getWorldBorder().getCenter().getX());
        out.putDouble("BorderCenterZ", world.getWorldBorder().getCenter().getZ());
        out.putDouble("BorderSize", world.getWorldBorder().getSize());
        out.putDouble("BorderSizeLerpTarget", world.getWorldBorder().getSizeLerpTarget());
        out.putLong("BorderSizeLerpTime", world.getWorldBorder().getSizeLerpTime());
        out.putDouble("BorderSafeZone", world.getWorldBorder().getDamageBuffer());
        out.putDouble("BorderWarningTime", world.getWorldBorder().getWarningTime());
        out.putDouble("BorderWarningBlocks", world.getWorldBorder().getWarningDistance());
        out.putDouble("BorderDamagePerBlock", world.getWorldBorder().getDamageAmount());

        // Game rules
        CompoundTag gameRules = new CompoundTag();
        String[] gameRuleKeys = world.getGameRules();
        for (String key : gameRuleKeys) {
            gameRules.putString(key, world.getGameRuleValue(key));
        }
        out.putCompound("GameRules", gameRules);

        // Not sure how to calculate this, so ignoring for now
        out.putLong("SizeOnDisk", 0);

        CompoundTag root = new CompoundTag();
        root.putCompound("Data", out);
        try (NbtOutputStream nbtOut = new NbtOutputStream(
            new FileOutputStream(new File(dir, "level.dat")))) {
            nbtOut.writeTag(root);
        } catch (IOException e) {
            handleWorldException("level.dat", e);
        }
    }
}
