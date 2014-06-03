package tsteelworks.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tconstruct.common.TContent;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IFacingLogic;
import tconstruct.library.util.IMasterLogic;
import tconstruct.library.util.IServantLogic;
import tsteelworks.TSteelworks;
import tsteelworks.blocks.logic.DeepTankLogic;
import tsteelworks.blocks.logic.HighOvenDrainLogic;
import tsteelworks.blocks.logic.HighOvenDuctLogic;
import tsteelworks.blocks.logic.HighOvenLogic;
import tsteelworks.blocks.logic.TSMultiServantLogic;
import tsteelworks.client.block.DeepTankRender;
import tsteelworks.entity.HighGolem;
import tsteelworks.lib.Repo;
import tsteelworks.lib.TSteelworksRegistry;
import tsteelworks.lib.blocks.TSInventoryBlock;

public class HighOvenBlock extends TSInventoryBlock
{
    static ArrayList<CoordTuple> directions = new ArrayList<CoordTuple>(6);
    Random rand;
    String texturePrefix = "";

    public HighOvenBlock(int id)
    {
        super(id, Material.rock);
        setHardness(3F);
        setResistance(20F);
        setStepSound(soundMetalFootstep);
        rand = new Random();
        setCreativeTab(TSteelworksRegistry.SteelworksCreativeTab);
        setUnlocalizedName("tsteelworks.HighOven");
    }

    public HighOvenBlock(int id, String prefix)
    {
        this(id);
        texturePrefix = prefix;
    }
    
    /**
     * Called on server worlds only when the block has been replaced by a different block ID, or the same block with a
     * different metadata value, but before the new metadata value is set. Args: World, x, y, z, old block ID, old
     * metadata
     */
    @Override
    public void breakBlock (World world, int x, int y, int z, int blockID, int meta)
    {
        final TileEntity logic = world.getBlockTileEntity(x, y, z);
        if (logic instanceof IServantLogic)
            ((IServantLogic) logic).notifyMasterOfChange();
        if (logic instanceof HighOvenDuctLogic)
        {
            if (logic != null)
            {
                for (int j1 = 0; j1 < ((HighOvenDuctLogic)logic).getSizeInventory(); ++j1)
                {
                    ItemStack itemstack = ((HighOvenDuctLogic)logic).getStackInSlot(j1);

                    if (itemstack != null)
                    {
                        float f = rand.nextFloat() * 0.8F + 0.1F;
                        float f1 = rand.nextFloat() * 0.8F + 0.1F;
                        float f2 = rand.nextFloat() * 0.8F + 0.1F;

                        while (itemstack.stackSize > 0)
                        {
                            int k1 = this.rand.nextInt(21) + 10;

                            if (k1 > itemstack.stackSize)
                            {
                                k1 = itemstack.stackSize;
                            }

                            itemstack.stackSize -= k1;
                            EntityItem entityitem = new EntityItem(world, (double)((float)x + f), (double)((float)y + f1), (double)((float)z + f2), new ItemStack(itemstack.itemID, k1, itemstack.getItemDamage()));

                            if (itemstack.hasTagCompound())
                            {
                                entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
                            }

                            float f3 = 0.05F;
                            entityitem.motionX = (double)((float)rand.nextGaussian() * f3);
                            entityitem.motionY = (double)((float)rand.nextGaussian() * f3 + 0.2F);
                            entityitem.motionZ = (double)((float)rand.nextGaussian() * f3);
                            world.spawnEntityInWorld(entityitem);
                        }
                    }
                }
                world.func_96440_m(x, y, z, blockID);
            }
        }
        super.breakBlock(world, x, y, z, blockID, meta);
    }

    @Override
    public boolean canConnectRedstone (IBlockAccess world, int x, int y, int z, int side)
    {
        final TileEntity logic = world.getBlockTileEntity(x, y, z);
        return (logic instanceof IMasterLogic);
    }

