package tsteelworks.lib;

import java.io.File;

import net.minecraftforge.common.Configuration;

public class ConfigCore
{
    // --- Items
    public static int materials;
    public static int manual;
    public static int buckets;
    public static boolean enableSteelArmor;
    public static int steelHelmet;
    public static int steelChestplate;
    public static int steelLeggings;
    public static int steelBoots;
    // -- Blocks
    public static int highoven;
    public static int limestone;
    public static int machine;
    public static int charcoalStorageBlock;
    public static int dustStorageBlock;
    public static int steam;
    public static int moltenLimestone;
    // --- Misc  
    public static boolean hardcorePiston;
    public static boolean hardcoreFlintAndSteel;
    public static boolean hardcoreAnvil;
    public static int ingotsPerOre;
    public static int deeptankCapacityMultiplier;
    public static boolean smelteryObsidianAlloy;
    public static boolean enableDuctVacuum;
    public static boolean enableTE3SlagOutput;
    public static int[] modTankGlassBlocks;

    public static void initProps (File configFile)
    {
        final Configuration config = new Configuration(configFile);

        config.load();
        /*
         * Block IDs Range: 3400~3499 Item IDs Range 14500~14599 
         */
        materials = config.getItem("Items", "Crafting Materials ID", 14500, "Materials (Ingots, Nuggets, Etc)").getInt(14500);
        manual = config.getItem("Items", "Steelworks Manual ID", 14501, "Manuals (Your First High Oven, Etc)").getInt(14501);
        buckets = config.getItem("Items", "Buckets", 14502).getInt(14502);
        
        enableSteelArmor = config.get("Equipables", "Steel Armor Enabler", true, "Enable steel armor").getBoolean(true);
        steelHelmet = config.getItem("Equipables", "Steel Helmet", 14512).getInt(14512);
        steelChestplate = config.getItem("Equipables", "Steel Chestplate", 14513).getInt(14513);
        steelLeggings = config.getItem("Equipables", "Steel Leggings", 14514).getInt(14514);
        steelBoots = config.getItem("Equipables", "Steel Boots", 14515).getInt(14515);

        highoven = config.getBlock("Blocks", "High Oven Blocks ID", 3400, "High Oven Blocks (Scorched Bricks, etc)").getInt(3400);
        charcoalStorageBlock = config.getBlock("Blocks", "Charcoal Block ID", 3401, "Compressed Charcoal Block (3x3)").getInt(3401);
        dustStorageBlock = config.getBlock("Blocks", "Compressed Powder Blocks ID", 3402, "Powder Blocks (Gunpowder, etc)").getInt(3402);
        machine = config.getBlock("Blocks", "Machine Blocks ID", 3403, "Machine Blocks (Turbine, etc)").getInt(3403);
        limestone = config.getBlock("Blocks", "Limestone Blocks ID", 3404, "Limestone Blocks").getInt(3404);
        moltenLimestone = config.getBlock("Blocks", "Molten Limestone Block ID", 3405, "Limestone Fluid 'Block'").getInt(3405);
        steam = config.getBlock("Blocks", "Steam Block ID", 3409, "Steam Fluid 'Block'").getInt(3409);
        
        enableDuctVacuum = config.get("High Oven", "Enable Duct Vacuum", false, "Enables High Oven Ducts to suck in items like a hopper (potential lag, tests needed!)").getBoolean(false);
        ingotsPerOre = config.get("High Oven", "Ingots per ore", 2, "Number of ingots returned from smelting ores in the High Oven").getInt(2);
        
        enableTE3SlagOutput = config.get("High Oven", "Enable TE3 Slag Output", true, "Enables Thermal Expansion slag output by low chance, if TE3 is present.").getBoolean(true);
        
        smelteryObsidianAlloy = config.get("Smeltery", "Obsidian Alloy", false, "Enables mixing lava and water in the Smeltery for obsidian").getBoolean(false);
        
        deeptankCapacityMultiplier = config.get("Deep Tank", "Fluid Capacity Multiplier", 4, "Determines how many buckets of fluid per internal block space.").getInt(4);
        modTankGlassBlocks = config.get("Deep Tank", "Additional Glass Blocks", new int[] {}, "Specify block ids for additional Deep Tank walls. EX: < 3000 3001 ... > (Note: Currently doesn't save properly...)").getIntList();
        
        hardcorePiston = config.get("TConification", "Hardcore Piston", false, "Piston requires tough iron tool rod.").getBoolean(false);
        hardcoreFlintAndSteel = config.get("TSteelification", "Hardcore Flint & Steel", false, "Flint & Steel requires steel ingot.").getBoolean(false);
        hardcoreAnvil = config.get("TSteelification", "Hardcore Anvil", false, "Anvil requires steel materials.").getBoolean(false);

        config.save();
    }
}
