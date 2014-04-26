package tsteelworks.common;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
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
    static String[] patSmallBlock = { "##", "##" };
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

    public static void setupCrafting ()
    {
        addOreDictionarySmelting();
        createRecipes();
        createAlloys();
    }
    
    public static void addOreDictionarySmelting ()
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
    
    public static void createAlloys ()
    {
        Smeltery.addAlloyMixing(new FluidStack(TSContent.liquidCementFluid, 1), new FluidStack(TContent.moltenStoneFluid, 1), new FluidStack(TSContent.moltenLimestoneFluid, 1));
    }
    
    public static void createRecipes ()
    {
        craftManual();
        craftScorchedStone();
        craftLimestone();
        craftStone();
        craftStorageBlocks();
        craftSteel();
        craftPigIron();
        craftObsidian();
        craftGlass();
        craftWater();
        
        if (ConfigCore.hardcorePiston) TSRecipes.changePiston();
        if (ConfigCore.hardcoreFlintAndSteel) TSRecipes.changeFlintAndSteel();
        if (ConfigCore.hardcoreAnvil) TSRecipes.changeAnvil();
        
        AdvancedSmelting.addMelting(Block.blockEmerald, 0, getFluidTempMod(FluidType.Emerald), new FluidStack(TContent.moltenEmeraldFluid, 320 * 9));
        AdvancedSmelting.addMelting(TContent.glueBlock, 0, getFluidTempMod(FluidType.Glue), new FluidStack(TContent.glueFluid, blockLiquidValue));
        final ItemStack netherQuartz = new ItemStack(Item.netherQuartz, 1);
        AdvancedSmelting.registerMixComboForSolidOutput(netherQuartz, FluidType.Glass, "dustGunpowder", "oreberryEssence", "blockGraveyardDirt");
    }
    
    public static void craftManual ()
    {
        final LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        final ItemStack manual1 = new ItemStack(TSContent.bookManual, 1, 0);
        final FluidStack fluidStoneMinor = new FluidStack(TContent.moltenStoneFluid, 8);
        
        tableCasting.addCastingRecipe(manual1, fluidStoneMinor, new ItemStack(TContent.manualBook, 1), true, 50);
        tableCasting.addCastingRecipe(manual1, fluidStoneMinor, new ItemStack(Item.book, 1), true, 50);
    }

    public static void craftScorchedStone ()
    {
        final LiquidCasting basinCasting = TConstructRegistry.instance.getBasinCasting();
        final LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        final Detailing chiseling = TConstructRegistry.getChiselDetailing();
        
        final ItemStack itemScorchedBrick = new ItemStack(TSContent.materialsTS, 1, 0);
        final ItemStack blockScorchedBrick = new ItemStack(TSContent.highoven, 1, 2);
        final FluidStack fluidStoneMinor = new FluidStack(TContent.moltenStoneFluid, 8);
        final FluidStack fluidStoneChunk = new FluidStack(TContent.moltenStoneFluid, 32);
        
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 0), patHollow, '#', itemScorchedBrick);
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 1), "b b", "b b", "b b", 'b', itemScorchedBrick);
        GameRegistry.addRecipe(blockScorchedBrick, patSmallBlock, '#', itemScorchedBrick);
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 12), "bbb", "   ", "bbb", 'b', itemScorchedBrick);
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 13), patSurround, '#', itemScorchedBrick, 'm', new ItemStack(Item.dyePowder, 1, 4));

        AdvancedSmelting.registerMixComboForSolidOutput(itemScorchedBrick, FluidType.Stone, "fuelCoal", null, "blockSand");
        AdvancedSmelting.registerMixComboForSolidOutput(itemScorchedBrick, FluidType.Stone, "coal", null, "blockSand");
        
        tableCasting.addCastingRecipe(itemScorchedBrick, fluidStoneMinor, new ItemStack(Item.brick), true, 50);
        basinCasting.addCastingRecipe(blockScorchedBrick, fluidStoneChunk, new ItemStack(Block.brick), true, 100);
        
        chiseling.addDetailing(TSContent.highoven, 4, TSContent.highoven, 6, TContent.chisel);
        chiseling.addDetailing(TSContent.highoven, 6, TSContent.highoven, 11, TContent.chisel);
        chiseling.addDetailing(TSContent.highoven, 11, TSContent.highoven, 2, TContent.chisel);
        chiseling.addDetailing(TSContent.highoven, 2, TSContent.highoven, 8, TContent.chisel);
        chiseling.addDetailing(TSContent.highoven, 8, TSContent.highoven, 9, TContent.chisel);
        chiseling.addDetailing(TSContent.highoven, 9, TSContent.highoven, 10, TContent.chisel);
        chiseling.addDetailing(TSContent.highoven, 10, TSContent.highoven, 4, TContent.chisel);
    }
    
    public static void craftLimestone ()
    {
        final Detailing chiseling = TConstructRegistry.getChiselDetailing();
        final Fluid fluid = TSContent.moltenLimestoneFluid;
        
        GameRegistry.addRecipe(new ItemStack(TSContent.limestoneBlock, 1, 2), patSmallBlock, '#', new ItemStack(TSContent.materialsTS, 1, 1));
        
        FurnaceRecipes.smelting().addSmelting(TSContent.limestoneBlock.blockID, 1, new ItemStack(TSContent.limestoneBlock, 0, 1), 2f);
        FurnaceRecipes.smelting().addSmelting(TSContent.limestoneBlock.blockID, 0, new ItemStack(TSContent.materialsTS, 1, 1), 2f);
        
        Smeltery.addMelting(TSContent.limestoneBlock, 0, 0, new FluidStack(fluid, ingotLiquidValue / 18));
        Smeltery.addMelting(TSContent.limestoneBlock, 1, 0, new FluidStack(fluid, ingotLiquidValue / 18));
        AdvancedSmelting.addMelting(TSContent.limestoneBlock, 0, 825, new FluidStack(fluid, ingotLiquidValue / 18));
        AdvancedSmelting.addMelting(TSContent.limestoneBlock, 1, 825, new FluidStack(fluid, ingotLiquidValue / 18));
        Smeltery.addMelting(new ItemStack(TSContent.materialsTS, 1, 1), TSContent.limestoneBlock.blockID, 1, 0, new FluidStack(fluid, ingotLiquidValue));
        AdvancedSmelting.addMelting(new ItemStack(TSContent.materialsTS, 1, 1), TSContent.limestoneBlock.blockID, 1, 825, new FluidStack(fluid, ingotLiquidValue));
    
        chiseling.addDetailing(TSContent.limestoneBlock, 2, TSContent.limestoneBlock, 3, TContent.chisel);
        chiseling.addDetailing(TSContent.limestoneBlock, 3, TSContent.limestoneBlock, 4, TContent.chisel);
        chiseling.addDetailing(TSContent.limestoneBlock, 4, TSContent.limestoneBlock, 5, TContent.chisel);
        chiseling.addDetailing(TSContent.limestoneBlock, 5, TSContent.limestoneBlock, 6, TContent.chisel);
        chiseling.addDetailing(TSContent.limestoneBlock, 6, TSContent.limestoneBlock, 7, TContent.chisel);
        chiseling.addDetailing(TSContent.limestoneBlock, 7, TSContent.limestoneBlock, 8, TContent.chisel);
        chiseling.addDetailing(TSContent.limestoneBlock, 8, TSContent.limestoneBlock, 3, TContent.chisel);
    }
    
    public static void craftStone ()
    {
        final FluidType ft = FluidType.Stone;
        final Fluid fluid = TContent.moltenStoneFluid;
        
        AdvancedSmelting.addMelting(Block.stone, 0, getFluidTempMod(ft), new FluidStack(fluid, ingotLiquidValue / 18));
        AdvancedSmelting.addMelting(Block.cobblestone, 0, getFluidTempMod(ft), new FluidStack(fluid, ingotLiquidValue / 18));
        AdvancedSmelting.addMelting(TContent.craftedSoil, 1, getFluidTempMod(ft), new FluidStack(fluid, ingotLiquidValue / 4));
        
        Smeltery.addMelting(ft, new ItemStack(TContent.materials, 1, 2), 0, ingotLiquidValue);
        AdvancedSmelting.addMelting(ft, new ItemStack(TContent.materials, 1, 2), getFluidTempMod(ft), ingotLiquidValue);
        for (int meta = 2; meta < 11; meta += 1)
        {
            if (meta == 3) continue;
            Smeltery.addMelting(ft, new ItemStack(TContent.smeltery, 1, meta), 0, ingotLiquidValue);
            AdvancedSmelting.addMelting(ft, new ItemStack(TContent.smeltery, 1, meta), getFluidTempMod(ft), ingotLiquidValue);
        }
    }
    
    public static void craftStorageBlocks ()
    {
        GameRegistry.addRecipe(new ItemStack(TSContent.charcoalBlock, 1, 0), patBlock, '#', new ItemStack(Item.coal, 1, 1));
        GameRegistry.addRecipe(new ItemStack(TSContent.dustStorageBlock, 1, 0), patBlock, '#', new ItemStack(Item.gunpowder, 1));
        GameRegistry.addRecipe(new ItemStack(TSContent.dustStorageBlock, 1, 1), patBlock, '#', new ItemStack(Item.sugar, 1));
        GameRegistry.addRecipe(new ItemStack(Item.coal, 9, 1), "#", '#', new ItemStack(TSContent.charcoalBlock, 1, 0));
        GameRegistry.addRecipe(new ItemStack(Item.gunpowder, 9), "#", '#', new ItemStack(TSContent.dustStorageBlock, 1, 0));
        GameRegistry.addRecipe(new ItemStack(Item.sugar, 9), "#", '#', new ItemStack(TSContent.dustStorageBlock, 1, 1));
    }
    
    public static void craftSteel ()
    {
        final FluidType ft = FluidType.Steel;
        final ItemStack ingotSteel = TConstructRegistry.getItemStack("ingotSteel");
        
        if (ConfigCore.enableSteelArmor)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.helmetSteel, new Object[] { patHead, '#', ingotSteel }));
            GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.chestplateSteel, new Object[] { patChest, '#', ingotSteel }));
            GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.leggingsSteel, new Object[] { patLegs, '#', ingotSteel }));
            GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.bootsSteel, new Object[] { patBoots, '#', ingotSteel }));
        }
        
        AdvancedSmelting.registerMixComboForFluidOutput(ft, FluidType.Iron, "dustGunpowder", "dustRedstone", "blockSand");
        AdvancedSmelting.registerMixComboForFluidOutput(ft, FluidType.Iron, "dustGunpowder", "dustAluminum", "blockSand");
        AdvancedSmelting.registerMixComboForFluidOutput(ft, FluidType.Iron, "dustGunpowder", "dustAluminium", "blockSand");
        AdvancedSmelting.registerMixComboForFluidOutput(ft, FluidType.Iron, "dustSulfur", "dustManganese", "blockSand");
    }

    public static void craftPigIron ()
    {
        final FluidType ft = FluidType.PigIron;
        AdvancedSmelting.registerMixComboForFluidOutput(ft, FluidType.Iron, "dustSugar", "dyeWhite", "hambone");
    }

    public static void craftObsidian ()
    {
        final FluidType ft = FluidType.Obsidian;
        final Fluid fluid = TContent.moltenObsidianFluid;
        AdvancedSmelting.addMelting(Block.obsidian, 0, getFluidTempMod(ft), new FluidStack(fluid, ingotLiquidValue * 2));
    }

    public static void craftGlass ()
    {
        final FluidType ft = FluidType.Glass;
        final Fluid fluid = TContent.moltenGlassFluid;
        
        AdvancedSmelting.addMelting(Block.sand, 0, getFluidTempMod(ft), new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME));
        AdvancedSmelting.addMelting(Block.glass, 0, getFluidTempMod(ft), new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME));
        AdvancedSmelting.addMelting(Block.thinGlass, 0, getFluidTempMod(ft), new FluidStack(fluid, 250));
        AdvancedSmelting.addMelting(TContent.clearGlass, 0, getFluidTempMod(ft), new FluidStack(fluid, 1000));
        AdvancedSmelting.addMelting(TContent.glassPane, 0, getFluidTempMod(ft), new FluidStack(fluid, 250));
    }

    public static void craftWater ()
    {
        final FluidType ft = FluidType.Water;
        final Fluid fluid = FluidRegistry.WATER;
        
        AdvancedSmelting.addMelting(Block.ice, 0, getFluidTempMod(ft), new FluidStack(fluid, 1000));
        AdvancedSmelting.addMelting(Block.blockSnow, 0, getFluidTempMod(ft), new FluidStack(fluid, 500));
        AdvancedSmelting.addMelting(Block.snow, 0, getFluidTempMod(ft), new FluidStack(fluid, 250));
    }

    public static void changeAnvil ()
    {
        RecipeRemover.removeShapedRecipe(new ItemStack(Block.anvil));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Block.anvil), "bbb", " i ", "iii", 'i', "ingotSteel", 'b', "blockSteel"));
        
        Smeltery.addMelting(FluidType.Steel, new ItemStack(Block.anvil, 1, 0), 0, ingotLiquidValue * 31);
        Smeltery.addMelting(FluidType.Steel, new ItemStack(Block.anvil, 1, 1), 0, ingotLiquidValue * 31);
        Smeltery.addMelting(FluidType.Steel, new ItemStack(Block.anvil, 1, 2), 0, ingotLiquidValue * 31);
    }

    public static void changeFlintAndSteel ()
    {
        RecipeRemover.removeShapedRecipe(new ItemStack(Item.flintAndSteel));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.flintAndSteel), "s ", " f", 's', "ingotSteel", 'f', new ItemStack(Item.flint)));
        
        Item.flintAndSteel.setMaxDamage(128);
        
        Smeltery.addMelting(FluidType.Steel, new ItemStack(Item.flintAndSteel, 1, 0), 0, ingotLiquidValue);
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
        case Water: return 10;
        case Iron: return 913;
        case Gold: return 663;
        case Tin: return -163;
        case Copper: return 534;
        case Aluminum: return 310;
        case NaturalAluminum: return 310;
        case Cobalt: return 845;
        case Ardite: return 910;
        case AluminumBrass: return 305;
        case Alumite: return -129;
        case Manyullyn: return 534;
        case Bronze: return 380;
        case Steel: return 840;
        case Nickel: return 1053;
        case Lead: return -73;
        case Silver: return 563;
        case Platinum: return 1370;
        case Invar: return 840;
        case Electrum: return 663;
        case Obsidian: return 330;
        case Ender: return 0;
        case Glass: return 975;
        case Stone: return 600;
        case Emerald: return 1025;
        case Slime: return 0;
        case PigIron: return 983;
        case Glue: return 0;
        default: return 0;
        }
    }
}