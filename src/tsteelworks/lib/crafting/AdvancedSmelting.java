package tsteelworks.lib.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.library.crafting.FluidType;
import tsteelworks.TSteelworks;

public class AdvancedSmelting
{
    public static AdvancedSmelting                   instance        = new AdvancedSmelting();
    private final HashMap<List<Integer>, FluidStack> smeltingList    = new HashMap<List<Integer>, FluidStack>();
    private final HashMap<List<Integer>, Integer>    temperatureList = new HashMap<List<Integer>, Integer>();
    private final HashMap<String, List<Integer>>     mixerList       = new HashMap<String, List<Integer>>();
    private final HashMap<FluidType, List>   mixerCombos     = new HashMap<FluidType, List>();
    private final HashMap<List<Integer>, ItemStack>  renderIndex     = new HashMap<List<Integer>, ItemStack>();

    /**
     * Adds mappings between an itemstack and an output liquid.
     * 
     * @param stack
     *            The itemstack to liquify
     * @param temperature
     *            How hot the block should be before liquifying. Max temp in the
     *            Smeltery is 800, other structures may vary
     * @param output
     *            The result of the process in liquid form
     */
    public static void addMelting (ItemStack stack, int temperature, FluidStack output)
    {
        addMelting(stack, stack.itemID, stack.getItemDamage(), temperature, output);
    }

    /**
     * Adds mappings between a block and its liquid.
     * 
     * @param blockID
     *            The ID of the block to liquify and render
     * @param metadata
     *            The metadata of the block to liquify and render
     * @param temperature
     *            How hot the block should be before liquifying. Max temp in the
     *            Smeltery is 800, other structures may vary
     * @param output
     *            The result of the process in liquid form
     */
    public static void addMelting (Block block, int metadata, int temperature, FluidStack output)
    {
        addMelting(new ItemStack(block, 1, metadata), block.blockID, metadata, temperature, output);
    }

    /**
     * Adds mappings between an input and its liquid. Renders with the given
     * input's block ID and metadata.
     * 
     * @param input
     *            The item to liquify
     * @param blockID
     *            The ID of the block to render
     * @param metadata
     *            The metadata of the block to render
     * @param temperature
     *            How hot the block should be before liquifying
     * @param liquid
     *            The result of the process
     */
    public static void addMelting (ItemStack input, int blockID, int metadata, int temperature, FluidStack liquid)
    {
        instance.smeltingList.put(Arrays.asList(input.itemID, input.getItemDamage()), liquid);
        instance.temperatureList.put(Arrays.asList(input.itemID, input.getItemDamage()), temperature);
        instance.renderIndex.put(Arrays.asList(input.itemID, input.getItemDamage()),
                                 new ItemStack(blockID, input.stackSize, metadata));
    }

    /**
     * Adds a mapping between FluidType and ItemStack
     * 
     * @author samtrion
     * @param type
     *            Type of Fluid
     * @param input
     *            The item to liquify
     * @param temperatureDifference
     *            Difference between FluidType BaseTemperature
     * @param fluidAmount
     *            Amount of Fluid
     */
    public static void addMelting (FluidType type, ItemStack input, int temperatureDifference, int fluidAmount)
    {
        int temp = type.baseTemperature + temperatureDifference;
        if (temp <= 20)
        {
            temp = type.baseTemperature;
        }
        addMelting(input, type.renderBlockID, type.renderMeta, type.baseTemperature + temperatureDifference,
                   new FluidStack(type.fluid, fluidAmount));
    }

    /**
     * Adds all Items to the Smeltery based on the oreDictionary Name
     * 
     * @author samtrion
     * @param oreName
     *            oreDictionary name e.g. oreIron
     * @param type
     *            Type of Fluid
     * @param temperatureDifference
     *            Difference between FluidType BaseTemperature
     * @param fluidAmount
     *            Amount of Fluid
     */
    public static void addDictionaryMelting (String oreName, FluidType type, int temperatureDifference, int fluidAmount)
    {
        for (final ItemStack is : OreDictionary.getOres(oreName))
        {
            addMelting(type, is, temperatureDifference, fluidAmount);
        }
    }

    /**
     * Used to get the resulting temperature from a source ItemStack
     * 
     * @param item
     *            The Source ItemStack
     * @return The result temperature
     */
    public static Integer getLiquifyTemperature (ItemStack item)
    {
        if (item == null) return 20;
        final Integer temp = instance.temperatureList.get(Arrays.asList(item.itemID, item.getItemDamage()));
        if (temp == null)
            return 20;
        else
            return temp;
    }

    /**
     * Used to get the resulting temperature from a source Block
     * 
     * @param item
     *            The Source ItemStack
     * @return The result ItemStack
     */
    public static Integer getLiquifyTemperature (int blockID, int metadata)
    {
        return instance.temperatureList.get(Arrays.asList(blockID, metadata));
    }

    /**
     * Used to get the resulting ItemStack from a source ItemStack
     * 
     * @param item
     *            The Source ItemStack
     * @return The result ItemStack
     */
    public static FluidStack getSmelteryResult (ItemStack item)
    {
        if (item == null) return null;
        final FluidStack stack = instance.smeltingList.get(Arrays.asList(item.itemID, item.getItemDamage()));
        if (stack == null) return null;
        return stack.copy();
    }
    
    /**
     * Used to get the resulting ItemStack from a source Block
     * 
     * @param item
     *            The Source ItemStack
     * @return The result ItemStack
     */
    public static FluidStack getSmelteryResult (int blockID, int metadata)
    {
        final FluidStack stack = instance.smeltingList.get(Arrays.asList(blockID, metadata));
        if (stack == null) return null;
        return stack.copy();
    }
    
