package tsteelworks.blocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import tsteelworks.common.TSContent;
import tsteelworks.lib.TSteelworksRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LimestoneSlab extends TSBaseSlab
{
    public LimestoneSlab(int id)
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
        if (meta <= 2)
            return TSContent.limestoneBlock.getIcon(side, meta);
        
        return TSContent.limestoneBlock.getIcon(side, meta + 1);
    }

    @SuppressWarnings ({ "rawtypes", "unchecked" })
    @Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 8; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }
}
