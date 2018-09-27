package net.glowstone.i18n;

import java.util.logging.Level;

public interface ConsoleMessages {
    interface Error {
        interface Biome {
            LoggableLocalizedString UNKNOWN = new LoggableLocalizedStringImpl(
                    "console.biome.unknown", Level.SEVERE
            );
        }

        interface BlockEntity {
            LoggableLocalizedString LOAD_FAILED = new LoggableLocalizedStringImpl(
                    "console.block-entity.load-failed", Level.SEVERE
            );

            LoggableLocalizedString SAVE_FAILED = new LoggableLocalizedStringImpl(
                    "console.block-entity.save-failed", Level.SEVERE
            );
        }

        LoggableLocalizedString CLASSPATH = new LoggableLocalizedStringImpl(
                "console.classpath.load-failed", Level.WARNING
        );

        interface Function {
            LoggableLocalizedString LOAD_FAILED = new LoggableLocalizedStringImpl(
                    "console.function.load-failed", Level.SEVERE
            );
        }

        interface Import {
            LoggableLocalizedString NO_MESSAGE = new LoggableLocalizedStringImpl(
                    "console.import.failed.no-message", Level.WARNING
            );

            LoggableLocalizedString WITH_MESSAGE = new LoggableLocalizedStringImpl(
                    "console.import.failed.with-message", Level.WARNING
            );
        }

        interface Io {

            LoggableLocalizedString MKDIR = new LoggableLocalizedStringImpl(
                    "console.io.mkdir-failed", Level.SEVERE
            );

            LoggableLocalizedString PLAYER_READ = new LoggableLocalizedStringImpl(
                    "console.io.player-read-failed", Level.SEVERE);

            LoggableLocalizedString PLAYER_READ_UNKNOWN = new LoggableLocalizedStringImpl(
                    "console.io.player-read-failed-unknown", Level.SEVERE);

            LoggableLocalizedString PLAYER_WRITE = new LoggableLocalizedStringImpl(
                    "console.io.player-write-failed", Level.SEVERE);

            LoggableLocalizedString WORLD_READ = new LoggableLocalizedStringImpl(
                    "console.io.world-read-failed", Level.SEVERE
            );
        }

        LoggableLocalizedString LOOTING_MANAGER = new LoggableLocalizedStringImpl(
                "console.looting-manager.load-failed", Level.SEVERE
        );

        interface Permission {
            LoggableLocalizedString INVALID = new LoggableLocalizedStringImpl(
                    "console.permission.invalid", Level.SEVERE
            ) {
                @Override
                public String get(Object... args) {
                    // This uses String.format instead of MessageFormat.format
                    // for Bukkit compatibility.
                    return String.format(get(), args);
                }
            };
        }

        interface Plugin {
            LoggableLocalizedString LOADING = new LoggableLocalizedStringImpl(
                    "console.plugin.load-failed", Level.SEVERE
            );

            LoggableLocalizedString MKDIR = new LoggableLocalizedStringImpl(
                    "console.plugin.mkdir-failed", Level.SEVERE
            );
        }

        interface Profile {
            LoggableLocalizedString INTERRUPTED = new LoggableLocalizedStringImpl(
                    "console.profile.interrupted", Level.SEVERE
            );
        }

        interface Rcon {
            LoggableLocalizedString BIND_INTERRUPTED = new LoggableLocalizedStringImpl(
                    "console.rcon.bind-interrupted", Level.SEVERE
            );
        }

        LoggableLocalizedString RELOAD = new LoggableLocalizedStringImpl(
                "console.reload-failed", Level.SEVERE
        );

        LoggableLocalizedString STARTUP = new LoggableLocalizedStringImpl(
                "console.startup-failed", Level.SEVERE
        );

        interface Structure {
            LoggableLocalizedString LOAD_FAILED = new LoggableLocalizedStringImpl(
                    "console.structure.load-failed", Level.SEVERE
            );

            LoggableLocalizedString SAVE_FAILED = new LoggableLocalizedStringImpl(
                    "console.structure.save-failed", Level.SEVERE
            );