    /**
     * Adds a mapping between an item and its mix type and consume chance
     * Stack size determines the amount required for consumption
     * 
     * @param item
     * @param type
     * @param chance
     */
    public static void addMixer (ItemStack item, int type, int chance)
    {
        instance.mixerList.put(mixItemKey(item), Arrays.asList(type, item.stackSize, chance));
    }
    
    public static Boolean doesMixerItemMeetRequirements (ItemStack item, int slot)
    {
        return (isMixerItemValidForSlot(item, slot) && item.stackSize >= getMixerConsumeAmount(item));
    }
    
    public static Boolean isMixerItemValidForSlot (ItemStack item, int slot)
    {
        if (!isMixerItemValid(item)) return false;
        if (getMixerType(item) != slot) return false;
        return true;
    }
    
    /**
     * Determine if item is in mixer list
     * 
     * @param item
     * @return
     */
    public static Boolean isMixerItemValid (ItemStack item)
    {
        return instance.mixerList.containsKey(mixItemKey(item));
    }
    
    /**
     * Gets item mixer type (0: oxidizer, 1: reducer, 2:purifier)
     * 
     * @param item
     * @return
     */
    public static Integer getMixerType (ItemStack item)
    {
        final List<Integer> list = instance.mixerList.get(mixItemKey(item));
        return list.get(0);
    }
    
    /**
     * Gets mixer item consumption amount
     * 
     * @param item
     * @return
     */
    public static Integer getMixerConsumeAmount (ItemStack item)
    {
        final List<Integer> list = instance.mixerList.get(mixItemKey(item));
        return list.get(1);
    }
    
    /**
     * Gets mixer item consumption chance
     * 
     * @param item
     * @return
     */
    public static Integer getMixerConsumeChance (ItemStack item)
    {
        final List<Integer> list = instance.mixerList.get(mixItemKey(item));
        return list.get(2);
    }
    
    public static void addMixerCombo (FluidType fluidout, FluidType fluidin, ItemStack item1, ItemStack item2, ItemStack item3)
    {
        instance.mixerCombos.put(fluidout, Arrays.asList(fluidin, mixItemKey(item1), mixItemKey(item2), mixItemKey(item3)));
    }
    
    public static void getMixerCombo (FluidType fluid)
    {
        instance.mixerCombos.get(fluid);
    }
    
    /**
     * Obtains items passed from slots, compares with the combo list, 
     * and if matching returns the fluid type from the combo list.
     * 
     * @param i1 Oxidizer
     * @param i2 Reducer
     * @param i3 Purifier
     * @return FluidType from ComboList.
     */
    public static FluidType validateMixerCombo (FluidType f1, ItemStack i1, ItemStack i2, ItemStack i3)
    {
        final Collection<String> inputs = new ArrayList(Arrays.asList(f1, mixItemKey(i1), mixItemKey(i2), mixItemKey(i3)));
        
        if (inputs.contains(null))
            return null;
        
        if (!doesMixerItemMeetRequirements(i1, 0) || !doesMixerItemMeetRequirements(i2, 1) || !doesMixerItemMeetRequirements(i3, 2))
            return null;
        
        for (Entry<FluidType, List> e : instance.mixerCombos.entrySet()) 
        {
            FluidType key = e.getKey();
            Object value = e.getValue();
            if (value.equals(inputs))
                return key;
        }
        return null;
    }
    
    /**
     * Determines ...pretty much nothing, really. Only keeping just incase.
     * 
     * @param fluid
     * @return
     */
    @Deprecated
    public static Boolean matchMixerLists (FluidType fluid)
    {
        boolean match = false;
        
        final ArrayList<String> copyMix = new ArrayList(instance.mixerList.keySet());
        final ArrayList<String> copyCombo = new ArrayList(instance.mixerCombos.get(fluid));
        
        for (int i = 0; i < copyMix.size(); i++)
        {
            String item = copyMix.get(i);
            Iterator iter = copyMix.iterator();
            while (iter.hasNext())
            {
                if (copyCombo.contains(item))
                {
                    match = true;
                    break;
                } 
                else
                {
                    match = false;
                    break;
                }
            }
            if (!match) break;  
        }
        return match;
    }
    
    /**
     * Internal: Used to create a key for mixer list based on item id and metadata
     * We do this because values cannot be retrieved from the list directly based 
     * on ItemStack obj. Will do this differently in the future.
     * 
     * @param item
     * @return
     */
    private static String mixItemKey (ItemStack item)
    {
        if (item == null)
            return null;
        return (item.itemID + ":" + item.getItemDamage());
    }
    
    /**
     * Used to get the block to render
     * 
     * @param input
     * @return
     */
    public static ItemStack getRenderIndex (ItemStack input)
    {
        return instance.renderIndex.get(Arrays.asList(input.itemID, input.getItemDamage()));
    }

    /**
     * @return The entire smelting list
     */
    public static HashMap<List<Integer>, FluidStack> getSmeltingList ()
    {
        return instance.smeltingList;
    }

    /**
     * @return The entire temperature list
     */
    public static HashMap<List<Integer>, Integer> getTemperatureList ()
    {
        return instance.temperatureList;
    }

    /**
     * @return The entire mixer list
     */
    public static HashMap<String, List<Integer>> getMixersList ()
    {
        return instance.mixerList;
    }
    
    /**
     * @return The entire mixer list
     */
    public static HashMap<FluidType, List> getCombosList ()
    {
        return instance.mixerCombos;
    }
    
    public static HashMap<List<Integer>, ItemStack> getRenderIndex ()
    {
        return instance.renderIndex;
    }
}