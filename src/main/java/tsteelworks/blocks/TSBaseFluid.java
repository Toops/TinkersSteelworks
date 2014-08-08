package tsteelworks.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import tsteelworks.common.TSRepo;

public class TSBaseFluid extends BlockFluidClassic {
	String texture;
	boolean alpha;
	public IIcon stillIcon;
	public IIcon flowIcon;
	public int renderColor = 16777215;

	public TSBaseFluid(Fluid fluid, Material material, String texture) {
		super(fluid, material);
		this.texture = texture;
	}

	public TSBaseFluid(Fluid fluid, Material material, String texture, boolean alpha) {
		this(fluid, material, texture);
		this.alpha = alpha;
	}

	@Override
	public int getRenderBlockPass() {
		return alpha ? 1 : 0;
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		stillIcon = iconRegister.registerIcon(TSRepo.textureDir + texture);
		flowIcon = iconRegister.registerIcon(TSRepo.textureDir + texture + "_flow");
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (side == 0 || side == 1)
			return stillIcon;

		return flowIcon;
	}
}
