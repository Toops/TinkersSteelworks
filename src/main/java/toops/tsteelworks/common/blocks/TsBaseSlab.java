package toops.tsteelworks.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class TsBaseSlab extends Block {
	private final Block modelBlock;
	private final int metaStart;
	private final int metaEnd;

	/**
	 * Creates a new slab by copying properties from an existing block.
	 *
	 * @param model     The base block for which a slab should be created.
	 * @param metaStart The start of the metadata range of the model (included).
	 * @param metaEnd   The end of the metadata range of the model (included).
	 */
	public TsBaseSlab(Block model, int metaStart, int metaEnd) {
		super(model.getMaterial());

		this.modelBlock = model;
		this.metaStart = metaStart;
		this.metaEnd = metaEnd;

		if (getBlockAmount() > 8) {
			throw new IllegalArgumentException("Can only define a range of 8 metadata per slabs, the other 8 are used for the upper slabs.");
		}
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisalignedbb, List arraylist, Entity entity) {
		setBlockBoundsBasedOnState(world, x, y, z);

		super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, entity);
	}

	@Override
	public void setBlockBoundsForItemRender() {
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z) / 8;
		float minY = meta == 1 ? 0.5F : 0.0F;
		float maxY = meta == 1 ? 1.0F : 0.5F;

		setBlockBounds(0.0F, minY, 0F, 1.0F, maxY, 1.0F);
	}

	@Override
	public int onBlockPlaced(World par1World, int blockX, int blockY, int blockZ, int side, float clickX, float clickY, float clickZ, int metadata) {
		if (side == 1)
			return metadata;
		if (side == 0 || clickY >= 0.5F)
			return metadata | 8;

		return metadata;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		meta = meta % 8 + metaStart;

		return modelBlock.getIcon(side, meta);
	}

	private int getBlockAmount() {
		return metaEnd - metaStart + 1;
	}

	@Override
	@SuppressWarnings({"unchecked"})
	public void getSubBlocks(Item id, CreativeTabs tab, List list) {
		int nbBlocks = getBlockAmount();

		for (int i = 0; i < nbBlocks; i++) {
			list.add(new ItemStack(id, 1, i));
		}
	}

	public int damageDropped(int meta) {
		return meta % 8;
	}
}
