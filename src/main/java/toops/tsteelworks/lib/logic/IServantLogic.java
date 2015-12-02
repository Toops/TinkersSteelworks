package toops.tsteelworks.lib.logic;

import net.minecraft.world.World;

public interface IServantLogic {
	boolean hasMaster();

	/**
	 * The block should already have a valid master
	 */
	void notifyMasterOfChange();

	/**
	 * Checks if this block can be tied to this master
	 *
	 * @param master The master to be tied to.
	 * @param world  The world the master is in.
	 * @return whether the servant can be tied to this master
	 */
	boolean setPotentialMaster(IMasterLogic master, World world);

	/**
	 * Used to verify that this is the block's master
	 *
	 * @param master The master to verify.
	 * @param world  The world the master is in.
	 * @return is this block tied to this master ?
	 */
	boolean verifyMaster(IMasterLogic master, World world);
}
