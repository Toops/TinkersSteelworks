package tsteelworks.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tconstruct.TConstruct;
import tconstruct.common.TContent;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.crafting.Smeltery;
import tconstruct.util.RecipeRemover;
import tsteelworks.lib.crafting.HighOvenSmelting;
import tsteelworks.lib.crafting.TSFluidType;
import cpw.mods.fml.common.registry.GameRegistry;

public class TSRecipes
{
    /*
     * Common Patterns
     */
    static String[] patBlock    =
                                { "###", "###", "###" };
    static String[] patHollow   =
                                { "###", "# #", "###" };
    static String[] patSurround =
                                { "###", "#m#", "###" };

    // ---- CRAFTING TABLE
    // --------------------------------------------------------------------------
    /**
     * Craft monoatomic gold materials (blocks, ingots, nuggets)
     */
    public static void craftTableMonoatomicGold ()
    {
        // Craft block from ingots
        GameRegistry.addRecipe(new ItemStack(TSContent.metalBlock, 1, 0), patBlock, '#',
                               new ItemStack(TSContent.materials, 1, 1));
        // Craft ingots from block
        GameRegistry.addRecipe(new ItemStack(TSContent.materials, 9, 1), "m", 'm', new ItemStack(TSContent.metalBlock,
                                                                                                 1, 0));
        // Craft ingot from nuggets
        GameRegistry.addRecipe(new ItemStack(TSContent.materials, 1, 1), patBlock, '#',
                               new ItemStack(TSContent.materials, 1, 2));
        // Craft nuggets from ingot
        GameRegistry.addRecipe(new ItemStack(TSContent.materials, 9, 2), "m", 'm', new ItemStack(TSContent.materials,
                                                                                                 1, 1));
    }

