package net.glowstone.advancement;

import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.util.TextMessage;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;


@Data
public class GlowAdvancementDisplay {

    private final TextMessage title, description;
    private final ItemStack icon;
    private final List<Flags> flags;
    private final String background;
    private final FrameType type;
    private final float x, y;

    public ByteBuf encode(ByteBuf buf) throws IOException {
        GlowBufUtils.writeChat(buf, title);
        GlowBufUtils.writeChat(buf, description);
        GlowBufUtils.writeSlot(buf, icon);
        ByteBufUtils.writeVarInt(buf, type.ordinal());
        int f = 0;
        for (Flags flag : flags) {
            f |= (1 << flag.getFlag());  // getFlag() can be 0x1, 0x2, 0x4
        }
        buf.writeInt(f);
        if (flags.contains(Flags.BACKGROUND) && background != null) {
            ByteBufUtils.writeUTF8(buf, background);
        }
        buf.writeFloat(x);
        buf.writeFloat(y);
        return buf;
    }

    public enum FrameType {
        TASK, CHALLENGE, GOAL
    }

    public enum Flags {
        BACKGROUND(0x1), TOAST(0x2), HIDDEN(0x4);

        private final int flag;

        Flags(int flag) {
            this.flag = flag;
        }

        public int getFlag() {
            return flag;
        }
    }
}
