/**
 * 
 */
package tsteelworks.lib.crafting;

import tsteelworks.common.TSContent;
import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * @author Toops
 *
 */
public enum TSFluidType
{
    /** Gold  Smelting **/
    MonoatomicGold(TSContent.metalBlock.blockID, 0, 700, TSContent.moltenMonoatomicGoldFluid, false);
    

    public final int renderBlockID;
    public final int renderMeta;
    public final int baseTemperature;
    public final Fluid fluid;
    public final boolean isToolpart;

    TSFluidType(int blockID, int meta, int baseTemperature, Fluid fluid, boolean isToolpart)
    {
        this.renderBlockID = blockID;
        this.renderMeta = meta;
        this.baseTemperature = baseTemperature;
        this.fluid = fluid;
        this.isToolpart = isToolpart;
    }

    public static TSFluidType getFluidType (Fluid searchedFluid)
    {
        for (TSFluidType ft : values())
        {
            if (ft.fluid.getBlockID() == searchedFluid.getBlockID())
                return ft;
        }
        return null;
    }

    public static int getTemperatureByFluid (Fluid searchedFluid)
    {
        for (TSFluidType ft : values())
        {
            if (ft.fluid.getBlockID() == searchedFluid.getBlockID())
                return ft.baseTemperature;
        }
        return 800;
    }
}