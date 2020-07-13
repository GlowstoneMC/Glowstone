package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.UpdateCommandBlockMinecartMessage;

import java.io.IOException;

public final class UpdateCommandBlockMinecartCodec implements Codec<UpdateCommandBlockMinecartMessage> {
    @Override
    public UpdateCommandBlockMinecartMessage decode(ByteBuf byteBuf) throws IOException {
        int entityID = ByteBufUtils.readVarInt(byteBuf);
        String command = ByteBufUtils.readUTF8(byteBuf);
        boolean trackOutput = byteBuf.readBoolean();
        return new UpdateCommandBlockMinecartMessage(entityID, command, trackOutput);
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, UpdateCommandBlockMinecartMessage message) throws IOException {
        ByteBufUtils.writeVarInt(byteBuf, message.getEntityID());
        ByteBufUtils.writeUTF8(byteBuf, message.getCommand());
        byteBuf.writeBoolean(message.isTrackOutput());
        return byteBuf;
    }
}
