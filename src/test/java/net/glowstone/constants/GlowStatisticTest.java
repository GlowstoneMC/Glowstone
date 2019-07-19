package net.glowstone.constants;

import static org.junit.Assert.assertEquals;

import net.glowstone.testutils.ServerShim;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GlowStatisticTest {

    @BeforeEach
    public void beforeEach() {
        ServerShim.install();
    }

    @Test
    public void testSimpleStatistic() {
        assertEquals("stat.entityKilledBy", GlowStatistic.getName(Statistic.ENTITY_KILLED_BY));
        assertEquals("stat.drop", GlowStatistic.getName(Statistic.DROP));
        assertEquals("stat.jump", GlowStatistic.getName(Statistic.JUMP));
        assertEquals("stat.chestOpened", GlowStatistic.getName(Statistic.CHEST_OPENED));
        assertEquals("stat.playOneMinute", GlowStatistic.getName(Statistic.PLAY_ONE_MINUTE));
    }

    @Test
    public void testEntityStatistic() {
        assertEquals("stat.entityKilledBy.Enderman", GlowStatistic.getName(Statistic.ENTITY_KILLED_BY, EntityType.ENDERMAN));
        assertEquals("stat.entityKilledBy.PigZombie", GlowStatistic.getName(Statistic.ENTITY_KILLED_BY, EntityType.PIG_ZOMBIE));

        assertEquals("stat.killEntity.Enderman", GlowStatistic.getName(Statistic.KILL_ENTITY, EntityType.ENDERMAN));
        assertEquals("stat.killEntity.CaveSpider", GlowStatistic.getName(Statistic.KILL_ENTITY, EntityType.CAVE_SPIDER));
    }

    @Test
    public void testMaterialStatistic() {
        assertEquals("stat.drop.minecraft.dirt", GlowStatistic.getName(Statistic.DROP, Material.DIRT));
        assertEquals("stat.drop.minecraft.stone", GlowStatistic.getName(Statistic.DROP, Material.STONE));
        assertEquals("stat.drop.minecraft.gold_ore", GlowStatistic.getName(Statistic.DROP, Material.GOLD_ORE));

        assertEquals("stat.breakItem.minecraft.bow", GlowStatistic.getName(Statistic.BREAK_ITEM, Material.BOW));
        assertEquals("stat.breakItem.minecraft.iron_shovel", GlowStatistic.getName(Statistic.BREAK_ITEM, Material.IRON_SHOVEL));
        assertEquals("stat.breakItem.minecraft.golden_pickaxe", GlowStatistic.getName(Statistic.BREAK_ITEM, Material.GOLDEN_PICKAXE));

        assertEquals("stat.pickup.minecraft.iron_shovel", GlowStatistic.getName(Statistic.PICKUP, Material.IRON_SHOVEL));
        assertEquals("stat.pickup.minecraft.golden_axe", GlowStatistic.getName(Statistic.PICKUP, Material.GOLDEN_AXE));
        assertEquals("stat.pickup.minecraft.wooden_axe", GlowStatistic.getName(Statistic.PICKUP, Material.WOODEN_AXE));

        assertEquals("stat.mineBlock.minecraft.dirt", GlowStatistic.getName(Statistic.MINE_BLOCK, Material.DIRT));
        assertEquals("stat.mineBlock.minecraft.gold_ore", GlowStatistic.getName(Statistic.MINE_BLOCK, Material.GOLD_ORE));

        assertEquals("stat.useItem.minecraft.golden_pickaxe", GlowStatistic.getName(Statistic.USE_ITEM, Material.GOLDEN_PICKAXE));
        assertEquals("stat.useItem.minecraft.diamond_axe", GlowStatistic.getName(Statistic.USE_ITEM, Material.DIAMOND_AXE));

        assertEquals("stat.craftItem.minecraft.iron_shovel", GlowStatistic.getName(Statistic.CRAFT_ITEM, Material.IRON_SHOVEL));
        assertEquals("stat.craftItem.minecraft.golden_pickaxe", GlowStatistic.getName(Statistic.CRAFT_ITEM, Material.GOLDEN_PICKAXE));
    }
}
