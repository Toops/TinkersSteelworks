package tsteelworks.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tconstruct.TConstruct;
import tconstruct.common.TContent;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.crafting.Smeltery;
import tconstruct.util.RecipeRemover;
import tsteelworks.lib.crafting.HighOvenSmelting;
import cpw.mods.fml.common.registry.GameRegistry;


public class TSRecipes
{
	static String[]	patBlock	= { "###", "###", "###" };
	static String[]	patHollow	= { "###", "# #", "###" };
	static String[]	patSurround	= { "###", "#m#", "###" };
	
	/**
	 * Craft highoven (high oven) components
	 */
	public static void craftTableHighOvenComponents ()
	{
		// High Oven Components Recipes
		final ItemStack brick = new ItemStack(TSContent.materials, 1, 0);
		GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 0), patHollow, '#', brick); // Controller
		GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 1), "b b", "b b", "b b", 'b', brick); // Drain
		GameRegistry.addRecipe(new ItemStack(TSContent.highoven, 1, 2), "bb", "bb", 'b', brick); // Bricks
	}

	public static void highOvenSteel ()
	{
		// Ore Processing
		HighOvenSmelting.addMelting(Block.oreIron, 0, 800, new FluidStack(TContent.moltenSteelFluid,
				TConstruct.ingotLiquidValue / 2));
		HighOvenSmelting.addMelting(TContent.oreGravel, 0, 800, new FluidStack(TContent.moltenSteelFluid,
				TConstruct.ingotLiquidValue / 2));
		HighOvenSmelting.addMelting(FluidType.Steel, TConstructRegistry.getItemStack("oreberryIron"), -100,
				TConstruct.nuggetLiquidValue / 2);
		// Prefab Smelting
		/*
		HighOvenSmelting.addMelting(new ItemStack(TContent.metalBlock, 1, 9), 400, new FluidStack(TContent.moltenSteelFluid,
				TConstruct.blockLiquidValue / 2));
		HighOvenSmelting.addMelting(FluidType.Steel, TConstructRegistry.getItemStack("ingotSteel"), 0,
				TConstruct.ingotLiquidValue / 2);
		HighOvenSmelting.addMelting(FluidType.Steel, TConstructRegistry.getItemStack("nuggetSteel"), 0,
				TConstruct.nuggetLiquidValue / 2);
		*/
	}
	
	public static void highOvenMonoatomicGold ()
	{
		HighOvenSmelting.addMelting(Block.oreGold, 0, 650, new FluidStack(TSContent.moltenMonoatomicGoldFluid,
				TConstruct.ingotLiquidValue / 2));
		HighOvenSmelting.addMelting(TContent.oreGravel, 1, 650, new FluidStack(TSContent.moltenMonoatomicGoldFluid,
				TConstruct.ingotLiquidValue / 2));
		//HighOvenSmelting.addMelting(FluidType.Steel, TConstructRegistry.getItemStack("oreberryGold"), -50,
		//		TConstruct.nuggetLiquidValue / 2);
	}
	
	public static void smelteryMonoatomicGold ()
	{
		Smeltery.addMelting(TSContent.metalBlock, 0, 100, new FluidStack(TSContent.fluids[0],
				TConstruct.blockLiquidValue));
		Smeltery.addMelting(new ItemStack(TSContent.materials, 1, 1), 100, new FluidStack(TSContent.moltenMonoatomicGoldFluid,
				TConstruct.ingotLiquidValue));
		Smeltery.addMelting(new ItemStack(TSContent.materials, 1, 2), 100, new FluidStack(TSContent.moltenMonoatomicGoldFluid,
				TConstruct.nuggetLiquidValue));
	}
	
	/**
	 * Craft monoatomic gold materials (blocks, ingots, nuggets)
	 */
	public static void craftTableMonoatomicGold ()
	{
		// Craft block from ingots
		GameRegistry
				.addRecipe(new ItemStack(TSContent.metalBlock, 1, 0), patBlock, '#', new ItemStack(TSContent.materials, 1, 1));
		// Craft ingots from block
		GameRegistry.addRecipe(new ItemStack(TSContent.materials, 9, 1), "m", 'm', new ItemStack(TSContent.metalBlock, 1, 0));
		// Craft ingot from nuggets
		GameRegistry.addRecipe(new ItemStack(TSContent.materials, 1, 1), patBlock, '#', new ItemStack(TSContent.materials, 1, 2));
		// Craft nuggets from ingot
		GameRegistry.addRecipe(new ItemStack(TSContent.materials, 9, 2), "m", 'm', new ItemStack(TSContent.materials, 1, 1));
	}

	/**
	 * Craft steel armor
	 */
	public static void craftTableSteelArmor ()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.helmetSteel, new Object[] { "sss", "s s", 's',
				TConstructRegistry.getItemStack("ingotSteel") }));
		GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.chestplateSteel, new Object[] { "s s", "sss", "sss", 's',
				TConstructRegistry.getItemStack("ingotSteel") }));
		GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.leggingsSteel, new Object[] { "sss", "s s", "s s", 's',
				TConstructRegistry.getItemStack("ingotSteel") }));
		GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.bootsSteel, new Object[] { "s s", "s s", 's',
				TConstructRegistry.getItemStack("ingotSteel") }));
	}

	/**
	 * Change flint & steel recipe to use steel
	 */
	public static void changeFlintAndSteelRecipe ()
	{
		// Thanks, TConstruct RecipeRemover!
		RecipeRemover.removeShapedRecipe(new ItemStack(Item.flintAndSteel));
		GameRegistry.addRecipe(new ItemStack(Item.flintAndSteel), "s ", " f", 's', TConstructRegistry.getItemStack("ingotSteel"),
				'f', new ItemStack(Item.flint));
	}

	/**
	 * Cast scorched brick item from standard brick item
	 */
	public static void castTableScorchedBrick ()
	{
		final ItemStack scorchedBrick = new ItemStack(TSContent.materials, 1, 0);
		final FluidStack stoneCastBrick = new FluidStack(TContent.moltenStoneFluid, TConstruct.chunkLiquidValue / 4);
		TConstruct.tableCasting.addCastingRecipe(scorchedBrick, stoneCastBrick, new ItemStack(Item.brick), true, 50);
	}

	/**
	 * Cast monoatomic gold to bucket
	 */
	public static void castTableMonoatomicGold ()
	{
		final ItemStack monoGoldBucket = new ItemStack(TSContent.buckets, 1, 0);
		final FluidStack monoGoldFillBucket =  new FluidStack(TSContent.moltenMonoatomicGoldFluid, FluidContainerRegistry.BUCKET_VOLUME);
		final ItemStack emptyBucket = new ItemStack(Item.bucketEmpty);
		final ItemStack monoGoldIngot = new ItemStack(TSContent.materials, 1, 1);
		FluidStack monoGoldFillIngot = new FluidStack(TSContent.moltenMonoatomicGoldFluid, TConstruct.ingotLiquidValue);
		ItemStack cast = new ItemStack(TContent.metalPattern, 1, 0);
		
		TConstruct.tableCasting.addCastingRecipe(monoGoldBucket, monoGoldFillBucket, emptyBucket, true, 10);
		TConstruct.tableCasting.addCastingRecipe(monoGoldIngot, monoGoldFillIngot, cast, 80);
	}

	/**
	 * Cast scorched brick block from standard brick block
	 */
	public static void castBasinScorchedBrickBlock ()
	{
		final ItemStack scorchedBrickBlock = new ItemStack(TSContent.highoven, 1, 2);
		final FluidStack stoneCastBrick = new FluidStack(TContent.moltenStoneFluid, TConstruct.chunkLiquidValue);
		TConstruct.basinCasting.addCastingRecipe(scorchedBrickBlock, stoneCastBrick, new ItemStack(Block.brick), true, 100);
	}
	
	public static void castBasinMonoatomicGold ()
	{
		final ItemStack is = new ItemStack(TSContent.metalBlock, 1, 0);
		final FluidStack fs = new FluidStack(TSContent.moltenMonoatomicGoldFluid, TConstruct.blockLiquidValue);
		TConstruct.basinCasting.addCastingRecipe(is, fs, 100);
		
		TConstruct.basinCasting.addCastingRecipe(is, fs, null, true, 100); //gold
	}
}
