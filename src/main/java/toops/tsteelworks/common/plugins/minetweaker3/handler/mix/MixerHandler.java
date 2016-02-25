package toops.tsteelworks.common.plugins.minetweaker3.handler.mix;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import toops.tsteelworks.api.highoven.IMixerRegistry;
import toops.tsteelworks.api.highoven.IMixerRegistry.IMixHolder;
import toops.tsteelworks.api.highoven.IMixerRegistry.IMixOutput;
import toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin;

import javax.annotation.Nullable;

class MixerHandler {
	static class Add extends MinetweakerPlugin.Add<IMixHolder, IMixOutput> {
		public Add(final Fluid input, final String ox, final String red, final String pur, final FluidStack outputLiquid, final ItemStack outputSolid) {
			super(new MixHolder(ox, red, pur, input), new IMixOutput() {
				@Nullable
				@Override
				public FluidStack getFluidOutput() {
					return outputLiquid;
				}

				@Nullable
				@Override
				public ItemStack getSolidOutput() {
					return outputSolid;
				}
			});
		}

		@Override
		public void apply() {
			oldData = IMixerRegistry.INSTANCE.registerMix(newData.getFluidOutput(), newData.getSolidOutput(), key.getInputFluid(), key.getOxidizer(), key.getReducer(), key.getPurifier());
		}

		@Override
		public void undo() {
			if (oldData == null)
				IMixerRegistry.INSTANCE.removeMix(key.getInputFluid(), key.getOxidizer(), key.getReducer(), key.getPurifier());
			else {
				IMixerRegistry.INSTANCE.registerMix(oldData.getFluidOutput(), oldData.getSolidOutput(), key.getInputFluid(), key.getOxidizer(), key.getReducer(), key.getPurifier());
			}
		}

		@Override
		public String describe() {
			return (oldData == null ? "Added " : "Replaced ") + key.toString() + " as valid mix.";
		}
	}

	static class Remove extends MinetweakerPlugin.Remove<IMixHolder, IMixOutput> {
		public Remove(Fluid input, String ox, String red, String pur) {
			super(new MixHolder(ox, red, pur, input));
		}

		@Override
		public void apply() {
			oldData = IMixerRegistry.INSTANCE.removeMix(key.getInputFluid(), key.getOxidizer(), key.getReducer(), key.getPurifier());
		}

		@Override
		public void undo() {
			if (oldData == null) return;

			IMixerRegistry.INSTANCE.registerMix(oldData.getFluidOutput(), oldData.getSolidOutput(), key.getInputFluid(), key.getOxidizer(), key.getReducer(), key.getPurifier());
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
