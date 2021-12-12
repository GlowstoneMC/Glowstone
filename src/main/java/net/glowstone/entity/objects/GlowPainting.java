package net.glowstone.entity.objects;

import com.flowpowered.network.Message;
import lombok.Getter;
import net.glowstone.EventFactory;
import net.glowstone.entity.GlowHangingEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.physics.EntityBoundingBox;
import net.glowstone.net.message.play.entity.DestroyEntitiesMessage;
import net.glowstone.net.message.play.entity.SpawnPaintingMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage.Action;
import org.bukkit.Art;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Painting;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GlowPainting extends GlowHangingEntity implements Painting {

    private static final double PAINTING_DEPTH = 0.0625;
    private static final Art DEFAULT_ART = Art.KEBAB;
    private static final Map<Art, String> TITLE_BY_ART = new EnumMap<>(Art.class);
    private static final Map<String, Art> ART_BY_TITLE = new HashMap<>();

    static {
        TITLE_BY_ART.put(Art.KEBAB, "Kebab");
        TITLE_BY_ART.put(Art.AZTEC, "Aztec");
        TITLE_BY_ART.put(Art.ALBAN, "Alban");
        TITLE_BY_ART.put(Art.AZTEC2, "Aztec2");
        TITLE_BY_ART.put(Art.BOMB, "Bomb");
        TITLE_BY_ART.put(Art.PLANT, "Plant");
        TITLE_BY_ART.put(Art.WASTELAND, "Wasteland");
        TITLE_BY_ART.put(Art.POOL, "Pool");
        TITLE_BY_ART.put(Art.COURBET, "Courbet");
        TITLE_BY_ART.put(Art.SEA, "Sea");
        TITLE_BY_ART.put(Art.SUNSET, "Sunset");
        TITLE_BY_ART.put(Art.CREEBET, "Creebet");
        TITLE_BY_ART.put(Art.WANDERER, "Wanderer");
        TITLE_BY_ART.put(Art.GRAHAM, "Graham");
        TITLE_BY_ART.put(Art.MATCH, "Match");
        TITLE_BY_ART.put(Art.BUST, "Bust");
        TITLE_BY_ART.put(Art.STAGE, "Stage");
        TITLE_BY_ART.put(Art.VOID, "Void");
        TITLE_BY_ART.put(Art.SKULL_AND_ROSES, "SkullAndRoses");
        TITLE_BY_ART.put(Art.WITHER, "Wither");
        TITLE_BY_ART.put(Art.FIGHTERS, "Fighters");
        TITLE_BY_ART.put(Art.POINTER, "Pointer");
        TITLE_BY_ART.put(Art.BURNING_SKULL, "BurningSkull");
        TITLE_BY_ART.put(Art.SKELETON, "Skeleton");
        TITLE_BY_ART.put(Art.DONKEY_KONG, "DonkeyKong");
        TITLE_BY_ART.put(Art.PIGSCENE, "PigScene");

        TITLE_BY_ART.forEach((art, title) -> ART_BY_TITLE.put(title, art));
    }

    @Getter
    private @Nullable Art art;

    @Getter
    private Location artCenter;

    public GlowPainting(Location center) {
        this(center, BlockFace.SOUTH);
    }

    /**
     * Creates a painting with the default art.
     *
     * @param center the center of the painting
     * @param facing the direction for the painting to face
     */
    public GlowPainting(Location center, BlockFace facing) {
        super(center, facing);
        this.artCenter = center;
        setArtInternal(DEFAULT_ART);
    }

    public static Art getArtFromTitle(String title) {
        return ART_BY_TITLE.get(title);
    }

    public String getArtTitle() {
        return TITLE_BY_ART.get(art);
    }

    @NotNull
    @Override
    public EntityType getType() {
        return EntityType.PAINTING;
    }

    @Override
    public boolean entityInteract(@NotNull GlowPlayer player, @NotNull InteractEntityMessage message) {
        if (message.getAction() == Action.ATTACK.ordinal()) {
            if (EventFactory.getInstance().callEvent(new HangingBreakByEntityEvent(this, player)).isCancelled()) {
                return false;
            }
            if (player.getGameMode() != GameMode.CREATIVE) {
                world.dropItemNaturally(location, new ItemStack(Material.PAINTING));
            }
            remove();
        }
        return true;
    }

    @Override
    public @NotNull List<Message> createSpawnMessage() {
        int x = artCenter.getBlockX();
        int y = artCenter.getBlockY();
        int z = artCenter.getBlockZ();
        int artId = art.getId();

        // TODO: replace art title with ID
        return Collections.singletonList(
                new SpawnPaintingMessage(this.getEntityId(), this.getUniqueId(), artId, x, y, z, facing.ordinal()));
    }

    @Override
    public boolean setArt(@NotNull Art art) {
        return this.setArt(art, false);
    }

    @Override
    public boolean setArt(@NotNull Art art, boolean force) {
        Art oldArt = this.art;
        setArtInternal(art);

        if (!force && isObstructed()) {
            setArtInternal(oldArt);
            return false;
        }

        refresh();

        return true;
    }

    /**
     * Refreshes the painting for nearby clients.
     *
     * <p>This will first destroy, and then spawn the painting again using its current art and
     * facing value.
     */
    public void refresh() {
        DestroyEntitiesMessage destroyMessage =
                new DestroyEntitiesMessage(Collections.singletonList(this.getEntityId()));
        List<Message> spawnMessages = this.createSpawnMessage();
        spawnMessages.add(0, destroyMessage);


        getWorld().getRawPlayers().stream()
                .filter(p -> p.canSeeEntity(this))
                .forEach(p -> p.getSession().sendAll(spawnMessages.toArray(new Message[0])));
    }

    /**
     * Sets the art of this painting, regardless of available space.
     *
     * <p>This matches the behaviour of {@link #setArt(Art, boolean) setArt(art, true)},
     * but the painting does not get refreshed.
     *
     * <p>Null values are ignored.
     *
     * @param art the Art of the painting
     */
    public void setArtInternal(@Nullable Art art) {
        if (art == null) {
            return;
        }
        this.art = art;

        recalculateLocation();

        updateBoundingBox();
    }

    private void recalculateLocation() {
        BlockFace rightFace = getRightFace();
        double modX = rightFace.getModX() * art.getBlockWidth() / 2.0;
        double modY = art.getBlockHeight() / 2.0;
        double modZ = rightFace.getModZ() * art.getBlockWidth() / 2.0;

        BlockFace facing = getFacing();
        if (modX == 0.0) {
            modX = 0.5 - facing.getModX() * 0.5 - PAINTING_DEPTH / 2;
        } else if (modZ == 0.0) {
            modZ = 0.5 - facing.getModZ() * 0.5 - PAINTING_DEPTH / 2;
        }

        Location add = getTopLeftCorner().add(modX, -modY, modZ);
        location.setX(add.getX());
        location.setY(add.getY());
        location.setZ(add.getZ());
        location.setPitch(0);
        location.setYaw(getYaw());
    }

    @Override
    public void setFacingDirection(@NotNull BlockFace blockFace) {
        setFacingDirection(blockFace, false);
    }

    @Override
    public boolean setFacingDirection(@NotNull BlockFace blockFace, boolean force) {
        HangingFace oldFace = facing;
        this.facing = HangingFace.getByBlockFace(blockFace);

        if (!force && isObstructed()) {
            this.facing = oldFace;
            return false;
        }

        refresh();

        return true;
    }

    @Override
    public void pulse() {
        super.pulse();

        if (ticksLived % (20 * 5) == 0 && isObstructed()) {
            if (EventFactory.getInstance().callEvent(new HangingBreakEvent(this, RemoveCause.PHYSICS)).isCancelled()) {
                return;
            }

            world.dropItemNaturally(location, new ItemStack(Material.PAINTING));
            remove();
        }
    }

    @Override
    protected void pulsePhysics() {
        // not affected by physics
    }

    /**
     * Check if the painting is obstructed at the current location.
     *
     * <p>Survivability is defined as:
     * <ul>
     *     <li>The wall behind the painting is completely solid</li>
     *     <li>The painting is not inside a block</li>
     *     <li>The painting is not inside another entity</li>
     * </ul>
     *
     * @return true if the painting should drop, false otherwise
     */
    public boolean isObstructed() {
        Location current = getTopLeftCorner();
        BlockFace right = getRightFace();

        for (int y = 0; y < art.getBlockHeight(); y++) {
            for (int x = 0; x < art.getBlockWidth(); x++) {
                if (!canHoldPainting(current)) {
                    return true;
                }
                current = current.getBlock().getRelative(right).getLocation();
            }

            current = current.getBlock().getRelative(BlockFace.DOWN).getLocation();

            // reset x and z
            current.subtract(right.getModX() * art.getBlockWidth(), 0, right.getModZ() * art.getBlockWidth());
        }

        List<Entity> entitiesInside = this.world.getEntityManager().getEntitiesInside(this.boundingBox, this);
        return entitiesInside.stream().anyMatch(e -> e instanceof Hanging);
    }

    private boolean canHoldPainting(@NotNull Location where) {
        Block block = where.getBlock();
        if (block.getType().isSolid()) {
            return false;
        }

        Block behind = block.getRelative(getAttachedFace());
        return behind.getType().isSolid();
    }

    private @NotNull Location getTopLeftCorner() {
        BlockFace left = getLeftFace();
        Location topLeft = artCenter.clone();
        int topMod = (int) getArtHeight();
        int widthMod = (int) getArtWidth();

        topLeft.add(left.getModX() * widthMod, topMod, left.getModZ() * widthMod);
        return topLeft;
    }

    private BlockFace getLeftFace() {
        return HangingFace.values()[(facing.ordinal() + 1) % 4].getBlockFace();
    }

    private @NotNull BlockFace getRightFace() {
        return getLeftFace().getOppositeFace();
    }

    @Override
    public double getWidth() {
        // Paper always returns 0.5 regardless of actual art size
        return 0.5;
    }

    @Override
    public double getHeight() {
        // Paper always returns 0.5 regardless of actual art size
        return 0.5;
    }

    private double getArtWidth() {
        return Math.max(0, (double) art.getBlockWidth() / 2 - 1);
    }

    private double getArtHeight() {
        return (double) art.getBlockHeight() / 2;
    }

    @Override
    protected void updateBoundingBox() {
        BlockFace rightFace = getRightFace();
        double modX = Math.abs(rightFace.getModX() * art.getBlockWidth());
        double modY = art.getBlockHeight();
        double modZ = Math.abs(rightFace.getModZ() * art.getBlockWidth());

        if (modX == 0.0) {
            modX = PAINTING_DEPTH;
        } else if (modZ == 0.0) {
            modZ = PAINTING_DEPTH;
        }

        // restrict the bounding box to not reach exactly onto the next blocks
        modX -= 0.00001;
        modY -= 0.00001;
        modZ -= 0.00001;

        boundingBox = new EntityBoundingBox(location, modX, modY, modZ);
        super.updateBoundingBox();

        // y of the painting is in the center, but for most other it is at the foot
        // therefore center it here
        // TODO:
        // boundingBox.minCorner.setY(location.getY() - modY / 2);
        // boundingBox.maxCorner.setY(location.getY() + modY / 2);
    }

    @Override
    public void setRawLocation(@NotNull Location location, boolean fall) {
        // Also try to move the center of the painting along
        Location difference = location.subtract(artCenter);
        super.setRawLocation(location, fall);

        artCenter = location.clone().subtract(difference);
    }
}
