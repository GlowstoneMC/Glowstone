package net.glowstone.entity.objects;

import com.flowpowered.networking.Message;
import java.util.Arrays;
import java.util.List;
import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.entity.DestroyEntitiesMessage;
import net.glowstone.net.message.play.entity.EntityEquipmentMessage;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.util.Position;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.EulerAngle;

public class GlowArmorStand extends GlowLivingEntity implements ArmorStand {

    private static final EulerAngle[] defaultPose = new EulerAngle[6];

    static {
        double ten = Math.toRadians(10);
        double fifteen = Math.toRadians(15);
        double one = Math.toRadians(1);
        defaultPose[0] = EulerAngle.ZERO;
        defaultPose[1] = EulerAngle.ZERO;
        defaultPose[2] = new EulerAngle(-ten, 0, -ten);
        defaultPose[3] = new EulerAngle(-fifteen, 0, ten);
        defaultPose[4] = new EulerAngle(-one, 0, -one);
        defaultPose[5] = new EulerAngle(one, 0, one);
    }


    private final ItemStack[] equipment = new ItemStack[5];
    private final boolean[] changedEquip = new boolean[5];
    private final EulerAngle[] pose = new EulerAngle[6];

    private boolean isMarker = false;
    private boolean isVisible = true;
    private boolean isSmall = false;
    private boolean hasBasePlate = true;
    private boolean hasGravity = true;
    private boolean hasArms = false;

    private boolean needsKill = false;

    public GlowArmorStand(Location location) {
        super(location, 2);

        System.arraycopy(defaultPose, 0, pose, 0, 6);
        for (int i = 0; i < 5; i++) {
            changedEquip[i] = false;
        }
    }

    @Override
    public void reset() {
        super.reset();
        for (int i = 0; i < 5; i++) {
            changedEquip[i] = false;
        }
        if (needsKill) needsKill = false;
    }

    @Override
    public void pulse() {
        super.pulse();
        if (isDead()) {
            remove();
            needsKill = true;
        } else if (this.ticksLived % 10 == 0) { //player needs to click fast (2 times) to kill the entity
            setHealth(2);
        }
    }

    @Override
    public void damage(double amount, Entity source, EntityDamageEvent.DamageCause cause) {
        if (getNoDamageTicks() > 0 || health <= 0 || !canTakeDamage(cause)) {
            return;
        }
        if (source instanceof Projectile && !(source instanceof Arrow)) {
            return;
        }
        EntityDamageEvent event;
        if (source == null) {
            event = new EntityDamageEvent(this, cause, amount);
        } else {
            event = new EntityDamageByEntityEvent(source, this, cause, amount);
        }
        EventFactory.callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        boolean drop = false;
        if (source instanceof GlowPlayer || (source instanceof Arrow && ((Projectile) source).getShooter() instanceof GlowPlayer)) {
            GlowPlayer damager = (GlowPlayer) (source instanceof GlowPlayer ? source : ((Arrow) source).getShooter());
            if (damager.getGameMode() == GameMode.ADVENTURE) return;
            else if (damager.getGameMode() == GameMode.CREATIVE) {
                amount = 2; //Instantly kill the entity
            } else {
                amount = 1; //Needs two hits
                drop = true;
            }
        }
        setLastDamage(amount);
        setHealth(health - amount, drop);
    }

    @Override
    public void setHealth(double health) {
        setHealth(health, false);
    }

    private void setHealth(double health, boolean drop) {
        if (health < 0) health = 0;
        if (health > getMaxHealth()) health = getMaxHealth();
        this.health = health;

        metadata.set(MetadataIndex.HEALTH, (float) health);
        for (Objective objective : getServer().getScoreboardManager().getMainScoreboard().getObjectivesByCriteria(Criterias.HEALTH)) {
            objective.getScore(this.getName()).setScore((int) health);
        }

        if (health == 0) {
            kill(drop);
        }
    }

