package tsteelworks.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import tsteelworks.common.TSRepo;
import tsteelworks.lib.TSteelworksRegistry;

public class DustStorageBlock extends BlockSand
{
    public static final String[] TEXTURE_NAMES = new String[] { "gunpowder", "sugar" };
    public Icon[] icons;

    public DustStorageBlock(int id)
    {
        super(id, Material.sand);
        setCreativeTab(TSteelworksRegistry.SteelworksCreativeTab);
        setStepSound(soundSandFootstep);
    }

    /*
     * (non-Javadoc)
     * @see net.minecraft.block.Block#damageDropped(int)
     */
    @Override
    public int damageDropped (int meta)
    {
        return meta;
    }

    /*
     * (non-Javadoc)
     * @see net.minecraft.block.Block#getBlockHardness(net.minecraft.world.World, int, int, int)
     */
    @Override
    public float getBlockHardness (World world, int x, int y, int z)
    {
        return 3f;
    }

    /*
     * (non-Javadoc)
     * @see net.minecraft.block.Block#getIcon(int, int)
     */
    @Override
    public Icon getIcon (int side, int meta)
    {
        return icons[meta];
    }

    /*
     * (non-Javadoc)
     * @see net.minecraft.block.Block#getSubBlocks(int, net.minecraft.creativetab.CreativeTabs, java.util.List)
     */
    @SuppressWarnings({ "rawtypes", "unchecked"})
	@Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
    	for (int iter = 0; iter < 2; iter++)
            list.add(new ItemStack(id, 1, iter));
    }
    
    

    /*
     * (non-Javadoc)
     * @see net.minecraft.block.Block#idDropped(int, java.util.Random, int)
     */
    @Override
    public int idDropped (int par1, Random par2Random, int par3)
    {
        return blockID;
    }

    /*
     * (non-Javadoc)
     * @see net.minecraft.block.Block#registerIcons(net.minecraft.client.renderer.texture.IconRegister)
     */
    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        icons = new Icon[TEXTURE_NAMES.length];

        for (int i = 0; i < icons.length; ++i)
            icons[i] = iconRegister.registerIcon(TSRepo.textureDir + TEXTURE_NAMES[i] + "_block");
    }
}
