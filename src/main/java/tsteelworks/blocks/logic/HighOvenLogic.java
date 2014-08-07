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
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistryDefaulted;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import nf.fr.ephys.cookiecore.helpers.BlockHelper;
import nf.fr.ephys.cookiecore.util.Inventory;
import tconstruct.library.component.MultiFluidTank;
import tconstruct.library.crafting.FluidType;
import tsteelworks.TSteelworks;
import tsteelworks.common.TSContent;
import tsteelworks.common.TSRepo;
import tsteelworks.inventory.HighOvenContainer;
import tsteelworks.lib.*;
import tsteelworks.lib.crafting.AdvancedSmelting;
import tsteelworks.structure.StructureHighOven;
import tsteelworks.util.InventoryHelper;

import java.util.List;
import java.util.Random;

/**
 * The primary class for the High Oven structure's logic.
 */
public class HighOvenLogic extends TileEntity implements IInventory, IFluidHandler, IActiveLogic, IFacingLogic, IFluidTank, IMasterLogic, IRedstonePowered {
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
	private MultiFluidTank tank = new MultiFluidTank();

	/**
	 * The inventory
	 * 4 first slots are for oxidizer, reducer, purifier and fuel
	 * 6 nexts slots (depending on structure size) are for metals
	 * (yes I'm locking this to 6 slots, sorry it feels better that way
	 * as the high oven smelts uber quickly)
	 */
	private Inventory inventory = new Inventory(10);

	/**
	 * Handles the multiblock structure
	 */
	private StructureHighOven structure = new StructureHighOven();

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
	private Random rand = new Random();
	private String invName;

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

