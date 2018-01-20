package net.glowstone.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import lombok.Getter;
import org.bukkit.util.CachedServerIcon;

/**
 * A {@link CachedServerIcon} implementation.
 */
public final class GlowServerIcon implements CachedServerIcon {

    /**
     * The image data to be sent to the client, or null.
     */
    @Getter
    private final String data;

    /**
     * Create an empty icon.
     */
    public GlowServerIcon() {
        data = null;
    }

    /**
     * Create icon from a file.
     *
     * @param file The file to load from.
     * @throws Exception if the file cannot be read.
     */
    public GlowServerIcon(File file) throws Exception {
        this(ImageIO.read(file));
    }

    /**
     * Create icon from an image.
     *
     * @param image The image to load from.
     * @throws Exception if the image cannot be read, or is not the correct size
     */
    public GlowServerIcon(BufferedImage image) throws Exception {
        checkNotNull(image, "Image must not be null");
        checkArgument(image.getWidth() == 64, "Must be 64 pixels wide");
        checkArgument(image.getHeight() == 64, "Must be 64 pixels high");

        ByteBuf png = Unpooled.buffer();
        ImageIO.write(image, "PNG", new ByteBufOutputStream(png));
        ByteBuf encoded = Base64.encode(png);
        png.release();

        data = "data:image/png;base64," + encoded.toString(Charsets.UTF_8);
        encoded.release();
    }
}
