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
import tconstruct.library.TConstructRegistry;
import tconstruct.library.armor.EnumArmorPart;
import tconstruct.library.crafting.FluidType;
import tsteelworks.blocks.SteelforgeBlock;
import tsteelworks.blocks.TSBaseFluid;
import tsteelworks.blocks.TSMetalBlock;
import tsteelworks.blocks.logic.HighOvenDrainLogic;
import tsteelworks.blocks.logic.HighOvenLogic;
import tsteelworks.items.TSArmorBasic;
import tsteelworks.items.TSExoArmor;
import tsteelworks.items.TSFilledBucket;
import tsteelworks.items.TSMaterialItem;
import tsteelworks.items.TSMetalPattern;
import tsteelworks.items.blocks.SteelforgeItemBlock;
import tsteelworks.items.blocks.TSMetalItemBlock;
import tsteelworks.lib.TSteelworksRegistry;
import tsteelworks.lib.config.ConfigCore;
import tsteelworks.lib.crafting.SteelforgeCrafting;
import cpw.mods.fml.common.registry.GameRegistry;

public class TSContent {
	// ---- ITEMS
	// --------------------------------------------------------------------------
	public static Item buckets;
	public static Item materials;
	public static Item woodPattern;
	public static Item metalPattern;
    // Armor - Basic
    public static Item helmetSteel;
    public static Item chestplateSteel;
    public static Item leggingsSteel;
    public static Item bootsSteel;
    public static EnumArmorMaterial materialSteel;
    //Armor - Exosuit
    public static Item exoGogglesSteel;
    public static Item exoChestSteel;
    public static Item exoPantsSteel;
    public static Item exoShoesSteel;
	// ---- BLOCKS
	// -------------------------------------------------------------------------
	public static Block steelforge;
	public static Block metalBlock;
	// ---- FLUIDS
	// -------------------------------------------------------------------------
    public static Material liquidMetal;
    public static Fluid moltenMonoatomicGoldFluid;
    public static Block moltenMonoatomicGoldBlock;
    
	public TSContent() 
	{
		registerItems();
		registerBlocks();
		registerFluids();
		registerMaterials();
		addCraftingRecipes();
		setupCreativeTabs();
	}

	private void setupCreativeTabs() 
	{
		TSteelworksRegistry.Steelforge.init(TConstructRegistry.getItemStack("ingotSteel"));
	}

    public static Fluid[] fluids = new Fluid[1];
    public static Block[] fluidBlocks = new Block[1];
    