		boolean hasUse = false;
		for (int i = SLOT_FIRST_MELTABLE; i < structure.getNbLayers() + SLOT_FIRST_MELTABLE; i ++) {
			if (inventory.getStackInSlot(i) == null || this.meltingTemps[i] <= ROOM_TEMP)
				continue;

			hasUse = true;
			// Increase temp if its temp is lower than the High Oven's internal temp and hasn't reached melting point
			if ((this.activeTemps[i] < this.internalTemp) && (this.activeTemps[i] < this.meltingTemps[i])) {
				this.activeTemps[i] += (this.internalTemp > 250) ? (this.internalTemp / 250) : 1;
				// Decrease temp if its temp is higher than the High Oven's internal
				// temp and the High Oven's internal temp is lower than the melting point
			} else if ((this.activeTemps[i] > this.internalTemp) && (this.internalTemp < this.meltingTemps[i])) {
				this.activeTemps[i] -= 1;
				// Liquify metals if the temp has reached the melting point
			} else if (this.activeTemps[i] >= this.meltingTemps[i]) {
				if (!worldObj.isRemote) {
					final FluidStack result = this.getNormalResultFor(this.inventory[i]);
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
		}

		isMeltingItems = hasUse;
	}

	/**
	 * Heat fluids. (like steam)
	 */
	private void heatFluids() {
		if ((this.internalTemp < 1300) || (this.fluidlist.size() < 1)) {
			return;
		}
		// Let's make steam!
		if ((this.getFluid().getFluid() == FluidRegistry.WATER) || (this.getFluid().getFluid() == FluidRegistry.getFluid("Steam"))) {
			int amount = 0;
			for (final FluidStack fluid : this.fluidlist) {
				if (fluid.getFluid() == FluidRegistry.WATER) {
					amount += fluid.amount;
				}
			}
			if (amount > 0) {
				final FluidStack steam = new FluidStack(TSContent.steamFluid.getID(), amount);
				if (this.addFluidToTank(steam, false)) {
					this.fluidlist.remove(0);
					this.currentLiquid -= amount;
				}
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
		if (this.addFluidToTank(fluid, false)) {
			if (InventoryHelper.itemIsOre(this.inventory[slot])) {
				this.outputTE3Slag();
			}

			if (this.inventory[slot].stackSize >= 2) {
				this.inventory[slot].stackSize--;
			} else {
				this.inventory[slot] = null;
			}
			this.activeTemps[slot] = ROOM_TEMP;
			if (doMix) {
				this.removeMixItems();
			}
			this.onInventoryChanged();
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
		if (this.inventory[slot].stackSize >= 2) {
			this.inventory[slot].stackSize--;
		} else {
			this.inventory[slot] = null;
		}
		this.activeTemps[slot] = ROOM_TEMP;
		if (doMix) {
			this.removeMixItems();
		}
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
		final FluidType mixResult = AdvancedSmelting.getMixFluidSmeltingResult(resultType, this.inventory[SLOT_OXIDIZER], this.inventory[SLOT_REDUCER], this.inventory[SLOT_PURIFIER]);
		if (mixResult != null) {
			return new FluidStack(mixResult.fluid, fluidstack.amount);
		}

		return null;
	}

	/**
	 * Gets the solid mixed result for.
	 *
	 * @param fluidstack the stack
	 * @return the solid mixed result for
	 */
	public ItemStack getSolidMixedResultFor(final FluidStack fluidstack) {
		final FluidType resultType = FluidType.getFluidType(fluidstack.getFluid());
		final ItemStack mixResult = AdvancedSmelting.getMixItemSmeltingResult(resultType, this.inventory[SLOT_OXIDIZER], this.inventory[SLOT_REDUCER], this.inventory[SLOT_PURIFIER]);
		if (mixResult != null) {
			return new ItemStack(mixResult.getItem(), mixResult.stackSize, mixResult.getItemDamage());
		}

		return null;
	}

	/**
	 * Output Thermal Expansion 3 slag if available.
	 */
	private void outputTE3Slag() {
		if (TSteelworks.thermalExpansionAvailable && ConfigCore.enableTE3SlagOutput) {
			if (new Random().nextInt(100) <= 10) {
				this.addItem(GameRegistry.findItemStack("ThermalExpansion", "slag", 1));
			}
		}
	}

	/**
	 * Remove additive materials by preset vs random chance and amount.
	 */
	private void removeMixItems() {
		// using SLOT_0 and SLOT_FIRST_MELTABLE - 1? another reason to split
		// inventory between FixedSizeInventoy for 0..3 and
		// VariableSizeInventory for meltables
		for (int i = SLOT_OXIDIZER; i < SLOT_FUEL; i++) {
			if (this.inventory[i] == null) {
				continue;
			}

			final int consumeChance = AdvancedSmelting.getMixItemConsumeChance(this.inventory[i]);
			final int consumeAmount = AdvancedSmelting.getMixItemConsumeAmount(this.inventory[i]);
			if (new Random().nextInt(100) <= consumeChance) {
				if (this.inventory[i].stackSize >= consumeAmount) {
					this.inventory[i].stackSize -= consumeAmount;
				}
			}
			if ((this.inventory[i] != null) && (this.inventory[i].stackSize == 0)) {
				this.inventory[i] = null;
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
		this.isMeltingItems = true;
		for (int i = SLOT_FIRST_MELTABLE; i < (this.nbLayers + SLOT_FIRST_MELTABLE); i += 1) {
			this.meltingTemps[i] = AdvancedSmelting.getLiquifyTemperature(this.inventory[i]);
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
		return HighOvenLogic.getFuelBurnTime(this.inventory[SLOT_FUEL]) > 0;
	}

	/**
	 * Get fuel gauge scaled for display.
	 *
	 * @param scale the scale
	 * @return scaled value
	 */
	public int getScaledFuelGauge(final int scale) {
		final int value = this.fuelBurnTime / scale;
		return value < 1 ? 1 : value;
	}

	/**
	 * Update fuel gauge.
	 */
	private void updateFuelGauge() {
		if (this.isBurning() || !this.getRSmode()) {
			return;
		}
		if (this.inventory[SLOT_FUEL] == null) {
			this.fuelBurnTime = 0;
			return;
		}
		if (HighOvenLogic.getFuelBurnTime(this.inventory[SLOT_FUEL]) > 0) {
			this.needsUpdate = true;
			this.fuelBurnTime = HighOvenLogic.getFuelBurnTime(this.inventory[SLOT_FUEL]);
			this.fuelHeatRate = HighOvenLogic.getFuelHeatRate(this.inventory[SLOT_FUEL]);
			this.inventory[SLOT_FUEL].stackSize--;
			if (this.inventory[SLOT_FUEL].stackSize <= 0) {
				this.inventory[SLOT_FUEL] = null;
			}
		}
	}

	/**
	 * Update fuel gauge display.
	 */
	public void updateFuelDisplay() {
		if (HighOvenLogic.getFuelBurnTime(this.inventory[SLOT_FUEL]) > 0) {
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
		if (itemstack == null) {
			return 0;
		}
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

	/* (non-Javadoc)
	 * @see tsteelworks.lib.blocks.TSInventoryLogic#getInventoryStackLimit()
	 */
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	/**
	 * Called when an the contents of Inventory change.
	 */
	public void onInventoryChanged() {
		this.updateTemperatures();

		this.needsUpdate = true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see tsteelworks.lib.blocks.TSInventoryLogic#getSizeInventory()
	 */
	@Override
	public int getSizeInventory() {
		return this.inventory.length;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see tsteelworks.lib.blocks.TSInventoryLogic#getStackInSlot(int)
	 */
	@Override
	public ItemStack getStackInSlot(final int slot) {
		return this.inventory[slot];
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see tsteelworks.lib.blocks.TSInventoryLogic#decrStackSize(int, int)
	 */
	@Override
	public ItemStack decrStackSize(final int slot, final int quantity) {
		if (this.inventory[slot] != null) {
			if (this.inventory[slot].stackSize <= quantity) {
				final ItemStack stack = this.inventory[slot];
				this.inventory[slot] = null;
				return stack;
			}
			final ItemStack split = this.inventory[slot].splitStack(quantity);
			if (this.inventory[slot].stackSize == 0) {
				this.inventory[slot] = null;
			}
			return split;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see tsteelworks.lib.blocks.TSInventoryLogic#getStackInSlotOnClosing(int)
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(final int slot) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * tsteelworks.lib.blocks.TSInventoryLogic#setInventorySlotContents(int,
	 * net.minecraft.item.ItemStack)
	 */
	@Override
	public void setInventorySlotContents(final int slot, final ItemStack itemstack) {
		this.inventory[slot] = itemstack;
		if ((itemstack != null) && (itemstack.stackSize > this.getInventoryStackLimit())) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return hasCustomInventoryName() ? this.invName : "crafters.HighOven";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return invName == null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see tsteelworks.lib.blocks.TSInventoryLogic#isItemValidForSlot(int,
	 * net.minecraft.item.ItemStack)
	 */
	@Override
	public boolean isItemValidForSlot(final int slot, final ItemStack itemstack) {
		if (slot < this.getSizeInventory()) {
			if ((this.inventory[slot] == null) || ((itemstack.stackSize + this.inventory[slot].stackSize) <= this.getInventoryStackLimit())) {
				return true;
			}
		}

		return false;
	}

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

		structure.alignInitialPlacement(centerBlock[0], centerBlock[1], centerBlock[2]);
	}

    /* ==================== Fluid Handling ==================== */

	/**
	 * Add molen metal fluidstack.
	 *
	 * @param liquid the liquid
	 * @param first  the first
	 * @return Success
	 */
	final boolean addFluidToTank(final FluidStack liquid, final boolean first) {
		// TODO: Tank should only hold multiple fluids under certain special circumstances ex water & steam, anything & slag

		this.needsUpdate = true;
		if (this.fluidlist.size() == 0) {
			this.fluidlist.add(liquid.copy());
			this.currentLiquid += liquid.amount;
			return true;
		} else {
			// if (liquid.fluidID != TSContent.steamFluid.getID())
			// return false;
			if ((liquid.amount + this.currentLiquid) > this.maxLiquid) {
				return false;
			}
			this.currentLiquid += liquid.amount;
			boolean added = false;
			for (int i = 0; i < this.fluidlist.size(); i++) {
				final FluidStack l = this.fluidlist.get(i);
				if (l.isFluidEqual(liquid)) {
					l.amount += liquid.amount;
					added = true;
				}
				if (l.amount <= 0) {
					this.fluidlist.remove(l);
					i--;
				}
			}
			if (!added) {
				if (first) {
					this.fluidlist.add(0, liquid.copy());
				} else {
					this.fluidlist.add(liquid.copy());
				}
			}
			return true;
		}
	}

	/**
	 * Adds the item.
	 *
	 * @param itemstack the ItemStack
	 */
	final void addItem(final ItemStack itemstack) {
		boolean transferred = false;
		// If we have an output duct...
		if (this.outputDuct != null) {
			final TileEntity te = this.worldObj.getTileEntity(this.outputDuct.x, this.outputDuct.y, this.outputDuct.z);
			if ((te != null) && (te instanceof HighOvenDuctLogic)) {
				transferred = this.sendItemToDuct((HighOvenDuctLogic) te, itemstack);
			} else {
				this.outputDuct = null; // If duct no longer exists, get rid of
				// it!
			}
		}
		// Dispense item if no duct is present
		if (!transferred) {
			this.dispenseItem(itemstack);
		}
	}

	/**
	 * Send item to duct.
	 *
	 * @param duct      the duct
	 * @param itemstack the stack
	 * @return item sent
	 */
	public boolean sendItemToDuct(final HighOvenDuctLogic duct, final ItemStack itemstack) {
		return itemstack == null || nf.fr.ephys.cookiecore.helpers.InventoryHelper.insertItem(duct, itemstack);
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

	/**
	 * Removes the from fluid list.
	 *
	 * @param fluidstack the fluidstack
	 * @return true, if successful
	 */
	public boolean removeFromFluidList(final FluidStack fluidstack) {
		return this.fluidlist.remove(fluidstack);
	}

	/**
	 * Adds the to fluid list.
	 *
	 * @param fluidstack the fluidstack
	 * @return true, if successful
	 */
	public boolean addToFluidList(final FluidStack fluidstack) {
		return this.fluidlist.add(fluidstack);
	}

	/**
	 * Adds the to fluid list.
	 *
	 * @param index      the index
	 * @param fluidstack the fluidstack
	 */
	public void addToFluidList(final int index, final FluidStack fluidstack) {
		this.fluidlist.add(index, fluidstack);
	}

	/**
	 * Get max liquid capacity.
	 *
	 * @return the capacity
	 */
	/*
     * (non-Javadoc)
     *
     * @see net.minecraftforge.fluids.IFluidTank#getCapacity()
     */
	@Override
	public final int getCapacity() {
		return this.maxLiquid;
	}

	/**
	 * Get current liquid amount.
	 *
	 * @return the total liquid
	 */
	public final int getTotalLiquid() {
		return this.currentLiquid;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.minecraftforge.fluids.IFluidTank#drain(int, boolean)
	 */
	@Override
	public final FluidStack drain(final int maxDrain, final boolean doDrain) {
		if (this.fluidlist.size() == 0) {
			return null;
		}
		final FluidStack liquid = this.fluidlist.get(0);
		if (liquid != null) {
			if ((liquid.amount - maxDrain) <= 0) {
				final FluidStack liq = liquid.copy();
				if (doDrain) {
					this.fluidlist.remove(liquid);
					this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
					this.currentLiquid = 0;
					this.needsUpdate = true;
				}
				return liq;
			} else {
				if (doDrain && (maxDrain > 0)) {
					liquid.amount -= maxDrain;
					this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
					this.currentLiquid -= maxDrain;
					this.needsUpdate = true;
				}
				return new FluidStack(liquid.fluidID, maxDrain, liquid.tag);
			}
		} else {
			return new FluidStack(0, 0);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.minecraftforge.fluids.IFluidTank#fill(net.minecraftforge.fluids.
	 * FluidStack, boolean)
	 */
	@Override
	public final int fill(final FluidStack resource, final boolean doFill) {
		if (resource != null && this.currentLiquid < this.maxLiquid) {
			final boolean first = resource.getFluid() == FluidRegistry.WATER;
			if ((resource.amount + this.currentLiquid) > this.maxLiquid) {
				resource.amount = this.maxLiquid - this.currentLiquid;
			}
			final int amount = resource.amount;
			if ((amount > 0) && doFill) {
				this.addFluidToTank(resource, first);
				this.needsUpdate = true;
				this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
			}
			return amount;
		} else {
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.minecraftforge.fluids.IFluidTank#getFluid()
	 */
	@Override
	public final FluidStack getFluid() {
		if (this.fluidlist.size() == 0) {
			return null;
		}
		return this.fluidlist.get(0);
	}

	/**
	 * Gets the total fluid amount.
	 *
	 * @return the total fluid amount
	 */
	public final int getTotalFluidAmount() {
		if (this.fluidlist.size() == 0) {
			return this.currentLiquid;
		}

		int amt = 0;

		for (int i = 0; i < this.fluidlist.size(); i++) {
			final FluidStack l = this.fluidlist.get(i);
			amt += l.amount;
		}
		return amt;
	}

	/**
	 * Gets the fill ratio.
	 *
	 * @return the fill ratio
	 */
	public final int getFillRatio() {
		return this.currentLiquid <= 0 ? 0 : this.maxLiquid / this.getTotalFluidAmount();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.minecraftforge.fluids.IFluidTank#getFluidAmount()
	 */
	@Override
	public final int getFluidAmount() {
		return this.currentLiquid;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.minecraftforge.fluids.IFluidTank#getInfo()
	 */
	@Override
	public final FluidTankInfo getInfo() {
		return new FluidTankInfo(this);
	}

	/**
	 * Gets the fluidlist.
	 *
	 * @return the fluidlist
	 */
	public final List<FluidStack> getFluidlist() {
		return this.fluidlist;
	}

	/**
	 * Gets the multi tank info.
	 *
	 * @return the multi tank info
	 */
	public final FluidTankInfo[] getMultiTankInfo() {
		final FluidTankInfo[] info = new FluidTankInfo[this.fluidlist.size() + 1];
		for (int i = 0; i < this.fluidlist.size(); i++) {
			final FluidStack fluid = this.fluidlist.get(i);
			info[i] = new FluidTankInfo(fluid.copy(), fluid.amount);
		}
		info[this.fluidlist.size()] = new FluidTankInfo(null, this.maxLiquid - this.currentLiquid);
		return info;
	}

    /* ==================== NBT ==================== */

	/*
	 * (non-Javadoc)
	 *
	 * @see tsteelworks.lib.blocks.TSInventoryLogic#readFromNBT(net.minecraft.nbt.NBTTagCompound)
	 */
	/*
	 * maxtemp is replaceable by maxTempByLayer()
	 */
	@Override
	public final void readFromNBT(final NBTTagCompound tags) {
		this.nbLayers = tags.getInteger(TSRepo.NBTNames.layers);
		this.maxTemp = tags.getInteger(TSRepo.NBTNames.maxTemp);
		this.inventory = new ItemStack[4 + this.nbLayers];

		final int[] duct = tags.getIntArray(TSRepo.NBTNames.outputDuct);
		if (duct.length > 2) {
			this.outputDuct = new CoordTuple(duct[0], duct[1], duct[2]);
		}

		super.readFromNBT(tags);
		this.setRSmode(tags.getBoolean(TSRepo.NBTNames.redstoneOn));
		this.internalTemp = tags.getInteger(TSRepo.NBTNames.internalTemp);
		this.isMeltingItems = tags.getBoolean(TSRepo.NBTNames.inUse);
		final int[] center = tags.getIntArray(TSRepo.NBTNames.centerPos);
		if (center.length > 2) {
			this.centerPos = new CoordTuple(center[0], center[1], center[2]);
		} else {
			this.centerPos = new CoordTuple(this.xCoord, this.yCoord, this.zCoord);
		}
		this.direction = tags.getByte(TSRepo.NBTNames.direction);
		this.setFuelBurnTime(tags.getInteger(TSRepo.NBTNames.useTime));
		this.setFuelHeatRate(tags.getInteger(TSRepo.NBTNames.fuelHeatRate));
		this.currentLiquid = tags.getInteger(TSRepo.NBTNames.currentLiquid);
		this.maxLiquid = tags.getInteger(TSRepo.NBTNames.maxLiquid);
		this.meltingTemps = tags.getIntArray(TSRepo.NBTNames.meltingTemps);
		this.activeTemps = tags.getIntArray(TSRepo.NBTNames.activeTemps);
		final NBTTagList liquidTag = tags.getTagList(TSRepo.NBTNames.liquids);
		this.fluidlist.clear();
		for (int iter = 0; iter < liquidTag.tagCount(); iter++) {
			final NBTTagCompound nbt = (NBTTagCompound) liquidTag.tagAt(iter);
			final FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
			if (fluid != null) {
				this.fluidlist.add(fluid);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * tsteelworks.lib.blocks.TSInventoryLogic#writeToNBT(net.minecraft.nbt.
	 * NBTTagCompound)
	 */
	@Override
	public final void writeToNBT(final NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setBoolean(TSRepo.NBTNames.redstoneOn, this.redstoneActivated);
		nbt.setInteger(TSRepo.NBTNames.internalTemp, this.internalTemp);
		nbt.setBoolean(TSRepo.NBTNames.inUse, this.isMeltingItems);

		int[] duct = new int[3];
		if (this.outputDuct != null) {
			duct = new int[] {this.outputDuct.x, this.outputDuct.y, this.outputDuct.z};
		}

		nbt.setIntArray(TSRepo.NBTNames.outputDuct, duct);

		int[] center = new int[3];
		if (this.centerPos == null) {
			center = new int[] {this.xCoord, this.yCoord, this.zCoord};
		} else {
			center = new int[] {this.centerPos.x, this.centerPos.y, this.centerPos.z};
		}
		nbt.setIntArray(TSRepo.NBTNames.centerPos, center);
		nbt.setByte(TSRepo.NBTNames.direction, this.direction);
		nbt.setInteger(TSRepo.NBTNames.useTime, this.fuelBurnTime);
		nbt.setInteger(TSRepo.NBTNames.fuelHeatRate, this.fuelHeatRate);
		nbt.setInteger(TSRepo.NBTNames.currentLiquid, this.currentLiquid);
		nbt.setInteger(TSRepo.NBTNames.maxLiquid, this.maxLiquid);
		nbt.setInteger(TSRepo.NBTNames.layers, this.nbLayers);
		nbt.setInteger(TSRepo.NBTNames.maxTemp, this.maxTemp);
		nbt.setIntArray(TSRepo.NBTNames.meltingTemps, this.meltingTemps);
		nbt.setIntArray(TSRepo.NBTNames.activeTemps, this.activeTemps);
		final NBTTagList taglist = new NBTTagList();
		for (final FluidStack liquid : this.fluidlist) {
			final NBTTagCompound nbt = new NBTTagCompound();
			liquid.writeToNBT(nbt);
			taglist.appendTag(nbt);
		}
		nbt.setTag(TSRepo.NBTNames.liquids, taglist);
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

    /* ==================== IInventory ==================== */

    public Container getGuiContainer(final InventoryPlayer inventoryplayer) {
	    return new HighOvenContainer(inventoryplayer, this);
    }

	@Override
	public boolean isUseableByPlayer(final EntityPlayer entityplayer) {
		if (worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this)
			return false;

		return entityplayer.getDistance(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64D;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

    /* =============== IMaster =============== */

	@Override
	public final CoordTuple getCoord() {
		return new CoordTuple(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public final boolean isValid() {
		return this.validStructure;
	}

	/* =============== IFluidHandler ============== */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[0];
	}
}
