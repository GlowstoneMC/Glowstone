package net.minecraft.network.play.server;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class S01PacketJoinGame implements Packet {
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

    public int getDimension() {
        return 0;
    }
}
