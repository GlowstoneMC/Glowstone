package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.EntityEquipmentPacket;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public final class EntityEquipmentCodec implements Codec<EntityEquipmentPacket> {
    @Override
    public EntityEquipmentPacket decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int slot = ByteBufUtils.readVarInt(buf);
        ItemStack stack = GlowBufUtils.readSlot(buf);
        return new EntityEquipmentPacket(id, slot, stack);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityEquipmentPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        ByteBufUtils.writeVarInt(buf, message.getSlot());
        GlowBufUtils.writeSlot(buf, message.getStack());
        return buf;
    }
}
