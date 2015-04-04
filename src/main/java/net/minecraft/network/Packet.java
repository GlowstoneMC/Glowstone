package net.minecraft.network;

import java.io.IOException;

public interface Packet {
    void readPacketData(PacketBuffer packetbuffer) throws IOException;

    void writePacketData(PacketBuffer packetbuffer) throws IOException;

    void processPacket(INetHandler inethandler);

    String getChannelName();

    PacketBuffer getBufferData();
}
