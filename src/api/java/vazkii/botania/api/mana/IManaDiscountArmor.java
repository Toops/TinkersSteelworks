/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jan 28, 2015, 9:22:53 PM (GMT)]
 */
package vazkii.botania.api.mana;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * An armor item that implements this can provide a mana discount for mana tools.
 * Mana tools are the ones on the main toolset (Pick, Shovel, Axe, Sword and Shovel).
 */
public interface IManaDiscountArmor {

	/**
	 * Gets the mana discount that this piece of armor provides. This is added
	 * together to create the full discount.
	 * Value is to be from 0.0 to 1.0. 0.1 is 10% discount, as an example.
	 * You can also return negative values to make tools cost more.
	 */
	public float getDiscount(ItemStack stack, int slot, EntityPlayer player);

}
