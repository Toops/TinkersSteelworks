package toops.tsteelworks.lib;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import toops.tsteelworks.common.blocks.CementFluidBlock;
import toops.tsteelworks.common.blocks.TSFluidBlock;
import toops.tsteelworks.common.core.TSContent;
import toops.tsteelworks.common.core.TSLogger;

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
	 * Anything added by TE
	 */
	public static class ThermalExpansion {
		public static boolean isLoaded;

		public static ItemStack slag;
	}

	public static void loadModsData() {
		ThermalExpansion.isLoaded = Loader.isModLoaded("ThermalExpansion");
		if (ThermalExpansion.isLoaded) {
			TSLogger.info("TE detected. Getting slagged.");

			ThermalExpansion.slag = GameRegistry.findItemStack("ThermalExpansion", "slag", 1);
		}
	}

	public static void registerFluids() {
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

		/* Limestone */
		Fluids.moltenLimestoneFluid = FluidRegistry.getFluid("limestone.molten");
		if (Fluids.moltenLimestoneFluid == null) {
			Fluids.moltenLimestoneFluid = new Fluid("limestone.molten").setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);

			FluidRegistry.registerFluid(Fluids.moltenLimestoneFluid);

			TSContent.moltenLimestoneFluid = Fluids.moltenLimestoneFluid;
		}

		if (!Fluids.moltenLimestoneFluid.canBePlacedInWorld()) {
			Fluids.moltenLimestone = new TSFluidBlock(Fluids.moltenLimestoneFluid, Material.lava, "liquid_limestone").setBlockName("molten.limestone");
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
	}
}