    @Override
    public boolean canCreatureSpawn (EnumCreatureType type, World world, int x, int y, int z)
    {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity (World world)
    {
        return null;
    }

    @Override
    public TileEntity createTileEntity (World world, int metadata)
    {
        switch (metadata)
        {
        case 0:
            return new HighOvenLogic();
        case 1:
            return new HighOvenDrainLogic();
        case 12:
            return new HighOvenDuctLogic();
        case 13:
            return new DeepTankLogic();
        }
        return new TSMultiServantLogic();
    }

    @Override
    public int damageDropped (int meta)
    {
        return meta;
    }

    @Override
    public int getRenderType ()
    {
        return DeepTankRender.deeptankModel;
    }
    
    @Override
    public Icon getBlockTexture (IBlockAccess world, int x, int y, int z, int side)
    {
        final TileEntity logic = world.getBlockTileEntity(x, y, z);
        final short direction = (logic instanceof IFacingLogic) ? ((IFacingLogic) logic).getRenderDirection() : 0;
        final int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0)
            if (side == direction)
            {
                if (isActive(world, x, y, z))
                    return icons[2];
                else
                    return icons[1];
            }
            else
                return icons[0];
        else if (meta == 1)
        {
            if (side == direction)
                return icons[5];
            else if ((side / 2) == (direction / 2))
                return icons[4];
            else
                return icons[3];
        }
        else if (meta == 2)
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
    public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        final int meta = world.getBlockMetadata(x, y, z);
        world.getBlockTileEntity(x, y, z);
        switch (meta)
        {
        case 0:
            return TSteelworks.proxy.highovenGuiID;
        case 12:
            return TSteelworks.proxy.highovenDuctGuiID;
        case 13:
            return TSteelworks.proxy.deeptankGuiID;
        default:
            return null;
        }
    }

    @Override
    public Icon getIcon (int side, int meta)
    {
        if (meta < 2)
        {
            final int sideTex = side == 3 ? 1 : 0;
            return icons[sideTex + (meta * 3)];
        }
        else if (meta == 2)
            return icons[6];
        else if (meta == 11)
        {
            if ((side == 0) || (side == 1))
                return icons[9];
        }
        else if (meta == 12)
        {
            final int sideTex = side == 3 ? 15 : 6;
            return icons[sideTex];
        }
        else if (meta == 13)
        {
            final int sideTex = side == 3 ? 16 : 6;
            return icons[sideTex];
        }
        return icons[3 + meta];
    }

    // Currently unused
    public int getIndirectPowerLevelTo (World world, int x, int y, int z, int side)
    {
        if (world.isBlockNormalCube(x, y, z))
            return world.getBlockPowerInput(x, y, z);
        else
        {
            final int i1 = world.getBlockId(x, y, z);
            return i1 == 0 ? 0 : Block.blocksList[i1].isProvidingWeakPower(world, x, y, z, side);
        }
    }

    @Override
    public int getLightValue (IBlockAccess world, int x, int y, int z)
    {
        return !isActive(world, x, y, z) ? 0 : 9;
    }

    @Override
    public Object getModInstance ()
    {
        return TSteelworks.instance;
    }

