package tsteelworks.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IFacingLogic;
import tsteelworks.TSteelworks;
import tsteelworks.blocks.logic.TurbineLogic;
import tsteelworks.client.block.MachineRender;
import tsteelworks.lib.Repo;
import tsteelworks.lib.TSteelworksRegistry;
import tsteelworks.lib.blocks.TSInventoryBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MachineBlock extends TSInventoryBlock
{
    public String[] textureNames = new String[] { "gunpowder", "sugar" };
    public Icon[] icons;
    static ArrayList<CoordTuple> directions = new ArrayList<CoordTuple>(6);

    public MachineBlock(int id) {
            super(id, Material.iron);
            setHardness(3F);
            setResistance(20F);
            this.setCreativeTab(TSteelworksRegistry.SteelworksCreativeTab);
            setUnlocalizedName("tsteelworks.Machine");
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
            return null; 
    }
    
    @Override
    public TileEntity createTileEntity (World world, int metadata)
    {
        return new TurbineLogic();
    }
   
    @Override
    public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        return null;
    }
    
    @Override
    public String[] getTextureNames ()
    {
        final String[] textureNames = { "turbine_front", "turbine_side", "turbine_back"};
        return textureNames;
    }
        
    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        final String[] textureNames = getTextureNames();
        icons = new Icon[textureNames.length];

        for (int i = 0; i < icons.length; ++i)
            icons[i] = iconRegister.registerIcon(Repo.textureDir + textureNames[i]);
    }
    
    @Override
    public Icon getIcon (int side, int meta)
    {
        if (meta == 0)
        {
            final int sideTex = side == 3 ? 1 : 0;
            return icons[sideTex + (meta * 3)];
        }
        return icons[0];
    }
    
    @Override
    public Icon getBlockTexture (IBlockAccess world, int x, int y, int z, int side)
    {
        final TileEntity logic = world.getBlockTileEntity(x, y, z);
        final short direction = (logic instanceof IFacingLogic) ? ((IFacingLogic) logic).getRenderDirection() : 0;
        final int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) {
            if (side == direction)
            {
                return icons[0];
            }
            else if (side / 2 == direction / 2)
            {
                return icons[2];
            }
            return icons[1];
        }
        return icons[0];
    }
    
    public int getTextureIndex (int side)
    {
        return getTextureIndex(side, false);
    }

    public int getTextureIndex (int side, boolean alt)
    {
        if (side == 0)
            return 2;
        if (side == 1)
            return 0;

        return alt ? 9 : 1;
    }

    public int getRenderType ()
    {
        return MachineRender.model;
    }
    
    
    
    @Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
//        for (int iter = 0; iter < 0; iter++)
            list.add(new ItemStack(id, 1, 0));
    }
    
    @Override
    public int damageDropped (int meta)
    {
        return meta;
    }
    
    @Override
    public int quantityDropped (final Random random)
    {
        return 1;
    }
    
    @Override
    public Object getModInstance ()
    {
        return TSteelworks.instance;
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
