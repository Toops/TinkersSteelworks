package toops.tsteelworks.common.core;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import mantle.lib.TabTools;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.oredict.OreDictionary;
import nf.fr.ephys.cookiecore.common.registryUtil.FuelHandler;
import nf.fr.ephys.cookiecore.helpers.InventoryHelper;
import toops.tsteelworks.TSteelworks;
import toops.tsteelworks.api.highoven.IMixAgentRegistry;
import toops.tsteelworks.common.blocks.*;
import toops.tsteelworks.common.blocks.logic.*;
import toops.tsteelworks.common.entity.HighGolem;
import toops.tsteelworks.common.entity.SteelGolem;
import toops.tsteelworks.common.entity.projectile.EntityLimestoneBrick;
import toops.tsteelworks.common.entity.projectile.EntityScorchedBrick;
import toops.tsteelworks.common.items.TSArmorBasic;
import toops.tsteelworks.common.items.TSManual;
import toops.tsteelworks.common.items.TSMaterialItem;
import toops.tsteelworks.common.items.blocks.*;
import toops.tsteelworks.lib.ModsData;
import toops.tsteelworks.lib.TSRepo;

import java.util.List;

public class TSContent {
	public static Item materialsTS;

	public static Item bookManual;
	public static Item helmetSteel;
	public static Item chestplateSteel;
	public static Item leggingsSteel;
	public static Item bootsSteel;

	public static Block cementBlock;
	public static Block highoven;
	public static Block scorchedSlab;
	public static Block limestoneBlock;
	public static Block limestoneSlab;
	public static Block tsCharcoalBlock;
	public static Block dustStorageBlock;

	public static Block steamTurbine;

	/** Instance of the fluid we registered, null if we did not */
	public static Fluid steamFluid;

	/** Instance of the fluid we registered, null if we did not */
	public static Fluid moltenLimestoneFluid;

	/** Instance of the fluid we registered, null if we did not */
	public static Fluid liquidCementFluid;

	public static ItemArmor.ArmorMaterial materialSteel;

	public static final TabTools creativeTab = new TabTools(TSRepo.MOD_ID);

	/**
	 * Content Constructor
	 */
	public static void preInit() {
		ModsData.registerFluids();
		
		registerItems();
		registerBlocks();
		setupCreativeTabs();
		oreRegistry();
	}
	
	public static void init() {
	}

	public static void postInit() {
		ModsData.loadModsData();

		registerMixerMaterials();

		createEntities();
	}

	/**
	 * Register Items
	 */
	public static void registerItems() {
		materialsTS = new TSMaterialItem().setUnlocalizedName("tsteelworks.Materials");
		GameRegistry.registerItem(materialsTS, "Materials");

		bookManual = new TSManual();
		GameRegistry.registerItem(bookManual, "tsteelManual");

		if (ConfigCore.enableSteelArmor) {
			List<ItemStack> steels = OreDictionary.getOres("ingotSteel");

			if (steels.size() > 0) {
				materialSteel = EnumHelper.addArmorMaterial("STEEL", 25, new int[]{3, 7, 5, 3}, 10);
				materialSteel.customCraftingMaterial = steels.get(0).getItem();

				helmetSteel = new TSArmorBasic(materialSteel, 0, "steel").setUnlocalizedName("tsteelworks.helmetSteel");
				chestplateSteel = new TSArmorBasic(materialSteel, 1, "steel").setUnlocalizedName("tsteelworks.chestplateSteel");
				leggingsSteel = new TSArmorBasic(materialSteel, 2, "steel").setUnlocalizedName("tsteelworks.leggingsSteel");
				bootsSteel = new TSArmorBasic(materialSteel, 3, "steel").setUnlocalizedName("tsteelworks.bootsSteel");

				GameRegistry.registerItem(helmetSteel, "helmetSteel");
				GameRegistry.registerItem(chestplateSteel, "chestplateSteel");
				GameRegistry.registerItem(leggingsSteel, "leggingsSteel");
				GameRegistry.registerItem(bootsSteel, "bootsSteel");
			}
		}
	}