    private void kill(boolean dropArmorStand) {
        active = false;
        ((GlowWorld) location.getWorld()).showParticle(location.clone().add(0, 1.317, 0), Effect.TILE_DUST, Material.WOOD.getId(), 0, 0.125f, 0.494f, 0.125f, 0.1f, 10, 10);
        for (ItemStack stack : equipment) {
            if (stack == null || stack.getType() == Material.AIR) continue;
            getWorld().dropItemNaturally(location, stack);
        }
        if (dropArmorStand) {
            getWorld().dropItemNaturally(location, new ItemStack(Material.ARMOR_STAND));
        }
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage msg) {
        if (player.getGameMode() == GameMode.SPECTATOR || isMarker) return false;
        if (msg.getAction() == InteractEntityMessage.Action.INTERACT_AT.ordinal()) {
            if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
                int slot = getEditSlot(msg.getTargetY());
                PlayerArmorStandManipulateEvent event = new PlayerArmorStandManipulateEvent(player, this, checkNullStack(null), checkNullStack(equipment[slot]), EquipmentSlot.values()[slot]);
                EventFactory.callEvent(event);

                if (event.isCancelled()) {
                    return false;
                }

                if (isEmpty(slot)) {
                    return false;
                }

                ItemStack stack = equipment[slot];
                player.setItemInHand(stack);
                equipment[slot] = null;
                changedEquip[slot] = true;
                return true;
            } else {
                int slot = getEquipType(player.getItemInHand().getType());
                if (slot == 0 && !hasArms) {
                    return false;
                }

                PlayerArmorStandManipulateEvent event = new PlayerArmorStandManipulateEvent(player, this, player.getItemInHand(), checkNullStack(equipment[slot]), EquipmentSlot.values()[slot]);
                EventFactory.callEvent(event);

                if (event.isCancelled()) {
                    return false;
                }

                ItemStack stack = player.getItemInHand();
                ItemStack back;
                if (isEmpty(slot)) {
                    if (stack.getAmount() > 1) {
                        stack.setAmount(stack.getAmount() - 1);
                        back = stack;
                    } else {
                        back = null;
                    }
                } else {
                    if (stack.getAmount() > 1) return false;
                    back = equipment[slot];
                }

                if (back != null) player.setItemInHand(back);
                equipment[slot] = stack;
                changedEquip[slot] = true;
                return true;
            }
        }
        return false;
    }

    private int getEquipType(Material mat) {
        switch (mat) {
            case IRON_HELMET:
            case LEATHER_HELMET:
            case CHAINMAIL_HELMET:
            case GOLD_HELMET:
            case DIAMOND_HELMET:
            case PUMPKIN:
            case SKULL_ITEM:
                return 4;
            case IRON_CHESTPLATE:
            case GOLD_CHESTPLATE:
            case LEATHER_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
                return 3;
            case IRON_LEGGINGS:
            case GOLD_LEGGINGS:
            case LEATHER_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case DIAMOND_LEGGINGS:
                return 2;
            case IRON_BOOTS:
            case GOLD_BOOTS:
            case LEATHER_BOOTS:
            case CHAINMAIL_BOOTS:
            case DIAMOND_BOOTS:
                return 1;
            default:
                return 0;
        }
    }

    private int getEditSlot(float height) {
        int slot = 0;
        if (isSmall) height *= 2;
        if (height >= 0.1 && height < 0.1 + (isSmall ? 0.8 : 0.45) && !isEmpty(1)) {
            slot = 1;
        } else if (height >= 0.9 + (isSmall ? 0.3 : 0) && height < 0.9 + (isSmall ? 1 : 0.7) && !isEmpty(3)) {
            slot = 3;
        } else if (height >= 0.4 && height < 0.4 + (isSmall ? 1 : 0.8) && !isEmpty(2)) {
            slot = 2;
        } else if (height >= 1.6 && !isEmpty(4)) {
            slot = 4;
        }
        return slot;
    }

    private boolean isEmpty(int slot) {
        return equipment[slot] == null || equipment[slot].getType() == Material.AIR;
    }

    private ItemStack checkNullStack(ItemStack stack) {
        if (stack == null) return new ItemStack(Material.AIR);
        else return stack;
    }

    @Override
    public boolean canTakeDamage(EntityDamageEvent.DamageCause cause) {
        switch (cause) {
            case ENTITY_ATTACK:
            case PROJECTILE:
            case FIRE_TICK:
            case BLOCK_EXPLOSION:
            case ENTITY_EXPLOSION:
            case VOID:
            case CUSTOM:
                return true;
        }
        return false;
    }

    @Override
    public List<Message> createSpawnMessage() {
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);

        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);

        return Arrays.asList(
            new SpawnObjectMessage(id, 78, x, y, z, pitch, yaw),
            new EntityMetadataMessage(id, metadata.getEntryList()),
            new EntityEquipmentMessage(id, EntityEquipmentMessage.HELD_ITEM, getItemInHand()),
            new EntityEquipmentMessage(id, EntityEquipmentMessage.BOOTS_SLOT, getBoots()),
            new EntityEquipmentMessage(id, EntityEquipmentMessage.LEGGINGS_SLOT, getLeggings()),
            new EntityEquipmentMessage(id, EntityEquipmentMessage.CHESTPLATE_SLOT, getChestplate()),
            new EntityEquipmentMessage(id, EntityEquipmentMessage.HELMET_SLOT, getHelmet())
        );
    }

    @Override
    public List<Message> createUpdateMessage() {
        List<Message> messages = super.createUpdateMessage();
        for (int i = 0; i < 5; i++) {
            if (changedEquip[i]) {
                messages.add(new EntityEquipmentMessage(id, i, equipment[i]));
            }
        }
        if (needsKill) {
            messages.add(new DestroyEntitiesMessage(Arrays.asList(this.id)));
        }
        return messages;
    }

    @Override
    public EntityType getType() {
        return EntityType.ARMOR_STAND;
    }

    @Override
    public ItemStack getItemInHand() {
        return equipment[0];
    }

    @Override
    public void setItemInHand(ItemStack item) {
        equipment[0] = item;
        changedEquip[0] = true;
    }

    @Override
    public ItemStack getBoots() {
        return equipment[1];
    }

    @Override
    public void setBoots(ItemStack item) {
        equipment[1] = item;
        changedEquip[1] = true;
    }

    @Override
    public ItemStack getLeggings() {
        return equipment[2];
    }

    @Override
    public void setLeggings(ItemStack item) {
        equipment[2] = item;
        changedEquip[2] = true;
    }

    @Override
    public ItemStack getChestplate() {
        return equipment[3];
    }

    @Override
    public void setChestplate(ItemStack item) {
        equipment[3] = item;
        changedEquip[3] = true;
    }

    @Override
    public ItemStack getHelmet() {
        return equipment[4];
    }

    @Override
    public void setHelmet(ItemStack item) {
        equipment[4] = item;
        changedEquip[4] = true;
    }

    @Override
    public EulerAngle getHeadPose() {
        return pose[0];
    }

    @Override
    public void setHeadPose(EulerAngle pose) {
        this.pose[0] = pose;
        metadata.set(MetadataIndex.ARMORSTAND_HEAD_POSITION, pose);
    }

    @Override
    public EulerAngle getBodyPose() {
        return pose[1];
    }

    @Override
    public void setBodyPose(EulerAngle pose) {
        this.pose[1] = pose;
        metadata.set(MetadataIndex.ARMORSTAND_BODY_POSITION, pose);
    }

    @Override
    public EulerAngle getLeftArmPose() {
        return pose[2];
    }

    @Override
    public void setLeftArmPose(EulerAngle pose) {
        this.pose[2] = pose;
        metadata.set(MetadataIndex.ARMORSTAND_LEFT_ARM_POSITION, pose);
    }

    @Override
    public EulerAngle getRightArmPose() {
        return pose[3];
    }

    @Override
    public void setRightArmPose(EulerAngle pose) {
        this.pose[3] = pose;
        metadata.set(MetadataIndex.ARMORSTAND_RIGHT_ARM_POSITION, pose);
    }

    @Override
    public EulerAngle getLeftLegPose() {
        return pose[4];
    }

    @Override
    public void setLeftLegPose(EulerAngle pose) {
        this.pose[4] = pose;
        metadata.set(MetadataIndex.ARMORSTAND_LEFT_LEG_POSITION, pose);
    }

    @Override
    public EulerAngle getRightLegPose() {
        return pose[5];
    }

    @Override
    public void setRightLegPose(EulerAngle pose) {
        this.pose[5] = pose;
        metadata.set(MetadataIndex.ARMORSTAND_RIGHT_LEG_POSITION, pose);
    }

    @Override
    public boolean hasBasePlate() {
        return hasBasePlate;
    }

    @Override
    public void setBasePlate(boolean basePlate) {
        hasBasePlate = basePlate;
        metadata.setBit(MetadataIndex.ARMORSTAND_FLAGS, MetadataIndex.ArmorStandFlags.NO_BASE_PLATE, !basePlate);
    }

    @Override
    public boolean hasGravity() {
        return hasGravity;
    }

    @Override
    public void setGravity(boolean gravity) {
        hasGravity = gravity;
        metadata.setBit(MetadataIndex.ARMORSTAND_FLAGS, MetadataIndex.ArmorStandFlags.HAS_GRAVITY, gravity);
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void setVisible(boolean visible) {
        isVisible = visible;
        metadata.setBit(MetadataIndex.STATUS, MetadataIndex.StatusFlags.INVISIBLE, !visible);
    }

    @Override
    public boolean hasArms() {
        return hasArms;
    }

    @Override
    public void setArms(boolean arms) {
        hasArms = arms;
        metadata.setBit(MetadataIndex.ARMORSTAND_FLAGS, MetadataIndex.ArmorStandFlags.HAS_ARMS, arms);
    }

    @Override
    public boolean isSmall() {
        return isSmall;
    }

    @Override
    public void setSmall(boolean small) {
        isSmall = small;
        metadata.setBit(MetadataIndex.ARMORSTAND_FLAGS, MetadataIndex.ArmorStandFlags.IS_SMALL, small);
    }

    @Override
    public boolean isMarker() {
        return isMarker;
    }

    @Override
    public void setMarker(boolean marker) {
        isMarker = marker;
        metadata.setBit(MetadataIndex.ARMORSTAND_FLAGS, MetadataIndex.ArmorStandFlags.IS_MARKER, marker);
    }
}