            LoggableLocalizedString NO_DATA = new LoggableLocalizedStringImpl(
                    "console.structure.no-data", Level.SEVERE
            );

            LoggableLocalizedString UNKNOWN_PIECE_TYPE = new LoggableLocalizedStringImpl(
                    "console.structure.unknown-piece-type", Level.SEVERE
            );
        }

        interface Uuid {
            LoggableLocalizedString INTERRUPTED = new LoggableLocalizedStringImpl(
                    "console.uuid.interrupted", Level.SEVERE
            );
        }

    }

    interface Info {

        interface Block {

            LoggableLocalizedString UNKNOWN_CLICKED = new LoggableLocalizedStringImpl(
                    "console.block.unknown-clicked", Level.INFO);
        }

        LoggableLocalizedString CONFIG_ONLY_DONE = new LoggableLocalizedStringImpl(
                "console.config-only-done", Level.INFO
        );

        interface Icon {
            LoggableLocalizedString IMPORT = new LoggableLocalizedStringImpl(
                    "console.icon.import", Level.INFO
            );
        }

        LoggableLocalizedString IMPORT = new LoggableLocalizedStringImpl(
                "console.import", Level.INFO
        );

        interface NativeTransport {
            LoggableLocalizedString EPOLL = new LoggableLocalizedStringImpl(
                    "console.native-transport.epoll", Level.INFO
            );

            LoggableLocalizedString KQUEUE = new LoggableLocalizedStringImpl(
                    "console.native-transport.kqueue", Level.INFO
            );
        }

        interface Opencl {
            LoggableLocalizedString BEST = new LoggableLocalizedStringImpl(
                    "console.opencl.best", Level.INFO
            );
            LoggableLocalizedString BEST_VERSION_TIEBREAKER = new LoggableLocalizedStringImpl(
                    "console.opencl.best.version-tiebreaker", Level.INFO
            );

            LoggableLocalizedString CPU = new LoggableLocalizedStringImpl(
                    "console.opencl.cpu", Level.INFO
            );

            LoggableLocalizedString FOUND_DEVICE = new LoggableLocalizedStringImpl(
                    "console.opencl.found-device", Level.INFO
            );

            LoggableLocalizedString INTEL_GPU = new LoggableLocalizedStringImpl(
                    "console.opencl.intel-gpu", Level.INFO
            );

            LoggableLocalizedString NO_DEVICE = new LoggableLocalizedStringImpl(
                    "console.opencl.no-device", Level.INFO
            );

            LoggableLocalizedString REQUIRED_EXTENSIONS = new LoggableLocalizedStringImpl(
                    "console.opencl.required-extensions", Level.INFO
            );

            LoggableLocalizedString REQUIRED_VERSION = new LoggableLocalizedStringImpl(
                    "console.opencl.required-version", Level.INFO
            );
        }

        interface Option {
            LoggableLocalizedString HELP = new LoggableLocalizedStringImpl(
                    "console.option.help", Level.INFO
            );
        }

        interface Plugin {
            LoggableLocalizedString COUNTS = new LoggableLocalizedStringImpl(
                    "console.plugin.counts", Level.INFO
            );

            LoggableLocalizedString SCANNING = new LoggableLocalizedStringImpl(
                    "console.plugin.scanning", Level.INFO
            );
        }

        LoggableLocalizedString PROXY = new LoggableLocalizedStringImpl(
                "console.proxy", Level.INFO
        );

        interface Proxy {
            LoggableLocalizedString ONLINE = new LoggableLocalizedStringImpl(
                    "console.proxy.online", Level.INFO
            );
        }

        LoggableLocalizedString READY = new LoggableLocalizedStringImpl(
                "console.ready", Level.INFO
        );

        LoggableLocalizedString RECIPE_COUNTS = new LoggableLocalizedStringImpl(
                "console.recipe.counts", Level.INFO
        );

        LoggableLocalizedString SAVE = new LoggableLocalizedStringImpl(
                "console.save", Level.INFO
        );

