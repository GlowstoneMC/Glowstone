package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.entity.meta.PlayerProperty;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.UserListItemMessage;

import java.io.IOException;
import java.util.List;

public final class UserListItemCodec implements Codec<UserListItemMessage> {
    @Override
    public UserListItemMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode UserListItemMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, UserListItemMessage message) throws IOException {
        final UserListItemMessage.Action action = message.getAction();
        final List<UserListItemMessage.Entry> entries = message.getEntries();
        ByteBufUtils.writeVarInt(buf, message.getAction().ordinal());
        ByteBufUtils.writeVarInt(buf, entries.size());

        for (UserListItemMessage.Entry entry : entries) {
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
                        ByteBufUtils.writeUTF8(buf, entry.displayName.toJSONString());
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
                        ByteBufUtils.writeUTF8(buf, entry.displayName.toJSONString());
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
