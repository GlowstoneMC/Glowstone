package net.glowstone.msg.handler;

import java.util.logging.Level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.IdentificationMessage;
import net.glowstone.net.Session;
import net.glowstone.net.Session.State;

public final class IdentificationMessageHandler extends MessageHandler<IdentificationMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, IdentificationMessage message) {
        Session.State state = session.getState();
        
        // Is the player on the whitelist?
        if (session.getServer().hasWhitelist() && !session.getServer().getWhitelist().contains(message.getName())) {
            session.getServer().getLogger().log(Level.INFO, "Player {0} was not on the whitelist.", message.getName());
            session.disconnect("You're not whitelisted!");
        }
        
        // Are we at the proper stage?
        if (state == Session.State.EXCHANGE_IDENTIFICATION) {
            session.setState(State.GAME);
            boolean allow = true; // Default to okay
            
            // If we're in online mode, attempt to verify with mc.net
            if (session.getServer().getOnlineMode()) {
                allow = false;
                try {
                    URL verify = new URL("http://www.minecraft.net/game/checkserver.jsp?user=" + URLEncoder.encode(message.getName(), "UTF-8") + "&serverId=" + URLEncoder.encode(session.getSessionId(), "UTF-8"));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(verify.openStream()));
                    String result = reader.readLine();
                    reader.close();
                    allow = result.equals("YES"); // Get minecraft.net's result. If the result is YES, allow login to continue
                } catch (IOException ex) {
                    // Something went wrong, disconnect the player
                    session.getServer().getLogger().log(Level.WARNING, "Failed to authenticate {0} with minecraft.net: {1}", new Object[]{message.getName(), ex.getMessage()});
                    session.disconnect("Player identification failed [" + ex.getMessage() + "]");
                }
            }
            
            // Was the player allowed?
            if (allow) {
                session.send(new IdentificationMessage(0, "", 0, 0));
                session.setPlayer(new GlowPlayer(session, message.getName())); // TODO case-correct the name
            } else {
                session.getServer().getLogger().log(Level.INFO, "Failed to authenticate {0} with minecraft.net.", message.getName());
                session.disconnect("Player identification failed!");
            }
        } else {
            // Kick if they send ident at the wrong time
            boolean game = state == State.GAME;
            session.disconnect(game ? "Identification already exchanged." : "Handshake not yet exchanged.");
        }
    }

}
