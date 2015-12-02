package toops.tsteelworks.client.core;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import toops.tsteelworks.common.core.TSContent;
import toops.tsteelworks.lib.ModsData;

public class TSEventHandler {
	@SubscribeEvent
	public void reloadResources(TextureStitchEvent.Post event) {
		if (TSContent.steamFluid != null)
			TSContent.steamFluid.setIcons(ModsData.Fluids.steamBlock.getIcon(0, 0), ModsData.Fluids.steamBlock.getIcon(2, 0));

		if (TSContent.moltenLimestoneFluid != null)
			TSContent.moltenLimestoneFluid.setIcons(ModsData.Fluids.moltenLimestone.getIcon(0, 0), ModsData.Fluids.moltenLimestone.getIcon(2, 0));

		if (TSContent.liquidCementFluid != null)
			TSContent.liquidCementFluid.setIcons(ModsData.Fluids.liquidCement.getIcon(0, 0), ModsData.Fluids.liquidCement.getIcon(2, 0));

		TSClientProxy.readManuals();
	}
}