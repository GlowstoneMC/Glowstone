package net.glowstone.command.minecraft;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.powermock.api.mockito.PowerMockito.when;

import com.google.common.collect.ImmutableList;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.command.CommandUtils;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.hamcrest.MatcherAssert;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@PrepareForTest({Bukkit.class, CommandUtils.class})
public abstract class CommandTest<T extends Command> {
    protected CommandSender sender;
    protected CommandSender opSender;
    protected T command;
    protected final Supplier<T> commandSupplier;

    protected CommandTest(Supplier<T> commandSupplier) {
        this.commandSupplier = commandSupplier;
    }

    @Test
    public void testExecuteFailsWithoutPermission() {
        MatcherAssert.assertThat(command.execute(sender, "label", new String[0]), is(false));
        Mockito.verify(sender).sendMessage(eq(ChatColor.RED
            + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error."));
    }

    @BeforeMethod
    public void before() {
        command = commandSupplier.get();
        sender = PowerMockito.mock(CommandSender.class);
        opSender = PowerMockito.mock(CommandSender.class);
        Mockito.when(opSender.hasPermission(Mockito.anyString())).thenReturn(true);
    }

    protected GlowPlayer[] prepareMockPlayers(Location location, @Nullable GlowServer server,
            @Nullable GlowWorld world, String... names) {
        Assert.assertFalse(names.length == 0, "prepareMockPlayers called with no names!");
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