	/**
	 * Register Blocks and TileEntities (Logic)
	 */
	public static void registerBlocks() {
		/* High Oven */
		highoven = new HighOvenBlock().setBlockName("HighOven");
		GameRegistry.registerBlock(highoven, HighOvenItemBlock.class, "HighOven");
		GameRegistry.registerTileEntity(HighOvenLogic.class, "TSteelworks.HighOven");
		GameRegistry.registerTileEntity(HighOvenDrainLogic.class, "TSteelworks.HighOvenDrain");
		GameRegistry.registerTileEntity(HighOvenDuctLogic.class, "TSteelworks.HighOvenDuct");
		GameRegistry.registerTileEntity(DeepTankLogic.class, "TSteelworks.DeepTank");
		GameRegistry.registerTileEntity(TSMultiServantLogic.class, "TSteelworks.Servants");

		/* Slabs */
		scorchedSlab = new ScorchedSlab().setBlockName("ScorchedSlab");
		scorchedSlab.stepSound = Block.soundTypeStone;
		GameRegistry.registerBlock(scorchedSlab, ScorchedSlabItemBlock.class, "ScorchedSlab");

		dustStorageBlock = new DustStorageBlock().setBlockName("tsteelworks.dustblock");
		GameRegistry.registerBlock(dustStorageBlock, DustStorageItemBlock.class, "dustStorage");

		limestoneBlock = new LimestoneBlock().setBlockName("Limestone");
		GameRegistry.registerBlock(limestoneBlock, LimestoneItemBlock.class, "Limestone");

		limestoneSlab = new LimestoneSlab().setBlockName("LimestoneSlab").setStepSound(Block.soundTypeStone);
		GameRegistry.registerBlock(limestoneSlab, LimestoneSlabItemBlock.class, "LimestoneSlab");

		cementBlock = new CementBlock().setBlockName("tsteelworks.cement").setStepSound(Block.soundTypeStone);
		GameRegistry.registerBlock(cementBlock, CementItemBlock.class, "Cement");

		steamTurbine = new SteamTurbineBlock().setBlockName("Machine.Turbine").setStepSound(Block.soundTypeMetal).setCreativeTab(creativeTab);
		GameRegistry.registerBlock(steamTurbine, steamTurbine.getUnlocalizedName());
		GameRegistry.registerTileEntity(SteamTurbineLogic.class, steamTurbine.getUnlocalizedName());

		TSContent.tsCharcoalBlock = new TSBaseBlock(Material.rock, 5.0f, new String[] {"charcoal_block"}).setBlockName("tsteelworks.blocks.charcoal");
		GameRegistry.registerBlock(TSContent.tsCharcoalBlock, "blockCharcoal");
		Blocks.fire.setFireInfo(TSContent.tsCharcoalBlock, 15, 30);
		OreDictionary.registerOre("blockCharcoal", TSContent.tsCharcoalBlock);
		GameRegistry.registerFuelHandler(new FuelHandler(new ItemStack(TSContent.tsCharcoalBlock), 15000));
	}

	public static void oreRegistry() {
		// Vanilla
		InventoryHelper.ensureOreIsRegistered("blockSand", new ItemStack(Blocks.sand));
		InventoryHelper.ensureOreIsRegistered("dustRedstone", new ItemStack(Items.redstone));
		InventoryHelper.ensureOreIsRegistered("dustGunpowder", new ItemStack(Items.gunpowder));
		InventoryHelper.ensureOreIsRegistered("dustSugar", new ItemStack(Items.sugar));
		InventoryHelper.ensureOreIsRegistered("coal", new ItemStack(Items.coal, 1, 0));
		OreDictionary.registerOre("fuelCoal", new ItemStack(Items.coal, 1, 0));
		InventoryHelper.ensureOreIsRegistered("fuelCharcoal", new ItemStack(Items.coal, 1, 1));
		InventoryHelper.ensureOreIsRegistered("itemClay", new ItemStack(Items.clay_ball));

		// TSteelworks
		OreDictionary.registerOre("blockGunpowder", new ItemStack(dustStorageBlock, 1, 0));
		OreDictionary.registerOre("blockSugar", new ItemStack(dustStorageBlock, 1, 1));

		for (int i = 0; i < ((LimestoneBlock) limestoneBlock).textureNames.length; i++) {
			OreDictionary.registerOre("blockLimestone", new ItemStack(limestoneBlock, 1, i));
		}
	}

