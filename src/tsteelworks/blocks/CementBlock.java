package tsteelworks.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import tsteelworks.lib.Repo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CementBlock extends TSBaseBlock
{
    static final String[] TEXTURE_NAME = { "white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "lightgray", "cyan", "purple", "blue", "brown", "green", "red", "black" };
    
    public CementBlock(int id)
    {
        super(id, Material.rock, 3F, TEXTURE_NAME);
    }

    /*
     * (non-Javadoc)
     * @see net.minecraft.block.Block#getRenderBlockPass()
     */
    @Override
    public int getRenderBlockPass ()
    {
        return 1;
    }
    
    /*
     * (non-Javadoc)
     * @see tsteelworks.blocks.TSBaseBlock#registerIcons(net.minecraft.client.renderer.texture.IconRegister)
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        this.icons = new Icon[TEXTURE_NAME.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon(Repo.textureDir + "cement/" + "cement_" + TEXTURE_NAME[i]);
        }
    }
}
