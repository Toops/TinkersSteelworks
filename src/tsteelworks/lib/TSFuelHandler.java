package tsteelworks.lib;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.common.TContent;
import tsteelworks.TSteelworks;
import tsteelworks.common.TSContent;
import cpw.mods.fml.common.IFuelHandler;

public class TSFuelHandler implements IFuelHandler
{
    // Inter-mod generic fuel registry 
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
            if (block == TSContent.charcoalBlock) return 420 * 6;
        }
        // Charcoal
        if ((fuel.itemID == new ItemStack(Item.coal).itemID) && (fuel.getItemDamage() == 1))
            return 420;
        // Coal Coke
        for (final ItemStack fuelCoke : OreDictionary.getOres("fuelCoke"))
            if (fuel.itemID == fuelCoke.itemID)
                return 420 * 2;
        // Block of Coal Coke ? (Only leaving this here in case other mods introduce this oredict)
        for (final ItemStack blockCoke : OreDictionary.getOres("blockCoke"))
            if (fuel.itemID == blockCoke.itemID)
                return 420 * 12;
        // Alumentum
        if (TContent.thaumcraftAvailable)
            if (fuel.itemID == TSContent.thaumcraftAlumentum.itemID)
                return 420 * 4;
        // Coal Coke Block - doesn't work -_-
//        if (TSteelworks.railcraftAvailable && TSContent.railcraftBlockCoalCoke != null)
//            if (fuel.itemID == TSContent.railcraftBlockCoalCoke.itemID)
//                return 420 * 12;
        
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
        if (TContent.thaumcraftAvailable)
            if (fuel.itemID == TSContent.thaumcraftAlumentum.itemID)
                return 3;
//        if (TSteelworks.railcraftAvailable)
//            if (fuel.itemID == TSContent.railcraftBlockCoalCoke.itemID && TSContent.railcraftBlockCoalCoke != null)
//                return 16;
        return 0;
    }
}
