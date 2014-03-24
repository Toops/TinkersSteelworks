package tsteelworks.lib;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import tsteelworks.common.TSContent;
import cpw.mods.fml.common.IFuelHandler;

public class TSFuelHandler implements IFuelHandler
{
    @Override
    public int getBurnTime (ItemStack fuel)
    {
        final int i = fuel.getItem().itemID;
        if ((fuel.getItem() instanceof ItemBlock) && (Block.blocksList[i] != null))
        {
            final Block block = Block.blocksList[i];
            if (block == TSContent.charcoalBlock)
                return 16000;
        }
        return 0;
    }
    
    public int getHighOvenFuelBurnTime (ItemStack fuel)
    {
        final int i = fuel.getItem().itemID;
        fuel.getItem();
        if ((fuel.getItem() instanceof ItemBlock) && (Block.blocksList[i] != null))
        {
            final Block block = Block.blocksList[i];
            // Chacoal Block
            if (block == TSContent.charcoalBlock) return 420 * 4;
        }
        // Charcoal
        if ((fuel.itemID == new ItemStack(Item.coal).itemID) && (fuel.getItemDamage() == 1))
            return 420;
        // Coal Coke
        for (final ItemStack fuelCoke : OreDictionary.getOres("fuelCoke"))
            if (fuel.itemID == fuelCoke.itemID)
                return 420 * 2;
        // Block of Coal Coke ?
        for (final ItemStack blockCoke : OreDictionary.getOres("blockCoke"))
            if (fuel.itemID == blockCoke.itemID)
                return 420 * 8;
        return 0;
    }
    
    public static int getHighOvenFuelHeatRate (ItemStack fuel)
    {
        final int i = fuel.getItem().itemID;
        fuel.getItem();
        if ((fuel.getItem() instanceof ItemBlock) && (Block.blocksList[i] != null))
        {
            final Block block = Block.blocksList[i];
         // Chacoal Block
            if (block == TSContent.charcoalBlock) return 8;
        }
        // Charcoal
        if ((fuel.itemID == new ItemStack(Item.coal).itemID) && (fuel.getItemDamage() == 1))
            return 2;
        // Coal Coke
        for (final ItemStack fuelCoke : OreDictionary.getOres("fuelCoke"))
            if (fuel.itemID == fuelCoke.itemID)
                return 4;
        // Block of Coal Coke ?
        for (final ItemStack blockCoke : OreDictionary.getOres("blockCoke"))
            if (fuel.itemID == blockCoke.itemID)
                return 16;
        return 0;
    }
}
