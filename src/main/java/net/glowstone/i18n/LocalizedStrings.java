package net.glowstone.i18n;

import java.util.logging.Level;

public interface LocalizedStrings {
    interface Console {
        interface Error {
            interface Biome {
                LoggableLocalizedStringImpl UNKNOWN = new LoggableLocalizedStringImpl(
                        "console.biome.unknown", Level.SEVERE
                );
            }

            interface BlockEntity {
                LoggableLocalizedStringImpl READ_ERROR = new LoggableLocalizedStringImpl(
                        "console.block-entity.read-error", Level.SEVERE
                );
            }

            LoggableLocalizedStringImpl CLASSPATH = new LoggableLocalizedStringImpl(
                    "console.classpath.load-failed", Level.WARNING
            );

            interface Function {
                LoggableLocalizedStringImpl FILE_READ = new LoggableLocalizedStringImpl(
                        "console.function.load-failed", Level.SEVERE
                );
            }

            interface Import {
                LoggableLocalizedStringImpl NO_MESSAGE = new LoggableLocalizedStringImpl(
                        "console.import.failed.no-message", Level.WARNING
                );

                LoggableLocalizedStringImpl WITH_MESSAGE = new LoggableLocalizedStringImpl(
                        "console.import.failed.with-message", Level.WARNING
                );
            }

            interface Io {
                LoggableLocalizedStringImpl MKDIR = new LoggableLocalizedStringImpl(
                        "console.io.mkdir-failed", Level.SEVERE
                );

                LoggableLocalizedStringImpl PLAYER_READ = new LoggableLocalizedStringImpl(
                        "console.io.player-read-failed", Level.SEVERE);

                LoggableLocalizedStringImpl PLAYER_READ_UNKNOWN = new LoggableLocalizedStringImpl(
                        "console.io.player-read-failed-unknown", Level.SEVERE);

                LoggableLocalizedStringImpl PLAYER_WRITE = new LoggableLocalizedStringImpl(
                        "console.io.player-write-failed", Level.SEVERE);

                LoggableLocalizedStringImpl WORLD_READ = new LoggableLocalizedStringImpl(
                        "console.io.world-read-failed", Level.SEVERE
                );
            }

            LoggableLocalizedStringImpl LOOTING_MANAGER = new LoggableLocalizedStringImpl(
                    "console.looting-manager.load-failed", Level.SEVERE
            );

            interface Manager {
                LoggableLocalizedStringImpl COMMAND = new LoggableLocalizedStringImpl(
                    "console.manager.command-failed", Level.WARNING
                );

                LoggableLocalizedStringImpl COMMAND_READ = new LoggableLocalizedStringImpl(
                    "console.manager.command-read-failed", Level.SEVERE
                );

                LoggableLocalizedStringImpl LOG_FOLDER = new LoggableLocalizedStringImpl(
                    "console.manager.log-folder-failed", Level.WARNING
                );

                LoggableLocalizedStringImpl LOG_FILE = new LoggableLocalizedStringImpl(
                    "console.manager.log-file-failed", Level.SEVERE
                );

                LoggableLocalizedStringImpl TAB_COMPLETE = new LoggableLocalizedStringImpl(
                    "console.manager.tab-complete-failed", Level.WARNING
                );
            }

            interface Permission {
                LoggableLocalizedStringImpl INVALID = new LoggableLocalizedStringImpl(
                        "console.permission.invalid", Level.SEVERE
                );
            }

            interface Plugin {
                LoggableLocalizedStringImpl LOADING = new LoggableLocalizedStringImpl(
                        "console.plugin.load-failed", Level.SEVERE
                );

                LoggableLocalizedStringImpl MKDIR = new LoggableLocalizedStringImpl(
                        "console.plugin.mkdir-failed", Level.SEVERE
                );
            }

            interface Profile {
                LoggableLocalizedStringImpl INTERRUPTED = new LoggableLocalizedStringImpl(
                        "console.profile.interrupted", Level.SEVERE
                );
            }

            interface Rcon {
                LoggableLocalizedStringImpl BIND_INTERRUPTED = new LoggableLocalizedStringImpl(
                        "console.rcon.bind-interrupted", Level.SEVERE
                );
            }

            LoggableLocalizedStringImpl RELOAD = new LoggableLocalizedStringImpl(
                    "console.reload-failed", Level.SEVERE
            );

