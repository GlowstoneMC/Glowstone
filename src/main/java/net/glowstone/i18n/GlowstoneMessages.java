package net.glowstone.i18n;

public interface GlowstoneMessages {
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
