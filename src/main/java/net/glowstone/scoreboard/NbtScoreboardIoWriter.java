package net.glowstone.scoreboard;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.NbtOutputStream;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

public class NbtScoreboardIoWriter {

    /**
     * Saves a scoreboard to a compressed NBT file.
     *
     * @param path the file path to write to
     * @param scoreboard the scoreboard to save
     * @throws IOException if the file cannot be written
     */
    public static void writeMainScoreboard(File path, GlowScoreboard scoreboard)
        throws IOException {
        CompoundTag root = new CompoundTag();
        CompoundTag data = new CompoundTag();
        root.putCompound("data", data);
        try (NbtOutputStream nbt = new NbtOutputStream(getDataOutputStream(path), true)) {
            writeObjectives(data, scoreboard);
            writeScores(data, scoreboard);
            writeTeams(data, scoreboard);
            writeDisplaySlots(data, scoreboard);

            nbt.writeTag(root);
            nbt.close();
        }
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
        for (Objective objective : scoreboard.getObjectives()) {
            CompoundTag objectiveNbt = new CompoundTag();
            objectiveNbt.putString("CriteriaName", objective.getCriteria());
            objectiveNbt.putString("DisplayName", objective.getDisplayName());
            objectiveNbt.putString("Name", objective.getName());
            objectiveNbt.putString("RenderType", ((GlowObjective) objective).getType().name());

            objectives.add(objectiveNbt);
        }
        root.putCompoundList("Objectives", objectives);
    }

    private static void writeScores(CompoundTag root, GlowScoreboard scoreboard) {
        List<CompoundTag> scores = new ArrayList<>();
        for (String objective : scoreboard.getEntries()) {
            for (Score score : scoreboard.getScores(objective)) {
                CompoundTag scoreNbt = new CompoundTag();
                scoreNbt.putInt("Score", score.getScore());
                scoreNbt.putString("Name", score.getEntry());
                scoreNbt.putString("Objective", score.getObjective().getName());
                scoreNbt.putByte("Locked", ((GlowScore) score).getLocked() ? 1 : 0);

                scores.add(scoreNbt);
            }
        }
        root.putCompoundList("PlayerScores", scores);
    }

    private static void writeTeams(CompoundTag root, GlowScoreboard scoreboard) {
        List<CompoundTag> teams = new ArrayList<>();
        for (Team team : scoreboard.getTeams()) {
            CompoundTag teamNbt = new CompoundTag();
            teamNbt.putByte("AllowFriendlyFire", team.allowFriendlyFire() ? 1 : 0);
            teamNbt.putByte("SeeFriendlyInvisibles", team.canSeeFriendlyInvisibles() ? 1 : 0);
            teamNbt.putString("NameTagVisibility",
                team.getOption(Team.Option.NAME_TAG_VISIBILITY).name().toLowerCase());
            switch (team.getOption(Team.Option.DEATH_MESSAGE_VISIBILITY)) {
                case NEVER:
                    teamNbt.putString("DeathMessageVisibility", "never");
                    break;
                case FOR_OTHER_TEAMS:
                    teamNbt.putString("DeathMessageVisibility", "hideForOtherTeams");
                    break;
                case FOR_OWN_TEAM:
                    teamNbt.putString("DeathMessageVisibility", "hideForOwnTeam");
                    break;
                default:
                    teamNbt.putString("DeathMessageVisibility", "always");
            }
            switch (team.getOption(Team.Option.COLLISION_RULE)) {
                case NEVER:
                    teamNbt.putString("CollisionRule", "never");
                    break;
                case FOR_OTHER_TEAMS:
                    teamNbt.putString("CollisionRule", "pushOtherTeams");
                    break;
                case FOR_OWN_TEAM:
                    teamNbt.putString("CollisionRule", "pushOwnTeam");
                    break;
                default:
                    teamNbt.putString("CollisionRule", "always");
            }
            teamNbt.putString("DisplayName", team.getDisplayName());
            teamNbt.putString("Name", team.getName());
            teamNbt.putString("Prefix", team.getPrefix());
            teamNbt.putString("Suffix", team.getSuffix());
            teamNbt.putString("TeamColor", ((GlowTeam) team).getColor().name().toLowerCase());

            List<String> players = new ArrayList<>(team.getEntries());

            teamNbt.putStringList("Players", players);
            teams.add(teamNbt);
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
