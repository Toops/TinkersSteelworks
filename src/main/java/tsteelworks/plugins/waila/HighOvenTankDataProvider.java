package tsteelworks.plugins.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import tsteelworks.common.blocks.logic.DeepTankLogic;
import tsteelworks.common.blocks.logic.HighOvenLogic;

// inspired from the one from TConstruct
public class HighOvenTankDataProvider implements IWailaDataProvider {
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
	    if (accessor.getTileEntity() instanceof DeepTankLogic)
        {
	        DeepTankLogic te = (DeepTankLogic) accessor.getTileEntity();
            if (te.isValid())
            {
    			List<FluidStack> fls = te.getAllFluids();
    			WailaHelper.showFluids(currenttip, config, fls, te.getCapacity());
            }
    		else
    		{
    			currenttip.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("tconstruct.waila.invalidstructure"));
            }
        }
	    else if (accessor.getTileEntity() instanceof HighOvenLogic)
        {
	        HighOvenLogic te = (HighOvenLogic) accessor.getTileEntity();
            if (te.isValid())
            {
                List<FluidStack> fls = te.getFluidlist();
                WailaHelper.showFluids(currenttip, config, fls, te.getCapacity());
            }
            else
            {
                currenttip.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("tconstruct.waila.invalidstructure"));
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
