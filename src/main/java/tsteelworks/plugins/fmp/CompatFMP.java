package tsteelworks.plugins.fmp;

import tsteelworks.plugins.ICompatPlugin;

public class CompatFMP implements ICompatPlugin {
	// todo: Intermodcoms for ForgeMicroblocks (instead of FMP, caus it's microblocks that handles the microblocks)
	/*
	TSteelworks.logger.info("ForgeMultipart detected. Registering TSteelworks decorative blocks with FMP.");
    RegisterWithFMP.registerBlock(TSContent.charcoalBlock);
    RegisterWithFMP.registerBlock(TSContent.dustStorageBlock, 0, 1);
    RegisterWithFMP.registerBlock(TSContent.highoven, 2, 2);
    RegisterWithFMP.registerBlock(TSContent.highoven, 4, 11);
    RegisterWithFMP.registerBlock(TSContent.limestoneBlock, 0, 8);
    RegisterWithFMP.registerBlock(TSContent.cementBlock, 0, 15);
	 */

	@Override
	public String getModId() {
		return "Microblocks";
	}

	@Override
	public void init() {

	}

	@Override
	public void postInit() {

	}

	@Override
	public void preInit() {

	}
}
