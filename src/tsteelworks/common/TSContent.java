package tsteelworks.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.common.TContent;
import tconstruct.library.TConstructRegistry;
import tsteelworks.TSteelworks;
import tsteelworks.blocks.DustStorageBlock;
import tsteelworks.blocks.HighOvenBlock;
import tsteelworks.blocks.MachineBlock;
import tsteelworks.blocks.SteamFluidBlock;
import tsteelworks.blocks.TSBaseBlock;
import tsteelworks.blocks.logic.DeepTankLogic;
import tsteelworks.blocks.logic.HighOvenDrainLogic;
import tsteelworks.blocks.logic.HighOvenDuctLogic;
import tsteelworks.blocks.logic.HighOvenLogic;
import tsteelworks.blocks.logic.TSMultiServantLogic;
import tsteelworks.blocks.logic.TurbineLogic;
import tsteelworks.entity.HighGolem;
import tsteelworks.entity.projectile.EntityScorchedBrick;
import tsteelworks.items.TSArmorBasic;
import tsteelworks.items.TSManual;
import tsteelworks.items.TSMaterialItem;
import tsteelworks.items.blocks.DustStorageItemBlock;
import tsteelworks.items.blocks.HighOvenItemBlock;
import tsteelworks.items.blocks.MachineItemBlock;
import tsteelworks.lib.ConfigCore;
import tsteelworks.lib.TSteelworksRegistry;
import tsteelworks.lib.crafting.AdvancedSmelting;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class TSContent
{
    public static Item materialsTS;
    public static Item bookManual;
    public static Item helmetSteel;
    public static Item chestplateSteel;
    public static Item leggingsSteel;
    public static Item bootsSteel;
    public static EnumArmorMaterial materialSteel;
    public static Block highoven;
    public static Block machine;
    public static Block charcoalBlock;
    public static Block dustStorageBlock;
    public static Block steamBlock;
    public static Fluid steamFluid;
    
    public static ItemStack thaumcraftAlumentum;

    /**
     * Content Constructor
     */
    public TSContent()
    {
        registerItems();
        registerBlocks();
        registerFluids();
        oreRegistry();
        registerMixerMaterials();
        setupCreativeTabs();
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
        GameRegistry.registerTileEntity(HighOvenDuctLogic.class, "TSteelworks.HighOvenDuct");
        GameRegistry.registerTileEntity(DeepTankLogic.class, "TSteelworks.DeepTank");
        GameRegistry.registerTileEntity(TSMultiServantLogic.class, "TSteelworks.Servants");
        /* Machines */
        machine = new MachineBlock(ConfigCore.machine).setUnlocalizedName("Machine");
        GameRegistry.registerBlock(machine, MachineItemBlock.class, "Machine");
        GameRegistry.registerTileEntity(TurbineLogic.class, "TSteelworks.Machine");
        /* Raw Vanilla Materials */
        charcoalBlock = new TSBaseBlock(ConfigCore.charcoalStorageBlock, Material.rock, 5.0f, new String[] { "charcoal_block" }).setUnlocalizedName("tsteelworks.blocks.charcoal");
        GameRegistry.registerBlock(charcoalBlock, "blockCharcoal");
        charcoalBlock.setBurnProperties(charcoalBlock.blockID, 15, 30);
        dustStorageBlock = new DustStorageBlock(ConfigCore.dustStorageBlock).setUnlocalizedName("DustStorage").setUnlocalizedName("tsteelworks.dustblock");
        GameRegistry.registerBlock(dustStorageBlock, DustStorageItemBlock.class, "dustStorage");
        
        
    }
    
    void registerFluids()
    {
        steamFluid = new Fluid("steam");
        if (!FluidRegistry.registerFluid(steamFluid))
            steamFluid = FluidRegistry.getFluid("steam");
        steamBlock = new SteamFluidBlock(ConfigCore.steam, steamFluid, Material.air).setCreativeTab(TSteelworksRegistry.SteelworksCreativeTab).setUnlocalizedName("water.steam");
        GameRegistry.registerBlock(steamBlock, "steam");
        steamBlock.setLightOpacity(3);
        steamFluid.setBlockID(steamBlock.blockID).setLuminosity(0).setDensity(18).setViscosity(5).setTemperature(588).setGaseous(true);
    }
    
    void oreRegistry ()
    {
        OreDictionary.registerOre("blockCoal", new ItemStack(charcoalBlock));
        OreDictionary.registerOre("blockCharcoal", new ItemStack(charcoalBlock)); // Mekanism compat
        OreDictionary.registerOre("blockGunpowder", new ItemStack(dustStorageBlock, 1, 0));
        OreDictionary.registerOre("blockSugar", new ItemStack(dustStorageBlock, 1, 1));

        ensureOreIsRegistered("dustRedstone", new ItemStack(Item.redstone));
    }

    void ensureOreIsRegistered (String oreName, ItemStack is)
    {
        final int oreId = OreDictionary.getOreID(is);
        if (oreId == -1)
            OreDictionary.registerOre(oreName, is);
        
    }
    
    /**
     * Register mixer materials
     */
    public static void registerMixerMaterials ()
    {
        AdvancedSmelting.registerMixItem(new ItemStack(Item.gunpowder,      1, 0), 0, 33);
        AdvancedSmelting.registerMixItem(new ItemStack(Item.sugar,          1, 0), 0, 62);
        AdvancedSmelting.registerMixItem(new ItemStack(Item.blazePowder,    1, 0), 0, 33);
        AdvancedSmelting.registerMixItem(new ItemStack(Item.redstone,       1, 0), 0, 33);
        AdvancedSmelting.registerMixItem(new ItemStack(Item.coal,           1, 0), 0, 33);
        
        AdvancedSmelting.registerMixItem(new ItemStack(Item.redstone,       1, 0), 1, 65);
        AdvancedSmelting.registerMixItem(new ItemStack(Item.emerald,        1, 0), 1, 30);
        AdvancedSmelting.registerMixItem(new ItemStack(Item.flint,          1, 0), 1, 30);
        AdvancedSmelting.registerMixItem(new ItemStack(Item.clay,           1, 0), 1, 20);
        
        AdvancedSmelting.registerMixItem(new ItemStack(Item.ghastTear,      1, 0), 2, 30);
        AdvancedSmelting.registerMixItem(new ItemStack(Block.blockClay,     1, 0), 2, 80);
        AdvancedSmelting.registerMixItem(new ItemStack(Block.sand,          2, 0), 2, 100);
        AdvancedSmelting.registerMixItem(new ItemStack(Block.sand,          1, 0), 2, 100);
        AdvancedSmelting.registerMixItem(new ItemStack(Block.cobblestone,   1, 0), 2, 100);
        AdvancedSmelting.registerMixItem(new ItemStack(TContent.meatBlock,  1, 0), 2, 100);
    }
    
    /**
     * Initialize the Steelworks creative tab with an icon.
     */
    private void setupCreativeTabs ()
    {
        TSteelworksRegistry.SteelworksCreativeTab.init(TConstructRegistry.getItemStack("ingotSteel"));
    }
    
    public void createEntities ()
    {
        EntityRegistry.registerModEntity(EntityScorchedBrick.class, "ScorchedBrick", 0, TSteelworks.instance, 32, 3, true);
        // TODO: Register with registerModEntity instead
        EntityRegistry.registerGlobalEntityID(HighGolem.class, "HighGolem", EntityRegistry.findGlobalUniqueEntityId(), 0x171717, 0x614D3C);
    }
    
    /**
     * Make TSRecipes add all crafting recipes
     */
    public void addCraftingRecipes ()
    {
        TSRecipes.smeltOreDict();
        TSRecipes.smeltIron();
        TSRecipes.smeltSteel();
        TSRecipes.solidSmeltMixCombos();
        TSRecipes.smeltPigIron();
        TSRecipes.smeltGold();
        TSRecipes.smeltMisc();
        TSRecipes.alloyMisc();
        TSRecipes.castScorchedBrick();
        TSRecipes.castManuals();
        TSRecipes.craftHighOven();
        TSRecipes.craftStorageBlocks();
        changeCraftingRecipes();
    }

    public void changeCraftingRecipes ()
    {
        if (ConfigCore.enableSteelArmor)
            TSRecipes.craftSteelArmor();
        if (ConfigCore.hardcorePiston)
            TSRecipes.changePiston();
        if (ConfigCore.hardcoreFlintAndSteel)
            TSRecipes.changeFlintAndSteel();
        if (ConfigCore.hardcoreAnvil)
            TSRecipes.changeAnvil();
    }
    
    public void modIntegration ()
    {
        if (TContent.thaumcraftAvailable)
        {
            Object objResource = TContent.getStaticItem("itemResource", "thaumcraft.common.config.ConfigItems");
            if (objResource != null)
            {
                TSteelworks.logger.info("Thaumcraft detected. Registering materials.");
                thaumcraftAlumentum = new ItemStack((Item) objResource, 1, 0);
            }
        }
    }
}
