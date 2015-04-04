package net.minecraft.network;

import java.io.IOException;

public interface Packet {
    void readPacketData(PacketBuffer packetbuffer) throws IOException;

    void writePacketData(PacketBuffer packetbuffer) throws IOException;

    void processPacket(INetHandler inethandler);

    // these cannot be part of the interface because FMLProxyPacket does not override it
    //String getChannelName();

    //PacketBuffer getBufferData();
}
