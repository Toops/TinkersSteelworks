package toops.tsteelworks.common.plugins.tconstruct;

import cpw.mods.fml.common.registry.GameRegistry;
import mantle.Mantle;
import mantle.utils.ItemMetaWrapper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import nf.fr.ephys.cookiecore.helpers.InventoryHelper;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.Smeltery;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;
import toops.tsteelworks.api.event.IRegistry;
import toops.tsteelworks.api.event.IRegistryListener;
import toops.tsteelworks.api.highoven.IMixerRegistry;
import toops.tsteelworks.api.highoven.ISmeltingRegistry;
import toops.tsteelworks.common.core.ConfigCore;
import toops.tsteelworks.common.core.TSContent;
import toops.tsteelworks.common.core.TSRecipes;
import toops.tsteelworks.lib.ModsData.Fluids;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class TCSmeltery {
	private List<Alloy> alloys = new ArrayList<>();
	private IRegistryListener<ItemStack, ISmeltingRegistry.IMeltData> smeltListener = new IRegistryListener<ItemStack, ISmeltingRegistry.IMeltData>() {
		@Override
		public void onRegistryChange(IRegistry.IRegistryEvent<ItemStack, ISmeltingRegistry.IMeltData> event) {
			ItemStack stack = event.getItem();
			ISmeltingRegistry.IMeltData meltData = event.getData();

 			// todo: internal (non API as it is only used for this plugin) registry to change from Item ItemStack -> Block ItemStack (like ironIngot -> ironBlock)
			Block block = Blocks.iron_ore;
			if (stack.getItem() instanceof ItemBlock)
				block = ((ItemBlock) stack.getItem()).field_150939_a;

			Smeltery.addMelting(stack, block, stack.getItemDamage(), meltData.getMeltingPoint(), meltData.getResult());
		}
	};

	public void preInit() {
		adjustFluidValues();
		// copy high oven smelting list to smeltery smelting list
		ISmeltingRegistry.INSTANCE.addEventListener(smeltListener);
	}

	public void init() {
		registerAlloysDiffer();
		craftPigIron();
		craftManual();
		registerCasting();
		meltSeared();
	}

	public void postInit() {
		ISmeltingRegistry.INSTANCE.removeEventListener(smeltListener);
		// copy smeltery smelting list to high oven smelting list
		copySmeltingList();
	}

	private void adjustFluidValues() {
		TSRecipes.INGOT_LIQUID_VALUE = TConstruct.ingotLiquidValue;
		TSRecipes.NUGGET_LIQUID_VALUE = TConstruct.nuggetLiquidValue;
		TSRecipes.BLOCK_LIQUID_VALUE = TConstruct.blockLiquidValue;
		TSRecipes.ORE_LIQUID_VALUE = (int) Math.round(TSRecipes.INGOT_LIQUID_VALUE * ConfigCore.ingotsPerOre);
	}

	private void copySmeltingList() {
		Map<ItemMetaWrapper, FluidStack> smeltingList = Smeltery.getSmeltingList();
		Map<ItemMetaWrapper, Integer> temperatureList = Smeltery.getTemperatureList();

		ISmeltingRegistry localRegistry = ISmeltingRegistry.INSTANCE;

		for (Map.Entry<ItemMetaWrapper, FluidStack> set : smeltingList.entrySet()) {
			ItemStack key = new ItemStack(set.getKey().item, 1, set.getKey().meta);

			boolean isOre = InventoryHelper.itemIsOre(key);

			localRegistry.addMeltable(key, isOre, set.getValue().copy(), temperatureList.get(set.getKey()));
		}
	}

	private void craftPigIron() {
		IMixerRegistry.INSTANCE.registerMix(new FluidStack(TinkerSmeltery.pigIronFluid, 1000), TinkerSmeltery.moltenIronFluid, "dustSugar", "dyeWhite", "hambone");
	}

	private void registerCasting() {
		final LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();
		final LiquidCasting tableCasting = TConstructRegistry.getTableCasting();

		final ItemStack itemScorchedBrick = new ItemStack(TSContent.materialsTS, 1, 0);
		final ItemStack blockScorchedBrick = new ItemStack(TSContent.highoven, 1, 2);
		tableCasting.addCastingRecipe(itemScorchedBrick, new FluidStack(TinkerSmeltery.moltenStoneFluid, 8), new ItemStack(Items.brick), true, 50);
		basinCasting.addCastingRecipe(blockScorchedBrick, new FluidStack(TinkerSmeltery.moltenStoneFluid, 32), new ItemStack(Blocks.brick_block), true, 100);

		final Fluid limestoneFluid = TSContent.moltenLimestoneFluid;
		tableCasting.addCastingRecipe(new ItemStack(TSContent.materialsTS, 1, 1), new FluidStack(limestoneFluid, TSRecipes.INGOT_LIQUID_VALUE), new ItemStack(TinkerSmeltery.metalPattern), false, 25);
		basinCasting.addCastingRecipe(new ItemStack(TSContent.limestoneBlock), new FluidStack(limestoneFluid, TSRecipes.INGOT_LIQUID_VALUE * 4), 100);

		ItemStack bucket = new ItemStack(Items.bucket);
		tableCasting.addCastingRecipe(Fluids.bucketSteam, new FluidStack(Fluids.steamFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);
		tableCasting.addCastingRecipe(Fluids.bucketLimestone, new FluidStack(Fluids.moltenLimestoneFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);
		tableCasting.addCastingRecipe(Fluids.bucketCement, new FluidStack(Fluids.liquidCementFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);
	}

	/**
	 * We only need the manual if TiCon is available otherwise it's going to be full of lies
	 */
	private void craftManual() {
		final ItemStack manual = new ItemStack(TSContent.bookManual, 1, 0);

		final LiquidCasting tableCasting = TConstructRegistry.getTableCasting();
		final FluidStack fluidStoneMinor = new FluidStack(TinkerSmeltery.moltenStoneFluid, 8);

		tableCasting.addCastingRecipe(manual, fluidStoneMinor, new ItemStack(TinkerTools.manualBook, 1, 0), true, 50);
		tableCasting.addCastingRecipe(manual, fluidStoneMinor, new ItemStack(TinkerTools.manualBook, 1, 1), true, 50);
		tableCasting.addCastingRecipe(manual, fluidStoneMinor, new ItemStack(TinkerTools.manualBook, 1, 2), true, 50);
		tableCasting.addCastingRecipe(manual, fluidStoneMinor, new ItemStack(TinkerTools.manualBook, 1, 3), true, 50);

		tableCasting.addCastingRecipe(manual, fluidStoneMinor, new ItemStack(Mantle.mantleBook, 1), true, 50);
		tableCasting.addCastingRecipe(manual, fluidStoneMinor, new ItemStack(Items.book, 1), true, 50);
	}

	public void registerAlloy(FluidStack input1, FluidStack input2, FluidStack output) {
		alloys.add(new Alloy(input1, input2, output));
	}

	private void registerAlloysDiffer() {
		for (Alloy alloy : alloys) {
			Smeltery.addAlloyMixing(alloy.output, alloy.f1, alloy.f2);
		}
	}

	private void meltSeared() {
		final Fluid fluid = TinkerSmeltery.moltenStoneFluid;
		final ISmeltingRegistry advancedSmelting = ISmeltingRegistry.INSTANCE;

		advancedSmelting.addMeltable(new ItemStack(TinkerTools.materials, 1, 2), false, new FluidStack(fluid, TSRecipes.INGOT_LIQUID_VALUE), 600);

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

			advancedSmelting.addMeltable(new ItemStack(TinkerSmeltery.smeltery, 1, meta), false, new FluidStack(fluid, TSRecipes.INGOT_LIQUID_VALUE), 600);
		}
	}

	private static class Alloy {
		private FluidStack f1;
		private FluidStack f2;
		private FluidStack output;

		public Alloy(FluidStack f1, FluidStack f2, FluidStack f3) {
			this.f1 = f1;
			this.f2 = f2;
			this.output = f3;
		}
	}
}
