/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Aug 21, 2014, 5:44:07 PM (GMT)]
 */
package vazkii.botania.api.mana.spark;

import java.util.Collection;

/**
 * An Entity that implements this is considered a Spark.
 */
public interface ISparkEntity {

	/**
	 * Which TileEntity is this Spark attached to? A common implementation is checking the block below.
	 * 
	  	int x = MathHelper.floor_double(posX);
		int y = MathHelper.floor_double(posY) - 1;
		int z = MathHelper.floor_double(posZ);
		TileEntity tile = worldObj.getTileEntity(x, y, z);
		if(tile != null && tile instanceof ISparkAttachable)
			return (ISparkAttachable) tile;

		return null;
	 */
	public ISparkAttachable getAttachedTile();

	/**
	 * Gets a collection of all Sparks this is tranfering to.
	 */
	public Collection<ISparkEntity> getTransfers();

	/**
	 * Registers the Spark passed in as a Spark meant for mana to be transfered towards.
	 */
	public void registerTransfer(ISparkEntity entity);

	/**
	 * Gets which upgrade is in this Spark.<br>
	 * 0: None<br>
	 * 1: Dispersive<br>
	 * 2: Dominant<br>
	 * 3: Recessive<br>
	 * 4: Isolated
	 */
	public int getUpgrade();

	/**
	 * Sets the upgrade on this Spark. See {@link ISparkEntity#getUpgrade}
	 */
	public void setUpgrade(int upgrade);

	/**
	 * See {@link ISparkAttachable#areIncomingTranfersDone()}
	 */
	public boolean areIncomingTransfersDone();

}
