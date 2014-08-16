package tsteelworks.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import tsteelworks.lib.TSRepo;

public class TSFluidBlock extends BlockFluidClassic {
	private String texture;
	private boolean alpha = false;
	private IIcon stillIcon;
	private IIcon flowIcon;

	public TSFluidBlock(Fluid fluid, Material material, String texture) {
		super(fluid, material);

		this.texture = texture;
	}

	public TSFluidBlock(Fluid fluid, Material material, String texture, boolean alpha) {
		this(fluid, material, texture);
		this.alpha = alpha;
	}

	public TSFluidBlock setAlpha(boolean alpha) {
		this.alpha = alpha;

		return this;
	}

	@Override
	public int getRenderBlockPass() {
		return alpha ? 1 : 0;
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		stillIcon = iconRegister.registerIcon(TSRepo.TEXTURE_DIR + texture);
		flowIcon = iconRegister.registerIcon(TSRepo.TEXTURE_DIR + texture + "_flow");
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (side == 0 || side == 1)
			return stillIcon;

		return flowIcon;
	}
}
