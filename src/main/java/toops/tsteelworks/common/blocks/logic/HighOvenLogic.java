package toops.tsteelworks.common.blocks.logic;

import cpw.mods.fml.common.Optional;
import mantle.blocks.iface.IActiveLogic;
import mantle.blocks.iface.IFacingLogic;
import mantle.world.CoordTuple;
import net.minecraft.block.BlockSourceImpl;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistryDefaulted;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import nf.fr.ephys.cookiecore.helpers.BlockHelper;
import nf.fr.ephys.cookiecore.helpers.InventoryHelper;
import nf.fr.ephys.cookiecore.helpers.MathHelper;
import nf.fr.ephys.cookiecore.helpers.NBTHelper;
import nf.fr.ephys.cookiecore.util.MultiFluidTank;
import nf.fr.ephys.cookiecore.util.SizeableInventory;
import toops.tsteelworks.api.highoven.IFuelRegistry;
import toops.tsteelworks.api.highoven.IFuelRegistry.IFuelData;
import toops.tsteelworks.api.highoven.IMixAgentRegistry;
import toops.tsteelworks.api.highoven.IMixerRegistry;
import toops.tsteelworks.api.highoven.ISmeltingRegistry;
import toops.tsteelworks.common.core.ConfigCore;
import toops.tsteelworks.common.structure.IStructure;
import toops.tsteelworks.common.structure.StructureHighOven;
import toops.tsteelworks.lib.ModsData;
import toops.tsteelworks.lib.TSRepo;
import toops.tsteelworks.lib.logic.*;
import vazkii.botania.api.item.IExoflameHeatable;

import java.util.Arrays;
import java.util.List;

@Optional.Interface(iface = "vazkii.botania.api.item.IExoflameHeatable", modid = "Botania")
public class HighOvenLogic extends TileEntity implements IActiveLogic, IFacingLogic, IMasterLogic, IRedstonePowered, INamable, IFluidTankHolder, IFluidHandler, IExoflameHeatable {
	/**
	 * The amount of fluid the tank may gain per layer - multiplier.
	 */
	public static final int FLUID_AMOUNT_PER_LAYER = 20000;
	public static final int TEMP_PER_LAYER = 500;
	/**
	 * The max temperature of the smallest High Oven structure.
	 */
	public static final int BASE_MAX_TEMP = 2000;
	/**
	 * Temperature the High Oven defaults to when not cooking.
	 */
	public static final int ROOM_TEMP = 20;
	/**
	 * Temperature decrease rate when the High Oven is not burning fuel.
	 */
	public static final int INTERNAL_COOLDOWN_RATE = 10;
	/**
	 * The dispense behavior.
	 */
	private final IRegistry dispenseBehavior = new RegistryDefaulted(new BehaviorDefaultDispenseItem());
	/**
	 * The molten metal.
	 */
	private MultiFluidTank tank = new MultiFluidTank(FLUID_AMOUNT_PER_LAYER);
	/**
	 * Handles the multiblock structure
	 */
	private StructureHighOven structure = new StructureHighOven(this);
	/**
	 * Used to determine if the controller is being supplied with a redstone
	 * signal.
	 */
	private boolean redstoneActivated = false;
	/**
	 * Used to determine if the controller is melting items.
	 */
	private boolean isMeltingItems;
	/**
	 * Used to determine the controller's facing direction.
	 */
	private byte direction;
	/**
	 * The internal temperature.
	 */
	private int internalTemp;
	/**
	 * The current fuel heat rate (gain).
	 */
	private int fuelHeatRate = 3;
	/**
	 * The max temperature.
	 */
	private int maxTemp = BASE_MAX_TEMP;
	/**
	 * The fuel burn time.
	 */
	private int fuelBurnTime;
	/**
	 * The current burnable item burn time.
	 */
	private int fuelBurnTimeTotal;
	/**
	 * The active temperatures of melting items.
	 */
	private int[] activeTemps = new int[0];
	/**
	 * The melting point temperatures if melting items.
	 */
	private int[] meltingTemps = new int[0];
	private SizeableInventory smeltableInventory = new SizeableInventory(0, 1) {
		@Override
		public void markDirty() {
			HighOvenLogic.this.markDirty();
		}
	};
	/**
	 * The inventory
	 * 4 first slots are for oxidizer, reducer, purifier and fuel
	 * 6 nexts slots (depending on structure size) are for metals
	 * (yes I'm locking this to 6 slots, sorry it feels better that way
	 * as the high oven smelts uber quickly)
	 */
	private SizeableInventory inventory = new SizeableInventory(4) {
		@Override
		public void markDirty() {
			HighOvenLogic.this.markDirty();
		}
	};
	/**
	 * Used to randomize things.
	 */
	private String invName;
	private boolean forceCheck = true;

