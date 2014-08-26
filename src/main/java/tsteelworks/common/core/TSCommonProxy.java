package tsteelworks.common.core;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;
import tsteelworks.TSteelworks;
import tsteelworks.common.network.PacketMoveFluidHandler;
import tsteelworks.common.network.PacketSetDuctModeHandler;
import tsteelworks.common.worldgen.TSBaseWorldGenerator;
import tsteelworks.lib.crafting.AlloyInfo;

public class TSCommonProxy {
	public void preInit() {
		TSContent.preInit();

		MinecraftForge.EVENT_BUS.register(new TSEventHandler());

		GameRegistry.registerWorldGenerator(new TSBaseWorldGenerator(), 8);

		NetworkRegistry.INSTANCE.registerGuiHandler(TSteelworks.instance, new GuiHandler());
	}

	public void init() {
		PacketMoveFluidHandler.register(0);
		PacketSetDuctModeHandler.register(1);
	}

	public void postInit() {
		TSContent.postInit();

		TSRecipes.setupRecipes();

		AlloyInfo.generateDealloyList();
	}
}
