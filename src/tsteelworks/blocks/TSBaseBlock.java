package tsteelworks.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import tconstruct.blocks.TConstructBlock;
import tsteelworks.lib.TSteelworksRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TSBaseBlock extends TConstructBlock
{
	public TSBaseBlock (int id, Material material, float hardness, String[] tex)
	{
		super(id, material, hardness, tex);
		setCreativeTab(TSteelworksRegistry.SteelworksCreativeTab);
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void registerIcons (IconRegister iconRegister)
	{
		icons = new Icon[textureNames.length];
		for (int i = 0; i < icons.length; ++i)
			icons[i] = iconRegister.registerIcon("tsteelworks:" + textureNames[i]);
	}
}
