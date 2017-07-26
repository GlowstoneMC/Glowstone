package net.glowstone.entity.objects;

import com.flowpowered.network.Message;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.glowstone.EventFactory;
import net.glowstone.entity.GlowHangingEntity;
import net.glowstone.net.message.play.entity.DestroyEntitiesMessage;
import net.glowstone.net.message.play.entity.SpawnPaintingMessage;
import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Painting;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.inventory.ItemStack;

public class GlowPainting extends GlowHangingEntity implements Painting {
    private Art art;

    private static final Map<Art, String> TITLE_BY_ART = new HashMap<>();
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
        TITLE_BY_ART.put(Art.BURNINGSKULL, "BurningSkull");
        TITLE_BY_ART.put(Art.SKELETON, "Skeleton");
        TITLE_BY_ART.put(Art.DONKEYKONG, "DonkeyKong");
        TITLE_BY_ART.put(Art.PIGSCENE, "PigScene");

        TITLE_BY_ART.forEach((art, title) -> ART_BY_TITLE.put(title, art));
    }

    public GlowPainting(Location location) {
        this(location, BlockFace.SOUTH);
    }

    public GlowPainting(Location location, BlockFace clickedface) {
        super(location, clickedface);
        setArtInternal(Art.KEBAB);
    }

    public String getArtTitle() {
        return TITLE_BY_ART.get(art);
    }

    public static Art getArtFromTitle(String title) {
        return ART_BY_TITLE.get(title);
    }

    @Override
    public EntityType getType() {
        return EntityType.PAINTING;
    }

    @Override
    public List<Message> createSpawnMessage() {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        String title = getArtTitle();

        return Collections.singletonList(
            new SpawnPaintingMessage(this.getEntityId(), this.getUniqueId(), title, x, y, z, facing.ordinal())
        );
    }

    @Override
    public Art getArt() {
        return art;
    }

    @Override
    public boolean setArt(Art art) {
        return this.setArt(art, false);
    }

    @Override
    public boolean setArt(Art art, boolean force) {
        Art oldArt = this.art;
        this.art = art;
        setBoundingBox(art.getBlockWidth() - 0.00001, art.getBlockHeight() - 0.00001);

        if (!force && isObstructed()) {
            this.art = oldArt;
            setBoundingBox(art.getBlockWidth() - 0.00001, art.getBlockHeight() - 0.00001);
            return false;
        }

        respawn();

        return false;
    }


    protected void respawn() {
        DestroyEntitiesMessage destroyMessage = new DestroyEntitiesMessage(Collections.singletonList(this.getEntityId()));
        List<Message> spawnMessage = this.createSpawnMessage();
        Collection<Message> messages = Lists.newArrayList(destroyMessage);
        messages.add(destroyMessage);
        messages.addAll(spawnMessage);

        getWorld()
            .getRawPlayers()
            .stream()
            .filter(p -> p.canSeeEntity(this))
            .forEach(p -> p.getSession().sendAll(messages.toArray(new Message[messages.size()])));

    }

    @Override
    protected void updateBoundingBox() {
        super.updateBoundingBox();
    }

    public void setArtInternal(Art art) {
        this.art = art;
        setBoundingBox(art.getBlockWidth() - 0.00001, art.getBlockHeight() - 0.00001);
    }

    @Override
    public boolean setFacingDirection(BlockFace blockFace, boolean force) {
        HangingFace oldFace = facing;
        this.facing = HangingFace.getByBlockFace(blockFace);

        if (!force && isObstructed()) {
            this.facing = oldFace;
            return false;
        }

        respawn();

        return true;
    }

    @Override
    public void setFacingDirection(BlockFace blockFace) {
        setFacingDirection(blockFace, false);
    }

    @Override
    public void pulse() {
        super.pulse();

        if (ticksLived % 11 == 0) {

            if (location.getBlock().getRelative(getAttachedFace()).getType() == Material.AIR) {
                if (EventFactory.callEvent(new HangingBreakEvent(this, RemoveCause.PHYSICS)).isCancelled()) {
                    return;
                }
                world.dropItemNaturally(location, new ItemStack(Material.PAINTING));
                remove();
            }
        }

        if (ticksLived % (20 * 5) == 0) {
            if (isObstructed()) {
                if (EventFactory.callEvent(new HangingBreakEvent(this, RemoveCause.PHYSICS)).isCancelled()) {
                    return;
                }
                if (location.getBlock().getRelative(getAttachedFace()).getType() == Material.AIR) {
                    world.dropItemNaturally(location, new ItemStack(Material.PAINTING));
                    remove();
                }
            }
        }
    }

    @Override
    protected void pulsePhysics() {
        // item frames aren't affected by physics
    }

    public boolean isObstructed() {
        Location topLeftCorner = getTopLeftCorner().getBlock().getRelative(facing.getBlockFace().getOppositeFace()).getLocation();
        BlockFace right = getLeftFace().getBlockFace().getOppositeFace();

        Location current = topLeftCorner.clone();
        for (int y = 0; y < art.getBlockHeight(); y++) {
            for (int x = 0; x < art.getBlockWidth(); x++) {
                if (!canHoldPainting(current)) {
                    return true;
                }
                current = current.getBlock().getRelative(right).getLocation();
            }

            current = current.getBlock().getRelative(BlockFace.DOWN).getLocation();
            current.setX(topLeftCorner.getX());
            current.setZ(topLeftCorner.getZ());
        }

        return !this.world.getEntityManager().getEntitiesInside(this.boundingBox, this).isEmpty();
    }

    private boolean canHoldPainting(Location where) {
        if (!where.getBlock().getType().isSolid()) {
            return false;
        }

        Block inFront = where.clone().getBlock().getRelative(facing.getBlockFace());
        if (inFront.getType().isSolid()) {
            return false;
        }
        return true;
    }

    private Location getTopLeftCorner() {
        BlockFace left = getLeftFace().getBlockFace();
        Location topLeft = location.clone();
        int topMod = art.getBlockHeight() / 2;
        int widthMod = Math.max(0, art.getBlockHeight() / 2 - 1);

        topLeft.add(left.getModX() * widthMod, topMod, left.getModZ() * widthMod);
        return topLeft;
    }

    private HangingFace getLeftFace() {
        return HangingFace.values()[(facing.ordinal() + 1) % 4];
    }

    @Override
    public double getWidth() {
        return 0.5;
    }

    @Override
    public double getHeight() {
        return 0.5;
    }
}
