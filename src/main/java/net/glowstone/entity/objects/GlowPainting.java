package net.glowstone.entity.objects;

import com.flowpowered.network.Message;
import java.util.Collections;
import java.util.List;
import net.glowstone.entity.GlowHangingEntity;
import net.glowstone.net.message.play.entity.SpawnPaintingMessage;
import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Painting;
import org.bukkit.inventory.ItemStack;

public class GlowPainting extends GlowHangingEntity implements Painting {
    private Art art;

    public GlowPainting(Location location) {
        this(location, BlockFace.SOUTH);
    }

    public GlowPainting(Location location, BlockFace clickedface) {
        super(location, clickedface);
        this.setArt(Art.KEBAB);
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

        return Collections.singletonList(
            new SpawnPaintingMessage(this.getEntityId(), this.getUniqueId(), art.name(), x, y, z, facing.ordinal())
        );
    }

    @Override
    public Art getArt() {
        return art;
    }

    @Override
    public boolean setArt(Art art) {
        this.art = art;
        return false;
    }

    @Override
    public boolean setArt(Art art, boolean b) {
        this.art = art;
        return false;
    }

    @Override
    public boolean setFacingDirection(BlockFace blockFace, boolean b) {
        return false;
    }

    @Override
    public void setFacingDirection(BlockFace blockFace) {
        facing = HangingFace.getByBlockFace(blockFace);

    }

    @Override
    public void pulse() {
        super.pulse();

        if (ticksLived % 11 == 0) {

            if (location.getBlock().getRelative(getAttachedFace()).getType() == Material.AIR) {
                world.dropItemNaturally(location, new ItemStack(Material.ITEM_FRAME));
                remove();
            }
        }
    }

    @Override
    protected void pulsePhysics() {
        // item frames aren't affected by physics
    }
}
