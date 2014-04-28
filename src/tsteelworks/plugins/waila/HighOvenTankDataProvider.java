package tsteelworks.plugins.waila;

import java.util.List;

import net.minecraft.item.ItemStack;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

// inspired from the one from TConstruct
public class HighOvenTankDataProvider implements IWailaDataProvider {

	/* (non-Javadoc)
	 * @see mcp.mobius.waila.api.IWailaBlock#getWailaStack(mcp.mobius.waila.api.IWailaDataAccessor, mcp.mobius.waila.api.IWailaConfigHandler)
	 */
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see mcp.mobius.waila.api.IWailaBlock#getWailaHead(net.minecraft.item.ItemStack, java.util.List, mcp.mobius.waila.api.IWailaDataAccessor, mcp.mobius.waila.api.IWailaConfigHandler)
	 */
	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		return currenttip;
	}
	
	/* (non-Javadoc)
	 * @see mcp.mobius.waila.api.IWailaBlock#getWailaBody(net.minecraft.item.ItemStack, java.util.List, mcp.mobius.waila.api.IWailaDataAccessor, mcp.mobius.waila.api.IWailaConfigHandler)
	 */
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {	
		TileEntity te = accessor.getTileEntity()
		if (te instanceof DeepTankLogic)
		{
			DeepTankLogic dtl = (DeepTankLogic) te;
			List<FluidStack> fls = dtl.getAllFluids();
			if(fls != null)
			{
				if(fls.size() <= 0)
				{
					currenttip.add("§o" + StatCollector.translateToLocal("tconstruct.waila.empty")); // "§o" == Italics
				}
				else
				{
					for(FluidStack stack : fls)
					{
						currenttip.add(WailaRegistrar.fluidNameHelper(st) + " (" + st.amount + "mB)");
					}
				}
			}
			else
			{
				// do what? consider as empty?
				currenttip.add("§o" + StatCollector.translateToLocal("tconstruct.waila.empty")); // "§o" == Italics
			}
		}
		return currenttip;
	}
}
