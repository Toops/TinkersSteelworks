package tsteelworks.common;

import net.minecraft.block.Block;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.common.TContent;
import tconstruct.library.TConstructRegistry;
import tsteelworks.blocks.HighOvenBlock;
import tsteelworks.blocks.logic.HighOvenDrainLogic;
import tsteelworks.blocks.logic.HighOvenLogic;
import tsteelworks.blocks.logic.TSMultiServantLogic;
import tsteelworks.items.TSArmorBasic;
import tsteelworks.items.TSMaterialItem;
import tsteelworks.items.blocks.HighOvenItemBlock;
import tsteelworks.lib.ConfigCore;
import tsteelworks.lib.TSteelworksRegistry;
import tsteelworks.lib.crafting.AdvancedSmelting;
import cpw.mods.fml.common.registry.GameRegistry;

public class TSContent
{
    public static Item materialsTS;
    public static Item helmetSteel;
    public static Item chestplateSteel;
    public static Item leggingsSteel;
    public static Item bootsSteel;
    public static EnumArmorMaterial materialSteel;
    public static Block highoven;

    /**
     * Content Constructor
     */
    public TSContent()
    {
        registerItems();
        registerBlocks();
        oreRegistry();
        registerMixerMaterials();
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
            materialSteel = EnumHelper.addArmorMaterial("STEEL", 25, new int[] { 3, 7, 5, 3 }, 10);
            helmetSteel = new TSArmorBasic(ConfigCore.steelHelmet, materialSteel, 0, "steel").setUnlocalizedName("tsteelworks.helmetSteel");
            chestplateSteel = new TSArmorBasic(ConfigCore.steelChestplate, materialSteel, 1, "steel").setUnlocalizedName("tsteelworks.chestplateSteel");
            leggingsSteel = new TSArmorBasic(ConfigCore.steelLeggings, materialSteel, 2, "steel").setUnlocalizedName("tsteelworks.leggingsSteel");
            bootsSteel = new TSArmorBasic(ConfigCore.steelBoots, materialSteel, 3, "steel").setUnlocalizedName("tsteelworks.bootsSteel");
            GameRegistry.registerItem(helmetSteel, "helmetSteel");
            GameRegistry.registerItem(chestplateSteel, "chestplateSteel");
            GameRegistry.registerItem(leggingsSteel, "leggingsSteel");
            GameRegistry.registerItem(bootsSteel, "bootsSteel");
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
        GameRegistry.registerTileEntity(TSMultiServantLogic.class, "TSteelworks.Servants");
    }
    

    
    void oreRegistry ()
    {
        ensureOreIsRegistered("blockCobble", new ItemStack(Block.cobblestone));
        ensureOreIsRegistered("dustRedstone", new ItemStack(Item.redstone));
    }
    
    void ensureOreIsRegistered (String oreName, ItemStack is)
    {
        int oreId = OreDictionary.getOreID(is);
        if (oreId == -1)
        {
            OreDictionary.registerOre(oreName, is);
        }
    }
    
    /**
     * Register mixer materials
     */
    public static void registerMixerMaterials ()
    {
        // Steel
        AdvancedSmelting.addMixer(new ItemStack(Item.gunpowder, 1, 0), 0, 33);
        AdvancedSmelting.addMixer(new ItemStack(Item.redstone,  1, 0), 1, 55);
        AdvancedSmelting.addMixer(new ItemStack(Block.sand,     2, 0), 2, 85);
        // Pig Iron
        AdvancedSmelting.addMixer(new ItemStack(Item.emerald,       1, 0), 1, 80);
        AdvancedSmelting.addMixer(new ItemStack(TContent.meatBlock, 1, 0), 2, 100);
    }
    
    /**
     * Make TSRecipes add all crafting recipes
     */
    void addCraftingRecipes ()
    {
        TSRecipes.addRecipesMaterialIron();
        TSRecipes.addRecipesMaterialSteel();
        TSRecipes.addRecipesMaterialPigIron();
        TSRecipes.addRecipesScorchedBrickMaterial();
        TSRecipes.addRecipesHighOvenComponents();
        changeCraftingRecipes();
    }
    
    public void changeCraftingRecipes ()
    {
        if (ConfigCore.enableSteelArmor)
            TSRecipes.addRecipesSteelArmor();
        if (ConfigCore.hardcorePiston)
            TSRecipes.changeRecipePiston();
        if (ConfigCore.hardcoreFlintAndSteel)
            TSRecipes.changeRecipeFlintAndSteel();
        if (ConfigCore.hardcoreAnvil)
            TSRecipes.changeRecipeAnvil();        
    }
}
