package tsteelworks.common;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import tsteelworks.TSteelworks;
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
    static String[] patSlab = { "###" };
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
        final List<FluidType> exceptions = Arrays.asList(new FluidType[] { FluidType.getFluidType("Water"), FluidType.getFluidType("Stone"), FluidType.getFluidType("Ender"), FluidType.getFluidType("Glass"), FluidType.getFluidType("Slime"), FluidType.getFluidType("Obsidian") });
        Iterator iter = FluidType.fluidTypes.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry pairs = (Map.Entry) iter.next();
            FluidType ft = (FluidType) pairs.getValue();
            if (exceptions.contains(ft)) continue;
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
            final FluidType ft = FluidType.getFluidType("Obsidian");
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
            AdvancedSmelting.addDictionaryMelting("compressedCobblestone" + i + "x", FluidType.getFluidType("Stone"), getFluidTempMod(FluidType.getFluidType("Stone")), (ingotLiquidValue / 18) * (9 ^ i));
        AdvancedSmelting.addDictionaryMelting("compressedSand1x", FluidType.getFluidType("Glass"), getFluidTempMod(FluidType.getFluidType("Glass")), FluidContainerRegistry.BUCKET_VOLUME * 9);
    }
    
    public static void createAlloys ()
    {
        Smeltery.addAlloyMixing(new FluidStack(TSContent.liquidCementFluid, 1), new FluidStack(TContent.moltenStoneFluid, 1), new FluidStack(TSContent.moltenLimestoneFluid, 1));
    }
    
    public static void createRecipes ()
    {
        craftManual();
        craftMachines();
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
        
        AdvancedSmelting.addMelting(Block.blockEmerald, 0, getFluidTempMod(FluidType.getFluidType("Emerald")), new FluidStack(TContent.moltenEmeraldFluid, 320 * 9));
        AdvancedSmelting.addMelting(TContent.glueBlock, 0, getFluidTempMod(FluidType.getFluidType("Glue")), new FluidStack(TContent.glueFluid, blockLiquidValue));
        final ItemStack netherQuartz = new ItemStack(Item.netherQuartz, 1);
        AdvancedSmelting.registerMixComboForSolidOutput(netherQuartz, FluidType.getFluidType("Glass"), "dustGunpowder", "oreberryEssence", "blockGraveyardDirt");
    }
    
    public static void craftManual ()
    {
        final LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        final ItemStack manual1 = new ItemStack(TSContent.bookManual, 1, 0);
        final FluidStack fluidStoneMinor = new FluidStack(TContent.moltenStoneFluid, 8);
        
        tableCasting.addCastingRecipe(manual1, fluidStoneMinor, new ItemStack(TContent.manualBook, 1), true, 50);
        tableCasting.addCastingRecipe(manual1, fluidStoneMinor, new ItemStack(Item.book, 1), true, 50);
    }

    public static void craftMachines ()
    {
        //final ItemStack blankPattern = TConstructRegistry.getItemStack("blankPattern");
        //final ItemStack heavyPlate = TConstructRegistry.getItemStack("heavyPlate");
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TSContent.machine, 1, 0), "aca", "#d#", "#r#", '#', "ingotBronze", 'a', "ingotAluminumBrass", 'c', "ingotSteel", 'r', new ItemStack(Item.redstone), 'd', new ItemStack(Block.pistonBase)));
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
        // High Oven / Deep Tank Components
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 0), patHollow, '#', itemScorchedBrick);
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 1), "b b", "b b", "b b", 'b', itemScorchedBrick);
        GameRegistry.addRecipe(blockScorchedBrick, patSmallBlock, '#', itemScorchedBrick);
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 12), "bbb", "   ", "bbb", 'b', itemScorchedBrick);
        GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 13), patSurround, '#', itemScorchedBrick, 'm', new ItemStack(Item.dyePowder, 1, 4));
        // Slabs
        GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 0), patSlab, '#', new ItemStack(TSContent.highoven, 1, 2));
        GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 1), patSlab, '#', new ItemStack(TSContent.highoven, 1, 4));
        GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 2), patSlab, '#', new ItemStack(TSContent.highoven, 1, 5));
        GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 3), patSlab, '#', new ItemStack(TSContent.highoven, 1, 6));
        GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 4), patSlab, '#', new ItemStack(TSContent.highoven, 1, 8));
        GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 5), patSlab, '#', new ItemStack(TSContent.highoven, 1, 9));
        GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 6), patSlab, '#', new ItemStack(TSContent.highoven, 1, 10));
        GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 7), patSlab, '#', new ItemStack(TSContent.highoven, 1, 11));
        // Recipes to obtain bricks from high oven
        String[] oxidizers = { "fuelCoal", "coal", "dustCoal" };
        String[] purifiers = { "blockSand", "Sandblock", "sand" };
        
        for (String o : oxidizers)
            for (String p : purifiers)
                AdvancedSmelting.registerMixComboForSolidOutput(itemScorchedBrick, FluidType.getFluidType("Stone"), o, null, p);

        // Casting
        tableCasting.addCastingRecipe(itemScorchedBrick, fluidStoneMinor, new ItemStack(Item.brick), true, 50);
        basinCasting.addCastingRecipe(blockScorchedBrick, fluidStoneChunk, new ItemStack(Block.brick), true, 100);
        // Chiseling
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
        
        AdvancedSmelting.registerMixComboForSolidOutput(new ItemStack(TSContent.materialsTS, 1, 1), FluidType.getFluidType("Stone"), "dyeLime", null, "blockSand");
        
        GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 0), patSlab, '#', new ItemStack(TSContent.limestoneBlock, 1, 0));
        GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 1), patSlab, '#', new ItemStack(TSContent.limestoneBlock, 1, 1));
        GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 2), patSlab, '#', new ItemStack(TSContent.limestoneBlock, 1, 2));
        GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 3), patSlab, '#', new ItemStack(TSContent.limestoneBlock, 1, 4));
        GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 4), patSlab, '#', new ItemStack(TSContent.limestoneBlock, 1, 5));
        GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 5), patSlab, '#', new ItemStack(TSContent.limestoneBlock, 1, 6));
        GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 6), patSlab, '#', new ItemStack(TSContent.limestoneBlock, 1, 7));
        GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 7), patSlab, '#', new ItemStack(TSContent.limestoneBlock, 1, 8));
        
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
        final FluidType ft = FluidType.getFluidType("Stone");
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
        final FluidType ft = FluidType.getFluidType("Steel");
        final ItemStack ingotSteel = TConstructRegistry.getItemStack("ingotSteel");
        
        if (ConfigCore.enableSteelArmor)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.helmetSteel, new Object[] { patHead, '#', ingotSteel }));
            GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.chestplateSteel, new Object[] { patChest, '#', ingotSteel }));
            GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.leggingsSteel, new Object[] { patLegs, '#', ingotSteel }));
            GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.bootsSteel, new Object[] { patBoots, '#', ingotSteel }));
        }
        
        String[] oxidizers = { "dustGunpowder", "dustSulphur", "dustSulfur", "dustSaltpeter", "dustSaltpetre", "dustCoal" };
        String[] reducers = { "dustRedstone", "dustManganese", "dustAluminum", "dustAluminium" };
        String[] purifiers = { "blockSand", "Sandblock", "sand" };
        
        for (String o : oxidizers)
            for (String r : reducers)
                for (String p : purifiers)
                    AdvancedSmelting.registerMixComboForFluidOutput(ft, FluidType.getFluidType("Iron"), o, r, p);
    }

    public static void craftPigIron ()
    {
        final FluidType ft = FluidType.getFluidType("PigIron");
        AdvancedSmelting.registerMixComboForFluidOutput(ft, FluidType.getFluidType("Iron"), "dustSugar", "dyeWhite", "hambone");
    }

    public static void craftObsidian ()
    {
        final FluidType ft = FluidType.getFluidType("Obsidian");
        final Fluid fluid = TContent.moltenObsidianFluid;
        AdvancedSmelting.addMelting(Block.obsidian, 0, getFluidTempMod(ft), new FluidStack(fluid, ingotLiquidValue * 2));
    }

    public static void craftGlass ()
    {
        final FluidType ft = FluidType.getFluidType("Glass");
        final Fluid fluid = TContent.moltenGlassFluid;
        
        AdvancedSmelting.addMelting(Block.sand, 0, getFluidTempMod(ft), new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME));
        AdvancedSmelting.addMelting(Block.glass, 0, getFluidTempMod(ft), new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME));
        AdvancedSmelting.addMelting(Block.thinGlass, 0, getFluidTempMod(ft), new FluidStack(fluid, 250));
        AdvancedSmelting.addMelting(TContent.clearGlass, 0, getFluidTempMod(ft), new FluidStack(fluid, 1000));
        AdvancedSmelting.addMelting(TContent.glassPane, 0, getFluidTempMod(ft), new FluidStack(fluid, 250));
    }

    public static void craftWater ()
    {
        final FluidType ft = FluidType.getFluidType("Water");
        final Fluid fluid = FluidRegistry.WATER;
        
        AdvancedSmelting.addMelting(Block.ice, 0, getFluidTempMod(ft), new FluidStack(fluid, 1000));
        AdvancedSmelting.addMelting(Block.blockSnow, 0, getFluidTempMod(ft), new FluidStack(fluid, 500));
        AdvancedSmelting.addMelting(Block.snow, 0, getFluidTempMod(ft), new FluidStack(fluid, 250));
    }

    public static void changeAnvil ()
    {
        RecipeRemover.removeShapedRecipe(new ItemStack(Block.anvil));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Block.anvil), "bbb", " i ", "iii", 'i', "ingotSteel", 'b', "blockSteel"));
        
        Smeltery.addMelting(FluidType.getFluidType("Steel"), new ItemStack(Block.anvil, 1, 0), 0, ingotLiquidValue * 31);
        Smeltery.addMelting(FluidType.getFluidType("Steel"), new ItemStack(Block.anvil, 1, 1), 0, ingotLiquidValue * 31);
        Smeltery.addMelting(FluidType.getFluidType("Steel"), new ItemStack(Block.anvil, 1, 2), 0, ingotLiquidValue * 31);
    }

    public static void changeFlintAndSteel ()
    {
        RecipeRemover.removeShapedRecipe(new ItemStack(Item.flintAndSteel));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.flintAndSteel), "s ", " f", 's', "ingotSteel", 'f', new ItemStack(Item.flint)));
        
        Item.flintAndSteel.setMaxDamage(128);
        
        Smeltery.addMelting(FluidType.getFluidType("Steel"), new ItemStack(Item.flintAndSteel, 1, 0), 0, ingotLiquidValue);
    }

    public static void changePiston ()
    {
        final ItemStack rod = new ItemStack(TContent.toughRod, 1, 2);
        
        RecipeRemover.removeAnyRecipe(new ItemStack(Block.pistonBase));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Block.pistonBase), "WWW", "CTC", "CRC", 'C', "cobblestone", 'T', rod, 'R', "dustRedstone", 'W', "plankWood"));
    }

    public static int getFluidTempMod (FluidType type) {
        Fluid fluid = type.fluid;
        String fluidName = fluid.getName();

        switch (fluidName){
        case "Water": return 10;
        case "Iron": return 913;
        case "Gold": return 663;
        case "Tin": return -163;
        case "Copper": return 534;
        case "Aluminum": return 310;
        case "NaturalAluminum": return 310;
        case "Cobalt": return 845;
        case "Ardite": return 910;
        case "AluminumBrass": return 305;
        case "Alumite": return -129;
        case "Manyullyn": return 534;
        case "Bronze": return 380;
        case "Steel": return 840;
        case "Nickel": return 1053;
        case "Lead": return -73;
        case "Silver": return 563;
        case "Platinum": return 1370;
        case "Invar": return 840;
        case "Electrum": return 663;
        case "Obsidian": return 330;
        case "Ender": return 0;
        case "Glass": return 975;
        case "Stone": return 600;
        case "Emerald": return 1025;
        case "Slime": return 0;
        case "PigIron": return 983;
        case "Glue": return 0;
        default: return 0;
        //TODO: Make this use if statements for Java 6 compatibility
        //if (fluidName.equals("Water")) {return 10;} etc ..
        }
    }
}