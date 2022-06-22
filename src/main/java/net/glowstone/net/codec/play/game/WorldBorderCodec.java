package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.WorldBorderMessage;

import java.io.IOException;

public final class WorldBorderCodec implements Codec<WorldBorderMessage> {

    @Override
    public WorldBorderMessage decode(ByteBuf buffer) throws IOException {
//        int actionId = ByteBufUtils.readVarInt(buffer);
//        Action action = Action.getAction(actionId);
//        switch (action) {
//            case SET_SIZE:
//                double radius = buffer.readDouble();
//                return new WorldBorderMessage(action, radius);
//            case LERP_SIZE:
//                double oldRadius = buffer.readDouble();
//                double newRadius = buffer.readDouble();
//                long speed = ByteBufUtils.readVarLong(buffer);
//                return new WorldBorderMessage(action, oldRadius, newRadius, speed);
//            case SET_CENTER:
//                double x = buffer.readDouble();
//                double z = buffer.readDouble();
//                return new WorldBorderMessage(action, x, z);
//            case INITIALIZE:
//                x = buffer.readDouble();
//                z = buffer.readDouble();
//                oldRadius = buffer.readDouble();
//                newRadius = buffer.readDouble();
//                speed = ByteBufUtils.readVarLong(buffer);
//                int portalTeleportBoundary = ByteBufUtils.readVarInt(buffer);
//                int warningTime = ByteBufUtils.readVarInt(buffer);
//                int warningBlocks = ByteBufUtils.readVarInt(buffer);
//                return new WorldBorderMessage(action, x, z, oldRadius, newRadius, speed,
//                    portalTeleportBoundary, warningTime, warningBlocks);
//            case SET_WARNING_TIME:
//            case SET_WARNING_BLOCKS:
//                warningTime = ByteBufUtils.readVarInt(buffer);
//                return new WorldBorderMessage(action, warningTime);
//            default:
//                throw new DecoderException(
//                    "Invalid WorldBorderMessage action " + actionId + "/" + action);
//        }
        throw new IOException("Cannot decode");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, WorldBorderMessage message) throws IOException {
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getZ());
        buf.writeDouble(message.getOldRadius());
        buf.writeDouble(message.getNewRadius());
        ByteBufUtils.writeVarLong(buf, message.getSpeed());
        ByteBufUtils.writeVarInt(buf, message.getPortalTeleportBoundary());
        ByteBufUtils.writeVarInt(buf, message.getWarningTime());
        ByteBufUtils.writeVarInt(buf, message.getWarningBlocks());
        return buf;
    }
}
