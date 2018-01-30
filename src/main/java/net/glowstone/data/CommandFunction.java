package net.glowstone.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@Data
public class CommandFunction {

    private final String namespace;
    private final String name;
    private final List<FunctionLine> lines;

    /**
     * Reads a function from a file.
     * @param namespace the function namespace
     * @param name the function name
     * @param file the file to read from
     * @return an instance to handle {@code /function namespace:name}
     * @throws IOException if the file can't be read
     */
    public static CommandFunction read(String namespace, String name, File file)
            throws IOException {
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

    public String getFullName() {
        return namespace + ":" + name;
    }

    /**
     * Calls the function.
     * @param sender the caller
     */
    public void execute(CommandSender sender) {
        int count = 0;
        for (FunctionLine line : lines) {
            line.execute(sender);
            if (!line.isComment()) {
                count++;
            }
        }
        sender.sendMessage(
                "Executed " + count + " command(s) from function '" + getFullName() + "'");
    }

    @Override
    public String toString() {
        return getFullName() + "{lines: " + lines + "}";
    }

    @Data
    public static class FunctionLine {

        private final boolean comment;
        private final String content;

        /**
         * Converts a line of code to a FunctionLine.
         * @param line a line from a function file
         * @return the line as a FunctionLine
         */
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

        /**
         * Executes the command on this line.
         * @param sender the function caller
         */
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
