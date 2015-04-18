package net.glowstone.shiny;

import com.google.common.base.Optional;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.GameProfile;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.attribute.*;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataManipulatorRegistry;
import org.spongepowered.api.data.types.*;
import org.spongepowered.api.effect.particle.ParticleEffectBuilder;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.*;
import org.spongepowered.api.item.inventory.ItemStackBuilder;
import org.spongepowered.api.item.merchant.TradeOfferBuilder;
import org.spongepowered.api.item.recipe.RecipeRegistry;
import org.spongepowered.api.potion.PotionEffectBuilder;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.scoreboard.ScoreboardBuilder;
import org.spongepowered.api.scoreboard.TeamBuilder;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot;
import org.spongepowered.api.scoreboard.objective.ObjectiveBuilder;
import org.spongepowered.api.stats.*;
import org.spongepowered.api.stats.achievement.AchievementBuilder;
import org.spongepowered.api.status.Favicon;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.rotation.Rotation;
import org.spongepowered.api.world.WorldBuilder;
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
    private final Map<String, Art> arts = new HashMap<>();
    private final Map<String, Rotation> rotations = new HashMap<>();
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
