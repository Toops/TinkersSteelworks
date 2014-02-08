package tsteelworks.lib.crafting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.library.crafting.FluidType;

public class HighOvenSmelting
{
    /*
     * Instance variables
     */
    public static HighOvenSmelting                   instance        = new HighOvenSmelting();
    private final HashMap<List<Integer>, FluidStack> smeltingList    = new HashMap<List<Integer>, FluidStack>();
    private final HashMap<List<Integer>, Integer>    temperatureList = new HashMap<List<Integer>, Integer>();
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
    public static void addMelting (TSFluidType type, ItemStack input, int temperatureDifference, int fluidAmount)
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
    public static void
        addDictionaryMelting (String oreName, FluidType type, int temperatureDifference, int fluidAmount)
    {
        for (final ItemStack is : OreDictionary.getOres(oreName))
        {
            addMelting(type, is, temperatureDifference, fluidAmount);
        }
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
    public static void addDictionaryMelting (String oreName, TSFluidType type, int temperatureDifference,
                                             int fluidAmount)
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

    public static HashMap<List<Integer>, ItemStack> getRenderIndex ()
    {
        return instance.renderIndex;
    }
}