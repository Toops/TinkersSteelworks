package toops.tsteelworks.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import toops.tsteelworks.lib.TSRepo;

public class CementBlock extends TSBaseBlock {
	static final String[] TEXTURE_NAME = {"white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "lightgray", "cyan", "purple", "blue", "brown", "green", "red", "black"};

	public CementBlock() {
		super(Material.rock, 3F, TEXTURE_NAME);
	}

// was this intended ? Caus it's causing renderTank weirdness
/*	@Override
	public int getRenderBlockPass() {
		return 1;
	}*/

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.icons = new IIcon[TEXTURE_NAME.length];

		for (int i = 0; i < this.icons.length; ++i) {
			this.icons[i] = iconRegister.registerIcon(TSRepo.NAMESPACE + "cement/" + "cement_" + TEXTURE_NAME[i]);
		}
	}
}
