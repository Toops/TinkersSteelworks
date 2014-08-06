package tsteelworks.lib;

import tconstruct.library.util.IMasterLogic;

public interface ITSMasterLogic extends IMasterLogic {
	/**
	 * 
	 * @return true = the master is valid / false = the master is not valid
	 */
	public boolean isValid();
}
