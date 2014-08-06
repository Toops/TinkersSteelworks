package tsteelworks.lib.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraftforge.fluids.FluidStack;
import tconstruct.library.crafting.AlloyMix;
import tconstruct.library.crafting.Smeltery;

public final class AlloyInfo
{
    public static final class DealloyInformation
    {
        public FluidStack alloy = null;
        public List<FluidStack> components = null;

        public DealloyInformation(FluidStack a, List<FluidStack> c)
        {
            alloy = a;
            components = c;
        }
    }

    public static ArrayList<AlloyMix> alloys = null;
    public static HashMap<FluidStack, DealloyInformation> dealloying = null;

    public static void init ()
    {
        alloys = Smeltery.getAlloyList();
        //Array size hints are handy.
        dealloying = new HashMap<FluidStack, DealloyInformation>(alloys.size());
        for (int i = 0; i < alloys.size(); ++i)
        {
            //World's hardest intermod compat code.
            AlloyMix currentMix = alloys.get(i);
            dealloying.put(currentMix.result, new DealloyInformation(currentMix.result, currentMix.mixers));
        }
    }

    /**
     * Takes a FluidStack
     * @param in
     * The fluidstack to get dealloying information for. Note: Not destroyed by this function. That behavior must be implemented in the code calling this function.
     * @return
     * A list of fluid stacks produced by the dealloying. Should equal the reagents required to produce this function's input in a Tinker's Construct smeltery.
     * Scaled to the size of our input stack.
     */
    public static ArrayList<FluidStack> deAlloy (FluidStack in)
    {
        //Is there a valid dealloying recipe for this fluidstack?
        //(Thanks Forge team for making FluidStacks implement comparable behavior, in a comparison that ignores amount.)
        if (dealloying.containsKey(in))
        {
            DealloyInformation resultInfo = dealloying.get(in);
            ArrayList<FluidStack> result = new ArrayList<FluidStack>(resultInfo.components.size());
            for (int i = 0; i < resultInfo.components.size(); ++i)
            {
                //float ratio = in.amount / resultInfo.alloy.amount;
                //Copy our fluidstack to get a result
                result.add(resultInfo.components.get(i).copy());
                //Scale our resultant fluidstacks.
                float ratio = (float)result.get(i).amount / (float)resultInfo.alloy.amount;
                result.get(i).amount = (int)(ratio * in.amount);
            }
            return result;
        }
        else
        {
            return null;
        }
    }
}
