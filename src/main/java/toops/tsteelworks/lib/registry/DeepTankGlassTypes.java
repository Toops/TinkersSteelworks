package toops.tsteelworks.lib.registry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import nf.fr.ephys.cookiecore.helpers.RegistryHelper;
import toops.tsteelworks.common.core.TSLogger;

import java.util.HashMap;

// TODO: move to API
public class DeepTankGlassTypes {
	private static HashMap<GlassType, Integer> glassTypes = new HashMap<>();

	public static Integer getBlockCapacity(GlassType glass) {
		return glassTypes.get(glass);
	}

	public static void addGlassType(ItemStack stack, int capacity) {
		glassTypes.put(new GlassType(stack), capacity);
		TSLogger.info("Registered deep tank glass type " + stack.getDisplayName() + " with capacity of " + capacity + "mB");
	}

	/**
	 * Adds a glass type using a string following this format:
	 * modName:blockName@metadata|capacity
	 *
	 * @param data the data to parse
	 */
	public static void parseGlassType(String data) {
		// you know what would be great java ? lua-like assignation. like: blockName, capacity = data.split("|");
		String[] splitData = data.split("\\|");

		if (splitData.length != 2) {
			TSLogger.warning("Parsing deep tank glass " + data + ". INVALID FORMAT");

			return;
		}

		int capacity;
		try {
			capacity = Integer.parseInt(splitData[1]);
		} catch(NumberFormatException e) {
			TSLogger.warning("Parsing deep tank glass " + data + ". INVALID FORMAT: capacity is not an integer");
			return;
		}

		ItemStack[] stacks = RegistryHelper.getItemStacks(splitData[0]);

		if (stacks == null) {
			TSLogger.warning("Parsing deep tank glass " + data + ". Parse error.");
			return;
		}

		if (stacks.length == 0) {
			TSLogger.warning("Parsing deep tank glass " + data + ". INVALID: no matching itemstack found.");
		}

		for (ItemStack stack : stacks)
			addGlassType(stack, capacity);
	}

	public static class GlassType {
		private int metadata;
		private Block block;

		public GlassType(Block block, int metadata) {
			this.block = block;
			this.metadata = metadata;
		}

		public GlassType(ItemStack item) {
			this.block = Block.getBlockFromItem(item.getItem());
			this.metadata = item.getItemDamage();
		}

		public GlassType(GlassType glass) {
			this.metadata = glass.metadata;
			this.block = glass.block;
		}

		@Override
		public String toString() {
			return block.getLocalizedName() + '@' + metadata;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			GlassType glassType = (GlassType) o;

			return metadata == glassType.metadata && block.equals(glassType.block);
		}

		@Override
		public int hashCode() {
			return block.hashCode();
		}

		public void setMetadata(int metadata) {
			this.metadata = metadata;
		}

		public void setBlock(Block block) {
			this.block = block;
		}
	}
}