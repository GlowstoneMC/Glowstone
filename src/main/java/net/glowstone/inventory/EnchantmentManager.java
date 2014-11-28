package net.glowstone.inventory;


import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.constants.GlowEnchantment;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.WeightedRandom;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.*;

public class EnchantmentManager {
    private final Random random = new Random();
    private final GlowPlayer player;
    private final GlowEnchantingInventory inventory;
    private int xpSeed;
    private final int[] enchLevelCosts = new int[3];
    private final int[] enchId = new int[3];
    private final int[] enchLevel = new int[3];

    public EnchantmentManager(GlowEnchantingInventory inventory, GlowPlayer player) {
        this.player = player;
        this.inventory = inventory;
        this.xpSeed = player.getXpSeed();
    }

    ////////////////////////////
    // Public functions

    public void invalidate() {
        ItemStack item = inventory.getItem();
        ItemStack resource = inventory.getResource();

        if (item == null || (player.getGameMode() != GameMode.CREATIVE && (resource == null || resource.getType() != Material.INK_SACK || resource.getDurability() != 4))) {
            clearEnch();
        } else {
            calculateNewEnchantsAndLevels();
        }
    }

    public void onPlayerEnchant(int clicked) {
        if (enchLevelCosts[clicked] <= 0 || isMaliciousClicked(clicked)) return;

        ItemStack item = inventory.getItem();

        List<LeveledEnchant> enchants = calculateCurrentEnchants(item, clicked, enchLevelCosts[clicked]);
        if (enchants == null) enchants = new ArrayList<>();

        EnchantItemEvent event = EventFactory.callEvent(new EnchantItemEvent(player, player.getOpenInventory(), inventory.getLocation().getBlock(), item.clone(), enchLevelCosts[clicked], toMap(enchants), clicked));
        if (event.isCancelled() || (player.getGameMode() != GameMode.CREATIVE && event.getExpLevelCost() > player.getLevel()))
            return;

        boolean isBook = item.getType() == Material.BOOK;

        if (isBook)
            item.setType(Material.ENCHANTED_BOOK);

        Map<Enchantment, Integer> toAdd = event.getEnchantsToAdd();
        if (toAdd == null || toAdd.isEmpty()) {
            return;
        }

        for (Map.Entry<Enchantment, Integer> enchantment : toAdd.entrySet()) {
            try {
                if (isBook) {
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                    meta.addStoredEnchant(enchantment.getKey(), enchantment.getValue(), true); //TODO validate true
                    item.setItemMeta(meta);
                } else {
                    item.addUnsafeEnchantment(enchantment.getKey(), enchantment.getValue());
                }
            } catch (IllegalArgumentException e) {
                //ignore, since plugins are allowed to add enchantments that can't be applied
            }
        }

        player.enchanted(clicked);

        if (player.getGameMode() != GameMode.CREATIVE) {
            ItemStack res = inventory.getResource();
            res.setAmount(res.getAmount() - clicked + 1);
            if (res.getAmount() <= 0)
                inventory.setResource(null);
        }

        this.xpSeed = player.getXpSeed();

        update();
    }

    /////////////////////////////
    // Enchantments calculating

    private void calculateNewEnchantsAndLevels() {
        random.setSeed(xpSeed);

        int realBookshelfs = inventory.getBookshelfCount();
        int countBookshelf = Math.min(15, realBookshelfs);

        for (int i = 0; i < enchLevelCosts.length; i++) {
            enchLevelCosts[i] = calculateLevelCost(i, countBookshelf);
            enchId[i] = -1;
            enchLevel[i] = -1;
        }

        PrepareItemEnchantEvent event = new PrepareItemEnchantEvent(player, player.getOpenInventory(), inventory.getLocation().getBlock(), inventory.getItem(), enchLevelCosts, realBookshelfs);
        event.setCancelled(inventory.getItem().getEnchantments().size() > 0); //TODO only tools (expect books)
        EventFactory.callEvent(event);
        if (event.isCancelled()) {
            for (int i = 0; i < enchLevelCosts.length; i++)
                enchLevelCosts[i] = 0;
        } else {
            for (int i = 0; i < enchLevelCosts.length; i++) {
                if (enchLevelCosts[i] == 0) continue;
                List<LeveledEnchant> enchants = calculateCurrentEnchants(inventory.getItem(), i, enchLevelCosts[i]);
                if (enchants != null && !enchants.isEmpty()) {
                    LeveledEnchant chosen = WeightedRandom.getRandom(random, enchants);
                    this.enchId[i] = chosen.getEnchantment().getId();
                    this.enchLevel[i] = chosen.getLevel();
                }
            }
        }

        update();
    }

    private List<LeveledEnchant> calculateCurrentEnchants(ItemStack item, int level, int cost) {
        random.setSeed(xpSeed + level);
        int modifier = calculateRandomizedModifier(random, item, cost);
        if (modifier <= 0) return null;

        List<LeveledEnchant> possibleEnchants = getAllPossibleEnchants(item, modifier);
        if (possibleEnchants == null || possibleEnchants.isEmpty()) return null;

        LeveledEnchant chosen = WeightedRandom.getRandom(random, possibleEnchants);
        if (chosen == null) return null;

        List<LeveledEnchant> enchants = new ArrayList<>();
        enchants.add(chosen);

        while (random.nextInt(50) <= modifier) {
            removeConflicting(enchants, possibleEnchants);

            if (!possibleEnchants.isEmpty()) {
                enchants.add(WeightedRandom.getRandom(random, possibleEnchants));
            }
            modifier /= 2;
        }

        if (item.getType() == Material.BOOK && enchants.size() > 1) {
            enchants.remove(random.nextInt(enchants.size()));
        }

        return enchants;
    }

