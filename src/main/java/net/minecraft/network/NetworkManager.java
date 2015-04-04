package net.minecraft.network;

import io.netty.channel.Channel;
import net.minecraft.util.ChatComponentText;

public class NetworkManager {

    public INetHandler getNetHandler() {
        return null;
    }

    public void setConnectionState(EnumConnectionState state) {

    }

    public boolean isLocalChannel() {
        return false;
    }

    public void closeChannel(ChatComponentText reason) {

    }

    public Channel channel() {
        return null;
    }

    public void sendPacket(Packet packet) {

    }
}
