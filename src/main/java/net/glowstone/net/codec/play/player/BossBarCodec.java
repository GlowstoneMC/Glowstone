package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.player.BossBarPacket;
import net.glowstone.net.message.play.player.BossBarPacket.Action;
import net.glowstone.net.message.play.player.BossBarPacket.Color;
import net.glowstone.net.message.play.player.BossBarPacket.Division;
import net.glowstone.util.TextMessage;

import java.io.IOException;
import java.util.UUID;

public class BossBarCodec implements Codec<BossBarPacket> {

    @Override
    public BossBarPacket decode(ByteBuf buffer) throws IOException {
        UUID uuid = GlowBufUtils.readUuid(buffer);
        Action action = Action.fromInt(ByteBufUtils.readVarInt(buffer));

        switch (action) {
            case ADD:
                TextMessage title = GlowBufUtils.readChat(buffer);
                float health = buffer.readFloat();
                Color color = Color.fromInt(ByteBufUtils.readVarInt(buffer));
                Division division = Division.fromInt(ByteBufUtils.readVarInt(buffer));
                byte flags = buffer.readByte();
                return new BossBarPacket(uuid, action, title, health, color, division, flags);
            case REMOVE:
                return new BossBarPacket(uuid, action);
            case UPDATE_HEALTH:
                health = buffer.readFloat();
                return new BossBarPacket(uuid, action, health);
            case UPDATE_TITLE:
                title = GlowBufUtils.readChat(buffer);
                return new BossBarPacket(uuid, action, title);
            case UPDATE_STYLE:
                color = Color.fromInt(ByteBufUtils.readVarInt(buffer));
                division = Division.fromInt(ByteBufUtils.readVarInt(buffer));
                return new BossBarPacket(uuid, action, color, division);
            case UPDATE_FLAGS:
                flags = buffer.readByte();
                return new BossBarPacket(uuid, action, flags);
        }

        //INFO: This return is dead code. We would NPE before on the action line.
        return null;
    }

    @Override
    public ByteBuf encode(ByteBuf buf, BossBarPacket message) throws IOException {
        GlowBufUtils.writeUuid(buf, message.getUuid());
        ByteBufUtils.writeVarInt(buf, message.getAction().ordinal());

        switch (message.getAction()) {
            case ADD:
                GlowBufUtils.writeChat(buf, message.getTitle());
                buf.writeFloat(message.getHealth());
                ByteBufUtils.writeVarInt(buf, message.getColor().ordinal());
                ByteBufUtils.writeVarInt(buf, message.getDivision().ordinal());
                buf.writeByte(message.getFlags());
                break;
            case UPDATE_HEALTH:
                buf.writeFloat(message.getHealth());
                break;
            case UPDATE_TITLE:
                GlowBufUtils.writeChat(buf, message.getTitle());
                break;
            case UPDATE_STYLE:
                ByteBufUtils.writeVarInt(buf, message.getColor().ordinal());
                ByteBufUtils.writeVarInt(buf, message.getDivision().ordinal());
                break;
            case UPDATE_FLAGS:
                buf.writeByte(message.getFlags());
                break;
        }
        return buf;
    }


}
