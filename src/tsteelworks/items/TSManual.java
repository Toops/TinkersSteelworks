package tsteelworks.items;

import tsteelworks.TSteelworks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;


public class TSManual extends TSCraftingItem
{
    static String[] name = new String[] { "highoven" };
    static String[] textureName = new String[] { "tinkerbook_highoven" };

    public TSManual(int id)
    {
        super(id, name, textureName, "");
        setUnlocalizedName("tsteelworks.manual");
    }
    
    @Override
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        player.openGui(TSteelworks.instance, TSteelworks.proxy.manualGuiID, world, 0, 0, 0);
        return stack;
    }
    
}
