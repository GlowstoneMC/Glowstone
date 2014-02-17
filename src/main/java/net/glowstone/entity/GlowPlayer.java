package net.glowstone.entity;

import com.flowpowered.networking.Message;
import net.glowstone.*;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.inventory.InventoryMonitor;
import net.glowstone.io.StorageOperation;
import net.glowstone.msg.*;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.login.LoginSuccessMessage;
import net.glowstone.net.message.play.entity.DestroyEntitiesMessage;
import net.glowstone.net.message.play.game.*;
import net.glowstone.net.message.play.inv.*;
import net.glowstone.net.protocol.PlayProtocol;
import net.glowstone.util.TextWrapper;
import org.bukkit.*;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.scoreboard.Scoreboard;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.logging.Level;

/**
 * Represents an in-game player.
 * @author Graham Edgecombe
 */
@DelegateDeserialization(GlowOfflinePlayer.class)
public final class GlowPlayer extends GlowHumanEntity implements Player {

    /**
     * The normal height of a player's eyes above their feet.
     */
    public static final double EYE_HEIGHT = 1.62D;

    /**
     * This player's session.
     */
    private final GlowSession session;

    /**
     * This player's unique id.
     */
    private final UUID uuid;

    /**
     * Cumulative amount of experience points the player has collected.
     */
    private int experience = 0;

    /**
     * The current level (or skill point amount) of the player.
     */
    private int level = 0;

    /**
     * The player's current exhaustion level.
     */
    private float exhaustion = 0;

    /**
     * The player's current saturation level.
     */
    private float saturation = 0;

    /**
     * This player's current time offset.
     */
    private long timeOffset = 0;

    /**
     * Whether the time offset is relative.
     */
    private boolean timeRelative = true;

    /**
     * The display name of this player, for chat purposes.
     */
    private String displayName;

    /**
     * The player's compass target.
     */
    private Location compassTarget;

    /**
     * The entities that the client knows about.
     */
    private final Set<GlowEntity> knownEntities = new HashSet<GlowEntity>();

    /**
     * The chunks that the client knows about.
     */
    private final Set<GlowChunk.Key> knownChunks = new HashSet<GlowChunk.Key>();

    /**
     * A queue of BlockChangeMessages to be sent.
     */
    private final List<BlockChangeMessage> blockChanges = new LinkedList<>();

    /**
     * The lock used to prevent chunks from unloading near the player.
     */
    private ChunkManager.ChunkLock chunkLock;

    /**
     * The tracker for changes to the currently open inventory.
     */
    private InventoryMonitor invMonitor;

    /**
     * Whether the player is sneaking.
     */
    private boolean sneaking = false;

    /**
     * The human entity's current food level
     */
    private int food = 20;

    /**
     * The bed spawn location of a player
     */
    private Location bedSpawn;

    /**
     * The name a player has in the player list
     */
    private String playerListName;

    /**
     * Creates a new player and adds it to the world.
     * @param session The player's session.
     * @param name    The player's name.
     */
    public GlowPlayer(GlowSession session, String name, UUID uuid) {
        super(session.getServer(), (GlowWorld) session.getServer().getWorlds().get(0), name);
        this.session = session;
        this.uuid = uuid;

        chunkLock = world.newChunkLock(getName());

        // send login response
        session.send(new LoginSuccessMessage(uuid.toString().replace("-", ""), name));
        session.setProtocol(new PlayProtocol(session.getServer()));

        // send join game
        // in future, handle hardcore, difficulty, and level type
        String type = "default";//world.getWorldType().getName().toLowerCase();
        int gameMode = getGameMode().getValue();
        if (server.isHardcore()) {
            gameMode |= 0x8;
        }
        session.send(new JoinGameMessage(getEntityId(), gameMode, world.getEnvironment().getId(), world.getDifficulty().getValue(), session.getServer().getMaxPlayers(), type));

        loadData();
        saveData();

        streamBlocks(); // stream the initial set of blocks
        setCompassTarget(world.getSpawnLocation()); // set our compass target
        session.send(new StateChangeMessage(getWorld().hasStorm() ? 2 : 1, 0)); // send the world's weather

        invMonitor = new InventoryMonitor(getOpenInventory());
        updateInventory(); // send inventory contents

        // send initial location
        double y = location.getY() + getEyeHeight() + 0.05;
        session.send(new PositionRotationMessage(location.getX(), y, location.getZ(), location.getYaw(), location.getPitch(), true));
    }

