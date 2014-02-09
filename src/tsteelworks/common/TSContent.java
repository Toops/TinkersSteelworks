package tsteelworks.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
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
import tconstruct.TConstruct;
import tconstruct.blocks.logic.MultiServantLogic;
import tconstruct.common.TContent;
import tconstruct.items.Pattern;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.armor.EnumArmorPart;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.crafting.Smeltery;
import tconstruct.library.util.IPattern;
import tsteelworks.TSteelworks;
import tsteelworks.blocks.HighOvenBlock;
import tsteelworks.blocks.TSBaseFluid;
import tsteelworks.blocks.TSMetalBlock;
import tsteelworks.blocks.logic.HighOvenDrainLogic;
import tsteelworks.blocks.logic.HighOvenLogic;
import tsteelworks.items.Ring;
import tsteelworks.items.TSArmorBasic;
import tsteelworks.items.TSExoArmor;
import tsteelworks.items.TSFilledBucket;
import tsteelworks.items.TSMaterialItem;
import tsteelworks.items.TSMetalPattern;
import tsteelworks.items.TSWoodPattern;
import tsteelworks.items.blocks.HighOvenItemBlock;
import tsteelworks.items.blocks.TSMetalItemBlock;
import tsteelworks.lib.ConfigCore;
import tsteelworks.lib.TSteelworksRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class TSContent
{
    // ---- ITEMS
    // --------------------------------------------------------------------------
    public static Item buckets;
    public static Item materialsTS;
    public static Item woodPatternTS;
    public static Item metalPatternTS;
    
    public static Item ring;
    // Armor - Basic
    public static Item helmetSteel;
    public static Item chestplateSteel;
    public static Item leggingsSteel;
    public static Item bootsSteel;
    public static EnumArmorMaterial materialSteel;
    // Armor - Exosuit
    public static Item exoGogglesSteel;
    public static Item exoChestSteel;
    public static Item exoPantsSteel;
    public static Item exoShoesSteel;
    // ---- BLOCKS
    // -------------------------------------------------------------------------
    public static Block highoven;
    public static Block metalBlockTS;
    // ---- FLUIDS
    // -------------------------------------------------------------------------
    public static Material liquidMetalTS;
    public static Fluid moltenMonoatomicGoldFluid;
    public static Block moltenMonoatomicGold;

    /**
     * Content Constructor
     */
    public TSContent()
    {
        registerItems();
        registerBlocks();
        registerFluids();
        registerMaterials();
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
     * Fluids array
     */
    public static Fluid[] fluids = new Fluid[1];

    /**
     * Fluid blocks array
     */
    public static Block[] fluidBlocks = new Block[1];

    /**
     * Register Items
     */
    void registerItems ()
    {
        materialsTS = new TSMaterialItem(ConfigCore.materials).setUnlocalizedName("tsteelworks.Materials");
        woodPatternTS = new TSWoodPattern(ConfigCore.woodPattern, "pattern_", "materials/").setUnlocalizedName("tsteelworks.WoodPattern");
        metalPatternTS = new TSMetalPattern(ConfigCore.metalPattern, "cast_", "materials/").setUnlocalizedName("tsteelworks.MetalPattern");
        buckets = new TSFilledBucket(ConfigCore.buckets);
        
        GameRegistry.registerItem(materialsTS, "Materials");
        GameRegistry.registerItem(buckets, "buckets");
        GameRegistry.registerItem(woodPatternTS, "woodPatternTS");
        GameRegistry.registerItem(metalPatternTS, "metalPatternTS");
        
        TConstructRegistry.addItemToDirectory("woodPatternTS", woodPatternTS);
        TConstructRegistry.addItemToDirectory("metalPatternTS", metalPatternTS);
        
        // --- Patterns & Casts
        final String[] patternTypes = { "ring" };
        
        for (int i = 0; i < patternTypes.length; i++)
        {
            TConstructRegistry.addItemStackToDirectory(patternTypes[i] + "Pattern", new ItemStack(woodPatternTS, 1, i));
        }
        
        for (int i = 0; i < patternTypes.length; i++)
        {
            TConstructRegistry.addItemStackToDirectory(patternTypes[i] + "Cast", new ItemStack(metalPatternTS, 1, i));
        }
        
        // --- Tool Parts
        ring = new Ring(ConfigCore.ring).setUnlocalizedName("tsteelworks.Ring");

        Item[] toolParts = { ring };
        String[] toolPartStrings = { "ring" };
        
        for (int i = 0; i < toolParts.length; i++)
        {
            GameRegistry.registerItem(toolParts[i], toolPartStrings[i]); 
            TConstructRegistry.addItemToDirectory(toolPartStrings[i], toolParts[i]);
        }
        
        // --- Materials
        final String[] materialStrings = { "scorchedBrick", "ingotMonoatomicGold", "nuggetMonoatomicGold" };
        
        for (int i = 0; i < materialStrings.length; i++)
        {
            TSteelworksRegistry.addItemStackToDirectory(materialStrings[i], new ItemStack(materialsTS, 1, i));
        }
        
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
        // Steel Forge (High Oven)
        highoven = new HighOvenBlock(ConfigCore.highoven).setUnlocalizedName("HighOven");
        GameRegistry.registerBlock(highoven, HighOvenItemBlock.class, "HighOven");
        GameRegistry.registerTileEntity(HighOvenLogic.class, "TSteelworks.HighOven");
        GameRegistry.registerTileEntity(HighOvenDrainLogic.class, "TSteelworks.HighOvenDrain");
        GameRegistry.registerTileEntity(MultiServantLogic.class, "TSteelworks.Servants");
        // Metal Blocks
        metalBlockTS = new TSMetalBlock(ConfigCore.metalBlock, Material.iron, 10.0F).setUnlocalizedName("tsteelworks.metalblock");
        metalBlockTS.stepSound = Block.soundMetalFootstep;
        GameRegistry.registerBlock(metalBlockTS, TSMetalItemBlock.class, "MetalBlock");
    }

    /**
     * Register Fluids by fluid and fluid block
     */
    void registerFluids ()
    {
        liquidMetalTS = new MaterialLiquid(MapColor.tntColor);
        // Monoatomic Gold
        moltenMonoatomicGoldFluid = new Fluid("monoatomicgold.molten");
        if (!FluidRegistry.registerFluid(moltenMonoatomicGoldFluid))
        {
            moltenMonoatomicGoldFluid = FluidRegistry.getFluid("monoatomicgold.molten");
        }
        moltenMonoatomicGold = new TSBaseFluid(ConfigCore.moltenMonoatomicGold, moltenMonoatomicGoldFluid, Material.lava, "liquid_monoatomicgold").setUnlocalizedName("metal.molten.monoatomicgold")
                .setLightValue(0.6f).setLightOpacity(15);
        GameRegistry.registerBlock(moltenMonoatomicGold, "metal.molten.monoatomicgold");
        fluids[0] = moltenMonoatomicGoldFluid;
        fluidBlocks[0] = moltenMonoatomicGold;
        moltenMonoatomicGoldFluid.setBlockID(moltenMonoatomicGold).setLuminosity(12).setDensity(1000).setViscosity(1000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenMonoatomicGoldFluid, 1000), new ItemStack(buckets, 1, 0), new ItemStack(Item.bucketEmpty)));
    }

    /**
     * Register Materials
     * Used for registering tool materialsTS.
     */
    void registerMaterials ()
    {
        PatternBuilder.instance.addToolPattern((IPattern) woodPatternTS);
    }

    /**
     * Register items in the OreDictionary
     */
    public void oreRegistry ()
    {
        OreDictionary.registerOre("ingotMonoatomicGold", new ItemStack(materialsTS, 1, 1));
        OreDictionary.registerOre("nuggetMonoatomicGold", new ItemStack(materialsTS, 1, 2));
        OreDictionary.registerOre("blockMonoatomicGold", new ItemStack(metalBlockTS, 1, 0));
    }

    public static Item[] patternOutputs;
    public static FluidStack[] liquids;
    
    /**
     * Make TSRecipes add all crafting recipes
     */
    void addCraftingRecipes ()
    {
        // TODO: Move to TSRecipes
        addPartMapping(); 
        addRecipesForTableCasting();
        // ---
        TSRecipes.addRecipesSteelMaterial();
        if (ConfigCore.enableMonoatomicGold)
            TSRecipes.addRecipesMonoatomicGoldMaterial();
        TSRecipes.addRecipesScorchedBrickMaterial();
        TSRecipes.addRecipesHighOvenComponents();
        TSRecipes.addRecipesSteelArmor();
        if (ConfigCore.hardcoreFlintAndSteel)
            TSRecipes.changeRecipeFlintAndSteel();
    }
    
    private void addPartMapping ()
    {
        /* Tools */
        patternOutputs = new Item[] { ring };

        for (int mat = 0; mat < 1; mat++)
        {
            for (int meta = 0; meta < patternOutputs.length; meta++)
            {
                if (patternOutputs[meta] != null)
                        TConstructRegistry.addPartMapping(woodPatternTS.itemID, meta, mat, new ItemStack(patternOutputs[meta], 1, mat));
            }
        }
    }
    
    private void addRecipesForTableCasting ()
    {
        patternOutputs = new Item[] { ring };
        LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        liquids = new FluidStack[] { new FluidStack(TContent.moltenIronFluid, 1) };
        int[] liquidDamage = new int[] { 1 }; //ItemStack damage value
        int fluidAmount = 0;
        Fluid fs = null;

        for (int iter = 0; iter < patternOutputs.length; iter++)
        {
            if (patternOutputs[iter] != null)
            {
                ItemStack cast = new ItemStack(metalPatternTS, 1, iter + 1);

                tableCasting.addCastingRecipe(cast, new FluidStack(TContent.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(patternOutputs[iter], 1, Short.MAX_VALUE), false, 50);
                tableCasting.addCastingRecipe(cast, new FluidStack(TContent.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(patternOutputs[iter], 1, Short.MAX_VALUE), false, 50);

                for (int iterTwo = 0; iterTwo < liquids.length; iterTwo++)
                {
                    fs = liquids[iterTwo].getFluid();
                    fluidAmount = ((IPattern) metalPatternTS).getPatternCost(cast) * TConstruct.ingotLiquidValue / 2;
                    ItemStack metalCast = new ItemStack(patternOutputs[iter], 1, liquidDamage[iterTwo]);
                    tableCasting.addCastingRecipe(metalCast, new FluidStack(fs, fluidAmount), cast, 50);
                    //Smeltery.addMelting(FluidType.getFluidType(fs), metalCast, 0, fluidAmount);
                }
            }
        }
    }
}