	/**
	 * Max temp by layer.
	 *
	 * @return the int
	 */
	public int maxTempByLayer() {
		return BASE_MAX_TEMP + (structure.getNbLayers() - 1) * TEMP_PER_LAYER;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public byte getRenderDirection() {
		return this.direction;
	}

	/* ==================== Facing Logic ==================== */

	@Override
	public ForgeDirection getForgeDirection() {
		return ForgeDirection.VALID_DIRECTIONS[this.direction];
	}

	@Override
	public void setDirection(final int side) {
	}

	@Override
	public void setDirection(final float yaw, final float pitch, final EntityLivingBase player) {
		this.direction = (byte) BlockHelper.orientationToMetadataXZ(player.rotationYaw);
	}

	@Override
	public boolean getActive() {
		return this.structure.isValid() && this.isBurning();
	}

	/* ==================== Active Logic ==================== */

	@Override
	public void setActive(final boolean flag) {
	}

	/**
	 * Get the current state of redstone-connected power.
	 *
	 * @return Redstone powered state
	 */
	@Override
	public boolean getRSmode() {
		return this.redstoneActivated;
	}

	/* ==================== IRedstonePowered ==================== */

	/**
	 * Set the redstone powered state.
	 *
	 * @param flag true: powered / false: not powered
	 */
	@Override
	public void setRSmode(final boolean flag) {
		this.redstoneActivated = flag;

		this.setActive(true);
	}

	@Override
	public void updateEntity() {
		final long tick = getWorldObj().getWorldTime();

		if (tick % 4 == 0) {
			this.heatItems();
		}

		// structural checks and fuel gauge updates
		if (tick % 20 == 0) {
			if (!worldObj.isRemote && !structure.isValid() || forceCheck) {
				forceCheck = false;
				this.checkValidPlacement();
			}

			if (this.isBurning()) {
				this.fuelBurnTime--;
				this.internalTemp = Math.min(this.internalTemp + this.fuelHeatRate, maxTemp);
			} else if (structure.isValid()) {
				this.internalTemp = Math.max(this.internalTemp - INTERNAL_COOLDOWN_RATE, ROOM_TEMP);
			} else {
				// If structure is broken, lose half the heat every second
				this.internalTemp = Math.max(this.internalTemp / 2 - INTERNAL_COOLDOWN_RATE, ROOM_TEMP);
			}

			if (structure.isValid() && this.fuelBurnTime <= 0) {
				this.updateFuelGauge();
			}
		}

		if (tick % 40 == 0)
			this.heatFluids();
	}

	/* ==================== Smelting Logic ==================== */

	/**
	 * Process item heating and liquifying.
	 */
	private void heatItems() {
		if (this.internalTemp <= ROOM_TEMP || !structure.isValid())
			return;

		boolean hasSmeltable = false;
		for (int i = 0; i < smeltableInventory.getSizeInventory(); i++) {
			if (smeltableInventory.getStackInSlot(i) == null || this.meltingTemps[i] == ROOM_TEMP)
				continue;

			hasSmeltable = true;

			// Increase temp if its temp is lower than the High Oven's internal temp and hasn't reached melting point
			// Decrease temp if its temp is higher than the High Oven's internal
			//  temp and the High Oven's internal temp is lower than the melting point
			// Liquify metals if the temp has reached the melting point

			if (this.activeTemps[i] < this.internalTemp && this.activeTemps[i] < this.meltingTemps[i]) {
				this.activeTemps[i] += this.internalTemp > 250 ? this.internalTemp / 250 : 1;
			} else if (this.activeTemps[i] > this.internalTemp && this.internalTemp < this.meltingTemps[i]) {
				this.activeTemps[i]--;
			}

			if (this.activeTemps[i] >= this.meltingTemps[i] && !worldObj.isRemote) {
				meltItem(i);
			}
		}

		isMeltingItems = hasSmeltable;
	}

	private boolean meltItem(int slot) {
		final ISmeltingRegistry.IMeltData meltData = ISmeltingRegistry.INSTANCE.getMeltable(this.smeltableInventory.getStackInSlot(slot));

		if (meltData == null) return false;

		FluidStack meltResult = meltData.getResult().copy();
		if (meltData.isOre()) {
			meltResult.amount = (int) (meltResult.amount * ConfigCore.ingotsPerOre);
		}

		final IMixerRegistry.IMixOutput mixResult = IMixerRegistry.INSTANCE.getMix(meltResult.getFluid(),
				inventory.getStackInSlot(SLOT.OXIDIZER.ordinal()),
				inventory.getStackInSlot(SLOT.REDUCER.ordinal()),
				inventory.getStackInSlot(SLOT.PURIFIER.ordinal()));

		if (mixResult == null) {
			if (!this.addFluidToTank(meltResult)) {
				return false;
			}
		} else if (mixResult.getFluidOutput() != null) {
			FluidStack mixedMeltResult = mixResult.getFluidOutput().copy();
			mixedMeltResult.amount = meltResult.amount;

			if (!this.addFluidToTank(mixedMeltResult)) {
				return false;
			}
		}

		if (meltData.isOre()) {
			this.outputTE3Slag();
		}

		if (mixResult != null) {
			if (mixResult.getSolidOutput() != null)
				this.addItem(mixResult.getSolidOutput());

			this.removeMixItems();
		}

		smeltableInventory.decrStackSize(slot, 1);
		activeTemps[slot] = ROOM_TEMP;

		return true;
	}

	/**
	 * Heat fluids. (like steam)
	 */
	private void heatFluids() {
		if (!structure.isValid() || internalTemp < 1300 || ConfigCore.steamProductionRate <= 0) return;

		for (int i = 0; i < tank.getNbFluids(); i++) {
			FluidStack fluid = tank.getFluid(i);

			if (fluid.getFluid() != FluidRegistry.WATER) continue;

			int production = Math.min((ConfigCore.steamProductionRate * 40 * internalTemp) / TEMP_PER_LAYER, fluid.amount);

			tank.drain(ForgeDirection.UNKNOWN, new FluidStack(FluidRegistry.WATER, production), true);
			tank.fill(new FluidStack(ModsData.Fluids.steamFluid, production), true);

			// move steam at the bottom of the tank so we don't get complains \o
			tank.setStackPos(ModsData.Fluids.steamFluid, 0);

			markDirty();

			break;
		}
	}

	/**
	 * Output Thermal Expansion 3 slag if available.
	 */
	private void outputTE3Slag() {
		if (ModsData.ThermalExpansion.isLoaded && ConfigCore.teSlagOutputChance >= 0 && MathHelper.random.nextInt(ConfigCore.teSlagOutputChance) == 0) {
			this.addItem(ModsData.ThermalExpansion.slag.copy());
		}
	}

	/**
	 * Remove additive materials by preset vs random chance and amount.
	 */
	private void removeMixItems() {
		for (int i = SLOT.OXIDIZER.ordinal(); i < SLOT.FUEL.ordinal(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null)
				continue;

			IMixAgentRegistry.IMixAgent mixData = IMixAgentRegistry.INSTANCE.getAgentData(stack);

			if (mixData == null)
				continue;

			if (MathHelper.random.nextInt(100) <= mixData.getConsumeChance()) {
				stack.stackSize--;
				//Dont leave itemstacks of zero size in the slots....
				if (stack.stackSize == 0) {
					inventory.setInventorySlotContents(i, null);
					this.markDirty();
				}
			}
		}
	}

	/**
	 * Get internal temperature for smelting.
	 *
	 * @return internal temperature value
	 */
	public int getInternalTemperature() {
		return this.internalTemp;
	}

	/* ==================== Temperatures ==================== */

	/**
	 * Get current temperature for slot.
	 *
	 * @param slot the slot
	 * @return the temp for slot
	 */
	public int getTempForSlot(final int slot) {
		return this.activeTemps[slot];
	}

	/**
	 * Get melting point for item in slot.
	 *
	 * @param slot the slot
	 * @return the melting point for slot
	 */
	public int getMeltingPointForSlot(final int slot) {
		return this.meltingTemps[slot];
	}

	/**
	 * Update melting temperatures for items.
	 */
	private void updateTemperatures() {
		for (int i = 0; i < smeltableInventory.getSizeInventory(); i++) {
			ItemStack stack = smeltableInventory.getStackInSlot(i);

			if (stack == null) {
				meltingTemps[i] = activeTemps[i] = ROOM_TEMP;
				continue;
			}

			ISmeltingRegistry.IMeltData data = ISmeltingRegistry.INSTANCE.getMeltable(stack);

			if (data == null)
				meltingTemps[i] = activeTemps[i] = ROOM_TEMP;
			else {
				this.meltingTemps[i] = data.getMeltingPoint();
			}
		}
	}

	/**
	 * Checks if controller is burning fuel.
	 *
	 * @return true, burning
	 */
	public boolean isBurning() {
		return this.fuelBurnTime > 0;
	}

	/* ==================== Fuel Handling ==================== */

	public int getFuelBurnTime() {
		return this.fuelBurnTime;
	}

	public void setFuelBurnTime(int fuelBurnTime) {
		this.fuelBurnTime = fuelBurnTime;
	}

	public int getFuelBurnTimeTotal() {
		return this.fuelBurnTimeTotal;
	}

	/**
	 * Update fuel gauge.
	 */
	private void updateFuelGauge() {
		if (this.isBurning() || this.getRSmode())
			return;

		ItemStack fuel = inventory.getStackInSlot(SLOT.FUEL.ordinal());
		if (fuel == null) {
			this.fuelBurnTime = 0;
			return;
		}

		IFuelData fuelData = IFuelRegistry.INSTANCE.getFuel(fuel);

		if (fuelData == null) return;

		this.fuelBurnTime = fuelData.getBurnTime(fuel);
		this.fuelBurnTimeTotal = fuelBurnTime;
		this.fuelHeatRate = fuelData.getHeatRate(fuel);

		fuelData.onStartBurning(fuel);

		if (fuel.stackSize <= 0) {
			inventory.setInventorySlotContents(SLOT.FUEL.ordinal(), null);
		} else {
			markDirty();
		}
	}

	/* ==================== Inventory ==================== */
	@Override
	public void markDirty() {
		super.markDirty();

		this.updateTemperatures();

		worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

	public boolean isUseableByPlayer(final EntityPlayer entityplayer) {
		return worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && entityplayer.getDistance(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64D;
	}

	public int getSizeInventory() {
		return smeltableInventory.getSizeInventory() + inventory.getSizeInventory();
	}

	@Override
	public void notifyChange(final IServantLogic servant, final int x, final int y, final int z) {
		forceCheck = true;
	}

	/* ==================== Multiblock ==================== */

	/**
	 * Check placement validation by facing direction.
	 */
	@Override
	public void checkValidPlacement() {
		int[] centerBlock = BlockHelper.getAdjacentBlock(xCoord, yCoord, zCoord, BlockHelper.getOppositeSide(getRenderDirection()));

		structure.validateStructure(centerBlock[0], centerBlock[1], centerBlock[2]);
	}

	/**
	 * Add molen metal fluidstack.
	 *
	 * @param fluidToAdd the liquid
	 * @return Success
	 */
	public boolean addFluidToTank(final FluidStack fluidToAdd) {
		if (tank.getNbFluids() != 0 && !canFluidsBeTogether(fluidToAdd, tank.getFluid(0)))
			return false;

		if (tank.fill(fluidToAdd, false) != fluidToAdd.amount)
			return false;

		tank.fill(fluidToAdd, true);

		markDirty();

		return true;
	}

	/* ==================== Fluid Handling ==================== */

	private boolean canFluidsBeTogether(FluidStack f1, FluidStack f2) {
		return f1.isFluidEqual(f2) ||
				f1.getFluid().equals(ModsData.Fluids.steamFluid) && f2.getFluid().equals(FluidRegistry.WATER) ||
				f2.getFluid().equals(ModsData.Fluids.steamFluid) && f1.getFluid().equals(FluidRegistry.WATER);
	}

	/**
	 * Adds the item.
	 *
	 * @param itemstack the ItemStack
	 */
	public void addItem(final ItemStack itemstack) {
		List<HighOvenDuctLogic> ducts = structure.getOutputDucts();

		for (HighOvenDuctLogic duct : ducts) {
			if (InventoryHelper.insertItem(duct, itemstack))
				return;
		}

		dispenseItem(itemstack);
	}

	/**
	 * Dispense item.
	 *
	 * @param itemstack the stack
	 */
	private void dispenseItem(final ItemStack itemstack) {
		final BlockSourceImpl blocksourceimpl = new BlockSourceImpl(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		final IBehaviorDispenseItem ibehaviordispenseitem = (IBehaviorDispenseItem) this.dispenseBehavior.getObject(itemstack.getItem());

		if (ibehaviordispenseitem != IBehaviorDispenseItem.itemDispenseBehaviorProvider) {
			ibehaviordispenseitem.dispense(blocksourceimpl, itemstack);
		}
	}

	/* ==================== NBT ==================== */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		if (nbt.hasKey("smeltableInventory"))
			smeltableInventory.readFromNBT(nbt.getCompoundTag("smeltableInventory"));

		if (nbt.hasKey("inventory")) {
			inventory.readFromNBT(nbt.getCompoundTag("inventory"));
		}

		tank.readFromNBT(nbt.getCompoundTag("tank"));

		internalTemp = nbt.getInteger(TSRepo.NBTNames.INTERNAL_TEMP);
		isMeltingItems = nbt.getBoolean(TSRepo.NBTNames.IN_USE);

		direction = nbt.getByte(TSRepo.NBTNames.DIRECTION);
		fuelBurnTime = nbt.getInteger(TSRepo.NBTNames.USE_TIME);
		fuelHeatRate = nbt.getInteger(TSRepo.NBTNames.FUEL_HEAT_RATE);
		fuelBurnTimeTotal = nbt.getInteger(TSRepo.NBTNames.USE_TIME_TOTAL);

		meltingTemps = nbt.getIntArray(TSRepo.NBTNames.MELTING_TEMPS);
		activeTemps = nbt.getIntArray(TSRepo.NBTNames.ACTIVE_TEMPS);

		redstoneActivated = nbt.getBoolean(TSRepo.NBTNames.REDSTONE);
	}

	@Override
	public void writeToNBT(final NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		NBTHelper.setWritable(nbt, "smeltableInventory", smeltableInventory);
		NBTHelper.setWritable(nbt, "inventory", inventory);

		NBTHelper.setWritable(nbt, "tank", tank);

		nbt.setInteger(TSRepo.NBTNames.INTERNAL_TEMP, internalTemp);
		nbt.setBoolean(TSRepo.NBTNames.IN_USE, isMeltingItems);

		nbt.setByte(TSRepo.NBTNames.DIRECTION, this.direction);
		nbt.setInteger(TSRepo.NBTNames.USE_TIME, this.fuelBurnTime);
		nbt.setInteger(TSRepo.NBTNames.FUEL_HEAT_RATE, this.fuelHeatRate);
		nbt.setInteger(TSRepo.NBTNames.USE_TIME_TOTAL, this.fuelBurnTimeTotal);

		nbt.setIntArray(TSRepo.NBTNames.MELTING_TEMPS, this.meltingTemps);
		nbt.setIntArray(TSRepo.NBTNames.ACTIVE_TEMPS, this.activeTemps);

		nbt.setBoolean(TSRepo.NBTNames.REDSTONE, redstoneActivated);
	}

	@Override
	public final Packet getDescriptionPacket() {
		final NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);

		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tag);
	}

	@Override
	public final void onDataPacket(final NetworkManager net, final S35PacketUpdateTileEntity packet) {
		this.readFromNBT(packet.func_148857_g());

		this.markDirty();
	}

	/* =============== IMaster =============== */
	@Override
	public CoordTuple getCoord() {
		return new CoordTuple(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public boolean isValid() {
		return !tileEntityInvalid && structure.isValid();
	}

	@Override
	public void onStructureChange(IStructure structure) {
		if (!structure.isValid()) {
			// cut temperature in half to discourage frequenet changes
			internalTemp = Math.max(internalTemp / 2, ROOM_TEMP);
		} else {
			final int oldNbLayers = activeTemps.length;
			final int nbLayers = structure.getNbLayers();

			this.tank.setCapacity(FLUID_AMOUNT_PER_LAYER * nbLayers);
			this.maxTemp = this.maxTempByLayer();

			int inventorySize = Math.min(nbLayers, 6);
			this.smeltableInventory.setInventorySize(inventorySize);

			if (nbLayers > oldNbLayers) {
				activeTemps = Arrays.copyOf(activeTemps, nbLayers);
				meltingTemps = Arrays.copyOf(meltingTemps, nbLayers);

				for (int i = oldNbLayers; i < inventorySize; i++) {
					this.activeTemps[i] = ROOM_TEMP;
					this.meltingTemps[i] = ROOM_TEMP;
				}
			}

			int[] dumpCoords = BlockHelper.getAdjacentBlock(xCoord, yCoord, zCoord, direction);
			this.smeltableInventory.dumpOverflow(worldObj, dumpCoords[0], dumpCoords[1], dumpCoords[2]);
		}

		markDirty();
	}

	@Override
	public String getCustomName() {
		return invName;
	}

	@Override
	public void setCustomName(String name) {
		this.invName = name;
	}

	public IStructure getStructure() {
		return structure;
	}

	public IInventory getSmeltableInventory() {
		return smeltableInventory;
	}

	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public MultiFluidTank getFluidTank() {
		return tank;
	}

	public void addOutputDuct(HighOvenDuctLogic duct) {
		structure.addOutputDuct(duct);
	}

	public void removeOutputDuct(HighOvenDuctLogic duct) {
		structure.removeOutputDuct(duct);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (tank.getNbFluids() != 0 && !canFluidsBeTogether(resource, tank.getFluid(0)))
			return 0;

		int filled = tank.fill(from, resource, doFill);

		if (doFill && filled != 0)
			markDirty();

		return filled;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		FluidStack drained = tank.drain(from, resource, doDrain);

		if (doDrain && drained != null)
			markDirty();

		return drained;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		FluidStack drained = tank.drain(from, maxDrain, doDrain);

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
		return tank.getTankInfo(from);
	}

	@Override
	public boolean canSmelt() {
		// if there is physical fuel, make it burn faster.
		// otherwise, make it burn like charcoal (with a lower heat rate).
		return ConfigCore.botaniaExoflame && getInventory().getStackInSlot(SLOT.FUEL.ordinal()) == null;
	}

	@Override
	public int getBurnTime() {
		return fuelBurnTime;
	}

	@Override
	public void boostBurnTime() {
		fuelBurnTime = 17;
		fuelBurnTimeTotal = 17;
		fuelHeatRate = 2;
	}

	@Override
	public void boostCookTime() {
		if (getInventory().getStackInSlot(SLOT.FUEL.ordinal()) == null) return;

		if (getWorldObj().getWorldTime() % 20 != 0) return;

		fuelHeatRate += fuelHeatRate;
	}

	public enum SLOT {OXIDIZER, REDUCER, PURIFIER, FUEL, FIRST_MELTABLE}
}