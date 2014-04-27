package tsteelworks.lib.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.common.TContent;
import tconstruct.library.crafting.FluidType;
import tsteelworks.TSteelworks;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class AdvancedSmelting
{
    public static AdvancedSmelting instance = new AdvancedSmelting();
    private final HashMap<List<Integer>, FluidStack> meltingList = new HashMap<List<Integer>, FluidStack>();
    private final HashMap<List<Integer>, Integer> temperatureList = new HashMap<List<Integer>, Integer>();
    private final HashMap<String, List<Integer>> mixItemList = new HashMap<String, List<Integer>>();
    private final Multimap<FluidType, List> mixerFluidComboList = ArrayListMultimap.create();
    private final Multimap<ItemStack, List> mixerSolidComboList = ArrayListMultimap.create();
    private final HashMap<List<Integer>, ItemStack> renderIndex = new HashMap<List<Integer>, ItemStack>();
    
    /* ========== Normal Smelting  ========== */
    
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
            addMelting(type, is, temperatureDifference, fluidAmount);
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
            temp = type.baseTemperature;
        addMelting(input, type.renderBlockID, type.renderMeta, type.baseTemperature + temperatureDifference, new FluidStack(type.fluid, fluidAmount));
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
        instance.meltingList.put(Arrays.asList(input.itemID, input.getItemDamage()), liquid);
        instance.temperatureList.put(Arrays.asList(input.itemID, input.getItemDamage()), temperature);
        instance.renderIndex.put(Arrays.asList(input.itemID, input.getItemDamage()), new ItemStack(blockID, input.stackSize, metadata));
    }
    
    /**
     * Used to get the resulting ItemStack from a source Block
     * 
     * @param item
     *            The Source ItemStack
     * @return The result ItemStack
     */
    public static FluidStack getMeltingResult (int blockID, int metadata)
    {
        final FluidStack stack = instance.meltingList.get(Arrays.asList(blockID, metadata));
        if (stack == null)
            return null;
        return stack.copy();
    }
    
    /**
     * Used to get the resulting ItemStack from a source ItemStack
     * 
     * @param item
     *            The Source ItemStack
     * @return The result ItemStack
     */
    public static FluidStack getMeltingResult (ItemStack item)
    {
        if (item == null)
            return null;
        final FluidStack stack = instance.meltingList.get(Arrays.asList(item.itemID, item.getItemDamage()));
        if (stack == null)
            return null;
        return stack.copy();
    }

    /* ========== Temperatures ========== */
    
    /**
     * Used to get the resulting temperature from a source ItemStack
     * 
     * @param item
     *            The Source ItemStack
     * @return The result temperature
     */
    public static Integer getLiquifyTemperature (ItemStack item)
    {
        if (item == null)
            return 20;
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
     * Used to get the block to render
     * 
     * @param input
     * @return
     */
    public static ItemStack getRenderIndex (ItemStack input)
    {
        return instance.renderIndex.get(Arrays.asList(input.itemID, input.getItemDamage()));
    }
    
    /* ========== Combinitorial Smelting ========== */
    
    /**
     * Adds a mapping between an item and its mix type and consume chance
     * Stack size determines the amount required for consumption
     * 
     * @param item
     * @param type
     * @param chance
     */
    public static void registerMixItem (String item, int type, int consume, int chance)
    {
        instance.mixItemList.put(item, Arrays.asList(type, consume, chance));
    }
    
    public static void registerMixComboForFluidOutput (FluidType fluidout, FluidType fluidin, String i1, String i2, String i3)
    {
//        instance.mixerFluidComboList.put(fluidout, Arrays.asList(fluidin, getInternalMixKey(item1), getInternalMixKey(item2), getInternalMixKey(item3)));
        instance.mixerFluidComboList.put(fluidout, Arrays.asList(fluidin, i1, i2, i3));
    }

    public static void registerMixComboForSolidOutput (ItemStack stackout, FluidType fluidin, String i1, String i2, String i3)
    {
        instance.mixerSolidComboList.put(stackout, Arrays.asList(fluidin, i1, i2, i3));
//        instance.mixerSolidComboList.put(stackout, Arrays.asList(fluidin, getInternalMixKey(item1), getInternalMixKey(item2), getInternalMixKey(item3)));
    }
    
    public static void getMixComboForFluidOutput (FluidType fluid)
    {
        instance.mixerFluidComboList.get(fluid);
    }
    
    public static void getMixComboForSolidOutput (ItemStack stack)
    {
        instance.mixerSolidComboList.get(stack);
    }
    
    /**
     * Gets item mixer type (0: oxidizer, 1: reducer, 2:purifier)
     * 
     * @param item
     * @return
     */
    public static Integer getMixItemMixType (ItemStack item)
    {
        if (item == null) return null;
        final List<Integer> list = instance.mixItemList.get(getOreDictionaryKey(item));
        return list.get(0);
    }
    
    /**
     * Gets mixer item consumption amount
     * 
     * @param item
     * @return
     */
    public static Integer getMixItemConsumeAmount (ItemStack item)
    {
        final List<Integer> list = instance.mixItemList.get(getOreDictionaryKey(item));
        return list.get(1);
    }
    
    /**
     * Gets mixer item consumption chance
     * 
     * @param item
     * @return
     */
    public static Integer getMixItemConsumeChance (ItemStack item)
    {
        final List<Integer> list = instance.mixItemList.get(getOreDictionaryKey(item));
        return list.get(2);
    }
    
    /**
     * Determine if item is in mixer list
     * 
     * @param item
     * @return
     */
    public static Boolean isMixItemListed (ItemStack item)
    {
        
        return instance.mixItemList.containsKey(getOreDictionaryKey(item));
    }
    
    public static Boolean isMixItemValidForSlot (ItemStack item, int slot)
    {
        if (item == null) return true;
        return ((isMixItemListed(item)) || (getMixItemMixType(item) == slot));
    }
    
    public static Boolean doesMixItemMeetRequirements (ItemStack item, int slot)
    {
        if (item == null) return true;
        return (isMixItemValidForSlot(item, slot) && (item.stackSize >= getMixItemConsumeAmount(item)));
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
    public static FluidType getMixSmeltingFluidResult (FluidType f1, ItemStack i1, ItemStack i2, ItemStack i3)
    {
        final Collection<String> inputs = new ArrayList(Arrays.asList(f1, getOreDictionaryKey(i1), getOreDictionaryKey(i2), getOreDictionaryKey(i3)));

        if (!doesMixItemMeetRequirements(i1, 0) || !doesMixItemMeetRequirements(i2, 1) || !doesMixItemMeetRequirements(i3, 2))
            return null;

        for (final Entry<FluidType, List> e : instance.mixerFluidComboList.entries())
        {
            final FluidType key = e.getKey();
            final Object value = e.getValue();
            if (value.equals(inputs))
                return key;
        }
        
        return null;
    }

    public static ItemStack getMixSmeltingSolidResult (FluidType f1, ItemStack i1, ItemStack i2, ItemStack i3)
    {
        final Collection<String> inputs = new ArrayList(Arrays.asList(f1, getOreDictionaryKey(i1), getOreDictionaryKey(i2), getOreDictionaryKey(i3)));

        if (!doesMixItemMeetRequirements(i1, 0) || !doesMixItemMeetRequirements(i2, 1) || !doesMixItemMeetRequirements(i3, 2))
            return null;

        for (final Entry<ItemStack, List> e : instance.mixerSolidComboList.entries())
        {
            final ItemStack key = e.getKey();
            final Object value = e.getValue();
            if (value.equals(inputs))
                return key;
        }
        return null;
    }

    /**
     * Internal: Used to create a key for mixer list based on item id and metadata
     * We do this because values cannot be retrieved from the list directly based 
     * on ItemStack obj. Will do this differently in the future.
     * 
     * @param item
     * @return
     */
    private static String getOreDictionaryKey (ItemStack item)
    {
        int oreId = OreDictionary.getOreID(item);
        return (oreId != -1) ?  OreDictionary.getOreName(oreId) : null;
    }

    /* ========== Get Lists ========== */
    
    public static HashMap<List<Integer>, FluidStack> getMeltingList ()
    {
        return instance.meltingList;
    }
    
    public static HashMap<List<Integer>, ItemStack> getRenderIndex ()
    {
        return instance.renderIndex;
    }
    
    public static HashMap<List<Integer>, Integer> getTemperatureList ()
    {
        return instance.temperatureList;
    }
    
    public static HashMap<String, List<Integer>> getMixItemsList ()
    {
        return instance.mixItemList;
    }
    
    public static Multimap<FluidType, List> getCombosList ()
    {
        return instance.mixerFluidComboList;
    }
}