	void registerItems() 
	{
		materials = new TSMaterialItem(ConfigCore.materials).setUnlocalizedName("tsteelworks.Materials");
		metalPattern = new TSMetalPattern(ConfigCore.metalPattern, "cast_", "materials/").setUnlocalizedName("tsteelworks.MetalPattern");
		buckets = new TSFilledBucket(ConfigCore.buckets);
		
		GameRegistry.registerItem(materials, "Materials");
        GameRegistry.registerItem(buckets, "buckets");
        GameRegistry.registerItem(metalPattern, "metalPattern");
        
        String[] materialStrings = { "scorchedBrick", "ingotMonoatomicGold", "nuggetMonoatomicGold" };
        String[] patternTypes = { "chainlink" };
        
        for (int i = 0; i < materialStrings.length; i++)
        {
            TSteelworksRegistry.addItemStackToDirectory(materialStrings[i], new ItemStack(materials, 1, i));
        }
        
        for (int i = 0; i < patternTypes.length; i++)
        {
        	TSteelworksRegistry.addItemStackToDirectory(patternTypes[i] + "Cast", new ItemStack(metalPattern, 1, i));
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
    
	void registerBlocks() 
	{
		// Steel Forge (High Oven)
		steelforge = new SteelforgeBlock(ConfigCore.steelforge).setUnlocalizedName("Steelforge");
		GameRegistry.registerBlock(steelforge, SteelforgeItemBlock.class, "Steelforge");
		GameRegistry.registerTileEntity(HighOvenLogic.class, "TSteelworks.Steelforge");
		GameRegistry.registerTileEntity(HighOvenDrainLogic.class, "TSteelworks.SteelforgeDrain");
		GameRegistry.registerTileEntity(MultiServantLogic.class, "TSteelworks.Servants");
		
		// Metal Blocks
        metalBlock = new TSMetalBlock(ConfigCore.metalBlock, Material.iron, 10.0F).setUnlocalizedName("tsteelworks.metalblock");
        metalBlock.stepSound = Block.soundMetalFootstep;
        GameRegistry.registerBlock(metalBlock, TSMetalItemBlock.class, "MetalBlock");
	}

	void registerFluids()
	{
        liquidMetal = new MaterialLiquid(MapColor.tntColor);
        // Monoatomic Gold
        moltenMonoatomicGoldFluid = new Fluid("monoatomicgold.molten");
        if (!FluidRegistry.registerFluid(moltenMonoatomicGoldFluid))
        	moltenMonoatomicGoldFluid = FluidRegistry.getFluid("monoatomicgold.molten");
        moltenMonoatomicGoldBlock = new TSBaseFluid(ConfigCore.moltenMonoatomicGold, moltenMonoatomicGoldFluid, Material.lava, "liquid_monoatomicgold").setUnlocalizedName("metal.molten.monoatomicgold").setLightValue(0.6f).setLightOpacity(15);
        GameRegistry.registerBlock(moltenMonoatomicGoldBlock, "metal.molten.monoatomicgold");
        fluids[0] = moltenMonoatomicGoldFluid;
        fluidBlocks[0] = moltenMonoatomicGoldBlock;
        moltenMonoatomicGoldFluid.setBlockID(moltenMonoatomicGoldBlock).setLuminosity(12).setDensity(0).setViscosity(1000).setTemperature(1300);
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenMonoatomicGoldFluid, 1000), new ItemStack(buckets, 1, 0), new ItemStack(Item.bucketEmpty)));
	}


	void registerMaterials() { }

    public void oreRegistry()
    {
        OreDictionary.registerOre("ingotMonoatomicGold", new ItemStack(materials, 1, 1));
        OreDictionary.registerOre("nuggetMonoatomicGold", new ItemStack(materials, 1, 2));
        OreDictionary.registerOre("blockMonoatomicGold", new ItemStack(metalBlock, 1, 0));
    }
	
	void addCraftingRecipes() {
		addRecipesForToolBuilder();
		addRecipesForTableCasting();
		addRecipesForBasinCasting();
		addRecipesForHighOven();
		addRecipesForSmeltery();
		addRecipesForSteelforge();
		addRecipesForChisel();
		addRecipesForFurnace();
		addRecipesForCraftingTable();
		addRecipesForDryingRack();
	}

	private void addRecipesForToolBuilder() { }

	private void addRecipesForTableCasting() 
	{
		TSRecipes.castTableScorchedBrick();
		TSRecipes.castTableMonoatomicGold();
	}

	private void addRecipesForBasinCasting() 
	{
		TSRecipes.castBasinScorchedBrickBlock();
	}

	void addRecipesForHighOven() { }

	void addRecipesForSmeltery() { }
	
	void addRecipesForSteelforge() 
	{
        SteelforgeCrafting.addMelting(Block.oreIron, 0, 600, new FluidStack(TContent.moltenSteelFluid, TConstruct.ingotLiquidValue / 2));
        SteelforgeCrafting.addMelting(TContent.oreGravel, 0, 600, new FluidStack(TContent.moltenSteelFluid, TConstruct.ingotLiquidValue / 2));
        SteelforgeCrafting.addMelting(FluidType.Steel, TConstructRegistry.getItemStack("oreberryIron"), -300, TConstruct.nuggetLiquidValue / 2);
	}

	void addRecipesForChisel() { }

	void addRecipesForFurnace() { }

	void addRecipesForCraftingTable() 
	{
        TSRecipes.craftTableSteelforgeComponents();
        TSRecipes.craftTableMonoatomicGold();
		if (ConfigCore.hardcoreFlintAndSteel) TSRecipes.changeFlintAndSteelRecipe();
        if (ConfigCore.enableSteelArmor) TSRecipes.craftTableSteelArmor();
	}

	void addRecipesForDryingRack() { }
}
