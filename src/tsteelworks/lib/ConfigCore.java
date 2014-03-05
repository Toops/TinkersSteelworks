package tsteelworks.lib;

import java.io.File;

import net.minecraftforge.common.Configuration;

public class ConfigCore
{
    public static void initProps (File configFile)
    {
        final Configuration config = new Configuration(configFile);

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
        
        highoven = config.getBlock("Blocks", "High Oven Blocks ID", 3400, "High Oven Blocks (Scorched Bricks, etc)").getInt(3400);
        charcoalStorageBlock = config.getBlock("Blocks", "Charcoal Block ID", 3401, "Compressed Charcoal Block (3x3)").getInt(3401);
        dustStorageBlock = config.getBlock("Blocks", "Compressed Powder Blocks ID", 3402, "Powder Blocks (Gunpowder, etc)").getInt(3402);

        ingotsPerOre = config.get("High Oven", "Ingots per ore", 1, "Number of ingots returned from smelting ores in the High Oven").getInt(1);
        
        hardcorePiston        = config.get("TConification", "Hardcore Piston", false,
                                           "Piston requires tough iron tool rod.").getBoolean(false);
        hardcoreFlintAndSteel = config.get("TSteelification", "Hardcore Flint & Steel", false,
                                           "Flint & Steel requires steel ingot.").getBoolean(false);
        hardcoreAnvil         = config.get("TSteelification", "Hardcore Anvil", false,
                                           "Anvil requires steel materials.").getBoolean(false);

        config.save();
    }

    // --- Items
    public static int     materials;
    public static boolean enableSteelArmor;
    public static int     steelHelmet;
    public static int     steelChestplate;
    public static int     steelLeggings;
    public static int     steelBoots;
    // -- Blocks
    public static int     highoven;
    public static int     charcoalStorageBlock;
    public static int     dustStorageBlock;
    
    
    public static int     ingotsPerOre;
    // --- Misc  
    public static boolean hardcorePiston;
    public static boolean hardcoreFlintAndSteel;
    public static boolean hardcoreAnvil;
}
