package toops.tsteelworks.lib.logic;

import net.minecraft.world.World;

public interface IServantLogic {
	public boolean hasMaster();

	/**
	 * The block should already have a valid master
	 */
	public void notifyMasterOfChange();

	/**
	 * Checks if this block can be tied to this master
	 *
	 * @param master the master to be tied to
	 * @return whether  the servant can be tied to this master
	 */
	public boolean setPotentialMaster(IMasterLogic master, World world);

	/**
	 * Used to verify that this is the block's master
	 *
	 * @param master the master
	 * @return is this block tied to this master ?
	 */
	public boolean verifyMaster(IMasterLogic master, World world);
}
