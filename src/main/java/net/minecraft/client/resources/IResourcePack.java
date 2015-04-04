package net.minecraft.client.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public interface IResourcePack {

    BufferedImage getPackImage();

    String getPackName();

    InputStream getInputStreamByName(String name) throws IOException;
}
