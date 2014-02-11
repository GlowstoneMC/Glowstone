package net.glowstone.io.entity;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.FloatTag;
import net.glowstone.util.nbt.IntTag;
import net.glowstone.util.nbt.Tag;
import org.bukkit.GameMode;

import java.util.List;

public class PlayerStore extends HumanEntityStore<GlowPlayer> {

    public PlayerStore() {
        super(GlowPlayer.class, "Player");
    }

    @Override
    public GlowPlayer load(GlowServer server, GlowWorld world, CompoundTag compound) {
        throw new UnsupportedOperationException("Only existing players can be loaded to");
    }

    @Override
    public void load(GlowPlayer entity, CompoundTag compound) {
        super.load(entity, compound);
        if (compound.is("XpTotal", IntTag.class)) {
            entity.setTotalExperience(compound.get("XpTotal", IntTag.class));
        }
        if (compound.is("foodLevel", IntTag.class)) {
            entity.setFoodLevel(compound.get("foodLevel", IntTag.class));
        }
        if (compound.is("foodTickTimer", IntTag.class)) {
            // entity.set(((IntTag)compound.getValue().get("foodTickTimer")).getValue());
        }
        if (compound.is("foodSaturationLevel", FloatTag.class)) {
            entity.setSaturation(compound.get("foodSaturationLevel", FloatTag.class));
        }
        if (compound.is("foodExhaustionLevel", FloatTag.class)) {
            entity.setExhaustion(compound.get("foodExhaustionLevel", FloatTag.class));
        }
        if (compound.is("playerGameType", IntTag.class)) {
            GameMode mode = GameMode.getByValue(compound.get("playerGameType", IntTag.class));
            if (mode != null) entity.setGameMode(mode);
        }
    }

    @Override
    public List<Tag> save(GlowPlayer entity) {
        List<Tag> ret = super.save(entity);
        ret.add(new IntTag("XpTotal", entity.getTotalExperience()));
        ret.add(new IntTag("Xp", entity.getExperience()));
        ret.add(new IntTag("XpLevel", entity.getLevel()));
        // ret.put("foodTickTimer", new IntTag("foodTickTimer", entity.get));
        ret.add(new FloatTag("foodSaturationLevel", entity.getSaturation()));
        ret.add(new FloatTag("foodExhaustionLevel", entity.getExhaustion()));
        ret.add(new IntTag("playerGameType", entity.getGameMode().getValue()));
        return ret;
    }
}
