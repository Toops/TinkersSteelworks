package tsteelworks.blocks.logic;

import cpw.mods.fml.common.registry.GameRegistry;
import mantle.world.CoordTuple;
import net.minecraft.block.BlockSourceImpl;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
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
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import nf.fr.ephys.cookiecore.common.tileentity.IChunkNotify;
import nf.fr.ephys.cookiecore.helpers.BlockHelper;
import nf.fr.ephys.cookiecore.helpers.MathHelper;
import nf.fr.ephys.cookiecore.helpers.NBTHelper;
import nf.fr.ephys.cookiecore.util.MultiFluidTank;
import nf.fr.ephys.cookiecore.util.SizeableInventory;
import tconstruct.library.crafting.FluidType;
import tsteelworks.TSteelworks;
import tsteelworks.common.TSContent;
import tsteelworks.common.TSRepo;
import tsteelworks.inventory.HighOvenContainer;
import tsteelworks.lib.*;
import tsteelworks.lib.crafting.AdvancedSmelting;
import tsteelworks.structure.StructureHighOven;
import tsteelworks.util.InventoryHelper;

import java.util.Arrays;

/**
 * The primary class for the High Oven structure's logic.
 */
public class HighOvenLogic extends TileEntity implements IInventory, IActiveLogic, IFacingLogic, IMasterLogic, IRedstonePowered, IChunkNotify {
	/**
	 * Oxidizer Slot - Redox agent.
	 * (gunpowder, sugar, etc)
	 */
	public static final int SLOT_OXIDIZER = 0;

	/**
	 * Reducer Slot - redox agent.
	 * (redstone dust, aluminum dust, etc)
	 */
	public static final int SLOT_REDUCER = 1;

	/**
	 * Purifier Slot - purifying agent.
	 * (sand, graveyard soil, etc)
	 */
	public static final int SLOT_PURIFIER = 2;

	/**
	 * Fuel Slot - cook things.
	 * (charcoal, charcoal block, etc)
	 */
	public static final int SLOT_FUEL = 3;

	/**
	 * First meltable item slot.
	 * All slots after this will also be meltable.
	 * (Iron Ore, Iron Ingot, etc)
	 */
	public static final int SLOT_FIRST_MELTABLE = 4;

	/**
	 * The amount of fluid the tank may gain per layer - multiplier.
	 */
	public static final int FLUID_AMOUNT_PER_LAYER = 20000;

	/**
	 * The max temperature of the smallest High Oven structure.
	 */
	public static final int DEFAULT_MAX_TEMP = 2000;

	/**
	 * Temperature the High Oven defaults to when not cooking.
	 */
	public static final int ROOM_TEMP = 20;

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
	 * The inventory
	 * 4 first slots are for oxidizer, reducer, purifier and fuel
	 * 6 nexts slots (depending on structure size) are for metals
	 * (yes I'm locking this to 6 slots, sorry it feels better that way
	 * as the high oven smelts uber quickly)
	 */
	private SizeableInventory inventory = new SizeableInventory(10);

	/**
	 * Handles the multiblock structure
	 */
	private StructureHighOven structure = new StructureHighOven(this);

	/**
	 * Used to determine if the controller needs to be updated.
	 */
	private boolean needsUpdate;

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
	 * Tick tock, tick tock.
	 */
	private int tick;

	/**
	 * The max temperature.
	 */
	private int maxTemp = DEFAULT_MAX_TEMP;

	/**
	 * The fuel burn time.
	 */
	private int fuelBurnTime;

	/**
	 * The active temperatures of melting items.
	 */
	private int[] activeTemps = new int[0];

	/**
	 * The melting point temperatures if melting items.
	 */
	private int[] meltingTemps = new int[0];

	/**
	 * Used to randomize things.
	 */
	private String invName;

	/**
	 * The structure's output duct instance.
	 */
	private HighOvenDuctLogic outputDuct;

	/**
	 * Max temp by layer.
	 *
	 * @return the int
	 */
	public int maxTempByLayer() {
		return DEFAULT_MAX_TEMP + (structure.getNbLayers() - 1) * 500;
	}

	/**
	 * @return the high oven structure handler
	 */
	public StructureHighOven getStructure() {
		return structure;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

    /* ==================== Facing Logic ==================== */

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
		this.direction = (byte) BlockHelper.orientationToMetadataXZ(yaw);
	}

    /* ==================== Active Logic ==================== */

	@Override
	public boolean getActive() {
		return this.structure.isValid() && this.isBurning();
	}

	@Override
	public void setActive(final boolean flag) {
		needsUpdate = true;
	}