    // -- Various internal mechanisms

    /**
     * Destroys this entity by removing it from the world and marking it as not
     * being active.
     */
    @Override
    public void remove() {
        knownChunks.clear();
        chunkLock.clear();
        saveData();
        getInventory().removeViewer(this);
        getInventory().getCraftingInventory().removeViewer(this);
        permissions.clearPermissions();
        super.remove();
    }

    @Override
    public void pulse() {
        super.pulse();

        // stream world
        streamBlocks();
        processBlockChanges();

        // update inventory
        for (InventoryMonitor.Entry entry : invMonitor.getChanges()) {
            sendItemChange(entry.slot, entry.item);
        }

        // update or remove entities
        for (Iterator<GlowEntity> it = knownEntities.iterator(); it.hasNext(); ) {
            GlowEntity entity = it.next();
            boolean withinDistance = !entity.isDead() && isWithinDistance(entity);

            if (withinDistance) {
                for (Message msg : entity.createUpdateMessage()) {
                    session.send(msg);
                }
            } else {
                session.send(new DestroyEntitiesMessage(entity.getEntityId()));
                it.remove();
            }
        }

        // add entities
        for (GlowEntity entity : world.getEntityManager()) {
            if (entity == this)
                continue;
            boolean withinDistance = !entity.isDead() && isWithinDistance(entity);

            if (withinDistance && !knownEntities.contains(entity)) {
                knownEntities.add(entity);
                for (Message msg : entity.createSpawnMessage()) {
                    session.send(msg);
                }
            }
        }
    }

    /**
     * Process and send pending BlockChangeMessages.
     */
    private void processBlockChanges() {
        List<BlockChangeMessage> messages = new ArrayList<>(blockChanges);
        blockChanges.clear();

        // separate messages by chunk
        Map<GlowChunk.Key, List<BlockChangeMessage>> chunks = new HashMap<>();
        for (BlockChangeMessage message : messages) {
            GlowChunk.Key key = new GlowChunk.Key(message.getX() >> 4, message.getZ() >> 4);
            List<BlockChangeMessage> list = chunks.get(key);
            if (list == null) {
                list = new LinkedList<>();
                chunks.put(key, list);
            }
            list.add(message);
        }

        // send away
        for (Map.Entry<GlowChunk.Key, List<BlockChangeMessage>> entry : chunks.entrySet()) {
            GlowChunk.Key key = entry.getKey();
            List<BlockChangeMessage> value = entry.getValue();

            if (value.size() == 1) {
                session.send(value.get(0));
            } else if (value.size() > 1) {
                BlockChangeMessage[] records = value.toArray(new BlockChangeMessage[value.size()]);
                session.send(new MultiBlockChangeMessage(key.getX(), key.getZ(), records));
            }
        }
    }

