package net.minecraft.client.resources;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FolderResourcePack implements IResourcePack {
    public FolderResourcePack(File source) {

    }

    @Override
    public BufferedImage getPackImage() throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getPackName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected InputStream getInputStreamByName(String name) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected boolean hasResourceName(String p_110593_1_) {
        return true;
    }
}
