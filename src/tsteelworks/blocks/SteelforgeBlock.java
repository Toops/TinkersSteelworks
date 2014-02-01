package tsteelworks.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tconstruct.blocks.logic.MultiServantLogic;
import tconstruct.library.util.IFacingLogic;
import tconstruct.library.util.IMasterLogic;
import tconstruct.library.util.IServantLogic;
import tsteelworks.TSteelworks;
import tsteelworks.blocks.logic.HighOvenDrainLogic;
import tsteelworks.blocks.logic.HighOvenLogic;
import tsteelworks.lib.TSteelworksRegistry;
import tconstruct.library.blocks.InventoryBlock;

public class SteelforgeBlock extends InventoryBlock {

	Random rand;
	String texturePrefix = "";

	public SteelforgeBlock(int id) {
		super(id, Material.rock);
		setHardness(3F);
		setResistance(20F);
		setStepSound(soundMetalFootstep);
		rand = new Random();
		this.setCreativeTab(TSteelworksRegistry.Steelforge);
		this.setUnlocalizedName("tsteelworks.Steelforge");
	}

	public SteelforgeBlock(int id, String prefix) {
		this(id);
		texturePrefix = prefix;
	}

	public int damageDropped(int meta) {
		return meta;
	}

	public int quantityDropped(Random random) {
		return 1;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int side, float clickX, float clickY,
			float clickZ) {
		if (player.isSneaking() || world.getBlockMetadata(x, y, z) != 0)
			return false;

		Integer integer = getGui(world, x, y, z, player);
		if (integer == null || integer == -1) {
			return false;
		} else {
			player.openGui(getModInstance(), integer, world, x, y, z);
			return true;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		switch (metadata) {
		case 0:
			return new HighOvenLogic();
        case 1:
            return new HighOvenDrainLogic();
		}
		return new MultiServantLogic();
	}

    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack)
    {
        super.onBlockPlacedBy(world, x, y, z, entityliving, stack);
        if (world.getBlockMetadata(x, y, z) == 0)
            onBlockPlacedElsewhere(world, x, y, z, entityliving);
    }

    public void onBlockPlacedElsewhere (World world, int x, int y, int z, EntityLivingBase entityliving)
    {
        HighOvenLogic logic = (HighOvenLogic) world.getBlockTileEntity(x, y, z);
        logic.checkValidPlacement();
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubBlocks(int id, CreativeTabs tab, List list) {
		for (int iter = 0; iter < 12; iter++) {
			if (iter != 3)
				list.add(new ItemStack(id, 1, iter));
		}
	}

	/* Updating */
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z,
			int nBlockID) {
		// System.out.println("Neighbor changed");
		TileEntity logic = world.getBlockTileEntity(x, y, z);
		if (logic instanceof IServantLogic) {
			((IServantLogic) logic).notifyMasterOfChange();
		} else if (logic instanceof IMasterLogic) {
			((IMasterLogic) logic).notifyChange(null, x, y, z);
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int blockID,
			int meta) {
		TileEntity logic = world.getBlockTileEntity(x, y, z);
		if (logic instanceof IServantLogic) {
			((IServantLogic) logic).notifyMasterOfChange();
		}
		super.breakBlock(world, x, y, z, blockID, meta);
	}

	@SuppressWarnings("static-access")
	@Override
	public Integer getGui(World world, int x, int y, int z,
			EntityPlayer entityplayer) {
		return TSteelworks.proxy.highOvenGuiID;
	}

	@Override
	public Object getModInstance() {
		return TSteelworks.instance;
	}

	@Override
	public String[] getTextureNames() {
		String[] textureNames = { "scorchedbrick", "highoven_inactive", "highoven_active", "drain_out", "drain_basin" };

		if (!texturePrefix.equals(""))
			for (int i = 0; i < textureNames.length; i++)
				textureNames[i] = texturePrefix + "_" + textureNames[i];

		return textureNames;
	}

	public Icon getIcon(int side, int meta) {
		// sides 0 / 1 = top/bottom, 3 = face
		if (meta < 2) // 0 high oven, 1 drain, 2 brick
		{
			int sideTex = side == 3 ? 1 : 0;
			return icons[sideTex + meta * 3];
		} else if (meta == 2) // scorched bricks
		{
			return icons[0];
		}
		return icons[0];
		//return icons[3 + meta];
	}

	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z,
			int side) {
		TileEntity logic = world.getBlockTileEntity(x, y, z);
		short direction = (logic instanceof IFacingLogic) ? ((IFacingLogic) logic)
				.getRenderDirection() : 0;
		int meta = world.getBlockMetadata(x, y, z);
		if (meta == 0) // High Oven
		{
			if (side == direction) // facing direction
			{
				if (isActive(world, x, y, z)) {
					return icons[2]; // active
				} else {
					return icons[1]; // inactive
				}
			} else {
				return icons[0];
			}
		} else if (meta == 1) //Drain
        {
            if (side == direction)
                return icons[4]; // drain face
            else if (side / 2 == direction / 2)
                return icons[3]; // drain back
            else
                return icons[0]; // drain side
		} else if (meta == 2) // scorched brick
		{
			return icons[0];
		}
		return icons[3 + meta];
	}
	
    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        String[] textureNames = getTextureNames();
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tsteelworks:" + textureNames[i]);
        }
    }

	public void randomDisplayTick(World world, int x, int y, int z, Random random) 
	{
		if (isActive(world, x, y, z)) 
		{
			TileEntity logic = world.getBlockTileEntity(x, y, z);
			byte face = 0;
			if (logic instanceof IFacingLogic)
				face = ((IFacingLogic) logic).getRenderDirection();
			float f = (float) x + 0.5F;
			float f1 = (float) y + 0.5F + (random.nextFloat() * 6F) / 16F;
			float f2 = (float) z + 0.5F;
			float f3 = 0.52F;
			float f4 = random.nextFloat() * 0.6F - 0.3F;
			switch (face) 
			{
			case 4:
				world.spawnParticle("smoke", f - f3, f1, f2 + f4, 0.0D, 0.0D,
						0.0D);
				world.spawnParticle("flame", f - f3, f1, f2 + f4, 0.0D, 0.0D,
						0.0D);
				break;

			case 5:
				world.spawnParticle("smoke", f + f3, f1, f2 + f4, 0.0D, 0.0D,
						0.0D);
				world.spawnParticle("flame", f + f3, f1, f2 + f4, 0.0D, 0.0D,
						0.0D);
				break;

			case 2:
				world.spawnParticle("smoke", f + f4, f1, f2 - f3, 0.0D, 0.0D,
						0.0D);
				world.spawnParticle("flame", f + f4, f1, f2 - f3, 0.0D, 0.0D,
						0.0D);
				break;

			case 3:
				world.spawnParticle("smoke", f + f4, f1, f2 + f3, 0.0D, 0.0D,
						0.0D);
				world.spawnParticle("flame", f + f4, f1, f2 + f3, 0.0D, 0.0D,
						0.0D);
				break;
			}
		}
	}

	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return !isActive(world, x, y, z) ? 0 : 9;
	}
}
