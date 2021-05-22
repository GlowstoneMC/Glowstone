package net.glowstone.io.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.glowstone.GlowWorld;
import net.glowstone.data.CommandFunction;
import net.glowstone.i18n.ConsoleMessages;
import net.glowstone.io.FunctionIoService;

public class WorldFunctionIoService implements FunctionIoService {

    public static final String FUNCTION_EXT = ".mcfunction";
    public static final int FUNCTION_EXT_LENGTH = FUNCTION_EXT.length();
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
            ConsoleMessages.Error.Function.LOAD_FAILED.log(ex, world.getName());
        }
        return functions;
    }

    private List<CommandFunction> functionsInside(String namespace, String location, File parent)
        throws IOException {
        List<CommandFunction> functions = new ArrayList<>();
        for (File file : parent.listFiles()) {
            if (file.isDirectory()) {
                functions.addAll(functionsInside(namespace, location + file.getName() + "/", file));
            } else if (file.getName().endsWith(FUNCTION_EXT)) { // NON-NLS
                functions.add(CommandFunction.read(namespace, location + file.getName()
                    .substring(0, file.getName().length() - FUNCTION_EXT_LENGTH), file)); // NON-NLS
            }
        }
        return functions;
    }
}
