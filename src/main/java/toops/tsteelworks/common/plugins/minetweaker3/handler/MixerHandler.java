package toops.tsteelworks.common.plugins.minetweaker3.handler;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import toops.tsteelworks.api.highoven.IMixerRegistry;
import toops.tsteelworks.api.highoven.IMixerRegistry.IMixHolder;
import toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin;

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

	private static class Add extends MinetweakerPlugin.Add<IMixHolder, Object> {
		public Add(final Fluid input, final String ox, final String pur, final String red, final Object output) {
			super(new MixHolder(ox, red, pur, input), output);
		}

		@Override
		public void apply() {
			if (newData instanceof ItemStack)
				oldData = IMixerRegistry.INSTANCE.registerMix((ItemStack) newData, key.getInputFluid(), key.getOxidizer(), key.getReducer(), key.getPurifier());
			else
				oldData = IMixerRegistry.INSTANCE.registerMix((FluidStack) newData, key.getInputFluid(), key.getOxidizer(), key.getReducer(), key.getPurifier());
		}

		@Override
		public void undo() {
			if (oldData == null)
				IMixerRegistry.INSTANCE.removeMix(key.getInputFluid(), key.getOxidizer(), key.getReducer(), key.getPurifier());
			else {
				if (oldData instanceof ItemStack)
					IMixerRegistry.INSTANCE.registerMix((ItemStack) oldData, key.getInputFluid(), key.getOxidizer(), key.getReducer(), key.getPurifier());
				else
					IMixerRegistry.INSTANCE.registerMix((FluidStack) oldData, key.getInputFluid(), key.getOxidizer(), key.getReducer(), key.getPurifier());
			}
		}

		@Override
		public String describe() {
			return (oldData == null ? "Added " : "Replaced ") + key.toString() + " as valid mix.";
		}
	}

	private static class Remove extends MinetweakerPlugin.Remove<IMixHolder, Object> {
		public Remove(Fluid input, String ox, String pur, String red) {
			super(new MixHolder(ox, red, pur, input));
		}

		@Override
		public void apply() {
			oldData = IMixerRegistry.INSTANCE.removeMix(key.getInputFluid(), key.getOxidizer(), key.getReducer(), key.getPurifier());
		}

		@Override
		public void undo() {
			if (oldData == null) return;
			
			if (oldData instanceof ItemStack)
				IMixerRegistry.INSTANCE.registerMix((ItemStack) oldData, key.getInputFluid(), key.getOxidizer(), key.getReducer(), key.getPurifier());
			else
				IMixerRegistry.INSTANCE.registerMix((FluidStack) oldData, key.getInputFluid(), key.getOxidizer(), key.getReducer(), key.getPurifier());
		}

		@Override
		public String describe() {
			return "Removed " + key.toString() + " as valid mix.";
		}
	}

	private static class MixHolder implements IMixHolder {
		private final String ox;
		private final String red;
		private final String pur;
		private final Fluid input;

		public MixHolder(String ox, String red, String pur, Fluid input) {
			this.ox = ox;
			this.red = red;
			this.pur = pur;
			this.input = input;
		}

		@Override
		public String getOxidizer() {
			return ox;
		}

		@Override
		public String getReducer() {
			return red;
		}

		@Override
		public String getPurifier() {
			return pur;
		}

		@Override
		public Fluid getInputFluid() {
			return input;
		}

		@Override
		public String toString() {
			return "[" + input.getName() + ", " + ox + ", " + pur + ", " + red + "]";
		}
	}
}
