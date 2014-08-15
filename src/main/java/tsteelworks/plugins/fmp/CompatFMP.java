package tsteelworks.plugins.fmp;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.item.ItemStack;
import tsteelworks.common.core.TSContent;
import tsteelworks.plugins.ICompatPlugin;

public class CompatFMP implements ICompatPlugin {
	@Override
	public String getModId() {
		return "ForgeMicroblock";
	}

	@Override
	public void init() {
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", TSContent.charcoalBlock);

		for (int i = 0; i <= 1; i++) {
			FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", new ItemStack(TSContent.dustStorageBlock, 0, i));
		}

		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", new ItemStack(TSContent.highoven, 0, 2));

		for (int i = 4; i <= 11; i++) {
			FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", new ItemStack(TSContent.highoven, 0, i));
		}

		for (int i = 0; i <= 8; i++) {
			FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", new ItemStack(TSContent.limestoneBlock, 0, i));
		}

		for (int i = 0; i <= 15; i++) {
			FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", new ItemStack(TSContent.cementBlock, 0, i));
		}
	}

	@Override
	public void postInit() {

	}

	@Override
	public void preInit() {

	}
}
