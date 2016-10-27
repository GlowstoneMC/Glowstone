package net.glowstone.entity.meta.profile;

import lombok.Data;

/**
 * Container for global player properties (such as textures) returned by the auth servers.
 */
@Data
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

}