    /**
     * Streams chunks to the player's client.
     */
    private void streamBlocks() {
        Set<GlowChunk.Key> previousChunks = new HashSet<GlowChunk.Key>(knownChunks);
        ArrayList<GlowChunk.Key> newChunks = new ArrayList<GlowChunk.Key>();

        int centralX = location.getBlockX() >> 4;
        int centralZ = location.getBlockZ() >> 4;

        int radius = server.getViewDistance();
        for (int x = (centralX - radius); x <= (centralX + radius); x++) {
            for (int z = (centralZ - radius); z <= (centralZ + radius); z++) {
                GlowChunk.Key key = new GlowChunk.Key(x, z);
                if (knownChunks.contains(key)) {
                    previousChunks.remove(key);
                } else {
                    newChunks.add(key);
                }
            }
        }

        if (newChunks.size() == 0 && previousChunks.size() == 0) {
            return;
        }

        Collections.sort(newChunks, new Comparator<GlowChunk.Key>() {
            public int compare(GlowChunk.Key a, GlowChunk.Key b) {
                double dx = 16 * a.getX() + 8 - location.getX();
                double dz = 16 * a.getZ() + 8 - location.getZ();
                double da = dx * dx + dz * dz;
                dx = 16 * b.getX() + 8 - location.getX();
                dz = 16 * b.getZ() + 8 - location.getZ();
                double db = dx * dx + dz * dz;
                return Double.compare(da, db);
            }
        });

        List<GlowChunk> bulkChunks = null;
        if (newChunks.size() > knownChunks.size() * 2 / 5) {
            // send a bulk message
            bulkChunks = new LinkedList<GlowChunk>();
        }

        // populate then send chunks to the player

        // done in two steps so that all the new chunks are finalized before any of them are sent
        // this prevents sending a chunk then immediately sending block changes in it because
        // one of its neighbors has populated
        for (GlowChunk.Key key : newChunks) {
            world.getChunkManager().forcePopulation(key.getX(), key.getZ());
        }
        for (GlowChunk.Key key : newChunks) {
            GlowChunk chunk = world.getChunkAt(key.getX(), key.getZ());
            if (bulkChunks == null) {
                session.send(chunk.toMessage());
            } else {
                bulkChunks.add(chunk);
            }
            knownChunks.add(key);
            chunkLock.acquire(key);
        }

        if (bulkChunks != null) {
            boolean skylight = world.getEnvironment() == World.Environment.NORMAL;
            session.send(new ChunkBulkMessage(skylight, bulkChunks));
        }

        for (GlowChunk.Key key : newChunks) {
            GlowChunk chunk = world.getChunkAt(key.getX(), key.getZ());
            for (GlowBlockState state : chunk.getTileEntities()) {
                state.update(this);
            }
        }

        for (GlowChunk.Key key : previousChunks) {
            session.send(ChunkDataMessage.empty(key.getX(), key.getZ()));
            knownChunks.remove(key);
            chunkLock.release(key);
        }

        previousChunks.clear();
    }

    /**
     * Checks whether the player can see the given chunk.
     * @return If the chunk is known to the player's client.
     */
    public boolean canSee(GlowChunk.Key chunk) {
        return knownChunks.contains(chunk);
    }

    /**
     * Checks whether the player can see the given entity.
     * @return If the entity is known to the player's client.
     */
    public boolean canSee(GlowEntity entity) {
        return knownEntities.contains(entity);
    }

    // -- Basic getters

    /**
     * Gets the session.
     * @return The session.
     */
    public GlowSession getSession() {
        return session;
    }

