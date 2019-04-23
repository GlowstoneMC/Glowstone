package net.glowstone.command;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.powermock.api.mockito.PowerMockito.when;

import com.google.common.collect.ImmutableList;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class, CommandUtils.class})
public abstract class CommandTest<T extends Command> {
    protected CommandSender sender;
    protected CommandSender opSender;
    protected T command;
    protected final Supplier<T> commandSupplier;
    String bundleName;
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("commands");

    protected CommandTest(Supplier<T> commandSupplier) {
        this.commandSupplier = commandSupplier;
    }

    /**
     * Expects execution without permission to fail. For commands that don't require any
     * permission, this should be overridden to expect success instead.
     */
    @Test
    public void testExecuteWithoutPermission() {
        assertThat(command.execute(sender, "label", new String[0]), is(true));
        Mockito.verify(sender).sendMessage(eq(ChatColor.RED
            + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error."));
    }

    @Test
    public void testThatDescriptionAndUsageExist() {
        // TODO: Remove the "if" once all commands are converted to extend GlowVanillaCommand
        if (command instanceof GlowVanillaCommand) {
            assertNotNull(RESOURCE_BUNDLE.getString(command.getName() + ".description"));
            assertNotNull(RESOURCE_BUNDLE.getString(command.getName() + ".usage"));
        }
    }

    @Before
    public void before() {
        command = commandSupplier.get();
        sender = PowerMockito.mock(CommandSender.class);
        opSender = PowerMockito.mock(CommandSender.class);
        Mockito.when(opSender.hasPermission(Mockito.anyString())).thenReturn(true);
    }

    protected GlowPlayer[] prepareMockPlayers(Location location, @Nullable GlowServer server,
            @Nullable GlowWorld world, String... names) {
        assertFalse("prepareMockPlayers called with no names!", names.length == 0);
        PowerMockito.mockStatic(Bukkit.class);
        GlowPlayer[] players = new GlowPlayer[names.length];
        for (int i = 0; i < names.length; i++) {
            players[i] = PowerMockito.mock(GlowPlayer.class);
            when(players[i].getName()).thenReturn(names[i]);
            when(players[i].getLocation()).thenReturn(location);
            when(players[i].getType()).thenReturn(EntityType.PLAYER);
            when(Bukkit.getPlayerExact(names[i])).thenReturn(players[i]);
        }
        if (server != null) {
            Mockito.doReturn(ImmutableList.copyOf(players)).when(server).getOnlinePlayers();
        }
        if (world != null) {
            when(world.getEntities()).thenReturn(ImmutableList.copyOf(players));
        }
        return players;
    }
}
