package tsteelworks.blocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import tconstruct.blocks.SlabBase;
import tconstruct.common.TContent;
import tconstruct.library.TConstructRegistry;
import tsteelworks.common.TSContent;
import tsteelworks.lib.TSteelworksRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ScorchedSlab extends TSBaseSlab
{
    public ScorchedSlab(int id)
    {
        super(id, Material.rock);
        this.setCreativeTab(TSteelworksRegistry.SteelworksCreativeTab);
        setHardness(12F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (int side, int meta)
    {
        meta = meta % 8;
        if (meta == 0)
            return TSContent.highoven.getIcon(side, 2);
        if (meta <= 3)
            return TSContent.highoven.getIcon(side, meta + 3);

        return TSContent.highoven.getIcon(side, meta + 4);
    }

    @Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 8; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }
}
