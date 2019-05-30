package net.glowstone.net;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.glowstone.GlowServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

public class GameServerTest {

    private static final InetSocketAddress LOCALHOST_IPV4 =
            InetSocketAddress.createUnresolved("127.0.0.1", 25565);
    @Mock private GlowServer glowServer;
    private Logger logger;
    private Logger unspiedLogger;
    private GameServer gameServer;

    @BeforeClass
    public void setUpClass() {
        unspiedLogger = GlowServer.logger;
        logger = Mockito.spy(unspiedLogger);
        Whitebox.setInternalState(null, "logger", logger,
                GlowServer.class);
    }

    @AfterClass
    public void tearDownClass() {
        Whitebox.setInternalState(null, "logger", unspiedLogger,
                GlowServer.class);
    }

    @BeforeEach
    public void setUp() {
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