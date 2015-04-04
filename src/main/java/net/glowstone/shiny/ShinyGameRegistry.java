package net.glowstone.shiny;

import com.google.common.base.Optional;
import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.GameProfile;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.attribute.*;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.tile.TileEntityType;
import org.spongepowered.api.block.tile.data.BannerPatternShape;
import org.spongepowered.api.block.tile.data.NotePitch;
import org.spongepowered.api.block.tile.data.SkullType;
import org.spongepowered.api.effect.particle.ParticleEffectBuilder;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.EntityInteractionType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.hanging.art.Art;
import org.spongepowered.api.entity.living.animal.*;
import org.spongepowered.api.entity.living.monster.SkeletonType;
import org.spongepowered.api.entity.living.villager.Career;
import org.spongepowered.api.entity.living.villager.Profession;
import org.spongepowered.api.entity.player.gamemode.GameMode;
import org.spongepowered.api.item.*;
import org.spongepowered.api.item.inventory.ItemStackBuilder;
import org.spongepowered.api.item.merchant.TradeOfferBuilder;
import org.spongepowered.api.item.recipe.RecipeRegistry;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.potion.PotionEffectBuilder;
import org.spongepowered.api.potion.PotionEffectType;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.scoreboard.ScoreboardBuilder;
import org.spongepowered.api.scoreboard.TeamBuilder;
import org.spongepowered.api.scoreboard.Visibility;
import org.spongepowered.api.scoreboard.critieria.Criterion;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot;
import org.spongepowered.api.scoreboard.objective.ObjectiveBuilder;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode;
import org.spongepowered.api.stats.*;
import org.spongepowered.api.stats.achievement.Achievement;
import org.spongepowered.api.stats.achievement.AchievementBuilder;
import org.spongepowered.api.status.Favicon;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.selector.ArgumentType;
import org.spongepowered.api.text.selector.SelectorType;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.rotation.Rotation;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.WorldBuilder;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Implementation of {@link GameRegistry}.
 */
public class ShinyGameRegistry implements GameRegistry {

    private final Map<String, BlockType> blocks = new HashMap<>();
    private final Map<String, ItemType> items = new HashMap<>();
    private final Map<String, TileEntityType> tileEntities = new HashMap<>();
    private final Map<String, BiomeType> biomes = new HashMap<>();
    private final Map<String, SoundType> sounds = new HashMap<>();
    private final Map<String, EntityType> entities = new HashMap<>();
    private final Map<String, Art> arts = new HashMap<>();
    private final Map<String, DyeColor> dyeColors = new HashMap<>();
    private final Map<String, HorseColor> horseColors = new HashMap<>();
    private final Map<String, HorseStyle> horseStyles = new HashMap<>();
    private final Map<String, HorseVariant> horseVariants = new HashMap<>();
    private final Map<String, OcelotType> ocelotTypes = new HashMap<>();
    private final Map<String, RabbitType> rabbitTypes = new HashMap<>();
    private final Map<String, SkeletonType> skeletonTypes = new HashMap<>();
    private final Map<String, Career> careers = new HashMap<>();
    private final Map<String, Profession> professions = new HashMap<>();
    private final Map<String, GameMode> gameModes = new HashMap<>();
    private final Map<String, PotionEffectType> potionEffectTypes = new HashMap<>();
    private final Map<String, Enchantment> enchantments = new HashMap<>();
    private final Map<String, String> strings = new HashMap<>();
    private final Map<String, Statistic> statistics = new HashMap<>();
    private final Map<String, StatisticFormat> statisticFormats = new HashMap<>();
    private final Map<String, Achievement> achievements = new HashMap<>();
    private final Map<String, DimensionType> dimensionTypes = new HashMap<>();
    private final Map<String, Rotation> rotations = new HashMap<>();
    private final Map<String, NotePitch> notePitchs = new HashMap<>();
    private final Map<String, SkullType> skullTypes = new HashMap<>();
    private final Map<String, BannerPatternShape> bannerPatternShapes = new HashMap<>();
    private final Map<String, Difficulty> difficulties = new HashMap<>();
    private final Map<String, EntityInteractionType> entityInteractionTypes = new HashMap<>();
    private final Map<String, Attribute> attributes = new HashMap<>();
    private final Map<String, Operation> operations = new HashMap<>();
    private final Map<String, CoalType> coalTypes = new HashMap<>();
    private final Map<String, Fish> fishs = new HashMap<>();
    private final Map<String, CookedFish> cookedFishs = new HashMap<>();
    private final Map<String, GoldenApple> goldenApples = new HashMap<>();
    private final Map<String, TextColor> textColors = new HashMap<>();
    private final Map<String, TextStyle> textStyles = new HashMap<>();
    private final Map<String, ChatType> chatTypes = new HashMap<>();
    private final Map<String, SelectorType> selectorTypes = new HashMap<>();
    private final Map<String, ArgumentType<?>> argumentTypes = new HashMap<>();
    private final Map<String, Locale> locales = new HashMap<>();
    private final Map<String, DisplaySlot> displaySlots = new HashMap<>();
    private final Map<String, Visibility> visibilities = new HashMap<>();
    private final Map<String, Criterion> criteria = new HashMap<>();
    private final Map<String, ObjectiveDisplayMode> objectiveDisplayModes = new HashMap<>();
    private final Map<String, GeneratorType> generatorTypes = new HashMap<>();
    private final Map<String, WorldGeneratorModifier> worldGeneratorModifiers = new HashMap<>();
    private final Map<Object, String> idMap = new IdentityHashMap<>();

