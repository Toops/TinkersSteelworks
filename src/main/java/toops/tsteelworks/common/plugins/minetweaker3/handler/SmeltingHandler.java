package toops.tsteelworks.common.plugins.minetweaker3.handler;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import toops.tsteelworks.api.highoven.ISmeltingRegistry;
import toops.tsteelworks.api.highoven.ISmeltingRegistry.IMeltData;
import toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin;

import static toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin.parseItem;
import static toops.tsteelworks.common.plugins.minetweaker3.MinetweakerPlugin.parseLiquid;

@ZenClass("mods.tsteelworks.highoven")
public class SmeltingHandler {
	@ZenMethod
	public static void addMeltable(IItemStack meltable, final boolean isOre, final ILiquidStack output, final int meltTemp) {
		MineTweakerAPI.apply(new Add(parseItem(meltable), new IMeltData() {
			@Override
			public int getMeltingPoint() {
				return meltTemp;
			}

			@Override
			public FluidStack getResult() {
				return parseLiquid(output);
			}

			@Override
			public boolean isOre() {
				return isOre;
			}
		}));
	}

	@ZenMethod
	public static void removeMeltable(IItemStack meltable) {
		MineTweakerAPI.apply(new Remove(parseItem(meltable)));
	}

	private static class Add extends MinetweakerPlugin.Add<ItemStack, IMeltData> {
		public Add(ItemStack meltable, IMeltData data) {
			super(meltable, data);
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

	private static class Remove extends MinetweakerPlugin.Remove<ItemStack, IMeltData> {
		public Remove(ItemStack stack) {
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