    public boolean isOnline() {
        return true;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    public boolean isBanned() {
        return server.getBanList(BanList.Type.NAME).isBanned(getName());
    }

    @Deprecated
    public void setBanned(boolean banned) {
        server.getBanList(BanList.Type.NAME).addBan(getName(), null, null, null);
    }

    public boolean isWhitelisted() {
        return !server.hasWhitelist() || server.getWhitelist().contains(getName());
    }

    public void setWhitelisted(boolean value) {
        if (value) {
            server.getWhitelist().add(getName());
        } else {
            server.getWhitelist().remove(getName());
        }
    }

    public Player getPlayer() {
        return this;
    }

    public InetSocketAddress getAddress() {
        return session.getAddress();
    }

    @Override
    public boolean isOp() {
        return getServer().getOpsList().contains(getName());
    }

    @Override
    public void setOp(boolean value) {
        if (value) {
            getServer().getOpsList().add(getName());
        } else {
            getServer().getOpsList().remove(getName());
        }
        permissions.recalculatePermissions();
    }

    // -- Malleable properties

    public String getDisplayName() {
        return displayName == null ? getName() : displayName;
    }

    public void setDisplayName(String name) {
        displayName = name;
    }

    public String getPlayerListName() {
        return playerListName == null || "".equals(playerListName) ? getName() : playerListName;
    }

    public void setPlayerListName(String name) {
        if (name.length() > 15)
            throw new IllegalArgumentException("The given name was " + name.length() + " chars long, longer than the maximum of 16");
        for (Player player : server.getOnlinePlayers()) {
            if (player.getPlayerListName().equals(getPlayerListName()))
                throw new IllegalArgumentException("The name given, " + name + ", is already used by " + player.getName() + ".");
        }
        net.glowstone.msg.Message removeMessage = new UserListItemMessage(getPlayerListName(), false, (short) 0);
        playerListName = name;
        net.glowstone.msg.Message reAddMessage = new UserListItemMessage(getPlayerListName(), true, (short) 0);
        for (Player player : server.getOnlinePlayers()) {
            ((GlowPlayer) player).getSession().send(removeMessage);
            ((GlowPlayer) player).getSession().send(reAddMessage);
        }
    }

    public Location getCompassTarget() {
        return compassTarget;
    }

    public void setCompassTarget(Location loc) {
        compassTarget = loc;
        session.send(new SpawnPositionMessage(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }

    public boolean isSneaking() {
        return (metadata.getByte(MetadataIndex.STATUS) & 0x02) != 0;
    }

    public void setSneaking(boolean sneak) {
        if (EventFactory.onPlayerToggleSneak(this, sneak).isCancelled()) {
            return;
        }

        if (sneak) {
            metadata.setBit(MetadataIndex.STATUS, 0x02);
        } else {
            metadata.clearBit(MetadataIndex.STATUS, 0x02);
        }

        updateMetadata();
    }

    public boolean isSprinting() {
        return metadata.getBit(MetadataIndex.STATUS, 0x08);
    }

    public void setSprinting(boolean sprinting) {
        // todo: event

        if (sprinting) {
            metadata.setBit(MetadataIndex.STATUS, 0x08);
        } else {
            metadata.clearBit(MetadataIndex.STATUS, 0x08);
        }

        updateMetadata();
    }

    public boolean isSleepingIgnored() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setSleepingIgnored(boolean isSleeping) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setGameMode(GameMode mode) {
        boolean changed = getGameMode() != mode;
        super.setGameMode(mode);
        if (changed) session.send(new StateChangeMessage(3, mode.getValue()));
    }

    // todo: most of the exp stuff is pretty broken

    public int getExperience() {
        return experience % ((getLevel() + 1) * 7);
    }

    public void setExperience(int exp) {
        setTotalExperience(experience - getExperience() + exp);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        experience = 0;
        for (this.level = 0; this.level < level; ++this.level) {
            experience += getExpToLevel();
        }
        session.send(createExperienceMessage());
    }

    public int getTotalExperience() {
        return experience;
    }

    public void setTotalExperience(int exp) {
        int calcExperience = exp;
        this.experience = exp;
        level = 0;
        while ((calcExperience -= getExpToLevel()) > 0) ++level;
        session.send(createExperienceMessage());
    }

    public void giveExp(int xp) {
        experience += xp;
        while (experience > (getLevel() + 1) * 7) {
            experience -= (getLevel() + 1) * 7;
            ++level;
        }
        session.send(createExperienceMessage());
    }

    public float getExp() {
        return (float) experience / getExpToLevel();
    }

    public void setExp(float percentToLevel) {
        experience = (int) (percentToLevel * getExpToLevel());
    }

    @Override
    public int getExpToLevel() {
        return getExpToLevel(level);
    }

    private int getExpToLevel(int level) {
        if (level >= 30) {
            return 62 + (level - 30) * 7;
        } else if (level >= 15) {
            return 17 + (level - 15) * 3;
        } else {
            return 17;
        }
    }

    public float getExhaustion() {
        return exhaustion;
    }

    public void setExhaustion(float value) {
        exhaustion = value;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float value) {
        saturation = value;
        session.send(createHealthMessage());
    }

    // -- Actions

    /**
     * Teleport the player.
     * @param location The destination to teleport to.
     * @return Whether the teleport was a success.
     */
    @Override
    public boolean teleport(Location location) {
        return teleport(location, TeleportCause.UNKNOWN);
    }

    @Override
    public boolean teleport(Location location, TeleportCause cause) {
        if (this.location != null && this.location.getWorld() != null) {
            PlayerTeleportEvent event = EventFactory.onPlayerTeleport(this, getLocation(), location, cause);
            if (event.isCancelled()) return false;
            location = event.getTo();
        }

        // account for floating point shenanigans in client physics
        double y = location.getY() + getEyeHeight() + 0.05;
        PositionRotationMessage message = new PositionRotationMessage(location.getX(), y, location.getZ(), location.getYaw(), location.getPitch(), true);

        if (location.getWorld() != world) {
            GlowWorld oldWorld = world;
            world.getEntityManager().deallocate(this);

            world = (GlowWorld) location.getWorld();
            world.getEntityManager().allocate(this);

            for (GlowChunk.Key key : knownChunks) {
                session.send(ChunkDataMessage.empty(key.getX(), key.getZ()));
            }
            knownChunks.clear();
            chunkLock.clear();
            chunkLock = world.newChunkLock(getName());

            session.send(new RespawnMessage((byte) world.getEnvironment().getId(), (byte) 1, (byte) getGameMode().getValue(), (short) world.getMaxHeight(), world.getSeed()));
            streamBlocks(); // stream blocks

            setCompassTarget(world.getSpawnLocation()); // set our compass target
            this.session.send(message);
            this.location = location; // take us to spawn position
            session.send(new StateChangeMessage((byte) (getWorld().hasStorm() ? 1 : 2), (byte) 0)); // send the world's weather
            reset();
            EventFactory.onPlayerChangedWorld(this, oldWorld);
        } else {
            this.session.send(message);
            this.location = location;
            reset();
        }

        return true;
    }

    public void sendMessage(String message) {
        sendRawMessage(message);
    }

    public void sendMessage(String[] messages) {
        for (String line : messages) {
            sendMessage(line);
        }
    }

    public void sendRawMessage(String message) {
        // todo: use chat components instead of plain text
        // textwrapper also does not preserve non-color formatting
        for (String line : TextWrapper.wrapText(message)) {
            session.send(new ChatMessage(line));
        }
    }

    public void kickPlayer(String message) {
        session.disconnect(message == null ? "" : message);
    }

    public boolean performCommand(String command) {
        return getServer().dispatchCommand(this, command);
    }

    /**
     * Says a message (or runs a command).
     * @param text message to print
     */
    public void chat(String text) {
        if (text.startsWith("/")) {
            try {
                PlayerCommandPreprocessEvent event = EventFactory.onPlayerCommand(this, text);
                if (event.isCancelled()) {
                    return;
                }

                server.getLogger().info(event.getPlayer().getName() + " issued command: " + event.getMessage());
                getServer().dispatchCommand(event.getPlayer(), event.getMessage().substring(1));
            } catch (Exception ex) {
                sendMessage(ChatColor.RED + "An internal error occured while executing your command.");
                getServer().getLogger().log(Level.SEVERE, "Exception while executing command: " + text, ex);
            }
        } else {
            PlayerChatEvent event = EventFactory.onPlayerChat(this, text);
            if (event.isCancelled()) {
                return;
            }

            String message = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
            getServer().getLogger().info(message);
            for (Player recipient : event.getRecipients()) {
                recipient.sendMessage(message);
            }
        }
    }

    public void saveData() {
        saveData(true);
    }

    public void saveData(boolean async) {
        final GlowWorld dataWorld = (GlowWorld) server.getWorlds().get(0);
        if (async) {
            final GlowPlayer player = this;
            server.getStorageQueue().queue(new StorageOperation() {
                @Override
                public boolean isParallel() {
                    return true;
                }

                @Override
                public String getGroup() {
                    return getName() + "_" + getWorld().getName();
                }

                @Override
                public boolean queueMultiple() {
                    return true;
                }

                @Override
                public String getOperation() {
                    return "player-data-save";
                }

                public void run() {
                    dataWorld.getMetadataService().writePlayerData(player);
                }
            });
        } else {
            dataWorld.getMetadataService().writePlayerData(this);
        }
    }

    public void loadData() {
        GlowWorld dataWorld = (GlowWorld) server.getWorlds().get(0);
        dataWorld.getMetadataService().readPlayerData(this);
    }

    // -- Data transmission

    public void playNote(Location loc, Instrument instrument, Note note) {
        playNote(loc, instrument.getType(), note.getId());
    }

    public void playNote(Location loc, byte instrument, byte note) {
        session.send(new PlayNoteMessage(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), instrument, note));
    }

    public void playEffect(Location loc, Effect effect, int data) {
        session.send(new PlayEffectMessage(effect.getId(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), data));
    }

    public void sendBlockChange(Location loc, Material material, byte data) {
        sendBlockChange(loc, material.getId(), data);
    }

    public void sendBlockChange(Location loc, int material, byte data) {
        sendBlockChange(new BlockChangeMessage(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), material, data));
    }

    public void sendBlockChange(BlockChangeMessage message) {
        // only send message if the chunk is within visible range
        GlowChunk.Key key = new GlowChunk.Key(message.getX() >> 4, message.getZ() >> 4);
        if (canSee(key)) {
            blockChanges.add(message);
        }
    }

    public boolean sendChunkChange(Location loc, int sx, int sy, int sz, byte[] data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // -- Achievements & Statistics [mostly borrowed from CraftBukkit]

    public void awardAchievement(Achievement achievement) {
        //sendStatistic(achievement.getId(), 1);
    }

    public void incrementStatistic(Statistic statistic) {
        incrementStatistic(statistic, 1);
    }

    public void incrementStatistic(Statistic statistic, int amount) {
        //sendStatistic(statistic.getId(), amount);
    }

    public void incrementStatistic(Statistic statistic, Material material) {
        incrementStatistic(statistic, material, 1);
    }

    public void incrementStatistic(Statistic statistic, Material material, int amount) {
        if (!statistic.isSubstatistic()) {
            throw new IllegalArgumentException("Given statistic is not a substatistic");
        }
        if (statistic.isBlock() != material.isBlock()) {
            throw new IllegalArgumentException("Given material is not valid for this substatistic");
        }

        int mat = material.getId();

        if (!material.isBlock()) {
            mat -= 255;
        }

        //sendStatistic(statistic.getId() + mat, amount);
    }

    private void sendStatistic(int id, int amount) {
        while (amount > Byte.MAX_VALUE) {
            sendStatistic(id, Byte.MAX_VALUE);
            amount -= Byte.MAX_VALUE;
        }

        if (amount > 0) {
            session.send(new StatisticMessage(id, (byte) amount));
        }
    }

    public void setStatistic(Statistic statistic, EntityType entityType, int newValue) {

    }

    public void removeAchievement(Achievement achievement) {

    }

    public boolean hasAchievement(Achievement achievement) {
        return false;
    }

    public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {

    }

    public void decrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException {

    }

    public void setStatistic(Statistic statistic, int newValue) throws IllegalArgumentException {

    }

    public int getStatistic(Statistic statistic) throws IllegalArgumentException {
        return 0;
    }

    public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {

    }

    public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        return 0;
    }

    public void decrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException {

    }

    public void setStatistic(Statistic statistic, Material material, int newValue) throws IllegalArgumentException {

    }

    public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {

    }

    public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {

    }

    public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        return 0;
    }

