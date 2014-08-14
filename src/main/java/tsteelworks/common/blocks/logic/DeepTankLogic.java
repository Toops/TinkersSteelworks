package tsteelworks.common.blocks.logic;

import mantle.blocks.iface.IFacingLogic;
import mantle.world.CoordTuple;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import nf.fr.ephys.cookiecore.common.tileentity.IChunkNotify;
import nf.fr.ephys.cookiecore.helpers.BlockHelper;
import nf.fr.ephys.cookiecore.helpers.NBTHelper;
import nf.fr.ephys.cookiecore.util.MultiFluidTank;
import tsteelworks.common.core.TSRepo;
import tsteelworks.common.structure.IStructure;
import tsteelworks.common.structure.StructureDeepTank;
import tsteelworks.lib.IFluidTankHolder;
import tsteelworks.lib.IMasterLogic;
import tsteelworks.lib.IServantLogic;

// todo: make the dealloyer a part of the deep tank: any fluid piped in will be dealloyed
public class DeepTankLogic extends TileEntity implements IFluidHandler, IFacingLogic, IFluidTank, IMasterLogic, IChunkNotify, IFluidTankHolder {
	/**
	 * The FluidStack listing.
	 */
	private MultiFluidTank fluidTank = new MultiFluidTank(0);

	/**
	 * Deep tank multiblock structure handling
	 */
	private StructureDeepTank structure = new StructureDeepTank(this);

	/**
	 * The direction.
	 */
	private byte direction;

	/**
	 * The tick.
	 */
	private int tick;

	public int calcFluidCapacity(int totalSpace) {
		return structure.getGlassCapacity() * totalSpace;
	}

	public void onStructureChange(IStructure str) {
		StructureDeepTank structure = (StructureDeepTank) str;

		this.fluidTank.setCapacity(calcFluidCapacity(structure.getNbLayers() * (structure.getXWidth() - 1) * (structure.getZWidth() - 1)));

		this.markDirty();
	}

	@Override
	public byte getRenderDirection() {
		return this.direction;
	}

	@Override
	public ForgeDirection getForgeDirection() {
		return ForgeDirection.VALID_DIRECTIONS[this.direction];
	}

	@Override
	public void setDirection(final int side) {}

	@Override
	public void setDirection(final float yaw, final float pitch, final EntityLivingBase player) {
		direction = (byte) BlockHelper.orientationToMetadataXZ(yaw);
	}

	@Override
	public void updateEntity() {
		if (++this.tick == 20) {
			if (!structure.isValid())
				this.checkValidPlacement();

			this.tick = 0;
		}
	}

	@Override
	public void markDirty() {
		super.markDirty();

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		worldObj.markBlockRangeForRenderUpdate(xCoord - 1, yCoord - 1, zCoord - 1, xCoord + 1, yCoord + 1, zCoord + 1);
	}

    /* Multiblock */

	@Override
	public void notifyChange(final IServantLogic servant, final int x, final int y, final int z) {
		this.checkValidPlacement();
	}

	@Override
	public void checkValidPlacement() {
		structure.validateStructure(xCoord, yCoord, zCoord);
	}

	@Override
	public int getCapacity() {
		return fluidTank.getCapacity();
	}

	@Override
	public MultiFluidTank getFluidTank() {
		return fluidTank;
	}

	@Override
	public FluidStack drain(final int maxDrain, final boolean doDrain) {
		FluidStack stack = fluidTank.drain(maxDrain, doDrain);

		if (doDrain && stack != null)
			markDirty();

		return stack;
	}

	@Override
	public int fill(final FluidStack resource, final boolean doFill) {
		int amount = fluidTank.fill(resource, doFill);

		if (doFill && amount != 0)
			markDirty();

		return amount;
	}

	@Override
	public FluidStack getFluid() {
		return fluidTank.getFluid();
	}

	@Override
	public int getFluidAmount() {
		return fluidTank.getFluidAmount();
	}

	@Override
	public FluidTankInfo getInfo() {
		return fluidTank.getInfo();
	}

	@Override
	public void readFromNBT(final NBTTagCompound tags) {
		super.readFromNBT(tags);

		this.fluidTank.readFromNBT(tags.getCompoundTag("Tank"));

		this.direction = tags.getByte(TSRepo.NBTNames.DIRECTION);
	}

	@Override
	public void writeToNBT(final NBTTagCompound tags) {
		super.writeToNBT(tags);

		tags.setByte(TSRepo.NBTNames.DIRECTION, this.direction);

		NBTHelper.setWritable(tags, "Tank", fluidTank);
	}

    /* Packets */

	@Override
	public Packet getDescriptionPacket() {
		final NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);

		structure.writeToNBT(tag);

		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}

	@Override
	public void onDataPacket(final NetworkManager net, final S35PacketUpdateTileEntity packet) {
		this.readFromNBT(packet.func_148857_g());

		structure.readFromNBT(packet.func_148857_g());

		worldObj.markBlockRangeForRenderUpdate(xCoord - 1, yCoord - 1, zCoord - 1, xCoord + 1, yCoord + 1, zCoord + 1);
	}

    /* =============== IMaster =============== */

	@Override
	public CoordTuple getCoord() {
		return new CoordTuple(xCoord, yCoord, zCoord);
	}

	@Override
	public boolean isValid() {
		return !tileEntityInvalid && structure.isValid();
	}

	@Override
	public void onChunkLoaded() {
		checkValidPlacement();
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		int filled = fluidTank.fill(from, resource, doFill);

		if (doFill && filled != 0)
			markDirty();

		return filled;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		FluidStack drained = fluidTank.drain(from, resource, doDrain);

		if (doDrain && drained != null)
			markDirty();

		return drained;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		FluidStack drained = fluidTank.drain(from, maxDrain, doDrain);

		if (doDrain && drained != null)
			markDirty();

		return drained;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return fluidTank.getTankInfo(from);
	}

	public StructureDeepTank getStructure() {
		return structure;
	}
}
