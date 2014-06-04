package tsteelworks.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import tsteelworks.lib.Repo;

public class TSBaseFluid extends BlockFluidClassic
{
    String texture;
    boolean alpha;
    public Icon stillIcon;
    public Icon flowIcon;
    public int renderColor = 16777215;
    
    public TSBaseFluid(int id, Fluid fluid, Material material, String texture)
    {
        super(id, fluid, material);
        this.texture = texture;
    }

    public TSBaseFluid(int id, Fluid fluid, Material material, String texture, boolean alpha)
    {
        this(id, fluid, material, texture);
        this.alpha = alpha;
    }

    @Override
    public int getRenderBlockPass ()
    {
        return alpha ? 1 : 0;
    }

//    @Override
//    public int getBlockColor()
//    {
//        return renderColor;
//    }
//
//    @Override
//    /**
//     * Returns the color this block should be rendered. Used by leaves.
//     */
//    public int getRenderColor(int par1)
//    {
//        return renderColor;
//    }

//    @Override
    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
//    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
//    {
//        return renderColor;
//    }
//    
//    public void setRenderColor(int colorvalue)
//    {
//        renderColor = colorvalue;
//    }
    
    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        stillIcon = iconRegister.registerIcon(Repo.textureDir + texture);
        flowIcon = iconRegister.registerIcon(Repo.textureDir + texture + "_flow");
    }

    @Override
    public Icon getIcon (int side, int meta)
    {
        if (side == 0 || side == 1)
            return stillIcon;
        return flowIcon;
    }
}
