package tsteelworks.common.items.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class ScorchedSlabItemBlock extends ItemBlock {
	public static final String blockType[] = {"brick", "stone", "cobble", "paver", "road", "fancy", "square", "creeper"};

	public ScorchedSlabItemBlock(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	public int getMetadata(int meta) {
		return meta;
	}

	public String getUnlocalizedName(ItemStack itemstack) {
		int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, blockType.length - 1);
		return (new StringBuilder()).append("block.scorchedstone.slab.").append(blockType[pos]).toString();
	}
}
