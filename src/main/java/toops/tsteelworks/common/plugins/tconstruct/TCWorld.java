package toops.tsteelworks.common.plugins.tconstruct;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.world.TinkerWorld;

class TCWorld {
	public void preInit() {
		OreDictionary.registerOre("oreberryEssence", new ItemStack(TinkerWorld.oreBerries, 1, 5));
	}
}
