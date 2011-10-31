package net.glowstone;

import org.bukkit.GameMode;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

public class GlowConfiguration {
    private final GlowServer server;

    public GlowConfiguration(GlowServer server) {
        this.server = server;
    }

    private String bukkitVersion;
    public String getBukkitVersion() {
        if (bukkitVersion == null) {
            String result = "Unknown-Version";

            InputStream stream = GlowServer.class.getClassLoader().getResourceAsStream("META-INF/maven/org.bukkit/bukkit/pom.properties");
            Properties properties = new Properties();
            if (stream != null) {
                try {
                    properties.load(stream);

                    result = properties.getProperty("version");
                } catch (IOException ex) {
                    server.getLogger().log(Level.SEVERE, "Could not get Bukkit version!", ex);
                }
            }

            return bukkitVersion = result;
        } else {
            return bukkitVersion;
        }
    }
}
