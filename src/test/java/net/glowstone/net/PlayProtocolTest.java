package net.glowstone.net;

import com.flowpowered.network.Message;
import java.util.Arrays;
import java.util.UUID;
import net.glowstone.net.message.KickMessage;
import net.glowstone.net.message.play.entity.EntityAnimationMessage;
import net.glowstone.net.message.play.entity.AttachEntityMessage;
import net.glowstone.net.message.play.entity.CollectItemMessage;
import net.glowstone.net.message.play.entity.DestroyEntitiesMessage;
import net.glowstone.net.message.play.entity.EntityEffectMessage;
import net.glowstone.net.message.play.entity.EntityEquipmentMessage;
import net.glowstone.net.message.play.entity.EntityHeadRotationMessage;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.EntityRemoveEffectMessage;
import net.glowstone.net.message.play.entity.EntityRotationMessage;
import net.glowstone.net.message.play.entity.EntityStatusMessage;
import net.glowstone.net.message.play.entity.EntityTeleportMessage;
import net.glowstone.net.message.play.entity.EntityVelocityMessage;
import net.glowstone.net.message.play.entity.RelativeEntityPositionMessage;
import net.glowstone.net.message.play.entity.RelativeEntityPositionRotationMessage;
import net.glowstone.net.message.play.entity.SpawnGlobalEntityMessage;
import net.glowstone.net.message.play.entity.SpawnMobMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.net.message.play.entity.SpawnPaintingMessage;
import net.glowstone.net.message.play.entity.SpawnPlayerMessage;
import net.glowstone.net.message.play.entity.SpawnXpOrbMessage;
import net.glowstone.net.message.play.entity.VehicleMoveMessage;
import net.glowstone.net.message.play.game.BlockActionMessage;
import net.glowstone.net.message.play.game.BlockChangeMessage;
import net.glowstone.net.message.play.game.ChatMessage;
import net.glowstone.net.message.play.game.ClientSettingsMessage;
import net.glowstone.net.message.play.game.CraftRecipeRequestMessage;
import net.glowstone.net.message.play.game.CraftRecipeResponseMessage;
import net.glowstone.net.message.play.game.CraftingBookDataMessage;
import net.glowstone.net.message.play.game.ExperienceMessage;
import net.glowstone.net.message.play.game.HealthMessage;
import net.glowstone.net.message.play.game.IncomingChatMessage;
import net.glowstone.net.message.play.game.PingMessage;
import net.glowstone.net.message.play.game.PluginMessage;
import net.glowstone.net.message.play.game.PositionRotationMessage;
import net.glowstone.net.message.play.game.RespawnMessage;
import net.glowstone.net.message.play.game.SpawnPositionMessage;
import net.glowstone.net.message.play.game.StateChangeMessage;
import net.glowstone.net.message.play.game.TimeMessage;
import net.glowstone.net.message.play.game.TitleMessage;
import net.glowstone.net.message.play.game.UnlockRecipesMessage;
import net.glowstone.net.message.play.game.UpdateSignMessage;
import net.glowstone.net.message.play.game.UserListHeaderFooterMessage;
import net.glowstone.net.message.play.inv.CloseWindowMessage;
import net.glowstone.net.message.play.inv.CreativeItemMessage;
import net.glowstone.net.message.play.inv.EnchantItemMessage;
import net.glowstone.net.message.play.inv.TransactionMessage;
import net.glowstone.net.message.play.inv.WindowClickMessage;
import net.glowstone.net.message.play.player.AdvancementTabMessage;
import net.glowstone.net.message.play.player.BlockPlacementMessage;
import net.glowstone.net.message.play.player.CameraMessage;
import net.glowstone.net.message.play.player.ClientStatusMessage;
import net.glowstone.net.message.play.player.DiggingMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.net.message.play.player.PlayerAbilitiesMessage;
import net.glowstone.net.message.play.player.PlayerActionMessage;
import net.glowstone.net.message.play.player.PlayerLookMessage;
import net.glowstone.net.message.play.player.PlayerPositionLookMessage;
import net.glowstone.net.message.play.player.PlayerPositionMessage;
import net.glowstone.net.message.play.player.PlayerSwingArmMessage;
import net.glowstone.net.message.play.player.PlayerUpdateMessage;
import net.glowstone.net.message.play.player.ResourcePackSendMessage;
import net.glowstone.net.message.play.player.ResourcePackStatusMessage;
import net.glowstone.net.message.play.player.ServerDifficultyMessage;
import net.glowstone.net.message.play.player.SpectateMessage;
import net.glowstone.net.message.play.player.SteerBoatMessage;
import net.glowstone.net.message.play.player.SteerVehicleMessage;
import net.glowstone.net.message.play.player.TabCompleteMessage;
import net.glowstone.net.message.play.player.TeleportConfirmMessage;
import net.glowstone.net.message.play.player.UseBedMessage;
import net.glowstone.net.message.play.player.UseItemMessage;
import net.glowstone.net.protocol.PlayProtocol;
import net.glowstone.util.TextMessage;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Test cases for {@link PlayProtocol}.
 */
