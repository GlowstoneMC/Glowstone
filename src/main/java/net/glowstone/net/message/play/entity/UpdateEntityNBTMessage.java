package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import net.glowstone.util.nbt.CompoundTag;

public final class UpdateEntityNBTMessage implements Message {

    private final int entityID;
    private final CompoundTag tag;

    public UpdateEntityNBTMessage(int entityID, CompoundTag tag) {
        this.entityID = entityID;
        this.tag = tag;
    }

    public int getEntityID() {
        return entityID;
    }

    public CompoundTag getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return "UpdateEntityNBTMessage{" +
                "entityID=" + entityID +
                ", tag=" + tag +
                '}';
    }
}
