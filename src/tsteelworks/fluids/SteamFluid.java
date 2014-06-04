package tsteelworks.fluids;

import net.minecraft.item.EnumRarity;
import net.minecraftforge.fluids.Fluid;

public class SteamFluid extends Fluid
{
    /**
     * The light level emitted by this fluid.
     *
     * Default value is 0, as most fluids do not actively emit light.
     */
    protected int luminosity = 0;

    /**
     * Density of the fluid - completely arbitrary; negative density indicates that the fluid is
     * lighter than air.
     *
     * Default value is approximately the real-life density of water in kg/m^3.
     */
    protected int density = -200;

    /**
     * Temperature of the fluid - completely arbitrary; higher temperature indicates that the fluid is
     * hotter than air.
     * 
     * Default value is approximately the real-life room temperature of water in degrees Kelvin.
     */
    protected int temperature = 588;

    /**
     * Viscosity ("thickness") of the fluid - completely arbitrary; negative values are not
     * permissible.
     *
     * Default value is approximately the real-life density of water in m/s^2 (x10^-3).
     */
    protected int viscosity = 5;

    /**
     * This indicates if the fluid is gaseous.
     *
     * Useful for rendering the fluid in containers and the world.
     *
     * Generally this is associated with negative density fluids.
     */
    protected boolean isGaseous = true;

    /**
     * The rarity of the fluid.
     *
     * Used primarily in tool tips.
     */
    protected EnumRarity rarity = EnumRarity.common;
    
    /**
     * @param fluidName
     */
    public SteamFluid(String fluidName)
    {
        super(fluidName);
    }

}
