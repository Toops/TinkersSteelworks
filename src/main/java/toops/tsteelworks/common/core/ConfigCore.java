package toops.tsteelworks.common.core;

import net.minecraftforge.common.config.Configuration;
import toops.tsteelworks.lib.registry.DeepTankGlassTypes;

import java.io.File;

public class ConfigCore {
	// --- Items
	public static boolean enableSteelArmor;

	// --- Misc
	public static boolean hardcorePiston;
	public static boolean hardcoreFlintAndSteel;
	public static boolean hardcoreAnvil;
	public static boolean enableDuctVacuum;
	public static int teSlagOutputChance;
	public static double ingotsPerOre;

	public static int steamProductionRate;
	public static String[] blacklistedAlloys;

	/**
	 * valid tank glass types with their capacity in mB
	 *
	 * modName:blockName@metadata|capacity
	 */
	private static String[] defaultGlass = {
			"minecraft:glass|10000",
			"minecraft:stained_glass@[0-15]|10000",
			"chisel:glass@*|10000",
			"chisel:stained_glass_white@[0-15]|10000",
			"chisel:stained_glass_yellow@[0-15]|10000",
			"chisel:stained_glass_lightgray@[0-15]|10000",
			"chisel:stained_glass_brown@[0-15]|10000",

			"ExtraUtilities:decorativeBlock2@0|12500", // thickened glass
			"ExtraUtilities:decorativeBlock2@1|12500",
			"ExtraUtilities:decorativeBlock2@2|12500",
			"ExtraUtilities:decorativeBlock2@3|17500", // creeper glass
			"ExtraUtilities:decorativeBlock2@4|30000", // golden edged glass
			"ExtraUtilities:decorativeBlock2@5|25000", // obsidian glass
			"ExtraUtilities:decorativeBlock2@6|12500",
			"ExtraUtilities:decorativeBlock2@7|30000", // glowstone glass
			"ExtraUtilities:decorativeBlock2@8|15000", // heart glass
			"ExtraUtilities:decorativeBlock2@9|12500",
			"ExtraUtilities:decorativeBlock2@10|30000", // dark glass

			"TConstruct:GlassBlock|12500", // clear glass
			"TConstruct:GlassBlock.StainedClear@[0-15]|12500",

			"Botania:elfGlass|17500",
			"Botania:manaGlass|12500",
			"Botany:stained@[0-15]|10000",
			"Forestry:stained@[0-15]|17500",
			"Natura:NetherGlass@[0-15]|12500",
			"Railcraft:tile.railcraftglass@[0-15]|30000",
			"MineFactoryReloaded:stainedglass.block@[0-15]|12500",
			"ThermalExpansion:Glass@0|45000",
			"Ztones:tile.glaxx@[0-15]|17500",
			"EnderIO:blockFusedQuartz@0|45000",
			"EnderIO:blockFusedQuartz@1|10000"
	};

	// --- Worldgen
	public static boolean enableLimestoneWorldgen;
	private static String[] items;

	public static void preInit(File configFile) {
		final Configuration config = new Configuration(configFile);

		config.load();

		enableSteelArmor = config.get("Equipables", "Steel Armor Enabler", true, "Enables steel armor").getBoolean(true);

		enableDuctVacuum = config.get("High Oven", "Enable Duct Vacuum", false, "Enables High Oven Ducts to suck in items like a hopper").getBoolean(false);

		ingotsPerOre = config.get("High Oven", "Ingots per ore", 2.0, "Number of ingots returned from smelting ores in the High Oven").getDouble(2.0);
		TSRecipes.ORE_LIQUID_VALUE = (int) Math.round(TSRecipes.INGOT_LIQUID_VALUE * ingotsPerOre);

		teSlagOutputChance = config.get("High Oven", "TE Slag output chance", 10, "1 in <config> chance of getting Thermal Expansion slag when smelting ores, set to -1 to disable. Active only if TE is present").getInt(10);
		steamProductionRate = config.get("High Oven", "Steam rate", 20, "Steam maximum production rate per tick per layer (in mB). Let's not renderTank railcraft's boilers useless, set to 0 to disable steam production" +
				"\n18 is ~2 TE4 steam dynamos with 6 layers").getInt(20);

		items = config.get("Deep Tank", "Additional Glass Blocks", defaultGlass, "Specify blocks for additional Deep Tank walls." +
				"\nFormat: modname:blockname@metadata|capacity (the metadata must either be a serie of numbers and/or ranges (like [1-14]) separated by commas. Capacity is the amount of mB per empty block in the tank). " +
				"\nex: minecraft:glass@*|2000 (Note: Each entry must be on a seperate line)").getStringList();

		hardcorePiston = config.get("TConification", "Hardcore Piston", false, "Piston requires tough iron tool rod (note: requires TConstruct)").getBoolean(false);
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
