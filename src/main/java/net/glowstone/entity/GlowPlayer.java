package net.glowstone.entity;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.player.PlayerChatEvent;

import net.glowstone.EventFactory;
import net.glowstone.GlowChunk;
import net.glowstone.GlowWorld;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.inventory.GlowPlayerInventory;
import net.glowstone.inventory.InventoryViewer;
import net.glowstone.util.Parameter;
import net.glowstone.util.TextWrapper;
import net.glowstone.msg.BlockChangeMessage;
import net.glowstone.msg.ChatMessage;
import net.glowstone.msg.DestroyEntityMessage;
import net.glowstone.msg.EntityEquipmentMessage;
import net.glowstone.msg.EntityMetadataMessage;
import net.glowstone.msg.LoadChunkMessage;
import net.glowstone.msg.Message;
import net.glowstone.msg.PlayEffectMessage;
import net.glowstone.msg.PlayNoteMessage;
import net.glowstone.msg.PositionRotationMessage;
import net.glowstone.msg.RespawnMessage;
import net.glowstone.msg.SetWindowSlotMessage;
import net.glowstone.msg.SpawnPositionMessage;
import net.glowstone.msg.StateChangeMessage;
import net.glowstone.msg.StatisticMessage;
import net.glowstone.net.Session;
import org.bukkit.map.MapView;

/**
 * Represents an in-game player.
 * @author Graham Edgecombe
 */
public final class GlowPlayer extends GlowHumanEntity implements Player, InventoryViewer {

    /**
     * The normal height of a player's eyes above their feet.
     */
    public static final double EYE_HEIGHT = 1.62D;

    /**
     * This player's session.
     */
    private final Session session;
    
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
    private Set<GlowEntity> knownEntities = new HashSet<GlowEntity>();

    /**
     * The chunks that the client knows about.
     */
    private final Set<GlowChunk.Key> knownChunks = new HashSet<GlowChunk.Key>();
    
    /**
     * The item the player has on their cursor.
     */
    private ItemStack itemOnCursor;

    /**
     * Whether the player is sneaking.
     */
    private boolean sneaking = false;

    /**
     * Creates a new player and adds it to the world.
     * @param session The player's session.
     * @param name The player's name.
     */
    public GlowPlayer(Session session, String name) {
        super(session.getServer(), (GlowWorld) session.getServer().getWorlds().get(0), name);
        this.session = session;

        streamBlocks(); // stream the initial set of blocks
        setCompassTarget(world.getSpawnLocation()); // set our compass target
        teleport(world.getSpawnLocation()); // take us to spawn position
        session.send(new StateChangeMessage((byte)(getWorld().hasStorm() ? 1 : 2))); // send the world's weather
        
        getInventory().addViewer(this);
        getInventory().getCraftingInventory().addViewer(this);
    }
    
    // -- Various internal mechanisms

    /**
     * Destroys this entity by removing it from the world and marking it as not
     * being active.
     */
    @Override
    public void remove() {
        getInventory().removeViewer(this);
        getInventory().getCraftingInventory().removeViewer(this);
        super.remove();
    }

    @Override
    public void pulse() {
        super.pulse();

        streamBlocks();

        for (Iterator<GlowEntity> it = knownEntities.iterator(); it.hasNext(); ) {
            GlowEntity entity = it.next();
            boolean withinDistance = !entity.isDead() && isWithinDistance(entity);

            if (withinDistance) {
                Message msg = entity.createUpdateMessage();
                if (msg != null)
                    session.send(msg);
            } else {
                session.send(new DestroyEntityMessage(entity.getEntityId()));
                it.remove();
            }
        }

        for (GlowEntity entity : world.getEntityManager()) {
            if (entity == this)
                continue;
            boolean withinDistance = !entity.isDead() && isWithinDistance(entity);

            if (withinDistance && !knownEntities.contains(entity)) {
                knownEntities.add(entity);
                session.send(entity.createSpawnMessage());
            }
        }
    }

