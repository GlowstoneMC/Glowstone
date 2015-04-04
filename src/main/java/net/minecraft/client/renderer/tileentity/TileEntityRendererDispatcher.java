package net.minecraft.client.renderer.tileentity;

import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;

public class TileEntityRendererDispatcher extends TileEntityRenderer {

    public HashMap<Class <? extends TileEntity>, TileEntitySpecialRenderer> mapSpecialRenderers = new HashMap<>();

    public static final TileEntityRendererDispatcher instance = new TileEntityRendererDispatcher();
}
