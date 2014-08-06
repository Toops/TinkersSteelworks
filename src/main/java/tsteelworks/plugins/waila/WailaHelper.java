package tsteelworks.plugins.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

public class WailaHelper {
	public static void showFluids(List<String> currenttip, IWailaConfigHandler config, List<FluidStack> fls, int maxCapacity) {
		if(fls != null)
		{
			if(fls.size() <= 0)
			{
				currenttip.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("tconstruct.waila.empty")); // "§o" == Italics
			}
			else
			{
				int total = 0;
				boolean autoUnit = config.getConfig("tseelworks.autoUnit");
				for(FluidStack stack : fls)
				{
					int amount = stack.amount;
					total += amount;

					String textValue = formatFluidValue(autoUnit, amount);
					currenttip.add(WailaRegistrar.fluidNameHelper(stack) + " (" + textValue + ")");
				}
				if(config.getConfig("tseelworks.showTotal"))
				{
					currenttip.add("-----");
					//currenttip.add("total : "+formatFluidValue(autoUnit, total));
					currenttip.add(formatFluidValue(autoUnit, total) + " / " + formatFluidValue(autoUnit, maxCapacity) +" Total");
				}
			}
		}
	}

	public static String formatFluidValue(boolean autoUnit, int amount) {
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
}
