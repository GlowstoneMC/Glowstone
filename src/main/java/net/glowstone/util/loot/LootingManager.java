package net.glowstone.util.loot;

import net.glowstone.GlowServer;
import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.util.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LootingManager {

    private static final Map<EntityType, EntityLootTable> entities = new HashMap<>();

    public static void load() throws Exception {
        String baseDir = "builtin/loot/entities/";
        register(EntityType.BAT, baseDir + "bat.json");
        register(EntityType.BLAZE, baseDir + "blaze.json");
        register(EntityType.CAVE_SPIDER, baseDir + "cave_spider.json");
        register(EntityType.CHICKEN, baseDir + "chicken.json");
        register(EntityType.COW, baseDir + "cow.json");
        register(EntityType.CREEPER, baseDir + "creeper.json");
        register(EntityType.DONKEY, baseDir + "horse.json");
        register(EntityType.ELDER_GUARDIAN, baseDir + "elder_guardian.json");
        register(EntityType.ENDER_DRAGON, baseDir + "ender_dragon.json");
        register(EntityType.ENDERMAN, baseDir + "enderman.json");
        register(EntityType.ENDERMITE, baseDir + "endermite.json");
        register(EntityType.EVOKER, baseDir + "evocation_illager.json");
        register(EntityType.GHAST, baseDir + "ghast.json");
        register(EntityType.GUARDIAN, baseDir + "guardian.json");
        register(EntityType.HORSE, baseDir + "horse.json");
        register(EntityType.HUSK, baseDir + "zombie.json");
        register(EntityType.IRON_GOLEM, baseDir + "iron_golem.json");
        register(EntityType.MAGMA_CUBE, baseDir + "magma_cube.json");
        register(EntityType.MULE, baseDir + "horse.json");
        register(EntityType.MUSHROOM_COW, baseDir + "mushroom_cow.json");
        register(EntityType.LLAMA, baseDir + "llama.json");
        register(EntityType.OCELOT, baseDir + "ocelot.json");
        register(EntityType.PARROT, baseDir + "parrot.json");
        register(EntityType.PIG, baseDir + "pig.json");
        register(EntityType.PIG_ZOMBIE, baseDir + "pig_zombie.json");
        register(EntityType.POLAR_BEAR, baseDir + "polar_bear.json");
        register(EntityType.RABBIT, baseDir + "rabbit.json");
        register(EntityType.SHEEP, baseDir + "sheep.json");
        register(EntityType.SHULKER, baseDir + "shulker.json");
        register(EntityType.SILVERFISH, baseDir + "silverfish.json");
        register(EntityType.SKELETON, baseDir + "skeleton.json");
        register(EntityType.SKELETON_HORSE, baseDir + "skeleton_horse.json");
        register(EntityType.SLIME, baseDir + "slime.json");
        register(EntityType.SNOWMAN, baseDir + "snowman.json");
        register(EntityType.SPIDER, baseDir + "spider.json");
        register(EntityType.STRAY, baseDir + "skeleton.json");
        register(EntityType.SQUID, baseDir + "squid.json");
        register(EntityType.VEX, baseDir + "vex.json");
        register(EntityType.VILLAGER, baseDir + "villager.json");
        register(EntityType.VINDICATOR, baseDir + "vindication_illager.json");
        register(EntityType.WITCH, baseDir + "witch.json");
        register(EntityType.WITHER, baseDir + "wither.json");
        register(EntityType.WITHER_SKELETON, baseDir + "wither_skeleton.json");
        register(EntityType.WOLF, baseDir + "wolf.json");
        register(EntityType.ZOMBIE, baseDir + "zombie.json");
        register(EntityType.ZOMBIE_HORSE, baseDir + "zombie_horse.json");
        register(EntityType.ZOMBIE_VILLAGER, baseDir + "zombie.json");
    }

    private static void register(EntityType type, String location) throws Exception {
        try {
            InputStream in = LootingManager.class.getClassLoader().getResourceAsStream(location);
            if (in == null) {
                GlowServer.logger.warning("Could not find default entity loot table '" + location + "' on classpath");
                return;
            }
            JSONObject json = (JSONObject) new JSONParser().parse(new InputStreamReader(in));
            entities.put(type, new EntityLootTable(json));
        } catch (Exception e) {
            Exception ex = new Exception("Failed to load loot table '" + location + "': " + e.getClass().getName() + " (" + e.getMessage() + ")");
            ex.setStackTrace(e.getStackTrace());
            throw ex;
        }
    }

    public static LootData generate(GlowLivingEntity entity) {
        if (!entities.containsKey(entity.getType())) {
            return new LootData(InventoryUtil.NO_ITEMS, 0);
        }
        Random random = LootingUtil.random;
        EntityLootTable table = entities.get(entity.getType());
        ArrayList<ItemStack> items = new ArrayList<>();
        for (LootItem lootItem : table.getItems()) {
            DefaultLootItem defaultItem = lootItem.getDefaultItem();
            int count = defaultItem.getCount().generate(random, entity);
            int data = 0;
            if (defaultItem.getData().isPresent()) {
                data = defaultItem.getData().get().generate(random);
            } else if (defaultItem.getReflectiveData().isPresent()) {
                data = ((Number) defaultItem.getReflectiveData().get().process(entity)).intValue();
            }
            String name = defaultItem.getType().generate(random);
            if (name == null) {
                name = "";
            }
            name = name.toUpperCase();

            ConditionalLootItem[] conditions = lootItem.getConditionalItems();
            for (ConditionalLootItem condition : conditions) {
                if (LootingUtil.conditionValue(entity, condition.getCondition())) {
                    if (condition.getCount().isPresent()) {
                        count = condition.getCount().get().generate(random, entity);
                    }
                    if (condition.getType().isPresent()) {
                        name = condition.getType().get().generate(random);
                        if (name == null) {
                            name = "";
                        }
                        name = name.toUpperCase();
                    }
                    if (condition.getData().isPresent()) {
                        data = condition.getData().get().generate(random);
                    } else if (condition.getReflectiveData().isPresent()) {
                        data = ((Number) condition.getReflectiveData().get().process(entity)).intValue();
                    }
                }
            }
            Material material = Material.getMaterial(name);
            if (material != null && count > 0) {
                items.add(new ItemStack(material, count, (byte) data));
            }
        }
        int experience = table.getExperience().generate(random, entity);
        return new LootData(items.toArray(new ItemStack[items.size()]), experience);
    }
}
