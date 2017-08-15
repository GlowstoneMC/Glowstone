package net.glowstone.entity;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class FishingRewardManager {
    private final Multimap<RewardCategory, RewardItem> values = MultimapBuilder.enumKeys(RewardCategory.class).arrayListValues().build();

    public FishingRewardManager() {
        YamlConfiguration builtinValues = YamlConfiguration.loadConfiguration(
            new InputStreamReader(getClass().getClassLoader().getResourceAsStream("builtin/fishingRewards.yml")));

        registerBuiltins(builtinValues);
    }

    private void registerBuiltins(ConfigurationSection mainSection) {
        ConfigurationSection valuesSection = mainSection.getConfigurationSection("rewards");
        Set<String> categories = valuesSection.getKeys(false);
        for (String strCategorie : categories) {
            RewardCategory category = RewardCategory.valueOf(strCategorie);
            ConfigurationSection categorySection = valuesSection.getConfigurationSection(strCategorie);
            values.put(category, RewardItem.deserialize(categorySection.getValues(true)));
        }
    }


    @Getter
    private enum RewardCategory {
        FISH(85.0, -1.15),
        TREASURE(5.0, 2.1),
        TRASH(10.0, -1.95);

        private double chance;

        /**
         * Each level of the "Luck of the Sea" enchantment will modify this categories chance by modifier amount
         */
        private double modifier;

        RewardCategory(double chance, double modifier) {
            this.chance = chance;
            this.modifier = modifier;
        }
    }

    @Getter
    @AllArgsConstructor
    private static class RewardItem implements ConfigurationSerializable {
        private ItemStack item;
        /**
         * Chance to get this item in his category
         */
        private double chance;

        private RewardItem() { }

        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> args = new HashMap<>();
            args.put("item", item.serialize());
            args.put("chance", chance);
            return args;
        }

        public static RewardItem deserialize(Map<String, Object> args) {
            RewardItem result = new RewardItem();
            if (args.containsKey("item")) {
                result.item = ItemStack.deserialize((Map<String, Object>) args.get("item"));
            }

            if (args.containsKey("chance")) {
                result.item = (ItemStack) args.get("chance");
            }

            return result;
        }
    }
}
