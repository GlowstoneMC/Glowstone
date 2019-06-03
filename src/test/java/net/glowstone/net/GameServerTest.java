package net.glowstone.net;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.glowstone.GlowServer;
import net.glowstone.util.config.ServerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;

public class GameServerTest {

    private static final Formatter LOG_FORMATTER = new Formatter() {
        @Override
        public String format(LogRecord logRecord) {
            return String.format("[%s] %s", logRecord.getLevel(), logRecord.getMessage());
        }
    };

    private static final InetSocketAddress LOCALHOST_IPV4;
    private static final InetSocketAddress LOCALHOST_IPV6;
    private final List<LogRecord> logRecords = new CopyOnWriteArrayList<>();
    private final Handler handler = new Handler() {
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

    public static final int PORT = 25565;

    static {
        try {
            LOCALHOST_IPV4 = new InetSocketAddress(
                        InetAddress.getByAddress(new byte[]{127,0,0,1}), ServerConfig.DEFAULT_PORT);
            LOCALHOST_IPV6 = new InetSocketAddress(
                    InetAddress.getByAddress(new byte[]{0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,1}),
                    ServerConfig.DEFAULT_PORT);
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
        GlowServer.logger.removeHandler(handler);
    }

    @BeforeEach
    public void setUp() throws IllegalAccessException {
        GlowServer.logger.addHandler(handler);
        glowServer = Mockito.mock(GlowServer.class, Answers.RETURNS_SMART_NULLS);
        CountDownLatch latch = new CountDownLatch(1);
        gameServer = new GameServer(glowServer, latch);
        latch.countDown();
    }

    @Test
    public void testOnBindSuccessIpv4() throws UnknownHostException {
        gameServer.onBindSuccess(LOCALHOST_IPV4);
        assertLoggedExactlyOnce("Successfully bound server to 127.0.0.1:25565.", Level.INFO);
    }

    @Test
    public void testOnBindSuccessIpv6() throws UnknownHostException {
        gameServer.onBindSuccess(LOCALHOST_IPV6);
        assertLoggedExactlyOnce("Successfully bound server to [0:0:0:0:0:0:0:1]:25565.", Level.INFO);
    }

    @Test
    public void testLogBindFailure() {
        gameServer.logBindFailure(LOCALHOST_IPV4, new ConnectException("Exception-handling test"));
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
        assertEquals(1, matches, () -> {
            return "Actual log entries:\n"
                    + logRecords.stream().map(LOG_FORMATTER::format).collect(Collectors.joining("\n"));
        });
    }
}