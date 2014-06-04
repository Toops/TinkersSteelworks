package tsteelworks.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IFacingLogic;
import tsteelworks.lib.Repo;
import tsteelworks.lib.TSteelworksRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LimestoneBlock extends TSBaseBlock
{
    static String[] textureNames = { "limestone", "limestonecobble", "limestonebrick", "limestonebrickcracked", "limestonepaver",  
            "limestoneroad", "limestonebrickfancy", "limestonebricksquare", "limestonecreeper" };
    
    static ArrayList<CoordTuple> directions = new ArrayList<CoordTuple>(6);
    Random rand;

    public LimestoneBlock(int id)
    {
        super(id, Material.rock, 3F, textureNames);
        setResistance(20F);
        setStepSound(soundMetalFootstep);
        rand = new Random();
        setCreativeTab(TSteelworksRegistry.SteelworksCreativeTab);
        setUnlocalizedName("tsteelworks.limestone");
    }

    @Override
    public void breakBlock (World world, int x, int y, int z, int blockID, int meta)
    {
        super.breakBlock(world, x, y, z, blockID, meta);
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
//    public int idDropped(int par1, Random par2Random, int par3)
//    {
//        return super.idDropped(1, par2Random, par3);
//    }
    
    @Override
    public int damageDropped (int meta)
    {
        if (meta == 0)
            return 1;
        return meta;
    }
    
    @SuppressWarnings ("unused")
    @Override
    @SideOnly(Side.CLIENT)
    public Icon getBlockTexture (IBlockAccess world, int x, int y, int z, int side)
    {
        final TileEntity logic = world.getBlockTileEntity(x, y, z);
        final short direction = (logic instanceof IFacingLogic) ? ((IFacingLogic) logic).getRenderDirection() : 0;
        final int meta = world.getBlockMetadata(x, y, z);
        if (meta == 8)
            if ((side == 0) || (side == 1))
                return icons[4];
        return icons[meta];
    }

    @Override
    public Icon getIcon (int side, int meta)
    {
        if (meta == 8)
            if ((side == 0) || (side == 1))
                return icons[4];
        return icons[meta];
    }

    @SuppressWarnings ({ "unchecked", "rawtypes" })
    @Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 9; iter++)
                list.add(new ItemStack(id, 1, iter));
    }

    @Override
    public void onBlockAdded (World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);
    }

    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack)
    {
        super.onBlockPlacedBy(world, x, y, z, entityliving, stack);
    }

    @Override
    public int quantityDropped (final Random random)
    {
        return 1;
    }

    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        icons = new Icon[textureNames.length];
        for (int i = 0; i < icons.length; ++i)
            icons[i] = iconRegister.registerIcon(Repo.textureDir + textureNames[i]);
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