public class PlayProtocolTest extends BaseProtocolTest {

    private static final Message[] TEST_MESSAGES = new Message[]{
        new PingMessage(1L),
        new IncomingChatMessage("test"),
        new InteractEntityMessage(1, 1),
        new InteractEntityMessage(1, 2, 1f, 2f, 3f, 0),
        new PlayerUpdateMessage(true),
        new PlayerPositionMessage(true, 1.0, 2.0, 3.0),
        new PlayerLookMessage(3f, 2f, true),
        new PlayerPositionLookMessage(true, 1.0, 2.0, 3.0, 1f, 2f),
        new DiggingMessage(0, 1, 2, 3, 4),
        new BlockPlacementMessage(1, 2, 3, 4, 5, 6, 7, 8),
        new PlayerSwingArmMessage(1),
        //new HeldItemMessage(1), // asymmetric
        new PlayerActionMessage(1, 2, 3),
        new SteerVehicleMessage(1f, 2f, true, false),
        new CloseWindowMessage(1),
        new WindowClickMessage(1, 2, 3, 4, 5, new ItemStack(Material.APPLE, 1)),
        new TransactionMessage(1, 2, true),
        new CreativeItemMessage(1, new ItemStack(Material.APPLE, 1)),
        new EnchantItemMessage(1, 2),
        new UpdateSignMessage(1, 2, 3,
            new TextMessage[]{new TextMessage("hello"), new TextMessage("hi"),
                new TextMessage("third"), new TextMessage("fourth")}),
        UpdateSignMessage.fromPlainText(1, 2, 3, "hello", "hi", "third", "fourth"),
        new PlayerAbilitiesMessage(1, 2f, 3f),
        new TabCompleteMessage(1, "text"),
        new ClientSettingsMessage("en-en", 16, 1, true, 2, 0),
        new ClientStatusMessage(1),
        new PluginMessage("glowstone", new byte[]{0x00, 0x11}),
        new SpectateMessage(UUID.randomUUID()),
        new ResourcePackStatusMessage(1),
        //new JoinGameMessage(1, 2, 3, 4, 5, "normal", true), //asymmetric
        new ChatMessage(ProtocolTestUtils.getTextMessage(), 2),
        new ChatMessage(ProtocolTestUtils.getTextMessage(), 1),
        new ChatMessage(ProtocolTestUtils.getJson()),
        new ChatMessage("glowstone"),
        new TimeMessage(1, 2),
        new EntityEquipmentMessage(1, 2, new ItemStack(Material.APPLE, 1)),
        new SpawnPositionMessage(1, 2, 3),
        new HealthMessage(1f, 2, 3f),
        new RespawnMessage(1, 2, 3, "world"),
        new PositionRotationMessage(1.0, 2.0, 3.0, 1f, 2f),
        new PositionRotationMessage(1.0, 2.0, 3.0, 4f, 5f, 6, 1),
        new PositionRotationMessage(new Location(null, 1.0, 2.0, 3.0, 4f, 5f)),
        new EntityAnimationMessage(1, 2),
        new SpawnPlayerMessage(1, UUID.randomUUID(), 2, 3, 4, 5, 6,
            ProtocolTestUtils.getMetadataEntry()),
        new CollectItemMessage(1, 2, 3),
        new SpawnObjectMessage(1, UUID.randomUUID(), 2, 3, 4, 5, 6, 7),
        new SpawnObjectMessage(1, UUID.randomUUID(), 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
        new SpawnMobMessage(1, UUID.randomUUID(), 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
            ProtocolTestUtils.getMetadataEntry()),
        new SpawnPaintingMessage(1, UUID.randomUUID(), 2, 3, 4, 5, 6),
        new SpawnXpOrbMessage(1, 2, 3, 4, (short) 5),
        new EntityVelocityMessage(1, new Vector(1, 2, 3)),
        new EntityVelocityMessage(1, 2, 3, 4),
        new DestroyEntitiesMessage(Arrays.asList(1, 2, 3)),
        new RelativeEntityPositionMessage(1, (short) 2, (short) 3, (short) 4),
        new RelativeEntityPositionMessage(1, (short) 2, (short) 3, (short) 4, true),
        new KickMessage(ProtocolTestUtils.getTextMessage()),
        new AttachEntityMessage(1, 2),
        new EntityEffectMessage(1, (byte) 2, (byte) 3, 4, false),
        new EntityHeadRotationMessage(1, 2),
        new EntityMetadataMessage(1, ProtocolTestUtils.getMetadataEntry()),
        new EntityRemoveEffectMessage(1, (byte) 2),
        new EntityRotationMessage(1, 2, 3),
        new EntityRotationMessage(1, 2, 3, false),
        new EntityStatusMessage(1, 2),
        new EntityStatusMessage(1, EntityStatusMessage.ANIMAL_HEARTS),
        new EntityTeleportMessage(1, 2, 3, 4, 5, 6),
        new EntityTeleportMessage(1, 2, 3, 4, 5, 6, false),
        new RelativeEntityPositionRotationMessage(1, (short) 2, (short) 3, (short) 4, 5, 6),
        new RelativeEntityPositionRotationMessage(1, (short) 2, (short) 3, (short) 4, 5, 6, false),
        new SpawnGlobalEntityMessage(1, 2, 3, 4),
        new SpawnGlobalEntityMessage(1, 2, 3, 4, 5),
        new BlockActionMessage(1, 2, 3, 4, 5, 6),
        new BlockChangeMessage(1, 2, 3, 4),
        new BlockChangeMessage(1, 2, 3, 4, 5),
        //new ChunkBulkMessage(...),
        //new ChunkDataMessage(...),
        new ExperienceMessage(1f, 2, 3),
        //new MapDataMessage(...),
        //new MultiBlockChangeMessage(...),
        //PlayEffect
        //PlayParticle
        //PlaySound
        //SignEditor
        new StateChangeMessage(1, 2f),
        new StateChangeMessage(StateChangeMessage.Reason.GAMEMODE, 2f),
        //Statistic
        new TitleMessage(TitleMessage.Action.TITLE, new TextMessage("Title")),
        new TitleMessage(TitleMessage.Action.TIMES, 1, 3, 4),
        new TitleMessage(TitleMessage.Action.RESET),
        new UserListHeaderFooterMessage(new TextMessage("head"), new TextMessage("foot")),
        //UserListItem
        //WorldBorder
        //OpenWindow
        //SetWindowContents
        //SetWindowSlot
        //WindowProperty
        new CameraMessage(1),
        //CombatEvent
        new ResourcePackSendMessage("url", "hash"),
        new ServerDifficultyMessage(Difficulty.NORMAL),
        new UseBedMessage(1, 2, 3, 4),
        //TabCompleteResponse
        new UseItemMessage(0),
        new VehicleMoveMessage(1.0, 2.0, 3.0, 4f, 5f),
        new TeleportConfirmMessage(1),
        new AdvancementTabMessage(0, "minecraft:test"),
        new SteerBoatMessage(true, true),
        new SteerBoatMessage(false, false),
        new CraftRecipeRequestMessage(0, 1, true),
        new CraftRecipeResponseMessage(0, 1),
        new CraftingBookDataMessage(CraftingBookDataMessage.TYPE_DISPLAYED_RECIPE, 0),
        new CraftingBookDataMessage(CraftingBookDataMessage.TYPE_STATUS, true, false, true, false),
        new UnlockRecipesMessage(UnlockRecipesMessage.ACTION_ADD, true, false, true, false,
                new int[]{1, 2, 3}),
        new UnlockRecipesMessage(UnlockRecipesMessage.ACTION_INIT, true, false, true, false,
                new int[]{1, 2}, new int[]{1, 2, 3})
    };

    public PlayProtocolTest() {
        super(new PlayProtocol(), TEST_MESSAGES);
    }
}
