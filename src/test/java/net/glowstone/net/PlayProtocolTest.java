package net.glowstone.net;

import com.flowpowered.network.Message;
import net.glowstone.net.handler.play.player.UseItemPacket;
import net.glowstone.net.message.KickPacket;
import net.glowstone.net.message.play.entity.*;
import net.glowstone.net.message.play.game.*;
import net.glowstone.net.message.play.inv.*;
import net.glowstone.net.message.play.player.*;
import net.glowstone.net.protocol.PlayProtocol;
import net.glowstone.util.TextMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.UUID;

/**
 * Test cases for {@link PlayProtocol}.
 */
public class PlayProtocolTest extends BaseProtocolTest {

    private static final Message[] TEST_MESSAGES = new Message[]{
            new PingPacket(1),
            new InboundChatPacket("test"),
            new InteractEntityPacket(1, 1),
            new InteractEntityPacket(1, 2, 1f, 2f, 3f, 0),
            new PlayerUpdatePacket(true),
            new PlayerPositionPacket(true, 1.0, 2.0, 3.0),
            new PlayerLookPacket(3f, 2f, true),
            new PlayerPositionLookPacket(true, 1.0, 2.0, 3.0, 1f, 2f),
            new BlockDigPacket(0, 1, 2, 3, 4),
            new BlockPlacePacket(1, 2, 3, 4, 5, 6, 7, 8),
            new PlayerSwingArmPacket(1),
            //new HeldItemMessage(1), // asymmetric
            new PlayerActionPacket(1, 2, 3),
            new SteerVehiclePacket(1f, 2f, true, false),
            new WindowClosePacket(1),
            new WindowClickPacket(1, 2, 3, 4, 5, new ItemStack(Material.APPLE, 1)),
            new TransactionPacket(1, 2, true),
            new CreativeItemPacket(1, new ItemStack(Material.APPLE, 1)),
            new EnchantItemPacket(1, 2),
            new UpdateSignPacket(1, 2, 3, new TextMessage[]{new TextMessage("hello"), new TextMessage("hi"), new TextMessage("third"), new TextMessage("fourth")}),
            UpdateSignPacket.fromPlainText(1, 2, 3, "hello", "hi", "third", "fourth"),
            new PlayerAbilitiesPacket(1, 2f, 3f),
            new TabCompletePacket("text", false, null),
            new TabCompletePacket("text", false, new BlockVector(1, 2, 3)),
            new ClientSettingsPacket("en-en", 16, 1, true, 2, 0),
            new ClientStatusPacket(1),
            new PluginLoadPacket("glowstone", new byte[]{0x00, 0x11}),
            new SpectatePacket(UUID.randomUUID()),
            new ResourcePackStatusPacket(1),
            //new JoinGameMessage(1, 2, 3, 4, 5, "normal", true), //asymmetric
            new OutboundChatPacket(ProtocolTestUtils.getTextMessage(), 2),
            new OutboundChatPacket(ProtocolTestUtils.getTextMessage(), 1),
            new OutboundChatPacket(ProtocolTestUtils.getJson()),
            new OutboundChatPacket("glowstone"),
            new TimePacket(1, 2),
            new EntityEquipmentPacket(1, 2, new ItemStack(Material.APPLE, 1)),
            new SpawnPositionPacket(1, 2, 3),
            new HealthPacket(1f, 2, 3f),
            new RespawnPacket(1, 2, 3, "world"),
            new PositionRotationPacket(1.0, 2.0, 3.0, 1f, 2f),
            new PositionRotationPacket(1.0, 2.0, 3.0, 4f, 5f, 6, 1),
            new PositionRotationPacket(new Location(null, 1.0, 2.0, 3.0, 4f, 5f)),
            new EntityAnimationPacket(1, 2),
            new SpawnPlayerPacket(1, UUID.randomUUID(), 2, 3, 4, 5, 6, ProtocolTestUtils.getMetadataEntry()),
            new CollectItemPacket(1, 2),
            new SpawnObjectPacket(1, UUID.randomUUID(),2, 3, 4, 5, 6, 7),
            new SpawnObjectPacket(1, UUID.randomUUID(), 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            new SpawnMobPacket(1, UUID.randomUUID(), 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, ProtocolTestUtils.getMetadataEntry()),
            new SpawnPaintingPacket(1, "painting", 2, 3, 4, 5),
            new SpawnXpOrbPacket(1, 2, 3, 4, (short) 5),
            new EntityVelocityPacket(1, new Vector(1, 2, 3)),
            new EntityVelocityPacket(1, 2, 3, 4),
            new DestroyEntitiesPacket(Arrays.asList(1, 2, 3)),
            new RelativeEntityPositionPacket(1, (short) 2, (short) 3, (short) 4),
            new RelativeEntityPositionPacket(1, (short) 2, (short) 3, (short) 4, true),
            new KickPacket(ProtocolTestUtils.getTextMessage()),
            new AttachEntityPacket(1, 2, true),
            new EntityEffectPacket(1, (byte) 2, (byte) 3, 4, false),
            new EntityHeadRotationPacket(1, 2),
            new EntityMetadataPacket(1, ProtocolTestUtils.getMetadataEntry()),
            new EntityRemoveEffectPacket(1, (byte) 2),
            new EntityRotationPacket(1, 2, 3),
            new EntityRotationPacket(1, 2, 3, false),
            new EntityStatusPacket(1, 2),
            new EntityStatusPacket(1, EntityStatusPacket.ANIMAL_HEARTS),
            new EntityTeleportPacket(1, 2, 3, 4, 5, 6),
            new EntityTeleportPacket(1, 2, 3, 4, 5, 6, false),
            new RelativeEntityPositionRotationPacket(1, (short) 2, (short) 3, (short) 4, 5, 6),
            new RelativeEntityPositionRotationPacket(1, (short) 2, (short) 3, (short) 4, 5, 6, false),
            new SpawnLightningPacket(1, 2, 3, 4),
            new SpawnLightningPacket(1, 2, 3, 4, 5),
            new BlockActionPacket(1, 2, 3, 4, 5, 6),
            new BlockChangePacket(1, 2, 3, 4),
            new BlockChangePacket(1, 2, 3, 4, 5),
            //new ChunkBulkMessage(...),
            //new ChunkDataMessage(...),
            new ExperiencePacket(1f, 2, 3),
            //new MapDataMessage(...),
            //new MultiBlockChangeMessage(...),
            //PlayEffect
            //PlayParticle
            //PlaySound
            //SignEditor
            new StateChangePacket(1, 2f),
            new StateChangePacket(StateChangePacket.Reason.GAMEMODE, 2f),
            //Statistic
            new TitlePacket(TitlePacket.Action.TITLE, new TextMessage("Title")),
            new TitlePacket(TitlePacket.Action.TIMES, 1, 3, 4),
            new TitlePacket(TitlePacket.Action.RESET),
            new PlayerListHeaderFooterPacket(new TextMessage("head"), new TextMessage("foot")),
            //UserListItem
            //WorldBorder
            //OpenWindow
            //SetWindowContents
            //SetWindowSlot
            //WindowProperty
            new CameraPacket(1),
            //CombatEvent
            new ResourcePackSendPacket("url", "hash"),
            new ServerDifficultyPacket(1),
            new UseBedPacket(1, 2, 3, 4),
            //TabCompleteResponse
            new UseItemPacket(0),
            new VehicleMovePacket(1.0,2.0,3.0,4f,5f),
            new TeleportConfirmPacket(1),
    };

    public PlayProtocolTest() {
        super(new PlayProtocol(), TEST_MESSAGES);
    }
}
