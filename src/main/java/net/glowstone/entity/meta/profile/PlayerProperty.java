package net.glowstone.entity.meta.profile;

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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerProperty that = (PlayerProperty) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (signature != null ? !signature.equals(that.signature) : that.signature != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (signature != null ? signature.hashCode() : 0);
        return result;
    }
}
