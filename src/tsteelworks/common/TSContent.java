package tsteelworks.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.common.TContent;
import tconstruct.library.TConstructRegistry;
import tsteelworks.blocks.DustStorageBlock;
import tsteelworks.blocks.HighOvenBlock;
import tsteelworks.blocks.TSBaseBlock;
import tsteelworks.blocks.logic.HighOvenDrainLogic;
import tsteelworks.blocks.logic.HighOvenLogic;
import tsteelworks.blocks.logic.TSMultiServantLogic;
import tsteelworks.items.TSArmorBasic;
import tsteelworks.items.TSManual;
import tsteelworks.items.TSMaterialItem;
import tsteelworks.items.blocks.DustStorageItemBlock;
import tsteelworks.items.blocks.HighOvenItemBlock;
import tsteelworks.lib.ConfigCore;
import tsteelworks.lib.TSteelworksRegistry;
import tsteelworks.lib.crafting.AdvancedSmelting;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class TSContent implements IFuelHandler
{
    public static Item materialsTS;
    public static Item bookManual;
    public static Item helmetSteel;
    public static Item chestplateSteel;
    public static Item leggingsSteel;
    public static Item bootsSteel;
    public static EnumArmorMaterial materialSteel;
    public static Block highoven;
    public static Block charcoalBlock;
    public static Block dustStorageBlock;

    /**
     * Content Constructor
     */
    public TSContent()
    {
        registerItems();
        registerBlocks();
        oreRegistry();
        registerMixerMaterials();
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
        
        bookManual = new TSManual(ConfigCore.manual);
        GameRegistry.registerItem(bookManual, "tsteelManual");
        
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
        /* High Oven */
        highoven = new HighOvenBlock(ConfigCore.highoven).setUnlocalizedName("HighOven");
        GameRegistry.registerBlock(highoven, HighOvenItemBlock.class, "HighOven");
        GameRegistry.registerTileEntity(HighOvenLogic.class, "TSteelworks.HighOven");
        GameRegistry.registerTileEntity(HighOvenDrainLogic.class, "TSteelworks.HighOvenDrain");
        GameRegistry.registerTileEntity(TSMultiServantLogic.class, "TSteelworks.Servants");
        /* Raw Vanilla Materials */
        charcoalBlock = new TSBaseBlock(ConfigCore.charcoalStorageBlock, Material.rock, 5.0f, new String[] {"charcoal_block"}).setUnlocalizedName("tsteelworks.blocks.charcoal");
        GameRegistry.registerBlock(charcoalBlock, "blockCharcoal");
        charcoalBlock.setBurnProperties(charcoalBlock.blockID, 15, 30);
        dustStorageBlock = new DustStorageBlock(ConfigCore.dustStorageBlock).setUnlocalizedName("DustStorage").setUnlocalizedName("tsteelworks.dustblock");
        GameRegistry.registerBlock(dustStorageBlock, DustStorageItemBlock.class, "dustStorage");
    }
    
    void oreRegistry ()
    {
        OreDictionary.registerOre("blockCoal", new ItemStack(charcoalBlock));
        OreDictionary.registerOre("blockGunpowder", new ItemStack(dustStorageBlock, 1, 0));
        OreDictionary.registerOre("blockSugar", new ItemStack(dustStorageBlock, 1, 1));
        
        ensureOreIsRegistered("blockCobble", new ItemStack(Block.cobblestone));
        ensureOreIsRegistered("dustRedstone", new ItemStack(Item.redstone));
    }
    
    void ensureOreIsRegistered (String oreName, ItemStack is)
    {
        int oreId = OreDictionary.getOreID(is);
        if (oreId == -1)
            OreDictionary.registerOre(oreName, is);
    }
    
    /**
     * Register mixer materials
     */
    public static void registerMixerMaterials ()
    {
        // Steel
        AdvancedSmelting.addMixer(new ItemStack(Item.gunpowder, 1, 0), 0,  33);
        AdvancedSmelting.addMixer(new ItemStack(Item.redstone,  1, 0), 1,  65);
        AdvancedSmelting.addMixer(new ItemStack(Block.sand,     2, 0), 2, 100);
        // Pig Iron
        AdvancedSmelting.addMixer(new ItemStack(Item.sugar,         1, 0), 0,  33);
        AdvancedSmelting.addMixer(new ItemStack(Item.emerald,       1, 0), 1,  90);
        AdvancedSmelting.addMixer(new ItemStack(TContent.meatBlock, 1, 0), 2, 100);
    }
    
    /**
     * Make TSRecipes add all crafting recipes
     */
    public void addCraftingRecipes ()
    {
        TSRecipes.addOreDictionarySmelteryRecipes();
        TSRecipes.addSmeltingIron();
        TSRecipes.addSmeltingSteel();
        TSRecipes.addSmeltingPigIron();
        TSRecipes.addSmeltingGold();
        TSRecipes.addSmeltingMisc();
        TSRecipes.addAlloyRecipesForSmeltery();
        TSRecipes.addRecipesScorchedBrickMaterial();
        TSRecipes.addRecipesHighOvenComponents();
        TSRecipes.addRecipesVanillaStorageBlocks();
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
    
    @Override
    public int getBurnTime (ItemStack fuel)
    {
        int i = fuel.getItem().itemID;
        if (fuel.getItem() instanceof ItemBlock && Block.blocksList[i] != null)
        {
            Block block = Block.blocksList[i];
            if (block == TSContent.charcoalBlock)
                return 16000;
        }
        return 0;
    }
}
