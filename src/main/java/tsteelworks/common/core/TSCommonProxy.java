package tsteelworks.common.core;

import cpw.mods.fml.relauncher.Side;
import tsteelworks.TSteelworks;
import tsteelworks.common.network.PacketMoveFluidHandler;
import tsteelworks.common.network.PacketSetDuctModeHandler;
import tsteelworks.lib.crafting.AlloyInfo;

public class TSCommonProxy {
	public void preInit() {}

	public void init() {
		registerPacket();
	}

	public void postInit() {
		AlloyInfo.generateDealloyList();
	}

	public void registerPacket() {
		TSteelworks.getNetHandler().registerMessage(PacketMoveFluidHandler.class, PacketMoveFluidHandler.PacketMoveFluid.class, PacketMoveFluidHandler.DISCRIMINER, Side.SERVER);
		TSteelworks.getNetHandler().registerMessage(PacketSetDuctModeHandler.class, PacketSetDuctModeHandler.PacketSetDuctMode.class, PacketSetDuctModeHandler.DISCRIMINER, Side.SERVER);
	}
}
