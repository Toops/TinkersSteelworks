package tsteelworks.common.blocks.logic;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import nf.fr.ephys.cookiecore.helpers.BlockHelper;
import nf.fr.ephys.cookiecore.util.MultiFluidTank;
import tsteelworks.common.core.TSContent;
import tsteelworks.lib.crafting.AlloyInfo;

public class SteamTurbineLogic extends TileEntity implements IFluidHandler {
	public static final int MAX_STEAM = 10000;
	public static final int MAX_INPUT = 1000;

	private FluidStack steam = new FluidStack(TSContent.steamFluid, 0);
	private FluidStack input;
	private MultiFluidTank output = new MultiFluidTank(100000);

	@Override
	public void updateEntity() {
		if (worldObj == null || worldObj.isRemote) return;

		if (worldObj.getTotalWorldTime() % 10 == 0) {
			processInput();
		}
	}

	private void processInput() {
		if (input == null) return;

		if (steam.amount < 100) return;
		steam.amount -= 100;

		int toProcess = Math.min(input.amount, 100);

		FluidStack[] stacks = AlloyInfo.deAlloy(new FluidStack(input, toProcess));

		int requiredSpace = 0;
		for (FluidStack stack : stacks) {
			requiredSpace += stack.amount;
		}

		if (output.getCapacity() - output.getFluidAmount() >= requiredSpace) {
			for (FluidStack stack : stacks) {
				output.fill(stack, true);
			}

			input.amount -= toProcess;

			if (input.amount <= 0)
				input = null;

			markDirty();
		}
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (from != ForgeDirection.DOWN && from.ordinal() != BlockHelper.getOppositeSide(getBlockMetadata()))
			return 0;

		if (resource.getFluid().equals(TSContent.steamFluid)) {
			int fill = Math.min(MAX_STEAM - steam.amount, resource.amount);

			if (doFill && fill > 0) {
				steam.amount += fill;
				markDirty();
			}

			return fill;
		}

		if (AlloyInfo.isAlloy(resource)) {
			int fill = Math.min(input == null ? MAX_INPUT : input.equals(resource) ? MAX_INPUT - input.amount : 0, resource.amount);

			if (doFill && fill > 0) {
				if (input == null)
					input = new FluidStack(resource, 0);

				input.amount += fill;

				markDirty();
			}

			return fill;
		}

		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (from.ordinal() != getBlockMetadata()) return null;

		FluidStack drained = output.drain(from, resource, doDrain);

		if (drained != null)
			markDirty();

		return drained;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (from.ordinal() != getBlockMetadata()) return null;

		FluidStack drained = output.drain(from, maxDrain, doDrain);

		if (drained != null)
			markDirty();

		return drained;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return from == ForgeDirection.DOWN || from.ordinal() == BlockHelper.getOppositeSide(getBlockMetadata());
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return from.ordinal() == getBlockMetadata();
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		if (from.ordinal() == getBlockMetadata())
			return output.getTankInfo(from);

		if (from == ForgeDirection.DOWN || from.ordinal() == BlockHelper.getOppositeSide(getBlockMetadata()))
			return new FluidTankInfo[] { new FluidTankInfo(steam, MAX_STEAM), new FluidTankInfo(input, MAX_INPUT) };

		return null;
	}

	@Override
	public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z) {
		return oldBlock != newBlock;
	}
}
