package tsteelworks.blocks;

import tsteelworks.lib.Repo;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CementBlock extends TSBaseBlock
{
    static String[] textureNames = { "white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "lightgray", "cyan", "purple", "blue", "brown", "green", "red", "black" };
    
    public CementBlock(int id)
    {
        super(id, Material.rock, 3F, textureNames);
    }

    @Override
    public int getRenderBlockPass ()
    {
        return 1;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon(Repo.textureDir + "cement/" + "cement_" + textureNames[i]);
        }
    }
}
