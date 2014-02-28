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
import tsteelworks.lib.ConfigCore;
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
    
    final static int blockLiquidValue = TConstruct.blockLiquidValue / 2;
    final static int ingotLiquidValue = TConstruct.ingotLiquidValue / 2;
    final static int chunkLiquidValue = TConstruct.chunkLiquidValue / 2;
    final static int nuggetLiquidValue = TConstruct.nuggetLiquidValue / 2;
    
    final static int ironTempDiff = 604; // + 600 = 1240
    final static int steelTempDiff = 840; // + 700 = 1540
    final static int pigIronMTempDiff = 983; // + 610 = 1593
    
    /**
     * Scorched brick recipes
     */
    public static void addRecipesScorchedBrickMaterial ()
    {
        LiquidCasting basinCasting = TConstructRegistry.instance.getBasinCasting();
        LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        
        final ItemStack itemScorchedBrick = new ItemStack(TSContent.materialsTS, 1, 0);
        final ItemStack blockScorchedBrick = new ItemStack(TSContent.highoven, 1, 2);
        
        final FluidStack fluidStoneMinor = new FluidStack(TContent.moltenStoneFluid, chunkLiquidValue / 4);
        final FluidStack fluidStoneChunk = new FluidStack(TContent.moltenStoneFluid, chunkLiquidValue);
        
        tableCasting.addCastingRecipe(itemScorchedBrick, fluidStoneMinor, new ItemStack(Item.brick), true, 50);
        basinCasting.addCastingRecipe(blockScorchedBrick, fluidStoneChunk, new ItemStack(Block.brick), true, 100);
        

    }
    
    public static void addRecipesMaterialIron ()
    {
        final FluidType fluidTypeIron = FluidType.Iron;
        // TODO: Rebalance melting temperatures
        AdvancedSmelting.addDictionaryMelting("oreIron", fluidTypeIron,         ironTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("blockIron", fluidTypeIron,       ironTempDiff, blockLiquidValue);
        AdvancedSmelting.addDictionaryMelting("ingotIron", fluidTypeIron,       ironTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("nuggetIron", fluidTypeIron,      ironTempDiff, nuggetLiquidValue);
        AdvancedSmelting.addDictionaryMelting("dustIron", fluidTypeIron,        ironTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("extractIron", fluidTypeIron,     ironTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("flakesIron", fluidTypeIron,      ironTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("crystallineIron", fluidTypeIron, ironTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("oreNetherIron", fluidTypeIron,   ironTempDiff, ingotLiquidValue);
        
        // Armor
        AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Item.helmetIron, 1, 0),  ironTempDiff, ingotLiquidValue * 5);
        AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Item.plateIron, 1, 0),   ironTempDiff, ingotLiquidValue * 8);
        AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Item.legsIron, 1, 0),    ironTempDiff, ingotLiquidValue * 7);
        AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Item.bootsIron, 1, 0),   ironTempDiff, ingotLiquidValue * 4);
        AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Item.horseArmorIron, 1), ironTempDiff, ingotLiquidValue * 6);

        //Vanilla tools
        AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Item.hoeIron, 1, 0),     ironTempDiff, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Item.swordIron, 1, 0),   ironTempDiff, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Item.shovelIron, 1, 0),  ironTempDiff, ingotLiquidValue);
        AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Item.pickaxeIron, 1, 0), ironTempDiff, ingotLiquidValue * 3);
        AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Item.axeIron, 1, 0),     ironTempDiff, ingotLiquidValue * 3);
        
        // Vanilla blocks
        AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Block.fenceIron),          ironTempDiff, ingotLiquidValue * 6 / 16);
        AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Block.pressurePlateIron),  ironTempDiff, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Block.rail),               ironTempDiff, ingotLiquidValue * 6 / 16);
        AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Block.railDetector),       ironTempDiff, ingotLiquidValue);
        AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Block.railActivator),      ironTempDiff, ingotLiquidValue);
        AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Block.cauldron),           ironTempDiff, ingotLiquidValue * 7);
        AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Block.hopperBlock),        ironTempDiff, ingotLiquidValue * 5);
        if (!ConfigCore.hardcoreAnvil)
        {
            AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Block.anvil, 1, 0), ironTempDiff, ingotLiquidValue * 31);
            AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Block.anvil, 1, 1), ironTempDiff, ingotLiquidValue * 31);
            AdvancedSmelting.addMelting(fluidTypeIron, new ItemStack(Block.anvil, 1, 2), ironTempDiff, ingotLiquidValue * 31);
        }
    }
    
    /**
     * Steel Recipes
     */
    public static void addRecipesMaterialSteel ()
    {
        final FluidType fluidTypeSteel = FluidType.Steel;
        
        // Raw
        AdvancedSmelting.addDictionaryMelting("blockSteel",  fluidTypeSteel, steelTempDiff, blockLiquidValue);
        AdvancedSmelting.addDictionaryMelting("ingotSteel",  fluidTypeSteel, steelTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("dustSteel",   fluidTypeSteel, steelTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("nuggetSteel", fluidTypeSteel, steelTempDiff, nuggetLiquidValue);
        
        // Armor
        AdvancedSmelting.addMelting(fluidTypeSteel, new ItemStack(Item.helmetChain, 1, 0), steelTempDiff, ingotLiquidValue);
        AdvancedSmelting.addMelting(fluidTypeSteel, new ItemStack(Item.plateChain,  1, 0), steelTempDiff, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(fluidTypeSteel, new ItemStack(Item.legsChain,   1, 0), steelTempDiff, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(fluidTypeSteel, new ItemStack(Item.bootsChain,  1, 0), steelTempDiff, ingotLiquidValue);
        
        if (ConfigCore.hardcoreAnvil)
        {
            AdvancedSmelting.addMelting(fluidTypeSteel, new ItemStack(Block.anvil, 1, 0), steelTempDiff, ingotLiquidValue * 31);
            AdvancedSmelting.addMelting(fluidTypeSteel, new ItemStack(Block.anvil, 1, 1), steelTempDiff, ingotLiquidValue * 31);
            AdvancedSmelting.addMelting(fluidTypeSteel, new ItemStack(Block.anvil, 1, 2), steelTempDiff, ingotLiquidValue * 31);
        }
        if (ConfigCore.hardcoreFlintAndSteel)
        {
            AdvancedSmelting.addMelting(fluidTypeSteel, new ItemStack(Item.flintAndSteel, 1, 0), steelTempDiff, ingotLiquidValue);
        }
        
        AdvancedSmelting.addMixerCombo(fluidTypeSteel, new ItemStack(Item.gunpowder, 1, 0), 
                                                       new ItemStack(Item.redstone,  1, 0), 
                                                       new ItemStack(Block.sand,     2, 0));
    }
    
    public static void addRecipesMaterialPigIron ()
    {
        final FluidType fluidTypePigIron = FluidType.PigIron;
        
        AdvancedSmelting.addDictionaryMelting("blockPigIron", fluidTypePigIron,  pigIronMTempDiff, blockLiquidValue);
        AdvancedSmelting.addDictionaryMelting("ingotPigIron", fluidTypePigIron,  pigIronMTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("nuggetPigIron", fluidTypePigIron, pigIronMTempDiff, nuggetLiquidValue);
        
        AdvancedSmelting.addMixerCombo(fluidTypePigIron, new ItemStack(Item.gunpowder,     1, 0), 
                                                         new ItemStack(Item.emerald,       1, 0), 
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
