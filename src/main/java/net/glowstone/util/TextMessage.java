package net.glowstone.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Simple container for chat message structures until more advanced chat
 * formatting is implemented.
 */
public final class TextMessage {

    private final JSONObject object;

    /**
     * Construct a new chat message from a simple text string.
     * @param text The text of the message.
     */
    @SuppressWarnings("unchecked")
    public TextMessage(String text) {
        object = new JSONObject();
        object.put("text", text);
    }

    /**
     * Construct a chat message from a JSON structure. No validation occurs.
     * @param object The JSON structure of the message.
     */
    public TextMessage(JSONObject object) {
        this.object = object;
        object.toJSONString();
    }

    /**
     * Encode this chat message to its textual JSON representation.
     * @return The encoded representation.
     */
    public String encode() {
        return object.toJSONString();
    }

    /**
     * Attempt to convert the message to its plaintext representation.
     * @return The plain text, or the empty string on failure.
     */
    public String asPlaintext() {
        if (object.containsKey("text")) {
            Object obj = object.get("text");
            if (obj instanceof String) {
                return (String) obj;
            }
        }
        return "";
    }

    @Override
    public String toString() {
        return "Message" + encode();
    }

    /**
     * Decode a chat message from its textual JSON representation if possible.
     * @param json The encoded representation.
     * @return The decoded TextMessage.
     */
    public static TextMessage decode(String json) {
        JSONParser parser = new JSONParser();
        try {
            Object o = parser.parse(json);
            if (o instanceof JSONObject) {
                return new TextMessage((JSONObject) o);
            } else {
                return new TextMessage(o.toString());
            }
        } catch (ParseException e) {
            return new TextMessage("parse error");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextMessage that = (TextMessage) o;

        if (object != null ? !object.equals(that.object) : that.object != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return object != null ? object.hashCode() : 0;
    }
}
