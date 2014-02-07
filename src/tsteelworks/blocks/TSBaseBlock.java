package tsteelworks.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import tconstruct.blocks.TConstructBlock;
import tsteelworks.lib.TSteelworksRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TSBaseBlock extends TConstructBlock
{
    public TSBaseBlock (int id, Material material, float hardness, String[] tex)
    {
        super(id, material, hardness, tex);
        this.setCreativeTab(TSteelworksRegistry.SteelworksCreativeTab);
    }

    @Override
    @SideOnly (Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        this.icons = new Icon[this.textureNames.length];
        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tsteelworks:" + this.textureNames[i]);
        }
    }
}