            LoggableLocalizedStringImpl STARTUP = new LoggableLocalizedStringImpl(
                    "console.startup-failed", Level.SEVERE
            );

            interface Structure {
                LoggableLocalizedStringImpl IO_READ = new LoggableLocalizedStringImpl(
                        "console.structure.io-read", Level.SEVERE
                );

                LoggableLocalizedStringImpl IO_WRITE = new LoggableLocalizedStringImpl(
                        "console.structure.io-write", Level.SEVERE
                );

                LoggableLocalizedStringImpl NO_DATA = new LoggableLocalizedStringImpl(
                        "console.structure.no-data", Level.SEVERE
                );

                LoggableLocalizedStringImpl UNKNOWN_PIECE_TYPE = new LoggableLocalizedStringImpl(
                        "console.structure.unknown-piece-type", Level.SEVERE
                );
            }

            interface Uuid {
                LoggableLocalizedStringImpl INTERRUPTED = new LoggableLocalizedStringImpl(
                        "console.uuid.interrupted", Level.SEVERE
                );
            }

        }

        interface Info {
            LoggableLocalizedStringImpl CONFIG_ONLY_DONE = new LoggableLocalizedStringImpl(
                    "console.config-only-done", Level.INFO
            );

            interface Icon {
                LoggableLocalizedStringImpl IMPORT = new LoggableLocalizedStringImpl(
                        "console.icon.import", Level.INFO
                );
            }

            LoggableLocalizedStringImpl IMPORT = new LoggableLocalizedStringImpl(
                    "console.import", Level.INFO
            );

            interface Manager {
                LoggableLocalizedStringImpl ROTATE = new LoggableLocalizedStringImpl(
                    "console.manager.log-rotate", Level.INFO
                );
            }

            interface NativeTransport {
                LoggableLocalizedStringImpl EPOLL = new LoggableLocalizedStringImpl(
                        "console.native-transport.epoll", Level.INFO
                );

                LoggableLocalizedStringImpl KQUEUE = new LoggableLocalizedStringImpl(
                        "console.native-transport.kqueue", Level.INFO
                );
            }

            interface Opencl {
                LoggableLocalizedStringImpl BEST = new LoggableLocalizedStringImpl(
                        "console.opencl.best", Level.INFO
                );
                LoggableLocalizedStringImpl
                    BEST_VERSION_TIEBREAKER = new LoggableLocalizedStringImpl(
                        "console.opencl.best.version-tiebreaker", Level.INFO
                );

                LoggableLocalizedStringImpl CPU = new LoggableLocalizedStringImpl(
                        "console.opencl.cpu", Level.INFO
                );

                LoggableLocalizedStringImpl FOUND_DEVICE = new LoggableLocalizedStringImpl(
                        "console.opencl.found-device", Level.INFO
                );

                LoggableLocalizedStringImpl INTEL_GPU = new LoggableLocalizedStringImpl(
                        "console.opencl.intel-gpu", Level.INFO
                );

                LoggableLocalizedStringImpl NO_DEVICE = new LoggableLocalizedStringImpl(
                        "console.opencl.no-device", Level.INFO
                );

                LoggableLocalizedStringImpl REQUIRED_EXTENSIONS = new LoggableLocalizedStringImpl(
                        "console.opencl.required-extensions", Level.INFO
                );

                LoggableLocalizedStringImpl REQUIRED_VERSION = new LoggableLocalizedStringImpl(
                        "console.opencl.required-version", Level.INFO
                );
            }

            interface Option {
                LoggableLocalizedStringImpl HELP = new LoggableLocalizedStringImpl(
                        "console.option.help", Level.INFO
                );
            }

            interface Plugin {
                LoggableLocalizedStringImpl COUNTS = new LoggableLocalizedStringImpl(
                        "console.plugin.counts", Level.INFO
                );

                LoggableLocalizedStringImpl SCANNING = new LoggableLocalizedStringImpl(
                        "console.plugin.scanning", Level.INFO
                );
            }

            LoggableLocalizedStringImpl PROXY = new LoggableLocalizedStringImpl(
                    "console.proxy", Level.INFO
            );

            interface Proxy {
                LoggableLocalizedStringImpl ONLINE = new LoggableLocalizedStringImpl(
                        "console.proxy.online", Level.INFO
                );
            }

