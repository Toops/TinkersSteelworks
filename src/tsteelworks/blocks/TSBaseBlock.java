package tsteelworks.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import tsteelworks.common.TSRepo;
import tsteelworks.lib.TSteelworksRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TSBaseBlock extends Block
{
    public String[] textureNames;
    public Icon[] icons;

    public TSBaseBlock(int id, Material material, float hardness)
    {
        super(id, material);
        setHardness(hardness);
        setCreativeTab(TSteelworksRegistry.SteelworksCreativeTab);
    }
    
    public TSBaseBlock(int id, Material material, float hardness, String[] tex)
    {
        super(id, material);
        setHardness(hardness);
        setCreativeTab(TSteelworksRegistry.SteelworksCreativeTab);
        textureNames = tex;
    }

    @Override
    public int damageDropped (int meta)
    {
        return meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (int side, int meta)
    {
        return meta < icons.length ? icons[meta] : icons[0];
    }

    @SideOnly(Side.CLIENT)
    public int getSideTextureIndex (int side)
    {
        if (side == 0)  return 2;
        if (side == 1) return 0;
        return 1;
    }

    @SuppressWarnings ({ "rawtypes", "unchecked" })
    @Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < icons.length; iter++)
            list.add(new ItemStack(id, 1, iter));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        icons = new Icon[textureNames.length];
        for (int i = 0; i < icons.length; ++i)
            icons[i] = iconRegister.registerIcon(TSRepo.textureDir + textureNames[i]);
    }
}
