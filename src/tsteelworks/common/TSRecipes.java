package tsteelworks.common;

import java.util.Arrays;
import java.util.List;

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
import tconstruct.library.crafting.Smeltery;
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
    
    final static int blockLiquidValue  = TConstruct.blockLiquidValue / 2;
    final static int oreLiquidValue    = TConstruct.oreLiquidValue / 2;
    final static int ingotLiquidValue  = TConstruct.ingotLiquidValue / 2;
    final static int chunkLiquidValue  = TConstruct.chunkLiquidValue / 2;
    final static int nuggetLiquidValue = TConstruct.nuggetLiquidValue / 2;
    
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
    
    public static void addAlloyRecipesForSmeltery ()
    {
        // Fixed that for ya, mDiyo.
        if (ConfigCore.smelteryObsidianAlloy)
            Smeltery.addAlloyMixing(new FluidStack(TContent.moltenObsidianFluid, 1), 
                                    new FluidStack(FluidRegistry.LAVA, 1),
                                    new FluidStack(FluidRegistry.WATER, 1));  
    }
    
    public static void addOreDictionarySmelteryRecipes ()
    {
        List<FluidType> exceptions = Arrays.asList(new FluidType[] { FluidType.Water, FluidType.Stone, FluidType.Ender, FluidType.Glass, FluidType.Slime, FluidType.Obsidian });
        for (FluidType ft : FluidType.values())
        {
            if (exceptions.contains(ft))
                continue;
            final int tempMod = getFluidTempMod(ft);
            AdvancedSmelting.addDictionaryMelting("nugget" + ft.toString(),      ft, tempMod, nuggetLiquidValue);
            AdvancedSmelting.addDictionaryMelting("ingot" + ft.toString(),       ft, tempMod, ingotLiquidValue);
            AdvancedSmelting.addDictionaryMelting("dust" + ft.toString(),        ft, tempMod, ingotLiquidValue);
            AdvancedSmelting.addDictionaryMelting("crystalline" + ft.toString(), ft, tempMod, ingotLiquidValue);
            AdvancedSmelting.addDictionaryMelting("ore" + ft.toString(),         ft, tempMod, ingotLiquidValue * ConfigCore.ingotsPerOre);
            AdvancedSmelting.addDictionaryMelting("oreNether" + ft.toString(),   ft, tempMod, ingotLiquidValue * ConfigCore.ingotsPerOre * 2);
            AdvancedSmelting.addDictionaryMelting("block" + ft.toString(),       ft, tempMod, blockLiquidValue);
        }
        {
            FluidType ft = FluidType.Obsidian;
            final int tempMod = getFluidTempMod(ft);
            AdvancedSmelting.addDictionaryMelting("nugget" + ft.toString(),      ft, tempMod, nuggetLiquidValue);
            AdvancedSmelting.addDictionaryMelting("ingot" + ft.toString(),       ft, tempMod, ingotLiquidValue);
            AdvancedSmelting.addDictionaryMelting("dust" + ft.toString(),        ft, tempMod, ingotLiquidValue / 4);
            AdvancedSmelting.addDictionaryMelting("crystalline" + ft.toString(), ft, tempMod, ingotLiquidValue);
            AdvancedSmelting.addDictionaryMelting("ore" + ft.toString(),         ft, tempMod, ingotLiquidValue * ConfigCore.ingotsPerOre);
            AdvancedSmelting.addDictionaryMelting("oreNether" + ft.toString(),   ft, tempMod, ingotLiquidValue * ConfigCore.ingotsPerOre * 2);
            AdvancedSmelting.addDictionaryMelting("block" + ft.toString(),       ft, tempMod, blockLiquidValue);
        }
        for (int i = 1; i <= 8; i++)
        {
            AdvancedSmelting.addDictionaryMelting("compressedCobblestone" + i + "x", FluidType.Stone, getFluidTempMod(FluidType.Stone), ingotLiquidValue / 18 * (9 ^ i));
        }
        AdvancedSmelting.addDictionaryMelting("compressedSand1x", FluidType.Glass, getFluidTempMod(FluidType.Glass), FluidContainerRegistry.BUCKET_VOLUME * 9);
    }
    
    /**
     * Add iron smelting recipes to the High Oven
     */
    public static void addSmeltingIron ()
    {
        final FluidType ft = FluidType.Iron;
        final int tempMod = getFluidTempMod(ft);
        AdvancedSmelting.addMelting(ft, new ItemStack(TContent.toolShard, 1, 2), tempMod, chunkLiquidValue);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.helmetIron, 1, 0),  tempMod, ingotLiquidValue * 5);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.plateIron, 1, 0),   tempMod, ingotLiquidValue * 8);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.legsIron, 1, 0),    tempMod, ingotLiquidValue * 7);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.bootsIron, 1, 0),   tempMod, ingotLiquidValue * 4);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.horseArmorIron, 1), tempMod, ingotLiquidValue * 6);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.hoeIron, 1, 0),     tempMod, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.swordIron, 1, 0),   tempMod, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.shovelIron, 1, 0),  tempMod, ingotLiquidValue);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.pickaxeIron, 1, 0), tempMod, ingotLiquidValue * 3);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.axeIron, 1, 0),     tempMod, ingotLiquidValue * 3);
        AdvancedSmelting.addMelting(ft, new ItemStack(Block.fenceIron),         tempMod, ingotLiquidValue * 6 / 16);
        AdvancedSmelting.addMelting(ft, new ItemStack(Block.pressurePlateIron), tempMod, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(ft, new ItemStack(Block.rail),              tempMod, ingotLiquidValue * 6 / 16);
        AdvancedSmelting.addMelting(ft, new ItemStack(Block.railDetector),      tempMod, ingotLiquidValue);
        AdvancedSmelting.addMelting(ft, new ItemStack(Block.railActivator),     tempMod, ingotLiquidValue);
        AdvancedSmelting.addMelting(ft, new ItemStack(Block.cauldron),          tempMod, ingotLiquidValue * 7);
        AdvancedSmelting.addMelting(ft, new ItemStack(Block.hopperBlock),       tempMod, ingotLiquidValue * 5);
        if (!ConfigCore.hardcoreAnvil)
        {
            AdvancedSmelting.addMelting(ft, new ItemStack(Block.anvil, 1, 0), tempMod, ingotLiquidValue * 31);
            AdvancedSmelting.addMelting(ft, new ItemStack(Block.anvil, 1, 1), tempMod, ingotLiquidValue * 31);
            AdvancedSmelting.addMelting(ft, new ItemStack(Block.anvil, 1, 2), tempMod, ingotLiquidValue * 31);
        }
    }
    
    /**
     * Add steel smelting recipes to the High Oven
     */
    public static void addSmeltingSteel ()
    {
        final FluidType ft = FluidType.Steel;
        final int tempMod = getFluidTempMod(ft);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.helmetChain, 1, 0), tempMod, ingotLiquidValue);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.plateChain,  1, 0), tempMod, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.legsChain,   1, 0), tempMod, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.bootsChain,  1, 0), tempMod, ingotLiquidValue);
        if (ConfigCore.hardcoreAnvil)
        {
            AdvancedSmelting.addMelting(ft, new ItemStack(Block.anvil, 1, 0), tempMod, ingotLiquidValue * 31);
            AdvancedSmelting.addMelting(ft, new ItemStack(Block.anvil, 1, 1), tempMod, ingotLiquidValue * 31);
            AdvancedSmelting.addMelting(ft, new ItemStack(Block.anvil, 1, 2), tempMod, ingotLiquidValue * 31);
        }
        if (ConfigCore.hardcoreFlintAndSteel)
        {
            AdvancedSmelting.addMelting(ft, new ItemStack(Item.flintAndSteel, 1, 0), tempMod, ingotLiquidValue);
        }
        AdvancedSmelting.addMixerCombo(ft, FluidType.Iron, new ItemStack(Item.gunpowder, 1, 0), new ItemStack(Item.redstone, 1, 0), new ItemStack(Block.sand, 2, 0));
    }
    
    /**
     * Add pig iron smelting recipes to the High Oven
     */
    public static void addSmeltingPigIron ()
    {
        final FluidType ft = FluidType.PigIron;
        AdvancedSmelting.addMixerCombo(ft, FluidType.Iron, new ItemStack(Item.sugar, 1, 0), new ItemStack(Item.emerald, 1, 0), new ItemStack(TContent.meatBlock, 1, 0));
    }
    
    public static void addSmeltingGold ()
    {
        final FluidType ft = FluidType.Gold;
        final int tempMod = getFluidTempMod(ft);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.helmetGold, 1, 0),  tempMod, ingotLiquidValue * 5);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.plateGold, 1, 0),   tempMod, ingotLiquidValue * 8);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.legsGold, 1, 0),    tempMod, ingotLiquidValue * 7);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.bootsGold, 1, 0),   tempMod, ingotLiquidValue * 4);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.horseArmorGold, 1), tempMod, ingotLiquidValue * 6);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.hoeGold, 1, 0),     tempMod, oreLiquidValue);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.swordGold, 1, 0),   tempMod, oreLiquidValue);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.shovelGold, 1, 0),  tempMod, ingotLiquidValue);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.pickaxeGold, 1, 0), tempMod, ingotLiquidValue * 3);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.axeGold, 1, 0),     tempMod, ingotLiquidValue * 3);
        AdvancedSmelting.addMelting(ft, new ItemStack(Block.pressurePlateGold, 4),  tempMod, oreLiquidValue);
        AdvancedSmelting.addMelting(ft, new ItemStack(Block.railPowered),           tempMod, ingotLiquidValue);
        AdvancedSmelting.addMelting(ft, new ItemStack(TContent.blankPattern, 4, 2), tempMod, ingotLiquidValue * 2);
    }
    
    public static void addSmeltingMisc ()
    {
        //TODO: OreDictify, expand, sort, and move these
        AdvancedSmelting.addMelting(Block.obsidian, 0, getFluidTempMod(FluidType.Obsidian), new FluidStack(TContent.moltenObsidianFluid, ingotLiquidValue * 2));
        AdvancedSmelting.addMelting(Block.ice, 0, getFluidTempMod(FluidType.Water), new FluidStack(FluidRegistry.getFluid("water"), 1000));
        AdvancedSmelting.addMelting(Block.blockSnow, 0, getFluidTempMod(FluidType.Water), new FluidStack(FluidRegistry.getFluid("water"), 500));
        AdvancedSmelting.addMelting(Block.snow, 0, getFluidTempMod(FluidType.Water), new FluidStack(FluidRegistry.getFluid("water"), 250));
        AdvancedSmelting.addMelting(Block.sand, 0, getFluidTempMod(FluidType.Glass), new FluidStack(TContent.moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME));
        AdvancedSmelting.addMelting(Block.glass, 0, getFluidTempMod(FluidType.Glass), new FluidStack(TContent.moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME));
        AdvancedSmelting.addMelting(Block.thinGlass, 0, getFluidTempMod(FluidType.Glass), new FluidStack(TContent.moltenGlassFluid, 250));
        AdvancedSmelting.addMelting(Block.stone, 0, getFluidTempMod(FluidType.Stone), new FluidStack(TContent.moltenStoneFluid, ingotLiquidValue / 18));
        AdvancedSmelting.addMelting(Block.cobblestone, 0, getFluidTempMod(FluidType.Stone), new FluidStack(TContent.moltenStoneFluid, ingotLiquidValue / 18));
        AdvancedSmelting.addMelting(Block.blockEmerald, 0, getFluidTempMod(FluidType.Emerald), new FluidStack(TContent.moltenEmeraldFluid, 320 * 9));
        AdvancedSmelting.addMelting(TContent.glueBlock, 0, getFluidTempMod(FluidType.Glue), new FluidStack(TContent.glueFluid, blockLiquidValue));
        AdvancedSmelting.addMelting(TContent.craftedSoil, 1, getFluidTempMod(FluidType.Stone), new FluidStack(TContent.moltenStoneFluid, ingotLiquidValue / 4));
        AdvancedSmelting.addMelting(TContent.clearGlass, 0, getFluidTempMod(FluidType.Glass), new FluidStack(TContent.moltenGlassFluid, 1000));
        AdvancedSmelting.addMelting(TContent.glassPane, 0, getFluidTempMod(FluidType.Glass), new FluidStack(TContent.moltenGlassFluid, 250));
    }
    
    public static int getFluidTempMod (FluidType type)
    {
        switch (type)
        {
            case Water:         return 10;
            case Iron:          return 913;
            case Gold:          return 663;
            case Tin:           return -163;
            case Copper:        return 534;
            case Aluminum:      return 310;
            case NaturalAluminum: return 310;
            case Cobalt:        return 845;
            case Ardite:        return 910;
            case AluminumBrass: return 305;
            case Alumite:       return -129;
            case Manyullyn:     return 534;
            case Bronze:        return 380;
            case Steel:         return 840;
            case Nickel:        return 1053;
            case Lead:          return -73;
            case Silver:        return 563;
            case Platinum:      return 1370;
            case Invar:         return 840;
            case Electrum:      return 663;
            case Obsidian:      return 330;
            case Ender:         return 0;
            case Glass:         return 975;
            case Stone:         return 600;
            case Emerald:       return 1025;
            case Slime:         return 0;
            case PigIron:       return 983;
            case Glue:          return 0;
            default:            return 0;
        }
    }
}
