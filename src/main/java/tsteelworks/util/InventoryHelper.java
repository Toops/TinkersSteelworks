package tsteelworks.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.command.IEntitySelector;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import nf.fr.ephys.cookiecore.helpers.MathHelper;

import javax.swing.text.html.parser.Entity;
import java.util.List;

public class InventoryHelper {
	public static IInventory getInventoryAt(World world, double x, double y, double z) {
		IInventory inventory = getBlockInventoryAt(world, (int) x, (int) y, (int) z);

		if (inventory == null)
			inventory = getEntityInventoryAt(world, x, y, z);

		return inventory;
	}

	public static IInventory getBlockInventoryAt(World world, int x, int y, int z) {
		// special mojang bad code hotfix yay
		Block block = world.getBlock(x, y, z);
		if (world.getBlock(x, y, z) instanceof BlockChest) {
			IInventory chestInventory = ((BlockChest) block).func_149951_m(world, x, y, z);

			if (chestInventory != null) return chestInventory;
		}

		TileEntity te = world.getTileEntity(x, y, z);

		if (te instanceof IInventory) {
			return (IInventory) te;
		}

		return null;
	}

	// todo: check if the AABB is valid
	@SuppressWarnings("unchecked")
	public static IInventory getEntityInventoryAt(World world, double x, double y, double z) {
		List<IInventory> entities = world.selectEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(x, y, z, x, y, z), IEntitySelector.selectInventories);

		return (IInventory) MathHelper.getRandom(entities);
	}

	public static boolean isBlockEqual(ItemStack stack, World world, int x, int y, int z) {
		return world.getBlock(x, y, z).equals(Block.getBlockFromItem(stack.getItem())) && world.getBlockMetadata(x, y, z) == stack.getItemDamage();
	}

	public static boolean isBlockEqual(String oredictName, World world, int x, int y, int z) {
		int needle = OreDictionary.getOreID(oredictName);

		ItemStack stack = new ItemStack(world.getBlock(x, y, z), 1, world.getBlockMetadata(x, y, z));

		int[] haystack = OreDictionary.getOreIDs(stack);

		for (int id : haystack) {
			if (id == needle) return true;
		}

		return false;
	}

	public static boolean isOre(ItemStack stack) {
		int[] ids = OreDictionary.getOreIDs(stack);

		for (int id : ids) {
			if (OreDictionary.getOreName(id).startsWith("ore"))
				return true;
		}

		return false;
	}
}
