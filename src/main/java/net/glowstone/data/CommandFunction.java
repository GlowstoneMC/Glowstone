package net.glowstone.data;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Data
public class CommandFunction {

    private final String namespace, name;
    private final List<FunctionLine> lines;

    public String getFullName() {
        return namespace + ":" + name;
    }

    public static CommandFunction read(String namespace, String name, File file) throws IOException {
        List<FunctionLine> lines = new ArrayList<>();
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            FunctionLine fl = FunctionLine.read(line);
            if (fl != null) {
                lines.add(fl);
            }
        }
        return new CommandFunction(namespace, name, lines);
    }

    public void execute(CommandSender sender) {
        int count = 0;
        for (FunctionLine line : lines) {
            line.execute(sender);
            if (!line.isComment()) count++;
        }
        sender.sendMessage("Executed " + count + " command(s) from function '" + getFullName() + "'");
    }

    @Override
    public String toString() {
        return getFullName() + "{lines: " + lines + "}";
    }

    @Data
    public static class FunctionLine {

        private final boolean comment;
        private final String content;

        public static FunctionLine read(String line) {
            line = line.trim();
            if (line.isEmpty()) {
                return null;
            }
            boolean comment = line.startsWith("#");
            String content = line;
            if (comment) {
                content = content.substring(1);
            }
            return new FunctionLine(comment, content);
        }

        public void execute(CommandSender sender) {
            if (isComment()) {
                return;
            }
            Bukkit.dispatchCommand(sender, content);
        }

        public String toString() {
            return (comment ? "#" : "/") + content;
        }
    }
}
