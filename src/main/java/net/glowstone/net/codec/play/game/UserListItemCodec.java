package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.entity.meta.PlayerProperty;
import net.glowstone.net.message.play.game.UserListItemMessage;

import java.io.IOException;
import java.util.List;

public final class UserListItemCodec implements Codec<UserListItemMessage> {
    public UserListItemMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode UserListItemMessage");
    }

    public ByteBuf encode(ByteBuf buf, UserListItemMessage message) throws IOException {
        final UserListItemMessage.Action action = message.getAction();
        final List<UserListItemMessage.Entry> entries = message.getEntries();
        ByteBufUtils.writeVarInt(buf, message.getAction().ordinal());
        ByteBufUtils.writeVarInt(buf, entries.size());

        for (UserListItemMessage.Entry entry : entries) {
            ByteBufUtils.writeUTF8(buf, entry.uuid.toString());

            // todo: implement the rest of the actions
            switch (action) {
                case ADD_PLAYER:
                    // this code is somewhat saddening
                    UserListItemMessage.AddEntry addEntry = (UserListItemMessage.AddEntry) entry;
                    ByteBufUtils.writeUTF8(buf, addEntry.profile.getName());
                    ByteBufUtils.writeVarInt(buf, addEntry.profile.getProperties().size());
                    for (PlayerProperty property : addEntry.profile.getProperties()) {
                        ByteBufUtils.writeUTF8(buf, property.getName());
                        ByteBufUtils.writeUTF8(buf, property.getValue());
                        if (property.getSignature() != null) {
                            buf.writeBoolean(true);
                            ByteBufUtils.writeUTF8(buf, property.getSignature());
                        } else {
                            buf.writeBoolean(false);
                        }
                    }
                    ByteBufUtils.writeVarInt(buf, addEntry.gameMode);
                    ByteBufUtils.writeVarInt(buf, addEntry.ping);
                    if (addEntry.displayName != null) {
                        buf.writeBoolean(true);
                        ByteBufUtils.writeUTF8(buf, addEntry.displayName.toJSONString());
                    } else {
                        buf.writeBoolean(false);
                    }
                    break;

                case REMOVE_PLAYER:
                    // nothing
                    break;

                default:
                    throw new UnsupportedOperationException("not yet implemented");
            }
        }
        return buf;
    }
}
