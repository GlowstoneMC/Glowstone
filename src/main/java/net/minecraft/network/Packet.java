package net.minecraft.network;

import java.io.IOException;

public class Packet {

    public Packet(String string, PacketBuffer buffer)
    {
    }

    public PacketBuffer getBufferData() {
        return null;
    }

    public String getChannelName() {
        return null;
    }

    public void readPacketData(PacketBuffer packetbuffer) throws IOException
    {
    }

    public void writePacketData(PacketBuffer packetbuffer) throws IOException
    {
    }

    public void processPacket(INetHandler inethandler)
    {
    }
}
