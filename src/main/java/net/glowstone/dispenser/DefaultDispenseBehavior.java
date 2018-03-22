package net.glowstone.dispenser;

import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.blocktype.BlockDispenser;
import net.glowstone.entity.objects.GlowItem;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class DefaultDispenseBehavior implements DispenseBehavior {

    public static final DefaultDispenseBehavior INSTANCE = new DefaultDispenseBehavior();

    protected DefaultDispenseBehavior() {}

    @Override
    public ItemStack dispense(GlowBlock block, ItemStack stack) {
        ItemStack result = dispenseStack(block, stack);
        playDispenseSound(block);
        spawnDispenseParticles(block, BlockDispenser.getFacing(block));
        return result;
    }

    protected ItemStack dispenseStack(GlowBlock block, ItemStack stack) {
        BlockFace facing = BlockDispenser.getFacing(block);
        Vector dispensePosition = BlockDispenser.getDispensePosition(block);

        ItemStack items = new ItemStack(stack.getType(), 1);
        stack.setAmount(stack.getAmount() - 1);

        doDispense(block, items, 6, facing, dispensePosition);

        return stack.getAmount() > 0 ? stack : null;
    }

    private void doDispense(GlowBlock block, ItemStack items, int power, BlockFace facing,
        Vector target) {

        double x = target.getX();
        double y = target.getY();
        double z = target.getZ();

        if (facing.getModY() != 0) {
            y -= 0.125;
        } else {
            y -= 0.15625;
        }

        double velocity = ThreadLocalRandom.current().nextDouble() * 0.1 + 0.2;
        double velocityX = facing.getModX() * velocity;
        double velocityY = 0.2;
        double velocityZ = facing.getModZ() * velocity;
        velocityX += ThreadLocalRandom.current().nextGaussian() * 0.0075 * power;
        velocityY += ThreadLocalRandom.current().nextGaussian() * 0.0075 * power;
        velocityZ += ThreadLocalRandom.current().nextGaussian() * 0.0075 * power;

        BlockDispenseEvent dispenseEvent = new BlockDispenseEvent(block, items,
            new Vector(velocityX, velocityY, velocityZ));
        block.getEventFactory().callEvent(dispenseEvent);
        if (!dispenseEvent.isCancelled()) {
            GlowItem item = block.getWorld().dropItem(new Location(block.getWorld(), x, y, z),
                dispenseEvent.getItem());
            item.setVelocity(dispenseEvent.getVelocity());
        }
    }

    private int getParticleMetadataForFace(BlockFace face) {
        return face.getModX() + 1 + (face.getModZ() + 1) * 3;
    }

    protected void playDispenseSound(GlowBlock block) {
        block.getWorld().playEffect(block.getLocation(), Effect.CLICK2, 0);
    }

    protected void spawnDispenseParticles(GlowBlock block, BlockFace facing) {
        block.getWorld()
            .playEffect(block.getLocation(), Effect.SMOKE, getParticleMetadataForFace(facing));
    }
}
