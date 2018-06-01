package net.glowstone.block.entity.state;

import java.util.Collection;
import lombok.Getter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.JukeboxEntity;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Jukebox;
import org.bukkit.inventory.ItemStack;

public class GlowJukebox extends GlowBlockState implements Jukebox {

    @Getter
    private ItemStack playingItem;

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
        playingItem = getBlockEntity().getPlaying();
    }

    private JukeboxEntity getBlockEntity() {
        return (JukeboxEntity) getBlock().getBlockEntity();
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result) {
            getBlockEntity().setPlaying(playingItem);
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Implementation

    @Override
    public Material getPlaying() {
        return playingItem.getType();
    }

    @Override
    public boolean isPlaying() {
        return getRawData() == 1;
    }

    @Override
    public void setPlaying(Material record) {
        int id = 0;
        if (record == null || record == Material.AIR) {
            playingItem = null;
        } else {
            playingItem = new ItemStack(record);
            id = record.getId();
        }
        Collection<GlowPlayer> players = getWorld().getRawPlayers();
        for (GlowPlayer player : players) {
            player.playEffect(getLocation(), Effect.RECORD_PLAY, id);
        }
        setRawData((byte) (id > 0 ? 1 : 0));
    }

    @Override
    public boolean eject() {
        if (isPlaying()) {
            getWorld().dropItemNaturally(getLocation(), playingItem);
            setPlaying(null);
            return true;
        }
        return false;
    }

}
