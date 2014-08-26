package tsteelworks.common.plugins.tconstruct.world;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.util.config.PHConstruct;
import tconstruct.world.TinkerWorld;
import tsteelworks.common.plugins.ICompatPlugin;

public class TWorldCommonPlugin implements ICompatPlugin {
	@Override
	public String getPluginName() {
		return "TConstruct|World";
	}

	@Override
	public boolean mayLoad() {
		return PHConstruct.worldModule;
	}

	@Override
	public void preInit() {
		// * Dual registry for smelting (slag) purposes (we need the ore prefix)
		OreDictionary.registerOre("oreberryIron", new ItemStack(TinkerWorld.oreBerries, 1, 0));
		OreDictionary.registerOre("oreberryCopper", new ItemStack(TinkerWorld.oreBerries, 1, 2));
		OreDictionary.registerOre("oreberryTin", new ItemStack(TinkerWorld.oreBerries, 1, 3));
		OreDictionary.registerOre("oreberryAluminum", new ItemStack(TinkerWorld.oreBerries, 1, 4));
		OreDictionary.registerOre("oreberryAluminium", new ItemStack(TinkerWorld.oreBerries, 1, 4));
		OreDictionary.registerOre("oreberryEssence", new ItemStack(TinkerWorld.oreBerries, 1, 5));
	}

	@Override
	public void init() {

	}

	@Override
	public void postInit() {

	}
}
