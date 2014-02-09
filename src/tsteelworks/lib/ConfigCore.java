package tsteelworks.lib;

import java.io.File;

import net.minecraftforge.common.Configuration;

public class ConfigCore
{
    public static void initProps (File configFile)
    {
        /*
         * [Forge] Configuration class, used as config method
         */
        final Configuration config = new Configuration(configFile);
        /*
         * Load the configuration file
         */
        config.load();
        /*
         * Block IDs Range: 3400~3499 Item IDs Range 14500~14599
         */
        materials = config.getItem("Items", "Crafting Materials ID", 14500, "Materials (Ingots, Nuggets, Etc)").getInt(14500);
        
        enableSteelArmor = config.get("Equipables", "Steel Armor Enabler", true, "Enable steel armor").getBoolean(true);
        steelHelmet = config.getItem("Equipables", "Steel Helmet", 14512).getInt(14512);
        steelChestplate = config.getItem("Equipables", "Steel Chestplate", 14513).getInt(14513);
        steelLeggings = config.getItem("Equipables", "Steel Leggings", 14514).getInt(14514);
        steelBoots = config.getItem("Equipables", "Steel Boots", 14515).getInt(14515);
        enableExoSteelArmor = config.get("Equipables", "Steel ExoSuit Enabler", true, "Enable steel exo-suit").getBoolean(true);
        exoGogglesSteel = config.getItem("Equipables", "Steel ExoSuit Helmet", 14516).getInt(14512);
        exoChestSteel = config.getItem("Equipables", "Steel ExoSuit Chestplate", 14517).getInt(14513);
        exoPantsSteel = config.getItem("Equipables", "Steel ExoSuit Leggings", 14518).getInt(14514);
        exoShoesSteel = config.getItem("Equipables", "Steel ExoSuit Boots", 14519).getInt(14515);
        
        highoven = config.getBlock("Blocks", "High Oven Blocks ID", 3400, "High Oven Blocks (Scorched Bricks, etc)").getInt(3400);

        /*
         * Difficulty Changes
         */
        hardcoreFlintAndSteel = config.get("Difficulty Changes", "Hardcore Flint & Steel", false,
                                           "Flint & Steel requires steel.").getBoolean(false);
        /*
         * Vanilla Furnace Steel Smelting
         */
        enableFurnaceSteelSmelting = config.get("Vanilla Furnace", "Vanilla Furnace Steel Smelting", false,
                                                "Enable vanilla furnaces to smelt steel from iron tsteelworks.items.").getBoolean(false);
        furnaceSteelConversionRate = config.get("Vanilla Furnace", "Vanilla Furnace Steel Yield", 1,
                                                "Number of nuggets/ingots returned from cooking ingots/tsteelworks.blocks.").getInt(1);
        /*
         * Save the configuration file
         */
        config.save();
    }

    // --- Items
    public static int     materials;

    public static boolean enableSteelArmor;
    public static int     steelHelmet;
    public static int     steelChestplate;
    public static int     steelLeggings;
    public static int     steelBoots;
    public static boolean enableExoSteelArmor;
    public static int     exoGogglesSteel;
    public static int     exoChestSteel;
    public static int     exoPantsSteel;
    public static int     exoShoesSteel;
    // -- Blocks
    public static int     highoven;

    // --- Misc
    public static int     highOvenSmeltTime;
    public static boolean hardcoreFlintAndSteel;
    public static boolean enableFurnaceSteelSmelting;
    public static int     furnaceSteelConversionRate;
}
