package net.glowstone.net;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.glowstone.GlowServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

public class GameServerTest {

    private static final InetSocketAddress LOCALHOST_IPV4;

    static {
        try {
            LOCALHOST_IPV4 = new InetSocketAddress(
                        InetAddress.getByAddress(new byte[]{127,0,0,1}), 25565);
        } catch (UnknownHostException e) {
            throw new AssertionError(e);
        }
    }

    @Mock private GlowServer glowServer;
    private Logger logger;
    private Logger unspiedLogger;
    private GameServer gameServer;

    @AfterEach
    public void tearDownClass() throws IllegalAccessException {
        Whitebox.getField(GlowServer.class, "logger").set(null, unspiedLogger);
    }

    @BeforeEach
    public void setUp() throws IllegalAccessException {
        unspiedLogger = GlowServer.logger;
        logger = Mockito.mock(Logger.class);
        Whitebox.getField(GlowServer.class, "logger").set(null, logger);
        glowServer = Mockito.mock(GlowServer.class, Answers.RETURNS_SMART_NULLS);
        CountDownLatch latch = new CountDownLatch(1);
        gameServer = new GameServer(glowServer, latch);
        latch.countDown();
    }

    @Test
    public void testOnBindSuccess() throws UnknownHostException {
        gameServer.onBindSuccess(LOCALHOST_IPV4);
        Mockito.verify(logger).log(Level.INFO, "Successfully bound server to 127.0.0.1:25565.");
    }

    @Test
    public void testOnBindFailure() {
        gameServer.onBindFailure(LOCALHOST_IPV4, new ConnectException("Exception-handling test"));
        Mockito.verify(logger).log(Level.SEVERE, "Failed to bind server to 127.0.0.1:25565.");
    }
}