package tsteelworks.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import tsteelworks.TSteelworks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TSManual extends TSCraftingItem
{
    static String[] name = new String[] { "highoven" };
    static String[] textureName = new String[] { "tinkerbook_highoven" };

    public TSManual(int id)
    {
        super(id, name, textureName, "");
        setUnlocalizedName("tsteelworks.manual");
    }

    @SuppressWarnings ({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        switch (stack.getItemDamage())
        {
        case 0:
            list.add("\u00a7o" + StatCollector.translateToLocal("manual.steelworks.tooltip1"));
            break;
        }
    }

    @SuppressWarnings ("static-access")
    @Override
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        player.openGui(TSteelworks.instance, TSteelworks.proxy.manualGuiID, world, 0, 0, 0);
        return stack;
    }
}
