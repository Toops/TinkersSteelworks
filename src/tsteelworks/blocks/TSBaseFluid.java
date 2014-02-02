package tsteelworks.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraftforge.fluids.Fluid;
import tconstruct.blocks.TConstructFluid;
import tsteelworks.lib.TSteelworksRegistry;


public class TSBaseFluid extends TConstructFluid
{
	String	texture;
	boolean	alpha;

	public TSBaseFluid (int id, Fluid fluid, Material material, String texture)
	{
		super(id, fluid, material, texture);
		this.texture = texture;
		setCreativeTab(TSteelworksRegistry.SteelworksCreativeTab);
	}

	public TSBaseFluid (int id, Fluid fluid, Material material, String texture, boolean alpha)
	{
		this(id, fluid, material, texture);
		this.alpha = alpha;
	}

	@Override
	public void registerIcons (IconRegister iconRegister)
	{
		stillIcon = iconRegister.registerIcon("tsteelworks:" + texture);
		flowIcon = iconRegister.registerIcon("tsteelworks:" + texture + "_flow");
	}
}
