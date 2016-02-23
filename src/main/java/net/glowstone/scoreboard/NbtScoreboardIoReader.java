package net.glowstone.scoreboard;

import net.glowstone.GlowOfflinePlayer;
import net.glowstone.GlowServer;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.NBTInputStream;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Score;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NbtScoreboardIoReader {

    public static GlowScoreboard readMainScoreboard(File path) throws IOException {
        CompoundTag root;

        try (NBTInputStream nbt = new NBTInputStream(getDataInputStream(path), true)) {
            root = nbt.readCompound().getCompound("data");
        }

        GlowScoreboard scoreboard = new GlowScoreboard();

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
        int scoreNum = data.getInt("Score");
        String name = data.getString("Name");
        String objective = data.getString("Objective");
        boolean locked = data.getByte("Locked") == 1  ? true : false;

        Score score = scoreboard.getObjective(objective).getScore(name);
        score.setScore(scoreNum);
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
        boolean allowFriendlyFire = data.getByte("AllowFriendlyFire") == 1 ? true : false;
        boolean seeFriendlyInvisibles = data.getByte("SeeFriendlyInvisibles") == 1 ? true : false;
        NameTagVisibility nameTagVisibility = NameTagVisibility.get(data.getString("NameTagVisibility"));
        NameTagVisibility deathMessageVisibility = NameTagVisibility.get(data.getString("NameTagVisibility"));
        String displayName = data.getString("DisplayName");
        String name = data.getString("Name");
        String prefix = data.getString("Prefix");
        String suffix = data.getString("Suffix");
        ChatColor teamColor = ChatColor.valueOf(data.getString("TeamColor").toUpperCase());

        List<OfflinePlayer> players = new ArrayList<>();
        List<String> playerNames = data.getList("Players", TagType.STRING);
        players.addAll(playerNames.stream().map(player -> new GlowOfflinePlayer((GlowServer) Bukkit.getServer(), player)).collect(Collectors.toList()));

        GlowTeam team = (GlowTeam) scoreboard.registerNewTeam(name);
        team.setDisplayName(displayName);
        team.setPrefix(prefix);
        team.setSuffix(suffix);
        team.setAllowFriendlyFire(allowFriendlyFire);
        team.setCanSeeFriendlyInvisibles(seeFriendlyInvisibles);
        team.setNameTagVisibility(nameTagVisibility);
        team.setDeathMessageVisibility(deathMessageVisibility);
        team.setColor(teamColor);

        players.forEach(team::addPlayer);
    }

    private static String getOrNull(String key, CompoundTag tag) {
        if (tag.isString(key)) {
            return tag.getString(key);
        }
        return null;
    }

    private static void registerDisplaySlots(CompoundTag root, GlowScoreboard scoreboard) {
        if (root.containsKey("DisplaySlots")) {
            CompoundTag data = root.getCompound("DisplaySlots");

            String list = getOrNull("slot_0", data);
            String sidebar = getOrNull("slot_1", data);
            String belowName = getOrNull("slot_2", data);

            if (list != null) {
                scoreboard.getObjective(list).setDisplaySlot(DisplaySlot.PLAYER_LIST);
            }

            if (sidebar != null) {
                scoreboard.getObjective(sidebar).setDisplaySlot(DisplaySlot.SIDEBAR);
            }

            if (belowName != null) {
                scoreboard.getObjective(belowName).setDisplaySlot(DisplaySlot.BELOW_NAME);
            }


            String slot3 = getOrNull("slot_3", data);
            String slot4 = getOrNull("slot_4", data);
            String slot5 = getOrNull("slot_5", data);
            String slot6 = getOrNull("slot_6", data);
            String slot7 = getOrNull("slot_7", data);
            String slot8 = getOrNull("slot_8", data);
            String slot9 = getOrNull("slot_9", data);
            String slot10 = getOrNull("slot_10", data);
            String slot11 = getOrNull("slot_11", data);
            String slot12 = getOrNull("slot_12", data);
            String slot13 = getOrNull("slot_13", data);
            String slot14 = getOrNull("slot_14", data);
            String slot15 = getOrNull("slot_15", data);
            String slot16 = getOrNull("slot_16", data);
            String slot17 = getOrNull("slot_17", data);
            String slot18 = getOrNull("slot_18", data);
        }
    }
}
