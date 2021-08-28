package net.glowstone.io.entity;

import net.glowstone.entity.objects.GlowArmorStand;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NonNls;

import java.util.Arrays;
import java.util.List;

class ArmorStandStore extends LivingEntityStore<GlowArmorStand> {

    public ArmorStandStore() {
        super(GlowArmorStand.class, EntityType.ARMOR_STAND);
    }

    private static List<Float> toFloatList(EulerAngle angle) {
        return Arrays.asList(
            (float) Math.toDegrees(angle.getX()),
            (float) Math.toDegrees(angle.getY()),
            (float) Math.toDegrees(angle.getZ())
        );
    }

    @Override
    public GlowArmorStand createEntity(Location location, CompoundTag compound) {
        return new GlowArmorStand(location);
    }

    @Override
    public void load(GlowArmorStand entity, CompoundTag tag) {
        super.load(entity, tag);
        tag.readBoolean("Marker", entity::setMarker);
        tag.readBooleanNegated("Invisible", entity::setVisible);
        tag.readBoolean("Marker", entity::setMarker);
        tag.readBooleanNegated("NoBasePlate", entity::setBasePlate);
        tag.readBooleanNegated("NoGravity", entity::setGravity);
        tag.readBoolean("ShowArms", entity::setArms);
        tag.readBoolean("Small", entity::setSmall);
        tag.tryGetCompound("Pose").ifPresent(pose -> {
            entity.setBodyPose(readSafeAngle(pose, "Body"));
            entity.setLeftArmPose(readSafeAngle(pose, "LeftArm"));
            entity.setRightArmPose(readSafeAngle(pose, "RightArm"));
            entity.setLeftLegPose(readSafeAngle(pose, "LeftLeg"));
            entity.setRightLegPose(readSafeAngle(pose, "RightLeg"));
            entity.setHeadPose(readSafeAngle(pose, "Head"));
        });
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

    private EulerAngle readSafeAngle(CompoundTag tag, @NonNls String key) {
        final EulerAngle[] out = {EulerAngle.ZERO};
        tag.readFloatList(key, list -> {
            if (list.size() >= 3) {
                out[0] = new EulerAngle(
                    Math.toRadians(list.get(0)), Math.toRadians(list.get(1)),
                    Math.toRadians(list.get(2)));
            }
        });
        return out[0];
    }
}
