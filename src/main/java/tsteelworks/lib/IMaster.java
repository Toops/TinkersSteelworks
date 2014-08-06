package tsteelworks.lib;

import tconstruct.library.util.CoordTuple;

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
