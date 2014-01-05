package net.glowstone.net.message;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * The base class for messages.
 *
 * Message classes should also have a constructor which takes a ChannelBuffer and decodes its contents.
 */
public abstract class Message {

    /**
     * Encode this message to the given buffer.
     * @param buf the destination buffer.
     */
    public abstract void encode(ChannelBuffer buf);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{?}";
    }
}
