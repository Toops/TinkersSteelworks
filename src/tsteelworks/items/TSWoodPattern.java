/**
 * 
 */
package tsteelworks.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.PatternBuilder.MaterialSet;
import tconstruct.library.util.IPattern;
import tsteelworks.lib.TSteelworksRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author Toops
 *
 */
public class TSWoodPattern extends TSCraftingItem implements IPattern
{
    public TSWoodPattern(int id, String patternType, String folder)
    {
        this(id, patternName, getPatternNames(patternType), folder);
    }
    
    public TSWoodPattern(int id, String[] names, String[] patternTypes, String folder)
    {
        super(id, names, patternTypes, folder);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setContainerItem(this);
        this.setMaxStackSize(1);
        this.setCreativeTab(TSteelworksRegistry.SteelworksCreativeTab);
    }

    protected static String[] getPatternNames (String partType)
    {
        String[] names = new String[patternName.length];
        for (int i = 0; i < patternName.length; i++)
            names[i] = partType + patternName[i];
        return names;
    }

    private static final String[] patternName = new String[] { "ring" };

    @Override
    public void getSubItems (int id, CreativeTabs tab, List list)
    {
        for (int i = 1; i < patternName.length; i++)
        {
            //if (i != 23)
            list.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public ItemStack getContainerItemStack (ItemStack stack)
    {
        if (stack.stackSize <= 0)
            return null;
        return stack;
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid (ItemStack stack)
    {
        return false;
    }

    /* Tags and information about the pattern */
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        float cost = getPatternCost(stack) / 2f;
        if (cost > 0)
        {
            if (cost - (int) cost < 0.1)
                list.add(StatCollector.translateToLocal("pattern1.tooltip") + (int) cost);
            else
                list.add(StatCollector.translateToLocal("pattern2.tooltip") + cost);
        }
    }

    //2 for full material, 1 for half.
    @Override
    public int getPatternCost (ItemStack pattern)
    {
        switch (pattern.getItemDamage())
        {
        case 0:
            return 1;
        default:
            return 0;
        }
    }

    @Override
    public ItemStack getPatternOutput (ItemStack stack, ItemStack input, MaterialSet set)
    {
        return TConstructRegistry.getPartMapping(this.itemID, stack.getItemDamage(), set.materialID);
    }
}