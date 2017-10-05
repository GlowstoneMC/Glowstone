package net.glowstone.block.function;

import lombok.Data;

/**
 * Data wrapper class to connect functionality names with their functions
 */
@Data
public class ItemFunctionality {
    String name;
    Class<ItemFunction> function;
}
