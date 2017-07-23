package net.glowstone.entity.objects;

import com.flowpowered.network.Message;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.glowstone.EventFactory;
import net.glowstone.entity.GlowHangingEntity;
import net.glowstone.net.message.play.entity.SpawnPaintingMessage;
import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Painting;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.inventory.ItemStack;

public class GlowPainting extends GlowHangingEntity implements Painting {
    private Art art;

    private static final Map<Art, String> ART_TITLE = new HashMap<>();

    static {
        ART_TITLE.put(Art.KEBAB, "Kebab");
        ART_TITLE.put(Art.AZTEC, "Aztec");
        ART_TITLE.put(Art.ALBAN, "Alban");
        ART_TITLE.put(Art.AZTEC2, "Aztec2");
        ART_TITLE.put(Art.BOMB, "Bomb");
        ART_TITLE.put(Art.PLANT, "Plant");
        ART_TITLE.put(Art.WASTELAND, "Wasteland");
        ART_TITLE.put(Art.POOL, "Pool");
        ART_TITLE.put(Art.COURBET, "Courbet");
        ART_TITLE.put(Art.SEA, "Sea");
        ART_TITLE.put(Art.SUNSET, "Sunset");
        ART_TITLE.put(Art.CREEBET, "Creebet");
        ART_TITLE.put(Art.WANDERER, "Wanderer");
        ART_TITLE.put(Art.GRAHAM, "Graham");
        ART_TITLE.put(Art.MATCH, "Match");
        ART_TITLE.put(Art.BUST, "Bust");
        ART_TITLE.put(Art.STAGE, "Stage");
        ART_TITLE.put(Art.VOID, "Void");
        ART_TITLE.put(Art.SKULL_AND_ROSES, "SkullAndRoses");
        ART_TITLE.put(Art.WITHER, "Wither");
        ART_TITLE.put(Art.FIGHTERS, "Fighters");
        ART_TITLE.put(Art.POINTER, "Pointer");
        ART_TITLE.put(Art.BURNINGSKULL, "BurningSkull");
        ART_TITLE.put(Art.SKELETON, "Skeleton");
        ART_TITLE.put(Art.DONKEYKONG, "DonkeyKong");
    }

    public GlowPainting(Location location) {
        this(location, BlockFace.SOUTH);
    }

    public GlowPainting(Location location, BlockFace clickedface) {
        super(location, clickedface);
        setArtInternal(Art.KEBAB);
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
        String title = ART_TITLE.get(art);

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

        if (isObstructed()) {
            this.art = oldArt;
            return false;
        }

        setBoundingBox(art.getBlockWidth() - 0.00001, art.getBlockHeight() - 0.00001);
        this.remove();
        return false;
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

        if (isObstructed()) {
            this.facing = oldFace;
            return false;
        }

        this.remove();
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

        List<Entity> entitiesInside = this.world.getEntityManager().getEntitiesInside(this.boundingBox, this);
        for (Entity entity : entitiesInside) {
            System.out.println("Art|entitiesInside:" + art.getBlockWidth() + "|" + art.getBlockHeight() + "|" + entity.getLocation());
        }
        return !entitiesInside.isEmpty();
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
