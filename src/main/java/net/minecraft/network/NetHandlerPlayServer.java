package net.minecraft.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class NetHandlerPlayServer implements INetHandler {

    public NetHandlerPlayServer(MinecraftServer minecraftServer, NetworkManager networkManager, EntityPlayerMP entityPlayerMP) {

    }

    public NetworkManager getNetworkManager() {
        return null;
    }

    public void update() {

    }
}
