package net.glowstone.shiny.inventory;

import com.google.common.base.Optional;
import org.spongepowered.api.attribute.AttributeModifier;
import org.spongepowered.api.data.DataManipulator;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.persistence.InvalidDataException;
import org.spongepowered.api.data.DataContainer;

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
    public <T extends DataManipulator<T>> DataTransactionResult setItemData(T itemData) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends DataManipulator<T>> Optional<T> getOrCreateItemData(Class<T> dataClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<Property<?, ?>> getProperties() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<DataManipulator<?>> getItemData() {
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
