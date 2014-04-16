package net.glowstone.entity.meta;

/**
 * Container for global player properties (such as textures) returned by the auth servers.
 */
public final class PlayerProperty {

    /**
     * The name of the property.
     */
    private final String name;

    /**
     * The property's value.
     */
    private final String value;

    /**
     * The signature of the value for validation.
     */
    private final String signature;

    /**
     * Construct a property with the given fields.
     * @param name The name of the property.
     * @param value The property's value.
     * @param signature The signature of the value for validation.
     */
    public PlayerProperty(String name, String value, String signature) {
        this.name = name;
        this.value = value;
        this.signature = signature;
    }

    /**
     * Get the name.
     * @return The name of the property.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the value.
     * @return The property's value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the signature.
     * @return The signature of the value for validation.
     */
    public String getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return "PlayerProperty{" +
                "name='" + name + '\'' +
                '}';
    }
}
