package net.glowstone.i18n;

import java.util.logging.Level;

public interface LocalizedStrings {
    interface Console {
        interface Error {
            interface Biome {
                LoggableLocalizedString UNKNOWN = new LoggableLocalizedStringImpl(
                    "console.error.biome.unknown", Level.SEVERE
                );
            }

            LoggableLocalizedString CLASSPATH = new LoggableLocalizedStringImpl(
                "console.error.classpath", Level.WARNING
            );

            interface Import {
                LoggableLocalizedString NO_MESSAGE = new LoggableLocalizedStringImpl(
                    "console.error.import.no-message", Level.WARNING
                );

                LoggableLocalizedString WITH_MESSAGE = new LoggableLocalizedStringImpl(
                    "console.error.import.with-message", Level.WARNING
                );
            }

            LoggableLocalizedString LOOTING_MANAGER = new LoggableLocalizedStringImpl(
                "console.error.looting-manager", Level.SEVERE
            );

            interface Permission {
                LoggableLocalizedString INVALID = new LoggableLocalizedStringImpl(
                    "console.error.permission.invalid", Level.SEVERE
                );
            }

            interface Plugin {
                LoggableLocalizedString LOADING = new LoggableLocalizedStringImpl(
                    "console.error.plugin.loading", Level.SEVERE
                );

                LoggableLocalizedString MKDIR = new LoggableLocalizedStringImpl(
                    "console.error.plugin.mkdir", Level.SEVERE
                );
            }

            interface Profile {
                LoggableLocalizedString INTERRUPTED = new LoggableLocalizedStringImpl(
                    "console.error.profile.interrupted", Level.SEVERE
                );
            }

            interface Rcon {
                LoggableLocalizedString BIND_INTERRUPTED = new LoggableLocalizedStringImpl(
                    "console.error.rcon.bind-interrupted", Level.SEVERE
                );
            }

            LoggableLocalizedString RELOAD = new LoggableLocalizedStringImpl(
                "console.error.reload", Level.SEVERE
            );

            LoggableLocalizedString STARTUP = new LoggableLocalizedStringImpl(
                "console.error.startup", Level.SEVERE
            );

            interface Structure {
                LoggableLocalizedString UNKNOWN_PIECE_TYPE = new LoggableLocalizedStringImpl(
                    "console.error.structure.unknown-piece-type", Level.SEVERE
                );
            }

            interface Uuid {
                LoggableLocalizedString INTERRUPTED = new LoggableLocalizedStringImpl(
                    "console.error.uuid.interrupted", Level.SEVERE
                );
            }
        }

        interface Info {
            LoggableLocalizedString CONFIG_ONLY_DONE = new LoggableLocalizedStringImpl(
                "console.info.config-only-done", Level.INFO
            );

            interface Icon {
                LoggableLocalizedString IMPORT = new LoggableLocalizedStringImpl(
                    "console.info.icon.import", Level.INFO
                );
            }

            LoggableLocalizedString IMPORT = new LoggableLocalizedStringImpl(
                "console.info.import", Level.INFO
            );

            interface NativeTransport {
                LoggableLocalizedString EPOLL = new LoggableLocalizedStringImpl(
                    "console.info.native-transport.epoll", Level.INFO
                );

                LoggableLocalizedString KQUEUE = new LoggableLocalizedStringImpl(
                    "console.info.native-transport.kqueue", Level.INFO
                );
            }

            interface Opencl {
                LoggableLocalizedString BEST = new LoggableLocalizedStringImpl(
                    "console.info.opencl.best", Level.INFO
                );
                LoggableLocalizedString BEST_VERSION_TIEBREAKER = new LoggableLocalizedStringImpl(
                    "console.info.opencl.best.version-tiebreaker", Level.INFO
                );

                LoggableLocalizedString CPU = new LoggableLocalizedStringImpl(
                    "console.info.opencl.cpu", Level.INFO
                );

                LoggableLocalizedString FOUND_DEVICE = new LoggableLocalizedStringImpl(
                    "console.info.opencl.found-device", Level.INFO
                );

                LoggableLocalizedString INTEL_GPU = new LoggableLocalizedStringImpl(
                    "console.info.opencl.intel-gpu", Level.INFO
                );

                LoggableLocalizedString NO_DEVICE = new LoggableLocalizedStringImpl(
                    "console.info.opencl.no-device", Level.INFO
                );

                LoggableLocalizedString REQUIRED_EXTENSIONS = new LoggableLocalizedStringImpl(
                    "console.info.opencl.required-extensions", Level.INFO
                );

                LoggableLocalizedString REQUIRED_VERSION = new LoggableLocalizedStringImpl(
                    "console.info.opencl.required-version", Level.INFO
                );
            }

            interface Option {
                LoggableLocalizedString HELP = new LoggableLocalizedStringImpl(
                    "console.info.option.help", Level.INFO
                );
            }

            interface Plugin {
                LoggableLocalizedString COUNTS = new LoggableLocalizedStringImpl(
                    "console.info.plugin.counts", Level.INFO
                );

