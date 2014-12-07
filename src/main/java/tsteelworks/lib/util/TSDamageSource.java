/**
 * 
 */
package tsteelworks.lib.util;

import net.minecraft.util.DamageSource;

/**
 * Class to manage the damage done by blocks/item/entity from this mod
 * @author wisthler
 *
 */
public class TSDamageSource extends DamageSource {
	public static TSDamageSource MOLTEN_LIMESTONE_DAMAGE = (TSDamageSource) (new TSDamageSource("moltenLimestone")).setFireDamage();

	/**
	 * @param par1Str
	 */
	protected TSDamageSource(String par1Str) {
		super(par1Str);
	}

}
