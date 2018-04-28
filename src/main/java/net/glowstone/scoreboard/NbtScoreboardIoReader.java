package net.glowstone.scoreboard;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.NbtInputStream;
import net.glowstone.util.nbt.TagType;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

public class NbtScoreboardIoReader {

    /**
     * Loads the scoreboard status from an NBT file.
     *
     * @param path the file path
     * @return the loaded scoreboard
     * @throws IOException if the file cannot be read
     */
    public static GlowScoreboard readMainScoreboard(File path) throws IOException {
        CompoundTag root;

        try (NbtInputStream nbt = new NbtInputStream(getDataInputStream(path), true)) {
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
        root.iterateCompoundList(objective -> registerObjective(objective, scoreboard),
                "Objectives");
    }

    private static void registerObjective(CompoundTag data, GlowScoreboard scoreboard) {
        String criteria = data.getString("CriteriaName");
        String name = data.getString("Name");
        GlowObjective objective = (GlowObjective) scoreboard.registerNewObjective(name, criteria);
        data.readString(objective::setDisplayName, "DisplayName");
        data.readString(objective::setRenderType, "RenderType");
    }


    private static void registerScores(CompoundTag root, GlowScoreboard scoreboard) {
        root.iterateCompoundList(score -> registerScore(score, scoreboard), "PlayerScores");
    }

    private static void registerScore(CompoundTag data, GlowScoreboard scoreboard) {
        int scoreNum = data.getInt("Score");
        String name = data.getString("Name");
        String objective = data.getString("Objective");

        Score score = scoreboard.getObjective(objective).getScore(name);
        score.setScore(scoreNum);
        data.readBoolean(((GlowScore) score)::setLocked, "Locked");
    }

    private static void registerTeams(CompoundTag root, GlowScoreboard scoreboard) {
        root.iterateCompoundList(team -> registerTeam(team, scoreboard), "Teams");
    }

    private static void registerTeam(CompoundTag data, GlowScoreboard scoreboard) {
        Team.OptionStatus deathMessageVisibility = Team.OptionStatus.ALWAYS;
        switch (data.getString("DeathMessageVisibility")) {
            case "never":
                deathMessageVisibility = Team.OptionStatus.NEVER;
                break;
            case "hideForOtherTeams":
                deathMessageVisibility = Team.OptionStatus.FOR_OTHER_TEAMS;
                break;
            case "hideForOwnTeam":
                deathMessageVisibility = Team.OptionStatus.FOR_OWN_TEAM;
                break;
            default:
                // TODO: should this raise a warning?
                // leave deathMessageVisibility at default
        }
        Team.OptionStatus collisionRule = Team.OptionStatus.ALWAYS;
        switch (data.getString("CollisionRule")) {
            case "never":
                collisionRule = Team.OptionStatus.NEVER;
                break;
            case "pushOtherTeams":
                collisionRule = Team.OptionStatus.FOR_OTHER_TEAMS;
                break;
            case "pushOwnTeam":
                collisionRule = Team.OptionStatus.FOR_OWN_TEAM;
                break;
            default:
                // TODO: Should this raise a warning?
                // leave collisionRule at default
        }
        ChatColor teamColor = null;
        if (data.containsKey("TeamColor")) {
            teamColor = ChatColor.valueOf(data.getString("TeamColor").toUpperCase());
        }

        GlowTeam team = (GlowTeam) scoreboard.registerNewTeam(data.getString("Name"));
        data.readString(team::setDisplayName, "DisplayName");
        data.readString(team::setPrefix, "Prefix");
        data.readString(team::setSuffix, "Suffix");
        data.readBoolean(team::setAllowFriendlyFire, "AllowFriendlyFire");
        data.readBoolean(team::setCanSeeFriendlyInvisibles, "SeeFriendlyInvisibles");
        data.readString(nameTagVisibility ->
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus
                .valueOf(nameTagVisibility.toUpperCase())), "NameTagVisibility");
        team.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, deathMessageVisibility);
        team.setOption(Team.Option.COLLISION_RULE, collisionRule);
        if (teamColor != null) {
            team.setColor(teamColor);
        }
        List<String> players = data.getList("Players", TagType.STRING);

        players.forEach(team::addEntry);
    }

    private static void registerDisplaySlots(CompoundTag root, GlowScoreboard scoreboard) {
        if (root.containsKey("DisplaySlots")) {
            CompoundTag data = root.getCompound("DisplaySlots");

            data.readString(
                list -> scoreboard.getObjective(list).setDisplaySlot(DisplaySlot.PLAYER_LIST),
                    "slot_0");
            data.readString(
                sidebar -> scoreboard.getObjective(sidebar).setDisplaySlot(DisplaySlot.SIDEBAR),
                    "slot_1");
            data.readString(
                belowName -> scoreboard.getObjective(belowName).setDisplaySlot(DisplaySlot.BELOW_NAME),
                    "slot_2");

            /* TODO: anything need to be done with team slots?
            String teamBlack = getOrNull("slot_3", data);
            String teamDarkBlue = getOrNull("slot_4", data);
            String teamDarkGreen = getOrNull("slot_5", data);
            String teamDarkAqua = getOrNull("slot_6", data);
            String teamDarkRed = getOrNull("slot_7", data);
            String teamDarkPurple = getOrNull("slot_8", data);
            String teamGold = getOrNull("slot_9", data);
            String teamGray = getOrNull("slot_10", data);
            String teamDarkGray = getOrNull("slot_11", data);
            String teamBlue = getOrNull("slot_12", data);
            String teamGreen = getOrNull("slot_13", data);
            String teamAqua = getOrNull("slot_14", data);
            String teamRed = getOrNull("slot_15", data);
            String teamLightPurple = getOrNull("slot_16", data);
            String teamYellow = getOrNull("slot_17", data);
            String teamWhite = getOrNull("slot_18", data);
            */
        }
    }
}
