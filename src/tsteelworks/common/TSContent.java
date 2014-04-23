package tsteelworks.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.common.TContent;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.client.TConstructClientRegistry;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.ToolCore;
import tconstruct.modifiers.tools.ModInteger;
import tconstruct.modifiers.tools.ModPiston;
import tsteelworks.TSteelworks;
import tsteelworks.blocks.DustStorageBlock;
import tsteelworks.blocks.HighOvenBlock;
import tsteelworks.blocks.LimestoneBlock;
import tsteelworks.blocks.LimestoneSlab;
import tsteelworks.blocks.MachineBlock;
import tsteelworks.blocks.ScorchedSlab;
import tsteelworks.blocks.SteamFluidBlock;
import tsteelworks.blocks.TSBaseBlock;
import tsteelworks.blocks.TSBaseFluid;
import tsteelworks.blocks.logic.DeepTankLogic;
import tsteelworks.blocks.logic.HighOvenDrainLogic;
import tsteelworks.blocks.logic.HighOvenDuctLogic;
import tsteelworks.blocks.logic.HighOvenLogic;
import tsteelworks.blocks.logic.TSMultiServantLogic;
import tsteelworks.blocks.logic.TurbineLogic;
import tsteelworks.entity.HighGolem;
import tsteelworks.entity.projectile.EntityScorchedBrick;
import tsteelworks.items.TSArmorBasic;
import tsteelworks.items.TSFilledBucket;
import tsteelworks.items.TSManual;
import tsteelworks.items.TSMaterialItem;
import tsteelworks.items.blocks.DustStorageItemBlock;
import tsteelworks.items.blocks.HighOvenItemBlock;
import tsteelworks.items.blocks.LimestoneItemBlock;
import tsteelworks.items.blocks.LimestoneSlabItemBlock;
import tsteelworks.items.blocks.MachineItemBlock;
import tsteelworks.items.blocks.ScorchedSlabItemBlock;
import tsteelworks.lib.ConfigCore;
import tsteelworks.lib.TSteelworksRegistry;
import tsteelworks.lib.crafting.AdvancedSmelting;
import tsteelworks.modifiers.tools.ModHopper;
import tsteelworks.modifiers.tools.TSActiveOmniMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class TSContent
{
    public static Item materialsTS;
    public static Item bucketsTS;    
    public static Item bookManual;
    public static Item helmetSteel;
    public static Item chestplateSteel;
    public static Item leggingsSteel;
    public static Item bootsSteel;
    public static EnumArmorMaterial materialSteel;
    public static Block highoven;
    public static Block scorchedSlab;
    public static Block limestoneBlock;
    public static Block limestoneSlab;
    public static Block machine;
    public static Block charcoalBlock;
    public static Block dustStorageBlock;
    public static Block steamBlock;
    public static Block moltenLimestone;
    public static Fluid steamFluid;
    public static Fluid moltenLimestoneFluid;
    
    public static ItemStack thaumcraftAlumentum;
//    public static ItemStack railcraftBlockCoalCoke;

    public static Fluid[] fluids = new Fluid[2];
    public static Block[] fluidBlocks = new Block[2];
    
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
        registerModifiers();
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

        bucketsTS = new TSFilledBucket(ConfigCore.buckets);
        GameRegistry.registerItem(bucketsTS, "buckets");
        
        
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
        
        scorchedSlab = new ScorchedSlab(ConfigCore.scorchedSlab).setUnlocalizedName("ScorchedSlab");
        scorchedSlab.stepSound = Block.soundStoneFootstep;
        GameRegistry.registerBlock(scorchedSlab, ScorchedSlabItemBlock.class, "ScorchedSlab");
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
        
        limestoneBlock = new LimestoneBlock(ConfigCore.limestone).setUnlocalizedName("Limestone");
        GameRegistry.registerBlock(limestoneBlock, LimestoneItemBlock.class, "Limestone");
        
        limestoneSlab = new LimestoneSlab(ConfigCore.limestoneSlab).setUnlocalizedName("LimestoneSlab");
        limestoneSlab.stepSound = Block.soundStoneFootstep;
        GameRegistry.registerBlock(limestoneSlab, LimestoneSlabItemBlock.class, "LimestoneSlab");
    }
    
    void registerFluids()
    {
        boolean doRegisterSteamBlock = false;
        steamFluid = new Fluid("steam");
        if (!FluidRegistry.registerFluid(steamFluid))
        {
            steamFluid = FluidRegistry.getFluid("steam");
            if (steamFluid.getBlockID() != -1)
            {
                steamBlock = Block.blocksList[steamFluid.getBlockID()];
                fluids[0] = steamFluid;
                fluidBlocks[0] = steamBlock;
            }
            else
            {
                TSteelworks.loginfo("Attempted to acquire another mod's steam block, but it is missing! Obtaining TSteelworks steam block instead.");
                doRegisterSteamBlock = true;
            }
        }
        else
            doRegisterSteamBlock = true;
        if (doRegisterSteamBlock)
        {
            steamBlock = new SteamFluidBlock(ConfigCore.steam, steamFluid, Material.air).setCreativeTab(TSteelworksRegistry.SteelworksCreativeTab).setUnlocalizedName("steam");
            fluids[0] = steamFluid;
            fluidBlocks[0] = steamBlock;
            GameRegistry.registerBlock(steamBlock, "steam");
            steamBlock.setLightOpacity(0);
            steamFluid.setBlockID(steamBlock.blockID).setLuminosity(0).setDensity(18).setViscosity(5).setTemperature(588).setGaseous(true);
            FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(steamFluid, 1000), new ItemStack(bucketsTS, 1, 0), new ItemStack(Item.bucketEmpty)));
        }
        
        moltenLimestoneFluid = new Fluid("limestone.molten");
        if (!FluidRegistry.registerFluid(moltenLimestoneFluid))
            moltenLimestoneFluid = FluidRegistry.getFluid("limestone.molten");
        moltenLimestone = new TSBaseFluid(ConfigCore.moltenLimestone, moltenLimestoneFluid, Material.lava, "liquid_limestone").setUnlocalizedName("molten.limestone");
        GameRegistry.registerBlock(moltenLimestone, "molten.limestone");
        fluids[1] = moltenLimestoneFluid;
        fluidBlocks[1] = moltenLimestone;
        moltenLimestoneFluid.setBlockID(moltenLimestone).setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenLimestoneFluid, 1000), new ItemStack(bucketsTS, 1, 1), new ItemStack(Item.bucketEmpty)));
    }
    
    void oreRegistry ()
    {
        OreDictionary.registerOre("blockCoal", new ItemStack(charcoalBlock));
        OreDictionary.registerOre("blockCharcoal", new ItemStack(charcoalBlock)); // Mekanism compat
        OreDictionary.registerOre("blockGunpowder", new ItemStack(dustStorageBlock, 1, 0));
        OreDictionary.registerOre("blockSugar", new ItemStack(dustStorageBlock, 1, 1));
        OreDictionary.registerOre("blockLimestone", new ItemStack(limestoneBlock, 1, 0));
        
        OreDictionary.registerOre("stone", new ItemStack(limestoneBlock, 1, 0));
        OreDictionary.registerOre("cobblestone", new ItemStack(limestoneBlock, 1, 1));

        ensureOreIsRegistered("dustRedstone", new ItemStack(Item.redstone));
        ensureOreIsRegistered("dustGunpowder", new ItemStack(Item.gunpowder));
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
    void registerMixerMaterials ()
    {
        AdvancedSmelting.registerMixItem(new ItemStack(Item.gunpowder,      1, 0), 0, 33);
        AdvancedSmelting.registerMixItem(new ItemStack(Item.sugar,          1, 0), 0, 62);
        AdvancedSmelting.registerMixItem(new ItemStack(Item.blazePowder,    1, 0), 0, 33);
        AdvancedSmelting.registerMixItem(new ItemStack(Item.redstone,       1, 0), 0, 33);
        AdvancedSmelting.registerMixItem(new ItemStack(Item.coal,           1, 0), 0, 56);
        
        AdvancedSmelting.registerMixItem(new ItemStack(Item.redstone,       1, 0), 1, 65);
        AdvancedSmelting.registerMixItem(new ItemStack(Item.emerald,        1, 0), 1, 30);
        AdvancedSmelting.registerMixItem(new ItemStack(Item.flint,          1, 0), 1, 30);
        AdvancedSmelting.registerMixItem(new ItemStack(Item.clay,           1, 0), 1, 20);
        AdvancedSmelting.registerMixItem(new ItemStack(TContent.oreBerries, 1, 5), 1, 37);
        AdvancedSmelting.registerMixItem(new ItemStack(TContent.materials, 1, 22), 1, 26);
        AdvancedSmelting.registerMixItem(new ItemStack(TContent.materials, 1, 40), 1, 53);
        
        AdvancedSmelting.registerMixItem(new ItemStack(Item.ghastTear,      1, 0), 2, 30);
        AdvancedSmelting.registerMixItem(new ItemStack(Block.blockClay,     1, 0), 2, 80);
        AdvancedSmelting.registerMixItem(new ItemStack(Block.sand,          2, 0), 2, 100);
        AdvancedSmelting.registerMixItem(new ItemStack(Block.sand,          1, 0), 2, 100);
        AdvancedSmelting.registerMixItem(new ItemStack(Block.cobblestone,   1, 0), 2, 100);
        AdvancedSmelting.registerMixItem(new ItemStack(TContent.meatBlock,  1, 0), 2, 100);
        AdvancedSmelting.registerMixItem(new ItemStack(TContent.craftedSoil, 1, 3), 2, 53);
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
        // TODO: Register with registerModEntity instead. We do this because registerModEntity does not seemingly add a mob spawner egg.
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
                TSteelworks.logger.info("Thaumcraft detected. Registering fuels.");
                thaumcraftAlumentum = new ItemStack((Item) objResource, 1, 0);
            }
        }
        // BlockCube and ItemCube not detected. :/ WTF railcraft?
//        if (TSteelworks.railcraftAvailable)
//        {
//            Object objBlockCube = TContent.getStaticItem("BlockCube", "mods.railcraft.common.blocks.aesthetics.cube");
//            if (objBlockCube != null)
//            {
//                TSteelworks.logger.info("Railcraft detected. Registering fuels.");
//                railcraftBlockCoalCoke = new ItemStack((Item) objBlockCube, 1, 0);
//            }
//        }
    }
    
    void registerModifiers()
    {
        ToolBuilder tb = ToolBuilder.instance;
        ItemStack hopper = new ItemStack(Block.hopperBlock);
        ItemStack enderpearl = new ItemStack(Item.enderPearl);
        
        tb.registerToolMod(new ModInteger(new ItemStack[] { hopper, enderpearl }, 50, "Hopper", 5, "\u00A7a", "Vacuous"));
//        tb.registerToolMod(new ModHopper(3, new ItemStack[] { hopper }, new int[] { 1 }));
        
        TConstructRegistry.registerActiveToolMod(new TSActiveOmniMod());
    }
}
