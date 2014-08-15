package tsteelworks.common.blocks.logic;

import mantle.blocks.iface.IFacingLogic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import nf.fr.ephys.cookiecore.helpers.BlockHelper;
import tsteelworks.lib.IMasterLogic;
import tsteelworks.lib.IServantLogic;

public class TSMultiServantLogic extends TileEntity implements IServantLogic, IFacingLogic {
	private IMasterLogic master;
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
		direction = tags.getByte("direction");
	}

	public void writeCustomNBT(NBTTagCompound tags) {
		tags.setByte("direction", direction);
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

	public static TSMultiServantLogic newInstance(World world, int x, int y, int z) {
		TSMultiServantLogic tile = new TSMultiServantLogic();

		tile.worldObj = world;
		tile.xCoord = x;
		tile.yCoord = y;
		tile.zCoord = z;

		tile.validate();

		world.setTileEntity(x, y, z, tile);

		return tile;
	}
}