            LoggableLocalizedStringImpl READY = new LoggableLocalizedStringImpl(
                    "console.ready", Level.INFO
            );

            LoggableLocalizedStringImpl RECIPE_COUNTS = new LoggableLocalizedStringImpl(
                    "console.recipe.counts", Level.INFO
            );

            LoggableLocalizedStringImpl SAVE = new LoggableLocalizedStringImpl(
                    "console.save", Level.INFO
            );

            LoggableLocalizedStringImpl SHUTDOWN = new LoggableLocalizedStringImpl(
                    "console.shutdown", Level.INFO
            );

            interface Version {
                LoggableLocalizedStringImpl BUKKIT = new LoggableLocalizedStringImpl(
                        "console.version.bukkit", Level.INFO
                );

                LoggableLocalizedStringImpl GLOWSTONE = new LoggableLocalizedStringImpl(
                        "console.version.glowstone", Level.INFO
                );

                LoggableLocalizedStringImpl MINECRAFT_CLIENT = new LoggableLocalizedStringImpl(
                        "console.version.minecraft-client", Level.INFO
                );
            }
        }

        interface Warn {

            interface BlockEntity {
                LoggableLocalizedStringImpl UNKNOWN = new LoggableLocalizedStringImpl(
                        "console.block-entity.unknown", Level.WARNING
                );
            }

            interface Chunk {
                LoggableLocalizedStringImpl SECTION_DUP = new LoggableLocalizedStringImpl(
                        "console.chunk.section-dup", Level.WARNING
                );

                LoggableLocalizedStringImpl SECTION_OOB = new LoggableLocalizedStringImpl(
                        "console.chunk.section-oob", Level.WARNING
                );

                LoggableLocalizedStringImpl UNKNOWN_BLOCK_TO_TICK = new LoggableLocalizedStringImpl(
                        "console.chunk.unknown-block-to-tick", Level.WARNING
                );
            }

            interface Entity {
                LoggableLocalizedStringImpl LOADING_ERROR = new LoggableLocalizedStringImpl(
                        "console.entity.loading-error", Level.WARNING
                );

                LoggableLocalizedStringImpl UNKNOWN = new LoggableLocalizedStringImpl(
                        "console.entity.unknown", Level.WARNING
                );
            }

            interface Event {
                LoggableLocalizedStringImpl INTERRUPTED = new LoggableLocalizedStringImpl(
                        "console.event.interrupted", Level.WARNING
                );

                LoggableLocalizedStringImpl SHUTDOWN = new LoggableLocalizedStringImpl(
                        "console.event.shutdown", Level.WARNING
                );
            }

            interface Icon {
                LoggableLocalizedStringImpl LOAD_FAILED_IMPORT = new LoggableLocalizedStringImpl(
                        "console.icon.load-failed.import", Level.WARNING
                );

                LoggableLocalizedStringImpl LOAD_FAILED = new LoggableLocalizedStringImpl(
                        "console.icon.load-failed", Level.WARNING
                );
            }

            interface Io {
                LoggableLocalizedStringImpl JSON_STAT_UNKNOWN = new LoggableLocalizedStringImpl(
                        "console.io.json.stat-unknown", Level.WARNING
                );

                LoggableLocalizedStringImpl MKDIR_FAILED = new LoggableLocalizedStringImpl(
                        "console.io.mkdir-failed", Level.WARNING
                );

                LoggableLocalizedStringImpl NO_WORLD_DATA_TAG = new LoggableLocalizedStringImpl(
                        "console.io.no-world-data-tag", Level.WARNING
                );

                LoggableLocalizedStringImpl
                    REMOVING_SINGLE_PLAYER = new LoggableLocalizedStringImpl(
                        "console.io.removing-single-player", Level.WARNING
                );
            }

            LoggableLocalizedStringImpl OFFLINE = new LoggableLocalizedStringImpl(
                    "console.offline", Level.WARNING
            );

            interface Option {
                LoggableLocalizedStringImpl INVALID = new LoggableLocalizedStringImpl(
                        "console.option.invalid", Level.WARNING
                );

                LoggableLocalizedStringImpl NO_VALUE = new LoggableLocalizedStringImpl(
                        "console.option.no-value", Level.WARNING
                );
            }

