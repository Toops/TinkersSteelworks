package toops.tsteelworks.common.plugins.tconstruct;

import net.minecraftforge.fluids.FluidStack;
import tconstruct.library.crafting.FluidType;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;
import tconstruct.world.TinkerWorld;
import toops.tsteelworks.TSteelworks;
import toops.tsteelworks.common.plugins.ModCompatPlugin;
import toops.tsteelworks.lib.ModsData.Fluids;

/**
 * This is full of hacks to ensure TConstruct compatibility without having a hard dep on it.
 */
public class TConstructPlugin extends ModCompatPlugin {
	private TCSmeltery smelteryPlugin;
	private TCTools toolsPlugin;
	private TCWorld worldPlugin;

	public boolean isSmelteryLoaded() {
		return smelteryPlugin != null;
	}

	public boolean isToolsLoaded() {
		return toolsPlugin != null;
	}

	public boolean isWorldLoaded() {
		return worldPlugin != null;
	}

	@Override
	public String getModId() {
		return "TConstruct";
	}

	@Override
	public void preInit() {
		if (TinkerSmeltery.smeltery != null)
			smelteryPlugin = new TCSmeltery();

		if (TinkerTools.binding != null)
			toolsPlugin = new TCTools();

		if (TinkerWorld.oreBerry != null)
			worldPlugin = new TCWorld();

		if (isSmelteryLoaded())
			smelteryPlugin.preInit();

		if (isToolsLoaded())
			toolsPlugin.preInit();

		if (isWorldLoaded())
			worldPlugin.preInit();

		FluidType.registerFluidType("Limestone", Fluids.moltenLimestone, 0, Fluids.moltenLimestoneFluid.getTemperature(), Fluids.moltenLimestoneFluid, false);
	}

	@Override
	public void init() {
		if (isSmelteryLoaded())
			smelteryPlugin.init();
	}

	@Override
	public void postInit() {
		if (isSmelteryLoaded())
			smelteryPlugin.postInit();
	}

	public static boolean registerAlloy(FluidStack input1, FluidStack input2, FluidStack output) {
		TConstructPlugin self = TSteelworks.Plugins.TConstruct;

		if (!self.mayLoad() || !self.isSmelteryLoaded()) return false;

		self.smelteryPlugin.registerAlloy(input1, input2, output);

		return true;
	}
}
