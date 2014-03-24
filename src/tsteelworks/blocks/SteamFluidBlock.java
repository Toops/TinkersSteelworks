package tsteelworks.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tsteelworks.lib.Repo;
import tsteelworks.lib.TSteelworksRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class SteamFluidBlock extends BlockFluidClassic
{
    public Icon stillIcon;
    public Icon flowIcon;
    boolean alpha = true;

    public SteamFluidBlock(int id, Fluid fluid, Material material)
    {
        super(id, fluid, material);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        stillIcon = iconRegister.registerIcon(Repo.textureDir + "liquid_steam");
        flowIcon = iconRegister.registerIcon(Repo.textureDir + "liquid_steam_flow");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (int side, int meta)
    {
        if (side == 0 || side == 1)
            return stillIcon;
        return flowIcon;
    }
}