    /* ==================== IRedstonePowered ==================== */

	/**
	 * Get the current state of redstone-connected power.
	 *
	 * @return Redstone powered state
	 */
	@Override
	public boolean getRSmode() {
		return this.redstoneActivated;
	}

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

    /* ==================== Smelting Logic ==================== */

	@Override
	public void updateEntity() {
		this.tick++;

		if (this.tick % 4 == 0)
			this.heatItems();

		// structural checks and fuel gauge updates
		if (this.tick % 20 == 0) {
			if (!structure.isValid())
				this.checkValidPlacement();

			if (this.isBurning()) {
				this.fuelBurnTime -= 3;
				this.internalTemp = Math.min(this.internalTemp + this.fuelHeatRate, maxTemp);
			} else {
				this.internalTemp = Math.max(this.internalTemp - INTERNAL_COOLDOWN_RATE, ROOM_TEMP);
			}

			if (structure.isValid() && this.fuelBurnTime <= 0) {
				this.updateFuelGauge();
			}

			if (needsUpdate) {
				needsUpdate = false;
				worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			}
		}

		if (this.tick == 40)
			this.heatFluids();

		// reset tick to 0, back to the beginning we go~
		if (this.tick == 60)
			this.tick = 0;
	}

	/**
	 * Process item heating and liquifying.
	 */
	private void heatItems() {
		if (this.internalTemp <= ROOM_TEMP)
			return;

		boolean hasSmeltable = false;
		for (int i = SLOT_FIRST_MELTABLE; i < structure.getNbLayers() + SLOT_FIRST_MELTABLE; i++) {
			if (inventory.getStackInSlot(i) == null || this.meltingTemps[i] <= ROOM_TEMP)
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
				final FluidStack result = this.getNormalResultFor(this.inventory.getStackInSlot(i));
				final ItemStack resultitemstack = this.getSolidMixedResultFor(result);

				if (resultitemstack != null) {
					this.meltItemsSolidOutput(i, resultitemstack, true);
				} else if (result != null) {
					final FluidStack resultEx = this.getLiquidMixedResultFor(result);

					if (resultEx != null) {
						this.meltItemsLiquidOutput(i, resultEx, true);
					} else {
						this.meltItemsLiquidOutput(i, result, false);
					}
				}
			}
		}

