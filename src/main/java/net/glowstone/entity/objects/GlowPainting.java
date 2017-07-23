package net.glowstone.entity.objects;

import com.flowpowered.network.Message;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.glowstone.entity.GlowHangingEntity;
import net.glowstone.net.message.play.entity.SpawnPaintingMessage;
import net.glowstone.util.PaintingSpawner;
import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Painting;
import org.bukkit.inventory.ItemStack;

public class GlowPainting extends GlowHangingEntity implements Painting {
    private Art art;

    private static final Map<Art, String> artTitle = new HashMap<>();

    static {
        artTitle.put(Art.KEBAB, "Kebab");
        artTitle.put(Art.AZTEC, "Aztec");
        artTitle.put(Art.ALBAN, "Alban");
        artTitle.put(Art.AZTEC2, "Aztec2");
        artTitle.put(Art.BOMB, "Bomb");
        artTitle.put(Art.PLANT, "Plant");
        artTitle.put(Art.WASTELAND, "Wasteland");
        artTitle.put(Art.POOL, "Pool");
        artTitle.put(Art.COURBET, "Courbet");
        artTitle.put(Art.SEA, "Sea");
        artTitle.put(Art.SUNSET, "Sunset");
        artTitle.put(Art.CREEBET, "Creebet");
        artTitle.put(Art.WANDERER, "Wanderer");
        artTitle.put(Art.GRAHAM, "Graham");
        artTitle.put(Art.MATCH, "Match");
        artTitle.put(Art.BUST, "Bust");
        artTitle.put(Art.STAGE, "Stage");
        artTitle.put(Art.VOID, "Void");
        artTitle.put(Art.SKULL_AND_ROSES, "SkullAndRoses");
        artTitle.put(Art.WITHER, "Wither");
        artTitle.put(Art.FIGHTERS, "Fighters");
        artTitle.put(Art.POINTER, "Pointer");
        artTitle.put(Art.BURNINGSKULL, "BurningSkull");
        artTitle.put(Art.SKELETON, "Skeleton");
        artTitle.put(Art.DONKEYKONG, "DonkeyKong");
    }

    public GlowPainting(Location location) {
        this(location, BlockFace.SOUTH);
    }

    public GlowPainting(Location location, BlockFace clickedface) {
        super(location, clickedface);
        this.setArtInternal(Art.KEBAB);
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
        String title = artTitle.get(art);

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
        if (new PaintingSpawner().spawn(location, art, facing.getBlockFace(), force)) {
            this.art = art;
            this.remove();
            return true;
        }
        return false;
    }

    public void setArtInternal(Art art) {
        this.art = art;
    }

    @Override
    public boolean setFacingDirection(BlockFace blockFace, boolean force) {
        if (new PaintingSpawner().spawn(location, art, blockFace, force)) {
            facing = HangingFace.getByBlockFace(blockFace);
            this.remove();
            return true;
        }
        return false;
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
    }

    @Override
    protected void pulsePhysics() {
        // item frames aren't affected by physics
    }
}
