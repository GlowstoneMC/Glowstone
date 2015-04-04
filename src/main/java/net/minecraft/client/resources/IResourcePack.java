package net.minecraft.client.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public interface IResourcePack {

    BufferedImage getPackImage() throws IOException;

    String getPackName();

    //InputStream getInputStreamByName(String name) throws IOException;

    //boolean hasResourceName(String p_110593_1_);
}
