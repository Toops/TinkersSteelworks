package tsteelworks.plugins.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import tsteelworks.blocks.logic.DeepTankLogic;

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
		TileEntity te = accessor.getTileEntity();
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
						currenttip.add(WailaRegistrar.fluidNameHelper(stack) + " (" + stack.amount + "mB)");
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
