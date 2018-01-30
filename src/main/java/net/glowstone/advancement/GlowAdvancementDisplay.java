package net.glowstone.advancement;

import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import lombok.Data;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.util.TextMessage;
import org.bukkit.inventory.ItemStack;


@Data
public class GlowAdvancementDisplay {

    private final TextMessage title;
    private final TextMessage description;
    private final ItemStack icon;
    private final FrameType type;
    private final float x;
    private final float y;

    /**
     * Writes this notification to the given {@link ByteBuf}.
     *
     * @param buf the buffer to write to
     * @return {@code buf}, with this notification written to it
     * @throws IOException if a string is too long
     */
    public ByteBuf encode(ByteBuf buf) throws IOException {
        GlowBufUtils.writeChat(buf, title);
        GlowBufUtils.writeChat(buf, description);
        GlowBufUtils.writeSlot(buf, icon);
        ByteBufUtils.writeVarInt(buf, type.ordinal());
        buf.writeInt((1 << 0x4)); // todo: flags
        buf.writeFloat(x);
        buf.writeFloat(y);
        return buf;
    }

    public enum FrameType {
        TASK, CHALLENGE, GOAL
    }
}
