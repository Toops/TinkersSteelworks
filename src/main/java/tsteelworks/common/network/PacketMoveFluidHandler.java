package tsteelworks.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import nf.fr.ephys.cookiecore.util.MultiFluidTank;
import tsteelworks.TSteelworks;
import tsteelworks.lib.IFluidTankHolder;

public class PacketMoveFluidHandler implements IMessageHandler<PacketMoveFluidHandler.PacketMoveFluid, IMessage> {
	public static void register(int discriminer) {
		TSteelworks.getNetHandler().registerMessage(PacketMoveFluidHandler.class, PacketMoveFluidHandler.PacketMoveFluid.class, discriminer, Side.SERVER);
	}

	public static void moveFluidGUI(TileEntity tank, FluidStack fluid) {
		PacketMoveFluid packet = new PacketMoveFluid(
				tank.xCoord,
				(short) tank.yCoord,
				tank.zCoord,
				tank.getWorldObj().provider.dimensionId,
				fluid.fluidID,
				GuiScreen.isShiftKeyDown());

		TSteelworks.getNetHandler().sendToServer(packet);
	}

	@Override
	public IMessage onMessage(PacketMoveFluid packet, MessageContext messageContext) {
		World world = MinecraftServer.getServer().worldServerForDimension(packet.worldId);

		TileEntity te = world.getTileEntity(packet.x, packet.y, packet.z);

		if (te instanceof IFluidTankHolder) {
			MultiFluidTank tank = ((IFluidTankHolder) te).getFluidTank();
			int pos = packet.isShiftKeyDown ? tank.getNbFluids() : 0;

			tank.setStackPos(FluidRegistry.getFluid(packet.fluidID), pos);

			te.markDirty();
		}

		return null;
	}

	public static class PacketMoveFluid implements IMessage {
		private int x;
		private short y;
		private int z;
		private int worldId;
		private int fluidID;
		private boolean isShiftKeyDown;

		public PacketMoveFluid() {}

		public PacketMoveFluid(int x, short y, int z, int worldId, int fluidID, boolean isShiftKeyDown) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.worldId = worldId;
			this.fluidID = fluidID;
			this.isShiftKeyDown = isShiftKeyDown;
		}

		@Override
		public void fromBytes(ByteBuf buffer) {
			x = buffer.readInt();
			y = buffer.readShort();
			z = buffer.readInt();

			worldId = buffer.readInt();
			fluidID = buffer.readInt();
			isShiftKeyDown = buffer.readBoolean();
		}

		@Override
		public void toBytes(ByteBuf buffer) {
			buffer.writeInt(x)
				.writeShort(y)
				.writeInt(z)

				.writeInt(worldId)
				.writeInt(fluidID)
				.writeBoolean(isShiftKeyDown);
		}
	}
}
