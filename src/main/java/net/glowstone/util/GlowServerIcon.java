package net.glowstone.util;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import org.apache.commons.lang.Validate;
import org.bukkit.util.CachedServerIcon;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * A {@link CachedServerIcon} implementation.
 */
public final class GlowServerIcon implements CachedServerIcon {

    /**
     * The image data to be sent to the client, or null.
     */
    private final String data;

    /**
     * Create an empty icon.
     */
    public GlowServerIcon() {
        data = null;
    }

    /**
     * Create icon from a file.
     * @param file The file to load from.
     */
    public GlowServerIcon(File file) throws Exception {
        this(ImageIO.read(file));
    }

    /**
     * Create icon from an image.
     * @param image The image to load from.
     */
    public GlowServerIcon(BufferedImage image) throws Exception {
        Validate.notNull(image, "Image must not be null");
        Validate.isTrue(image.getWidth() == 64, "Must be 64 pixels wide");
        Validate.isTrue(image.getHeight() == 64, "Must be 64 pixels high");

        ByteBuf png = Unpooled.buffer();
        ImageIO.write(image, "PNG", new ByteBufOutputStream(png));
        ByteBuf encoded = Base64.encode(png);

        data = "data:image/png;base64," + encoded.toString(Charsets.UTF_8);
    }

    /**
     * The image data to be sent to the client, or null.
     */
    public String getData() {
        return data;
    }

}
