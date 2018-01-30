package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import net.glowstone.util.nbt.CompoundTag;

@Data
public final class UpdateBlockEntityMessage implements Message {

    private final int x;
    private final int y;
    private final int z;
    private final int action;
    private final CompoundTag nbt;

}
