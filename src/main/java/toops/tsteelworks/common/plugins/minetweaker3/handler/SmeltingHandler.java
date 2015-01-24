package toops.tsteelworks.common.plugins.minetweaker3.handler;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import toops.tsteelworks.api.highoven.ISmeltingRegistry;

@ZenClass("mods.tsteelworks.highoven.meltable")
public class SmeltingHandler {
	@ZenMethod
	public static void addMeltable(IItemStack meltable, boolean isOre, ILiquidStack output, int meltTemp) {
		MineTweakerAPI.apply(new Add(meltable, isOre, output, meltTemp));
	}

	@ZenMethod
	public static void removeMeltable(IItemStack meltable) {
		MineTweakerAPI.apply(new Remove(meltable));
	}

	private static class Add implements IUndoableAction {
		private final ItemStack meltable;
		private final boolean isOre;
		private final FluidStack output;
		private final int meltTemp;

		public Add(IItemStack meltable, boolean isOre, ILiquidStack output, int meltTemp) {
			this.meltable = (ItemStack) meltable.getInternal();
			this.output = FluidRegistry.getFluidStack(output.getName(), output.getAmount());
			this.isOre = isOre;
			this.meltTemp = meltTemp;
		}

		@Override
		public void apply() {
			ISmeltingRegistry.INSTANCE.addMeltable(meltable, isOre, output, meltTemp);
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void undo() {
			ISmeltingRegistry.INSTANCE.removeMeltable(meltable);
		}

		@Override
		public String describe() {
			return "Added " + meltable.getDisplayName() + " as valid High Oven meltable.";
		}

		@Override
		public String describeUndo() {
			return "Removed " + meltable.getDisplayName() + " as valid High Oven meltable.";
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}

	private static class Remove implements IUndoableAction {
		private final ItemStack meltable;
		private boolean isOre;
		private FluidStack output;
		private int meltTemp;

		public Remove(IItemStack stack) {
			this.meltable = (ItemStack) stack.getInternal();
		}

		@Override
		public void apply() {
			ISmeltingRegistry.IMeltData data = ISmeltingRegistry.INSTANCE.removeMeltable(meltable);

			isOre = data.isOre();
			meltTemp = data.getMeltingPoint();
			output = data.getResult();
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void undo() {
			ISmeltingRegistry.INSTANCE.addMeltable(meltable, isOre, output, meltTemp);
		}

		@Override
		public String describeUndo() {
			return "Added " + meltable.getDisplayName() + " as valid High Oven meltable.";
		}

		@Override
		public String describe() {
			return "Removed " + meltable.getDisplayName() + " as valid High Oven meltable.";
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}
}
