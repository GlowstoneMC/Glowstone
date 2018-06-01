package net.glowstone.util.mojangson.ex;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class MojangsonParseException extends Exception {

    @Getter
    private ParseExceptionReason reason;

    public MojangsonParseException(String message, ParseExceptionReason reason) {
        super(message);
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return reason.getMessage() + ": " + super.getMessage();
    }

    @RequiredArgsConstructor
    public enum ParseExceptionReason {
        INVALID_FORMAT_NUM("Given value is not numerical"),
        UNEXPECTED_SYMBOL("Unexpected symbol in Mojangson string"),
        INCOMPATIBLE_TYPE("List does not support given tag type.");

        @Getter
        private final String message;
    }
}
