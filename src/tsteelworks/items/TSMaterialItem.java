package tsteelworks.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import tsteelworks.entity.projectile.EntityLimestoneBrick;
import tsteelworks.entity.projectile.EntityScorchedBrick;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TSMaterialItem extends TSCraftingItem
{
    static String[] materialNames = new String[] { "ScorchedBrick", "LimestoneBrick", "LimestoneDust" };

    static String[] craftingTextures = new String[] { "scorchedbrick", "limestonebrick", "limestonedust" };

    private static String[] getTextures ()
    {
        final String[] names = new String[craftingTextures.length];
        for (int i = 0; i < craftingTextures.length; i++)
            if (craftingTextures[i].equals(""))
                names[i] = "";
            else
                names[i] = "material_" + craftingTextures[i];
        return names;
    }

    public TSMaterialItem(int id)
    {
        super(id, materialNames, getTextures(), "materials/");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        switch (stack.getItemDamage())
        {
        case 0:
            list.add(StatCollector.translateToLocal("material.scorchedbrick.tooltip1"));
            break;
        case 1:
            list.add(StatCollector.translateToLocal("material.limestonebrick.tooltip1"));
            break;
        default:
            break;
        }
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick (ItemStack itemstack, World world, EntityPlayer player)
    {
        if (itemstack.getItemDamage() == 0)
        {
            if (!player.capabilities.isCreativeMode)
                --itemstack.stackSize;
            world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / ((itemRand.nextFloat() * 0.4F) + 0.8F));
            if (!world.isRemote)
                world.spawnEntityInWorld(new EntityScorchedBrick(world, player));
            return itemstack;
        }
        else if (itemstack.getItemDamage() == 1)
        {
            if (!player.capabilities.isCreativeMode)
                --itemstack.stackSize;
            world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / ((itemRand.nextFloat() * 0.4F) + 0.8F));
            if (!world.isRemote)
                world.spawnEntityInWorld(new EntityLimestoneBrick(world, player));
            return itemstack;
        }
        else
            return itemstack;
    }
}