    private void register(BlockType block) {
        blocks.put(block.getId(), block);
        idMap.put(block, block.getId());
    }

    private void register(ItemType item) {
        items.put(item.getId(), item);
        idMap.put(item, item.getId());
    }

    @Override
    public Optional<BlockType> getBlock(String id) {
        return Optional.fromNullable(blocks.get(id));
    }

    @Override
    public Collection<BlockType> getBlocks() {
        return blocks.values();
    }

    @Override
    public Optional<ItemType> getItem(String id) {
        return Optional.fromNullable(items.get(id));
    }

    @Override
    public Collection<ItemType> getItems() {
        return items.values();
    }

    @Override
    public Optional<TileEntityType> getTileEntityType(String id) {
        return Optional.fromNullable(tileEntities.get(id));
    }

    @Override
    public Collection<TileEntityType> getTileEntityTypes() {
        return tileEntities.values();
    }

    @Override
    public Optional<BiomeType> getBiome(String id) {
        return Optional.fromNullable(biomes.get(id));
    }

    @Override
    public Collection<BiomeType> getBiomes() {
        return biomes.values();
    }

    @Override
    public ItemStackBuilder getItemBuilder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TradeOfferBuilder getTradeOfferBuilder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PotionEffectBuilder getPotionEffectBuilder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ObjectiveBuilder getObjectiveBuilder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TeamBuilder getTeamBuilder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ScoreboardBuilder getScoreboardBuilder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<ParticleType> getParticleType(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<ParticleType> getParticleTypes() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ParticleEffectBuilder getParticleEffectBuilder(ParticleType particle) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<SoundType> getSound(String name) {
        return Optional.fromNullable(sounds.get(name));
    }

    @Override
    public Collection<SoundType> getSounds() {
        return sounds.values();
    }

    @Override
    public Optional<EntityType> getEntity(String id) {
        return Optional.fromNullable(entities.get(id));
    }

    @Override
    public Collection<EntityType> getEntities() {
        return entities.values();
    }



    @Override
    public Optional<Art> getArt(String id) {
        return Optional.fromNullable(arts.get(id));
    }

    @Override
    public Collection<Art> getArts() {
        return arts.values();
    }

    @Override
    public Optional<DyeColor> getDye(String id) {
        return Optional.fromNullable(dyeColors.get(id));
    }

    @Override
    public Collection<DyeColor> getDyes() {
        return dyeColors.values();
    }

    @Override
    public Optional<HorseColor> getHorseColor(String id) {
        return Optional.fromNullable(horseColors.get(id));
    }

    @Override
    public Collection<HorseColor> getHorseColors() {
        return horseColors.values();
    }

    @Override
    public Optional<HorseStyle> getHorseStyle(String id) {
        return Optional.fromNullable(horseStyles.get(id));
    }

    @Override
    public Collection<HorseStyle> getHorseStyles() {
        return horseStyles.values();
    }

    @Override
    public Optional<HorseVariant> getHorseVariant(String id) {
        return Optional.fromNullable(horseVariants.get(id));
    }

    @Override
    public Collection<HorseVariant> getHorseVariants() {
        return horseVariants.values();
    }

    @Override
    public Optional<OcelotType> getOcelotType(String id) {
        return Optional.fromNullable(ocelotTypes.get(id));
    }

    @Override
    public Collection<OcelotType> getOcelotTypes() {
        return ocelotTypes.values();
    }

    @Override
    public Optional<RabbitType> getRabbitType(String id) {
        return Optional.fromNullable(rabbitTypes.get(id));
    }

    @Override
    public Collection<RabbitType> getRabbitTypes() {
        return rabbitTypes.values();
    }

    @Override
    public Optional<SkeletonType> getSkeletonType(String id) {
        return Optional.fromNullable(skeletonTypes.get(id));
    }

    @Override
    public Collection<SkeletonType> getSkeletonTypes() {
        return skeletonTypes.values();
    }

    @Override
    public Optional<Career> getCareer(String id) {
        return Optional.fromNullable(careers.get(id));
    }

    @Override
    public Collection<Career> getCareers() {
        return careers.values();
    }

    @Override
    public Collection<Career> getCareers(Profession profession) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<Profession> getProfession(String id) {
        return Optional.fromNullable(professions.get(id));
    }

    @Override
    public Collection<Profession> getProfessions() {
        return professions.values();
    }

    @Override
    public Collection<GameMode> getGameModes() {
        return gameModes.values();
    }

    @Override
    public Collection<PotionEffectType> getPotionEffects() {
        return potionEffectTypes.values();
    }

    @Override
    public Optional<Enchantment> getEnchantment(String id) {
        return Optional.fromNullable(enchantments.get(id));
    }

    @Override
    public Collection<Enchantment> getEnchantments() {
        return enchantments.values();
    }

    @Override
    public Optional<Statistic> getStatistic(String id) {
        return Optional.fromNullable(statistics.get(id));
    }

    @Override
    public Collection<Statistic> getStatistics() {
        return statistics.values();
    }

    @Override
    public Optional<StatisticFormat> getStatisticFormat(String id) {
        return Optional.fromNullable(statisticFormats.get(id));
    }

    @Override
    public Collection<StatisticFormat> getStatisticFormats() {
        return statisticFormats.values();
    }

    @Override
    public Optional<Achievement> getAchievement(String id) {
        return Optional.fromNullable(achievements.get(id));
    }

    @Override
    public Collection<Achievement> getAchievements() {
        return achievements.values();
    }

    @Override
    public AchievementBuilder getAchievementBuilder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<DimensionType> getDimensionType(String id) {
        return Optional.fromNullable(dimensionTypes.get(id));
    }

    @Override
    public Collection<DimensionType> getDimensionTypes() {
        return dimensionTypes.values();
    }

    @Override
    public Optional<Rotation> getRotationFromDegree(int degrees) {
        return Optional.fromNullable(rotations.get(degrees)); // TODO: int -> Rotation
    }

    @Override
    public Collection<Rotation> getRotations() {
        return rotations.values();
    }

    @Override
    public Optional<NotePitch> getNotePitch(String id) {
        return Optional.fromNullable(notePitchs.get(id));
    }

    @Override
    public Collection<NotePitch> getNotePitches() {
        return notePitchs.values();
    }

    @Override
    public Optional<SkullType> getSkullType(String id) {
        return Optional.fromNullable(skullTypes.get(id));
    }

    @Override
    public Collection<SkullType> getSkullTypes() {
        return skullTypes.values();
    }

    @Override
    public Optional<BannerPatternShape> getBannerPatternShape(String id) {
        return Optional.fromNullable(bannerPatternShapes.get(id));
    }

    @Override
    public Collection<BannerPatternShape> getBannerPatternShapes() {
        return bannerPatternShapes.values();
    }

    @Override
    public Optional<Difficulty> getDifficulty(String id) {
        return Optional.fromNullable(difficulties.get(id));
    }

    @Override
    public Collection<Difficulty> getDifficulties() {
        return difficulties.values();
    }

    @Override
    public Optional<EntityInteractionType> getEntityInteractionType(String id) {
        return Optional.fromNullable(entityInteractionTypes.get(id));
    }

    @Override
    public Collection<EntityInteractionType> getEntityInteractionTypes() {
        return entityInteractionTypes.values();
    }

    @Override
    public Optional<Attribute> getAttribute(String id) {
        return Optional.fromNullable(attributes.get(id));
    }

    @Override
    public Collection<Attribute> getAttributes() {
        return attributes.values();
    }

    @Override
    public Optional<Operation> getOperation(String id) {
        return Optional.fromNullable(operations.get(id));
    }

    @Override
    public Collection<Operation> getOperations() {
        return operations.values();
    }

    @Override
    public Optional<CoalType> getCoalType(String id) {
        return Optional.fromNullable(coalTypes.get(id));
    }

    @Override
    public Collection<CoalType> getCoalTypes() {
        return coalTypes.values();
    }

    @Override
    public Optional<Fish> getFishType(String id) {
        return Optional.fromNullable(fishs.get(id));
    }

    @Override
    public Collection<Fish> getFishTypes() {
        return fishs.values();
    }

    @Override
    public Optional<CookedFish> getCookedFishType(String id) {
        return Optional.fromNullable(cookedFishs.get(id));
    }

    @Override
    public Collection<CookedFish> getCookedFishTypes() {
        return cookedFishs.values();
    }

    @Override
    public Optional<GoldenApple> getGoldenAppleType(String id) {
        return Optional.fromNullable(goldenApples.get(id));
    }

    @Override
    public Collection<GoldenApple> getGoldenAppleTypes() {
        return goldenApples.values();
    }

    @Override
    public Optional<TextColor> getTextColor(String id) {
        return Optional.fromNullable(textColors.get(id));
    }

    @Override
    public Collection<TextColor> getTextColors() {
        return textColors.values();
    }

    @Override
    public Optional<TextStyle> getTextStyle(String id) {
        return Optional.fromNullable(textStyles.get(id));
    }

    @Override
    public Collection<TextStyle> getTextStyles() {
        return textStyles.values();
    }

    @Override
    public Optional<ChatType> getChatType(String id) {
        return Optional.fromNullable(chatTypes.get(id));
    }

    @Override
    public Collection<ChatType> getChatTypes() {
        return chatTypes.values();
    }

    @Override
    public Optional<SelectorType> getSelectorType(String id) {
        return Optional.fromNullable(selectorTypes.get(id));
    }

    @Override
    public Collection<SelectorType> getSelectorTypes() {
        return selectorTypes.values();
    }

    @Override
    public Optional<Locale> getLocale(String id) {
        return Optional.fromNullable(locales.get(id));
    }

    @Override
    public Collection<Locale> getLocales() {
        return locales.values();
    }

    @Override
    public Optional<DisplaySlot> getDisplaySlot(String id) {
        return Optional.fromNullable(displaySlots.get(id));
    }

    @Override
    public Collection<DisplaySlot> getDisplaySlots() {
        return displaySlots.values();
    }

    @Override
    public Optional<Visibility> getVisibility(String id) {
        return Optional.fromNullable(visibilities.get(id));
    }

    @Override
    public Collection<Visibility> getVisibilities() {
        return visibilities.values();
    }

    @Override
    public Optional<Criterion> getCriterion(String id) {
        return Optional.fromNullable(criteria.get(id));
    }

    @Override
    public Collection<Criterion> getCriteria() {
        return criteria.values();
    }

    @Override
    public Optional<ObjectiveDisplayMode> getObjectiveDisplayMode(String id) {
        return Optional.fromNullable(objectiveDisplayModes.get(id));
    }

    @Override
    public Collection<ObjectiveDisplayMode> getObjectiveDisplayModes() {
        return objectiveDisplayModes.values();
    }

    @Override
    public Optional<GeneratorType> getGeneratorType(String id) {
        return Optional.fromNullable(generatorTypes.get(id));
    }

    @Override
    public Collection<GeneratorType> getGeneratorTypes() {
        return generatorTypes.values();
    }

    @Override
    public Optional<WorldGeneratorModifier> getWorldGeneratorModifier(String id) {
        return Optional.fromNullable(worldGeneratorModifiers.get(id));
    }

    @Override
    public Collection<WorldGeneratorModifier> getWorldGeneratorModifiers() {
        return worldGeneratorModifiers.values();
    }

    @Override
    public Collection<String> getDefaultGameRules() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<EntityStatistic> getEntityStatistic(StatisticGroup statisticGroup, EntityType entityType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<ItemStatistic> getItemStatistic(StatisticGroup statisticGroup, ItemType itemType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<BlockStatistic> getBlockStatistic(StatisticGroup statisticGroup, BlockType blockType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<TeamStatistic> getTeamStatistic(StatisticGroup statisticGroup, TextColor teamColor) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<Statistic> getStatistics(StatisticGroup statisticGroup) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public StatisticBuilder getStatisticBuilder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public StatisticBuilder.EntityStatisticBuilder getEntityStatisticBuilder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public StatisticBuilder.BlockStatisticBuilder getBlockStatisticBuilder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public StatisticBuilder.ItemStatisticBuilder getItemStatisticBuilder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public StatisticBuilder.TeamStatisticBuilder getTeamStatisticBuilder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void registerStatistic(Statistic stat) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public GameProfile createGameProfile(UUID uuid, String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Favicon loadFavicon(String raw) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Favicon loadFavicon(File file) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Favicon loadFavicon(URL url) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Favicon loadFavicon(InputStream in) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Favicon loadFavicon(BufferedImage image) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<BannerPatternShape> getBannerPatternShapeById(String id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public GameDictionary getGameDictionary() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RecipeRegistry getRecipeRegistry() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AttributeModifierBuilder getAttributeModifierBuilder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AttributeCalculator getAttributeCalculator() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AttributeBuilder getAttributeBuilder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FireworkEffectBuilder getFireworkEffectBuilder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<ArgumentType<?>> getArgumentType(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<ArgumentType<?>> getArgumentTypes() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<Locale> getLocaleById(String id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<Translation> getTranslationById(String id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<ResourcePack> getById(String id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<DisplaySlot> getDisplaySlotForColor(TextColor color) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WorldBuilder getWorldBuilder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WorldBuilder getWorldBuilder(WorldCreationSettings settings) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WorldBuilder getWorldBuilder(WorldProperties properties) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void registerWorldGeneratorModifier(PluginContainer plugin, String genId, WorldGeneratorModifier modifier) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
