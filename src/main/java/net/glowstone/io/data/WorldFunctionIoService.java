package net.glowstone.io.data;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.data.CommandFunction;
import net.glowstone.io.FunctionIoService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class WorldFunctionIoService implements FunctionIoService {

    private static final String FUNCTIONS_DIR_NAME = "functions";

    private final File dataDir;
    private final GlowWorld world;

    public WorldFunctionIoService(GlowWorld world, File dataDir) {
        this.dataDir = dataDir;
        this.world = world;
    }

    @Override
    public List<CommandFunction> readFunctions() {
        List<CommandFunction> functions = new ArrayList<>();
        try {
            File functionsDir = new File(dataDir, FUNCTIONS_DIR_NAME);
            functionsDir.mkdirs();
            File[] subdirs = functionsDir.listFiles(File::isDirectory);
            for (File dir : subdirs) {
                String namespace = dir.getName();
                List<CommandFunction> namespaceFunctions = functionsInside(namespace, "", dir);
                functions.addAll(namespaceFunctions);
            }
        } catch (IOException ex) {
            GlowServer.logger.log(Level.SEVERE, "Error while loading functions for world '" + world.getName() + "'", ex);
        }
        return functions;
    }

    private List<CommandFunction> functionsInside(String namespace, String location, File parent) throws IOException {
        List<CommandFunction> functions = new ArrayList<>();
        for (File file : parent.listFiles()) {
            if (file.isDirectory()) {
                functions.addAll(functionsInside(namespace, location + file.getName() + "/", file));
            } else if (file.getName().endsWith(".mcfunction")) {
                functions.add(CommandFunction.read(namespace, location + file.getName().substring(0, file.getName().length() - ".mcfunction".length()), file));
            }
        }
        return functions;
    }
}
