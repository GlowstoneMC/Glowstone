package net.glowstone.io.entity;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.GameMode;
import org.bukkit.Location;

public class PlayerStore extends HumanEntityStore<GlowPlayer> {

    public PlayerStore() {
        super(GlowPlayer.class, "Player");
    }

    @Override
    public GlowPlayer load(GlowServer server, GlowWorld world, CompoundTag compound) {
        throw new UnsupportedOperationException("Only existing players can be loaded to");
    }

    @Override
    public void load(GlowPlayer entity, CompoundTag tag) {
        super.load(entity, tag);
        if (tag.isInt("XpLevel")) {
            entity.setLevel(tag.getInt("XpLevel"));
        }
        if (tag.isFloat("XpP")) {
            entity.setExp(tag.getFloat("XpP"));
        }
        if (tag.isInt("XpTotal")) {
            entity.setTotalExperience(tag.getInt("XpTotal"));
        }
        if (tag.isInt("foodLevel")) {
            entity.setFoodLevel(tag.getInt("foodLevel"));
        }
        if (tag.isInt("foodTickTimer")) {
            // entity.set(((IntTag)compound.getValue().get("foodTickTimer")).getValue());
        }
        if (tag.isFloat("foodSaturationLevel")) {
            entity.setSaturation(tag.getFloat("foodSaturationLevel"));
        }
        if (tag.isFloat("foodExhaustionLevel")) {
            entity.setExhaustion(tag.getFloat("foodExhaustionLevel"));
        }
        if (tag.isInt("playerGameType")) {
            GameMode mode = GameMode.getByValue(tag.getInt("playerGameType"));
            if (mode != null) entity.setGameMode(mode);
        }
        if (tag.isInt("SpawnX") && tag.isInt("SpawnY") && tag.isInt("SpawnZ")) {
            int x = tag.getInt("SpawnX");
            int y = tag.getInt("SpawnY");
            int z = tag.getInt("SpawnZ");
            entity.setBedSpawnLocation(new Location(entity.getWorld(), x, y, z));
        }
    }

    @Override
    public void save(GlowPlayer entity, CompoundTag tag) {
        super.save(entity, tag);

        tag.putInt("XpLevel", entity.getLevel());
        tag.putFloat("XpP", entity.getExp());
        tag.putInt("XpTotal", entity.getTotalExperience());

        // ret.put("foodTickTimer", new IntTag("foodTickTimer", entity.get));
        tag.putFloat("foodSaturationLevel", entity.getSaturation());
        tag.putFloat("foodExhaustionLevel", entity.getExhaustion());
        tag.putInt("playerGameType", entity.getGameMode().getValue());

        // spawn location
        Location bed = entity.getBedSpawnLocation();
        if (bed != null) {
            tag.putInt("SpawnX", bed.getBlockX());
            tag.putInt("SpawnY", bed.getBlockY());
            tag.putInt("SpawnZ", bed.getBlockZ());
        }
    }
}