    public void incrementStatistic(Statistic statistic, EntityType entityType, int amount) throws IllegalArgumentException {

    }

    public void decrementStatistic(Statistic statistic, EntityType entityType, int amount) {

    }

    ////////////////////////////////////////////////////////////////////////////
    // Inventory

    public void updateInventory() {
        session.send(new SetWindowContentsMessage(invMonitor.getId(), invMonitor.getContents()));
    }

    public void sendItemChange(int slot, ItemStack item) {
        session.send(new SetWindowSlotMessage(invMonitor.getId(), slot, item));
    }

    @Override
    public void setItemOnCursor(ItemStack item) {
        super.setItemOnCursor(item);
        session.send(new SetWindowSlotMessage(-1, -1, item));
    }

    @Override
    public boolean setWindowProperty(InventoryView.Property prop, int value) {
        if (!super.setWindowProperty(prop, value)) return false;
        session.send(new WindowPropertyMessage(invMonitor.getId(), prop.getId(), value));
        return true;
    }

    @Override
    public void openInventory(InventoryView view) {
        session.send(new CloseWindowMessage(invMonitor.getId()));

        super.openInventory(view);

        invMonitor = new InventoryMonitor(getOpenInventory());
        int viewId = invMonitor.getId();
        if (viewId != 0) {
            String title = view.getTitle();
            boolean useTitle = !view.getType().getDefaultTitle().equals(title);
            if (view.getTopInventory() instanceof PlayerInventory && !useTitle) {
                title = ((PlayerInventory) view.getTopInventory()).getHolder().getName();
                useTitle = true;
            }
            Message open = new OpenWindowMessage(viewId, invMonitor.getType(), title, view.getTopInventory().getSize(), useTitle);
            session.send(open);
        }

        updateInventory();
    }

