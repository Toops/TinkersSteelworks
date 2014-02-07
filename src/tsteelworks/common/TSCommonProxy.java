package tsteelworks.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tconstruct.library.blocks.InventoryLogic;
import cpw.mods.fml.common.network.IGuiHandler;

public class TSCommonProxy implements IGuiHandler
{
    public static int highOvenGuiID = 0;

    @Override
    public Object getServerGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID < 0)
            return null;
        else
            if (ID < 100)
            {
                final TileEntity tile = world.getBlockTileEntity(x, y, z);
                if ((tile != null) && (tile instanceof InventoryLogic))
                    return ((InventoryLogic) tile).getGuiContainer(player.inventory, world, x, y, z);
            }
        return null;
    }

    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    public void registerRenderers ()
    {
        // Stub
    }

    public void registerSounds ()
    {}
}
