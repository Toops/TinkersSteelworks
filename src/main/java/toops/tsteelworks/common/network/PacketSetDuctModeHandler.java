package toops.tsteelworks.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import toops.tsteelworks.TSteelworks;
import toops.tsteelworks.common.blocks.logic.HighOvenDuctLogic;

public class PacketSetDuctModeHandler implements IMessageHandler<PacketSetDuctModeHandler.PacketSetDuctMode, IMessage> {
	public static void register(int discriminer) {
		TSteelworks.getNetHandler().registerMessage(PacketSetDuctModeHandler.class, PacketSetDuctModeHandler.PacketSetDuctMode.class, discriminer, Side.SERVER);
	}

	public static void changeDuctMode(HighOvenDuctLogic duct, byte mode) {
		PacketSetDuctMode packet = new PacketSetDuctMode(
				duct.xCoord,
				(short) duct.yCoord,
				duct.zCoord,
				duct.getWorldObj().provider.dimensionId,
				mode);

		TSteelworks.getNetHandler().sendToServer(packet);
	}

	@Override
	public IMessage onMessage(PacketSetDuctMode packet, MessageContext messageContext) {
		World world = MinecraftServer.getServer().worldServerForDimension(packet.dim);

		TileEntity te = world.getTileEntity(packet.x, packet.y, packet.z);

		if (te instanceof HighOvenDuctLogic) {
			((HighOvenDuctLogic) te).setMode(packet.mode);
		}

		return null;
	}

	public static class PacketSetDuctMode implements IMessage {
		private int x;
		private short y;
		private int z;
		private int dim;
		private byte mode;

		public PacketSetDuctMode() {
		}

		public PacketSetDuctMode(int x, short y, int z, int dim, byte mode) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.dim = dim;
			this.mode = mode;
		}

		@Override
		public void fromBytes(ByteBuf buffer) {
			x = buffer.readInt();
			y = buffer.readShort();
			z = buffer.readInt();

			dim = buffer.readInt();
			mode = buffer.readByte();
		}

		@Override
		public void toBytes(ByteBuf buffer) {
			buffer.writeInt(x)
					.writeShort(y)
					.writeInt(z)

					.writeInt(dim)
					.writeByte(mode);
		}
	}
}