package net.glowstone.net;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
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
    private final List<LogRecord> logRecords = new ArrayList<>();
    private final Handler HANDLER = new Handler() {
        @Override
        public void publish(LogRecord logRecord) {
            logRecords.add(logRecord);
        }

        @Override
        public void flush() {
            // No-op.
        }

        @Override
        public void close() {
            // No-op.
        }
    };

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
        GlowServer.logger.removeHandler(HANDLER);
    }

    @BeforeEach
    public void setUp() throws IllegalAccessException {
        GlowServer.logger.addHandler(HANDLER);
        glowServer = Mockito.mock(GlowServer.class, Answers.RETURNS_SMART_NULLS);
        CountDownLatch latch = new CountDownLatch(1);
        gameServer = new GameServer(glowServer, latch);
        latch.countDown();
    }

    private static void setLogger(Logger logger) throws IllegalAccessException {
        Field field = Whitebox.getField(GlowServer.class, "logger");
        field.setAccessible(true);
        field.set(null, logger);
    }

    @Test
    public void testOnBindSuccess() throws UnknownHostException {
        gameServer.onBindSuccess(LOCALHOST_IPV4);
        assertLoggedExactlyOnce("Successfully bound server to 127.0.0.1:25565.", Level.INFO);
    }

    @Test
    public void testOnBindFailure() {
        gameServer.onBindFailure(LOCALHOST_IPV4, new ConnectException("Exception-handling test"));
        assertLoggedExactlyOnce("Failed to bind server to 127.0.0.1:25565.", Level.SEVERE);
    }

    private void assertLoggedExactlyOnce(String expectedMessage, Level expectedLevel) {
        int matches = 0;
        for (LogRecord logRecord : logRecords) {
            if (logRecord.getMessage().equals(expectedMessage)) {
                matches++;
                assertEquals(expectedLevel, logRecord.getLevel());
            }
        }
        assertEquals(1, matches);
    }
}