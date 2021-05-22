package net.glowstone.block.entity.state;

import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.JukeboxEntity;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Jukebox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class GlowJukebox extends GlowBlockState implements Jukebox {

    @Getter
    @Setter
    private ItemStack record;

    /**
     * Creates a block state for the given jukebox block.
     *
     * @param block the jukebox block
     */
    public GlowJukebox(GlowBlock block) {
        super(block);
        if (block.getType() != Material.JUKEBOX) {
            throw new IllegalArgumentException(
                "GlowJukebox: expected JUKEBOX, got " + block.getType());
        }
        record = getBlockEntity().getPlaying();
    }

    private JukeboxEntity getBlockEntity() {
        return (JukeboxEntity) getBlock().getBlockEntity();
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result) {
            getBlockEntity().setPlaying(record);
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Implementation

    @Override
    public Material getPlaying() {
        return record.getType();
    }

    @Override
    public boolean isPlaying() {
        return getRawData() == 1;
    }

    @Override
    public void setPlaying(Material record) {
        int id = 0;
        if (record == null || record == Material.AIR) {
            this.record = null;
        } else {
            this.record = new ItemStack(record);
            id = record.getId();
        }
        Collection<GlowPlayer> players = getWorld().getRawPlayers();
        for (GlowPlayer player : players) {
            player.playEffect(getLocation(), Effect.RECORD_PLAY, id);
        }
        setRawData((byte) (id > 0 ? 1 : 0));
    }

    @Override
    public void stopPlaying() {
        setPlaying(null);
    }

    @Override
    public boolean eject() {
        if (isPlaying()) {
            getWorld().dropItemNaturally(getLocation(), record);
            setPlaying(null);
            return true;
        }
        return false;
    }

    @Override
    public @NotNull PersistentDataContainer getPersistentDataContainer() {
        throw new UnsupportedOperationException();
    }
}