            interface Permission {
                LoggableLocalizedStringImpl DUPLICATE = new LoggableLocalizedStringImpl(
                        "console.permission.duplicate", Level.WARNING
                );
            }

            interface Plugin {
                LoggableLocalizedStringImpl NO_SPONGE = new LoggableLocalizedStringImpl(
                        "console.plugin.no-sponge", Level.WARNING
                );

                LoggableLocalizedStringImpl UNRECOGNIZED = new LoggableLocalizedStringImpl(
                        "console.plugin.unrecognized", Level.WARNING
                );

                LoggableLocalizedStringImpl MALFORMED_URL = new LoggableLocalizedStringImpl(
                        "console.plugin.malformed-url", Level.WARNING
                );

                LoggableLocalizedStringImpl IO = new LoggableLocalizedStringImpl(
                        "console.plugin.ioexception", Level.WARNING
                );

                LoggableLocalizedStringImpl BUKKIT2SPONGE = new LoggableLocalizedStringImpl(
                        "console.plugin.no-sponge.bukkit2sponge", Level.WARNING
                );

                LoggableLocalizedStringImpl PERMISSION_DUPLICATE = new LoggableLocalizedStringImpl(
                        "console.plugin.permission.duplicate", Level.WARNING
                );

                LoggableLocalizedStringImpl UNSUPPORTED = new LoggableLocalizedStringImpl(
                        "console.plugin.unsupported", Level.WARNING
                );

                LoggableLocalizedStringImpl UNSUPPORTED_CANARY = new LoggableLocalizedStringImpl(
                        "console.plugin.unsupported.canary", Level.WARNING
                );

                LoggableLocalizedStringImpl UNSUPPORTED_FORGE = new LoggableLocalizedStringImpl(
                        "console.plugin.unsupported.forge", Level.WARNING
                );

                LoggableLocalizedStringImpl UNSUPPORTED_OTHER = new LoggableLocalizedStringImpl(
                        "console.plugin.unsupported.other", Level.WARNING
                );

                LoggableLocalizedStringImpl UNSUPPORTED_SPONGE = new LoggableLocalizedStringImpl(
                        "console.plugin.unsupported.sponge", Level.WARNING
                );
            }

            interface Profile {
                LoggableLocalizedStringImpl TIMEOUT = new LoggableLocalizedStringImpl(
                        "console.profile.timeout", Level.WARNING
                );
            }

            interface Recipe {
                LoggableLocalizedStringImpl NO_DEFAULTS = new LoggableLocalizedStringImpl(
                        "console.recipe.no-defaults", Level.WARNING
                );
            }

            interface Uuid {
                LoggableLocalizedStringImpl TIMEOUT = new LoggableLocalizedStringImpl(
                        "console.uuid.timeout", Level.WARNING
                );
            }

            interface WorldGen {
                LoggableLocalizedStringImpl DISABLED = new LoggableLocalizedStringImpl(
                        "console.worldgen.disabled", Level.WARNING
                );
            }
        }
    }

    interface Glowstone {
        interface Advancement {
            LocalizedString TITLE = new LocalizedStringImpl("glowstone.advancement.title");
        }

        interface Command {
            interface Error {
                LocalizedString UNKNOWN_COMMAND =
                    new LocalizedStringImpl("glowstone.command.error.unknown-command");
            }
        }

        interface Kick {
            LocalizedString BANNED = new LocalizedStringImpl("glowstone.kick.banned");

            LocalizedString FILE_READ = new LocalizedStringImpl("glowstone.kick.file-read");

            LocalizedString FILE_WRITE = new LocalizedStringImpl("glowstone.kick.file-write");

            LocalizedString FULL = new LocalizedStringImpl("glowstone.kick.full");

            LocalizedString WHITELIST = new LocalizedStringImpl("glowstone.kick.whitelist");
        }

        interface Player {
            LocalizedString JOINED = new LocalizedStringImpl("glowstone.player.joined");

            LocalizedString LEFT = new LocalizedStringImpl("glowstone.player.left");
        }

        interface Entity {
            LocalizedString UNKNOWN_TYPE_WITH_ID =
                    new LocalizedStringImpl("glowstone.entity.unknown-type-no-id");
            LocalizedString UNKNOWN_TYPE_NO_ID =
                    new LocalizedStringImpl("glowstone.entity.unknown-type-with-id");
        }
    }
}
