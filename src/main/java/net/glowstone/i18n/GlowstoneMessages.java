package net.glowstone.i18n;

import com.google.common.collect.ImmutableList;
import java.util.Locale;

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
        ImmutableList<String> NAMES = ImmutableList.copyOf(
                new LocalizedStringImpl("glowstone.gamemode.names").get().split(","));
        ImmutableList<String> NAMES_LOWERCASE = NAMES.stream()
                .map(x -> x.toLowerCase(Locale.getDefault()))
                .collect(ImmutableList.toImmutableList());
        LocalizedString UNKNOWN = new LocalizedStringImpl("glowstone.gamemode.unknown");
    }

    interface Kick {
        LocalizedString BANNED = new LocalizedStringImpl("glowstone.kick.banned");

        LocalizedString CREATIVE_ITEM = new LocalizedStringImpl("glowstone.kick.creative-item");

        LocalizedString FILE_READ = new LocalizedStringImpl("glowstone.kick.file-read");

        LocalizedString FILE_WRITE = new LocalizedStringImpl("glowstone.kick.file-write");

        LocalizedString FULL = new LocalizedStringImpl("glowstone.kick.full");

        LocalizedString WHITELIST = new LocalizedStringImpl("glowstone.kick.whitelist");

        interface Crypt {
            LocalizedString RSA_INIT_FAILED = new LocalizedStringImpl(
                    "glowstone.kick.crypt.rsa-init-failed");
        }
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
