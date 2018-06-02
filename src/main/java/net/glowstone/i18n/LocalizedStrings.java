package net.glowstone.i18n;

import java.util.logging.Level;

public interface LocalizedStrings {
    interface Console {
        interface Error {
            interface Biome {
                LoggedLocalizedString UNKNOWN = new LoggedLocalizedString(
                        "console.biome.unknown", Level.SEVERE
                );
            }

            interface BlockEntity {
                LoggedLocalizedString READ_ERROR = new LoggedLocalizedString(
                        "console.block-entity.read-error", Level.SEVERE
                );
            }

            LoggedLocalizedString CLASSPATH = new LoggedLocalizedString(
                    "console.classpath.load-failed", Level.WARNING
            );

            interface Function {
                LoggedLocalizedString FILE_READ = new LoggedLocalizedString(
                        "console.function.load-failed", Level.SEVERE
                );
            }

            interface Import {
                LoggedLocalizedString NO_MESSAGE = new LoggedLocalizedString(
                        "console.import.failed.no-message", Level.WARNING
                );

                LoggedLocalizedString WITH_MESSAGE = new LoggedLocalizedString(
                        "console.import.failed.with-message", Level.WARNING
                );
            }

            interface Io {
                LoggedLocalizedString MKDIR = new LoggedLocalizedString(
                        "console.io.mkdir-failed", Level.SEVERE
                );

                LoggedLocalizedString PLAYER_READ = new LoggedLocalizedString(
                        "console.io.player-read-failed", Level.SEVERE);

                LoggedLocalizedString PLAYER_READ_UNKNOWN = new LoggedLocalizedString(
                        "console.io.player-read-failed-unknown", Level.SEVERE);

                LoggedLocalizedString PLAYER_WRITE = new LoggedLocalizedString(
                        "console.io.player-write-failed", Level.SEVERE);

                LoggedLocalizedString WORLD_READ = new LoggedLocalizedString(
                        "console.io.world-read-failed", Level.SEVERE
                );
            }

            LoggedLocalizedString LOOTING_MANAGER = new LoggedLocalizedString(
                    "console.looting-manager.load-failed", Level.SEVERE
            );

            interface Manager {
                LoggedLocalizedString COMMAND = new LoggedLocalizedString(
                    "console.manager.command-failed", Level.WARNING
                );

                LoggedLocalizedString COMMAND_READ = new LoggedLocalizedString(
                    "console.manager.command-read-failed", Level.SEVERE
                );

                LoggedLocalizedString LOG_FOLDER = new LoggedLocalizedString(
                    "console.manager.log-folder-failed", Level.WARNING
                );

                LoggedLocalizedString LOG_FILE = new LoggedLocalizedString(
                    "console.manager.log-file-failed", Level.SEVERE
                );

                LoggedLocalizedString TAB_COMPLETE = new LoggedLocalizedString(
                    "console.manager.tab-complete-failed", Level.WARNING
                );
            }

            interface Permission {
                LoggedLocalizedString INVALID = new LoggedLocalizedString(
                        "console.permission.invalid", Level.SEVERE
                );
            }

            interface Plugin {
                LoggedLocalizedString LOADING = new LoggedLocalizedString(
                        "console.plugin.load-failed", Level.SEVERE
                );

                LoggedLocalizedString MKDIR = new LoggedLocalizedString(
                        "console.plugin.mkdir-failed", Level.SEVERE
                );
            }

            interface Profile {
                LoggedLocalizedString INTERRUPTED = new LoggedLocalizedString(
                        "console.profile.interrupted", Level.SEVERE
                );
            }

            interface Rcon {
                LoggedLocalizedString BIND_INTERRUPTED = new LoggedLocalizedString(
                        "console.rcon.bind-interrupted", Level.SEVERE
                );
            }

            LoggedLocalizedString RELOAD = new LoggedLocalizedString(
                    "console.reload-failed", Level.SEVERE
            );

            LoggedLocalizedString STARTUP = new LoggedLocalizedString(
                    "console.startup-failed", Level.SEVERE
            );

            interface Structure {
                LoggedLocalizedString IO_READ = new LoggedLocalizedString(
                        "console.structure.io-read", Level.SEVERE
                );

                LoggedLocalizedString IO_WRITE = new LoggedLocalizedString(
                        "console.structure.io-write", Level.SEVERE
                );

                LoggedLocalizedString NO_DATA = new LoggedLocalizedString(
                        "console.structure.no-data", Level.SEVERE
                );

                LoggedLocalizedString UNKNOWN_PIECE_TYPE = new LoggedLocalizedString(
                        "console.structure.unknown-piece-type", Level.SEVERE
                );
            }

