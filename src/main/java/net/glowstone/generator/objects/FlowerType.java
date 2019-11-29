package net.glowstone.generator.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@RequiredArgsConstructor
public enum FlowerType {
    DANDELION(Material.DANDELION),
    POPPY(Material.POPPY),
    BLUE_ORCHID(Material.BLUE_ORCHID),
    ALLIUM(Material.ALLIUM),
    HOUSTONIA(Material.AZURE_BLUET),
    TULIP_RED(Material.RED_TULIP),
    TULIP_ORANGE(Material.ORANGE_TULIP),
    TULIP_WHITE(Material.WHITE_TULIP),
    TULIP_PINK(Material.PINK_TULIP),
    OXEYE_DAISY(Material.OXEYE_DAISY);

    @Getter
    private final Material type;
}

