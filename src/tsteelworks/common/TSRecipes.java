package tsteelworks.common;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
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
    static String[] patBlock = { "###", "###", "###" };
    static String[] patHollow = { "###", "# #", "###" };
    static String[] patSurround = { "###", "#m#", "###" };
    static String[] patHead = { "###", "# #" };
    static String[] patChest = { "# #", "###", "###" };
    static String[] patLegs = { "###", "# #", "# #" };
    static String[] patBoots = { "# #", "# #" };

    public static final int ingotLiquidValue = 144;
    public static final int oreLiquidValue = ingotLiquidValue * ConfigCore.ingotsPerOre;
    public static final int blockLiquidValue = ingotLiquidValue * 9;
    public static final int chunkLiquidValue = ingotLiquidValue / 2;
    public static final int nuggetLiquidValue = ingotLiquidValue / 9;

    /**
     * High oven component recipes
     */
    public static void craftHighOven ()
    {
        final Detailing chiseling = TConstructRegistry.getChiselDetailing();
        final ItemStack itemScorchedBrick = new ItemStack(TSContent.materialsTS, 1, 0);
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 0), patHollow, '#', itemScorchedBrick);
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 1), "b b", "b b", "b b", 'b', itemScorchedBrick);
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 2), "bb", "bb", 'b', itemScorchedBrick);
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 12), "bbb", "   ", "bbb", 'b', itemScorchedBrick);
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 12), patSurround, '#', itemScorchedBrick, 'm', new ItemStack(Item.dyePowder, 1, 4));

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
    public static void craftSteelArmor ()
    {
        final ItemStack ingotSteel = TConstructRegistry.getItemStack("ingotSteel");
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.helmetSteel, new Object[] { patHead, '#', ingotSteel }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.chestplateSteel, new Object[] { patChest, '#', ingotSteel }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.leggingsSteel, new Object[] { patLegs, '#', ingotSteel }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.bootsSteel, new Object[] { patBoots, '#', ingotSteel }));
    }

    /**
     * Manual recipes
     */
    public static void castManuals ()
    {
        final LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        final ItemStack manual1 = new ItemStack(TSContent.bookManual, 1, 0);
        final FluidStack fluidStoneMinor = new FluidStack(TContent.moltenStoneFluid, 8);
        tableCasting.addCastingRecipe(manual1, fluidStoneMinor, new ItemStack(TContent.manualBook, 1), true, 50);
        tableCasting.addCastingRecipe(manual1, fluidStoneMinor, new ItemStack(Item.book, 1), true, 50);
    }

    /**
     * Scorched brick recipes
     */
    public static void castScorchedBrick ()
    {
        final LiquidCasting basinCasting = TConstructRegistry.instance.getBasinCasting();
        final LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        final ItemStack itemScorchedBrick = new ItemStack(TSContent.materialsTS, 1, 0);
        final ItemStack blockScorchedBrick = new ItemStack(TSContent.highoven, 1, 2);
        final FluidStack fluidStoneMinor = new FluidStack(TContent.moltenStoneFluid, 8);
        final FluidStack fluidStoneChunk = new FluidStack(TContent.moltenStoneFluid, 32);
        tableCasting.addCastingRecipe(itemScorchedBrick, fluidStoneMinor, new ItemStack(Item.brick), true, 50);
        basinCasting.addCastingRecipe(blockScorchedBrick, fluidStoneChunk, new ItemStack(Block.brick), true, 100);
    }

    /**
     * Add recipes related to vanilla-style storage blocks
     */
    public static void craftStorageBlocks ()
    {
        GameRegistry.addRecipe(new ItemStack(TSContent.charcoalBlock, 1, 0), patBlock, '#', new ItemStack(Item.coal, 1, 1));
        GameRegistry.addRecipe(new ItemStack(TSContent.dustStorageBlock, 1, 0), patBlock, '#', new ItemStack(Item.gunpowder, 1));
        GameRegistry.addRecipe(new ItemStack(TSContent.dustStorageBlock, 1, 1), patBlock, '#', new ItemStack(Item.sugar, 1));
        GameRegistry.addRecipe(new ItemStack(Item.coal, 9, 1), "#", '#', new ItemStack(TSContent.charcoalBlock, 1, 0));
        GameRegistry.addRecipe(new ItemStack(Item.gunpowder, 9), "#", '#', new ItemStack(TSContent.dustStorageBlock, 1, 0));
        GameRegistry.addRecipe(new ItemStack(Item.sugar, 9), "#", '#', new ItemStack(TSContent.dustStorageBlock, 1, 1));
    }
    
    public static void smeltOreDict ()
    {
        final List<FluidType> exceptions = Arrays.asList(new FluidType[] { FluidType.Water, FluidType.Stone, FluidType.Ender, FluidType.Glass, FluidType.Slime, FluidType.Obsidian });
        for (final FluidType ft : FluidType.values())
        {
            if (exceptions.contains(ft))
                continue;
            final int tempMod = getFluidTempMod(ft);
            AdvancedSmelting.addDictionaryMelting("nugget" + ft.toString(), ft, tempMod, nuggetLiquidValue);
            AdvancedSmelting.addDictionaryMelting("ingot" + ft.toString(), ft, tempMod, ingotLiquidValue);
            AdvancedSmelting.addDictionaryMelting("dust" + ft.toString(), ft, tempMod, ingotLiquidValue);
            AdvancedSmelting.addDictionaryMelting("crystalline" + ft.toString(), ft, tempMod, ingotLiquidValue);
            AdvancedSmelting.addDictionaryMelting("ore" + ft.toString(), ft, tempMod, oreLiquidValue);
            AdvancedSmelting.addDictionaryMelting("oreNether" + ft.toString(), ft, tempMod, oreLiquidValue * 2);
            AdvancedSmelting.addDictionaryMelting("block" + ft.toString(), ft, tempMod, blockLiquidValue);
        }
        {
            final FluidType ft = FluidType.Obsidian;
            final int tempMod = getFluidTempMod(ft);
            AdvancedSmelting.addDictionaryMelting("nugget" + ft.toString(), ft, tempMod, nuggetLiquidValue);
            AdvancedSmelting.addDictionaryMelting("ingot" + ft.toString(), ft, tempMod, ingotLiquidValue);
            AdvancedSmelting.addDictionaryMelting("dust" + ft.toString(), ft, tempMod, ingotLiquidValue / 4);
            AdvancedSmelting.addDictionaryMelting("crystalline" + ft.toString(), ft, tempMod, ingotLiquidValue);
            AdvancedSmelting.addDictionaryMelting("ore" + ft.toString(), ft, tempMod, oreLiquidValue);
            AdvancedSmelting.addDictionaryMelting("oreNether" + ft.toString(), ft, tempMod, oreLiquidValue * 2);
            AdvancedSmelting.addDictionaryMelting("block" + ft.toString(), ft, tempMod, blockLiquidValue);
        }
        for (int i = 1; i <= 8; i++)
            AdvancedSmelting.addDictionaryMelting("compressedCobblestone" + i + "x", FluidType.Stone, getFluidTempMod(FluidType.Stone), (ingotLiquidValue / 18) * (9 ^ i));
        AdvancedSmelting.addDictionaryMelting("compressedSand1x", FluidType.Glass, getFluidTempMod(FluidType.Glass), FluidContainerRegistry.BUCKET_VOLUME * 9);
    }
    
    /**
     * Add iron smelting recipes to the High Oven
     */
    public static void smeltIron ()
    {
        final FluidType ft = FluidType.Iron;
        final int tempMod = getFluidTempMod(ft);
        AdvancedSmelting.addMelting(ft, new ItemStack(TContent.toolShard, 1, 2), tempMod, chunkLiquidValue);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.helmetIron, 1, 0), tempMod, ingotLiquidValue * 5);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.plateIron, 1, 0), tempMod, ingotLiquidValue * 8);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.legsIron, 1, 0), tempMod, ingotLiquidValue * 7);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.bootsIron, 1, 0), tempMod, ingotLiquidValue * 4);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.horseArmorIron, 1), tempMod, ingotLiquidValue * 6);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.hoeIron, 1, 0), tempMod, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.swordIron, 1, 0), tempMod, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.shovelIron, 1, 0), tempMod, ingotLiquidValue);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.pickaxeIron, 1, 0), tempMod, ingotLiquidValue * 3);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.axeIron, 1, 0), tempMod, ingotLiquidValue * 3);
        AdvancedSmelting.addMelting(ft, new ItemStack(Block.fenceIron), tempMod, (ingotLiquidValue * 6) / 16);
        AdvancedSmelting.addMelting(ft, new ItemStack(Block.pressurePlateIron), tempMod, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(ft, new ItemStack(Block.rail), tempMod, (ingotLiquidValue * 6) / 16);
        AdvancedSmelting.addMelting(ft, new ItemStack(Block.railDetector), tempMod, ingotLiquidValue);
        AdvancedSmelting.addMelting(ft, new ItemStack(Block.railActivator), tempMod, ingotLiquidValue);
        AdvancedSmelting.addMelting(ft, new ItemStack(Block.cauldron), tempMod, ingotLiquidValue * 7);
        AdvancedSmelting.addMelting(ft, new ItemStack(Block.hopperBlock), tempMod, ingotLiquidValue * 5);
        if (!ConfigCore.hardcoreAnvil)
        {
            AdvancedSmelting.addMelting(ft, new ItemStack(Block.anvil, 1, 0), tempMod, ingotLiquidValue * 31);
            AdvancedSmelting.addMelting(ft, new ItemStack(Block.anvil, 1, 1), tempMod, ingotLiquidValue * 31);
            AdvancedSmelting.addMelting(ft, new ItemStack(Block.anvil, 1, 2), tempMod, ingotLiquidValue * 31);
        }
    }
    
    public static void smeltGold ()
    {
        final FluidType ft = FluidType.Gold;
        final int tempMod = getFluidTempMod(ft);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.helmetGold, 1, 0), tempMod, ingotLiquidValue * 5);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.plateGold, 1, 0), tempMod, ingotLiquidValue * 8);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.legsGold, 1, 0), tempMod, ingotLiquidValue * 7);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.bootsGold, 1, 0), tempMod, ingotLiquidValue * 4);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.horseArmorGold, 1), tempMod, ingotLiquidValue * 6);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.hoeGold, 1, 0), tempMod, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.swordGold, 1, 0), tempMod, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.shovelGold, 1, 0), tempMod, ingotLiquidValue);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.pickaxeGold, 1, 0), tempMod, ingotLiquidValue * 3);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.axeGold, 1, 0), tempMod, ingotLiquidValue * 3);
        AdvancedSmelting.addMelting(ft, new ItemStack(Block.pressurePlateGold, 4), tempMod, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(ft, new ItemStack(Block.railPowered), tempMod, ingotLiquidValue);
        AdvancedSmelting.addMelting(ft, new ItemStack(TContent.blankPattern, 4, 2), tempMod, ingotLiquidValue * 2);
    }
    
    public static void smeltPigIron ()
    {
        final FluidType ft = FluidType.PigIron;
        AdvancedSmelting.registerMixComboForFluidOutput(ft, FluidType.Iron, new ItemStack(Item.sugar, 1, 0), new ItemStack(Item.emerald, 1, 0), new ItemStack(TContent.meatBlock, 1, 0));
    }
    
    /**
     * Add steel smelting recipes to the High Oven
     */
    public static void smeltSteel ()
    {
        final FluidType ft = FluidType.Steel;
        final int tempMod = getFluidTempMod(ft);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.helmetChain, 1, 0), tempMod, ingotLiquidValue);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.plateChain, 1, 0), tempMod, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.legsChain, 1, 0), tempMod, ingotLiquidValue * 2);
        AdvancedSmelting.addMelting(ft, new ItemStack(Item.bootsChain, 1, 0), tempMod, ingotLiquidValue);
        if (ConfigCore.hardcoreAnvil)
        {
            AdvancedSmelting.addMelting(ft, new ItemStack(Block.anvil, 1, 0), tempMod, ingotLiquidValue * 31);
            AdvancedSmelting.addMelting(ft, new ItemStack(Block.anvil, 1, 1), tempMod, ingotLiquidValue * 31);
            AdvancedSmelting.addMelting(ft, new ItemStack(Block.anvil, 1, 2), tempMod, ingotLiquidValue * 31);
        }
        if (ConfigCore.hardcoreFlintAndSteel)
            AdvancedSmelting.addMelting(ft, new ItemStack(Item.flintAndSteel, 1, 0), tempMod, ingotLiquidValue);
        AdvancedSmelting.registerMixComboForFluidOutput(ft, FluidType.Iron, new ItemStack(Item.gunpowder, 1, 0), new ItemStack(Item.redstone, 1, 0), new ItemStack(Block.sand, 2, 0));
    }

    public static void smeltMisc ()
    {
        smeltObsidian();
        smeltWater();
        smeltGlass();
        smeltStone();
        AdvancedSmelting.addMelting(Block.blockEmerald, 0, getFluidTempMod(FluidType.Emerald), new FluidStack(TContent.moltenEmeraldFluid, 320 * 9));
        AdvancedSmelting.addMelting(TContent.glueBlock, 0, getFluidTempMod(FluidType.Glue), new FluidStack(TContent.glueFluid, blockLiquidValue));
    }

    public static void smeltObsidian ()
    {
        final FluidType ft = FluidType.Obsidian;
        final Fluid fluid = TContent.moltenObsidianFluid;
        AdvancedSmelting.addMelting(Block.obsidian, 0, getFluidTempMod(ft), new FluidStack(fluid, ingotLiquidValue * 2));
    }

    public static void smeltGlass ()
    {
        final FluidType ft = FluidType.Glass;
        final Fluid fluid = TContent.moltenGlassFluid;
        AdvancedSmelting.addMelting(Block.sand, 0, getFluidTempMod(ft), new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME));
        AdvancedSmelting.addMelting(Block.glass, 0, getFluidTempMod(ft), new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME));
        AdvancedSmelting.addMelting(Block.thinGlass, 0, getFluidTempMod(ft), new FluidStack(fluid, 250));
        AdvancedSmelting.addMelting(TContent.clearGlass, 0, getFluidTempMod(ft), new FluidStack(fluid, 1000));
        AdvancedSmelting.addMelting(TContent.glassPane, 0, getFluidTempMod(ft), new FluidStack(fluid, 250));
    }

    public static void solidSmeltMixCombos ()
    {
        final ItemStack out = new ItemStack(Item.netherQuartz, 1);
        AdvancedSmelting.registerMixComboForSolidOutput(out, FluidType.Glass, new ItemStack(Item.gunpowder, 1, 0), new ItemStack(Item.flint, 1, 0), new ItemStack(Block.blockClay, 1, 0));
    }

    /**
     * Add pig iron smelting recipes to the High Oven
     */


    public static void smeltStone ()
    {
        final FluidType ft = FluidType.Stone;
        final Fluid fluid = TContent.moltenStoneFluid;
        AdvancedSmelting.addMelting(Block.stone, 0, getFluidTempMod(ft), new FluidStack(fluid, ingotLiquidValue / 18));
        AdvancedSmelting.addMelting(Block.cobblestone, 0, getFluidTempMod(ft), new FluidStack(fluid, ingotLiquidValue / 18));
        AdvancedSmelting.addMelting(TContent.craftedSoil, 1, getFluidTempMod(ft), new FluidStack(fluid, ingotLiquidValue / 4));
        
        Smeltery.addMelting(ft, new ItemStack(TContent.materials, 1, 2), getFluidTempMod(ft), ingotLiquidValue);
        AdvancedSmelting.addMelting(ft, new ItemStack(TContent.materials, 1, 2), getFluidTempMod(ft), ingotLiquidValue);
        for (int meta = 2; meta < 11; meta += 1)
        {
            if (meta == 3) continue;
            Smeltery.addMelting(ft, new ItemStack(TContent.smeltery, 1, meta), getFluidTempMod(ft), ingotLiquidValue);
            AdvancedSmelting.addMelting(ft, new ItemStack(TContent.smeltery, 1, meta), getFluidTempMod(ft), ingotLiquidValue);
        }
    }

    public static void smeltWater ()
    {
        final FluidType ft = FluidType.Water;
        final Fluid fluid = FluidRegistry.getFluid("water");
        AdvancedSmelting.addMelting(Block.ice, 0, getFluidTempMod(ft), new FluidStack(fluid, 1000));
        AdvancedSmelting.addMelting(Block.blockSnow, 0, getFluidTempMod(ft), new FluidStack(fluid, 500));
        AdvancedSmelting.addMelting(Block.snow, 0, getFluidTempMod(ft), new FluidStack(fluid, 250));
    }

    public static void alloyMisc ()
    {
        if (ConfigCore.smelteryObsidianAlloy)
            Smeltery.addAlloyMixing(new FluidStack(TContent.moltenObsidianFluid, 1), new FluidStack(FluidRegistry.LAVA, 1), new FluidStack(FluidRegistry.WATER, 1));
    }
    
    public static void changeAnvil ()
    {
        RecipeRemover.removeShapedRecipe(new ItemStack(Block.anvil));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Block.anvil), "bbb", " i ", "iii", 'i', "ingotSteel", 'b', "blockSteel"));
    }

    /**
     * Change flint & steel recipe to use steel
     */
    public static void changeFlintAndSteel ()
    {
        RecipeRemover.removeShapedRecipe(new ItemStack(Item.flintAndSteel));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.flintAndSteel), "s ", " f", 's', "ingotSteel", 'f', new ItemStack(Item.flint)));
    }

    public static void changePiston ()
    {
        final ItemStack rod = new ItemStack(TContent.toughRod, 1, 2);
        RecipeRemover.removeAnyRecipe(new ItemStack(Block.pistonBase));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Block.pistonBase), "WWW", "CTC", "CRC", 'C', "cobblestone", 'T', rod, 'R', "dustRedstone", 'W', "plankWood"));
    }

    public static int getFluidTempMod (FluidType type)
    {
        switch (type)
        {
        case Water:
            return 10;
        case Iron:
            return 913;
        case Gold:
            return 663;
        case Tin:
            return -163;
        case Copper:
            return 534;
        case Aluminum:
            return 310;
        case NaturalAluminum:
            return 310;
        case Cobalt:
            return 845;
        case Ardite:
            return 910;
        case AluminumBrass:
            return 305;
        case Alumite:
            return -129;
        case Manyullyn:
            return 534;
        case Bronze:
            return 380;
        case Steel:
            return 840;
        case Nickel:
            return 1053;
        case Lead:
            return -73;
        case Silver:
            return 563;
        case Platinum:
            return 1370;
        case Invar:
            return 840;
        case Electrum:
            return 663;
        case Obsidian:
            return 330;
        case Ender:
            return 0;
        case Glass:
            return 975;
        case Stone:
            return 600;
        case Emerald:
            return 1025;
        case Slime:
            return 0;
        case PigIron:
            return 983;
        case Glue:
            return 0;
        default:
            return 0;
        }
    }
}
