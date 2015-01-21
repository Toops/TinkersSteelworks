package toops.tsteelworks.common.core;

import cpw.mods.fml.common.registry.GameRegistry;
import mantle.Mantle;
import mantle.utils.RecipeRemover;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.Detailing;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.Smeltery;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;
import toops.tsteelworks.api.highoven.IFuelRegistry;
import toops.tsteelworks.api.highoven.IMixerRegistry;
import toops.tsteelworks.api.highoven.ISmeltingRegistry;
import toops.tsteelworks.common.blocks.HighOvenBlock;
import toops.tsteelworks.lib.ModsData;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TSRecipes {
	/*
	 * Common Patterns
	 */
	public static final String[] PAT_BLOCK = {"###", "###", "###"};
	public static final String[] PAT_SMALL_BLOCK = {"##", "##"};
	public static final String[] PAT_SLAB = {"###"};
	public static final String[] PAT_HOLLOW = {"###", "# #", "###"};
	public static final String[] PAT_SURROUND = {"###", "#m#", "###"};

	public static final String[] PAT_HEAD = {"###", "# #"};
	public static final String[] PAT_CHEST = {"# #", "###", "###"};
	public static final String[] PAT_LEGS = {"###", "# #", "# #"};
	public static final String[] PAT_BOOTS = {"# #", "# #"};

	public static final int INGOT_LIQUID_VALUE = TConstruct.ingotLiquidValue;
	public static int ORE_LIQUID_VALUE = INGOT_LIQUID_VALUE;
	public static final int BLOCK_LIQUID_VALUE = TConstruct.blockLiquidValue;
	public static final int NUGGET_LIQUID_VALUE = TConstruct.nuggetLiquidValue;

	public static void setupRecipes() {
		addHighOvenSmelts();
		addHighOvenFuels();
		createAlloys();

		createRecipes();
	}

	private static void addHighOvenFuels() {
		IFuelRegistry.INSTANCE.addFuel(new ItemStack(Items.coal, 1, 1), 420, 4);
		for(final ItemStack charcoalBlock : OreDictionary.getOres("blockCharcoal")){
			IFuelRegistry.INSTANCE.addFuel(charcoalBlock, 4200, 7);
		}

		if (ModsData.Railcraft.isLoaded) {
			IFuelRegistry.INSTANCE.addFuel(ModsData.Railcraft.coalCoke, 840, 10);
			IFuelRegistry.INSTANCE.addFuel(ModsData.Railcraft.coalCokeBlock, 8400, 15);
		}

		if (ModsData.Thaumcraft.isLoaded) {
			IFuelRegistry.INSTANCE.addFuel(ModsData.Thaumcraft.alumentum, 420 * 4, 4);
		}
	}

	public static void addHighOvenSmelts() {
		final List<String> exceptions = Arrays.asList("Water", "Stone", "Ender", "Glass", "Slime", "Obsidian");
		final ISmeltingRegistry advancedSmelting = ISmeltingRegistry.INSTANCE;

		for (Map.Entry<String, FluidType> entry : FluidType.fluidTypes.entrySet()) {
			if (exceptions.contains(entry.getKey())) continue;

			final int temperatureMod = getFluidTempMod(entry.getKey());
			final FluidStack ingotFS = new FluidStack(entry.getValue().fluid, INGOT_LIQUID_VALUE);

			advancedSmelting.addDictionaryMeltable("nugget" + entry.getKey(), new FluidStack(entry.getValue().fluid, NUGGET_LIQUID_VALUE), temperatureMod);
			advancedSmelting.addDictionaryMeltable("ingot" + entry.getKey(), ingotFS, temperatureMod);
			advancedSmelting.addDictionaryMeltable("dust" + entry.getKey(), ingotFS, temperatureMod);
			advancedSmelting.addDictionaryMeltable("crystalline" + entry.getKey(), ingotFS, temperatureMod);
			advancedSmelting.addDictionaryMeltable("ore" + entry.getKey(), new FluidStack(entry.getValue().fluid, ORE_LIQUID_VALUE), temperatureMod);
			advancedSmelting.addDictionaryMeltable("oreNether" + entry.getKey(), new FluidStack(entry.getValue().fluid, ORE_LIQUID_VALUE * 2), temperatureMod);
			advancedSmelting.addDictionaryMeltable("block" + entry.getKey(), new FluidStack(entry.getValue().fluid, BLOCK_LIQUID_VALUE), temperatureMod);
		}

		final FluidType obsidian = FluidType.getFluidType("Obsidian");
		if (obsidian != null) {
			final String name = "Obsidian";
			final FluidStack ingotFS = new FluidStack(obsidian.fluid, INGOT_LIQUID_VALUE);
			final int temperatureMod = getFluidTempMod(name);

			advancedSmelting.addDictionaryMeltable("nugget" + name, new FluidStack(obsidian.fluid, NUGGET_LIQUID_VALUE), temperatureMod);
			advancedSmelting.addDictionaryMeltable("ingot" + name, ingotFS, temperatureMod);
			advancedSmelting.addDictionaryMeltable("dust" + name, new FluidStack(obsidian.fluid, INGOT_LIQUID_VALUE / 2), temperatureMod);
			advancedSmelting.addDictionaryMeltable("block" + name, new FluidStack(obsidian.fluid, INGOT_LIQUID_VALUE), temperatureMod);
		}

		advancedSmelting.addMeltable(new ItemStack(Items.emerald), false, getFluidTempMod("Emerald"), new FluidStack(TinkerSmeltery.moltenEmeraldFluid, INGOT_LIQUID_VALUE));
		advancedSmelting.addMeltable(new ItemStack(TinkerSmeltery.glueBlock), false, getFluidTempMod("Glue"), new FluidStack(TinkerSmeltery.glueFluid, BLOCK_LIQUID_VALUE));

		FluidType limestoneFluid = FluidType.getFluidType("Limestone");
		Smeltery.addDictionaryMelting("blockLimestone", limestoneFluid, 825, INGOT_LIQUID_VALUE * 4);
		advancedSmelting.addDictionaryMeltable("blockLimestone", new FluidStack(limestoneFluid.fluid, INGOT_LIQUID_VALUE * 4), 825);

		Smeltery.addMelting(new ItemStack(TSContent.materialsTS, 1, 1), TSContent.limestoneBlock, 1, 0, new FluidStack(ModsData.Fluids.moltenLimestoneFluid, INGOT_LIQUID_VALUE));
		advancedSmelting.addMeltable(new ItemStack(TSContent.materialsTS, 1, 1), false, 825, new FluidStack(ModsData.Fluids.moltenLimestoneFluid, INGOT_LIQUID_VALUE));

		Smeltery.addMelting(new ItemStack(TSContent.materialsTS, 1, 2), TSContent.limestoneBlock, 1, 0, new FluidStack(ModsData.Fluids.moltenLimestoneFluid, INGOT_LIQUID_VALUE));
		advancedSmelting.addMeltable(new ItemStack(TSContent.materialsTS, 1, 2), false, 825, new FluidStack(ModsData.Fluids.moltenLimestoneFluid, INGOT_LIQUID_VALUE));
	}

	public static void createAlloys() {
		Smeltery.addAlloyMixing(
				new FluidStack(TSContent.liquidCementFluid, 1),
				new FluidStack(TinkerSmeltery.moltenStoneFluid, 1),
				new FluidStack(TSContent.moltenLimestoneFluid, 1)
		);
	}

	public static void createRecipes() {
		craftManual();
		craftScorchedStone();
		craftLimestone();
		craftStone();
		craftStorageBlocks();
		craftSteel();
		craftPigIron();
		craftGlass();
		craftWater();
		craftMachines();

		if (ConfigCore.hardcorePiston)
			TSRecipes.changePiston();

		if (ConfigCore.hardcoreFlintAndSteel)
			TSRecipes.changeFlintAndSteel();

		if (ConfigCore.hardcoreAnvil)
			TSRecipes.changeAnvil();


		final ItemStack netherQuartz = new ItemStack(Items.quartz, 2);
		IMixerRegistry.INSTANCE.registerMix(netherQuartz, TinkerSmeltery.moltenGlassFluid, "dustGunpowder", "oreberryEssence", "blockGraveyardDirt");
	}

	public static void craftMachines() {
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TSContent.steamTurbine), "aca", "#d#", "#r#", '#', "ingotBronze", 'a', "ingotAluminumBrass", 'c', "ingotSteel", 'r', new ItemStack(Items.redstone), 'd', new ItemStack(Blocks.piston)));
	}

	public static void craftManual() {
		final LiquidCasting tableCasting = TConstructRegistry.getTableCasting();

		final ItemStack manual = new ItemStack(TSContent.bookManual, 1, 0);
		final FluidStack fluidStoneMinor = new FluidStack(TinkerSmeltery.moltenStoneFluid, 8);

		tableCasting.addCastingRecipe(manual, fluidStoneMinor, new ItemStack(TinkerTools.manualBook, 1, 0), true, 50);
		tableCasting.addCastingRecipe(manual, fluidStoneMinor, new ItemStack(TinkerTools.manualBook, 1, 1), true, 50);
		tableCasting.addCastingRecipe(manual, fluidStoneMinor, new ItemStack(TinkerTools.manualBook, 1, 2), true, 50);
		tableCasting.addCastingRecipe(manual, fluidStoneMinor, new ItemStack(TinkerTools.manualBook, 1, 3), true, 50);

		tableCasting.addCastingRecipe(manual, fluidStoneMinor, new ItemStack(Mantle.mantleBook, 1), true, 50);
		tableCasting.addCastingRecipe(manual, fluidStoneMinor, new ItemStack(Items.book, 1), true, 50);
	}

	public static void craftScorchedStone() {
		final LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();
		final LiquidCasting tableCasting = TConstructRegistry.getTableCasting();

		final Detailing chiseling = TConstructRegistry.getChiselDetailing();

		final ItemStack itemScorchedBrick = new ItemStack(TSContent.materialsTS, 1, 0);
		final ItemStack blockScorchedBrick = new ItemStack(TSContent.highoven, 1, 2);
		final FluidStack fluidStoneMinor = new FluidStack(TinkerSmeltery.moltenStoneFluid, 8);
		final FluidStack fluidStoneChunk = new FluidStack(TinkerSmeltery.moltenStoneFluid, 32);
		// High Oven / Deep Tank Components
		GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_HIGHOVEN), PAT_HOLLOW, '#', itemScorchedBrick);
		GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_DRAIN), "b b", "b b", "b b", 'b', itemScorchedBrick);
		GameRegistry.addRecipe(blockScorchedBrick, PAT_SMALL_BLOCK, '#', itemScorchedBrick);
		GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_DUCT), "bbb", "   ", "bbb", 'b', itemScorchedBrick);
		GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_TANK), PAT_SURROUND, '#', itemScorchedBrick, 'm', new ItemStack(Items.dye, 1, 4));
		GameRegistry.addSmelting(new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_COBBLE), new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_STONE), 2);
		GameRegistry.addSmelting(new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_BRICK), new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_CRACKED), 2);

		// Slabs
		GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 0), PAT_SLAB, '#', new ItemStack(TSContent.highoven, 1, 2));
		GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 1), PAT_SLAB, '#', new ItemStack(TSContent.highoven, 1, 4));
		GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 2), PAT_SLAB, '#', new ItemStack(TSContent.highoven, 1, 5));
		GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 3), PAT_SLAB, '#', new ItemStack(TSContent.highoven, 1, 6));
		GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 4), PAT_SLAB, '#', new ItemStack(TSContent.highoven, 1, 8));
		GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 5), PAT_SLAB, '#', new ItemStack(TSContent.highoven, 1, 9));
		GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 6), PAT_SLAB, '#', new ItemStack(TSContent.highoven, 1, 10));
		GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 7), PAT_SLAB, '#', new ItemStack(TSContent.highoven, 1, 11));
		// Recipes to obtain bricks from high oven
		String[] oxidizers = {"fuelCoal", "coal", "dustCoal"};
		String[] purifiers = {"blockSand", "Sandblock", "sand"};

		for (String o : oxidizers)
			for (String p : purifiers)
				IMixerRegistry.INSTANCE.registerMix(itemScorchedBrick, TinkerSmeltery.moltenStoneFluid, o, null, p);

		// Casting
		tableCasting.addCastingRecipe(itemScorchedBrick, fluidStoneMinor, new ItemStack(Items.brick), true, 50);
		basinCasting.addCastingRecipe(blockScorchedBrick, fluidStoneChunk, new ItemStack(Blocks.brick_block), true, 100);

		// Chiseling
		chiseling.addDetailing(TSContent.highoven, 4, TSContent.highoven, 6, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.highoven, 6, TSContent.highoven, 11, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.highoven, 11, TSContent.highoven, 2, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.highoven, 2, TSContent.highoven, 8, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.highoven, 8, TSContent.highoven, 9, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.highoven, 9, TSContent.highoven, 10, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.highoven, 10, TSContent.highoven, 4, TinkerTools.chisel);
	}

	public static void craftLimestone() {
		final Fluid fluid = TSContent.moltenLimestoneFluid;

		GameRegistry.addRecipe(new ItemStack(TSContent.limestoneBlock, 1, 2), PAT_SMALL_BLOCK, '#', new ItemStack(TSContent.materialsTS, 1, 1));

		// add to smelt
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(TSContent.limestoneBlock, 1, 1), new ItemStack(TSContent.limestoneBlock, 1, 0), 2f);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(TSContent.limestoneBlock, 1, 0), new ItemStack(TSContent.materialsTS, 4, 1), 2f);

		IMixerRegistry.INSTANCE.registerMix(new ItemStack(TSContent.materialsTS, 1, 1), TinkerSmeltery.moltenStoneFluid, "dyeLime", null, "blockSand");

		GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 0), PAT_SLAB, '#', new ItemStack(TSContent.limestoneBlock, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 1), PAT_SLAB, '#', new ItemStack(TSContent.limestoneBlock, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 2), PAT_SLAB, '#', new ItemStack(TSContent.limestoneBlock, 1, 2));
		GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 3), PAT_SLAB, '#', new ItemStack(TSContent.limestoneBlock, 1, 4));
		GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 4), PAT_SLAB, '#', new ItemStack(TSContent.limestoneBlock, 1, 5));
		GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 5), PAT_SLAB, '#', new ItemStack(TSContent.limestoneBlock, 1, 6));
		GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 6), PAT_SLAB, '#', new ItemStack(TSContent.limestoneBlock, 1, 7));
		GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 7), PAT_SLAB, '#', new ItemStack(TSContent.limestoneBlock, 1, 8));

		TConstructRegistry.getTableCasting().addCastingRecipe(new ItemStack(TSContent.materialsTS, 1, 1), new FluidStack(fluid, INGOT_LIQUID_VALUE), new ItemStack(TinkerSmeltery.metalPattern), false, 25);
		TConstructRegistry.getBasinCasting().addCastingRecipe(new ItemStack(TSContent.limestoneBlock), new FluidStack(fluid, INGOT_LIQUID_VALUE * 4), 100);

		final Detailing chiseling = TConstructRegistry.getChiselDetailing();
		chiseling.addDetailing(TSContent.limestoneBlock, 2, TSContent.limestoneBlock, 3, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.limestoneBlock, 3, TSContent.limestoneBlock, 4, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.limestoneBlock, 4, TSContent.limestoneBlock, 5, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.limestoneBlock, 5, TSContent.limestoneBlock, 6, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.limestoneBlock, 6, TSContent.limestoneBlock, 7, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.limestoneBlock, 7, TSContent.limestoneBlock, 8, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.limestoneBlock, 8, TSContent.limestoneBlock, 3, TinkerTools.chisel);
	}

	public static void craftStone() {
		final String fluidName = "Stone";
		final Fluid fluid = TinkerSmeltery.moltenStoneFluid;
		final ISmeltingRegistry advancedSmelting = ISmeltingRegistry.INSTANCE;

		advancedSmelting.addMeltable(new ItemStack(Blocks.stone), false, getFluidTempMod(fluidName), new FluidStack(fluid, INGOT_LIQUID_VALUE / 18));
		advancedSmelting.addMeltable(new ItemStack(Blocks.cobblestone), false, getFluidTempMod(fluidName), new FluidStack(fluid, INGOT_LIQUID_VALUE / 18));
		advancedSmelting.addMeltable(new ItemStack(TinkerTools.craftedSoil, 1, 1), false, getFluidTempMod(fluidName), new FluidStack(fluid, INGOT_LIQUID_VALUE / 4));

		Smeltery.addMelting(FluidType.getFluidType(fluidName), new ItemStack(TinkerTools.materials, 1, 2), 0, INGOT_LIQUID_VALUE);
		advancedSmelting.addMeltable(new ItemStack(TinkerTools.materials, 1, 2), false, getFluidTempMod(fluidName), new FluidStack(fluid, INGOT_LIQUID_VALUE));

		final String[] dyes = new String[] { "dyeWhite", "dyeOrange", "dyeMagenta", "dyeLightBlue", "dyeYellow", "dyeLime", "dyePink", "dyeGray", "dyeLightGray", "dyeCyan", "dyePurple", "dyeBlue", "dyeBrown", "dyeGreen", "dyeRed", "dyeBlack" };

		for (int i = 0; i < dyes.length; i++) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TSContent.cementBlock, 8, i),
					"ccc",
					"cdc",
					"ccc",
					'c', TSContent.cementBlock,
					'd', dyes[i]
			));
		}

		for (int meta = 2; meta <= 11; meta ++) {
			if (meta == 3)
				continue;

			Smeltery.addMelting(new ItemStack(TinkerSmeltery.smeltery, 1, meta), getFluidTempMod(fluidName), new FluidStack(fluid, INGOT_LIQUID_VALUE));
			advancedSmelting.addMeltable(new ItemStack(TinkerSmeltery.smeltery, 1, meta), false, getFluidTempMod(fluidName), new FluidStack(fluid, INGOT_LIQUID_VALUE));
		}
	}

	public static void craftStorageBlocks() {
		GameRegistry.addRecipe(ModsData.Shared.charcoalBlock, PAT_BLOCK, '#', new ItemStack(Items.coal, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TSContent.dustStorageBlock, 1, 0), PAT_BLOCK, '#', new ItemStack(Items.gunpowder, 1));
		GameRegistry.addRecipe(new ItemStack(TSContent.dustStorageBlock, 1, 1), PAT_BLOCK, '#', new ItemStack(Items.sugar, 1));
		GameRegistry.addRecipe(new ItemStack(Items.coal, 9, 1), "#", '#', ModsData.Shared.charcoalBlock);
		GameRegistry.addRecipe(new ItemStack(Items.gunpowder, 9), "#", '#', new ItemStack(TSContent.dustStorageBlock, 1, 0));
		GameRegistry.addRecipe(new ItemStack(Items.sugar, 9), "#", '#', new ItemStack(TSContent.dustStorageBlock, 1, 1));
	}

	public static void craftSteel() {
		if (ConfigCore.enableSteelArmor) {
			final ItemStack ingotSteel = TConstructRegistry.getItemStack("ingotSteel");

			GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.helmetSteel, PAT_HEAD, '#', ingotSteel));
			GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.chestplateSteel, PAT_CHEST, '#', ingotSteel));
			GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.leggingsSteel, PAT_LEGS, '#', ingotSteel));
			GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.bootsSteel, PAT_BOOTS, '#', ingotSteel));
		}

		String[] oxidizers = {"dustGunpowder", "dustSulphur", "dustSulfur", "dustSaltpeter", "dustCoal"};
		String[] reducers = {"dustRedstone", "dustManganese", "dustAluminum", "dustAluminium"};
		String[] purifiers = {"blockSand"};

		FluidStack steel = new FluidStack(TinkerSmeltery.moltenSteelFluid, 1000);
		for (String o : oxidizers)
			for (String r : reducers)
				for (String p : purifiers)
					IMixerRegistry.INSTANCE.registerMix(steel, TinkerSmeltery.moltenIronFluid, o, r, p);
	}

	public static void craftPigIron() {
		IMixerRegistry.INSTANCE.registerMix(new FluidStack(TinkerSmeltery.pigIronFluid, 1000), TinkerSmeltery.moltenIronFluid, "dustSugar", "dyeWhite", "hambone");
	}

	public static void craftGlass() {
		final String fluidName = "Glass";
		final Fluid glassFluid = TinkerSmeltery.moltenGlassFluid;
		final ISmeltingRegistry advancedSmelting = ISmeltingRegistry.INSTANCE;

		advancedSmelting.addMeltable(new ItemStack(Blocks.sand), false, getFluidTempMod(fluidName), new FluidStack(glassFluid, FluidContainerRegistry.BUCKET_VOLUME));
		advancedSmelting.addMeltable(new ItemStack(Blocks.glass), false, getFluidTempMod(fluidName), new FluidStack(glassFluid, FluidContainerRegistry.BUCKET_VOLUME));
		advancedSmelting.addMeltable(new ItemStack(Blocks.glass_pane), false, getFluidTempMod(fluidName), new FluidStack(glassFluid, 250));

		advancedSmelting.addMeltable(new ItemStack(TinkerSmeltery.clearGlass), false, getFluidTempMod(fluidName), new FluidStack(glassFluid, 1000));
		advancedSmelting.addMeltable(new ItemStack(TinkerSmeltery.glassPane), false, getFluidTempMod(fluidName), new FluidStack(glassFluid, 250));
	}

	public static void craftWater() {
		final String fluidName = "Water";
		final Fluid fluidWater = FluidRegistry.WATER;
		final ISmeltingRegistry advancedSmelting = ISmeltingRegistry.INSTANCE;

		advancedSmelting.addMeltable(new ItemStack(Blocks.ice), false, getFluidTempMod(fluidName), new FluidStack(fluidWater, 1000));
		advancedSmelting.addMeltable(new ItemStack(Blocks.snow), false, getFluidTempMod(fluidName), new FluidStack(fluidWater, 500));
		advancedSmelting.addMeltable(new ItemStack(Items.snowball), false, getFluidTempMod(fluidName), new FluidStack(fluidWater, 125));
	}

	public static void changeAnvil() {
		RecipeRemover.removeShapedRecipe(new ItemStack(Blocks.anvil));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.anvil), "bbb", " i ", "iii", 'i', "ingotSteel", 'b', "blockSteel"));

		FluidType steel = FluidType.getFluidType("Steel");
		Smeltery.addMelting(steel, new ItemStack(Blocks.anvil, 1, 0), 0, INGOT_LIQUID_VALUE * 31);
		Smeltery.addMelting(steel, new ItemStack(Blocks.anvil, 1, 1), 0, INGOT_LIQUID_VALUE * 31);
		Smeltery.addMelting(steel, new ItemStack(Blocks.anvil, 1, 2), 0, INGOT_LIQUID_VALUE * 31);
	}

	public static void changeFlintAndSteel() {
		RecipeRemover.removeShapedRecipe(new ItemStack(Items.flint_and_steel));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.flint_and_steel), "s ", " f", 's', "ingotSteel", 'f', new ItemStack(Items.flint)));

		Items.flint_and_steel.setMaxDamage(128);

		FluidType steel = FluidType.getFluidType("Steel");
		Smeltery.addMelting(steel, new ItemStack(Items.flint_and_steel, 1, 0), 0, INGOT_LIQUID_VALUE);
	}

	public static void changePiston() {
		final ItemStack rod = new ItemStack(TinkerTools.toughRod, 1, 2);

		RecipeRemover.removeAnyRecipe(new ItemStack(Blocks.piston));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.piston), "WWW", "CTC", "CRC", 'C', "cobblestone", 'T', rod, 'R', "dustRedstone", 'W', "plankWood"));
	}

	public static int getFluidTempMod(String fluidName) {
		switch (fluidName) {
			case "Water":
				return 10;
			case "Iron":
				return 913;
			case "Gold":
				return 663;
			case "Tin":
				return -163;
			case "Copper":
				return 534;
			case "Aluminum":
				return 310;
			case "NaturalAluminum":
				return 310;
			case "Cobalt":
				return 845;
			case "Ardite":
				return 910;
			case "AluminumBrass":
				return 305;
			case "Alumite":
				return -129;
			case "Manyullyn":
				return 534;
			case "Bronze":
				return 380;
			case "Steel":
				return 840;
			case "Nickel":
				return 1053;
			case "Lead":
				return -73;
			case "Silver":
				return 563;
			case "Platinum":
				return 1370;
			case "Invar":
				return 840;
			case "Electrum":
				return 663;
			case "Obsidian":
				return 330;
			case "Ender":
				return 0;
			case "Glass":
				return 975;
			case "Stone":
				return 600;
			case "Emerald":
				return 1025;
			case "Slime":
				return 0;
			case "PigIron":
				return 983;
			case "Glue":
				return 0;
			default:
				return 0;
		}
	}
}
