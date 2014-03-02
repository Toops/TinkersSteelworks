package tsteelworks.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
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
    final static int oreLiquidValue = TConstruct.oreLiquidValue / 2;
    final static int ingotLiquidValue = TConstruct.ingotLiquidValue / 2;
    final static int chunkLiquidValue = TConstruct.chunkLiquidValue / 2;
    final static int nuggetLiquidValue = TConstruct.nuggetLiquidValue / 2;
    
    // Standard Metal
    final static int ironTempDiff      = 604;  // + 600 = 1240
    final static int goldTempDiff      = 663;  // + 400 = 1063
    final static int tinTempDiff       = -163; // + 400 = 232
    final static int copperTempDiff    = 534;  // + 550 = 1084
    final static int aluminumTempDiff  = 310;  // + 350 = 660
    final static int cobaltTempDiff    = 845;  // + 650 = 1495
    final static int arditeTempDiff    = 910;  // + 650 = 1560
    // Thermal Expansion
    final static int nickelTempDiff    = 1053; // + 400 = 1453
    final static int leadTempDiff      = -73;  // + 400 = 327
    final static int silverTempDiff    = 563;  // + 400 = 963
    final static int platinumTempDiff  = 1370; // + 400 = 1770
    // Non-Netal
    final static int obsidianTempDiff  = 330;  // + 750 = 1080
    final static int enderTempDiff     = 0;    // + 500 = 500
    final static int glassTempDiff     = 975;  // + 625 = 1600
    final static int stoneTempDiff     = 400;  // + 800 = 1200
    final static int emeraldTempDiff   = 1025; // + 575 = 1600
    final static int slimeTempDiff     = 0;    // + 250 = 250
    final static int glueTempDiff      = 0;    // + 125 = 125
    // Alloys
    final static int steelTempDiff      = 840;  // + 700 = 1540
    final static int pigIronMTempDiff   = 983;  // + 610 = 1593
    final static int alubrassTempDiff   = 305;  // + 350 = 655
    final static int bronzeTempDiff     = 380;  // + 550 = 930
    final static int alumiteTempDiff    = -129; // + 800 = 671
    final static int manyullynTempDiff  = 534;  // + 750 = 1284
    
    final static int invarTempDiff     = 840; // + 400 = 1540
    final static int electrumTempDiff  = 663; // + 400 = 1063 
    
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
        
        chiseling.addDetailing(TSContent.highoven, 4,  TSContent.highoven, 6,  TContent.chisel);
        chiseling.addDetailing(TSContent.highoven, 6,  TSContent.highoven, 11, TContent.chisel);
        chiseling.addDetailing(TSContent.highoven, 11, TSContent.highoven, 2,  TContent.chisel);
        chiseling.addDetailing(TSContent.highoven, 2,  TSContent.highoven, 8,  TContent.chisel);
        chiseling.addDetailing(TSContent.highoven, 8,  TSContent.highoven, 9,  TContent.chisel);
        chiseling.addDetailing(TSContent.highoven, 9,  TSContent.highoven, 10, TContent.chisel);
        chiseling.addDetailing(TSContent.highoven, 10, TSContent.highoven, 4,  TContent.chisel);
    }
    
    /**
     * Steel armor recipes
     */
    public static void addRecipesSteelArmor ()
    {
        ItemStack ingotSteel = TConstructRegistry.getItemStack("ingotSteel");
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.helmetSteel,     new Object[] { patHead, '#', ingotSteel }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.chestplateSteel, new Object[] { patChest, '#', ingotSteel }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.leggingsSteel,   new Object[] { patLegs, '#', ingotSteel }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.bootsSteel,      new Object[] { patBoots, '#', ingotSteel }));
    }
    
    /**
     * Add recipes related to vanilla-style storage blocks
     */
    public static void addRecipesVanillaStorageBlocks ()
    {
        GameRegistry.addRecipe(new ItemStack(TSContent.charcoalBlock, 1, 0),    patBlock, '#', new ItemStack(Item.coal, 1, 1));
        GameRegistry.addRecipe(new ItemStack(TSContent.dustStorageBlock, 1, 0), patBlock, '#', new ItemStack(Item.gunpowder, 1));
        GameRegistry.addRecipe(new ItemStack(TSContent.dustStorageBlock, 1, 1), patBlock, '#', new ItemStack(Item.sugar, 1));
        GameRegistry.addRecipe(new ItemStack(Item.coal, 9, 1),      "#", '#', new ItemStack(TSContent.charcoalBlock, 1, 0));
        GameRegistry.addRecipe(new ItemStack(Item.gunpowder, 9, 1), "#", '#', new ItemStack(TSContent.dustStorageBlock, 1, 0));
        GameRegistry.addRecipe(new ItemStack(Item.sugar, 9, 1),     "#", '#', new ItemStack(TSContent.dustStorageBlock, 1, 1));
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
    
    /**
     * Add iron smelting recipes to the High Oven
     */
    public static void addSmeltingIron ()
    {
        final FluidType fluidtype = FluidType.Iron;
        // Raw
        AdvancedSmelting.addDictionaryMelting("oreIron",         fluidtype, ironTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("blockIron",       fluidtype, ironTempDiff, blockLiquidValue);
        AdvancedSmelting.addDictionaryMelting("ingotIron",       fluidtype, ironTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("nuggetIron",      fluidtype, ironTempDiff, nuggetLiquidValue);
        AdvancedSmelting.addDictionaryMelting("dustIron",        fluidtype, ironTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("extractIron",     fluidtype, ironTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("flakesIron",      fluidtype, ironTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("crystallineIron", fluidtype, ironTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("oreNetherIron",   fluidtype, ironTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("ironRod",         fluidtype, ironTempDiff, chunkLiquidValue);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(TContent.toolShard, 1, 2), ironTempDiff, chunkLiquidValue);
        // Armor
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.helmetIron, 1, 0),  ironTempDiff, ingotLiquidValue * 5);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.plateIron, 1, 0),   ironTempDiff, ingotLiquidValue * 8);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.legsIron, 1, 0),    ironTempDiff, ingotLiquidValue * 7);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.bootsIron, 1, 0),   ironTempDiff, ingotLiquidValue * 4);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.horseArmorIron, 1), ironTempDiff, ingotLiquidValue * 6);
        // Tools
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.hoeIron, 1, 0),     ironTempDiff, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.swordIron, 1, 0),   ironTempDiff, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.shovelIron, 1, 0),  ironTempDiff, ingotLiquidValue);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.pickaxeIron, 1, 0), ironTempDiff, ingotLiquidValue * 3);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.axeIron, 1, 0),     ironTempDiff, ingotLiquidValue * 3);
        // Blocks
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Block.fenceIron),         ironTempDiff, ingotLiquidValue * 6 / 16);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Block.pressurePlateIron), ironTempDiff, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Block.rail),              ironTempDiff, ingotLiquidValue * 6 / 16);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Block.railDetector),      ironTempDiff, ingotLiquidValue);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Block.railActivator),     ironTempDiff, ingotLiquidValue);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Block.cauldron),          ironTempDiff, ingotLiquidValue * 7);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Block.hopperBlock),       ironTempDiff, ingotLiquidValue * 5);
        if (!ConfigCore.hardcoreAnvil)
        {
            AdvancedSmelting.addMelting(fluidtype, new ItemStack(Block.anvil, 1, 0), ironTempDiff, ingotLiquidValue * 31);
            AdvancedSmelting.addMelting(fluidtype, new ItemStack(Block.anvil, 1, 1), ironTempDiff, ingotLiquidValue * 31);
            AdvancedSmelting.addMelting(fluidtype, new ItemStack(Block.anvil, 1, 2), ironTempDiff, ingotLiquidValue * 31);
        }
    }
    
    /**
     * Add steel smelting recipes to the High Oven
     */
    public static void addSmeltingSteel ()
    {
        final FluidType fluidtype = FluidType.Steel;
        // Raw
        AdvancedSmelting.addDictionaryMelting("blockSteel",  fluidtype, steelTempDiff, blockLiquidValue);
        AdvancedSmelting.addDictionaryMelting("ingotSteel",  fluidtype, steelTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("dustSteel",   fluidtype, steelTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("nuggetSteel", fluidtype, steelTempDiff, nuggetLiquidValue);
        AdvancedSmelting.addDictionaryMelting("steelRod",    fluidtype, steelTempDiff, chunkLiquidValue);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(TContent.toolShard, 1, 16), steelTempDiff, chunkLiquidValue);
        // Armor
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.helmetChain, 1, 0), steelTempDiff, ingotLiquidValue);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.plateChain,  1, 0), steelTempDiff, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.legsChain,   1, 0), steelTempDiff, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.bootsChain,  1, 0), steelTempDiff, ingotLiquidValue);
        // Blocks
        if (ConfigCore.hardcoreAnvil)
        {
            AdvancedSmelting.addMelting(fluidtype, new ItemStack(Block.anvil, 1, 0), steelTempDiff, ingotLiquidValue * 31);
            AdvancedSmelting.addMelting(fluidtype, new ItemStack(Block.anvil, 1, 1), steelTempDiff, ingotLiquidValue * 31);
            AdvancedSmelting.addMelting(fluidtype, new ItemStack(Block.anvil, 1, 2), steelTempDiff, ingotLiquidValue * 31);
        }
        // Items
        if (ConfigCore.hardcoreFlintAndSteel)
        {
            AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.flintAndSteel, 1, 0), steelTempDiff, ingotLiquidValue);
        }
        // Mixer Combo
        AdvancedSmelting.addMixerCombo(fluidtype, new ItemStack(Item.gunpowder, 1, 0), 
                                                       new ItemStack(Item.redstone,  1, 0), 
                                                       new ItemStack(Block.sand,     2, 0));
    }
    
    /**
     * Add pig iron smelting recipes to the High Oven
     */
    public static void addSmeltingPigIron ()
    {
        final FluidType fluidtype = FluidType.PigIron;
        
        AdvancedSmelting.addDictionaryMelting("blockPigIron", fluidtype,  pigIronMTempDiff, blockLiquidValue);
        AdvancedSmelting.addDictionaryMelting("ingotPigIron", fluidtype,  pigIronMTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("nuggetPigIron", fluidtype, pigIronMTempDiff, nuggetLiquidValue);
        
        AdvancedSmelting.addMixerCombo(fluidtype, new ItemStack(Item.sugar,         1, 0), 
                                                  new ItemStack(Item.emerald,       1, 0), 
                                                  new ItemStack(TContent.meatBlock, 1, 0));
    }
    
    public static void addSmeltingGold ()
    {
        final FluidType fluidtype = FluidType.Gold;
        // Raw
        AdvancedSmelting.addDictionaryMelting("oreGold",         fluidtype, goldTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("blockGold",       fluidtype, goldTempDiff, blockLiquidValue);
        AdvancedSmelting.addDictionaryMelting("ingotGold",       fluidtype, goldTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("nuggetGold",      fluidtype, goldTempDiff, nuggetLiquidValue);
        AdvancedSmelting.addDictionaryMelting("dustGold",        fluidtype, goldTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("extractGold",     fluidtype, goldTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("flakesGold",      fluidtype, goldTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("crystallineGold", fluidtype, goldTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("oreNetherGold",   fluidtype, goldTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("goldRod",         fluidtype, goldTempDiff, chunkLiquidValue);
        // Armor
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.helmetGold, 1, 0),  goldTempDiff, ingotLiquidValue * 5);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.plateGold, 1, 0),   goldTempDiff, ingotLiquidValue * 8);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.legsGold, 1, 0),    goldTempDiff, ingotLiquidValue * 7);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.bootsGold, 1, 0),   goldTempDiff, ingotLiquidValue * 4);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.horseArmorGold, 1), goldTempDiff, ingotLiquidValue * 6);
        // Tools
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.hoeGold, 1, 0),     goldTempDiff, oreLiquidValue);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.swordGold, 1, 0),   goldTempDiff, oreLiquidValue);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.shovelGold, 1, 0),  goldTempDiff, ingotLiquidValue);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.pickaxeGold, 1, 0), goldTempDiff, ingotLiquidValue * 3);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Item.axeGold, 1, 0),     goldTempDiff, ingotLiquidValue * 3);
        // Blocks
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Block.pressurePlateGold, 4), goldTempDiff, oreLiquidValue);
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(Block.railPowered),          goldTempDiff, ingotLiquidValue);
        // Items
        AdvancedSmelting.addMelting(fluidtype, new ItemStack(TContent.blankPattern, 4, 2), goldTempDiff, ingotLiquidValue * 2);
    }
    
    public static void addSmeltingCopper ()
    {
        final FluidType fluidtype = FluidType.Copper;
        // Raw
        AdvancedSmelting.addDictionaryMelting("oreCopper",         fluidtype, copperTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("blockCopper",       fluidtype, copperTempDiff, blockLiquidValue);
        AdvancedSmelting.addDictionaryMelting("ingotCopper",       fluidtype, copperTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("nuggetCopper",      fluidtype, copperTempDiff, nuggetLiquidValue);
        AdvancedSmelting.addDictionaryMelting("dustCopper",        fluidtype, copperTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("extractCopper",     fluidtype, copperTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("flakesCopper",      fluidtype, copperTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("crystallineCopper", fluidtype, copperTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("oreNetherCopper",   fluidtype, copperTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("copperRod",         fluidtype, copperTempDiff, chunkLiquidValue);
    }
    
    public static void addSmeltingTin ()
    {
        final FluidType fluidtype = FluidType.Tin;
        // Raw
        AdvancedSmelting.addDictionaryMelting("oreTin",         fluidtype, tinTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("blockTin",       fluidtype, tinTempDiff, blockLiquidValue);
        AdvancedSmelting.addDictionaryMelting("ingotTin",       fluidtype, tinTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("nuggetTin",      fluidtype, tinTempDiff, nuggetLiquidValue);
        AdvancedSmelting.addDictionaryMelting("dustTin",        fluidtype, tinTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("extractTin",     fluidtype, tinTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("flakesTin",      fluidtype, tinTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("crystallineTin", fluidtype, tinTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("oreNetherTin",   fluidtype, tinTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("tinRod",         fluidtype, tinTempDiff, chunkLiquidValue);
    }
    
    public static void addSmeltingAluminum ()
    {
        final FluidType fluidtype = FluidType.Aluminum;
        // Raw
        AdvancedSmelting.addDictionaryMelting("oreAluminum",         fluidtype, aluminumTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("blockAluminum",       fluidtype, aluminumTempDiff, blockLiquidValue);
        AdvancedSmelting.addDictionaryMelting("ingotAluminum",       fluidtype, aluminumTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("nuggetAluminum",      fluidtype, aluminumTempDiff, nuggetLiquidValue);
        AdvancedSmelting.addDictionaryMelting("dustAluminum",        fluidtype, aluminumTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("extractAluminum",     fluidtype, aluminumTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("flakesAluminum",      fluidtype, aluminumTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("crystallineAluminum", fluidtype, aluminumTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("oreNetherAluminum",   fluidtype, aluminumTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("aluminumRod",         fluidtype, aluminumTempDiff, chunkLiquidValue);
    }
    
    public static void addSmeltingCobalt ()
    {
        final FluidType fluidtype = FluidType.Cobalt;
        // Raw
        AdvancedSmelting.addDictionaryMelting("oreCobalt",         fluidtype, cobaltTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("blockCobalt",       fluidtype, cobaltTempDiff, blockLiquidValue);
        AdvancedSmelting.addDictionaryMelting("ingotCobalt",       fluidtype, cobaltTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("nuggetCobalt",      fluidtype, cobaltTempDiff, nuggetLiquidValue);
        AdvancedSmelting.addDictionaryMelting("dustCobalt",        fluidtype, cobaltTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("extractCobalt",     fluidtype, cobaltTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("flakesCobalt",      fluidtype, cobaltTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("cobaltRod",         fluidtype, cobaltTempDiff, chunkLiquidValue);
    }
    
    public static void addSmeltingArdite ()
    {
        final FluidType fluidtype = FluidType.Ardite;
        // Raw
        AdvancedSmelting.addDictionaryMelting("oreArdite",         fluidtype, arditeTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("blockArdite",       fluidtype, arditeTempDiff, blockLiquidValue);
        AdvancedSmelting.addDictionaryMelting("ingotArdite",       fluidtype, arditeTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("nuggetArdite",      fluidtype, arditeTempDiff, nuggetLiquidValue);
        AdvancedSmelting.addDictionaryMelting("dustArdite",        fluidtype, arditeTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("extractArdite",     fluidtype, arditeTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("flakesArdite",      fluidtype, arditeTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("crystallineArdite", fluidtype, arditeTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("arditeRod",         fluidtype, arditeTempDiff, chunkLiquidValue);
    }
    
    public static void addSmeltingNickle ()
    {
        final FluidType fluidtype = FluidType.Nickel;
        // Raw
        AdvancedSmelting.addDictionaryMelting("oreNickel",         fluidtype, nickelTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("blockNickel",       fluidtype, nickelTempDiff, blockLiquidValue);
        AdvancedSmelting.addDictionaryMelting("ingotNickel",       fluidtype, nickelTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("nuggetNickel",      fluidtype, nickelTempDiff, nuggetLiquidValue);
        AdvancedSmelting.addDictionaryMelting("dustNickel",        fluidtype, nickelTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("extractNickel",     fluidtype, nickelTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("flakesNickel",      fluidtype, nickelTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("crystallineNickel", fluidtype, nickelTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("oreNetherNickel",   fluidtype, nickelTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("nickelRod",         fluidtype, nickelTempDiff, chunkLiquidValue);
    }
    
    public static void addSmeltingLead ()
    {
        final FluidType fluidtype = FluidType.Lead;
        // Raw
        AdvancedSmelting.addDictionaryMelting("oreLead",         fluidtype, leadTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("blockLead",       fluidtype, leadTempDiff, blockLiquidValue);
        AdvancedSmelting.addDictionaryMelting("ingotLead",       fluidtype, leadTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("nuggetLead",      fluidtype, leadTempDiff, nuggetLiquidValue);
        AdvancedSmelting.addDictionaryMelting("dustLead",        fluidtype, leadTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("extractLead",     fluidtype, leadTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("flakesLead",      fluidtype, leadTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("crystallineLead", fluidtype, leadTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("oreNetherLead",   fluidtype, leadTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("leadRod",         fluidtype, leadTempDiff, chunkLiquidValue);
    }
    
    public static void addSmeltingSilver ()
    {
        final FluidType fluidtype = FluidType.Silver;
        // Raw
        AdvancedSmelting.addDictionaryMelting("oreSilver",         fluidtype, silverTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("blockSilver",       fluidtype, silverTempDiff, blockLiquidValue);
        AdvancedSmelting.addDictionaryMelting("ingotSilver",       fluidtype, silverTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("nuggetSilver",      fluidtype, silverTempDiff, nuggetLiquidValue);
        AdvancedSmelting.addDictionaryMelting("dustSilver",        fluidtype, silverTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("extractSilver",     fluidtype, silverTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("flakesSilver",      fluidtype, silverTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("crystallineSilver", fluidtype, silverTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("oreNetherSilver",   fluidtype, silverTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("silverRod",         fluidtype, silverTempDiff, chunkLiquidValue);
    }
    
    public static void addSmeltingPlatinum ()
    {
        final FluidType fluidtype = FluidType.Platinum;
        // Raw
        AdvancedSmelting.addDictionaryMelting("orePlatinum",         fluidtype, platinumTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("blockPlatinum",       fluidtype, platinumTempDiff, blockLiquidValue);
        AdvancedSmelting.addDictionaryMelting("ingotPlatinum",       fluidtype, platinumTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("nuggetPlatinum",      fluidtype, platinumTempDiff, nuggetLiquidValue);
        AdvancedSmelting.addDictionaryMelting("dustPlatinum",        fluidtype, platinumTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("extractPlatinum",     fluidtype, platinumTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("flakesPlatinum",      fluidtype, platinumTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("crystallinePlatinum", fluidtype, platinumTempDiff, ingotLiquidValue);
        AdvancedSmelting.addDictionaryMelting("oreNetherPlatinum",   fluidtype, platinumTempDiff, oreLiquidValue);
        AdvancedSmelting.addDictionaryMelting("platinumRod",         fluidtype, platinumTempDiff, chunkLiquidValue);
    }
    
    public static void addSmeltingMisc ()
    {
        //TODO: OreDictify, expand, sort, and move these
        AdvancedSmelting.addMelting(Block.obsidian, 0, obsidianTempDiff, new FluidStack(TContent.moltenObsidianFluid, ingotLiquidValue * 2));
        AdvancedSmelting.addMelting(Block.ice, 0, 10, new FluidStack(FluidRegistry.getFluid("water"), 1000));
        AdvancedSmelting.addMelting(Block.blockSnow, 0, 10, new FluidStack(FluidRegistry.getFluid("water"), 500));
        AdvancedSmelting.addMelting(Block.snow, 0, 10, new FluidStack(FluidRegistry.getFluid("water"), 250));
        AdvancedSmelting.addMelting(Block.sand, 0, glassTempDiff, new FluidStack(TContent.moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME));
        AdvancedSmelting.addMelting(Block.glass, 0, glassTempDiff, new FluidStack(TContent.moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME));
        AdvancedSmelting.addMelting(Block.thinGlass, 0, glassTempDiff, new FluidStack(TContent.moltenGlassFluid, 250));
        AdvancedSmelting.addMelting(Block.stone, 0, stoneTempDiff, new FluidStack(TContent.moltenStoneFluid, ingotLiquidValue / 18));
        AdvancedSmelting.addMelting(Block.cobblestone, 0, stoneTempDiff, new FluidStack(TContent.moltenStoneFluid, ingotLiquidValue / 18));
        AdvancedSmelting.addMelting(Block.blockEmerald, 0, emeraldTempDiff, new FluidStack(TContent.moltenEmeraldFluid, 640 * 9));
        AdvancedSmelting.addMelting(TContent.glueBlock, 0, glueTempDiff, new FluidStack(TContent.glueFluid, blockLiquidValue));
        AdvancedSmelting.addMelting(TContent.craftedSoil, 1, stoneTempDiff, new FluidStack(TContent.moltenStoneFluid, ingotLiquidValue / 4));
        AdvancedSmelting.addMelting(TContent.clearGlass, 0, glassTempDiff, new FluidStack(TContent.moltenGlassFluid, 1000));
        AdvancedSmelting.addMelting(TContent.glassPane, 0, glassTempDiff, new FluidStack(TContent.moltenGlassFluid, 250));
    }
}
