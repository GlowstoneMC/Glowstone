package net.minecraft.network.play.server;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class S3FPacketCustomPayload implements Packet {

    public S3FPacketCustomPayload() {

    }

    public S3FPacketCustomPayload(String channel, PacketBuffer payload) {

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
