package net.glowstone.scoreboard;

import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.NBTInputStream;
import net.glowstone.util.nbt.NBTOutputStream;
import net.glowstone.util.nbt.TagType;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.*;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NbtScoreboardIoWriter {
    public static void writeMainScoreboard(File path, GlowScoreboard scoreboard) throws IOException{
        CompoundTag root = new CompoundTag();
        CompoundTag data  = new CompoundTag();
        root.putCompound("data", data);
        NBTOutputStream nbt = new NBTOutputStream(getDataOutputStream(path), true);

        writeObjectives(data, scoreboard);
        writeScores(data, scoreboard);
        writeTeams(data, scoreboard);
        writeDisplaySlots(data, scoreboard);

        nbt.writeTag(root);
        nbt.close();
    }

    private static DataOutputStream getDataOutputStream(File path) throws IOException {
        if (!path.exists()) {
            path.getParentFile().mkdirs();
            path.createNewFile();
        }
        return new DataOutputStream(new FileOutputStream(path));
    }

    private static void writeObjectives(CompoundTag root, GlowScoreboard scoreboard) {
        List<CompoundTag> objectives = new ArrayList<>();
        for (Objective objective: scoreboard.getObjectives()) {
            CompoundTag objective_nbt = new CompoundTag();
            objective_nbt.putString("Criteria", objective.getCriteria().getValue());
            objective_nbt.putString("DisplayName", objective.getDisplayName());
            objective_nbt.putString("Name", objective.getName());
            objective_nbt.putString("RenderType", objective.getType().name());

            objectives.add(objective_nbt);
        }
        root.putCompoundList("Objectives", objectives);
    }

    private static void writeScores(CompoundTag root, GlowScoreboard scoreboard) {
        List<CompoundTag> scores = new ArrayList<>();
        for (String objective: scoreboard.getEntries()) {
            for (Score score: scoreboard.getScores(objective)) {
                CompoundTag score_nbt = new CompoundTag();
                score_nbt.putInt("Score", score.getScore());
                score_nbt.putString("Name", score.getEntry());
                score_nbt.putString("Objective", score.getObjective().getName());
                score_nbt.putByte("Locked", score.getLocked() ? 1: 0);

                scores.add(score_nbt);
            }
        }
        root.putCompoundList("PlayerScores", scores);
    }

    private static void writeTeams(CompoundTag root, GlowScoreboard scoreboard) {
        List<CompoundTag> teams = new ArrayList<>();
        for (Team team: scoreboard.getTeams()) {
            CompoundTag team_nbt = new CompoundTag();
            team_nbt.putByte("AllowFriendlyFire", team.allowFriendlyFire() ? 1 : 0);
            team_nbt.putByte("SeeFriendlyInvisibles", team.canSeeFriendlyInvisibles() ? 1 : 0);
            team_nbt.putString("NameTagVisibility", team.getNametagVisibility().getValue());
            team_nbt.putString("DeathMessageVisibility", team.getDeathMessageVisibility().getValue());
            team_nbt.putString("DisplayName", team.getDisplayName());
            team_nbt.putString("Name", team.getName());
            team_nbt.putString("Prefix", team.getPrefix());
            team_nbt.putString("Suffix", team.getSuffix());
            team_nbt.putString("TeamColor", team.getColor().name().toLowerCase());


            List<String> players = new ArrayList<>();
            for (OfflinePlayer player: team.getPlayers()) {
                players.add(player.getName());
            }

            team_nbt.putList("Players", TagType.STRING, players);
            teams.add(team_nbt);
        }
        root.putCompoundList("Teams", teams);
    }

    private static void writeDisplaySlots(CompoundTag root, GlowScoreboard scoreboard) {
        CompoundTag slots = new CompoundTag();
        if (scoreboard.getObjective(DisplaySlot.PLAYER_LIST) != null) {
            slots.putString("slot_0", scoreboard.getObjective(DisplaySlot.PLAYER_LIST).getName());
        }
        if (scoreboard.getObjective(DisplaySlot.SIDEBAR) != null) {
            slots.putString("slot_1", scoreboard.getObjective(DisplaySlot.SIDEBAR).getName());
        }
        if (scoreboard.getObjective(DisplaySlot.BELOW_NAME) != null) {
            slots.putString("slot_2", scoreboard.getObjective(DisplaySlot.BELOW_NAME).getName());
        }

        root.putCompound("DisplaySlots", slots);
    }
}