package toops.tsteelworks.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import toops.tsteelworks.common.core.TSContent;
import toops.tsteelworks.lib.TSRepo;

import java.util.List;
import java.util.Random;

public class LimestoneBlock extends TSBaseBlock {
	public LimestoneBlock() {
		super(Material.rock, 3F, new String[]{
				"limestone",
				"limestonecobble",
				"limestonebrick",
				"limestonebrickcracked",
				"limestonepaver",
				"limestoneroad",
				"limestonebrickfancy",
				"limestonebricksquare",
				"limestonecreeper"
		});

		setResistance(20F);
		setStepSound(soundTypeMetal);

		setCreativeTab(TSContent.creativeTab);
		setBlockName("tsteelworks.limestone");
	}

	@Override
	public int damageDropped(int meta) {
		if (meta == 0)
			return 1;

		return meta;
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		int meta = world.getBlockMetadata(x, y, z);

		if (meta == 8 && (side == 0 || side == 1))
			return icons[4];

		return icons[meta];
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (meta == 8 && (side == 0 || side == 1))
			return icons[4];

		return icons[meta];
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int iter = 0; iter < 9; iter++)
			list.add(new ItemStack(item, 1, iter));
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, entityliving, stack);
	}

	@Override
	public int quantityDropped(final Random random) {
		return 1;
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		icons = new IIcon[textureNames.length];

		for (int i = 0; i < icons.length; ++i)
			icons[i] = iconRegister.registerIcon(TSRepo.NAMESPACE + textureNames[i]);
	}
}
