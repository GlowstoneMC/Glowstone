package net.glowstone.scoreboard;

import net.glowstone.GlowOfflinePlayer;
import net.glowstone.GlowServer;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.NBTInputStream;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NbtScoreboardIoReader {

    public static GlowScoreboard readMainScoreboard(File path) throws IOException{

        CompoundTag root;

        try (NBTInputStream nbt = new NBTInputStream(getDataInputStream(path), false)) {
            root = nbt.readCompound();
        }

        GlowScoreboard scoreboard = new GlowScoreboard((GlowServer) Bukkit.getServer());

        registerObjectives(root, scoreboard);
        registerScores(root, scoreboard);
        registerTeams(root, scoreboard);
        registerDisplaySlots(root, scoreboard);

        return scoreboard;
    }

    private static DataInputStream getDataInputStream(File path) throws FileNotFoundException {
        return new DataInputStream(new FileInputStream(path));
    }

    private static void registerObjectives(CompoundTag root, GlowScoreboard scoreboard) {
        if (root.containsKey("Objectives")) {
            List<CompoundTag> objectives = root.getCompoundList("Objectives");
            for (CompoundTag objective: objectives) {
                registerObjective(objective, scoreboard);
            }
        }
    }

    private static void registerObjective(CompoundTag data, GlowScoreboard scoreboard) {
        String criteria = data.getString("CriteriaName");
        String displayName = data.getString("DisplayName");
        String name = data.getString("Name");
        String renderType = data.getString("RenderType");

        GlowObjective objective = (GlowObjective) scoreboard.registerNewObjective(name, criteria);
        objective.setDisplayName(displayName);
        objective.setRenderType(renderType);
    }



    private static void registerScores(CompoundTag root, GlowScoreboard scoreboard) {
        if (root.containsKey("PlayerScores")) {
            List<CompoundTag> scores = root.getCompoundList("PlayerScores");
            for (CompoundTag score: scores) {
                registerScore(score, scoreboard);
            }
        }
    }

    private static void registerScore(CompoundTag data, GlowScoreboard scoreboard) {
        int score_num = data.getInt("Score");
        String name = data.getString("Name");
        String objective = data.getString("Objective");
        boolean locked = data.getByte("Locked") == 1  ? true : false;

        GlowScore score = (GlowScore) scoreboard.getObjective(objective).getScore(name);
        score.setScore(score_num);
        score.setLocked(locked);
    }

    private static void registerTeams(CompoundTag root, GlowScoreboard scoreboard) {
        if (root.containsKey("Teams")) {
            List<CompoundTag> teams = root.getCompoundList("Teams");
            for (CompoundTag team: teams) {
                registerTeam(team, scoreboard);
            }
        }
    }

    private static void registerTeam(CompoundTag data, GlowScoreboard scoreboard) {
        boolean allowFriendlyFire = data.getByte("AllowFriendlyFire") == 1 ? true: false;
        boolean seeFriendlyInvisibles = data.getByte("SeeFriendlyInvisibles") == 1 ? true: false;
        String nameTagVisibility = data.getString("NameTagVisibility");
        String deathMessageVisibility = data.getString("NameTagVisibility");
        String displayName = data.getString("DisplayName");
        String name = data.getString("Name");
        String prefix = data.getString("Prefix");
        String suffix = data.getString("Suffix");
        String teamColor = data.getString("TeamColor");

        List<OfflinePlayer> players = new ArrayList<>();
        List<String> playerNames = data.getList("Players", TagType.STRING);
        for (String player: playerNames) {
            players.add(new GlowOfflinePlayer((GlowServer) Bukkit.getServer(), player));
        }

        GlowTeam team = (GlowTeam) scoreboard.registerNewTeam(name);
        team.setDisplayName(displayName);
        team.setPrefix(prefix);
        team.setSuffix(suffix);
        team.setAllowFriendlyFire(allowFriendlyFire);
        team.setCanSeeFriendlyInvisibles(seeFriendlyInvisibles);
        team.setNameTagVisibility(nameTagVisibility);
        team.setDeathMessageVisibility(deathMessageVisibility);
        team.setColor(teamColor);

        for (OfflinePlayer player: players) {
            team.addPlayer(player);
        }
    }

    private static void registerDisplaySlots(CompoundTag root, GlowScoreboard scoreboard) {
        if (root.containsKey("DisplaySlots")) {
            CompoundTag data = root.getCompound("DisplaySlots");
            String list = data.getString("slot_0");
            String sidebar = data.getString("slot_1");
            String belowName = data.getString("slot_2");

            if (list != null) {
                scoreboard.getObjective(list).setDisplaySlot(DisplaySlot.PLAYER_LIST);
            }

            if (sidebar != null) {
                scoreboard.getObjective(sidebar).setDisplaySlot(DisplaySlot.SIDEBAR);
            }

            if (belowName != null) {
                scoreboard.getObjective(belowName).setDisplaySlot(DisplaySlot.BELOW_NAME);
            }


            String slot_3 = data.getString("slot_3");
            String slot_4 = data.getString("slot_4");
            String slot_5 = data.getString("slot_5");
            String slot_6 = data.getString("slot_6");
            String slot_7 = data.getString("slot_7");
            String slot_8 = data.getString("slot_8");
            String slot_9 = data.getString("slot_9");
            String slot_10 = data.getString("slot_10");
            String slot_11 = data.getString("slot_11");
            String slot_12 = data.getString("slot_12");
            String slot_13 = data.getString("slot_13");
            String slot_14 = data.getString("slot_14");
            String slot_15 = data.getString("slot_15");
            String slot_16 = data.getString("slot_16");
            String slot_17 = data.getString("slot_17");
            String slot_18 = data.getString("slot_18");
        }
    }
}
