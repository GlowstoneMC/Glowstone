package net.minecraft.network.login.server;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.io.IOException;

public class S00PacketDisconnect implements Packet {

    public S00PacketDisconnect(ChatComponentText chatComponent) {

    }

    @Override
    public void readPacketData(PacketBuffer packetbuffer) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writePacketData(PacketBuffer packetbuffer) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void processPacket(INetHandler inethandler) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getChannelName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PacketBuffer getBufferData() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
