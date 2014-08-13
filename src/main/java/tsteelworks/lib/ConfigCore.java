package tsteelworks.lib;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import nf.fr.ephys.cookiecore.helpers.RegistryHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigCore {
	// --- Items
	public static boolean enableSteelArmor;

	// --- Misc
	public static boolean hardcorePiston;
	public static boolean hardcoreFlintAndSteel;
	public static boolean hardcoreAnvil;
	public static int ingotsPerOre;
	public static int deeptankCapacityMultiplier;
	public static boolean enableDuctVacuum;
	public static boolean enableTE3SlagOutput;
	public static ItemStack[] modTankGlassBlocks;

	// --- Worldgen
	public static boolean enableLimestoneWorldgen;

	public static void initProps(File configFile) {
		final Configuration config = new Configuration(configFile);

		config.load();

		enableSteelArmor = config.get("Equipables", "Steel Armor Enabler", true, "Enables steel armor").getBoolean(true);

		enableDuctVacuum = config.get("High Oven", "Enable Duct Vacuum", false, "Enables High Oven Ducts to suck in items like a hopper").getBoolean(false);
		ingotsPerOre = config.get("High Oven", "Ingots per ore", 2, "Number of ingots returned from smelting ores in the High Oven").getInt(2);

		enableTE3SlagOutput = config.get("High Oven", "Enable TE3 Slag Output", true, "Enables Thermal Expansion slag output by low chance, if TE3 is present").getBoolean(true);

		deeptankCapacityMultiplier = config.get("Deep Tank", "Fluid Capacity Multiplier", 4, "Determines how many buckets of fluid per internal block space").getInt(4);

		String[] items = config.get("Deep Tank", "Additional Glass Blocks", new int[] {}, "Specify blocks for additional Deep Tank walls in modname:blockname@metadata (the @metadata is optionnal, defautls to 0) format. ex: minecraft:glass (Note: Each entry must be on a seperate line)").getStringList();

		List<ItemStack> glassBlocks = new ArrayList<>();
		for (String item : items) {
			ItemStack stack = RegistryHelper.getItemStack(item);

			if (stack == null)
				TSLogger.warning(item + " is not a valid itemstack.");
			else
				glassBlocks.add(stack);
		}

		modTankGlassBlocks = glassBlocks.toArray(new ItemStack[glassBlocks.size()]);

		hardcorePiston = config.get("TConification", "Hardcore Piston", false, "Piston requires tough iron tool rod").getBoolean(false);
		hardcoreFlintAndSteel = config.get("TSteelification", "Hardcore Flint & Steel", false, "Flint & Steel requires steel ingot").getBoolean(false);
		hardcoreAnvil = config.get("TSteelification", "Hardcore Anvil", false, "Anvil requires steel materials").getBoolean(false);

		enableLimestoneWorldgen = config.get("World Generation", "Limestone", true, "Allow limestone to generate (usually under rivers and oceans)").getBoolean(true);

		if (config.hasChanged())
			config.save();
	}
}
