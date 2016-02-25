package toops.tsteelworks.lib.logic;

import mantle.world.CoordTuple;
import toops.tsteelworks.common.structure.IStructure;

public interface IMasterLogic {
	/**
	 * Get coordinates of the master
	 *
	 * @return the coordinate of the master
	 */
	CoordTuple getCoord();

	/**
	 * Called when servants change their state
	 *
	 * @param servant The servant which changed state.
	 * @param x Servant X
	 * @param y Servant Y
	 * @param z Servant Z
	 */
	void notifyChange(IServantLogic servant, int x, int y, int z);

	/**
	 * Determine if a structure is valid
	 *
	 * @return true = the master is valid / false = the master is not valid
	 */
	boolean isValid();

	/**
	 * Check valid placement.
	 */
	void checkValidPlacement();

	void onStructureChange(IStructure structure);

	IStructure getStructure();
}
