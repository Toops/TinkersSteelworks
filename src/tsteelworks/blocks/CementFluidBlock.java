package tsteelworks.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;
import tsteelworks.lib.TSteelworksRegistry;

public class CementFluidBlock extends TSBaseFluid
{
    public CementFluidBlock(int id, Fluid fluid, Material material, String texture)
    {
        super(id, fluid, material, texture);
    }
    
    @SideOnly(Side.CLIENT)
    public void setRenderColorByMeta ()
    {
        
    }
}
