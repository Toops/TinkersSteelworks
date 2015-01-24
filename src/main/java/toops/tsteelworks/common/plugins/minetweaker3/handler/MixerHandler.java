package toops.tsteelworks.common.plugins.minetweaker3.handler;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import toops.tsteelworks.api.highoven.IMixerRegistry;

import static toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin.parseItem;
import static toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin.parseLiquid;

@ZenClass("mods.tsteelworks.mix")
public class MixerHandler {
	@ZenMethod
	public static void addFluidMix(ILiquidStack input, String oxidizer, String purifier, String reducer, ILiquidStack output) {
		MineTweakerAPI.apply(new Add(parseLiquid(input).getFluid(), oxidizer, purifier, reducer, parseLiquid(output)));
	}

	public static void addSolidMix(ILiquidStack input, String oxidizer, String purifier, String reducer, IItemStack output) {
		MineTweakerAPI.apply(new Add(parseLiquid(input).getFluid(), oxidizer, purifier, reducer, parseItem(output)));
	}

	@ZenMethod
	public static void removeMix(ILiquidStack input, String oxidizer, String purifier, String reducer) {
		MineTweakerAPI.apply(new Remove(parseLiquid(input).getFluid(), oxidizer, purifier, reducer));
	}

	private static class Add implements IUndoableAction {
		private final Fluid input;
		private final String ox;
		private final String pur;
		private final String red;
		private final Object output;

		public Add(Fluid input, String ox, String pur, String red, Object output) {
			this.input = input;
			this.ox = ox;
			this.pur = pur;
			this.red = red;
			this.output = output;
		}

		@Override
		public void apply() {
			if (output instanceof ItemStack)
				IMixerRegistry.INSTANCE.registerMix((ItemStack) output, input, ox, pur, red);
			else
				IMixerRegistry.INSTANCE.registerMix((FluidStack) output, input, ox, pur, red);
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void undo() {
			IMixerRegistry.INSTANCE.removeMix(input, ox, pur, red);
		}

		@Override
		public String describe() {
			return "Added [" + input.getName() + ", " + ox + ", " + pur + ", " + red + "] as valid mix.";
		}

		@Override
		public String describeUndo() {
			return "Removed [" + input.getName() + ", " + ox + ", " + pur + ", " + red + "] as valid mix.";
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}

	private static class Remove implements IUndoableAction {
		private final Fluid input;
		private final String ox;
		private final String pur;
		private final String red;
		private Object output;

		public Remove(Fluid input, String ox, String pur, String red) {
			this.input = input;
			this.ox = ox;
			this.pur = pur;
			this.red = red;
		}

		@Override
		public void apply() {
			output = IMixerRegistry.INSTANCE.removeMix(input, ox, pur, red);
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void undo() {
			if (output instanceof ItemStack)
				IMixerRegistry.INSTANCE.registerMix((ItemStack) output, input, ox, pur, red);
			else
				IMixerRegistry.INSTANCE.registerMix((FluidStack) output, input, ox, pur, red);
		}

		@Override
		public String describeUndo() {
			return "Added [" + input.getName() + ", " + ox + ", " + pur + ", " + red + "] as valid mix.";
		}

		@Override
		public String describe() {
			return "Removed [" + input.getName() + ", " + ox + ", " + pur + ", " + red + "] as valid mix.";
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}
}
