package net.minecraft.network;

import io.netty.channel.Channel;
import io.netty.util.concurrent.GenericFutureListener;
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

    public void sendPacket(Packet packet, GenericFutureListener futureListener) {

    }

    public EnumPacketDirection getDirection() {
        return EnumPacketDirection.SERVERBOUND;
    }
}
