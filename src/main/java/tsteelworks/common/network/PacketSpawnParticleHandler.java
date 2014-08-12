package tsteelworks.common.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import nf.fr.ephys.cookiecore.helpers.ParticleHelper;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

public class PacketSpawnParticleHandler implements IMessageHandler<PacketSpawnParticleHandler.PacketSpawnParticle, IMessage> {
	public static void sendPacketSpawnParticle(int particleID, double x, double y, double z, double velX, double velY, double velZ, int dimention) {
		PacketSpawnParticle packet = new PacketSpawnParticle(particleID, x, y, z, velX, velY, velZ);

		PlayerProxies.getNetHandler().sendToAllAround(packet, new NetworkRegistry.TargetPoint(dimention, x, y, z, 32));
	}

	@Override
	public IMessage onMessage(PacketSpawnParticle packet, MessageContext messageContext) {
		Minecraft.getMinecraft().theWorld.spawnParticle(
				ParticleHelper.getParticleNameFromID(packet.particleID),
				packet.x,
				packet.y,
				packet.z,
				packet.velX,
				packet.velY,
				packet.velZ);

		return null;
	}

	public static class PacketSpawnParticle implements IMessage {
		private int particleID;
		private double x;
		private double y;
		private double z;
		private double velX;
		private double velY;
		private double velZ;

		public PacketSpawnParticle() {}

		public PacketSpawnParticle(int particleID, double x, double y, double z, double velX, double velY, double velZ) {
			this.particleID = particleID;
			this.x = x;
			this.y = y;
			this.z = z;
			this.velX = velX;
			this.velY = velY;
			this.velZ = velZ;
		}

		@Override
		public void fromBytes(ByteBuf byteBuf) {
			this.particleID = byteBuf.readInt();

			this.x = byteBuf.readDouble();
			this.y = byteBuf.readDouble();
			this.z = byteBuf.readDouble();

			this.velX = byteBuf.readDouble();
			this.velY = byteBuf.readDouble();
			this.velZ = byteBuf.readDouble();
		}

		@Override
		public void toBytes(ByteBuf byteBuf) {
			byteBuf.writeInt(particleID);

			byteBuf.writeDouble(x);
			byteBuf.writeDouble(y);
			byteBuf.writeDouble(z);

			byteBuf.writeDouble(velX);
			byteBuf.writeDouble(velY);
			byteBuf.writeDouble(velZ);
		}
	}
}
