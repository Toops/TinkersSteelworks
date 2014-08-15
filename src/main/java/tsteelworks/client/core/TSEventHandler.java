package tsteelworks.client.core;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import tsteelworks.common.core.TSContent;

public class TSEventHandler {
	@SubscribeEvent
	public void reloadTextures(TextureStitchEvent.Post event) {
		if (TSContent.steamIsOurs)
			TSContent.steamFluid.setIcons(TSContent.steamBlock.getIcon(0,0), TSContent.steamBlock.getIcon(2, 0));

		if (TSContent.limestoneIsOurs)
			TSContent.moltenLimestoneFluid.setIcons(TSContent.moltenLimestone.getIcon(0,0), TSContent.moltenLimestone.getIcon(2, 0));

		if (TSContent.cementIsOurs)
			TSContent.liquidCementFluid.setIcons(TSContent.liquidCement.getIcon(0,0), TSContent.liquidCement.getIcon(2, 0));
	}
}