    @Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 14; iter++)
            if (iter != 3)
                list.add(new ItemStack(id, 1, iter));
    }

    @Override
    public String[] getTextureNames ()
    {
        final String[] textureNames = { "highoven_side", "highoven_inactive", "highoven_active", "drain_side", "drain_out", "drain_basin", "scorchedbrick", "scorchedstone", "scorchedcobble",
                "scorchedpaver", "scorchedbrickcracked", "scorchedroad", "scorchedbrickfancy", "scorchedbricksquare", "scorchedcreeper", "duct_out", "deeptank_controller" };
        if (!texturePrefix.equals(""))
            for (int i = 0; i < textureNames.length; i++)
                textureNames[i] = texturePrefix + "_" + textureNames[i];
        return textureNames;
    }

    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float clickX, float clickY, float clickZ)
    {
        final int meta = world.getBlockMetadata(x, y, z);
        if (player.isSneaking())
            return false;
        final Integer integer = getGui(world, x, y, z, player);
        if ((integer == null) || (integer == -1))
            return false;
        if ((meta == 0) || (meta == 12) || (meta == 13))
        {
            player.openGui(getModInstance(), integer, world, x, y, z);
            return true;
        }
        return false;
    }

    @Override
    public void onBlockAdded (World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);
        if (world.getBlockMetadata(x, y, z) == 0)
            spawnHighGolem(world, x, y, z);
    }

    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack)
    {
        super.onBlockPlacedBy(world, x, y, z, entityliving, stack);
        if (world.getBlockMetadata(x, y, z) == 0 || world.getBlockMetadata(x, y, z) == 13)
            onBlockPlacedElsewhere(world, x, y, z, entityliving);
    }

    public void onBlockPlacedElsewhere (World world, int x, int y, int z, EntityLivingBase entityliving)
    {
        if (world.getBlockMetadata(x, y, z) == 0)
        {
            final HighOvenLogic logic = (HighOvenLogic) world.getBlockTileEntity(x, y, z);
            logic.checkValidPlacement();
        }
        if (world.getBlockMetadata(x, y, z) == 13)
        {
            final DeepTankLogic logic = (DeepTankLogic) world.getBlockTileEntity(x, y, z);
            logic.checkValidPlacement();
        }
    }

    @Override
    public void onNeighborBlockChange (World world, int x, int y, int z, int nBlockID)
    {
        final TileEntity logic = world.getBlockTileEntity(x, y, z);
        if (logic instanceof IServantLogic)
            ((IServantLogic) logic).notifyMasterOfChange();
        else if (logic instanceof IMasterLogic)
            ((IMasterLogic) logic).notifyChange(null, x, y, z);
        if (logic instanceof HighOvenLogic)
            ((HighOvenLogic) logic).setRSmode(world.isBlockIndirectlyGettingPowered(x, y, z));
        if (logic instanceof HighOvenDuctLogic)
            ((HighOvenDuctLogic) logic).setRedstoneActive(world.isBlockIndirectlyGettingPowered(x, y, z));
    }

    @Override
    public int quantityDropped (final Random random)
    {
        return 1;
    }

    @Override
    public void randomDisplayTick (World world, int x, int y, int z, Random random)
    {
        if (isActive(world, x, y, z))
        {
            final TileEntity logic = world.getBlockTileEntity(x, y, z);
            byte face = 0;
            if (logic instanceof IFacingLogic)
                face = ((IFacingLogic) logic).getRenderDirection();
            final float f = x + 0.5F;
            final float f1 = y + 0.5F + ((random.nextFloat() * 6F) / 16F);
            final float f2 = z + 0.5F;
            final float f3 = 0.52F;
            final float f4 = (random.nextFloat() * 0.6F) - 0.3F;
            switch (face)
            {
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
    public void registerIcons (IconRegister iconRegister)
    {
        final String[] textureNames = getTextureNames();
        icons = new Icon[textureNames.length];

        for (int i = 0; i < icons.length; ++i)
            icons[i] = iconRegister.registerIcon(Repo.textureDir + textureNames[i]);
    }

    private void spawnHighGolem (World world, int x, int y, int z)
    {
        final boolean check1 = ((world.getBlockId(x, y - 1, z) == TContent.smeltery.blockID) && (world.getBlockMetadata(x, y - 1, z) > 1));
        final boolean check2 = ((world.getBlockId(x, y - 2, z) == TContent.smeltery.blockID) && (world.getBlockMetadata(x, y - 2, z) > 1));

        if (check1 && check2)
        {
            if (!world.isRemote)
            {
                world.setBlock(x, y, z, 0, 0, 2);
                world.setBlock(x, y - 1, z, 0, 0, 2);
                world.setBlock(x, y - 2, z, 0, 0, 2);
                final HighGolem entityhighgolem = new HighGolem(world);
                entityhighgolem.setLocationAndAngles(x + 0.5D, y - 1.95D, z + 0.5D, 0.0F, 0.0F);
                world.spawnEntityInWorld(entityhighgolem);
                world.notifyBlockChange(x, y, z, 0);
                world.notifyBlockChange(x, y - 1, z, 0);
                world.notifyBlockChange(x, y - 2, z, 0);
                world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "tile.piston.out", 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
                for (int l = 0; l < 120; ++l)
                    TSteelworks.proxy.spawnParticle("scorchedbrick", x + world.rand.nextDouble(), (y - 2) + (world.rand.nextDouble() * 2.5D), z + world.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
            } 
        }
    }

    boolean activeRedstone (World world, int x, int y, int z)
    {
        final Block wire = Block.blocksList[world.getBlockId(x, y, z)];
        if ((wire != null) && (wire.blockID == Block.redstoneWire.blockID))
            return world.getBlockMetadata(x, y, z) > 0;
        return false;
    }
    
    static
    {
        directions.add(new CoordTuple(0, -1, 0));
        directions.add(new CoordTuple(0, 1, 0));
        directions.add(new CoordTuple(0, 0, -1));
        directions.add(new CoordTuple(0, 0, 1));
        directions.add(new CoordTuple(-1, 0, 0));
        directions.add(new CoordTuple(1, 0, 0));
    }
    
}
