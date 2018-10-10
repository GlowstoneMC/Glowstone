package net.glowstone.i18n;

public interface GlowstoneMessages {
    interface Advancement {
        LocalizedString TITLE = new LocalizedStringImpl("glowstone.advancement.title");
    }

    interface Bed {
        LocalizedString DAY = new LocalizedStringImpl("glowstone.bed.day");

        LocalizedString MOB = new LocalizedStringImpl("glowstone.bed.mob");

        LocalizedString OCCUPIED = new LocalizedStringImpl("glowstone.bed.occupied");
    }

    interface Block {
        LocalizedString MAX_HEIGHT = new LocalizedStringImpl("glowstone.block.max-height");
    }

    interface Command {
        interface Error {
            LocalizedString UNKNOWN_COMMAND =
                new LocalizedStringImpl("glowstone.command.error.unknown-command");
        }
    }

    interface GameMode {
        LocalizedString NAMES = new LocalizedStringImpl("glowstone.gamemode.names");
        LocalizedString UNKNOWN = new LocalizedStringImpl("glowstone.gamemode.unknown");
    }

    interface Entity {
        LocalizedString UNKNOWN_TYPE_WITH_ID =
                new LocalizedStringImpl("glowstone.entity.unknown-type-no-id");
        LocalizedString UNKNOWN_TYPE_NO_ID =
                new LocalizedStringImpl("glowstone.entity.unknown-type-with-id");
    }

    interface Kick {
        LocalizedString BANNED = new LocalizedStringImpl("glowstone.kick.banned");

        LocalizedString CREATIVE_ITEM = new LocalizedStringImpl("glowstone.kick.creative-item");

        LocalizedString FILE_READ = new LocalizedStringImpl("glowstone.kick.file-read");

        LocalizedString FILE_WRITE = new LocalizedStringImpl("glowstone.kick.file-write");

        LocalizedString FULL = new LocalizedStringImpl("glowstone.kick.full");

        LocalizedString WHITELIST = new LocalizedStringImpl("glowstone.kick.whitelist");

        interface Crypt {
            LocalizedString HASH_FAILED = new LocalizedStringImpl(
                    "glowstone.kick.crypt.sha1-failed");

            LocalizedString RSA_INIT_FAILED = new LocalizedStringImpl(
                    "glowstone.kick.crypt.rsa-init-failed");

            LocalizedString SHARED_SECRET = new LocalizedStringImpl(
                    "glowstone.kick.crypt.shared-secret");

            LocalizedString VERIFY_TOKEN = new LocalizedStringImpl(
                    "glowstone.kick.crypt.verify-token");

            LocalizedString AUTH_FAILED = new LocalizedStringImpl(
                    "glowstone.kick.crypt.user-auth");

            LocalizedString AUTH_INTERNAL = new LocalizedStringImpl(
                    "glowstone.kick.crypt.auth-internal");

            LocalizedString BAD_UUID = new LocalizedStringImpl("glowstone.kick.crypt.invalid-uuid");
        }
    }

    interface Player {
        LocalizedString JOINED = new LocalizedStringImpl("glowstone.player.joined");

        LocalizedString LEFT = new LocalizedStringImpl("glowstone.player.left");
    }
}
