package toops.tsteelworks.common.plugins.minetweaker3.handler;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import toops.tsteelworks.api.highoven.ISmeltingRegistry;
import toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin;

import static toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin.parseItem;

@ZenClass("mods.tsteelworks.highoven")
public class SmeltingHandler {
	@ZenMethod
	public static void addMeltable(IItemStack meltable, boolean isOre, ILiquidStack output, int meltTemp) {
		MineTweakerAPI.apply(new Add(parseItem(meltable), isOre, MinetweakerPlugin.parseLiquid(output), meltTemp));
	}

	@ZenMethod
	public static void removeMeltable(IItemStack meltable) {
		MineTweakerAPI.apply(new Remove(parseItem(meltable)));
	}

	private static class Add implements IUndoableAction {
		private final ItemStack meltable;
		private final boolean isOre;
		private final FluidStack output;
		private final int meltTemp;

		public Add(ItemStack meltable, boolean isOre, FluidStack output, int meltTemp) {
			this.meltable = meltable;
			this.output = output;
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

		public Remove(ItemStack stack) {
			this.meltable = stack;
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