        LoggableLocalizedString SHUTDOWN = new LoggableLocalizedStringImpl(
                "console.shutdown", Level.INFO
        );

        interface Version {
            LoggableLocalizedString BUKKIT = new LoggableLocalizedStringImpl(
                    "console.version.bukkit", Level.INFO
            );

            LoggableLocalizedString GLOWSTONE = new LoggableLocalizedStringImpl(
                    "console.version.glowstone", Level.INFO
            );

            LoggableLocalizedString MINECRAFT_CLIENT = new LoggableLocalizedStringImpl(
                    "console.version.minecraft-client", Level.INFO
            );
        }
    }

    interface Warn {

        interface Block {
            interface Chest {
                LoggableLocalizedString FACING = new LoggableLocalizedStringImpl(
                        "console.block.chest.facing", Level.WARNING);

                LoggableLocalizedString INTERACT_WRONG_CLASS = new LoggableLocalizedStringImpl(
                        "console.block.chest.interact-wrong-class", Level.WARNING);

                LoggableLocalizedString TRIPLE_ALREADY = new LoggableLocalizedStringImpl(
                        "console.block.chest.triple-already", Level.WARNING);

                LoggableLocalizedString TRIPLE_END = new LoggableLocalizedStringImpl(
                        "console.block.chest.triple-end", Level.WARNING);

                LoggableLocalizedString TRIPLE_MIDDLE = new LoggableLocalizedStringImpl(
                        "console.block.chest.triple-middle", Level.WARNING);
            }

            interface DoubleSlab {
                LoggableLocalizedString WRONG_MATERIAL = new LoggableLocalizedStringImpl(
                        "console.block.doubleslab.wrong-material", Level.WARNING);
            }

            LoggableLocalizedString WRONG_MATERIAL_DATA = new LoggableLocalizedStringImpl(
                    "console.block.wrong-material-data", Level.WARNING);
        }

        interface BlockEntity {
            LoggableLocalizedString UNKNOWN = new LoggableLocalizedStringImpl(
                    "console.block-entity.unknown", Level.WARNING
            );
        }

        interface Chunk {
            LoggableLocalizedString SECTION_DUP = new LoggableLocalizedStringImpl(
                    "console.chunk.section-dup", Level.WARNING
            );

            LoggableLocalizedString SECTION_OOB = new LoggableLocalizedStringImpl(
                    "console.chunk.section-oob", Level.WARNING
            );

            LoggableLocalizedString UNKNOWN_BLOCK_TO_TICK = new LoggableLocalizedStringImpl(
                    "console.chunk.unknown-block-to-tick", Level.WARNING
            );
        }

        interface Entity {
            LoggableLocalizedString LOAD_FAILED = new LoggableLocalizedStringImpl(
                    "console.entity.load-failed", Level.WARNING
            );

            LoggableLocalizedString PARTICLE_INVALID = new LoggableLocalizedStringImpl(
                    "console.entity.particle-invalid", Level.WARNING
            );

            LoggableLocalizedString SAVE_FAILED = new LoggableLocalizedStringImpl(
                    "console.entity.save-failed", Level.WARNING
            );

            LoggableLocalizedString UNKNOWN = new LoggableLocalizedStringImpl(
                    "console.entity.unknown", Level.WARNING
            );
        }

        interface Event {
            LoggableLocalizedString INTERRUPTED = new LoggableLocalizedStringImpl(
                    "console.event.interrupted", Level.WARNING
            );

            LoggableLocalizedString SHUTDOWN = new LoggableLocalizedStringImpl(
                    "console.event.shutdown", Level.WARNING
            );
        }

        interface Icon {
            LoggableLocalizedString LOAD_FAILED_IMPORT = new LoggableLocalizedStringImpl(
                    "console.icon.load-failed.import", Level.WARNING
            );

            LoggableLocalizedString LOAD_FAILED = new LoggableLocalizedStringImpl(
                    "console.icon.load-failed", Level.WARNING
            );
        }

