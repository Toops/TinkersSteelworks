package tsteelworks.lib.blocks;

import mantle.blocks.iface.IActiveLogic;
import mantle.blocks.iface.IFacingLogic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nf.fr.ephys.cookiecore.helpers.InventoryHelper;
import tsteelworks.lib.TSRepo;
import tsteelworks.lib.INamable;
import tsteelworks.lib.IRedstonePowered;
import tsteelworks.lib.IServantLogic;

public abstract class TSInventoryBlock extends BlockContainer {
	protected IIcon[] icons;

	public TSInventoryBlock(Material material) {
		super(material);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		final TileEntity te = world.getTileEntity(x, y, z);

		if (te instanceof IInventory) {
			InventoryHelper.dropContents((IInventory) te, world, x, y, z);
		}

		if (te instanceof IServantLogic)
			((IServantLogic) te).notifyMasterOfChange();

		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		final TileEntity logic = world.getTileEntity(x, y, z);

		return (logic instanceof IRedstonePowered);
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	/**
	 * @return the GUI id or -1 if the gui does not exist, -2 if it exists but we should not open it
	 */
	public abstract int getGui(World world, int x, int y, int z, EntityPlayer entityplayer);

	public abstract Object getModInstance();

	public abstract String[] getTextureNames();

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float clickX, float clickY, float clickZ) {
		if (player.isSneaking())
			return false;

		int guiID = getGui(world, x, y, z, player);

		if (guiID != -1) {
			if (guiID != -2 && !world.isRemote)
				player.openGui(getModInstance(), guiID, world, x, y, z);

			return true;
		}

		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack) {
		final TileEntity logic = world.getTileEntity(x, y, z);
		if (logic instanceof IFacingLogic) {
			final IFacingLogic direction = (IFacingLogic) logic;

			if (entityliving == null)
				direction.setDirection(0);
			else
				direction.setDirection(entityliving.rotationYaw, entityliving.rotationPitch, entityliving);
		}

		if (stack.hasDisplayName() && logic instanceof INamable) {
			((INamable) logic).setCustomName(stack.getDisplayName());
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		final String[] textureNames = getTextureNames();
		icons = new IIcon[textureNames.length];

		for (int i = 0; i < icons.length; ++i)
			icons[i] = iconRegister.registerIcon(TSRepo.NAMESPACE + textureNames[i]);
	}

	public static boolean isActive(IBlockAccess world, int x, int y, int z) {
		final TileEntity logic = world.getTileEntity(x, y, z);

		return logic instanceof IActiveLogic && ((IActiveLogic) logic).getActive();
	}
}