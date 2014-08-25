package tsteelworks.lib;

import mantle.world.CoordTuple;

public interface IMaster {
	/**
	 *
	 * @return the coordinate of the master
	 */
	public CoordTuple getCoord();

	/**
	 *
	 * @return true = the master is valid / false = the master is not valid
	 */
	public boolean isValid();

	public int getBlockMetadata();

	public int getBlockId();
}
