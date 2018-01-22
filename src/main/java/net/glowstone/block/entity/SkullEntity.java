package net.glowstone.block.entity;

import lombok.Getter;
import lombok.Setter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.blocktype.BlockSkull;
import net.glowstone.block.entity.state.GlowSkull;
import net.glowstone.constants.GlowBlockEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.profile.GlowPlayerProfile;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.SkullType;
import org.bukkit.material.Skull;

public class SkullEntity extends BlockEntity {

    @Getter
    @Setter
    private byte type;
    @Getter
    @Setter
    private byte rotation;
    @Getter
    private GlowPlayerProfile owner;

    public SkullEntity(GlowBlock block) {
        super(block);
        setSaveId("minecraft:skull");
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        type = tag.getByte("SkullType");

        if (BlockSkull.canRotate((Skull) getBlock().getState().getData())) {
            rotation = tag.getByte("Rot");
        }
        if (tag.containsKey("Owner")) {
            CompoundTag ownerTag = tag.getCompound("Owner");
            owner = GlowPlayerProfile.fromNbt(ownerTag).join();
        } else if (tag.containsKey("ExtraType")) {
            // Pre-1.8 uses just a name, instead of a profile object
            String name = tag.getString("ExtraType");
            if (name != null && !name.isEmpty()) {
                owner = GlowPlayerProfile.getProfile(name).join();
            }
        }
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        tag.putByte("SkullType", type);
        if (BlockSkull.canRotate((Skull) getBlock().getState().getData())) {
            tag.putByte("Rot", rotation);
        }
        if (type == BlockSkull.getType(SkullType.PLAYER) && owner != null) {
            tag.putCompound("Owner", owner.toNbt());
        }
    }

    @Override
    public GlowBlockState getState() {
        return new GlowSkull(block);
    }

    @Override
    public void update(GlowPlayer player) {
        super.update(player);
        CompoundTag nbt = new CompoundTag();
        saveNbt(nbt);
        player.sendBlockEntityChange(getBlock().getLocation(), GlowBlockEntity.SKULL, nbt);
    }

    public void setOwner(GlowPlayerProfile owner) {
        this.owner = owner;
        type = BlockSkull.getType(SkullType.PLAYER);
    }
}
