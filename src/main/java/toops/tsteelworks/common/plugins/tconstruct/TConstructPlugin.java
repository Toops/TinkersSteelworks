package toops.tsteelworks.common.plugins.tconstruct;

import net.minecraftforge.fluids.FluidStack;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;
import toops.tsteelworks.TSteelworks;
import toops.tsteelworks.common.plugins.ModCompatPlugin;

/**
 * This is full of hacks to ensure TConstruct compatibility without having a hard dep on it.
 */
public class TConstructPlugin extends ModCompatPlugin {
	private TCSmeltery smelteryPlugin;
	private TCTools toolsPlugin;

	public boolean isSmelteryLoaded() {
		return smelteryPlugin != null;
	}

	public boolean isToolsLoaded() {
		return toolsPlugin != null;
	}

	public TCSmeltery getSmelteryPlugin() {
		return smelteryPlugin;
	}

	public TCTools getToolsPlugin() {
		return toolsPlugin;
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

		if (isSmelteryLoaded())
			smelteryPlugin.preInit();

		if (isToolsLoaded())
			toolsPlugin.preInit();
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

		if (!self.isSmelteryLoaded()) return false;

		self.getSmelteryPlugin().registerAlloy(input1, input2, output);

		return true;
	}
}
