package net.glowstone.advancement;

import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.Objects;
import lombok.Data;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.util.TextMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;


@Data
public class GlowAdvancementDisplay {
    /**
     * The title for this advancement
     */
    private final TextMessage title;
    /**
     * The description for this advancement
     */
    private final TextMessage description;
    /**
     * The icon to represent this advancement
     */
    private final ItemStack icon;
    /**
     * The type of frame for the icon
     */
    private final FrameType type;
    /**
     * The optional directory for the background to use in this advancement tab (used only for the root advancement)
     */
    private final NamespacedKey background;
    /**
     * The x coordinate of the advancement
     */
    private final float x;
    /**
     * The y coordinate of the advancement
     */
    private final float y;

    private static final int HAS_BACKGROUND_TEXTURE = 0x1;
    private static final int SHOW_TOAST = 0x2;
    private static final int HIDDEN = 0x4;

    /**
     * Writes this notification to the given {@link ByteBuf}.
     *
     * @param buf the buffer to write to
     * @param hasBackgroundTexture Whether the advancement notification has a background texture
     * @param showToast Whether or not to show the toast pop up after completing this advancement
     * @param hidden Whether or not to hide this advancement and all its children from the advancement screen until this advancement have been completed
     * @return {@code buf}, with this notification written to it
     * @throws IOException if a string is too long
     */
    public ByteBuf encode(ByteBuf buf, boolean hasBackgroundTexture, boolean showToast, boolean hidden) throws IOException {
        int flags = (hasBackgroundTexture ? HAS_BACKGROUND_TEXTURE : 0)
            | (showToast ? SHOW_TOAST : 0)
            | (hidden ? HIDDEN : 0);

        GlowBufUtils.writeChat(buf, title);
        GlowBufUtils.writeChat(buf, description);
        GlowBufUtils.writeSlot(buf, icon);
        ByteBufUtils.writeVarInt(buf, type.ordinal());
        buf.writeInt(flags);
        ByteBufUtils.writeUTF8(buf, Objects.toString(background, null));
        buf.writeFloat(x);
        buf.writeFloat(y);
        return buf;
    }

    public enum FrameType {
        /**
         * Normal Tile
         */
        TASK,
        /**
         * A tile with a more fancy spiked border, used for the kill all mobs advancement
         */
        CHALLENGE,
        /**
         * A tile with a rounded border, used for the full beacon advancement
         */
        GOAL
    }
}