        interface Io {
            LoggableLocalizedString JSON_STAT_UNKNOWN = new LoggableLocalizedStringImpl(
                    "console.io.json.stat-unknown", Level.WARNING
            );

            LoggableLocalizedString MKDIR_FAILED = new LoggableLocalizedStringImpl(
                    "console.io.mkdir-failed", Level.WARNING
            );

            LoggableLocalizedString NO_WORLD_DATA_TAG = new LoggableLocalizedStringImpl(
                    "console.io.no-world-data-tag", Level.WARNING
            );

            LoggableLocalizedString REMOVING_SINGLE_PLAYER = new LoggableLocalizedStringImpl(
                    "console.io.removing-single-player", Level.WARNING
            );
        }

        LoggableLocalizedString OFFLINE = new LoggableLocalizedStringImpl(
                "console.offline", Level.WARNING
        );

        interface Option {
            LoggableLocalizedString INVALID = new LoggableLocalizedStringImpl(
                    "console.option.invalid", Level.WARNING
            );

            LoggableLocalizedString NO_VALUE = new LoggableLocalizedStringImpl(
                    "console.option.no-value", Level.WARNING
            );
        }

        interface Permission {
            LoggableLocalizedString DUPLICATE = new LoggableLocalizedStringImpl(
                    "console.permission.duplicate", Level.WARNING
            );
        }

        interface Plugin {
            LoggableLocalizedString NO_SPONGE = new LoggableLocalizedStringImpl(
                    "console.plugin.no-sponge", Level.WARNING
            );

            LoggableLocalizedString UNRECOGNIZED = new LoggableLocalizedStringImpl(
                    "console.plugin.unrecognized", Level.WARNING
            );

            LoggableLocalizedString MALFORMED_URL = new LoggableLocalizedStringImpl(
                    "console.plugin.malformed-url", Level.WARNING
            );

            LoggableLocalizedString LOAD_FAILED = new LoggableLocalizedStringImpl(
                    "console.plugin.load-failed.type-detector", Level.WARNING
            );

            LoggableLocalizedString BUKKIT2SPONGE = new LoggableLocalizedStringImpl(
                    "console.plugin.no-sponge.bukkit2sponge", Level.WARNING
            );

            LoggableLocalizedString PERMISSION_DUPLICATE = new LoggableLocalizedStringImpl(
                    "console.plugin.permission.duplicate", Level.WARNING
            );

            LoggableLocalizedString UNSUPPORTED = new LoggableLocalizedStringImpl(
                    "console.plugin.unsupported", Level.WARNING
            );

            LoggableLocalizedString UNSUPPORTED_CANARY = new LoggableLocalizedStringImpl(
                    "console.plugin.unsupported.canary", Level.WARNING
            );

            LoggableLocalizedString UNSUPPORTED_FORGE = new LoggableLocalizedStringImpl(
                    "console.plugin.unsupported.forge", Level.WARNING
            );

            LoggableLocalizedString UNSUPPORTED_OTHER = new LoggableLocalizedStringImpl(
                    "console.plugin.unsupported.other", Level.WARNING
            );

            LoggableLocalizedString UNSUPPORTED_SPONGE = new LoggableLocalizedStringImpl(
                    "console.plugin.unsupported.sponge", Level.WARNING
            );
        }

        interface Profile {
            LoggableLocalizedString TIMEOUT = new LoggableLocalizedStringImpl(
                    "console.profile.timeout", Level.WARNING
            );
        }

        interface Recipe {
            LoggableLocalizedString NO_DEFAULTS = new LoggableLocalizedStringImpl(
                    "console.recipe.no-defaults", Level.WARNING
            );
        }

        interface Uuid {
            LoggableLocalizedString TIMEOUT = new LoggableLocalizedStringImpl(
                    "console.uuid.timeout", Level.WARNING
            );
        }

        interface WorldGen {
            LoggableLocalizedString DISABLED = new LoggableLocalizedStringImpl(
                    "console.worldgen.disabled", Level.WARNING
            );
        }
    }
}