            interface Uuid {
                LoggedLocalizedString INTERRUPTED = new LoggedLocalizedString(
                        "console.uuid.interrupted", Level.SEVERE
                );
            }

        }

        interface Info {
            LoggedLocalizedString CONFIG_ONLY_DONE = new LoggedLocalizedString(
                    "console.config-only-done", Level.INFO
            );

            interface Icon {
                LoggedLocalizedString IMPORT = new LoggedLocalizedString(
                        "console.icon.import", Level.INFO
                );
            }

            LoggedLocalizedString IMPORT = new LoggedLocalizedString(
                    "console.import", Level.INFO
            );

            interface Manager {
                LoggedLocalizedString ROTATE = new LoggedLocalizedString(
                    "console.manager.log-rotate", Level.INFO
                );
            }

            interface NativeTransport {
                LoggedLocalizedString EPOLL = new LoggedLocalizedString(
                        "console.native-transport.epoll", Level.INFO
                );

                LoggedLocalizedString KQUEUE = new LoggedLocalizedString(
                        "console.native-transport.kqueue", Level.INFO
                );
            }

            interface Opencl {
                LoggedLocalizedString BEST = new LoggedLocalizedString(
                        "console.opencl.best", Level.INFO
                );
                LoggedLocalizedString BEST_VERSION_TIEBREAKER = new LoggedLocalizedString(
                        "console.opencl.best.version-tiebreaker", Level.INFO
                );

                LoggedLocalizedString CPU = new LoggedLocalizedString(
                        "console.opencl.cpu", Level.INFO
                );

                LoggedLocalizedString FOUND_DEVICE = new LoggedLocalizedString(
                        "console.opencl.found-device", Level.INFO
                );

                LoggedLocalizedString INTEL_GPU = new LoggedLocalizedString(
                        "console.opencl.intel-gpu", Level.INFO
                );

                LoggedLocalizedString NO_DEVICE = new LoggedLocalizedString(
                        "console.opencl.no-device", Level.INFO
                );

                LoggedLocalizedString REQUIRED_EXTENSIONS = new LoggedLocalizedString(
                        "console.opencl.required-extensions", Level.INFO
                );

                LoggedLocalizedString REQUIRED_VERSION = new LoggedLocalizedString(
                        "console.opencl.required-version", Level.INFO
                );
            }

            interface Option {
                LoggedLocalizedString HELP = new LoggedLocalizedString(
                        "console.option.help", Level.INFO
                );
            }

            interface Plugin {
                LoggedLocalizedString COUNTS = new LoggedLocalizedString(
                        "console.plugin.counts", Level.INFO
                );

                LoggedLocalizedString SCANNING = new LoggedLocalizedString(
                        "console.plugin.scanning", Level.INFO
                );
            }

            LoggedLocalizedString PROXY = new LoggedLocalizedString(
                    "console.proxy", Level.INFO
            );

            interface Proxy {
                LoggedLocalizedString ONLINE = new LoggedLocalizedString(
                        "console.proxy.online", Level.INFO
                );
            }

            LoggedLocalizedString READY = new LoggedLocalizedString(
                    "console.ready", Level.INFO
            );

            LoggedLocalizedString RECIPE_COUNTS = new LoggedLocalizedString(
                    "console.recipe.counts", Level.INFO
            );

            LoggedLocalizedString SAVE = new LoggedLocalizedString(
                    "console.save", Level.INFO
            );

            LoggedLocalizedString SHUTDOWN = new LoggedLocalizedString(
                    "console.shutdown", Level.INFO
            );

            interface Version {
                LoggedLocalizedString BUKKIT = new LoggedLocalizedString(
                        "console.version.bukkit", Level.INFO
                );

                LoggedLocalizedString GLOWSTONE = new LoggedLocalizedString(
                        "console.version.glowstone", Level.INFO
                );

                LoggedLocalizedString MINECRAFT_CLIENT = new LoggedLocalizedString(
                        "console.version.minecraft-client", Level.INFO
                );
            }
        }

        interface Warn {

            interface BlockEntity {
                LoggedLocalizedString UNKNOWN = new LoggedLocalizedString(
                        "console.block-entity.unknown", Level.WARNING
                );
            }

            interface Chunk {
                LoggedLocalizedString SECTION_DUP = new LoggedLocalizedString(
                        "console.chunk.section-dup", Level.WARNING
                );

                LoggedLocalizedString SECTION_OOB = new LoggedLocalizedString(
                        "console.chunk.section-oob", Level.WARNING
                );

                LoggedLocalizedString UNKNOWN_BLOCK_TO_TICK = new LoggedLocalizedString(
                        "console.chunk.unknown-block-to-tick", Level.WARNING
                );
            }

