package toops.tsteelworks.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import nf.fr.ephys.cookiecore.helpers.BlockHelper;
import toops.tsteelworks.common.blocks.logic.SteamTurbineLogic;

public class SteamTurbineBlock extends BlockContainer {
	private IIcon textureFront;
	private IIcon textureBack;
	private IIcon textureTop;
	private IIcon textureSide;

	public SteamTurbineBlock() {
		super(Material.iron);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new SteamTurbineLogic();
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack) {
		world.setBlockMetadataWithNotify(x, y, z, BlockHelper.orientationToMetadataXZ(entityliving.rotationYaw), 2);
	}

	@Override
	public IIcon getIcon(int side, int metadata) {
		if (side == 0 || BlockHelper.getOppositeSide(side) == metadata) return textureBack;

		if (side == 1) return textureTop;

		if (side == metadata) return textureFront;

		return textureSide;
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		textureBack = register.registerIcon("tsteelworks:turbine_back");
		textureFront = register.registerIcon("tsteelworks:turbine_output");
		textureSide = register.registerIcon("tsteelworks:turbine_side");
		textureTop = register.registerIcon("tsteelworks:turbine_top");
	}
}