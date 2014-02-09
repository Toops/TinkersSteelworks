package tsteelworks.common;

import net.minecraft.block.Block;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumHelper;
import tconstruct.blocks.logic.MultiServantLogic;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.armor.EnumArmorPart;
import tsteelworks.blocks.HighOvenBlock;
import tsteelworks.blocks.logic.HighOvenDrainLogic;
import tsteelworks.blocks.logic.HighOvenLogic;
import tsteelworks.items.TSArmorBasic;
import tsteelworks.items.TSExoArmor;
import tsteelworks.items.TSMaterialItem;
import tsteelworks.items.blocks.HighOvenItemBlock;
import tsteelworks.lib.ConfigCore;
import tsteelworks.lib.TSteelworksRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class TSContent
{
    public static Item materialsTS;
    public static Item helmetSteel;
    public static Item chestplateSteel;
    public static Item leggingsSteel;
    public static Item bootsSteel;
    public static EnumArmorMaterial materialSteel;
    public static Item exoGogglesSteel;
    public static Item exoChestSteel;
    public static Item exoPantsSteel;
    public static Item exoShoesSteel;
    public static Block highoven;

    /**
     * Content Constructor
     */
    public TSContent()
    {
        registerItems();
        registerBlocks();
        addCraftingRecipes();
        setupCreativeTabs();
    }

    /**
     * Initialize the Steelworks creative tab with an icon.
     */
    private void setupCreativeTabs ()
    {
        TSteelworksRegistry.SteelworksCreativeTab.init(TConstructRegistry.getItemStack("ingotSteel"));
    }

    /**
     * Register Items
     */
    void registerItems ()
    {
        materialsTS = new TSMaterialItem(ConfigCore.materials).setUnlocalizedName("tsteelworks.Materials");
        GameRegistry.registerItem(materialsTS, "Materials");
        TSteelworksRegistry.addItemStackToDirectory("scorchedBrick", new ItemStack(materialsTS, 1, 0));
        
        if (ConfigCore.enableSteelArmor)
        {
            // TODO: Change properties
            materialSteel = EnumHelper.addArmorMaterial("STEEL", 2, new int[] { 1, 2, 2, 1 }, 3);
            helmetSteel = new TSArmorBasic(ConfigCore.steelHelmet, materialSteel, 0, "steel").setUnlocalizedName("tsteelworks.helmetSteel");
            chestplateSteel = new TSArmorBasic(ConfigCore.steelChestplate, materialSteel, 1, "steel").setUnlocalizedName("tsteelworks.chestplateSteel");
            leggingsSteel = new TSArmorBasic(ConfigCore.steelLeggings, materialSteel, 2, "steel").setUnlocalizedName("tsteelworks.leggingsSteel");
            bootsSteel = new TSArmorBasic(ConfigCore.steelBoots, materialSteel, 3, "steel").setUnlocalizedName("tsteelworks.bootsSteel");
            GameRegistry.registerItem(helmetSteel, "helmetSteel");
            GameRegistry.registerItem(chestplateSteel, "chestplateSteel");
            GameRegistry.registerItem(leggingsSteel, "leggingsSteel");
            GameRegistry.registerItem(bootsSteel, "bootsSteel");
        }
        if (ConfigCore.enableExoSteelArmor)
        {
            exoGogglesSteel = new TSExoArmor(ConfigCore.exoGogglesSteel, EnumArmorPart.HELMET, "exosuit").setUnlocalizedName("tsteelworks.exoGogglesSteel");
            exoChestSteel = new TSExoArmor(ConfigCore.exoChestSteel, EnumArmorPart.CHEST, "exosuit").setUnlocalizedName("tsteelworks.exoChestSteel");
            exoPantsSteel = new TSExoArmor(ConfigCore.exoPantsSteel, EnumArmorPart.PANTS, "exosuit").setUnlocalizedName("tsteelworks.exoPantsSteel");
            exoShoesSteel = new TSExoArmor(ConfigCore.exoShoesSteel, EnumArmorPart.SHOES, "exosuit").setUnlocalizedName("tsteelworks.exoShoesSteel");
        }
    }

    /**
     * Register Blocks and TileEntities (Logic)
     */
    void registerBlocks ()
    {
        highoven = new HighOvenBlock(ConfigCore.highoven).setUnlocalizedName("HighOven");
        GameRegistry.registerBlock(highoven, HighOvenItemBlock.class, "HighOven");
        GameRegistry.registerTileEntity(HighOvenLogic.class, "TSteelworks.HighOven");
        GameRegistry.registerTileEntity(HighOvenDrainLogic.class, "TSteelworks.HighOvenDrain");
        GameRegistry.registerTileEntity(MultiServantLogic.class, "TSteelworks.Servants");
    }
    
    /**
     * Make TSRecipes add all crafting recipes
     */
    void addCraftingRecipes ()
    {
        TSRecipes.addRecipesSteelMaterial();
        TSRecipes.addRecipesScorchedBrickMaterial();
        TSRecipes.addRecipesHighOvenComponents();
        TSRecipes.addRecipesSteelArmor();
        if (ConfigCore.hardcoreFlintAndSteel)
            TSRecipes.changeRecipeFlintAndSteel();
    }
}
