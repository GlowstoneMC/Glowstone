package net.minecraft.client.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class FolderResourcePack implements IResourcePack {
    @Override
    public BufferedImage getPackImage() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getPackName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected InputStream getInputStreamByName(String name) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
