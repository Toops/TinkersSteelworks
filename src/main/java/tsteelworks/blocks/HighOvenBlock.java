package tsteelworks.blocks;

import mantle.blocks.iface.IFacingLogic;
import mantle.world.CoordTuple;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.world.TinkerWorld;
import tsteelworks.TSteelworks;
import tsteelworks.blocks.logic.*;
import tsteelworks.client.block.DeepTankRender;
import tsteelworks.common.GuiHandler;
import tsteelworks.common.TSRepo;
import tsteelworks.entity.HighGolem;
import tsteelworks.entity.SteelGolem;
import tsteelworks.lib.IMasterLogic;
import tsteelworks.lib.IServantLogic;
import tsteelworks.lib.TSteelworksRegistry;
import tsteelworks.lib.blocks.TSInventoryBlock;
import tsteelworks.util.InventoryHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HighOvenBlock extends TSInventoryBlock {
	public static final int META_HIGHOVEN = 0;
	public static final int META_DRAIN = 1;

	public static final int META_DUCT = 12;
	public static final int META_TANK = 13;

	private String texturePrefix = "";

	public HighOvenBlock() {
		super(Material.rock);

		setHardness(3F);
		setResistance(20F);
		setStepSound(soundTypeMetal);

		setCreativeTab(TSteelworksRegistry.SteelworksCreativeTab);
		setBlockName("tsteelworks.HighOven");
	}

	public HighOvenBlock(String prefix) {
		this();

		texturePrefix = prefix;
	}

	@Override
	public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		switch (metadata) {
			case META_HIGHOVEN:
				return new HighOvenLogic();
			case META_DRAIN:
				return new HighOvenDrainLogic();
			case META_DUCT:
				return new HighOvenDuctLogic();
			case META_TANK:
				return new DeepTankLogic();
		}
		return new TSMultiServantLogic();
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public int getRenderType() {
		return DeepTankRender.deeptankModel;
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		final TileEntity logic = world.getTileEntity(x, y, z);
		final short direction = (logic instanceof IFacingLogic) ? ((IFacingLogic) logic).getRenderDirection() : 0;
		final int meta = world.getBlockMetadata(x, y, z);

		if (meta == 0)
			if (side == direction) {
				if (isActive(world, x, y, z))
					return icons[2];
				else
					return icons[1];
			} else
				return icons[0];
		else if (meta == 1) {
			if (side == direction)
				return icons[5];
			else if ((side / 2) == (direction / 2))
				return icons[4];
			else
				return icons[3];
		} else if (meta == 2)
			return icons[6];
		else if (meta == 11)
			if ((side == 0) || (side == 1))
				return icons[9];
		if (meta == 12)
			if (side == direction)
				return icons[15];
			else if ((side / 2) == (direction / 2))
				return icons[4];
			else
				return icons[3];
		else if (meta == 13)
			if (side == direction)
				return icons[16];
			else
				return icons[0];
		return icons[3 + meta];
	}

	@Override
	public int getGui(World world, int x, int y, int z, EntityPlayer entityplayer) {
		final int meta = world.getBlockMetadata(x, y, z);

		switch (meta) {
			case 0:
				return GuiHandler.HIGHOVEN_GUI_ID;
			case 12:
				return GuiHandler.HIGHOVEN_DUCT_GUI_ID;
			case 13:
				return GuiHandler.DEEPTANK_GUI_ID;
			default:
				return -1;
		}
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (meta < 2) {
			final int sideTex = side == 3 ? 1 : 0;
			return icons[sideTex + (meta * 3)];
		} else if (meta == 2)
			return icons[6];
		else if (meta == 11) {
			if ((side == 0) || (side == 1))
				return icons[9];
		} else if (meta == 12) {
			final int sideTex = side == 3 ? 15 : 6;
			return icons[sideTex];
		} else if (meta == 13) {
			final int sideTex = side == 3 ? 16 : 6;
			return icons[sideTex];
		}
		return icons[3 + meta];
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return !isActive(world, x, y, z) ? 0 : 9;
	}

	@Override
	public Object getModInstance() {
		return TSteelworks.instance;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item id, CreativeTabs tab, List list) {
		for (int iter = 0; iter < 14; iter++)
			if (iter != 3)
				list.add(new ItemStack(id, 1, iter));
	}

	@Override
	public String[] getTextureNames() {
		final String[] textureNames = {"highoven_side", "highoven_inactive", "highoven_active", "drain_side", "drain_out", "drain_basin", "scorchedbrick", "scorchedstone", "scorchedcobble", "scorchedpaver", "scorchedbrickcracked", "scorchedroad", "scorchedbrickfancy", "scorchedbricksquare", "scorchedcreeper", "duct_out", "deeptank_controller"};
		if (!texturePrefix.equals(""))
			for (int i = 0; i < textureNames.length; i++)
				textureNames[i] = texturePrefix + "_" + textureNames[i];

		return textureNames;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);

		if (world.getBlockMetadata(x, y, z) == 0) {
			spawnHighGolem(world, x, y, z);
			spawnSteelGolem(world, x, y, z);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, entityliving, stack);
		if (world.getBlockMetadata(x, y, z) == 0 || world.getBlockMetadata(x, y, z) == 13) {
			((IMasterLogic) world.getTileEntity(x, y, z)).checkValidPlacement();
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block nBlockID) {
		final TileEntity logic = world.getTileEntity(x, y, z);
		if (logic instanceof IServantLogic)
			((IServantLogic) logic).notifyMasterOfChange();
		else if (logic instanceof IMasterLogic)
			((IMasterLogic) logic).notifyChange(null, x, y, z);
		if (logic instanceof HighOvenLogic)
			((HighOvenLogic) logic).setRSmode(world.isBlockIndirectlyGettingPowered(x, y, z));
		if (logic instanceof HighOvenDuctLogic)
			((HighOvenDuctLogic) logic).setRSmode(world.isBlockIndirectlyGettingPowered(x, y, z));
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
			final float f3 = 0.52F;
			final float f4 = (random.nextFloat() * 0.6F) - 0.3F;
			switch (face) {
				case 4:
					world.spawnParticle("smoke", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
					break;
				case 5:
					world.spawnParticle("smoke", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
					break;
				case 2:
					world.spawnParticle("smoke", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
					break;
				case 3:
					world.spawnParticle("smoke", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
					break;
			}
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		final String[] textureNames = getTextureNames();
		icons = new IIcon[textureNames.length];

		for (int i = 0; i < icons.length; ++i)
			icons[i] = iconRegister.registerIcon(TSRepo.textureDir + textureNames[i]);
	}

	private void spawnHighGolem(World world, int x, int y, int z) {
		final boolean hasTorso = (world.getBlock(x, y - 1, z) == TinkerSmeltery.smeltery) && (world.getBlockMetadata(x, y - 1, z) > 1);
		final boolean hasFeet = (world.getBlock(x, y - 2, z) == TinkerSmeltery.smeltery) && (world.getBlockMetadata(x, y - 2, z) > 1);

		if (hasTorso && hasFeet) {
			if (world.isRemote) {
				for (int l = 0; l < 120; ++l)
					world.spawnParticle("scorchedbrick", x + world.rand.nextDouble(), y - 2 + world.rand.nextDouble() * 2.5D, z + world.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
			} else {
				world.setBlockToAir(x, y, z);
				world.setBlockToAir(x, y - 1, z);
				world.setBlockToAir(x, y - 2, z);

				final HighGolem entityhighgolem = new HighGolem(world);
				entityhighgolem.setLocationAndAngles(x + 0.5D, y - 1.95D, z + 0.5D, 0.0F, 0.0F);
				world.spawnEntityInWorld(entityhighgolem);

				world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "tile.piston.out", 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
			}
		}
	}

	private void spawnSteelGolem(World world, int x, int y, int z) {
		if (world.isRemote) return;

		ItemStack blockArdite = new ItemStack(TinkerWorld.metalBlock, 1, 1);

		// check torso & foot
		if (world.getBlock(x, y - 1, z).equals(TinkerWorld.meatBlock) && InventoryHelper.isBlockEqual(blockArdite, world, x, y - 2, z)) {
			boolean armOnXAxis = InventoryHelper.isBlockEqual("blockSteel", world, x + 1, y - 1, z) && InventoryHelper.isBlockEqual("blockSteel", world, x - 1, y - 1, z);

			if (armOnXAxis || (InventoryHelper.isBlockEqual("blockSteel", world, x, y - 1, z - 1) && InventoryHelper.isBlockEqual("blockSteel", world, x, y - 1, z + 1))) {
				if (world.isRemote) {
					for (int l = 0; l < 120; ++l)
						world.spawnParticle("scorchedbrick", x + world.rand.nextDouble(), y - 2 + world.rand.nextDouble() * 2.5D, z + world.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
				} else {
					// remove block golem
					world.setBlockToAir(x, y, z);
					world.setBlockToAir(x, y - 1, z);

					if (armOnXAxis) {
						world.setBlockToAir(x + 1, y - 1, z);
						world.setBlockToAir(x - 1, y - 1, z);
					} else {
						world.setBlockToAir(x, y - 1, z + 1);
						world.setBlockToAir(x, y - 1, z - 1);
					}

					world.setBlockToAir(x, y - 2, z);

					// spawn entity golem
					SteelGolem golem = new SteelGolem(world);
					golem.setPlayerCreated(true);
					golem.setLocationAndAngles(x + 0.5D, y - 1.95D, z + 0.5D, 0.0F, 0.0F);
					world.spawnEntityInWorld(golem);

					world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "tile.piston.out", 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
				}
			}
		}
	}
}
