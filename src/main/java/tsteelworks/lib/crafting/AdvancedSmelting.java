package tsteelworks.lib.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.library.crafting.FluidType;
import tsteelworks.util.InventoryHelper;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Toops
 *
 */
public class AdvancedSmelting
{
    public static AdvancedSmelting instance = new AdvancedSmelting();
    private final HashMap<List<Integer>, FluidStack> meltingList = new HashMap<List<Integer>, FluidStack>();
    private final HashMap<List<Integer>, Integer> temperatureList = new HashMap<List<Integer>, Integer>();
    private final HashMap<String, List<Integer>> mixItemList = new HashMap<String, List<Integer>>();
    @SuppressWarnings ("rawtypes")
    private final Multimap<FluidType, List> fluidComboList = ArrayListMultimap.create();
    @SuppressWarnings ("rawtypes")
    private final Multimap<ItemStack, List> itemComboList = ArrayListMultimap.create();
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
     * @param tempDiff
     *            Difference between FluidType BaseTemperature
     * @param fluidAmount
     *            Amount of Fluid
     */
    public static void addDictionaryMelting (String oreName, FluidType type, int tempDiff, int fluidAmount)
    {
        for (final ItemStack is : OreDictionary.getOres(oreName))
            addMelting(type, is, tempDiff, fluidAmount);
    }
    
    /**
     * Adds a mapping between FluidType and ItemStack
     * 
     * @author samtrion
     * @param type
     *            Type of Fluid
     * @param input
     *            The item to liquify
     * @param tempDiff
     *            Difference between FluidType BaseTemperature
     * @param fluidAmount
     *            Amount of Fluid
     */
    public static void addMelting (FluidType type, ItemStack input, int tempDiff, int fluidAmount)
    {
        int temp = type.baseTemperature + tempDiff;
        if (temp <= 20)
            temp = type.baseTemperature;
        addMelting(input, type.renderBlockID, type.renderMeta, type.baseTemperature + tempDiff, new FluidStack(type.fluid, fluidAmount));
    }
    
    /**
     * Adds mappings between an input and its liquid. Renders with the given
     * input's block ID and metadata.
     * 
     * @param itemstack : The item to liquify
     * @param blockID : The ID of the block to render
     * @param metadata : The metadata of the block to render
     * @param temperature : How hot the block should be before liquifying
     * @param liquid : The result of the process
     */
    public static void addMelting (ItemStack itemstack, int blockID, int metadata, int temperature, FluidStack liquid)
    {
        instance.meltingList.put(Arrays.asList(itemstack.itemID, itemstack.getItemDamage()), liquid);
        instance.temperatureList.put(Arrays.asList(itemstack.itemID, itemstack.getItemDamage()), temperature);
        instance.renderIndex.put(Arrays.asList(itemstack.itemID, itemstack.getItemDamage()), new ItemStack(blockID, itemstack.stackSize, metadata));
    }
    
    /**
     * Used to get the resulting ItemStack from a source Block
     * 
     * @param blockID : source block ID
     * @param metadata : source block metadata
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
     * @param itemstack
     *            The Source ItemStack
     * @return The result ItemStack
     */
    public static FluidStack getMeltingResult (ItemStack itemstack)
    {
        if (itemstack == null) return null;
        final FluidStack stack = instance.meltingList.get(Arrays.asList(itemstack.itemID, itemstack.getItemDamage()));
        if (stack == null) return null;
        return stack.copy();
    }

    /* ========== Temperatures ========== */
    
