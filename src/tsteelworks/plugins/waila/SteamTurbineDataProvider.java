package tsteelworks.plugins.waila;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.blocks.logic.LavaTankLogic;
import tconstruct.plugins.waila.WailaRegistrar;
import tsteelworks.blocks.logic.TurbineLogic;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class SteamTurbineDataProvider implements IWailaDataProvider
{
    @Override
    public ItemStack getWailaStack (IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return null;
    }

    @Override
    public List<String> getWailaHead (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getTileEntity() instanceof TurbineLogic)
        {
            TurbineLogic te = (TurbineLogic) accessor.getTileEntity();
            if (te.containsFluid())
            {
                FluidStack fs = te.tank.getFluid();
                currenttip.add(WailaRegistrar.fluidNameHelper(fs) + ": " + fs.amount + "/" + te.tank.getCapacity() + "mB");
            }
            else
            {
                currenttip.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("tconstruct.waila.empty")); // "§o" == Italics
            }
        }
        return currenttip;
    }
    
    @Override
    public List<String> getWailaTail (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }
}
