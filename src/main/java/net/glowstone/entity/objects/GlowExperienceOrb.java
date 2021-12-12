package net.glowstone.entity.objects;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.flowpowered.network.Message;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.EventFactory;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.entity.SpawnXpOrbMessage;
import net.glowstone.util.EntityUtils;
import net.glowstone.util.TickUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

public class GlowExperienceOrb extends GlowEntity implements ExperienceOrb {

    private static final int LIFETIME = TickUtil.minutesToTicks(5);
    private static final double MAX_DRAG_DISTANCE = 7.25;

    @Getter
    @Setter
    private boolean fromBottle;
    @Getter
    @Setter
    private UUID sourceEntityId;
    @Getter
    @Setter
    private UUID triggerEntityId;
    @Getter
    @Setter
    private SpawnReason spawnReason;
    @Getter
    private int experience;
    private boolean tickSkipped = false;

    public GlowExperienceOrb(Location location) {
        this(location, 1);
    }

    /**
     * Creates an experience orb.
     *
     * @param location   the location
     * @param experience the amount of experience contained
     */
    public GlowExperienceOrb(Location location, int experience) {
        super(location);
        setBoundingBox(0.5, 0.5);
        this.experience = experience;
        this.fromBottle = false;
    }

    @Override
    public List<Message> createSpawnMessage() {
        return Collections.singletonList(
                new SpawnXpOrbMessage(getEntityId(), getLocation(), (short) getExperience()));
    }

    @Override
    public void damage(double amount, Entity source, @NotNull EntityDamageEvent.DamageCause cause) {
        if (!isInvulnerable()) {
            remove();
        }
    }


    @Override
    public void pulse() {
        super.pulse();

        // Drag self towards the nearest player
        getNearestPlayer().ifPresent(player -> {
            final Vector distance = player.getLocation().subtract(location).toVector();

            // The more the player is distant, the more the orb is slow
            distance.multiply(Math.pow(distance.length(), -1));
            this.setVelocity(distance);
        });


        if (tickSkipped) {
            // find player to give experience
            Optional<GlowPlayer> player = getWorld().getRawPlayers().stream()
                    .filter(p -> p.getLocation().distanceSquared(location) <= 1)
                    .findAny();

            if (player.isPresent()) {
                PlayerPickupExperienceEvent event = new PlayerPickupExperienceEvent(player.get(), this);
                event = EventFactory.getInstance().callEvent(event);
                if (!event.isCancelled()) {
                    player.get().giveExp(experience);
                    world.playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                    remove();
                }
                return;
            }
        }
        if (getTicksLived() > LIFETIME) {
            remove();
            return;
        }
        if (!tickSkipped) {
            tickSkipped = true;
        }
    }

    private @NotNull Optional<Player> getNearestPlayer() {
        return getWorld().getPlayers().stream()
                .filter(p -> p.getLocation().distanceSquared(location) < (MAX_DRAG_DISTANCE * MAX_DRAG_DISTANCE))
                .min(Comparator.comparingInt(o -> (int) o.getLocation().distanceSquared(location)));
    }

    @Override
    public void setExperience(int experience) {
        checkArgument(experience > 0, "Experience points cannot be negative.");
        this.experience = experience;
        EntityUtils.refresh(this);
    }

    @Override
    public @NotNull EntityType getType() {
        return EntityType.EXPERIENCE_ORB;
    }
}