    /**
     * Used to get the resulting temperature from a source ItemStack
     * 
     * @param itemstack
     *            The Source ItemStack
     * @return The result temperature
     */
    public static Integer getLiquifyTemperature (ItemStack itemstack)
    {
        if (itemstack == null)
            return 20;
        final Integer temp = instance.temperatureList.get(Arrays.asList(itemstack.itemID, itemstack.getItemDamage()));
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
     * @param itemstack
     * @return
     */
    public static ItemStack getRenderIndex (ItemStack itemstack)
    {
        return instance.renderIndex.get(Arrays.asList(itemstack.itemID, itemstack.getItemDamage()));
    }
    
    /* ========== Combinitorial Smelting ========== */
    
    /**
     * Adds a mapping between an item and its mix type and consume chance
     * Stack size determines the amount required for consumption
     * 
     * @param oreName
     * @param type
     * @param chance
     */
    public static void registerMixItem (String oreName, int type, int consume, int chance)
    {
        for (final ItemStack is : OreDictionary.getOres(oreName))
            instance.mixItemList.put(InventoryHelper.getOreDictionaryName(is), Arrays.asList(type, consume, chance));
    }
    
    @SuppressWarnings ("unchecked")
    public static void registerMixComboForFluidOutput (FluidType fluidout, FluidType fluidin, String i1, String i2, String i3)
    {
        instance.fluidComboList.put(fluidout, Arrays.asList(fluidin, i1, i2, i3));
    }

    @SuppressWarnings ("unchecked")
    public static void registerMixComboForSolidOutput (ItemStack stackout, FluidType fluidin, String i1, String i2, String i3)
    {
        instance.itemComboList.put(stackout, Arrays.asList(fluidin, i1, i2, i3));
    }
    
    public static void getMixComboForFluidOutput (FluidType fluidtype)
    {
        instance.fluidComboList.get(fluidtype);
    }
    
    public static void getMixComboForSolidOutput (ItemStack itemstack)
    {
        instance.itemComboList.get(itemstack);
    }
    
    /**
     * Determine if item is in mixer list
     * 
     * @param itemstack
     * @return
     */
    public static Boolean isMixItemListed (ItemStack itemstack)
    {
        return instance.mixItemList.containsKey(InventoryHelper.getOreDictionaryName(itemstack));
    }
    
    /**
     * Gets item mixer type (0: oxidizer, 1: reducer, 2:purifier)
     * 
     * @param itemstack
     * @return
     */
    public static Integer getMixItemType (ItemStack itemstack)
    {
        final List<Integer> list = instance.mixItemList.get(InventoryHelper.getOreDictionaryName(itemstack));
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
        final List<Integer> list = instance.mixItemList.get(InventoryHelper.getOreDictionaryName(item));
        return list.get(1);
    }
    
    /**
     * Gets mixer item consumption chance
     * 
     * @param itemstack
     * @return
     */
    public static Integer getMixItemConsumeChance (ItemStack itemstack)
    {
        final List<Integer> list = instance.mixItemList.get(InventoryHelper.getOreDictionaryName(itemstack));
        return list.get(2);
    }
    
    /**
     * Determines whether a given itemstack meets or exceeds the required amount
     * Returns true if itemstack is null, because some recipes do not require an
     * item in a slot.
     * 
     * @param itemstack : ItemStack to check against
     * @return 
     */
    public static Boolean doesMixItemMeetRequirements (ItemStack itemstack)
    {
        if (itemstack == null) return true;
        if (!isMixItemListed(itemstack)) return false;
        return (itemstack.stackSize >= getMixItemConsumeAmount(itemstack));
    }

    /**
     * Obtains items passed from slots, compares with the fluid combo list, 
     * and if matching returns the fluid type from the fluid combo list.
     * 
     * @param i1 Oxidizer
     * @param i2 Reducer
     * @param i3 Purifier
     * @return FluidType from fluidComboList on success, null otherwise
     */
    @SuppressWarnings ({ "rawtypes", "unchecked" })
    public static FluidType getMixFluidSmeltingResult (FluidType f1, ItemStack i1, ItemStack i2, ItemStack i3)
    {
        String ox = InventoryHelper.getOreDictionaryName(i1);
        String re = InventoryHelper.getOreDictionaryName(i2);
        String pu = InventoryHelper.getOreDictionaryName(i3);
        final Collection<String> inputs = new ArrayList(Arrays.asList(f1, ox, re, pu));
        for (final Entry<FluidType, List> e : instance.fluidComboList.entries())
        {
            final FluidType key = e.getKey();
            final List value = e.getValue();
            if (value.equals(inputs))
                if (doesMixItemMeetRequirements(i1) && doesMixItemMeetRequirements(i2) && doesMixItemMeetRequirements(i3))
                    return key;
        }
        return null;
    }

    /**
     * Obtains items passed from slots, compares with the item combo list, 
     * and if matching returns the fluid type from the item combo list.
     * 
     * @param i1 Oxidizer
     * @param i2 Reducer
     * @param i3 Purifier
     * @return ItemStack from itemComboList on success, null otherwise
     */
    @SuppressWarnings ({ "unchecked", "rawtypes" })
    public static ItemStack getMixItemSmeltingResult (FluidType f1, ItemStack i1, ItemStack i2, ItemStack i3)
    {
        String ox = InventoryHelper.getOreDictionaryName(i1);
        String re = InventoryHelper.getOreDictionaryName(i2);
        String pu = InventoryHelper.getOreDictionaryName(i3);
        final Collection<String> inputs = new ArrayList(Arrays.asList(f1, ox, re, pu));
        for (final Entry<ItemStack, List> e : instance.itemComboList.entries())
        {
            final ItemStack key = e.getKey();
            final List value = e.getValue();
            if (value.equals(inputs))
                if (doesMixItemMeetRequirements(i1) && doesMixItemMeetRequirements(i2) && doesMixItemMeetRequirements(i3))
                    return key.copy();
        }
        return null;
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
    
    @SuppressWarnings ("rawtypes")
    public static Multimap<FluidType, List> getFluidCombosList ()
    {
        return instance.fluidComboList;
    }
    
    @SuppressWarnings ("rawtypes")
    public static Multimap<ItemStack, List> getItemCombosList ()
    {
        return instance.itemComboList;
    }
}