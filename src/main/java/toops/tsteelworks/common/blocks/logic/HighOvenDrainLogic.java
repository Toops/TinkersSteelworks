package toops.tsteelworks.common.blocks.logic;

import mantle.blocks.iface.IFacingLogic;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import toops.tsteelworks.lib.logic.IMasterLogic;

public class HighOvenDrainLogic extends TSMultiServantLogic implements IFluidHandler, IFacingLogic {
	private byte direction;

	// ========== HighOvenDrainLogic ===========

	public IFluidHandler getTank() {
		IMasterLogic master = getMaster();

		if (master instanceof IFluidHandler)
			return (IFluidHandler) master;

		return null;
	}

	// ========== IFacingLogic ===========

	@Override
	public byte getRenderDirection() {
		return direction;
	}

	@Override
	public ForgeDirection getForgeDirection() {
		return ForgeDirection.VALID_DIRECTIONS[direction];
	}

	@Override
	public void setDirection(int side) {
	}

	@Override
	public void setDirection(float yaw, float pitch, EntityLivingBase player) {
		direction = (byte) BlockPistonBase.determineOrientation(worldObj, xCoord, yCoord, zCoord, player);
	}

	// ========== IFluidHandler ===========

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return from.ordinal() == direction && getTank() != null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (from.ordinal() != direction) return null;

		IFluidHandler tank = getTank();

		return tank == null ? null : tank.drain(from, resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (from.ordinal() != direction) return null;

		IFluidHandler tank = getTank();

		return tank == null ? null : tank.drain(from, maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return from.ordinal() == direction && getTank() != null;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (from.ordinal() != direction) return 0;

		IFluidHandler tank = getTank();

		return tank == null ? 0 : tank.fill(from, resource, doFill);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		if (from.ordinal() != direction) return null;

		IFluidHandler tank = getTank();

		return tank == null ? new FluidTankInfo[0] : tank.getTankInfo(from);
	}

	// ========== NBT ===========

	@Override
	public void writeCustomNBT(NBTTagCompound tags) {
		super.writeCustomNBT(tags);

		tags.setByte("Direction", direction);
	}

	@Override
	public void readCustomNBT(NBTTagCompound tags) {
		super.readCustomNBT(tags);

		direction = tags.getByte("Direction");
	}
}
