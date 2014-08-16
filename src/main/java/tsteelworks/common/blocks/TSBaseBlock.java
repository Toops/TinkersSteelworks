package tsteelworks.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import tsteelworks.common.core.TSContent;
import tsteelworks.lib.TSRepo;

import java.util.List;

public class TSBaseBlock extends Block {
	public String[] textureNames;
	public IIcon[] icons;

	public TSBaseBlock(Material material, float hardness) {
		super(material);

		setHardness(hardness);
		setCreativeTab(TSContent.creativeTab);
	}

	public TSBaseBlock(Material material, float hardness, String[] tex) {
		super(material);

		setHardness(hardness);
		setCreativeTab(TSContent.creativeTab);
		textureNames = tex;
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return meta < icons.length ? icons[meta] : icons[0];
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item block, CreativeTabs tab, List list) {
		for (int iter = 0; iter < icons.length; iter++)
			list.add(new ItemStack(block, 1, iter));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		icons = new IIcon[textureNames.length];
		for (int i = 0; i < icons.length; ++i)
			icons[i] = iconRegister.registerIcon(TSRepo.TEXTURE_DIR + textureNames[i]);
	}
}
