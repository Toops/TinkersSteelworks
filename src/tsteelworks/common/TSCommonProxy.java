package tsteelworks.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tsteelworks.blocks.logic.DeepTankLogic;
import tsteelworks.blocks.logic.HighOvenDuctLogic;
import tsteelworks.lib.blocks.TSInventoryLogic;
import cpw.mods.fml.common.network.IGuiHandler;

public class TSCommonProxy implements IGuiHandler
{
    public static int manualGuiID = -1;
    public static int highovenGuiID = 0;
    public static int highovenDuctGuiID = 1;
    public static int deeptankGuiID = 2;

    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public Object getServerGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID < 0)
            return null;
        else if (ID < 100)
        {
            final TileEntity tile = world.getBlockTileEntity(x, y, z);
            if ((tile != null) && (tile instanceof TSInventoryLogic))
                return ((TSInventoryLogic) tile).getGuiContainer(player.inventory, world, x, y, z);
            if ((tile != null) && (tile instanceof HighOvenDuctLogic))
                return ((HighOvenDuctLogic) tile).getGuiContainer(player.inventory, world, x, y, z);
            if ((tile != null) && (tile instanceof DeepTankLogic))
                return ((DeepTankLogic) tile).getGuiContainer(player.inventory, world, x, y, z);
        }
        return null;
    }

    public void postInit ()
    {
    }

    public void readManuals ()
    {
    }

    public void registerRenderer ()
    {
    }

    public void registerSounds ()
    {
    }

    public void spawnParticle (String slimeParticle, double xPos, double yPos, double zPos, double velX, double velY, double velZ)
    {
    }
}