	/**
	 * Register mixer materials
	 */
	public static void registerMixerMaterials() {
		IMixAgentRegistry registry = IMixAgentRegistry.INSTANCE;

		registry.registerAgent("dustGunpowder", IMixAgentRegistry.AgentType.OXIDIZER, 1, 33);
		registry.registerAgent("dustSulphur", IMixAgentRegistry.AgentType.OXIDIZER, 1, 29);
		registry.registerAgent("dustSugar", IMixAgentRegistry.AgentType.OXIDIZER, 1, 62);
		registry.registerAgent("fuelCoal", IMixAgentRegistry.AgentType.OXIDIZER, 1, 43);
		registry.registerAgent("coal", IMixAgentRegistry.AgentType.OXIDIZER, 1, 43);
		registry.registerAgent("dustCoal", IMixAgentRegistry.AgentType.OXIDIZER, 1, 37);
		registry.registerAgent("dyeLime", IMixAgentRegistry.AgentType.OXIDIZER, 1, 37);

		registry.registerAgent("dustRedstone", IMixAgentRegistry.AgentType.PURIFIER, 1, 65);
		registry.registerAgent("dustManganese", IMixAgentRegistry.AgentType.PURIFIER, 1, 47);
		registry.registerAgent("oreManganese", IMixAgentRegistry.AgentType.PURIFIER, 1, 51);
		registry.registerAgent("dustAluminum", IMixAgentRegistry.AgentType.PURIFIER, 1, 60);
		registry.registerAgent("dustAluminium", IMixAgentRegistry.AgentType.PURIFIER, 1, 60);
		registry.registerAgent("dustBone", IMixAgentRegistry.AgentType.PURIFIER, 1, 37);
		registry.registerAgent("oreberryEssence", IMixAgentRegistry.AgentType.PURIFIER, 1, 27);
		registry.registerAgent("dustSaltpeter", IMixAgentRegistry.AgentType.PURIFIER, 1, 30);
		registry.registerAgent("dustSaltpetre", IMixAgentRegistry.AgentType.PURIFIER, 1, 30);

		registry.registerAgent("blockSand", IMixAgentRegistry.AgentType.REDUCER, 1, 100);
		registry.registerAgent("hambone", IMixAgentRegistry.AgentType.REDUCER, 1, 73);
		registry.registerAgent("blockGraveyardDirt", IMixAgentRegistry.AgentType.REDUCER, 1, 59);
	}

	/**
	 * Initialize the Steelworks creative tab with an icon.
	 */
	private static void setupCreativeTabs() {
		List<ItemStack> steels = OreDictionary.getOres("ingotSteel");

		if (steels.size() == 0)
			creativeTab.init(new ItemStack(TSContent.materialsTS));
		else
			creativeTab.init(steels.get(0));
	}

	public static void createEntities() {
		EntityRegistry.registerModEntity(EntityScorchedBrick.class, "ScorchedBrick", 0, TSteelworks.instance, 32, 3, true);
		EntityRegistry.registerModEntity(EntityLimestoneBrick.class, "LimestoneBrick", 1, TSteelworks.instance, 32, 3, true);

		// TODO: Register with registerModEntity instead. We do this because registerModEntity does not seemingly add a mob spawner egg.
		EntityRegistry.registerGlobalEntityID(HighGolem.class, "HighGolem", EntityRegistry.findGlobalUniqueEntityId(), 0x171717, 0x614D3C);
		EntityRegistry.registerGlobalEntityID(SteelGolem.class, "SteelGolem", EntityRegistry.findGlobalUniqueEntityId(), 0x171717, 0x614D3C);
	}
}
