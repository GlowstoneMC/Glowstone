package net.minecraft.client.resources;

import net.minecraftforge.fml.common.ModContainer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileResourcePack implements IResourcePack {

    public FileResourcePack(File source) {

    }

    @Override
    public BufferedImage getPackImage() throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getPackName() {
        return "default";
    }

    @Override
    protected InputStream getInputStreamByName(String resourceName) throws IOException {
        return null;
    }
}