            interface Entity {
                LoggedLocalizedString LOADING_ERROR = new LoggedLocalizedString(
                        "console.entity.loading-error", Level.WARNING
                );

                LoggedLocalizedString UNKNOWN = new LoggedLocalizedString(
                        "console.entity.unknown", Level.WARNING
                );
            }

            interface Event {
                LoggedLocalizedString INTERRUPTED = new LoggedLocalizedString(
                        "console.event.interrupted", Level.WARNING
                );

                LoggedLocalizedString SHUTDOWN = new LoggedLocalizedString(
                        "console.event.shutdown", Level.WARNING
                );
            }

            interface Icon {
                LoggedLocalizedString LOAD_FAILED_IMPORT = new LoggedLocalizedString(
                        "console.icon.load-failed.import", Level.WARNING
                );

                LoggedLocalizedString LOAD_FAILED = new LoggedLocalizedString(
                        "console.icon.load-failed", Level.WARNING
                );
            }

            interface Io {
                LoggedLocalizedString JSON_STAT_UNKNOWN = new LoggedLocalizedString(
                        "console.io.json.stat-unknown", Level.WARNING
                );

                LoggedLocalizedString MKDIR_FAILED = new LoggedLocalizedString(
                        "console.io.mkdir-failed", Level.WARNING
                );

                LoggedLocalizedString NO_WORLD_DATA_TAG = new LoggedLocalizedString(
                        "console.io.no-world-data-tag", Level.WARNING
                );

                LoggedLocalizedString REMOVING_SINGLE_PLAYER = new LoggedLocalizedString(
                        "console.io.removing-single-player", Level.WARNING
                );
            }

            LoggedLocalizedString OFFLINE = new LoggedLocalizedString(
                    "console.offline", Level.WARNING
            );

            interface Option {
                LoggedLocalizedString INVALID = new LoggedLocalizedString(
                        "console.option.invalid", Level.WARNING
                );

                LoggedLocalizedString NO_VALUE = new LoggedLocalizedString(
                        "console.option.no-value", Level.WARNING
                );
            }

            interface Permission {
                LoggedLocalizedString DUPLICATE = new LoggedLocalizedString(
                        "console.permission.duplicate", Level.WARNING
                );
            }

            interface Plugin {
                LoggedLocalizedString NO_SPONGE = new LoggedLocalizedString(
                        "console.plugin.no-sponge", Level.WARNING
                );

                LoggedLocalizedString UNRECOGNIZED = new LoggedLocalizedString(
                        "console.plugin.unrecognized", Level.WARNING
                );

                LoggedLocalizedString MALFORMED_URL = new LoggedLocalizedString(
                        "console.plugin.malformed-url", Level.WARNING
                );

                LoggedLocalizedString IO = new LoggedLocalizedString(
                        "console.plugin.ioexception", Level.WARNING
                );

                LoggedLocalizedString BUKKIT2SPONGE = new LoggedLocalizedString(
                        "console.plugin.no-sponge.bukkit2sponge", Level.WARNING
                );

                LoggedLocalizedString PERMISSION_DUPLICATE = new LoggedLocalizedString(
                        "console.plugin.permission.duplicate", Level.WARNING
                );

                LoggedLocalizedString UNSUPPORTED = new LoggedLocalizedString(
                        "console.plugin.unsupported", Level.WARNING
                );

                LoggedLocalizedString UNSUPPORTED_CANARY = new LoggedLocalizedString(
                        "console.plugin.unsupported.canary", Level.WARNING
                );

                LoggedLocalizedString UNSUPPORTED_FORGE = new LoggedLocalizedString(
                        "console.plugin.unsupported.forge", Level.WARNING
                );

                LoggedLocalizedString UNSUPPORTED_OTHER = new LoggedLocalizedString(
                        "console.plugin.unsupported.other", Level.WARNING
                );

                LoggedLocalizedString UNSUPPORTED_SPONGE = new LoggedLocalizedString(
                        "console.plugin.unsupported.sponge", Level.WARNING
                );
            }

            interface Profile {
                LoggedLocalizedString TIMEOUT = new LoggedLocalizedString(
                        "console.profile.timeout", Level.WARNING
                );
            }

            interface Recipe {
                LoggedLocalizedString NO_DEFAULTS = new LoggedLocalizedString(
                        "console.recipe.no-defaults", Level.WARNING
                );
            }

            interface Uuid {
                LoggedLocalizedString TIMEOUT = new LoggedLocalizedString(
                        "console.uuid.timeout", Level.WARNING
                );
            }

            interface WorldGen {
                LoggedLocalizedString DISABLED = new LoggedLocalizedString(
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
