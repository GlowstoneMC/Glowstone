package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.TabCompleteMessage;
import net.glowstone.net.message.play.player.TabCompleteResponseMessage;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TabCompleteHandler implements MessageHandler<GlowSession, TabCompleteMessage> {
    @Override
    public void handle(GlowSession session, TabCompleteMessage message) {
        final Player sender = session.getPlayer();
        final String buffer = message.getText();
        final List<String> completions = new ArrayList<>();

        // complete command or username
        if (buffer.startsWith("/")) {
            List<String> items = session.getServer().getCommandMap().tabComplete(sender, buffer.substring(1));
            if (items != null) {
                completions.addAll(items);
            }
        } else {
            int space = buffer.lastIndexOf(' ');
            String lastWord;
            if (space == -1) {
                lastWord = buffer;
            } else {
                lastWord = buffer.substring(space + 1);
            }

            // from Command
            for (Player player : session.getServer().getOnlinePlayers()) {
                String name = player.getName();
                if (sender.canSee(player) && StringUtil.startsWithIgnoreCase(name, lastWord)) {
                    completions.add(name);
                }
            }
            Collections.sort(completions, String.CASE_INSENSITIVE_ORDER);
        }

        // call event and send response
        EventFactory.onPlayerTabComplete(sender, buffer, completions);
        session.send(new TabCompleteResponseMessage(completions));
    }
}
