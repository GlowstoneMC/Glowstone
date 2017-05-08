import net.glowstone.block.itemtype.ItemWearable;
import net.glowstone.block.itemtype.ItemWearablePosition;
import org.bukkit.Material;

public class ItemSkullItem extends ItemWearable {
     public ItemSkullItem() {
        super(0, ItemWearablePosition.HEAD);
        setPlaceAs(Material.SKULL);
    }
}
