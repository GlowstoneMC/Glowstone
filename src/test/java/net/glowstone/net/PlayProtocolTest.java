package net.glowstone.net;

import com.flowpowered.networking.Message;
import net.glowstone.net.message.play.entity.*;
import net.glowstone.net.message.play.game.*;
import net.glowstone.net.message.play.inv.*;
import net.glowstone.net.message.play.player.*;
import net.glowstone.net.protocol.PlayProtocol;
import net.glowstone.util.TextMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.UUID;

//@PrepareForTest(Bukkit.class)
public class PlayProtocolTest extends BaseProtocolTest {

    private static final Message[] TEST_MESSAGES = new Message[]{
            new PingMessage(1),
            new IncomingChatMessage("test"),
            new InteractEntityMessage(1, 0),
            new InteractEntityMessage(1, 2, 1f, 2f, 3f),
            new PlayerUpdateMessage(true),
            new PlayerPositionMessage(true, 1.0, 2.0, 3.0),
            new PlayerLookMessage(3f, 2f, true),
            new PlayerPositionLookMessage(true, 1.0, 2.0, 3.0, 1f, 2f),
            new DiggingMessage(0, 1, 2, 3, 4),
            new BlockPlacementMessage(1, 2, 3, 4, new ItemStack(Material.DIRT, 1), 5, 6, 7), //Does a NPE on getItemFactory
            new PlayerSwingArmMessage(),
            //new HeldItemMessage(1), //GG mojang, send a byte, receive a short
            new PlayerActionMessage(1, 2, 3),
            new SteerVehicleMessage(1f, 2f, true, false),
            new CloseWindowMessage(1),
            new WindowClickMessage(1, 2, 3, 4, 5, new ItemStack(Material.APPLE, 1)), //Does a NPE on getItemFactory
            new TransactionMessage(1, 2, true),
            new CreativeItemMessage(1, new ItemStack(Material.APPLE, 1)), //Does a NPE on getItemFactory
            new EnchantItemMessage(1, 2),
            new UpdateSignMessage(1, 2, 3, new TextMessage[]{new TextMessage("hello"), new TextMessage("hi"), new TextMessage("third"), new TextMessage("fourth")}),
            UpdateSignMessage.fromPlainText(1, 2, 3, new String[]{"hello", "hi", "third", "fourth"}),
            new PlayerAbilitiesMessage(1, 2f, 3f),
            new TabCompleteMessage("text"),
            new TabCompleteMessage("text", new BlockVector(1, 2, 3)),
            new ClientSettingsMessage("en-en", 16, 1, true, 2),
            new ClientStatusMessage(1),
            new PluginMessage("glowstone", new byte[]{0x00, 0x11}),
            new SpectateMessage(UUID.randomUUID()),
            new ResourcePackStatusMessage("glowstonehash", 1),
            new JoinGameMessage(1, 2, 3, 4, 5, "normal", true),
            new ChatMessage(ProtocolTestUtils.getTextMessage(), 1),
            new ChatMessage(ProtocolTestUtils.getJson()),
            new ChatMessage("glowstone"),
            new TimeMessage(1, 2),
            new EntityEquipmentMessage(1, 2, new ItemStack(Material.APPLE, 1)), //Does a NPE on getItemFactory
            new SpawnPositionMessage(1, 2, 3),
            new HealthMessage(1f, 2, 3f),
            new RespawnMessage(1, 2, 3, "world"), //Cannot decode
            new PositionRotationMessage(1.0, 2.0, 3.0, 1f, 2f),
            new PositionRotationMessage(1.0, 2.0, 3.0, 4f, 5f, 6),
            //TODO Test PositionRotationMessage with world parameter
            new AnimateEntityMessage(1, 2),
            new SpawnPlayerMessage(1, UUID.randomUUID(), 2, 3, 4, 5, 6, 7, ProtocolTestUtils.getMetadataEntry()),
            new CollectItemMessage(1, 2),
            new SpawnObjectMessage(1, 2, 3, 4, 5, 6, 7),
            new SpawnObjectMessage(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            new SpawnMobMessage(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, ProtocolTestUtils.getMetadataEntry()),
            new SpawnPaintingMessage(1, "painting", 2, 3, 4, 5),
            new SpawnXpOrbMessage(1, 2, 3, 4, (short) 5),
            new EntityVelocityMessage(1, new Vector(1, 2, 3)),
            new EntityVelocityMessage(1, 2, 3, 4),
            new DestroyEntitiesMessage(1, 2, 3),
            new RelativeEntityPositionMessage(1, 2, 3, 4),
            new RelativeEntityPositionMessage(1, 2, 3, 4, true),

    };

    public PlayProtocolTest() throws NoSuchFieldException, IllegalAccessException {
        super(new PlayProtocol(), TEST_MESSAGES);
    }
}
