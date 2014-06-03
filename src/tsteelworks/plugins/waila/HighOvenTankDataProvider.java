package tsteelworks.plugins.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
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
            if (te.isStructureValid())
            {
    			List<FluidStack> fls = te.getAllFluids();
    			showFluids(currenttip, config, fls);
            }
    		else
    		{
    			currenttip.add("§o" + StatCollector.translateToLocal("tconstruct.waila.invalidstructure")); // "§o" == Italics
    		}
        }
	    else if (accessor.getTileEntity() instanceof HighOvenLogic)
        {
	        HighOvenLogic te = (HighOvenLogic) accessor.getTileEntity();
            if (te.validStructure)
            {
                List<FluidStack> fls = te.moltenMetal;
                showFluids(currenttip, config, fls);
            }
            else
            {
                currenttip.add("§o" + StatCollector.translateToLocal("tconstruct.waila.invalidstructure")); // "§o" == Italics
            }
        }
		return currenttip;
	}

	private void showFluids(List<String> currenttip, IWailaConfigHandler config, List<FluidStack> fls) {
		if(fls != null)
		{
			if(fls.size() <= 0)
			{
				currenttip.add("§o" + StatCollector.translateToLocal("tconstruct.waila.empty")); // "§o" == Italics
			}
			else
			{
				int total = 0;
				boolean autoUnit = config.getConfig("tseelworks.autoUnit");
				for(FluidStack stack : fls)
				{
					int amount = stack.amount;
					total  += amount;
					
					String textValue = formatFluidValue(autoUnit, amount);
					currenttip.add(WailaRegistrar.fluidNameHelper(stack) + " (" + textValue + ")");
				}
				if(config.getConfig("tseelworks.showTotal"))
				{
					currenttip.add("-----");
					currenttip.add("total : "+formatFluidValue(autoUnit, total));
				}
			}
		}
	}

	private String formatFluidValue(boolean autoUnit, int amount) {
		String textValue = "";
		if(!autoUnit || amount < 1000)
		{
			textValue += amount + "mB";
		}else
		{
			double converted = amount;
			converted = converted / 1000;
			if(converted < 1000)
			{
				textValue += converted +"B";
			}else
			{
				converted = converted / 1000;
				textValue += converted +"kB";
			}
			
		}
		return textValue;
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
