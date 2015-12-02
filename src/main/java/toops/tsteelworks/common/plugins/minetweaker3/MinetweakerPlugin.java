package toops.tsteelworks.common.plugins.minetweaker3;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import toops.tsteelworks.common.plugins.ModCompatPlugin;
import toops.tsteelworks.common.plugins.minetweaker3.handler.highoven.HighOvenWrapper;
import toops.tsteelworks.common.plugins.minetweaker3.handler.mix.MixWrapper;

public class MinetweakerPlugin extends ModCompatPlugin {
	public static FluidStack parseLiquid(ILiquidStack ls) {
		return FluidRegistry.getFluidStack(ls.getName(), ls.getAmount());
	}

	public static ItemStack parseItem(IItemStack is) {
		return (ItemStack) is.getInternal();
	}

	@Override
	public String getModId() {
		return "MineTweaker3";
	}

	@Override
	public void preInit() {
	}

	@Override
	public void init() {
		MineTweakerAPI.registerClass(HighOvenWrapper.class);
		MineTweakerAPI.registerClass(MixWrapper.class);
	}

	@Override
	public void postInit() {
	}

	public static abstract class Remove<Key, Value> implements IUndoableAction {
		protected final Key key;
		protected Value oldData;

		public Remove(Key stack) {
			this.key = stack;
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public String describeUndo() {
			return "Undid " + describe();
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}

	public static abstract class Add<Key, Value> implements IUndoableAction {
		protected final Key key;
		protected Value newData;
		protected Value oldData;

		public Add(Key key, Value newData) {
			this.key = key;
			this.newData = newData;
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public String describeUndo() {
			return "Undid " + describe();
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}
}
