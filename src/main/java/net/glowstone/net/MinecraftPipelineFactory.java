package net.glowstone.net;

import net.glowstone.GlowServer;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.StaticChannelPipeline;

/**
 * A {@link ChannelPipelineFactory} for the Minecraft protocol.
 * @author Graham Edgecombe
 */
public final class MinecraftPipelineFactory implements ChannelPipelineFactory {

    /**
     * The server.
     */
    private final GlowServer server;

    /**
     * Creates a new Minecraft pipeline factory.
     * @param server The server.
     */
    public MinecraftPipelineFactory(GlowServer server) {
        this.server = server;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        return new StaticChannelPipeline(
            new MinecraftDecoder(),
            new MinecraftEncoder(),
            new MinecraftHandler(server)
        );
    }

}
