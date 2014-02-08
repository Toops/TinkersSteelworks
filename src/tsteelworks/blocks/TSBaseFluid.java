package tsteelworks.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import tsteelworks.lib.Repo;
import tsteelworks.lib.TSteelworksRegistry;

public class TSBaseFluid extends BlockFluidClassic
{
    String      texture;
    boolean     alpha;
    public Icon stillIcon;
    public Icon flowIcon;

    public TSBaseFluid (int id, Fluid fluid, Material material, String texture)
    {
        super(id, fluid, material);
        this.texture = texture;
        setCreativeTab(TSteelworksRegistry.SteelworksCreativeTab);
    }

    public TSBaseFluid (int id, Fluid fluid, Material material, String texture, boolean alpha)
    {
        this(id, fluid, material, texture);
        this.alpha = alpha;
    }

    @Override
    public int getRenderBlockPass ()
    {
        return alpha ? 1 : 0;
    }

    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        stillIcon = iconRegister.registerIcon(Repo.textureDir + texture);
        flowIcon = iconRegister.registerIcon(Repo.textureDir + texture + "_flow");
    }

    @Override
    public Icon getIcon (int side, int meta)
    {
        if ((side == 0) || (side == 1)) return stillIcon;
        return flowIcon;
    }
}
