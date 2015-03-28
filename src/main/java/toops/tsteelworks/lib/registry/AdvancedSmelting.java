package toops.tsteelworks.lib.registry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import nf.fr.ephys.cookiecore.util.HashedItemStack;
import toops.tsteelworks.api.highoven.ISmeltingRegistry;
import toops.tsteelworks.common.blocks.logic.HighOvenLogic;
import toops.tsteelworks.common.core.ConfigCore;

import java.util.HashMap;
import java.util.Map;

class AdvancedSmelting extends BasicRegistry<ItemStack, ISmeltingRegistry.IMeltData> implements ISmeltingRegistry {
/* ========== ISmeltingRegistry  ========== */
	/** list of meltables items & blocks mapped to their result (fluidstack, melting point, etc) */
	private final Map<HashedItemStack, IMeltData> meltingList = new HashMap<>();

	@Override
	public void addDictionaryMeltable(final String inputOre, final FluidStack output, final int meltTemperature) {
		final boolean isOre = inputOre.startsWith("ore");

		for (final ItemStack stack : OreDictionary.getOres(inputOre))
			addMeltable(stack, isOre, output, meltTemperature);
	}

	@Override
	public IMeltData addMeltable(ItemStack input, boolean isOre, FluidStack output, int meltTemperature) {
		if (meltTemperature == HighOvenLogic.ROOM_TEMP)
			meltTemperature = HighOvenLogic.ROOM_TEMP + 1; // ugly hack o/

		IMeltData newData = new MeltData(meltTemperature, output, isOre);
		IMeltData oldData = meltingList.put(new HashedItemStack(input), newData);

		if (oldData != null) dispatchDeleteEvent(input, oldData);
		dispatchAddEvent(input, newData);

		return oldData;
	}

	@Override
	public IMeltData getMeltable(ItemStack stack) {
		return meltingList.get(new HashedItemStack(stack));
	}

	@Override
	public IMeltData removeMeltable(ItemStack stack) {
		IMeltData oldData = meltingList.remove(new HashedItemStack(stack));

		if (oldData != null) dispatchDeleteEvent(stack, oldData);

		return oldData;
	}

	private static final class MeltData implements IMeltData {
		private final int meltingPoint;
		private final FluidStack result;
		private final boolean isOre;

		public MeltData(int meltingPoint, FluidStack result, boolean isOre) {
			this.meltingPoint = meltingPoint;
			this.result = result;
			this.isOre = isOre;
		}

		@Override
		public int getMeltingPoint() {
			return meltingPoint;
		}

		@Override
		public FluidStack getResult() {
			return result;
		}

		@Override
		public boolean isOre() {
			return isOre;
		}
	}
}