    private int calculateLevelCost(int stage, int countBookshelf) {
        int modifier = calculateModifier(inventory.getItem());
        if (modifier <= 0) return 0;

        int rand = random.nextInt(8) + random.nextInt(countBookshelf + 1);
        rand += 1;
        rand += countBookshelf / 2;

        int result;
        if (stage == 0) {
            result = Math.max(rand / 3, 1);
        } else if (stage == 1) {
            result = rand * 2 / 3 + 1;
        } else {
            result = Math.max(rand, countBookshelf * 2);
        }

        if (result < stage + 1)
            return 0;
        else
            return result;
    }

    //////////////////////////////////
    // Modifier calculation

    private static int calculateRandomizedModifier(Random random, ItemStack itemStack, int cost) {
        int modifier = calculateModifier(itemStack);
        if (modifier <= 0) return -1;

        float randomValue = 1 + (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;


        modifier /= 4;
        modifier += 1;
        modifier = random.nextInt(modifier) + random.nextInt(modifier);
        modifier += 1 + cost;

        modifier = Math.round(modifier * randomValue);
        modifier = Math.max(1, modifier);

        return modifier;
    }

    private static int calculateModifier(ItemStack item) {
        //TODO: replace this by a better system?
        Material type = item.getType();

        switch (type) {
            case BOOK:
            case BOW:
            case FISHING_ROD:
                return 1;
        }

        if (ClothType.CHAINMAIL.matches(type))
            return 12;
        else if (ClothType.IRON.matches(type))
            return 9;
        else if (ClothType.DIAMOND.matches(type))
            return 10;
        else if (ClothType.LEATHER.matches(type))
            return 15;
        else if (ClothType.GOLD.matches(type))
            return 25;


        else if (MaterialToolType.WOOD.matches(type))
            return 15;
        else if (MaterialToolType.STONE.matches(type))
            return 5;
        else if (MaterialToolType.DIAMOND.matches(type))
            return 10;
        else if (MaterialToolType.IRON.matches(type))
            return 14;
        else if (MaterialToolType.GOLD.matches(type))
            return 22;

        return 0;
    }

    /////////////////////////////////////
    // Internal stuff / helper functions

    private void clearEnch() {
        for (int i = 0; i < 3; i++) {
            enchLevelCosts[i] = 0;
            enchId[i] = -1;
            enchLevel[i] = -1;
        }

        update();
    }

    private void update() {
        player.setWindowProperty(InventoryView.Property.ENCHANT_BUTTON1, enchLevelCosts[0]);
        player.setWindowProperty(InventoryView.Property.ENCHANT_BUTTON2, enchLevelCosts[1]);
        player.setWindowProperty(InventoryView.Property.ENCHANT_BUTTON3, enchLevelCosts[2]);
        player.setWindowProperty(InventoryView.Property.ENCHANT_XP_SEED, xpSeed & -16);
        player.setWindowProperty(InventoryView.Property.ENCHANT_ID_AND_LEVEL1, enchId[0] | enchLevel[0] << 8);
        player.setWindowProperty(InventoryView.Property.ENCHANT_ID_AND_LEVEL2, enchId[1] | enchLevel[1] << 8);
        player.setWindowProperty(InventoryView.Property.ENCHANT_ID_AND_LEVEL3, enchId[2] | enchLevel[2] << 8);
    }

    private boolean isMaliciousClicked(int clicked) {
        //TODO: better handling of for malicious clients?

        if (clicked < 0 || clicked > enchLevelCosts.length) {
            GlowServer.logger.info("Malicious client, cannot enchant slot " + clicked);
            update();
            return true;
        }

        int level = enchLevelCosts[clicked];
        if (player.getGameMode() != GameMode.CREATIVE) {
            if (player.getLevel() < level || inventory.getResource() == null || inventory.getResource().getAmount() < clicked) {
                GlowServer.logger.info("Malicious client, player has not enough levels / enough resources to enchant item!");
                update();
                return true;
            }
        }

        return false;
    }

    private static Map<Enchantment, Integer> toMap(List<LeveledEnchant> list) {
        Map<Enchantment, Integer> map = new HashMap<>(list.size());
        for (LeveledEnchant enchant : list)
            map.put(enchant.getEnchantment(), enchant.getLevel());
        return map;
    }

    private static List<LeveledEnchant> getAllPossibleEnchants(ItemStack item, int modifier) {
        List<LeveledEnchant> enchantments = new ArrayList<>();

        boolean isBook = item.getType() == Material.BOOK;

        for (Enchantment enchantment : Enchantment.values()) {
            if (enchantment.canEnchantItem(item) || isBook) {
                for (int level = enchantment.getStartLevel(); level <= enchantment.getMaxLevel(); level++) {
                    if (((GlowEnchantment) enchantment).isInRange(level, modifier)) {
                        enchantments.add(new LeveledEnchant(enchantment, level));
                    }
                }
            }
        }

        return enchantments;
    }

    private static void removeConflicting(List<LeveledEnchant> enchants, List<LeveledEnchant> toReduce) {
        Iterator<LeveledEnchant> it = toReduce.iterator();

        while (it.hasNext()) {
            Enchantment currentEnchantment = it.next().getEnchantment();

            boolean conflicts = false;
            for (LeveledEnchant entry : enchants) {
                if (entry.getEnchantment().conflictsWith(currentEnchantment)) {
                    conflicts = true;
                    break;
                }
            }
            if (conflicts)
                it.remove();
        }
    }
}
