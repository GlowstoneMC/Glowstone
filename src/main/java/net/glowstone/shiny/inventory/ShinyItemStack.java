package net.glowstone.shiny.inventory;

import com.google.common.base.Optional;
import org.spongepowered.api.attribute.AttributeModifier;
import org.spongepowered.api.item.ItemDataTransactionResult;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.data.ItemData;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.properties.ItemProperty;
import org.spongepowered.api.service.persistence.InvalidDataException;
import org.spongepowered.api.service.persistence.data.DataContainer;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Implementation of {@link ItemStack}.
 */
public class ShinyItemStack implements ItemStack {

    private final ItemType item;
    private int quantity;
    private short damage;

    private int maxQuantity;

    public ShinyItemStack(ItemType item) {
        this(item, 1, (short) 0);
    }

    public ShinyItemStack(ItemType item, int quantity) {
        this(item, quantity, (short) 0);
    }

    public ShinyItemStack(ItemType item, int quantity, short damage) {
        this.item = item;
        this.quantity = quantity;
        this.damage = damage;
        maxQuantity = item.getMaxStackQuantity();
    }

    @Override
    public ItemType getItem() {
        return item;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(int quantity) throws IllegalArgumentException {
        if (quantity > maxQuantity) {
            throw new IllegalArgumentException("Quantity exceeds max: " + quantity + " > " + maxQuantity);
        }
        this.quantity = quantity;
    }

    @Override
    public int getMaxStackQuantity() {
        return maxQuantity;
    }

    @Override
    public void setMaxStackQuantity(int quantity) {
        maxQuantity = quantity;
    }

    @Override
    public <T extends ItemData<T>> ItemDataTransactionResult setItemData(T itemData) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends ItemData<T>> Optional<T> getOrCreateItemData(Class<T> dataClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<ItemProperty<?, ?>> getProperties() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<ItemData<?>> getItemData() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean validateData(DataContainer container) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setRawData(DataContainer container) throws InvalidDataException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<AttributeModifier> getAttributeModifiers() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> Optional<T> getData(Class<T> dataClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DataContainer toContainer() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
