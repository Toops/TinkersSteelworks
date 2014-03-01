package tsteelworks.lib.crafting;

import java.util.LinkedList;

import tconstruct.library.crafting.FluidType;
import net.minecraft.item.Item;
import net.minecraftforge.fluids.FluidStack;
//TODO: Actually use this class
public class MixingRecipes
{    
    protected LinkedList<Item> oreList = new LinkedList<Item>();
    protected LinkedList<Item> oxidizerList = new LinkedList<Item>();
    protected LinkedList<Item> reducerList = new LinkedList<Item>();
    protected LinkedList<Item> purifierList = new LinkedList<Item>();
    protected FluidType result;
    
    public MixingRecipes(Item ore, Item oxidizer, Item reducer, Item purifier, FluidType fluid)
    {
        this.oreList.addLast(ore);
        this.oxidizerList.add(oxidizer);
        this.reducerList.add(reducer);
        this.purifierList.addLast(purifier);
        result = fluid;
    }
    
    public void addOreItem (Item ore)
    {
        this.oreList.add(ore);
    }

    public void addOxidizerItem (Item oxidizer)
    {
        this.oxidizerList.add(oxidizer);
    }

    public void addReducerItem (Item reducer)
    {
        this.reducerList.add(reducer);
    }

    public void addPurifierItem (Item purifier)
    {
        this.purifierList.add(purifier);
    }

    public boolean validOre (Item ore)
    {
        for (Item material : oreList)
        {
            if (material == ore)
                return true;
        }
        return false;
    }
    
    public boolean validOxidizer (Item oxidizer)
    {
        for (Item material : oxidizerList)
        {
            if (material == oxidizer)
                return true;
        }
        return false;
    }
    
    public boolean validReducer (Item reducer)
    {
        for (Item material : reducerList)
        {
            if (material == reducer)
                return true;
        }
        return false;
    }
    
    public boolean validPurifier (Item purifier)
    {
        for (Item material : purifierList)
        {
            if (material == purifier)
                return true;
        }
        return false;
    }


    public FluidType getType ()
    {
        return result;
    }
}
