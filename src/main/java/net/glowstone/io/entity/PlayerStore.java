package net.glowstone.io.entity;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

class PlayerStore extends HumanEntityStore<GlowPlayer> {

    public PlayerStore() {
        super(GlowPlayer.class, EntityType.PLAYER);
    }

    @Override
    public GlowPlayer createEntity(Location location, CompoundTag compound) {
        throw new UnsupportedOperationException("Cannot create players through PlayerStore");
    }

    // todo: the following tags
    // - int "Score"
    // - int "foodTickTimer"
    // in "abilities":
    // - bool "invulnerable"
    // - bool "mayBuild"
    // - bool "instabuild"
    // in "bukkit":
    // - bool "keepLevel"
    // - int "expToDrop"
    // - int "newExp"
    // - int "newLevel"
    // - int "newTotalExp"

    // Bukkit walk and fly speed units are twice Minecraft's

    @Override
    public void load(GlowPlayer entity, CompoundTag tag) {
        super.load(entity, tag);

        // experience
        tag.readInt("XpLevel", entity::setLevel);
        tag.readFloat("XpP", entity::setExp);
        tag.readInt("XpTotal", entity::setTotalExperience);

        // food
        tag.readInt("foodLevel", entity::setFoodLevel);
        tag.readFloat("foodSaturationLevel", entity::setSaturation);
        tag.readFloat("foodExhaustionLevel", entity::setExhaustion);

        // spawn location
        if (tag.isInt("SpawnX") && tag.isInt("SpawnY") && tag.isInt("SpawnZ")) {
            int x = tag.getInt("SpawnX");
            int y = tag.getInt("SpawnY");
            int z = tag.getInt("SpawnZ");
            entity.setBedSpawnLocation(new Location(entity.getWorld(), x, y, z),
                    tag.getBoolean("SpawnForced", false));
        }

        // abilities
        tag.readCompound("abilities", abilities -> {
            abilities.readFloat("walkSpeed", speed -> entity.setWalkSpeed(speed * 2f));
            abilities.readFloat("flySpeed", speed -> entity.setFlySpeed(speed * 2f));
            abilities.readBoolean("mayfly", entity::setAllowFlight);
            abilities.readBoolean("flying", entity::setFlying);
        });
        // shoulders (1.12)
        tag.readCompound("ShoulderEntityLeft", entity::setLeftShoulderTag);
        tag.readCompound("ShoulderEntityRight", entity::setRightShoulderTag);
        // seen credits
        tag.readBoolean("seenCredits", entity::setSeenCredits);

        // recipe book
        entity.getRecipeMonitor().read(tag);

        // bukkit
        // cannot read firstPlayed, lastPlayed, or lastKnownName
    }

    @Override
    public void save(GlowPlayer entity, CompoundTag tag) {
        super.save(entity, tag);

        // players have no id tag
        tag.remove("id");

        // experience
        tag.putInt("XpLevel", entity.getLevel());
        tag.putFloat("XpP", entity.getExp());
        tag.putInt("XpTotal", entity.getTotalExperience());

        // food
        tag.putInt("foodLevel", entity.getFoodLevel());
        tag.putFloat("foodSaturationLevel", entity.getSaturation());
        tag.putFloat("foodExhaustionLevel", entity.getExhaustion());

        // spawn location
        Location bed = entity.getBedSpawnLocation();
        if (bed != null) {
            tag.putInt("SpawnX", bed.getBlockX());
            tag.putInt("SpawnY", bed.getBlockY());
            tag.putInt("SpawnZ", bed.getBlockZ());
            tag.putBool("SpawnForced", entity.isBedSpawnForced());
        }

        // abilities
        CompoundTag abilities = new CompoundTag();
        abilities.putFloat("walkSpeed", entity.getWalkSpeed() / 2f);
        abilities.putFloat("flySpeed", entity.getFlySpeed() / 2f);
        abilities.putBool("mayfly", entity.getAllowFlight());
        abilities.putBool("flying", entity.isFlying());
        // for now, base these on the game mode value
        abilities.putBool("invulnerable", entity.getGameMode() == GameMode.CREATIVE);
        abilities.putBool("mayBuild", entity.getGameMode() != GameMode.ADVENTURE);
        abilities.putBool("instabuild", entity.getGameMode() == GameMode.CREATIVE);
        tag.putCompound("abilities", abilities);

        // shoulders
        if (!entity.getLeftShoulderTag().isEmpty()) {
            tag.putCompound("ShoulderEntityLeft", entity.getLeftShoulderTag());
        }
        if (!entity.getRightShoulderTag().isEmpty()) {
            tag.putCompound("ShoulderEntityRight", entity.getRightShoulderTag());
        }

        tag.putBool("seenCredits", entity.isSeenCredits());

        // recipe book
        entity.getRecipeMonitor().write(tag);

        // bukkit
        CompoundTag bukkit = new CompoundTag();
        bukkit.putLong("firstPlayed",
            entity.getFirstPlayed() == 0 ? entity.getJoinTime() : entity.getFirstPlayed());
        bukkit.putLong("lastPlayed", entity.getJoinTime());
        bukkit.putString("lastKnownName", entity.getName());
        tag.putCompound("bukkit", bukkit);
    }
}
