package net.glowstone.io.entity;

import java.util.Arrays;
import java.util.List;
import net.glowstone.entity.objects.GlowArmorStand;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.EulerAngle;

class ArmorStandStore extends LivingEntityStore<GlowArmorStand> {

    public ArmorStandStore() {
        super(GlowArmorStand.class, EntityType.ARMOR_STAND);
    }

    @Override
    public GlowArmorStand createEntity(Location location, CompoundTag compound) {
        return new GlowArmorStand(location);
    }

    @Override
    public void load(GlowArmorStand entity, CompoundTag tag) {
        super.load(entity, tag);
        if (tag.containsKey("Marker")) {
            entity.setMarker(tag.getBool("Marker"));
        }
        if (tag.containsKey("Invisible")) {
            entity.setVisible(!tag.getBool("Invisible"));
        }
        if (tag.containsKey("NoBasePlate")) {
            entity.setBasePlate(!tag.getBool("NoBasePlate"));
        }
        if (tag.containsKey("NoGravity")) {
            entity.setGravity(!tag.getBool("NoGravity"));
        }
        if (tag.containsKey("ShowArms")) {
            entity.setArms(tag.getBool("ShowArms"));
        }
        if (tag.containsKey("Small")) {
            entity.setSmall(tag.getBool("Small"));
        }
        if (tag.isCompound("Pose")) {
            entity.setBodyPose(readSafeAngle(tag.getCompound("Pose"), "Body"));
            entity.setLeftArmPose(readSafeAngle(tag.getCompound("Pose"), "LeftArm"));
            entity.setRightArmPose(readSafeAngle(tag.getCompound("Pose"), "RightArm"));
            entity.setLeftLegPose(readSafeAngle(tag.getCompound("Pose"), "LeftLeg"));
            entity.setRightLegPose(readSafeAngle(tag.getCompound("Pose"), "RightLeg"));
            entity.setHeadPose(readSafeAngle(tag.getCompound("Pose"), "Head"));
        }
    }

    @Override
    public void save(GlowArmorStand entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("Marker", entity.isMarker());
        tag.putBool("Invisible", !entity.isVisible());
        tag.putBool("NoBasePlate", !entity.hasBasePlate());
        tag.putBool("NoGravity", !entity.hasGravity());
        tag.putBool("ShowArms", entity.hasArms());
        tag.putBool("Small", entity.isSmall());
        CompoundTag pose = new CompoundTag();
        pose.putFloatList("Body", toFloatList(entity.getBodyPose()));
        pose.putFloatList("LeftArm", toFloatList(entity.getLeftArmPose()));
        pose.putFloatList("RightArm", toFloatList(entity.getRightArmPose()));
        pose.putFloatList("LeftLeg", toFloatList(entity.getLeftLegPose()));
        pose.putFloatList("RightLeg", toFloatList(entity.getRightLegPose()));
        pose.putFloatList("Head", toFloatList(entity.getHeadPose()));
        tag.putCompound("Pose", pose);
    }

    private static List<Float> toFloatList(EulerAngle angle) {
        return Arrays.asList(
            (float) Math.toDegrees(angle.getX()),
            (float) Math.toDegrees(angle.getY()),
            (float) Math.toDegrees(angle.getZ())
        );
    }

    private EulerAngle readSafeAngle(CompoundTag tag, String key) {
        if (tag.isList(key, TagType.FLOAT)) {
            List<Float> list = tag.getList(key, TagType.FLOAT);
            return new EulerAngle(Math.toRadians(list.get(0)), Math.toRadians(list.get(1)),
                Math.toRadians(list.get(2)));
        } else {
            return EulerAngle.ZERO;
        }
    }
}
