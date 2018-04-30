package net.glowstone.i18n;

import java.util.logging.Level;

public interface LocalizedStrings {
    interface Console {
        interface Error {
            interface Biome {
                LoggableLocalizedString UNKNOWN = new LoggableLocalizedStringImpl(
                        "console.biome.unknown", Level.SEVERE
                );
            }

            LoggableLocalizedString CLASSPATH = new LoggableLocalizedStringImpl(
                    "console.classpath.load-failed", Level.WARNING
            );

            interface Import {
                LoggableLocalizedString NO_MESSAGE = new LoggableLocalizedStringImpl(
                        "console.import.failed.no-message", Level.WARNING
                );

                LoggableLocalizedString WITH_MESSAGE = new LoggableLocalizedStringImpl(
                        "console.import.failed.with-message", Level.WARNING
                );
            }

            LoggableLocalizedString LOOTING_MANAGER = new LoggableLocalizedStringImpl(
                    "console.looting-manager.load-failed", Level.SEVERE
            );

            interface Permission {
                LoggableLocalizedString INVALID = new LoggableLocalizedStringImpl(
                        "console.permission.invalid", Level.SEVERE
                );
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
            interface Entity {
                LoggableLocalizedString LOADING_ERROR = new LoggableLocalizedStringImpl(
                        "console.warn.entity.loading-error", Level.WARNING
                );

                LoggableLocalizedString UNKNOWN = new LoggableLocalizedStringImpl(
                        "console.warn.entity.unknown", Level.WARNING
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

                LoggableLocalizedString IO = new LoggableLocalizedStringImpl(
                        "console.plugin.ioexception", Level.WARNING
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

        interface Entity {
            LocalizedString UNKNOWN_TYPE_WITH_ID =
                    new LocalizedStringImpl("glowstone.entity.unknown-type-no-id");
            LocalizedString UNKNOWN_TYPE_NO_ID =
                    new LocalizedStringImpl("glowstone.entity.unknown-type-with-id");
        }
    }
}
