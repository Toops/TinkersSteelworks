package tsteelworks.common.blocks;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import tsteelworks.common.core.TSContent;
import tsteelworks.common.core.TSRepo;

import java.util.List;

public class DustStorageBlock extends BlockFalling {
	public static final String[] TEXTURE_NAMES = new String[] {"gunpowder", "sugar"};
	public IIcon[] icons;

	public DustStorageBlock() {
		super(Material.sand);

		setCreativeTab(TSContent.creativeTab);
		setStepSound(soundTypeSand);
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		return 3f;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return icons[meta];
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int iter = 0; iter < 2; iter++)
			list.add(new ItemStack(item, 1, iter));
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		icons = new IIcon[TEXTURE_NAMES.length];

		for (int i = 0; i < icons.length; ++i)
			icons[i] = iconRegister.registerIcon(TSRepo.TEXTURE_DIR + TEXTURE_NAMES[i] + "_block");
	}
}
