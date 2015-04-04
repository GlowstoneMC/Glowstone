package net.minecraft.network.handshake.client;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class C00Handshake implements Packet {
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

    @Override
    public String getChannelName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PacketBuffer getBufferData() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public boolean hasFMLMarker() {
        return true;
    }

    public EnumConnectionState getRequestedState() {
        return PLAY;
    }
}
