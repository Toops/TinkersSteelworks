package toops.tsteelworks.common.plugins.minetweaker3.handler.highoven;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import toops.tsteelworks.api.highoven.ISmeltingRegistry;
import toops.tsteelworks.api.highoven.ISmeltingRegistry.IMeltData;
import toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin;

class MeltingHandler {
	static class Add extends MinetweakerPlugin.Add<ItemStack, IMeltData> {
		public Add(final ItemStack meltable, final boolean isOre, final FluidStack output, final int meltTemp) {
			super(meltable, new ISmeltingRegistry.IMeltData() {
				@Override
				public int getMeltingPoint() {
					return meltTemp;
				}

				@Override
				public FluidStack getResult() {
					return output;
				}

				@Override
				public boolean isOre() {
					return isOre;
				}
			});
		}

		@Override
		public void apply() {
			oldData = ISmeltingRegistry.INSTANCE.addMeltable(key, newData.isOre(), newData.getResult(), newData.getMeltingPoint());
		}

		@Override
		public void undo() {
			if (oldData == null)
				ISmeltingRegistry.INSTANCE.removeMeltable(key);
			else
				ISmeltingRegistry.INSTANCE.addMeltable(key, oldData.isOre(), oldData.getResult(), oldData.getMeltingPoint());
		}

		@Override
		public String describe() {
			return (oldData == null ? "Added " : "Remplaced ") + key.getDisplayName() + " as valid High Oven meltable.";
		}
	}

	static class Remove extends MinetweakerPlugin.Remove<ItemStack, IMeltData> {
		public Remove(final ItemStack stack) {
			super(stack);
		}

		@Override
		public void apply() {
			oldData = ISmeltingRegistry.INSTANCE.removeMeltable(key);
		}

		@Override
		public void undo() {
			if (oldData == null) return;
			
			ISmeltingRegistry.INSTANCE.addMeltable(key, oldData.isOre(), oldData.getResult(), oldData.getMeltingPoint());
		}

		@Override
		public String describe() {
			return "Removed " + key.getDisplayName() + " as valid High Oven meltable.";
		}
	}
}
