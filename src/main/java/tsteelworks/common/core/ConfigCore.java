package tsteelworks.common.core;

import net.minecraftforge.common.config.Configuration;
import tsteelworks.lib.DeepTankGlassTypes;

import java.io.File;

public class ConfigCore {
	// --- Items
	public static boolean enableSteelArmor;

	// --- Misc
	public static boolean hardcorePiston;
	public static boolean hardcoreFlintAndSteel;
	public static boolean hardcoreAnvil;
	public static float ingotsPerOre;
	public static boolean enableDuctVacuum;
	public static boolean enableTE3SlagOutput;

	public static int steamProductionRate;
	public static String[] blacklistedAlloys;

	/**
	 * valid tank glass types with their capacity in mB
	 *
	 * modName:blockName@metadata|capacity
	 */
	private static String[] defaultGlass = {
			"minecraft:glass|20000",
			"minecraft:stained_glass@*|20000",

			"ExtraUtilities:decorativeBlock2@0|25000", // thickened glass
			"ExtraUtilities:decorativeBlock2@1|25000",
			"ExtraUtilities:decorativeBlock2@2|25000",
			"ExtraUtilities:decorativeBlock2@3|35000", // creeper glass
			"ExtraUtilities:decorativeBlock2@4|60000", // golden edged glass
			"ExtraUtilities:decorativeBlock2@5|50000", // obsidian glass
			"ExtraUtilities:decorativeBlock2@6|25000",
			"ExtraUtilities:decorativeBlock2@7|60000", // glowstone glass
			"ExtraUtilities:decorativeBlock2@8|30000", // heart glass
			"ExtraUtilities:decorativeBlock2@9|25000",
			"ExtraUtilities:decorativeBlock2@10|50000", // dark glass

			"TConstruct:LavaTank@1|35000", // seared glass
			"TConstruct:LavaTankNether@1|35000",

			"TConstruct:GlassBlock|25000", // clear glass
			"TConstruct:GlassBlock.StainedClear@*|25000",
	};

	// --- Worldgen
	public static boolean enableLimestoneWorldgen;
	private static String[] items;

	public static void preInit(File configFile) {
		final Configuration config = new Configuration(configFile);

		config.load();

		enableSteelArmor = config.get("Equipables", "Steel Armor Enabler", true, "Enables steel armor").getBoolean(true);

		enableDuctVacuum = config.get("High Oven", "Enable Duct Vacuum", false, "Enables High Oven Ducts to suck in items like a hopper").getBoolean(false);
		ingotsPerOre = (float) config.get("High Oven", "Ingots per ore", 2.0, "Number of ingots returned from smelting ores in the High Oven").getDouble(2.0);
		enableTE3SlagOutput = config.get("High Oven", "Enable TE3 Slag Output", true, "Enables Thermal Expansion slag output by low chance, if TE3 is present").getBoolean(true);
		steamProductionRate = config.get("High Oven", "Steam rate", 20, "Steam maximum production rate per tick per layer (in mB). Let's not render railcraft's boilers useless, set to 0 to disable steam production" +
				"\n18 is ~2 TE4 steam dynamos with 6 layers").getInt(20);

		items = config.get("Deep Tank", "Additional Glass Blocks", defaultGlass, "Specify blocks for additional Deep Tank walls." +
				"\nFormat: modname:blockname@metadata|capacity (use * as metadata for every value, capacity is the amount of mB per empty block in the tank). " +
				"\nex: minecraft:glass@*|2000 (Note: Each entry must be on a seperate line)").getStringList();

		hardcorePiston = config.get("TConification", "Hardcore Piston", false, "Piston requires tough iron tool rod").getBoolean(false);
		hardcoreFlintAndSteel = config.get("TSteelification", "Hardcore Flint & Steel", false, "Flint & Steel requires steel ingot").getBoolean(false);
		hardcoreAnvil = config.get("TSteelification", "Hardcore Anvil", false, "Anvil requires steel materials").getBoolean(false);

		enableLimestoneWorldgen = config.get("World Generation", "Limestone", true, "Allow limestone to generate (usually under rivers and oceans)").getBoolean(true);

		blacklistedAlloys = config.get("Machines", "Blacklisted dealloys", new String[0], "List of alloy which may not be dealloyed by the steam turbine. List the fluid unlocalized names. One entry per line").getStringList();

		if (config.hasChanged())
			config.save();
	}

	public static void postInit() {
		for (String item : items) {
			DeepTankGlassTypes.parseGlassType(item);
		}
	}
}