                LoggableLocalizedString SCANNING = new LoggableLocalizedStringImpl(
                    "console.info.plugin.scanning", Level.INFO
                );
            }

            LoggableLocalizedString PROXY = new LoggableLocalizedStringImpl(
                "console.info.proxy", Level.INFO
            );

            interface Proxy {
                LoggableLocalizedString ONLINE = new LoggableLocalizedStringImpl(
                    "console.info.proxy.online", Level.INFO
                );
            }

            LoggableLocalizedString READY = new LoggableLocalizedStringImpl(
                "console.info.ready", Level.INFO
            );

            LoggableLocalizedString SAVE = new LoggableLocalizedStringImpl(
                "console.info.save", Level.INFO
            );

            LoggableLocalizedString SHUTDOWN = new LoggableLocalizedStringImpl(
                "console.info.shutdown", Level.INFO
            );

            interface Version {
                LoggableLocalizedString BUKKIT = new LoggableLocalizedStringImpl(
                    "console.info.version.bukkit", Level.INFO
                );

                LoggableLocalizedString GLOWSTONE = new LoggableLocalizedStringImpl(
                    "console.info.version.glowstone", Level.INFO
                );

                LoggableLocalizedString MINECRAFT_CLIENT = new LoggableLocalizedStringImpl(
                    "console.info.version.minecraft-client", Level.INFO
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
                        "console.warn.event.interrupted", Level.WARNING
                );

                LoggableLocalizedString SHUTDOWN = new LoggableLocalizedStringImpl(
                        "console.warn.event.shutdown", Level.WARNING
                );
            }

            interface Icon {
                LoggableLocalizedString LOAD_FAILED_IMPORT = new LoggableLocalizedStringImpl(
                    "console.warn.icon.load-failed.import", Level.WARNING
                );

                LoggableLocalizedString LOAD_FAILED = new LoggableLocalizedStringImpl(
                    "console.warn.icon.load-failed", Level.WARNING
                );
            }

            LoggableLocalizedString OFFLINE = new LoggableLocalizedStringImpl(
                "console.warn.offline", Level.WARNING
            );

            interface Option {
                LoggableLocalizedString INVALID = new LoggableLocalizedStringImpl(
                    "console.warn.option.invalid", Level.WARNING
                );

                LoggableLocalizedString NO_VALUE = new LoggableLocalizedStringImpl(
                    "console.warn.option.no-value", Level.WARNING
                );
            }

            interface Permission {
                LoggableLocalizedString DUPLICATE = new LoggableLocalizedStringImpl(
                    "console.warn.permission.duplicate", Level.WARNING
                );
            }

            interface Plugin {
                LoggableLocalizedString NO_SPONGE = new LoggableLocalizedStringImpl(
                    "console.warn.plugin.no-sponge", Level.WARNING
                );

                LoggableLocalizedString UNRECOGNIZED = new LoggableLocalizedStringImpl(
                    "console.warn.plugin.unrecognized", Level.WARNING
                );

                LoggableLocalizedString MALFORMED_URL = new LoggableLocalizedStringImpl(
                        "console.warn.plugin.malformed-url", Level.WARNING
                );

                LoggableLocalizedString IO = new LoggableLocalizedStringImpl(
                        "console.warn.plugin.io", Level.WARNING
                );

                LoggableLocalizedString BUKKIT2SPONGE = new LoggableLocalizedStringImpl(
                    "console.warn.plugin.no-sponge.bukkit2sponge", Level.WARNING
                );

                LoggableLocalizedString PERMISSION_DUPLICATE = new LoggableLocalizedStringImpl(
                    "console.warn.plugin.permission.duplicate", Level.WARNING
                );

                LoggableLocalizedString UNSUPPORTED = new LoggableLocalizedStringImpl(
                    "console.warn.plugin.unsupported", Level.WARNING
                );

                LoggableLocalizedString UNSUPPORTED_CANARY = new LoggableLocalizedStringImpl(
                    "console.warn.plugin.unsupported.canary", Level.WARNING
                );

                LoggableLocalizedString UNSUPPORTED_FORGE = new LoggableLocalizedStringImpl(
                    "console.warn.plugin.unsupported.forge", Level.WARNING
                );

                LoggableLocalizedString UNSUPPORTED_OTHER = new LoggableLocalizedStringImpl(
                    "console.warn.plugin.unsupported.other", Level.WARNING
                );

                LoggableLocalizedString UNSUPPORTED_SPONGE = new LoggableLocalizedStringImpl(
                    "console.warn.plugin.unsupported.sponge", Level.WARNING
                );
            }

            interface Profile {
                LoggableLocalizedString TIMEOUT = new LoggableLocalizedStringImpl(
                    "console.warn.profile.timeout", Level.WARNING
                );
            }

            interface Uuid {
                LoggableLocalizedString TIMEOUT = new LoggableLocalizedStringImpl(
                    "console.warn.uuid.timeout", Level.WARNING
                );
            }

            interface WorldGen {
                LoggableLocalizedString DISABLED = new LoggableLocalizedStringImpl(
                    "console.warn.worldgen.disabled", Level.WARNING
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
