package tsteelworks.common;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tconstruct.TConstruct;
import tconstruct.common.TContent;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.Smeltery;
import tconstruct.util.RecipeRemover;
import tsteelworks.lib.ConfigCore;
import tsteelworks.lib.crafting.HighOvenSmelting;
import tsteelworks.lib.crafting.TSFluidType;
import cpw.mods.fml.common.registry.GameRegistry;

public class TSRecipes
{
    /*
     * Common Patterns
     */
    static String[] patBlock    = { "###", "###", "###" };
    static String[] patHollow   = { "###", "# #", "###" };
    static String[] patSurround = { "###", "#m#", "###" };
    static String[] patHead     = { "###", "# #" };
    static String[] patChest    = { "# #", "###", "###" };
    static String[] patLegs     = { "###", "# #", "# #" };
    static String[] patBoots     = { "# #", "# #" };
    
    /**
     * Scorched brick recipes
     */
    public static void addRecipesScorchedBrickMaterial ()
    {
        LiquidCasting basinCasting = TConstructRegistry.instance.getBasinCasting();
        LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        
        final ItemStack itemScorchedBrick = new ItemStack(TSContent.materialsTS, 1, 0);
        final ItemStack blockScorchedBrick = new ItemStack(TSContent.highoven, 1, 2);
        
        final FluidStack fluidStoneMinor = new FluidStack(TContent.moltenStoneFluid, TConstruct.chunkLiquidValue / 4);
        final FluidStack fluidStoneChunk = new FluidStack(TContent.moltenStoneFluid, TConstruct.chunkLiquidValue);
        
        tableCasting.addCastingRecipe(itemScorchedBrick, fluidStoneMinor, new ItemStack(Item.brick), true, 50);
        basinCasting.addCastingRecipe(blockScorchedBrick, fluidStoneChunk, new ItemStack(Block.brick), true, 100);
    }
    
    /**
     * Steel Recipes
     */
    public static void addRecipesSteelMaterial ()
    {
        final FluidType fluidTypeSteel = FluidType.Steel;
        // TODO: Figure out why TConstruct.xxxLiquidValue is doubling?
        // Add smelting of steel (ores only!) to High Oven. Uses Forge's OreDictionary (mostly)
        // Generic Ore Support
        HighOvenSmelting.addDictionaryMelting("oreIron", fluidTypeSteel, 0, TConstruct.ingotLiquidValue / 2);
        // Dusts Support (Thermal Expansion, etc)
        HighOvenSmelting.addDictionaryMelting("dustIron", fluidTypeSteel, 0, TConstruct.ingotLiquidValue / 2);
        // Factorization support
        HighOvenSmelting.addDictionaryMelting("crystallineIron", fluidTypeSteel, 0, TConstruct.ingotLiquidValue / 2);
        // NetherOres support
        HighOvenSmelting.addDictionaryMelting("oreNetherIron", fluidTypeSteel, 0, TConstruct.ingotLiquidValue / 2);
        // OreBerry Support
        HighOvenSmelting.addMelting(fluidTypeSteel, TConstructRegistry.getItemStack("oreberryIron"), 0,
                                    TConstruct.nuggetLiquidValue / 2);
    }
    
    /**
     * Monoatomic gold recipes
     */
    public static void addRecipesMonoatomicGoldMaterial ()
    {
        LiquidCasting basinCasting = TConstructRegistry.instance.getBasinCasting();
        LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        
        final TSFluidType fluidTypeMonoGold = TSFluidType.MonoatomicGold;
        
        final ItemStack cast = new ItemStack(TContent.metalPattern, 1, 0);
        final ItemStack bucketEmpty = new ItemStack(Item.bucketEmpty);
        final ItemStack bucketMonoGold = new ItemStack(TSContent.buckets, 1, 0);
        
        final ItemStack nuggetMonoGold = new ItemStack(TSContent.materialsTS, 1, 2);
        final ItemStack ingotMonoGold = new ItemStack(TSContent.materialsTS, 1, 1);
        final ItemStack blockMonoGold = new ItemStack(TSContent.metalBlockTS, 1, 0);
                
        final FluidStack fluidMonoGoldNugget = new FluidStack(TSContent.moltenMonoatomicGoldFluid, TConstruct.nuggetLiquidValue / 2);
        final FluidStack fluidMonoGoldIngot = new FluidStack(TSContent.moltenMonoatomicGoldFluid, TConstruct.ingotLiquidValue / 2);
        final FluidStack fluidMonoGoldBlock = new FluidStack(TSContent.moltenMonoatomicGoldFluid, TConstruct.blockLiquidValue / 2);
        final FluidStack fluidMonoGoldBucket = new FluidStack(TSContent.moltenMonoatomicGoldFluid, FluidContainerRegistry.BUCKET_VOLUME);
        
        GameRegistry.addRecipe(blockMonoGold, patBlock, '#', ingotMonoGold);
        GameRegistry.addRecipe(new ItemStack(TSContent.materialsTS, 9, 1), "b", 'b', blockMonoGold);
        GameRegistry.addRecipe(ingotMonoGold, patBlock, '#', nuggetMonoGold);
        GameRegistry.addRecipe(new ItemStack(TSContent.materialsTS, 9, 2), "i", 'i', ingotMonoGold);
        
        tableCasting.addCastingRecipe(bucketMonoGold, fluidMonoGoldBucket, bucketEmpty, true, 10);
        tableCasting.addCastingRecipe(ingotMonoGold, fluidMonoGoldIngot, cast, 80);      
        basinCasting.addCastingRecipe(blockMonoGold, fluidMonoGoldBlock, null, true, 100);
        
        HighOvenSmelting.addDictionaryMelting("oreGold", fluidTypeMonoGold, -500, TConstruct.ingotLiquidValue / 2);
        Smeltery.addMelting(nuggetMonoGold, 100, fluidMonoGoldNugget);
        Smeltery.addMelting(ingotMonoGold, 100, fluidMonoGoldIngot);
        Smeltery.addMelting(blockMonoGold, 100, fluidMonoGoldBlock);
    }
    
    /**
     * High oven component recipes
     */
    public static void addRecipesHighOvenComponents ()
    {
        final ItemStack itemScorchedBrick = new ItemStack(TSContent.materialsTS, 1, 0);
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 0), patHollow, '#', itemScorchedBrick);
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 1), "b b", "b b", "b b", 'b', itemScorchedBrick);
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 2), "bb", "bb", 'b', itemScorchedBrick);
    }
    
    /**
     * Steel armor recipes
     */
    public static void addRecipesSteelArmor ()
    {
        if (ConfigCore.enableSteelArmor) 
        {
            ItemStack ingotSteel = TConstructRegistry.getItemStack("ingotSteel");
            GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.helmetSteel, new Object[] { patHead, '#', ingotSteel }));
            GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.chestplateSteel, new Object[] { patChest, '#', ingotSteel }));
            GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.leggingsSteel, new Object[] { patLegs, '#', ingotSteel }));
            GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.bootsSteel, new Object[] { patBoots, '#', ingotSteel }));
        }
    }
    
    /**
     * Change flint & steel recipe to use steel
     */
    public static void changeRecipeFlintAndSteel ()
    {
        // Thanks, TConstruct RecipeRemover!
        RecipeRemover.removeShapedRecipe(new ItemStack(Item.flintAndSteel));
        GameRegistry.addRecipe(new ItemStack(Item.flintAndSteel), "s ", " f", 's',
                               TConstructRegistry.getItemStack("ingotSteel"), 'f', new ItemStack(Item.flint));
    }
}
