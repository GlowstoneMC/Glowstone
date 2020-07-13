package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.UpdateStructureBlockMessage;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public final class UpdateStructureBlockCodec implements Codec<UpdateStructureBlockMessage> {
    @Override
    public UpdateStructureBlockMessage decode(ByteBuf byteBuf) throws IOException {
        BlockVector location = GlowBufUtils.readBlockPosition(byteBuf);
        int action = ByteBufUtils.readVarInt(byteBuf);
        int mode = ByteBufUtils.readVarInt(byteBuf);
        String name = ByteBufUtils.readUTF8(byteBuf);
        byte offsetX = byteBuf.readByte();
        byte offsetY = byteBuf.readByte();
        byte offsetZ = byteBuf.readByte();
        byte sizeX = byteBuf.readByte();
        byte sizeY = byteBuf.readByte();
        byte sizeZ = byteBuf.readByte();
        int mirror = ByteBufUtils.readVarInt(byteBuf);
        int rotation = ByteBufUtils.readVarInt(byteBuf);
        String metadata = ByteBufUtils.readUTF8(byteBuf);
        float integrity = byteBuf.readFloat();
        long seed = ByteBufUtils.readVarLong(byteBuf);
        byte flags = byteBuf.readByte();
        boolean ignoreEntities = (flags & 1) == 1;
        boolean showAir = ((flags >> 1) & 1) == 1;
        boolean showBoundingBox = ((flags >> 3) & 1) == 1;
        return new UpdateStructureBlockMessage(
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ(),
                action,
                mode,
                name,
                offsetX,
                offsetY,
                offsetZ,
                sizeX,
                sizeY,
                sizeZ,
                mirror,
                rotation,
                metadata,
                integrity,
                seed,
                ignoreEntities,
                showAir,
                showBoundingBox
        );
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, UpdateStructureBlockMessage message) throws IOException {
        GlowBufUtils.writeBlockPosition(byteBuf, message.getX(), message.getY(), message.getZ());
        ByteBufUtils.writeVarInt(byteBuf, message.getAction());
        ByteBufUtils.writeVarInt(byteBuf, message.getMode());
        ByteBufUtils.writeUTF8(byteBuf, message.getName());
        byteBuf.writeByte(message.getOffsetX());
        byteBuf.writeByte(message.getOffsetY());
        byteBuf.writeByte(message.getOffsetZ());
        byteBuf.writeByte(message.getSizeX());
        byteBuf.writeByte(message.getSizeY());
        byteBuf.writeByte(message.getSizeZ());
        ByteBufUtils.writeVarInt(byteBuf, message.getMirror());
        ByteBufUtils.writeVarInt(byteBuf, message.getRotation());
        ByteBufUtils.writeUTF8(byteBuf, message.getMetadata());
        byteBuf.writeFloat(message.getIntegrity());
        ByteBufUtils.writeVarLong(byteBuf, message.getSeed());
        byte ignoreEntities = booleanToByte(message.isIgnoreEntities());
        byte showAir = booleanToByte(message.isShowAir());
        byte showBoundingBox = booleanToByte(message.isShowBoundingBox());
        byteBuf.writeByte(ignoreEntities | showAir | showBoundingBox);
        return byteBuf;
    }

    private static byte booleanToByte(boolean b) {
        if (b)
            return 1;
        else
            return 0;
    }
}
