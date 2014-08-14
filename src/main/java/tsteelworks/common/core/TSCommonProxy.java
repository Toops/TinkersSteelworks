package tsteelworks.common.core;

import cpw.mods.fml.relauncher.Side;
import tsteelworks.TSteelworks;
import tsteelworks.common.network.PacketMoveFluidHandler;
import tsteelworks.common.network.PacketSetDuctModeHandler;

public class TSCommonProxy {
	public void preInit() {}

	public void init() {
		registerPacket();
	}

	public void postInit() {}

	public void registerPacket() {
		TSteelworks.getNetHandler().registerMessage(PacketMoveFluidHandler.class, PacketMoveFluidHandler.PacketMoveFluid.class, PacketMoveFluidHandler.DISCRIMINER, Side.CLIENT);
		TSteelworks.getNetHandler().registerMessage(PacketSetDuctModeHandler.class, PacketSetDuctModeHandler.PacketSetDuctMode.class, PacketSetDuctModeHandler.DISCRIMINER, Side.CLIENT);
	}
}
