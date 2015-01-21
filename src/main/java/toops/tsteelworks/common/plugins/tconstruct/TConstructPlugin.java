package toops.tsteelworks.common.plugins.tconstruct;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import nf.fr.ephys.cookiecore.helpers.InventoryHelper;
import tconstruct.library.crafting.FluidType;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;
import toops.tsteelworks.api.highoven.ISmeltingRegistry;
import toops.tsteelworks.common.plugins.ModCompatPlugin;

/**
 * This is full of hacks to ensure TConstruct compatibility without having a hard dep on it.
 */
public class TConstructPlugin extends ModCompatPlugin {
	private static boolean smelteryLoaded;
	private static boolean toolsLoaded;

	public static boolean isSmelteryLoaded() {
		return smelteryLoaded;
	}

	public static boolean isToolsLoaded() {
		return toolsLoaded;
	}

	@Override
	public String getModId() {
		return "TConstruct";
	}

	@Override
	public String getPluginName() {
		return "TConstruct";
	}

	@Override
	public void preInit() {
		smelteryLoaded = TinkerSmeltery.smeltery != null;
		toolsLoaded = TinkerTools.binding != null;

		if (smelteryLoaded)
			TCSmeltery.preInit();
	}

	@Override
	public void init() {
		if (smelteryLoaded)
			TCSmeltery.init();
	}

	@Override
	public void postInit() {}

	public static void addDictionaryMeltable(String ore, String tcFluidName, int temp, int amount, Fluid fluid) {
		if (smelteryLoaded) {
			TCSmeltery.addDictionaryMeltable(ore, tcFluidName, temp, amount);

			if (fluid == null)
				fluid = TCSmeltery.getFluidForType(tcFluidName);
		}

		if (fluid == null) return;

		ISmeltingRegistry.INSTANCE.addDictionaryMeltable(ore, new FluidStack(fluid, amount), temp);
	}

	public static void addMeltable(ItemStack is, String tcFluidName, int temp, int amount, Fluid fluid) {
		if (smelteryLoaded) {
			TCSmeltery.addMeltable(is, tcFluidName, temp, amount);

			if (fluid == null)
				fluid = TCSmeltery.getFluidForType(tcFluidName);
		}

		if (fluid == null) return;

		ISmeltingRegistry.INSTANCE.addMeltable(is, InventoryHelper.itemIsOre(is), new FluidStack(fluid, amount), temp);
	}
}
