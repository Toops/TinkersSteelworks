package toops.tsteelworks.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import toops.tsteelworks.common.core.TSContent;

import java.util.List;

public class LimestoneSlab extends TSBaseSlab {
	public LimestoneSlab() {
		super(Material.rock);

		this.setCreativeTab(TSContent.creativeTab);
		setHardness(12F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		meta = meta % 8;

		if (meta <= 2)
			return TSContent.limestoneBlock.getIcon(side, meta);

		return TSContent.limestoneBlock.getIcon(side, meta + 1);
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int iter = 0; iter < 8; iter++) {
			list.add(new ItemStack(item, 1, iter));
		}
	}
}
