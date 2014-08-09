package tsteelworks.blocks;

import mantle.blocks.iface.IFacingLogic;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import nf.fr.ephys.cookiecore.helpers.FluidHelper;
import nf.fr.ephys.cookiecore.helpers.InventoryHelper;
import tsteelworks.TSteelworks;
import tsteelworks.blocks.logic.TurbineLogic;
import tsteelworks.client.block.MachineRender;
import tsteelworks.common.core.TSRepo;
import tsteelworks.lib.TSteelworksRegistry;
import tsteelworks.lib.blocks.TSInventoryBlock;

import java.util.List;
import java.util.Random;

//TODO: Keep steam on block removal
public class MachineBlock extends TSInventoryBlock {
	public MachineBlock() {
		super(Material.iron);

		setHardness(3F);
		setResistance(20F);
		setCreativeTab(TSteelworksRegistry.SteelworksCreativeTab);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TurbineLogic();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float clickX, float clickY, float clickZ) {
		ItemStack heldItem = player.inventory.getCurrentItem();
		if (heldItem == null)
			return false;

		FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(player.getCurrentEquippedItem());
		if (liquid == null || !liquid.getFluid().equals(FluidRegistry.getFluid("steam")))
			return false;

		TurbineLogic logic = (TurbineLogic) world.getTileEntity(x, y, z);

		if (FluidHelper.insertFluid(liquid, (IFluidHandler) logic)) {
			if (!player.capabilities.isCreativeMode) {
				heldItem.stackSize--;

				ItemStack container = heldItem.getItem().getContainerItem(heldItem);

				if (container != null) {
					if (heldItem.stackSize == 0)
						player.setCurrentItemOrArmor(0, container);
					else if (!InventoryHelper.insertItem(player.inventory, container)) {
						InventoryHelper.dropItem(container, player);
					}
				} else if (heldItem.stackSize == 0) {
					player.setCurrentItemOrArmor(0, null);
				}
			}
		}

		return true;
	}

	@Override
	public int getGui(World world, int x, int y, int z, EntityPlayer entityplayer) {
		return -1;
	}

	@Override
	public String[] getTextureNames() {
		return new String[] {"turbine_front", "turbine_side", "turbine_back"};
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		final String[] textureNames = getTextureNames();
		icons = new IIcon[textureNames.length];

		for (int i = 0; i < textureNames.length; ++i)
			icons[i] = iconRegister.registerIcon(TSRepo.textureDir + textureNames[i]);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (meta == 0)
			return icons[getTextureIndex(side)];

		return icons[0];
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		final TileEntity logic = world.getTileEntity(x, y, z);
		final short direction = (logic instanceof IFacingLogic) ? ((IFacingLogic) logic).getRenderDirection() : 0;
		final int meta = world.getBlockMetadata(x, y, z);

		if (meta == 0) {
			if (side == direction) {
				return icons[0];
			} else if (side / 2 == direction / 2) {
				return icons[2];
			}

			return icons[1];
		}
		return icons[0];
	}

	public int getTextureIndex(int side) {
		return getTextureIndex(side, false);
	}

	public int getTextureIndex(int side, boolean alt) {
		if (side == 0)
			return 2;
		if (side == 1)
			return 0;

		return alt ? 9 : 1;
	}

	public int getRenderType() {
		return MachineRender.model;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		if (isActive(world, x, y, z)) {
			final TileEntity logic = world.getTileEntity(x, y, z);
			byte face = 0;
			if (logic instanceof IFacingLogic)
				face = ((IFacingLogic) logic).getRenderDirection();
			final float f = x + 0.5F;
			final float f1 = y + 0.5F + ((random.nextFloat() * 6F) / 16F);
			final float f2 = z + 0.5F;
			final float f3 = 0.35F;
			final float f4 = (random.nextFloat() * 0.6F) - 0.3F;
			switch (face) {
				case 4:
					world.spawnParticle("explode", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
					break;
				case 5:
					world.spawnParticle("explode", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
					break;
				case 2:
					world.spawnParticle("explode", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
					break;
				case 3:
					world.spawnParticle("explode", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
					break;
			}
		}
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void getSubBlocks(Item id, CreativeTabs tab, List list) {
		list.add(new ItemStack(id, 1, 0));
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block nBlockID) {
		final TileEntity logic = world.getTileEntity(x, y, z);

		if (logic instanceof TurbineLogic)
			((TurbineLogic) logic).setActive(world.isBlockIndirectlyGettingPowered(x, y, z));
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public int quantityDropped(final Random random) {
		return 1;
	}

	@Override
	public Object getModInstance() {
		return TSteelworks.instance;
	}
}
