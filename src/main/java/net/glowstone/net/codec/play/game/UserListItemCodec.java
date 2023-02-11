package net.glowstone.net.codec.play.game;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.UserListItemMessage;
import net.glowstone.net.message.play.game.UserListItemMessage.Action;
import net.glowstone.net.message.play.game.UserListItemMessage.Entry;

import java.io.IOException;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public final class UserListItemCodec implements Codec<UserListItemMessage> {

    @Override
    public UserListItemMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode UserListItemMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, UserListItemMessage message) throws IOException {
        List<Action> actions = message.getActions();
        //Sort list so actions are added to the buf in the correct order
        Collections.sort(actions);
        List<Entry> entries = message.getEntries();
        BitSet actionBitSet = new BitSet(6);
        for (Action a : actions) {
            actionBitSet.set(a.getBitFieldIndex(), true);
        }
        byte arr[] = actionBitSet.toByteArray();
        buf.writeByte(arr[0]);
        ByteBufUtils.writeVarInt(buf, entries.size());

        for (Entry entry : entries) {
            GlowBufUtils.writeUuid(buf, entry.uuid);

            for (Action a : actions) {
                switch (a) {
                    case ADD_PLAYER:
                        // this code is somewhat saddening
                        ByteBufUtils.writeUTF8(buf, entry.profile.getName());
                        ByteBufUtils.writeVarInt(buf, entry.profile.getProperties().size());
                        for (ProfileProperty property : entry.profile.getProperties()) {
                            ByteBufUtils.writeUTF8(buf, property.getName());
                            ByteBufUtils.writeUTF8(buf, property.getValue());
                            buf.writeBoolean(property.isSigned());
                            if (property.isSigned()) {
                                ByteBufUtils.writeUTF8(buf, property.getSignature());
                            }
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
                        throw new UnsupportedOperationException("not yet implemented: " + a);
                }
            }
            // todo: implement the rest of the actions

        }
        return buf;
    }
}
