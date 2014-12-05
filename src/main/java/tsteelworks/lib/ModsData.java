package tsteelworks.lib;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import tsteelworks.common.blocks.CementFluidBlock;
import tsteelworks.common.blocks.TSBaseBlock;
import tsteelworks.common.blocks.TSFluidBlock;
import tsteelworks.common.core.TSContent;
import tsteelworks.common.core.TSLogger;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModsData {
	/**
	 * Anything fluid
	 */
	public static class Fluids {
		public static Fluid steamFluid;
		public static ItemStack bucketSteam;
		public static Block steamBlock;

		public static Fluid moltenLimestoneFluid;
		public static ItemStack bucketLimestone;
		public static Block moltenLimestone;

		public static Fluid liquidCementFluid;
		public static ItemStack bucketCement;
		public static Block liquidCement;
	}

	/**
	 * Anything that can also be added by other mods, includes oredict things
	 */
	public static class Shared {
		public static ItemStack charcoalBlock;
	}

	/**
	 * Anything added by thaumcraft
	 */
	public static class Thaumcraft {
		public static boolean isLoaded;

		public static ItemStack alumentum;
	}

	/**
	 * Anything added by TE
	 */
	public static class ThermalExpansion {
		public static boolean isLoaded;

		public static ItemStack slag;
	}

	/**
	 * Anything added by Railcraft
	 */
	public static class Railcraft {
		public static boolean isLoaded;

		public static ItemStack coalCoke;
		public static ItemStack coalCokeBlock;
	}

	public static void loadModsData() {
		Thaumcraft.isLoaded = Loader.isModLoaded("Thaumcraft");
		if (Thaumcraft.isLoaded) {
			TSLogger.info("Thaumcraft detected. Registering fuels.");

			Thaumcraft.alumentum = RegistryHelper.getItemStack("Thaumcraft:ItemResource@0");
		}

		ThermalExpansion.isLoaded = Loader.isModLoaded("ThermalExpansion");
		if (ThermalExpansion.isLoaded) {
			TSLogger.info("TE detected. Getting slagged.");

			ThermalExpansion.slag = GameRegistry.findItemStack("ThermalExpansion", "slag", 1);
		}

		Railcraft.isLoaded = Loader.isModLoaded("Railcraft");
		if (Railcraft.isLoaded) {
			TSLogger.info("Railcraft detected. Coke production in progress.");

			Railcraft.coalCoke = RegistryHelper.getItemStack("Railcraft:fuel.coke@0");
			Railcraft.coalCokeBlock = RegistryHelper.getItemStack("Railcraft:tile.railcraft.cube@0");
		}
	}

	public static void loadSharedData() {
		registerFluids();

		/* Raw Vanilla Materials */
		List<ItemStack> charcoalBlocks = OreDictionary.getOres("blockCharcoal");

		if (charcoalBlocks.isEmpty()) {
			TSContent.tsCharcoalBlock = new TSBaseBlock(Material.rock, 5.0f, new String[] {"charcoal_block"}).setBlockName("tsteelworks.blocks.charcoal");
			GameRegistry.registerBlock(TSContent.tsCharcoalBlock, "blockCharcoal");

			Blocks.fire.setFireInfo(TSContent.tsCharcoalBlock, 15, 30);
			OreDictionary.registerOre("blockCharcoal", TSContent.tsCharcoalBlock);

			Shared.charcoalBlock = new ItemStack(TSContent.tsCharcoalBlock);
			GameRegistry.registerFuelHandler(new FuelHandler(Shared.charcoalBlock, 15000));
		} else {
			Shared.charcoalBlock = charcoalBlocks.get(0);
		}
	}

	private static void registerFluids() {
		LiquidCasting tableCasting = TConstructRegistry.getTableCasting();
		ItemStack bucket = new ItemStack(Items.bucket);

		/* Steam */
		Fluids.steamFluid = FluidRegistry.getFluid("steam");
		if (Fluids.steamFluid == null) {
			Fluids.steamFluid = new Fluid("steam");
			Fluids.steamFluid.setDensity(-1).setViscosity(5).setTemperature(1300).setGaseous(true);

			FluidRegistry.registerFluid(Fluids.steamFluid);

			TSContent.steamFluid = Fluids.steamFluid;
		}

		if (!Fluids.steamFluid.canBePlacedInWorld()) {
			Fluids.steamBlock = new TSFluidBlock(Fluids.steamFluid, Material.air, "liquid_steam").setBlockName("steam").setLightOpacity(0);
			GameRegistry.registerBlock(Fluids.steamBlock, "steam");
		} else {
			Fluids.steamBlock = Fluids.steamFluid.getBlock();
		}

		ItemStack filledBucket = FluidContainerRegistry.fillFluidContainer(new FluidStack(Fluids.steamFluid, 1000), bucket);
		if (filledBucket == null) {
			Item bucketSteam = new ItemBucket(Fluids.steamBlock).setTextureName("TSteelworks:materials/bucket_steam").setUnlocalizedName("tsteelworks.bucket.Steam").setCreativeTab(TSContent.creativeTab).setContainerItem(Items.bucket);
			GameRegistry.registerItem(bucketSteam, "steamBucket");

			filledBucket = new ItemStack(bucketSteam, 1, 0);
			FluidContainerRegistry.registerFluidContainer(new FluidContainerRegistry.FluidContainerData(new FluidStack(Fluids.steamFluid, 1000), filledBucket, bucket));
		}

		Fluids.bucketSteam = filledBucket;

		tableCasting.addCastingRecipe(filledBucket, new FluidStack(Fluids.steamFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);

		/* Limestone */
		Fluids.moltenLimestoneFluid = FluidRegistry.getFluid("limestone.molten");
		if (Fluids.moltenLimestoneFluid == null) {
			Fluids.moltenLimestoneFluid = new Fluid("limestone.molten").setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);

			FluidRegistry.registerFluid(Fluids.moltenLimestoneFluid);

			TSContent.moltenLimestoneFluid = Fluids.moltenLimestoneFluid;
		}

		if (!Fluids.moltenLimestoneFluid.canBePlacedInWorld()) {
			Fluids.moltenLimestone = new TSFluidBlock(Fluids.moltenLimestoneFluid, Material.lava, "liquid_limestone").setBlockName("molten.limestone");
			//Fluids.moltenLimestone.
			GameRegistry.registerBlock(Fluids.moltenLimestone, "molten.limestone");
		} else {
			Fluids.moltenLimestone = Fluids.moltenLimestoneFluid.getBlock();
		}

		filledBucket = FluidContainerRegistry.fillFluidContainer(new FluidStack(Fluids.moltenLimestoneFluid, 1000), bucket);
		if (filledBucket == null) {
			Item bucketLimestone = new ItemBucket(Fluids.moltenLimestone).setTextureName("TSteelworks:materials/bucket_limestone").setUnlocalizedName("tsteelworks.bucket.Limestone").setCreativeTab(TSContent.creativeTab).setContainerItem(Items.bucket);
			GameRegistry.registerItem(bucketLimestone, "limestoneBucket");

			filledBucket = new ItemStack(bucketLimestone, 1, 1);
			FluidContainerRegistry.registerFluidContainer(new FluidContainerRegistry.FluidContainerData(new FluidStack(Fluids.moltenLimestoneFluid, 1000), filledBucket, bucket));
		}

		Fluids.bucketLimestone = filledBucket;

		FluidType.registerFluidType("Limestone", Fluids.moltenLimestone, 0, Fluids.moltenLimestoneFluid.getTemperature(), Fluids.moltenLimestoneFluid, false);

		tableCasting.addCastingRecipe(filledBucket, new FluidStack(Fluids.moltenLimestoneFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);

		/* Cement */
		Fluids.liquidCementFluid = FluidRegistry.getFluid("cement.liquid");
		if (Fluids.liquidCementFluid == null) {
			Fluids.liquidCementFluid = new Fluid("cement.liquid").setLuminosity(0).setDensity(6000).setViscosity(6000).setTemperature(20);

			FluidRegistry.registerFluid(Fluids.liquidCementFluid);

			TSContent.liquidCementFluid = Fluids.liquidCementFluid;
		}

		if (!Fluids.liquidCementFluid.canBePlacedInWorld()) {
			Fluids.liquidCement = new CementFluidBlock(Fluids.liquidCementFluid, Material.water, "liquid_cement").setBlockName("liquid.cement");
			GameRegistry.registerBlock(Fluids.liquidCement, "liquid.cement");
		} else {
			Fluids.liquidCement = Fluids.liquidCementFluid.getBlock();
		}

		filledBucket = FluidContainerRegistry.fillFluidContainer(new FluidStack(Fluids.liquidCementFluid, 1000), bucket);
		if (filledBucket == null) {
			Item bucketCement = new ItemBucket(Fluids.liquidCement).setTextureName("TSteelworks:materials/bucket_cement").setUnlocalizedName("tsteelworks.bucket.Cement").setCreativeTab(TSContent.creativeTab).setContainerItem(Items.bucket);
			GameRegistry.registerItem(bucketCement, "cementBucket");

			filledBucket = new ItemStack(bucketCement, 1, 2);
			FluidContainerRegistry.registerFluidContainer(new FluidContainerRegistry.FluidContainerData(new FluidStack(Fluids.liquidCementFluid, 1000), filledBucket, bucket));
		}

		Fluids.bucketCement = filledBucket;

		tableCasting.addCastingRecipe(filledBucket, new FluidStack(Fluids.liquidCementFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);
	}
}