    // -- Goofy relative time stuff --

    public void setPlayerTime(long time, boolean relative) {
        timeOffset = time % 24000;
        timeRelative = relative;

        if (timeOffset < 0) timeOffset += 24000;
    }

    public long getPlayerTime() {
        if (timeRelative) {
            // add timeOffset ticks to current time
            return (world.getTime() + timeOffset) % 24000;
        } else {
            // return time offset
            return timeOffset % 24000;
        }
    }

    public long getPlayerTimeOffset() {
        return timeOffset;
    }

    public boolean isPlayerTimeRelative() {
        return timeRelative;
    }

    public void resetPlayerTime() {
        setPlayerTime(0, true);
    }

    public void sendMap(MapView map) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setHealth(double health) {
        super.setHealth(health);
        session.send(createHealthMessage());
    }

    public int getFoodLevel() {
        return food;
    }

    public void setFoodLevel(int food) {
        this.food = Math.min(food, 20);
        session.send(createHealthMessage());
    }

    public HealthMessage createHealthMessage() {
        return new HealthMessage((float) getHealth(), getFoodLevel(), getSaturation());
    }

    public ExperienceMessage createExperienceMessage() {
        return new ExperienceMessage(getExp(), (byte) getLevel(), (short) getTotalExperience());
    }

    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("name", getName());
        return ret;
    }

    // NEW STUFF


    @Override
    public void playSound(Location location, Sound sound, float volume, float pitch) {

    }

    @Override
    public void playSound(Location location, String sound, float volume, float pitch) {

    }

    @Override
    public <T> void playEffect(Location loc, Effect effect, T data) {

    }

    @Override
    public void setPlayerWeather(WeatherType type) {

    }

    @Override
    public WeatherType getPlayerWeather() {
        return null;
    }

    @Override
    public void resetPlayerWeather() {

    }

    @Override
    public void giveExpLevels(int amount) {

    }

    @Override
    public void setBedSpawnLocation(Location location, boolean force) {

    }

    @Override
    public boolean getAllowFlight() {
        return false;
    }

    @Override
    public void setAllowFlight(boolean flight) {

    }

    @Override
    public void hidePlayer(Player player) {

    }

    @Override
    public void showPlayer(Player player) {

    }

    @Override
    public boolean canSee(Player player) {
        return false;
    }

    @Override
    public boolean isFlying() {
        return false;
    }

    @Override
    public void setFlying(boolean value) {

    }

    @Override
    public void setFlySpeed(float value) throws IllegalArgumentException {

    }

    @Override
    public void setWalkSpeed(float value) throws IllegalArgumentException {

    }

    @Override
    public float getFlySpeed() {
        return 0;
    }

    @Override
    public float getWalkSpeed() {
        return 0;
    }

    @Override
    public void setTexturePack(String url) {

    }

    @Override
    public void setResourcePack(String url) {

    }

    @Override
    public Scoreboard getScoreboard() {
        return null;
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {

    }

    @Override
    public boolean isHealthScaled() {
        return false;
    }

    @Override
    public void setHealthScaled(boolean scale) {

    }

    @Override
    public void setHealthScale(double scale) throws IllegalArgumentException {

    }

    @Override
    public double getHealthScale() {
        return 0;
    }

    @Override
    public boolean isConversing() {
        return false;
    }

    @Override
    public void acceptConversationInput(String input) {

    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        return false;
    }

    @Override
    public void abandonConversation(Conversation conversation) {

    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {

    }

    @Override
    public long getFirstPlayed() {
        return 0;
    }

    @Override
    public long getLastPlayed() {
        return 0;
    }

    @Override
    public boolean hasPlayedBefore() {
        return false;
    }

    @Override
    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
        // todo: send and handle REGISTER, UNREGISTER
        StandardMessenger.validatePluginMessage(getServer().getMessenger(), source, channel, message);
        session.send(new PluginMessage(channel, message));
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        // todo: keep track of client's accepted channels
        return null;
    }


}