		isMeltingItems = hasSmeltable;
	}

	/**
	 * Heat fluids. (like steam)
	 *
	 * todo: support for other fluids
	 *       if the temperature < heat temperature, turn fluid back to it's liquid state
	 *       Only melt the liquid at the very bottom of the oven
	 *       But cool every fluids
	 */
	private void heatFluids() {
		if (internalTemp < 1300 || tank.getNbFluids() == 0) return;

		// Let's make steam!
		if (tank.getFluid().getFluid() != FluidRegistry.WATER && tank.getFluid().getFluid() != FluidRegistry.getFluid("Steam"))
			return;

		int amount = 0;
		for (int i = 0; i < tank.getNbFluids(); i++) {
			FluidStack fluid = tank.getFluid(i);

			if (fluid.getFluid() == FluidRegistry.WATER)
				amount += fluid.amount;
		}

		if (amount > 0) {
			FluidStack steam = new FluidStack(TSContent.steamFluid.getID(), amount);
			FluidStack water = new FluidStack(FluidRegistry.WATER, amount);

			if (this.addFluidToTank(steam)) {
				tank.drain(ForgeDirection.UNKNOWN, water, true);
			}
		}
	}

	/**
	 * Melt items liquid output.
	 *
	 * @param slot  the slot
	 * @param fluid the fluid
	 * @param doMix the do mix
	 */
	private void meltItemsLiquidOutput(final int slot, final FluidStack fluid, final Boolean doMix) {
		if (this.addFluidToTank(fluid)) {
			ItemStack stack = this.getStackInSlot(slot);
			if (InventoryHelper.itemIsOre(stack))
				this.outputTE3Slag();

			decrStackSize(slot, 1);

			this.activeTemps[slot] = ROOM_TEMP;

			if (doMix)
				this.removeMixItems();
		}
	}

	/**
	 * Melt items solid output.
	 *
	 * @param slot  the slot
	 * @param stack the stack
	 * @param doMix the do mix
	 */
	private void meltItemsSolidOutput(final int slot, final ItemStack stack, final Boolean doMix) {
		decrStackSize(slot, 1);

		this.activeTemps[slot] = ROOM_TEMP;

		if (doMix)
			this.removeMixItems();

		this.addItem(stack);
		this.onInventoryChanged();
	}

	/**
	 * Gets the normal result for.
	 *
	 * @param itemstack the stack
	 * @return the normal result for
	 */
	public FluidStack getNormalResultFor(final ItemStack itemstack) {
		return AdvancedSmelting.getMeltingResult(itemstack);
	}

	/**
	 * Gets the liquid mixed result for.
	 *
	 * @param fluidstack the stack
	 * @return the liquid mixed result for
	 */
	public FluidStack getLiquidMixedResultFor(final FluidStack fluidstack) {
		final FluidType resultType = FluidType.getFluidType(fluidstack.getFluid());
		final FluidType mixResult = AdvancedSmelting.getMixFluidSmeltingResult(resultType, getStackInSlot(SLOT_OXIDIZER), getStackInSlot(SLOT_REDUCER), getStackInSlot(SLOT_PURIFIER));

		if (mixResult == null) return null;

		return new FluidStack(mixResult.fluid, fluidstack.amount);
	}

	/**
	 * Gets the solid mixed result for.
	 *
	 * @param fluidstack the stack
	 * @return the solid mixed result for
	 */
	public ItemStack getSolidMixedResultFor(final FluidStack fluidstack) {
		final FluidType resultType = FluidType.getFluidType(fluidstack.getFluid());
		final ItemStack mixResult = AdvancedSmelting.getMixItemSmeltingResult(resultType, getStackInSlot(SLOT_OXIDIZER), getStackInSlot(SLOT_REDUCER), getStackInSlot(SLOT_PURIFIER));
		if (mixResult == null) return null;

		return new ItemStack(mixResult.getItem(), mixResult.stackSize, mixResult.getItemDamage());
	}

	/**
	 * Output Thermal Expansion 3 slag if available.
	 */
	private void outputTE3Slag() {
		if (TSteelworks.thermalExpansionAvailable && ConfigCore.enableTE3SlagOutput) {
			if (MathHelper.random.nextInt(10) == 0) {
				this.addItem(GameRegistry.findItemStack("ThermalExpansion", "slag", 1));
			}
		}
	}

	/**
	 * Remove additive materials by preset vs random chance and amount.
	 */
	private void removeMixItems() {
		for (int i = SLOT_OXIDIZER; i < SLOT_FUEL; i++) {
			ItemStack stack = getStackInSlot(i);
			if (stack == null)
				continue;

			final int consumeChance = AdvancedSmelting.getMixItemConsumeChance(stack);
			final int consumeAmount = AdvancedSmelting.getMixItemConsumeAmount(stack);

			if (MathHelper.random.nextInt(100) <= consumeChance) {
				if (stack.stackSize >= consumeAmount) {
					decrStackSize(i, consumeAmount);
				}
			}
		}
	}

    /* ==================== Temperatures ==================== */

	/**
	 * Get internal temperature for smelting.
	 *
	 * @return internal temperature value
	 */
	public int getInternalTemperature() {
		return this.internalTemp;
	}

	/**
	 * Get current temperature for slot.
	 *
	 * @param slot the slot
	 * @return the temp for slot
	 */
	public int getTempForSlot(final int slot) {
		return (this.isSmeltingSlot(slot)) ? this.activeTemps[slot] : ROOM_TEMP;
	}

	/**
	 * Get melting point for item in slot.
	 *
	 * @param slot the slot
	 * @return the melting point for slot
	 */
	public int getMeltingPointForSlot(final int slot) {
		return (this.isSmeltingSlot(slot)) ? this.meltingTemps[slot] : ROOM_TEMP;
	}

	/**
	 * Update melting temperatures for items.
	 */
	private void updateTemperatures() {
		isMeltingItems = true;

		for (int i = SLOT_FIRST_MELTABLE; i < (structure.getNbLayers() + SLOT_FIRST_MELTABLE); i += 1) {
			this.meltingTemps[i] = AdvancedSmelting.getLiquifyTemperature(getStackInSlot(i));
		}
	}

    /* ==================== Fuel Handling ==================== */

	/**
	 * Checks if controller is burning fuel.
	 *
	 * @return true, burning
	 */
	public boolean isBurning() {
		return this.fuelBurnTime > 0;
	}

	/**
	 * Checks for fuel available.
	 *
	 * @return true, fuel is available
	 */
	public boolean hasFuel() {
		return HighOvenLogic.getFuelBurnTime(getStackInSlot(SLOT_FUEL)) > 0;
	}

	/**
	 * Get fuel gauge scaled for display.
	 *
	 * @param scale the scale
	 * @return scaled value
	 */
	public int getScaledFuelGauge(final int scale) {
		return (int) Math.ceil(this.fuelBurnTime / (float) scale);
	}

	/**
	 * Update fuel gauge.
	 */
	private void updateFuelGauge() {
		if (this.isBurning() || !this.getRSmode())
			return;

		ItemStack fuel = getStackInSlot(SLOT_FUEL);
		if (fuel == null) {
			this.fuelBurnTime = 0;
			return;
		}

		int fuelBurnTime = HighOvenLogic.getFuelBurnTime(fuel);
		if (fuelBurnTime <= 0) return;

		this.needsUpdate = true;
		this.fuelBurnTime = fuelBurnTime;
		this.fuelHeatRate = HighOvenLogic.getFuelHeatRate(fuel);

		decrStackSize(SLOT_FUEL, 1);
	}

	/**
	 * Update fuel gauge display.
	 */
	public void updateFuelDisplay() {
		if (HighOvenLogic.getFuelBurnTime(getStackInSlot(SLOT_FUEL)) > 0) {
			this.needsUpdate = true;
			worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		}
	}

	/**
	 * Gets the current fuel burn time.
	 *
	 * @return the fuel burn time
	 */
	public int getFuelBurnTime() {
		return this.fuelBurnTime;
	}

	/**
	 * Sets the current fuel burn time.
	 *
	 * @param value the new fuel burn time
	 */
	public void setFuelBurnTime(final int value) {
		this.fuelBurnTime = value;
	}

	/**
	 * Gets the fuel burn time by given item from the fuel handler.
	 *
	 * @param itemstack the stack
	 * @return the fuel burn time
	 */
	public static int getFuelBurnTime(final ItemStack itemstack) {
		if (itemstack == null) {
			return 0;
		}
		return TSteelworks.fuelHandler.getHighOvenFuelBurnTime(itemstack);
	}

	/**
	 * Gets the current fuel heat rate.
	 *
	 * @return the fuel heat rate
	 */
	public int getFuelHeatRate() {
		return this.fuelHeatRate;
	}

	/**
	 * Sets the current fuel heat rate.
	 *
	 * @param value the new fuel heat rate
	 */
	public void setFuelHeatRate(final int value) {
		this.fuelHeatRate = value;
	}

	/**
	 * Get the rate of heat increase by given item from the fuel handler.
	 *
	 * @param itemstack the itemstack
	 * @return the fuel heat rate
	 */
	public static int getFuelHeatRate(final ItemStack itemstack) {
		if (itemstack == null)
			return 0;

		return TSFuelHandler.getHighOvenFuelHeatRate(itemstack);
	}

    /* ==================== Inventory ==================== */

	/**
	 * Determine is slot is valid for 'ore' processing.
	 *
	 * @param slot the slot
	 * @return true if slot is valid
	 */
	public boolean isSmeltingSlot(final int slot) {
		return slot > SLOT_FUEL;
	}

	public Container getGuiContainer(final InventoryPlayer inventoryplayer) {
		return new HighOvenContainer(inventoryplayer, this);
	}

	/**
	 * Called when an the contents of Inventory change.
	 */
	public void onInventoryChanged() {
		this.updateTemperatures();

		this.needsUpdate = true;

		this.markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(final int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(final int slot, final int quantity) {
		ItemStack stack = inventory.decrStackSize(slot, quantity);

		onInventoryChanged();

		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(final int slot) {
		ItemStack stack = inventory.getStackInSlotOnClosing(slot);

		onInventoryChanged();

		return stack;
	}

	@Override
	public void setInventorySlotContents(final int slot, final ItemStack itemstack) {
		inventory.setInventorySlotContents(slot, itemstack);

		onInventoryChanged();
	}

	@Override
	public String getInventoryName() {
		return hasCustomInventoryName() ? this.invName : "crafters.HighOven";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return invName == null;
	}

	@Override
	public boolean isItemValidForSlot(final int slot, final ItemStack itemstack) {
		return true;
	}

	@Override
	public boolean isUseableByPlayer(final EntityPlayer entityplayer) {
		return worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && entityplayer.getDistance(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64D;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

    /* ==================== Multiblock ==================== */

	@Override
	public void notifyChange(final IServantLogic servant, final int x, final int y, final int z) {
		this.checkValidPlacement();
	}

	/**
	 * Check placement validation by facing direction.
	 */
	@Override
	public void checkValidPlacement() {
		int[] centerBlock =  BlockHelper.getAdjacentBlock(xCoord, yCoord, zCoord, BlockHelper.getOppositeSide(getRenderDirection()));

		structure.checkValidStructure(centerBlock[0], centerBlock[1], centerBlock[2]);
	}

    /* ==================== Fluid Handling ==================== */

	/**
	 * Add molen metal fluidstack.
	 *
	 * @param liquid the liquid
	 * @return Success
	 */
	public boolean addFluidToTank(final FluidStack liquid) {
		// TODO: Tank should only hold multiple fluids under certain special circumstances ex water & steam, anything & slag

		if (tank.fill(ForgeDirection.UNKNOWN, liquid, false) != liquid.amount)
			return false;

		needsUpdate = true;

		tank.fill(ForgeDirection.UNKNOWN, liquid, true);

		return true;
	}

	/**
	 * Adds the item.
	 *
	 * @param itemstack the ItemStack
	 */
	public void addItem(final ItemStack itemstack) {
		if (outputDuct != null) {
			nf.fr.ephys.cookiecore.helpers.InventoryHelper.insertItem(outputDuct, itemstack);
		} else {
			dispenseItem(itemstack);
		}
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
	public final void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		inventory.readFromNBT(nbt.getCompoundTag("inventory"));
		tank.readFromNBT(nbt.getCompoundTag("tank"));

		internalTemp = nbt.getInteger(TSRepo.NBTNames.internalTemp);
		isMeltingItems = nbt.getBoolean(TSRepo.NBTNames.inUse);

		direction = nbt.getByte(TSRepo.NBTNames.direction);
		setFuelBurnTime(nbt.getInteger(TSRepo.NBTNames.useTime));
		setFuelHeatRate(nbt.getInteger(TSRepo.NBTNames.fuelHeatRate));

		meltingTemps = nbt.getIntArray(TSRepo.NBTNames.meltingTemps);
		activeTemps = nbt.getIntArray(TSRepo.NBTNames.activeTemps);
	}

	@Override
	public final void writeToNBT(final NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		NBTHelper.setWritable(nbt, "inventory", inventory);
		NBTHelper.setWritable(nbt, "tank", tank);

		nbt.setInteger(TSRepo.NBTNames.internalTemp, internalTemp);
		nbt.setBoolean(TSRepo.NBTNames.inUse, isMeltingItems);

		nbt.setByte(TSRepo.NBTNames.direction, this.direction);
		nbt.setInteger(TSRepo.NBTNames.useTime, this.fuelBurnTime);
		nbt.setInteger(TSRepo.NBTNames.fuelHeatRate, this.fuelHeatRate);

		nbt.setIntArray(TSRepo.NBTNames.meltingTemps, this.meltingTemps);
		nbt.setIntArray(TSRepo.NBTNames.activeTemps, this.activeTemps);
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

		this.onInventoryChanged();
		this.worldObj.markBlockRangeForRenderUpdate(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 1, this.zCoord + 1);
	}

    /* =============== IMaster =============== */
	@Override
	public final CoordTuple getCoord() {
		return new CoordTuple(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public final boolean isValid() {
		return structure.isValid();
	}

	/**
	 * @return the fill ratio
	 */
	public final int getFillRatio() {
		return tank.getNbFluids() == 0 ? 0 : tank.getCapacity() / tank.getFluidAmount();
	}

	public void onStructureChange(StructureHighOven structure) {
		needsUpdate = true;
		if (!structure.isValid()) {
			internalTemp = ROOM_TEMP;

			return;
		}

		final int oldNbLayers = activeTemps.length;
		final int nbLayers = structure.getNbLayers();

		this.tank.setCapacity(FLUID_AMOUNT_PER_LAYER * nbLayers);
		this.maxTemp = this.maxTempByLayer();

		if (nbLayers > oldNbLayers) {
			activeTemps = Arrays.copyOf(activeTemps, nbLayers);
			meltingTemps = Arrays.copyOf(meltingTemps, nbLayers);

			for (int i = oldNbLayers; i < nbLayers; i++) {
				if (!this.isSmeltingSlot(i))
					continue;

				this.activeTemps[i] = ROOM_TEMP;
				this.meltingTemps[i] = ROOM_TEMP;
			}
		}

		this.inventory.setInventorySize(nbLayers);

		int[] dumpCoords = BlockHelper.getAdjacentBlock(xCoord, yCoord, zCoord, direction);
		this.inventory.dumpOverflow(worldObj, dumpCoords[0], dumpCoords[1], dumpCoords[2]);
	}

	@Override
	public void onChunkLoaded() {
		checkValidPlacement();
	}
}