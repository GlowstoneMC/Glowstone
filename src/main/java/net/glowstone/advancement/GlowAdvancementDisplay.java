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

    private final TextMessage title, description;
    private final ItemStack icon;
    private final FrameType type;
    private final float x, y;

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
