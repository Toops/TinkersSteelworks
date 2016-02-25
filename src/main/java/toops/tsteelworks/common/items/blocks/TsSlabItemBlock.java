package toops.tsteelworks.common.items.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class TsSlabItemBlock extends ItemBlock {
	private final String baseName;
	private final String[] blockNames;

	public TsSlabItemBlock(Block block, String baseName, String[] blockNames) {
		super(block);

		setMaxDamage(0);
		setHasSubtypes(true);

		this.baseName = baseName;
		this.blockNames = blockNames;
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		int meta = MathHelper.clamp_int(itemstack.getItemDamage(), 0, blockNames.length - 1);

		return baseName + "." + blockNames[meta];
	}
}