    /**
     * Craft steel armor
     */
    public static void craftTableSteelArmor ()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.helmetSteel, new Object[]
        { "sss", "s s", 's', TConstructRegistry.getItemStack("ingotSteel") }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.chestplateSteel, new Object[]
        { "s s", "sss", "sss", 's', TConstructRegistry.getItemStack("ingotSteel") }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.leggingsSteel, new Object[]
        { "sss", "s s", "s s", 's', TConstructRegistry.getItemStack("ingotSteel") }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.bootsSteel, new Object[]
        { "s s", "s s", 's', TConstructRegistry.getItemStack("ingotSteel") }));
    }

    /**
     * Craft highoven (high oven) components
     */
    public static void craftTableHighOvenComponents ()
    {
        // High Oven Components Recipes
        final ItemStack brick = new ItemStack(TSContent.materials, 1, 0);
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 0), patHollow, '#', brick); // Controller
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 1), "b b", "b b", "b b", 'b', brick); // Drain
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 2), "bb", "bb", 'b', brick); // Bricks
    }

    /**
     * Change flint & steel recipe to use steel
     */
    public static void changeFlintAndSteelRecipe ()
    {
        // Thanks, TConstruct RecipeRemover!
        RecipeRemover.removeShapedRecipe(new ItemStack(Item.flintAndSteel));
        GameRegistry.addRecipe(new ItemStack(Item.flintAndSteel), "s ", " f", 's',
                               TConstructRegistry.getItemStack("ingotSteel"), 'f', new ItemStack(Item.flint));
    }

    // ---- SMELTERY
    // --------------------------------------------------------------------------
    /**
     * Add smelting of Monoatomic Gold to TConstruct Smeltery
     */
    public static void smelteryMonoatomicGold ()
    {
        Smeltery.addMelting(TSContent.metalBlock, 0, 100, new FluidStack(TSContent.fluids[0],
                                                                         TConstruct.blockLiquidValue));
        Smeltery.addMelting(new ItemStack(TSContent.materials, 1, 1), 100,
                            new FluidStack(TSContent.moltenMonoatomicGoldFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(new ItemStack(TSContent.materials, 1, 2), 100,
                            new FluidStack(TSContent.moltenMonoatomicGoldFluid, TConstruct.nuggetLiquidValue));
    }

    // ---- HIGH OVEN
    // --------------------------------------------------------------------------
    /**
     * Add smelting of steel (ores only!) to High Oven
     * Uses Forge's OreDictionary (mostly)
     */
    public static void highOvenSteel ()
    {
        final FluidType type = FluidType.Steel;
        // Generic Ore Support
        HighOvenSmelting.addDictionaryMelting("oreIron", type, 0, TConstruct.ingotLiquidValue / 2);
        // Dusts Support (Thermal Expansion, etc)
        HighOvenSmelting.addDictionaryMelting("dustIron", type, 0, TConstruct.ingotLiquidValue / 2);
        // Factorization support
        HighOvenSmelting.addDictionaryMelting("crystallineIron", type, 0, TConstruct.ingotLiquidValue / 2);
        // NetherOres support
        HighOvenSmelting.addDictionaryMelting("oreNetherIron", type, 0, TConstruct.ingotLiquidValue / 2);
        // OreBerry Support
        HighOvenSmelting.addMelting(type, TConstructRegistry.getItemStack("oreberryIron"), 0,
                                    TConstruct.nuggetLiquidValue / 2);
    }

    /**
     * Add smelting of monoatomic gold to High Oven
     * Uses Forge's OreDictionary
     */
    public static void highOvenMonoatomicGold ()
    {
        final TSFluidType type = TSFluidType.MonoatomicGold;
        HighOvenSmelting.addDictionaryMelting("oreGold", type, 0, TConstruct.ingotLiquidValue / 2);
    }

    // ---- CASTING TABLE
    // --------------------------------------------------------------------------
    /**
     * Cast scorched brick item from standard brick item
     */
    public static void castTableScorchedBrick ()
    {
        final ItemStack scorchedBrick = new ItemStack(TSContent.materials, 1, 0);
        final FluidStack stoneCastBrick = new FluidStack(TContent.moltenStoneFluid, TConstruct.chunkLiquidValue / 4);
        TConstruct.tableCasting.addCastingRecipe(scorchedBrick, stoneCastBrick, new ItemStack(Item.brick), true, 50);
    }

    /**
     * Cast monoatomic gold to bucket
     */
    public static void castTableMonoatomicGold ()
    {
        final ItemStack monoGoldBucket = new ItemStack(TSContent.buckets, 1, 0);
        final FluidStack monoGoldFillBucket = new FluidStack(TSContent.moltenMonoatomicGoldFluid,
                                                             FluidContainerRegistry.BUCKET_VOLUME);
        final ItemStack emptyBucket = new ItemStack(Item.bucketEmpty);
        final ItemStack monoGoldIngot = new ItemStack(TSContent.materials, 1, 1);
        final FluidStack monoGoldFillIngot = new FluidStack(TSContent.moltenMonoatomicGoldFluid,
                                                            TConstruct.ingotLiquidValue);
        final ItemStack cast = new ItemStack(TContent.metalPattern, 1, 0);

        TConstruct.tableCasting.addCastingRecipe(monoGoldBucket, monoGoldFillBucket, emptyBucket, true, 10);
        TConstruct.tableCasting.addCastingRecipe(monoGoldIngot, monoGoldFillIngot, cast, 80);
    }

    // ---- CASTING BASIN
    // --------------------------------------------------------------------------
    /**
     * Cast scorched brick block from standard brick block and stone fluid
     */
    public static void castBasinScorchedBrickBlock ()
    {
        final ItemStack scorchedBrickBlock = new ItemStack(TSContent.highoven, 1, 2);
        final FluidStack stoneCastBrick = new FluidStack(TContent.moltenStoneFluid, TConstruct.chunkLiquidValue);
        TConstruct.basinCasting.addCastingRecipe(scorchedBrickBlock, stoneCastBrick, new ItemStack(Block.brick), true,
                                                 100);
    }

    /**
     * Cast monoatomic gold block from fluid
     */
    public static void castBasinMonoatomicGold ()
    {
        final ItemStack is = new ItemStack(TSContent.metalBlock, 1, 0);
        final FluidStack fs = new FluidStack(TSContent.moltenMonoatomicGoldFluid, TConstruct.blockLiquidValue);
        TConstruct.basinCasting.addCastingRecipe(is, fs, 100);
        TConstruct.basinCasting.addCastingRecipe(is, fs, null, true, 100); // gold
    }
}
