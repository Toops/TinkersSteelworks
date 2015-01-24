package toops.tsteelworks.common.plugins.minetweaker3;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import toops.tsteelworks.api.highoven.IFuelRegistry;
import toops.tsteelworks.common.plugins.ModCompatPlugin;
import toops.tsteelworks.common.plugins.minetweaker3.handler.FuelHandler;
import toops.tsteelworks.common.plugins.minetweaker3.handler.MixAgentHandler;
import toops.tsteelworks.common.plugins.minetweaker3.handler.MixerHandler;
import toops.tsteelworks.common.plugins.minetweaker3.handler.SmeltingHandler;

public class MinetweakerPlugin extends ModCompatPlugin {
	@Override
	public String getModId() {
		return "MineTweaker3";
	}

	@Override
	public void preInit() {}

	@Override
	public void init() {
		MineTweakerAPI.registerClass(FuelHandler.class);
		MineTweakerAPI.registerClass(MixerHandler.class);
		MineTweakerAPI.registerClass(MixAgentHandler.class);
		MineTweakerAPI.registerClass(SmeltingHandler.class);
	}

	@Override
	public void postInit() {}

	public static FluidStack parseLiquid(ILiquidStack ls) {
		return FluidRegistry.getFluidStack(ls.getName(), ls.getAmount());
	}

	public static ItemStack parseItem(IItemStack is) {
		return (ItemStack) is.getInternal();
	}
	
	public static abstract class Remove<Key, Value> implements IUndoableAction {
		protected final Key key;
		protected Value removedData;

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
