package tsteelworks.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import tsteelworks.blocks.logic.HighOvenLogic;
import tsteelworks.client.gui.HighOvenGui;
import tsteelworks.common.TSCommonProxy;

public class TSClientProxy extends TSCommonProxy
{
    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == highOvenGuiID)
            return new HighOvenGui(player.inventory, (HighOvenLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
        return null;
    }

    @Override
    public void registerRenderers ()
    {
        // Stub: For rendering entities, etc
    }

    @Override
    public void registerSounds ()
    {}
}
