package toops.tsteelworks.common.core;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import nf.fr.ephys.cookiecore.helpers.RegistryHelper;
import toops.tsteelworks.api.highoven.IFuelRegistry;
import toops.tsteelworks.api.highoven.IMixerRegistry;
import toops.tsteelworks.api.highoven.ISmeltingRegistry;
import toops.tsteelworks.common.blocks.HighOvenBlock;
import toops.tsteelworks.common.plugins.tconstruct.TConstructPlugin;
import toops.tsteelworks.lib.ModsData;

public class TSRecipes {
	/**
	 * Common craft patterns
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

	public static int INGOT_LIQUID_VALUE = 144;
	public static int BLOCK_LIQUID_VALUE = INGOT_LIQUID_VALUE * 4;
	public static int NUGGET_LIQUID_VALUE = INGOT_LIQUID_VALUE / 9;

	public static void setupRecipes() {
		addHighOvenSmelts();
		addHighOvenFuels();
		createAlloys();

		createRecipes();
	}

	private static void addHighOvenFuels() {
		IFuelRegistry.INSTANCE.addFuel(new ItemStack(Items.coal, 1, 1), 140, 4);

		for (final ItemStack charcoalBlock : OreDictionary.getOres("blockCharcoal")) {
			IFuelRegistry.INSTANCE.addFuel(charcoalBlock, 1400, 7);
		}

		for (final ItemStack coalCokeBlock : OreDictionary.getOres("blockFuelCoke")) {
			IFuelRegistry.INSTANCE.addFuel(coalCokeBlock, 2800, 15);
		}

		for (final ItemStack coalCoke : OreDictionary.getOres("fuelCoke")) {
			IFuelRegistry.INSTANCE.addFuel(coalCoke, 280, 10);
		}
	}

	public static void addHighOvenSmelts() {
		ISmeltingRegistry.INSTANCE.addDictionaryMeltable("blockLimestone", new FluidStack(ModsData.Fluids.moltenLimestoneFluid, INGOT_LIQUID_VALUE * 4), 825);
		ISmeltingRegistry.INSTANCE.addDictionaryMeltable("limestone", new FluidStack(ModsData.Fluids.moltenLimestoneFluid, INGOT_LIQUID_VALUE * 4), 825);

		final FluidStack moltenLimestone = new FluidStack(ModsData.Fluids.moltenLimestoneFluid, INGOT_LIQUID_VALUE);
		final ItemStack limestoneBlock = new ItemStack(TSContent.limestoneBlock, 1, 0);
		final ItemStack limestoneBrickItem = new ItemStack(TSContent.materialsTS, 1, 1);
		final ItemStack limestoneDustItem = new ItemStack(TSContent.materialsTS, 1, 2);

		TConstructPlugin.registerSmelteryItemRenderer(limestoneBrickItem, limestoneBlock);
		ISmeltingRegistry.INSTANCE.addMeltable(limestoneBrickItem, false, moltenLimestone, 825);

		TConstructPlugin.registerSmelteryItemRenderer(limestoneDustItem, limestoneBlock);
		ISmeltingRegistry.INSTANCE.addMeltable(limestoneDustItem, false, moltenLimestone, 825);
	}

	public static void createAlloys() {
		TConstructPlugin.registerAlloy(
				FluidRegistry.getFluidStack("stone.seared", 1),
				new FluidStack(TSContent.moltenLimestoneFluid, 1),
				new FluidStack(TSContent.liquidCementFluid, 1)
		);
	}

	public static void createRecipes() {
		craftScorchedStone();
		craftLimestone();
		craftStorageBlocks();
		craftSteel();
		craftMachines();
		craftQuartz();

		TSRecipes.changeFlintAndSteel();
		TSRecipes.changeAnvil();
	}

	public static void craftMachines() {
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TSContent.steamTurbine), "aca", "#d#", "#r#", '#', "ingotBronze", 'a', "ingotAluminumBrass", 'c', "ingotSteel", 'r', new ItemStack(Items.redstone), 'd', new ItemStack(Blocks.piston)));
	}

	public static void craftScorchedStone() {
		final ItemStack itemScorchedBrick = new ItemStack(TSContent.materialsTS, 1, 0);
		final ItemStack blockScorchedBrick = new ItemStack(TSContent.highoven, 1, 2);

		// High Oven / Deep Tank Components
		GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_HIGHOVEN), PAT_HOLLOW, '#', itemScorchedBrick);
		GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_DRAIN), "b b", "b b", "b b", 'b', itemScorchedBrick);
		GameRegistry.addRecipe(blockScorchedBrick, PAT_SMALL_BLOCK, '#', itemScorchedBrick);
		GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_DUCT), "bbb", "   ", "bbb", 'b', itemScorchedBrick);
		GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_TANK), PAT_SURROUND, '#', itemScorchedBrick, 'm', new ItemStack(Items.dye, 1, 4));
		GameRegistry.addSmelting(new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_COBBLE), new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_STONE), 2);
		GameRegistry.addSmelting(new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_BRICK), new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_CRACKED), 2);

		// Slabs
		GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 0), PAT_SLAB, '#', new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_BRICK));
		GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 1), PAT_SLAB, '#', new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_CREEPY));
		GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 2), PAT_SLAB, '#', new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_STONE));
		GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 3), PAT_SLAB, '#', new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_COBBLE));
		GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 4), PAT_SLAB, '#', new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_PAVER));
		GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 5), PAT_SLAB, '#', new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_CRACKED));
		GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 6), PAT_SLAB, '#', new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_ROAD));
		GameRegistry.addRecipe(new ItemStack(TSContent.scorchedSlab, 6, 7), PAT_SLAB, '#', new ItemStack(TSContent.highoven, 1, HighOvenBlock.META_CHISELED));

		// Recipes to obtain bricks from high oven
		final Fluid moltenStoneFluid = FluidRegistry.getFluid("stone.seared");
		if (moltenStoneFluid != null) {
			final String[] oxidizers = {"fuelCoal", "coal", "dustCoal"};
			final String[] purifiers = {"blockSand", "Sandblock", "sand"};

			for (final String oxidizer : oxidizers) {
				for (final String purifier : purifiers) {
					IMixerRegistry.INSTANCE.registerMix(null, itemScorchedBrick, moltenStoneFluid, oxidizer, null, purifier);
				}
			}
		}
	}

	public static void craftLimestone() {
		GameRegistry.addRecipe(new ItemStack(TSContent.limestoneBlock, 1, 2), PAT_SMALL_BLOCK, '#', new ItemStack(TSContent.materialsTS, 1, 1));

		// add to smelt
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(TSContent.limestoneBlock, 1, 1), new ItemStack(TSContent.limestoneBlock, 1, 0), 2f);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(TSContent.limestoneBlock, 1, 0), new ItemStack(TSContent.materialsTS, 4, 1), 2f);

		GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 0), PAT_SLAB, '#', new ItemStack(TSContent.limestoneBlock, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 1), PAT_SLAB, '#', new ItemStack(TSContent.limestoneBlock, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 2), PAT_SLAB, '#', new ItemStack(TSContent.limestoneBlock, 1, 2));
		GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 3), PAT_SLAB, '#', new ItemStack(TSContent.limestoneBlock, 1, 4));
		GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 4), PAT_SLAB, '#', new ItemStack(TSContent.limestoneBlock, 1, 5));
		GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 5), PAT_SLAB, '#', new ItemStack(TSContent.limestoneBlock, 1, 6));
		GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 6), PAT_SLAB, '#', new ItemStack(TSContent.limestoneBlock, 1, 7));
		GameRegistry.addRecipe(new ItemStack(TSContent.limestoneSlab, 6, 7), PAT_SLAB, '#', new ItemStack(TSContent.limestoneBlock, 1, 8));

		Fluid moltenStoneFluid = FluidRegistry.getFluid("stone.molten");

		if (moltenStoneFluid != null)
			IMixerRegistry.INSTANCE.registerMix(null, new ItemStack(TSContent.materialsTS, 1, 1), moltenStoneFluid, "dyeLime", null, "blockSand");
	}

	private static void craftQuartz() {
		Fluid moltenGlass = FluidRegistry.getFluid("glass.molten");
		if (moltenGlass == null) return;

		final ItemStack netherQuartz = new ItemStack(Items.quartz, 2);
		IMixerRegistry.INSTANCE.registerMix(null, netherQuartz, moltenGlass, "dustGunpowder", "oreberryEssence", "blockGraveyardDirt");
	}

	public static void craftStorageBlocks() {
		ItemStack charcoal = new ItemStack(TSContent.tsCharcoalBlock);

		GameRegistry.addRecipe(charcoal, PAT_BLOCK, '#', new ItemStack(Items.coal, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TSContent.dustStorageBlock, 1, 0), PAT_BLOCK, '#', new ItemStack(Items.gunpowder, 1));
		GameRegistry.addRecipe(new ItemStack(TSContent.dustStorageBlock, 1, 1), PAT_BLOCK, '#', new ItemStack(Items.sugar, 1));
		GameRegistry.addRecipe(new ItemStack(Items.coal, 9, 1), "#", '#', charcoal);
		GameRegistry.addRecipe(new ItemStack(Items.gunpowder, 9), "#", '#', new ItemStack(TSContent.dustStorageBlock, 1, 0));
		GameRegistry.addRecipe(new ItemStack(Items.sugar, 9), "#", '#', new ItemStack(TSContent.dustStorageBlock, 1, 1));
	}

	public static void craftSteel() {
		if (ConfigCore.enableSteelArmor) {
			final String ingotSteel = "ingotSteel";

			GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.helmetSteel, PAT_HEAD, '#', ingotSteel));
			GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.chestplateSteel, PAT_CHEST, '#', ingotSteel));
			GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.leggingsSteel, PAT_LEGS, '#', ingotSteel));
			GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.bootsSteel, PAT_BOOTS, '#', ingotSteel));
		}

		Fluid moltenSteel = FluidRegistry.getFluid("steel.molten");
		Fluid moltenIron = FluidRegistry.getFluid("iron.molten");

		if (moltenSteel == null) return;

		String[] oxidizers = {"dustGunpowder", "dustSulphur", "dustSulfur", "dustSaltpeter", "dustCoal"};
		String[] reducers = {"dustRedstone", "dustManganese", "dustAluminum", "dustAluminium"};
		String[] purifiers = {"blockSand"};

		FluidStack steel = new FluidStack(moltenSteel, 1000);
		for (String o : oxidizers)
			for (String r : reducers)
				for (String p : purifiers)
					IMixerRegistry.INSTANCE.registerMix(steel, null, moltenIron, o, r, p);
	}

	public static void changeAnvil() {
		if (!ConfigCore.hardcoreAnvil) return;

		ItemStack anvil = new ItemStack(Blocks.anvil);

		RegistryHelper.removeItemRecipe(anvil);
		GameRegistry.addRecipe(new ShapedOreRecipe(anvil, "bbb", " i ", "iii", 'i', "ingotSteel", 'b', "blockSteel"));

		FluidStack steel = FluidRegistry.getFluidStack("steel.molten", INGOT_LIQUID_VALUE * 31);
		if (steel == null) {
			TSLogger.warning("steel.molten fluid does not exist, anvils won't be meltable I'm afraid");
		}

		for (int i = 0; i < 3; i++) {
			ISmeltingRegistry.INSTANCE.addMeltable(new ItemStack(Blocks.anvil, 1, i), false, steel, 840);
		}
	}

	public static void changeFlintAndSteel() {
		if (!ConfigCore.hardcoreFlintAndSteel) return;

		ItemStack flintAndSteel = new ItemStack(Items.flint_and_steel, 1, 0);
		RegistryHelper.removeItemRecipe(flintAndSteel);
		GameRegistry.addRecipe(new ShapedOreRecipe(flintAndSteel, "s ", " f", 's', "ingotSteel", 'f', Items.flint));

		Items.flint_and_steel.setMaxDamage(128);

		FluidStack steel = FluidRegistry.getFluidStack("steel.molten", INGOT_LIQUID_VALUE);
		if (steel == null) {
			TSLogger.warning("steel.molten fluid does not exist, flint will not be meltable I'm afraid");
		}

		ISmeltingRegistry.INSTANCE.addMeltable(flintAndSteel, false, steel, 840);
	}
}
