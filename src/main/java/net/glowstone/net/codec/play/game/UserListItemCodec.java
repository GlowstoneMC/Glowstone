package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.entity.meta.profile.PlayerProperty;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.PlayerListItemPacket;
import net.glowstone.net.message.play.game.PlayerListItemPacket.Action;
import net.glowstone.net.message.play.game.PlayerListItemPacket.Entry;

import java.io.IOException;
import java.util.List;

public final class UserListItemCodec implements Codec<PlayerListItemPacket> {
    @Override
    public PlayerListItemPacket decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode UserListItemMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PlayerListItemPacket message) throws IOException {
        Action action = message.getAction();
        List<Entry> entries = message.getEntries();
        ByteBufUtils.writeVarInt(buf, message.getAction().ordinal());
        ByteBufUtils.writeVarInt(buf, entries.size());

        for (Entry entry : entries) {
            GlowBufUtils.writeUuid(buf, entry.uuid);

            // todo: implement the rest of the actions
            switch (action) {
                case ADD_PLAYER:
                    // this code is somewhat saddening
                    ByteBufUtils.writeUTF8(buf, entry.profile.getName());
                    ByteBufUtils.writeVarInt(buf, entry.profile.getProperties().size());
                    for (PlayerProperty property : entry.profile.getProperties()) {
                        ByteBufUtils.writeUTF8(buf, property.getName());
                        ByteBufUtils.writeUTF8(buf, property.getValue());
                        if (property.getSignature() != null) {
                            buf.writeBoolean(true);
                            ByteBufUtils.writeUTF8(buf, property.getSignature());
                        } else {
                            buf.writeBoolean(false);
                        }
                    }
                    ByteBufUtils.writeVarInt(buf, entry.gameMode);
                    ByteBufUtils.writeVarInt(buf, entry.ping);
                    if (entry.displayName != null) {
                        buf.writeBoolean(true);
                        GlowBufUtils.writeChat(buf, entry.displayName);
                    } else {
                        buf.writeBoolean(false);
                    }
                    break;

                case UPDATE_GAMEMODE:
                    ByteBufUtils.writeVarInt(buf, entry.gameMode);
                    break;

                case UPDATE_LATENCY:
                    ByteBufUtils.writeVarInt(buf, entry.ping);
                    break;

                case UPDATE_DISPLAY_NAME:
                    if (entry.displayName != null) {
                        buf.writeBoolean(true);
                        GlowBufUtils.writeChat(buf, entry.displayName);
                    } else {
                        buf.writeBoolean(false);
                    }
                    break;

                case REMOVE_PLAYER:
                    // nothing
                    break;

                default:
                    throw new UnsupportedOperationException("not yet implemented: " + action);
            }
        }
        return buf;
    }
}
