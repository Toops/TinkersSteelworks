package toops.tsteelworks.common.core;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import mantle.lib.TabTools;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.oredict.OreDictionary;
import nf.fr.ephys.cookiecore.helpers.InventoryHelper;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.modifiers.tools.ModInteger;
import tconstruct.tools.TinkerTools;
import tconstruct.world.TinkerWorld;
import toops.tsteelworks.TSteelworks;
import toops.tsteelworks.common.blocks.*;
import toops.tsteelworks.common.blocks.logic.*;
import toops.tsteelworks.common.entity.HighGolem;
import toops.tsteelworks.common.entity.SteelGolem;
import toops.tsteelworks.common.entity.projectile.EntityLimestoneBrick;
import toops.tsteelworks.common.entity.projectile.EntityScorchedBrick;
import toops.tsteelworks.common.items.TSManual;
import toops.tsteelworks.common.items.TSMaterialItem;
import toops.tsteelworks.common.items.blocks.*;
import toops.tsteelworks.lib.ModsData;
import toops.tsteelworks.lib.TSRepo;
import toops.tsteelworks.lib.registry.AdvancedSmelting;
import toops.tsteelworks.common.items.TSArmorBasic;
import toops.tsteelworks.common.modifier.TSActiveOmniMod;

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
		registerItems();
		registerBlocks();
		setupCreativeTabs();
		registerModifiers();

		ModsData.loadSharedData();

		oreRegistry();
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

		TConstructRegistry.addItemStackToDirectory("scorchedBrick", new ItemStack(materialsTS, 1, 0));

		bookManual = new TSManual();
		GameRegistry.registerItem(bookManual, "tsteelManual");

		if (ConfigCore.enableSteelArmor) {
			materialSteel = EnumHelper.addArmorMaterial("STEEL", 25, new int[] {3, 7, 5, 3}, 10);
			materialSteel.customCraftingMaterial = TConstructRegistry.getItemStack("ingotSteel").getItem();

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

		// TConstuct
		OreDictionary.registerOre("blockGraveyardDirt", new ItemStack(TinkerTools.craftedSoil, 1, 3));

		if (TinkerWorld.oreBerries != null)
			OreDictionary.registerOre("oreberryEssence", new ItemStack(TinkerWorld.oreBerries, 1, 5));
	}

	/**
	 * Register mixer materials
	 */
	public static void registerMixerMaterials() {
		AdvancedSmelting.registerMixItem("dustGunpowder", AdvancedSmelting.MixData.MixType.OXYDIZER, 1, 33);
		AdvancedSmelting.registerMixItem("dustSulphur", AdvancedSmelting.MixData.MixType.OXYDIZER, 1, 29);
		AdvancedSmelting.registerMixItem("dustSugar", AdvancedSmelting.MixData.MixType.OXYDIZER, 1, 62);
		AdvancedSmelting.registerMixItem("fuelCoal", AdvancedSmelting.MixData.MixType.OXYDIZER, 1, 43);
		AdvancedSmelting.registerMixItem("coal", AdvancedSmelting.MixData.MixType.OXYDIZER, 1, 43);
		AdvancedSmelting.registerMixItem("dustCoal", AdvancedSmelting.MixData.MixType.OXYDIZER, 1, 37);
		AdvancedSmelting.registerMixItem("dyeLime", AdvancedSmelting.MixData.MixType.OXYDIZER, 1, 37);

		AdvancedSmelting.registerMixItem("dustRedstone", AdvancedSmelting.MixData.MixType.PURIFIER, 1, 65);
		AdvancedSmelting.registerMixItem("dustManganese", AdvancedSmelting.MixData.MixType.PURIFIER, 1, 47);
		AdvancedSmelting.registerMixItem("oreManganese", AdvancedSmelting.MixData.MixType.PURIFIER, 1, 51);
		AdvancedSmelting.registerMixItem("dustAluminum", AdvancedSmelting.MixData.MixType.PURIFIER, 1, 60);
		AdvancedSmelting.registerMixItem("dustAluminium", AdvancedSmelting.MixData.MixType.PURIFIER, 1, 60);
		AdvancedSmelting.registerMixItem("dyeWhite", AdvancedSmelting.MixData.MixType.PURIFIER, 1, 37);
		AdvancedSmelting.registerMixItem("oreberryEssence", AdvancedSmelting.MixData.MixType.PURIFIER, 1, 27);
		AdvancedSmelting.registerMixItem("dustSaltpeter", AdvancedSmelting.MixData.MixType.PURIFIER, 1, 30);
		AdvancedSmelting.registerMixItem("dustSaltpetre", AdvancedSmelting.MixData.MixType.PURIFIER, 1, 30);

		AdvancedSmelting.registerMixItem("blockSand", AdvancedSmelting.MixData.MixType.REDUCER, 1, 100);
		AdvancedSmelting.registerMixItem("hambone", AdvancedSmelting.MixData.MixType.REDUCER, 1, 73);
		AdvancedSmelting.registerMixItem("blockGraveyardDirt", AdvancedSmelting.MixData.MixType.REDUCER, 1, 59);
	}

	/**
	 * Initialize the Steelworks creative tab with an icon.
	 */
	private static void setupCreativeTabs() {
		creativeTab.init(TConstructRegistry.getItemStack("ingotSteel"));
	}

	public static void createEntities() {
		EntityRegistry.registerModEntity(EntityScorchedBrick.class, "ScorchedBrick", 0, TSteelworks.instance, 32, 3, true);
		EntityRegistry.registerModEntity(EntityLimestoneBrick.class, "LimestoneBrick", 1, TSteelworks.instance, 32, 3, true);

		// TODO: Register with registerModEntity instead. We do this because registerModEntity does not seemingly add a mob spawner egg.
		EntityRegistry.registerGlobalEntityID(HighGolem.class, "HighGolem", EntityRegistry.findGlobalUniqueEntityId(), 0x171717, 0x614D3C);
		EntityRegistry.registerGlobalEntityID(SteelGolem.class, "SteelGolem", EntityRegistry.findGlobalUniqueEntityId(), 0x171717, 0x614D3C);
	}

	public static void registerModifiers() {
		ItemStack hopper = new ItemStack(Blocks.hopper);
		ItemStack enderpearl = new ItemStack(Items.ender_pearl);
		String modifierName = StatCollector.translateToLocal("modifier.tool.vacuous");

		ModifyBuilder.registerModifier(new ModInteger(new ItemStack[] {hopper, enderpearl}, 50, "Vacuous", 5, EnumChatFormatting.GREEN.toString(), modifierName));

		TConstructRegistry.registerActiveToolMod(new TSActiveOmniMod());
	}
}
