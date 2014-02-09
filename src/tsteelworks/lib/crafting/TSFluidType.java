/**
 * 
 */
package tsteelworks.lib.crafting;

import net.minecraftforge.fluids.Fluid;
import tsteelworks.common.TSContent;

/**
 * @author Toops
 */
public enum TSFluidType
{
    /** Monoatomic Gold Smelting **/
    MonoatomicGold(TSContent.metalBlockTS.blockID, 0, 700, TSContent.moltenMonoatomicGoldFluid, false);

    /*
     * Public Variables
     */
    public final int     renderBlockID;
    public final int     renderMeta;
    public final int     baseTemperature;
    public final Fluid   fluid;
    public final boolean isToolpart;

    /**
     * Fluid Type Constructor
     * 
     * @param blockID
     * @param meta
     * @param baseTemperature
     * @param fluid
     * @param isToolpart
     */
    TSFluidType (int blockID, int meta, int baseTemperature, Fluid fluid, boolean isToolpart)
    {
        renderBlockID = blockID;
        renderMeta = meta;
        this.baseTemperature = baseTemperature;
        this.fluid = fluid;
        this.isToolpart = isToolpart;
    }

    /**
     * Get the fluid type by Fluid
     * 
     * @param searchedFluid
     * @return The fluid type
     */
    public static TSFluidType getFluidType (Fluid searchedFluid)
    {
        for (final TSFluidType type : values())
        {
            if (type.fluid.getBlockID() == searchedFluid.getBlockID()) return type;
        }
        return null;
    }

    /**
     * Get temperature value by Fluid
     * 
     * @param searchedFluid
     * @return The fluid's base temperature
     */
    public static int getTemperatureByFluid (Fluid searchedFluid)
    {
        for (final TSFluidType type : values())
        {
            if (type.fluid.getBlockID() == searchedFluid.getBlockID()) return type.baseTemperature;
        }
        return 800;
    }
}