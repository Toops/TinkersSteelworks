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
import tconstruct.util.RecipeRemover;
import cpw.mods.fml.common.registry.GameRegistry;


public class TSRecipes {
    static String[] patBlock = { "###", "###", "###" };
    static String[] patHollow = { "###", "# #", "###" };
    static String[] patSurround = { "###", "#m#", "###" };
	/**
	 * Craft steelforge (high oven) components
	 */
    public static void craftTableSteelforgeComponents ()
    {
        // High Oven Components Recipes
		ItemStack brick = new ItemStack(TSContent.materials, 1, 0);
        GameRegistry.addRecipe(new ItemStack(TSContent.steelforge, 1, 0), patHollow, '#', brick); //Controller
        GameRegistry.addRecipe(new ItemStack(TSContent.steelforge, 1, 1), "b b", "b b", "b b", 'b', brick); //Drain
        GameRegistry.addRecipe(new ItemStack(TSContent.steelforge, 1, 2), "bb", "bb", 'b', brick); //Bricks
    }
	/**
	 * Craft monoatomic gold materials (blocks, ingots, nuggets)
	 */
    public static void craftTableMonoatomicGold ()
    {
		// Craft block from ingots
        GameRegistry.addRecipe(new ItemStack(TSContent.metalBlock, 1, 0), patBlock, '#', new ItemStack(TSContent.materials, 1, 1)); 
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
	public static void craftTableSteelArmor () {
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.helmetSteel, 
        		new Object[] { "sss", "s s", 's', TConstructRegistry.getItemStack("ingotSteel") }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.chestplateSteel, 
        		new Object[] { "s s", "sss", "sss", 's', TConstructRegistry.getItemStack("ingotSteel") }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.leggingsSteel, 
        		new Object[] { "sss", "s s", "s s", 's', TConstructRegistry.getItemStack("ingotSteel") }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TSContent.bootsSteel, 
        		new Object[] { "s s", "s s", 's', TConstructRegistry.getItemStack("ingotSteel") }));
	}
	/**
	 * Change flint & steel recipe to use steel
	 */
	public static void changeFlintAndSteelRecipe () {
		// Thanks, TConstruct RecipeRemover!
		RecipeRemover.removeShapedRecipe(new ItemStack(Item.flintAndSteel));
		GameRegistry.addRecipe(new ItemStack(Item.flintAndSteel), "s ",
				" f", 's', TConstructRegistry.getItemStack("ingotSteel"),
				'f', new ItemStack(Item.flint));
	}
	/**
	 * Cast scorched brick item from standard brick item
	 */
	public static void castTableScorchedBrick ()
	{
		ItemStack is = new ItemStack(TSContent.materials, 1, 0);
		FluidStack fs = new FluidStack(TContent.moltenStoneFluid, TConstruct.chunkLiquidValue / 4);
		TConstruct.tableCasting.addCastingRecipe(is, fs, new ItemStack(Item.brick), true, 50);
	}
	/**
	 * Cast monoatomic gold to bucket
	 */
	public static void castTableMonoatomicGold () 
	{
        ItemStack bucket = new ItemStack(Item.bucketEmpty);
        TConstruct.tableCasting.addCastingRecipe(new ItemStack(TSContent.buckets, 1, 0), new FluidStack(TSContent.fluids[0], FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);	
	}
	/**
	 * Cast scorched brick block from standard brick block
	 */
	public static void castBasinScorchedBrickBlock () 
	{
		ItemStack is = new ItemStack(TSContent.steelforge, 1, 2);
		FluidStack fs = new FluidStack(TContent.moltenStoneFluid, TConstruct.chunkLiquidValue);
		TConstruct.basinCasting.addCastingRecipe(is, fs, new ItemStack(Block.brick), true, 100);
	}
}
