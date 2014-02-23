package tsteelworks.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tconstruct.TConstruct;
import tconstruct.common.TContent;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.Detailing;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.util.RecipeRemover;
import tsteelworks.lib.crafting.AdvancedSmelting;
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
    
    public static void addRecipesMaterialIron ()
    {
        final FluidType fluidTypeIron = FluidType.Iron;
        
        AdvancedSmelting.addDictionaryMelting("oreIron", fluidTypeIron, -400, TConstruct.ingotLiquidValue / 2);
        // Dusts Support (Thermal Expansion, etc)
        AdvancedSmelting.addDictionaryMelting("dustIron", fluidTypeIron, 0, TConstruct.ingotLiquidValue / 2);
        // Factorization support
        AdvancedSmelting.addDictionaryMelting("crystallineIron", fluidTypeIron, 0, TConstruct.ingotLiquidValue / 2);
        // NetherOres support
        AdvancedSmelting.addDictionaryMelting("oreNetherIron", fluidTypeIron, 0, TConstruct.ingotLiquidValue / 2);
        // OreBerry Support
        AdvancedSmelting.addMelting(fluidTypeIron, TConstructRegistry.getItemStack("oreberryIron"), 0,
                                    TConstruct.nuggetLiquidValue / 2);
    }
    
    /**
     * Steel Recipes
     */
    public static void addRecipesMaterialSteel ()
    {
        final FluidType fluidTypeSteel = FluidType.Steel;
        AdvancedSmelting.addMixerCombo(fluidTypeSteel, new ItemStack(Item.gunpowder, 1, 0), 
                                                       new ItemStack(Item.redstone,  1, 0), 
                                                       new ItemStack(Block.sand,     2, 0));
    }
    
    public static void addRecipesMaterialPigIron ()
    {
        final FluidType fluidTypePigIron = FluidType.PigIron;
        AdvancedSmelting.addMixerCombo(fluidTypePigIron, new ItemStack(Item.gunpowder, 1, 0), 
                                                         new ItemStack(Item.emerald,   1, 0), 
                                                         new ItemStack(TContent.meatBlock, 1, 0));
    }
    
    /**
     * High oven component recipes
     */
    public static void addRecipesHighOvenComponents ()
    {
        Detailing chiseling = TConstructRegistry.getChiselDetailing();
        final ItemStack itemScorchedBrick = new ItemStack(TSContent.materialsTS, 1, 0);
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 0), patHollow, '#', itemScorchedBrick);
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 1), "b b", "b b", "b b", 'b', itemScorchedBrick);
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 2), "bb", "bb", 'b', itemScorchedBrick);
        
        chiseling.addDetailing(TSContent.highoven, 4, TSContent.highoven, 6, TContent.chisel);
        chiseling.addDetailing(TSContent.highoven, 6, TSContent.highoven, 11, TContent.chisel);
        chiseling.addDetailing(TSContent.highoven, 11, TSContent.highoven, 2, TContent.chisel);
        chiseling.addDetailing(TSContent.highoven, 2, TSContent.highoven, 8, TContent.chisel);
        chiseling.addDetailing(TSContent.highoven, 8, TSContent.highoven, 9, TContent.chisel);
        chiseling.addDetailing(TSContent.highoven, 9, TSContent.highoven, 10, TContent.chisel);
        chiseling.addDetailing(TSContent.highoven, 10, TSContent.highoven, 4, TContent.chisel);
    }
    
    /**
     * Steel armor recipes
     */
    public static void addRecipesSteelArmor ()
    {
        ItemStack ingotSteel = TConstructRegistry.getItemStack("ingotSteel");
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.helmetSteel, new Object[] { patHead, '#', ingotSteel }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.chestplateSteel, new Object[] { patChest, '#', ingotSteel }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.leggingsSteel, new Object[] { patLegs, '#', ingotSteel }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.bootsSteel, new Object[] { patBoots, '#', ingotSteel }));
    }
    
    /**
     * Change flint & steel recipe to use steel
     */
    public static void changeRecipeFlintAndSteel ()
    {
        RecipeRemover.removeShapedRecipe(new ItemStack(Item.flintAndSteel));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.flintAndSteel), "s ", " f", 
                                's', "ingotSteel", 
                                'f', new ItemStack(Item.flint)));
    }
    
    public static void changeRecipeAnvil ()
    {
        RecipeRemover.removeShapedRecipe(new ItemStack(Block.anvil));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Block.anvil), "bbb", " i ", "iii", 
                                'i', "ingotSteel", 
                                'b', "blockSteel"));
    }
    
    public static void changeRecipePiston ()
    {
        final ItemStack rod = new ItemStack(TContent.toughRod, 1, 2);
        RecipeRemover.removeAnyRecipe(new ItemStack(Block.pistonBase));
        // TODO: Figure out wtf is wrong with this.
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Block.pistonBase), "WWW", "CTC", "CRC", 
            'C', Block.cobblestone, //"blockCobble", 
            'T', rod, 
            'R', Item.redstone, //"dustRedstone", 
            'W', "plankWood"));
    }
}
