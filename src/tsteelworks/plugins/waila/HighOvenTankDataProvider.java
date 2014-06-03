package tsteelworks.plugins.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import tsteelworks.blocks.logic.DeepTankLogic;
import tsteelworks.blocks.logic.HighOvenLogic;

// inspired from the one from TConstruct
public class HighOvenTankDataProvider implements IWailaDataProvider {

	/* (non-Javadoc)
	 * @see mcp.mobius.waila.api.IWailaBlock#getWailaStack(mcp.mobius.waila.api.IWailaDataAccessor, mcp.mobius.waila.api.IWailaConfigHandler)
	 */
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	/* (non-Javadoc)
	 * @see mcp.mobius.waila.api.IWailaBlock#getWailaHead(net.minecraft.item.ItemStack, java.util.List, mcp.mobius.waila.api.IWailaDataAccessor, mcp.mobius.waila.api.IWailaConfigHandler)
	 */
	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}
	
	/* (non-Javadoc)
	 * @see mcp.mobius.waila.api.IWailaBlock#getWailaBody(net.minecraft.item.ItemStack, java.util.List, mcp.mobius.waila.api.IWailaDataAccessor, mcp.mobius.waila.api.IWailaConfigHandler)
	 */
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {	
	    if (accessor.getTileEntity() instanceof DeepTankLogic)
        {
	        DeepTankLogic te = (DeepTankLogic) accessor.getTileEntity();
            if (te.validStructure)
            {
    			List<FluidStack> fls = te.getAllFluids();
<<<<<<< HEAD
    			WailaHelper.showFluids(currenttip, config, fls, te.getCapacity());
//    			if(fls != null)
//    			{
//    				if(fls.size() <= 0)
//    				{ 
//    					currenttip.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("tconstruct.waila.empty")); // "Â§o" == Italics
//    				}
//    				else
//    				{
//    					for(FluidStack stack : fls)
//    					{
//    						currenttip.add(WailaRegistrar.fluidNameHelper(stack) + " (" + stack.amount + "mB)");
//    					}
//    					currenttip.add(te.getTotalFluidAmount() + "mB / " + te.getCapacity() + "mB Total");
//    				}
//    			}
            }
    		else
    		{
    			currenttip.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("tconstruct.waila.invalidstructure")); // "Â§o" == Italics
=======
    			showFluids(currenttip, config, fls);
            }
    		else
    		{
    			currenttip.add("§o" + StatCollector.translateToLocal("tconstruct.waila.invalidstructure")); // "§o" == Italics
>>>>>>> be110a673570a6e64f4cdb20a0ce52050375eacd
    		}
        }
	    else if (accessor.getTileEntity() instanceof HighOvenLogic)
        {
	        HighOvenLogic te = (HighOvenLogic) accessor.getTileEntity();
            if (te.validStructure)
            {
                List<FluidStack> fls = te.moltenMetal;
<<<<<<< HEAD
                WailaHelper.showFluids(currenttip, config, fls, te.getCapacity());
//                if(fls != null)
//                {
//                    if(fls.size() <= 0)
//                    {
//                        currenttip.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("tconstruct.waila.empty")); // "Â§o" == Italics
//                    }
//                    else
//                    {
//                        for(FluidStack stack : fls)
//                        {
//                            currenttip.add(WailaRegistrar.fluidNameHelper(stack) + " (" + stack.amount + "mB)");
//                            
//                        }
//                        currenttip.add(te.getTotalFluidAmount() + "mB / " + te.getCapacity() + "mB Total");
//                    }
//                }
            }
            else
            {
                currenttip.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("tconstruct.waila.invalidstructure")); // "Â§o" == Italics
=======
                showFluids(currenttip, config, fls);
            }
            else
            {
                currenttip.add("§o" + StatCollector.translateToLocal("tconstruct.waila.invalidstructure")); // "§o" == Italics
>>>>>>> be110a673570a6e64f4cdb20a0ce52050375eacd
            }
        }
		return currenttip;
	}
	
	   /* (non-Javadoc)
     * @see mcp.mobius.waila.api.IWailaBlock#getWailaTail(net.minecraft.item.ItemStack, java.util.List, mcp.mobius.waila.api.IWailaDataAccessor, mcp.mobius.waila.api.IWailaConfigHandler)
     */
    @Override
    public List<String> getWailaTail (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }
}
