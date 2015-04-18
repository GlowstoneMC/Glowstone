package net.glowstone.shiny;

import com.google.common.base.Optional;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.GameProfile;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.attribute.*;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.tile.TileEntityType;
import org.spongepowered.api.data.DataManipulatorRegistry;
import org.spongepowered.api.data.types.*;
import org.spongepowered.api.effect.particle.ParticleEffectBuilder;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.EntityInteractionType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.data.types.SkeletonType;
import org.spongepowered.api.entity.player.gamemode.GameMode;
import org.spongepowered.api.item.*;
import org.spongepowered.api.item.inventory.ItemStackBuilder;
import org.spongepowered.api.item.merchant.TradeOfferBuilder;
import org.spongepowered.api.item.recipe.RecipeRegistry;
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
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.gen.PopulatorFactory;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

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
    public <T extends CatalogType> Optional<T> getType(Class<T> tClass, String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends CatalogType> Collection<? extends T> getAllOf(Class<T> tClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> Optional<T> getBuilderOf(Class<T> tClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
    public ParticleEffectBuilder getParticleEffectBuilder(ParticleType particle) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public Collection<Career> getCareers(Profession profession) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AchievementBuilder getAchievementBuilder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<Rotation> getRotationFromDegree(int degrees) {
        return Optional.fromNullable(rotations.get(degrees)); // TODO: int -> Rotation
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
    public GameDictionary getGameDictionary() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RecipeRegistry getRecipeRegistry() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DataManipulatorRegistry getManipulatorRegistry() {
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
    public Optional<ResourcePack> getById(String id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<DisplaySlot> getDisplaySlotForColor(TextColor color) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void registerWorldGeneratorModifier(WorldGeneratorModifier worldGeneratorModifier) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WorldBuilder getWorldBuilder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PopulatorFactory getPopulatorFactory() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<Translation> getTranslationById(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
