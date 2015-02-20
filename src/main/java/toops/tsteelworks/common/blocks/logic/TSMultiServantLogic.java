package toops.tsteelworks.common.blocks.logic;

import mantle.world.CoordTuple;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import toops.tsteelworks.lib.logic.IServantLogic;
import toops.tsteelworks.lib.logic.IMasterLogic;

public class TSMultiServantLogic extends TileEntity implements IServantLogic {
	private IMasterLogic master;

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
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

		return true;
	}

	@Override
	public boolean verifyMaster(IMasterLogic logic, World world) {
		return master == logic;
	}

	@Override
	public void writeToNBT(NBTTagCompound p_145841_1_) {
		super.writeToNBT(p_145841_1_);

		writeCustomNBT(p_145841_1_);
	}

	@Override
	public void readFromNBT(NBTTagCompound p_145839_1_) {
		super.readFromNBT(p_145839_1_);

		readCustomNBT(p_145839_1_);
	}

	protected void writeCustomNBT(NBTTagCompound tags) {}
	protected void readCustomNBT(NBTTagCompound tags) {}

	/* Packets */
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		final NBTTagCompound tags = packet.func_148857_g();
		readCustomNBT(tags);

		if (tags.hasKey("master")) {
			int[] coords = tags.getIntArray("master");
			TileEntity te = worldObj.getTileEntity(coords[0], coords[1], coords[2]);

			if (te instanceof IMasterLogic)
				master = (IMasterLogic) te;
		}

		getWorldObj().markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
	}

	@Override
	public Packet getDescriptionPacket() {
		final NBTTagCompound tags = new NBTTagCompound();
		writeCustomNBT(tags);

		if (master != null) {
			CoordTuple coords = master.getCoord();
			tags.setIntArray("master", new int[] { coords.x, coords.y, coords.z });
		}

		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tags);
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