    /**
     * Streams chunks to the player's client.
     */
    private void streamBlocks() {
        Set<GlowChunk.Key> previousChunks = new HashSet<GlowChunk.Key>(knownChunks);

        int centralX = ((int) location.getX()) >> 4;
        int centralZ = ((int) location.getZ()) >> 4;
        
        int radius = server.getViewDistance();
        for (int x = (centralX - radius); x <= (centralX + radius); x++) {
            for (int z = (centralZ - radius); z <= (centralZ + radius); z++) {
                GlowChunk.Key key = new GlowChunk.Key(x, z);
                if (!knownChunks.contains(key)) {
                    knownChunks.add(key);
                    session.send(new LoadChunkMessage(x, z, true));
                    session.send(world.getChunkAt(x, z).toMessage());
                }
                previousChunks.remove(key);
            }
        }

        for (GlowChunk.Key key : previousChunks) {
            session.send(new LoadChunkMessage(key.getX(), key.getZ(), false));
            knownChunks.remove(key);
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
    public Session getSession() {
        return session;
    }

    public boolean isOnline() {
        return true;
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
        this.recalculatePermissions();
    }
    
    // -- Malleable properties

    public String getDisplayName() {
        return displayName == null ? getName() : displayName;
    }

    public void setDisplayName(String name) {
        displayName = name;
    }

    public Location getCompassTarget() {
        return compassTarget;
    }

    public void setCompassTarget(Location loc) {
        compassTarget = loc;
        session.send(new SpawnPositionMessage(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }

    public boolean isSneaking() {
        return sneaking;
    }

    public void setSneaking(boolean sneak) {
        if (EventFactory.onPlayerToggleSneak(this, sneak).isCancelled()) {
            return;
        }
        this.sneaking = sneak;
        setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, 0, new Byte((byte) (this.sneaking ? 0x02: 0))));
        // FIXME: other bits in the bitmask would be wiped out
        EntityMetadataMessage message = new EntityMetadataMessage(id, metadata);
        for (Player player : world.getPlayers()) {
            if (player != this && canSee((GlowPlayer) player)) {
                ((GlowPlayer) player).session.send(message);
            }
        }
    }

    public boolean isSleepingIgnored() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setSleepingIgnored(boolean isSleeping) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    // -- Actions

    /**
     * Teleport the player.
     * @param location The destination to teleport to.
     * @return Whether the teleport was a success.
     */
    @Override
    public boolean teleport(Location location) {
        if (location.getWorld() != world) {
            world.getEntityManager().deallocate(this);
            
            world = (GlowWorld) location.getWorld();
            world.getEntityManager().allocate(this);
            
            for (GlowChunk.Key key : knownChunks) {
                session.send(new LoadChunkMessage(key.getX(), key.getZ(), false));
            }
            knownChunks.clear();
            
            session.send(new RespawnMessage((byte) world.getEnvironment().getId()));
            
            streamBlocks(); // stream blocks
            
            setCompassTarget(world.getSpawnLocation()); // set our compass target
            this.session.send(new PositionRotationMessage(location.getX(), location.getY() + EYE_HEIGHT + 0.01, location.getZ(), location.getY(), (float) location.getYaw(), (float) location.getPitch(), true));
            this.location = location; // take us to spawn position
            session.send(new StateChangeMessage((byte)(getWorld().hasStorm() ? 1 : 2))); // send the world's weather
            reset();
        } else {
            this.session.send(new PositionRotationMessage(location.getX(), location.getY() + EYE_HEIGHT + 0.01, location.getZ(), location.getY(), (float) location.getYaw(), (float) location.getPitch(), true));
            this.location = location;
            reset();
        }
        
        return true;
    }

    public void sendMessage(String message) {
        for (String line : TextWrapper.wrapText(message)) {
            sendRawMessage(line);
        }
    }

    public void sendRawMessage(String message) {
        session.send(new ChatMessage(message.length() <= 119 ? message : message.substring(0, 119)));
    }

    public void kickPlayer(String message) {
        session.disconnect(message == null ? "" : message);
    }

    public boolean performCommand(String command) {
        return getServer().dispatchCommand(this, command);
    }

    /**
     * Says a message (or runs a command).
     *
     * @param text message to print
     */
    public void chat(String text) {
        if (text.startsWith("/")) {
            try {
                if (EventFactory.onPlayerCommand(this, text).isCancelled()) {
                    return;
                }
                
                if (!performCommand(text.substring(1))) {
                    String firstword = text.substring(1);
                    if (firstword.indexOf(' ') >= 0) {
                        firstword = firstword.substring(0, firstword.indexOf(' '));
                    }
                    
                    sendMessage(ChatColor.GRAY + "Command not found: " + firstword);
                }
            }
            catch (Exception ex) {
                sendMessage(ChatColor.RED + "An internal error occured while executing your command.");
                getServer().getLogger().log(Level.SEVERE, "Exception while executing command: {0}", ex.getMessage());
                ex.printStackTrace();
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void loadData() {
        throw new UnsupportedOperationException("Not supported yet.");
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
        session.send(new BlockChangeMessage(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), material, data));
    }

    public boolean sendChunkChange(Location loc, int sx, int sy, int sz, byte[] data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    // -- Achievements & Statistics [mostly borrowed from CraftBukkit]

    public void awardAchievement(Achievement achievement) {
        sendStatistic(achievement.getId(), 1);
    }

    public void incrementStatistic(Statistic statistic) {
        incrementStatistic(statistic, 1);
    }

    public void incrementStatistic(Statistic statistic, int amount) {
        sendStatistic(statistic.getId(), amount);
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

        sendStatistic(statistic.getId() + mat, amount);
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
    
    // -- Inventory

    public void updateInventory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Get the current item on the player's cursor, for inventory screen purposes.
     * @return The ItemStack the player is holding.
     */
    public ItemStack getItemOnCursor() {
        return itemOnCursor;
    }
    
    /**
     * Set the item on the player's cursor, for inventory screen purposes.
     * @param item The ItemStack to set the cursor to.
     */
    public void setItemOnCursor(ItemStack item) {
        itemOnCursor = item;
        if (item == null) {
            session.send(new SetWindowSlotMessage(-1, -1));
        } else {
            session.send(new SetWindowSlotMessage(-1, -1, item.getTypeId(), item.getAmount(), item.getDurability()));
        }
    }
    
    /**
     * Inform the client that an item has changed.
     * @param inventory The GlowInventory in which a slot has changed.
     * @param slot The slot number which has changed.
     * @param item The ItemStack which the slot has changed to.
     */
    public void onSlotSet(GlowInventory inventory, int slot, ItemStack item) {
        if (inventory == getInventory()) {
            int type = item == null ? -1 : item.getTypeId();
            int data = item == null ? 0 : item.getDurability();
            
            int equipSlot = -1;
            if (slot == getInventory().getHeldItemSlot()) {
                equipSlot = EntityEquipmentMessage.HELD_ITEM;
            } else if (slot == GlowPlayerInventory.HELMET_SLOT) {
                equipSlot = EntityEquipmentMessage.HELMET_SLOT;
            } else if (slot == GlowPlayerInventory.CHESTPLATE_SLOT) {
                equipSlot = EntityEquipmentMessage.CHESTPLATE_SLOT;
            } else if (slot == GlowPlayerInventory.LEGGINGS_SLOT) {
                equipSlot = EntityEquipmentMessage.LEGGINGS_SLOT;
            } else if (slot == GlowPlayerInventory.BOOTS_SLOT) {
                equipSlot = EntityEquipmentMessage.BOOTS_SLOT;
            }
            
            if (equipSlot >= 0) {
                EntityEquipmentMessage message = new EntityEquipmentMessage(getEntityId(), equipSlot, type, data);
                for (GlowPlayer player : new ArrayList<GlowPlayer>(getWorld().getRawPlayers())) {
                    if (player != this && player.canSee(this)) {
                        player.getSession().send(message);
                    }
                }
            }
        }
        
        if (item == null) {
            session.send(new SetWindowSlotMessage(inventory.getId(), inventory.getNetworkSlot(slot)));
        } else {
            session.send(new SetWindowSlotMessage(inventory.getId(), inventory.getNetworkSlot(slot), item.getTypeId(), item.getAmount(), item.getDurability()));
        }
    }
    
    // -- Goofy relative time stuff --
    
    /**
     * Sets the current time on the player's client. When relative is true the player's time
     * will be kept synchronized to its world time with the specified offset.
     *
     * When using non relative time the player's time will stay fixed at the specified time parameter. It's up to
     * the caller to continue updating the player's time. To restore player time to normal use resetPlayerTime().
     *
     * @param time The current player's perceived time or the player's time offset from the server time.
     * @param relative When true the player time is kept relative to its world time.
     */
    public void setPlayerTime(long time, boolean relative) {
        timeOffset = time % 24000;
        timeRelative = relative;
        
        if (timeOffset < 0) timeOffset += 24000;
    }

    /**
     * Returns the player's current timestamp.
     *
     * @return
     */
    public long getPlayerTime() {
        if (timeRelative) {
            // add timeOffset ticks to current time
            return (world.getTime() + timeOffset) % 24000;
        } else {
            // return time offset
            return timeOffset % 24000;
        }
    }

    /**
     * Returns the player's current time offset relative to server time, or the current player's fixed time
     * if the player's time is absolute.
     *
     * @return
     */
    public long getPlayerTimeOffset() {
        return timeOffset;
    }

    /**
     * Returns true if the player's time is relative to the server time, otherwise the player's time is absolute and
     * will not change its current time unless done so with setPlayerTime().
     *
     * @return true if the player's time is relative to the server time.
     */
    public boolean isPlayerTimeRelative() {
        return timeRelative;
    }

    /**
     * Restores the normal condition where the player's time is synchronized with the server time.
     * Equivalent to calling setPlayerTime(0, true).
     */
    public void resetPlayerTime() {
        setPlayerTime(0, true);
    }

    /**
     * Render a map and send it to the player in its entirety. This may be used
     * when streaming the map in the normal manner is not desirbale.
     * 
     * @param map The map to be sent
     */
    public void sendMap(MapView map) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
