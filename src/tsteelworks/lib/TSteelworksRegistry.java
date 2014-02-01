package tsteelworks.lib;

import java.util.HashMap;
import java.util.logging.Logger;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tconstruct.library.util.TabTools;

public class TSteelworksRegistry {
	public static TSteelworksRegistry instance = new TSteelworksRegistry();

	public static Logger logger = Logger.getLogger("TSteel-API");

	/* Creative tabs */
	public static TabTools Steelforge;
	
    /* Items */

    /** A directory of crafting items and tools used by the mod.
     * 
     */
    public static HashMap<String, Item> itemDirectory = new HashMap<String, Item>();

    /** Adds an item to the directory
     * 
     * @param name Associates the name with the stack
     * @param itemstack The stack to add to the directory
     */

    public static void addItemToDirectory (String name, Item itemstack)
    {
        Item add = itemDirectory.get(name);
        if (add != null)
            logger.warning(name + " is already present in the Item directory");

        itemDirectory.put(name, itemstack);
    }

    /** Retrieves an itemstack from the directory
     * 
     * @param name The name of the item to get
     * @return Item associated with the name, or null if not present.
     */

    public static Item getItem (String name)
    {
        Item ret = itemDirectory.get(name);
        if (ret == null)
            logger.warning("Could not find " + name + " in the Item directory");

        return ret;
    }

    /** A directory of ItemStacks. Contains mostly crafting items
     * 
     * Materials:
     * scorchedbrick, ingotMonogold, nuggetMonogold
     */
    static HashMap<String, ItemStack> itemstackDirectory = new HashMap<String, ItemStack>();

    /** Adds an itemstack to the directory
     * 
     * @param name Associates the name with the stack
     * @param itemstack The stack to add to the directory
     */

    public static void addItemStackToDirectory (String name, ItemStack itemstack)
    {
        ItemStack add = itemstackDirectory.get(name);
        if (add != null)
            logger.warning(name + " is already present in the ItemStack directory");

        itemstackDirectory.put(name, itemstack);
    }

    /** Retrieves an itemstack from the directory
     * 
     * @param name The name of the item to get
     * @return Item associated with the name, or null if not present.
     */

    public static ItemStack getItemStack (String name)
    {
        ItemStack ret = itemstackDirectory.get(name);
        if (ret == null)
            logger.warning("Could not find " + name + " in the ItemStack directory");

        return ret;
    }
}
