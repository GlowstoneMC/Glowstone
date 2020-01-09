package net.glowstone.net;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.protocol.HandshakeProtocol;
import net.glowstone.net.protocol.LoginProtocol;
import net.glowstone.net.protocol.PlayProtocol;
import net.glowstone.net.protocol.ProtocolProvider;
import net.glowstone.net.protocol.StatusProtocol;
import net.glowstone.net.query.QueryHandler;
import net.glowstone.net.query.QueryServer;
import net.glowstone.util.Convert;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Tests for the minecraft query server.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(QueryServer.class)
public class QueryTest {

    /**
     * Test values taken from <a href="http://wiki.vg/Query">wiki.vg/Query</a>
     */
    private static final byte[] HANDSHAKE_RECV = Convert.fromHex("FEFD0900000001");
    private static final byte[] HANDSHAKE_SEND = Convert.fromHex("09000000013935313333303700");
    private static final byte[] BASIC_STATS_RECV = Convert.fromHex("FEFD00000000010091295B");
    private static final byte[] BASIC_STATS_SEND = Convert.fromHex(
        "000000000141204D696E6563726166742053657276657200534D5000776F726C64003200323000DD633132372E302E302E3100");
    private static final byte[] FULL_STATS_RECV = Convert.fromHex("FEFD00000000010091295B00000000");
    private static final byte[] FULL_STATS_SEND = Convert
        .fromHex("000000000173706C69746E756D008000686F73746E616D65004120"
            + "4D696E656372616674205365727665720067616D657479706500534D500067616D655F6964004D494E4543524146540076657273696F6E00"
            + ByteBufUtil
            .hexDump(Unpooled.wrappedBuffer(GlowServer.GAME_VERSION.getBytes(CharsetUtil.UTF_8)))
            // Always use the recent game version
            + "00706C7567696E7300"
            + ByteBufUtil.hexDump(
            Unpooled.wrappedBuffer("Glowstone 123 on Bukkit xyz".getBytes(CharsetUtil.UTF_8)))
            // Added, the original example did not use the 'plugin' key
            + "006D617000776F726C64006E756D706C61796572730032006D6178706C617965727300323000686F7374706F727400323535363500686F73"
            + "746970003132372E302E302E31000001706C617965725F00006261726E657967616C6500566976616C6168656C7669670000");

    private GlowServer glowServer;
    private QueryServer server;
    private ThreadLocalRandom random;
    private InetSocketAddress address;
    private boolean queryPlugins;

    @Before
    public void setup() throws Exception {
        glowServer = mock(GlowServer.class);
        CountDownLatch latch = new CountDownLatch(1);
        ProtocolProvider protocolProvider = new ProtocolProvider(
            mock(HandshakeProtocol.class),
            mock(StatusProtocol.class),
            mock(LoginProtocol.class),
            mock(PlayProtocol.class)
        );
        this.queryPlugins = true;
        server = new QueryServer(glowServer, protocolProvider, latch, queryPlugins);
        random = mock(ThreadLocalRandom.class);
        PowerMockito.mockStatic(ThreadLocalRandom.class);
        when(ThreadLocalRandom.current()).thenReturn(random);
        address = InetSocketAddress.createUnresolved("somehost", 12345);
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void testChallengeTokens() throws Exception {
        assertThat("Accepted random challenge token.", server.verifyChallengeToken(address, 54321),
            is(false));

        when(random.nextInt()).thenReturn(12345);
        int token1 = server.generateChallengeToken(address);
        assertThat("Did not add challenge token.", server.verifyChallengeToken(address, token1),
            is(true));

        when(random.nextInt()).thenReturn(6789);
        int token2 = server.generateChallengeToken(address);
        assertThat("Expired token accepted.", server.verifyChallengeToken(address, token1),
            is(false));
        assertThat("Did not add challenge token.", server.verifyChallengeToken(address, token2),
            is(true));

        server.flushChallengeTokens();
        assertThat("Flush did not remove token.", server.verifyChallengeToken(address, token2),
            is(false));
    }

    @Test
    public void testHandshake() throws Exception {
        QueryHandler handler = new QueryHandler(server, queryPlugins);
        when(random.nextInt()).thenReturn(9513307);
        testChannelRead(handler, HANDSHAKE_RECV, HANDSHAKE_SEND);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void testBasicStats() throws Exception {
        World world = mock(World.class);
        when(world.getName()).thenReturn("world");
        when(glowServer.getMotd()).thenReturn("A Minecraft Server");
        when(glowServer.getOnlinePlayers())
            .thenReturn((List) Arrays.asList(new Object(), new Object()));
        when(glowServer.getMaxPlayers()).thenReturn(20);
        when(glowServer.getPort()).thenReturn(25565);
        when(glowServer.getWorlds()).thenReturn(Arrays.asList(world));
        when(glowServer.getIp()).thenReturn("");

        QueryHandler handler = new QueryHandler(server, queryPlugins);

        when(random.nextInt()).thenReturn(9513307);
        server.generateChallengeToken(address);

        testChannelRead(handler, BASIC_STATS_RECV, BASIC_STATS_SEND);
    }

    @Test
    public void testFullStats() throws Exception {
        World world = mock(World.class);
        when(world.getName()).thenReturn("world");
        GlowPlayer p1 = mock(GlowPlayer.class);
        GlowPlayer p2 = mock(GlowPlayer.class);
        when(p1.getName()).thenReturn("barneygale");
        when(p2.getName()).thenReturn("Vivalahelvig");
        PluginManager pluginManager = mock(PluginManager.class);
        when(glowServer.getMotd()).thenReturn("A Minecraft Server");
        Mockito.doReturn(Arrays.asList(p1, p2)).when(glowServer).getOnlinePlayers();
        when(glowServer.getMaxPlayers()).thenReturn(20);
        when(glowServer.getPort()).thenReturn(25565);
        when(glowServer.getWorlds()).thenReturn(Arrays.asList(world));
        when(glowServer.getIp()).thenReturn("");
        when(glowServer.getVersion()).thenReturn("123");
        when(glowServer.getBukkitVersion()).thenReturn("xyz");
        when(glowServer.getPluginManager()).thenReturn(pluginManager);
        when(pluginManager.getPlugins()).thenReturn(new Plugin[0]);

        QueryHandler handler = new QueryHandler(server, queryPlugins);

        when(random.nextInt()).thenReturn(9513307);
        server.generateChallengeToken(address);

        testChannelRead(handler, FULL_STATS_RECV, FULL_STATS_SEND);
    }

    private void testChannelRead(QueryHandler handler, byte[] recv, byte[] send) throws Exception {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        ByteBufAllocator alloc = mock(ByteBufAllocator.class);
        when(ctx.alloc()).thenReturn(alloc);
        when(alloc.buffer()).thenReturn(Unpooled.buffer());

        DatagramPacket packet = new DatagramPacket(Unpooled.wrappedBuffer(recv), null, address);
        handler.channelRead(ctx, packet);
        verify(ctx).write(argThat(new DatagramPacketMatcher(send)));
    }

    /**
     * Matches the content (nothing else) of two {@link DatagramPacket}s.
     */
    private static class DatagramPacketMatcher implements ArgumentMatcher<DatagramPacket> {

        private final byte[] content;

        public DatagramPacketMatcher(byte[] content) {
            this.content = content;
        }

        @Override
        public boolean matches(DatagramPacket obj) {
            ByteBuf buf = obj.content();
            byte[] array = new byte[buf.readableBytes()];
            buf.readBytes(array);
            buf.readerIndex(buf.readerIndex() - array.length);
            return Arrays.equals(array, content);
        }
    }
}
