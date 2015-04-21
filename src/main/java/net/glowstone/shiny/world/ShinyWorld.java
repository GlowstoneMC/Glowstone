package net.glowstone.shiny.world;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.ScheduledBlockUpdate;
import org.spongepowered.api.block.tile.TileEntity;
import org.spongepowered.api.data.*;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.service.permission.context.Context;
import org.spongepowered.api.service.persistence.InvalidDataException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.*;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.api.world.storage.WorldStorage;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class ShinyWorld implements World {
    private org.bukkit.World handle;

    public ShinyWorld(org.bukkit.World handle) {
        this.handle = handle;
    }

    public org.bukkit.World getHandle() {
        return this.handle;
    }

    @Override
    public Difficulty getDifficulty() {
        switch (this.handle.getDifficulty()) {
            case EASY: return Difficulties.EASY;
            case HARD: return Difficulties.HARD;
            case NORMAL: return Difficulties.NORMAL;
            case PEACEFUL: return Difficulties.PEACEFUL;
            default: return Difficulties.NORMAL;
        }
    }

    @Override
    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isLoaded() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<Chunk> getChunk(Vector3i position) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<Chunk> loadChunk(Vector3i position, boolean shouldGenerate) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean unloadChunk(Chunk chunk) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<Chunk> getLoadedChunks() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<Entity> getEntity(UUID uuid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WorldBorder getWorldBorder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<String> getGameRule(String gameRule) {
        return Optional.of(this.handle.getGameRuleValue(gameRule));
    }

    @Override
    public Map<String, String> getGameRules() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Dimension getDimension() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WorldGenerator getWorldGenerator() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setWorldGenerator(WorldGenerator generator) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean doesKeepSpawnLoaded() {
        return this.handle.getKeepSpawnInMemory();
    }

    @Override
    public void setKeepSpawnLoaded(boolean keepLoaded) {
        this.handle.setKeepSpawnInMemory(keepLoaded);
    }

    @Override
    public WorldStorage getWorldStorage() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Scoreboard getScoreboard() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WorldCreationSettings getCreationSettings() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WorldProperties getProperties() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Location getSpawnLocation() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getHeight() {
        return this.handle.getMaxHeight();
    }

    @Override
    public int getBuildHeight() {
        return this.handle.getMaxHeight(); // TODO: same?
    }

    @Override
    public Context getContext() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean contains(Location location) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Location getFullBlock(Vector3i position) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Location getFullBlock(int x, int y, int z) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BlockType getBlockType(Vector3i position) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BlockType getBlockType(int x, int y, int z) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setBlockType(Vector3i position, BlockType type) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setBlockType(int x, int y, int z, BlockType type) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BlockSnapshot getBlockSnapshot(Vector3i position) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BlockSnapshot getBlockSnapshot(int x, int y, int z) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setBlockSnapshot(Vector3i position, BlockSnapshot snapshot) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setBlockSnapshot(int x, int y, int z, BlockSnapshot snapshot) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> Optional<T> getBlockData(Vector3i position, Class<T> dataClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> Optional<T> getBlockData(int x, int y, int z, Class<T> dataClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void interactBlock(Vector3i position) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void interactBlock(int x, int y, int z) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void interactBlockWith(Vector3i position, ItemStack itemStack) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void interactBlockWith(int x, int y, int z, ItemStack itemStack) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean digBlock(Vector3i position) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean digBlock(int x, int y, int z) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean digBlockWith(Vector3i position, ItemStack itemStack) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean digBlockWith(int x, int y, int z, ItemStack itemStack) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getBlockDigTime(Vector3i position) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getBlockDigTime(int x, int y, int z) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getBlockDigTimeWith(Vector3i position, ItemStack itemStack) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getBlockDigTimeWith(int x, int y, int z, ItemStack itemStack) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte getLuminance(Vector3i position) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte getLuminance(int x, int y, int z) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte getLuminanceFromSky(Vector3i position) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte getLuminanceFromSky(int x, int y, int z) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte getLuminanceFromGround(Vector3i position) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte getLuminanceFromGround(int x, int y, int z) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isBlockPowered(Vector3i position) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isBlockPowered(int x, int y, int z) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isBlockIndirectlyPowered(Vector3i position) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isBlockIndirectlyPowered(int x, int y, int z) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isBlockFacePowered(Vector3i position, Direction direction) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isBlockFacePowered(int x, int y, int z, Direction direction) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isBlockFaceIndirectlyPowered(Vector3i position, Direction direction) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isBlockFaceIndirectlyPowered(int x, int y, int z, Direction direction) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<Direction> getPoweredBlockFaces(Vector3i position) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<Direction> getPoweredBlockFaces(int x, int y, int z) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<Direction> getIndirectlyPoweredBlockFaces(Vector3i position) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<Direction> getIndirectlyPoweredBlockFaces(int x, int y, int z) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isBlockPassable(Vector3i position) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isBlockPassable(int x, int y, int z) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isBlockFlammable(Vector3i position, Direction faceDirection) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isBlockFlammable(int x, int y, int z, Direction faceDirection) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<ScheduledBlockUpdate> getScheduledUpdates(Vector3i position) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<ScheduledBlockUpdate> getScheduledUpdates(int x, int y, int z) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ScheduledBlockUpdate addScheduledUpdate(Vector3i position, int priority, int ticks) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ScheduledBlockUpdate addScheduledUpdate(int x, int y, int z, int priority, int ticks) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeScheduledUpdate(Vector3i position, ScheduledBlockUpdate update) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeScheduledUpdate(int x, int y, int z, ScheduledBlockUpdate update) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends DataManipulator<T>> Optional<T> getData(Vector3i position, Class<T> dataClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends DataManipulator<T>> Optional<T> getData(int x, int y, int z, Class<T> dataClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends DataManipulator<T>> Optional<T> getOrCreate(Vector3i position, Class<T> manipulatorClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends DataManipulator<T>> Optional<T> getOrCreate(int x, int y, int z, Class<T> manipulatorClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends DataManipulator<T>> boolean remove(Vector3i position, Class<T> manipulatorClass) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends DataManipulator<T>> boolean remove(int x, int y, int z, Class<T> manipulatorClass) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends DataManipulator<T>> boolean isCompatible(Vector3i position, Class<T> manipulatorClass) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends DataManipulator<T>> boolean isCompatible(int x, int y, int z, Class<T> manipulatorClass) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends DataManipulator<T>> DataTransactionResult offer(Vector3i position, T manipulatorData) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends DataManipulator<T>> DataTransactionResult offer(int x, int y, int z, T manipulatorData) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends DataManipulator<T>> DataTransactionResult offer(Vector3i position, T manipulatorData, DataPriority priority) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends DataManipulator<T>> DataTransactionResult offer(int x, int y, int z, T manipulatorData, DataPriority priority) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<? extends DataManipulator<?>> getManipulators(Vector3i position) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<? extends DataManipulator<?>> getManipulators(int x, int y, int z) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(Vector3i position, Class<T> propertyClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(int x, int y, int z, Class<T> propertyClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<? extends Property<?, ?>> getProperties(Vector3i position) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<? extends Property<?, ?>> getProperties(int x, int y, int z) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean validateRawData(Vector3i position, DataContainer container) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean validateRawData(int x, int y, int z, DataContainer container) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setRawData(Vector3i position, DataContainer container) throws InvalidDataException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setRawData(int x, int y, int z, DataContainer container) throws InvalidDataException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Vector2i getBiomeMin() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Vector2i getBiomeMax() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Vector2i getBiomeSize() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BiomeType getBiome(Vector2i position) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BiomeType getBiome(int x, int z) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setBiome(Vector2i position, BiomeType biome) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setBiome(int x, int z, BiomeType biome) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<Entity> getEntities() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<Entity> getEntities(Predicate<Entity> filter) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<Entity> createEntity(EntityType type, Vector3d position) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<Entity> createEntity(EntityType type, Vector3i position) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<Entity> createEntity(DataContainer entityContainer) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<Entity> createEntity(DataContainer entityContainer, Vector3d position) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean spawnEntity(Entity entity) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public UUID getUniqueId() {
        return this.handle.getUID();
    }

    @Override
    public Collection<TileEntity> getTileEntities() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<TileEntity> getTileEntities(Predicate<TileEntity> filter) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<TileEntity> getTileEntity(Vector3i position) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<TileEntity> getTileEntity(int x, int y, int z) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<TileEntity> getTileEntity(Location blockLoc) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Vector3i getBlockMin() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Vector3i getBlockMax() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Vector3i getBlockSize() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BlockState getBlock(Vector3i position) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setBlock(Vector3i position, BlockState block) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setBlock(int x, int y, int z, BlockState block) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void spawnParticles(ParticleEffect particleEffect, Vector3d position) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void spawnParticles(ParticleEffect particleEffect, Vector3d position, int radius) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume, double pitch) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume, double pitch, double minVolume) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendMessage(ChatType type, String... message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendMessage(ChatType type, Text... messages) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendMessage(ChatType type, Iterable<Text> messages) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendTitle(Title title) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void resetTitle() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clearTitle() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Weather getWeather() {
        if (this.handle.hasStorm()) {
            return Weathers.RAIN;
        } else if (this.handle.isThundering()) {
            return Weathers.THUNDER_STORM;
        } else {
            return Weathers.CLEAR;
        }
    }

    @Override
    public long getRemainingDuration() {
        return this.handle.getWeatherDuration() != 0 ? this.handle.getWeatherDuration() : this.handle.getThunderDuration();
    }

    @Override
    public long getRunningDuration() {
        // TODO: doesn't appear the Bukkit API exposes running weather duration?
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void forecast(Weather weather) {
        if (weather == Weathers.RAIN) {
            this.handle.setStorm(true);
        } else if (weather == Weathers.THUNDER_STORM) {
            this.handle.setThundering(true);
        } else if (weather == Weathers.CLEAR) {
            this.handle.setStorm(false);
            this.handle.setThundering(false);
        }
    }

    @Override
    public void forecast(Weather weather, long duration) {
        this.forecast(weather);

        if (weather == Weathers.RAIN) {
            this.handle.setWeatherDuration((int) duration);
        } else if (weather == Weathers.THUNDER_STORM) {
            this.handle.setThunderDuration((int) duration);
        }
    }
}
