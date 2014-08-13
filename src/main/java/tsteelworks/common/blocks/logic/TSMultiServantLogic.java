package tsteelworks.common.blocks.logic;

import mantle.blocks.iface.IFacingLogic;
import mantle.world.CoordTuple;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import nf.fr.ephys.cookiecore.common.tileentity.IChunkNotify;
import nf.fr.ephys.cookiecore.helpers.BlockHelper;
import tsteelworks.lib.IMasterLogic;
import tsteelworks.lib.IServantLogic;

public class TSMultiServantLogic extends TileEntity implements IServantLogic, IChunkNotify, IFacingLogic {
	private IMasterLogic master;
	/** Used to get the master on chunk load */
	private CoordTuple masterPos;

	private byte direction;

	@Override
	public boolean canUpdate() {
		return false;
	}

	public boolean hasMaster() {
		return master != null && master.isValid();
	}

	public IMasterLogic getMaster() {
		return master;
	}

	@Override
	public void notifyMasterOfChange() {
		if (hasMaster()) {
			master.notifyChange(this, xCoord, yCoord, zCoord);
		}
	}

	@Override
	public boolean setPotentialMaster(IMasterLogic logic, World world) {
		if (hasMaster())
			return false;

		master = logic;

		return true;
	}

	@Override
	public boolean verifyMaster(IMasterLogic logic, World world) {
		return master == logic;
	}

	@Override
	public void readFromNBT(NBTTagCompound tags) {
		super.readFromNBT(tags);
		readCustomNBT(tags);
	}

	@Override
	public void writeToNBT(NBTTagCompound tags) {
		super.writeToNBT(tags);
		writeCustomNBT(tags);
	}

	public void readCustomNBT(NBTTagCompound tags) {
		if (tags.getBoolean("TiedToMaster")) {
			int xCenter = tags.getInteger("xCenter");
			int yCenter = tags.getInteger("yCenter");
			int zCenter = tags.getInteger("zCenter");

			masterPos = new CoordTuple(xCenter, yCenter, zCenter);
		}

		direction = tags.getByte("direction");
	}

	public void writeCustomNBT(NBTTagCompound tags) {
		tags.setBoolean("TiedToMaster", hasMaster());

		if (hasMaster()) {
			CoordTuple coords = master.getCoord();

			tags.setInteger("xCenter", coords.x);
			tags.setInteger("yCenter", coords.y);
			tags.setInteger("zCenter", coords.z);
		}

		tags.setByte("direction", direction);
	}

	@Override
	public void onChunkLoaded() {
		if (masterPos != null) {
			master = (IMasterLogic) worldObj.getTileEntity(masterPos.x, masterPos.y, masterPos.z);
			masterPos = null;
		}
	}

	/* Packets */
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		readCustomNBT(packet.func_148857_g());

		getWorldObj().markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
	}

	@Override
	public Packet getDescriptionPacket() {
		final NBTTagCompound tag = new NBTTagCompound();
		writeCustomNBT(tag);

		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}

	@Override
	public byte getRenderDirection() {
		return direction;
	}

	@Override
	public ForgeDirection getForgeDirection() {
		return ForgeDirection.getOrientation(direction);
	}

	@Override
	public void setDirection(int i) {
		direction = (byte) i;
	}

	@Override
	public void setDirection(float v, float v1, EntityLivingBase entityLivingBase) {
		direction = (byte) BlockHelper.orientationToMetadataXZ(entityLivingBase.rotationYaw);
	}
}
