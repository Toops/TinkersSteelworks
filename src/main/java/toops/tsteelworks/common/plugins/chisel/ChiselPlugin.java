package toops.tsteelworks.common.plugins.chisel;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.block.Block;
import toops.tsteelworks.common.blocks.HighOvenBlock;
import toops.tsteelworks.common.core.TSContent;
import toops.tsteelworks.common.plugins.ModCompatPlugin;

public class ChiselPlugin extends ModCompatPlugin {

	@Override
	public String getModId() {
		return "chisel";
	}

	@Override
	public void preInit() {}

	@Override
	public void init() {
		final String scorchedGroup = "scorchedstone";

		addBlockVariation(scorchedGroup, TSContent.highoven, HighOvenBlock.META_BRICK);
		addBlockVariation(scorchedGroup, TSContent.highoven, HighOvenBlock.META_CRACKED);
		addBlockVariation(scorchedGroup, TSContent.highoven, HighOvenBlock.META_STONE);
		addBlockVariation(scorchedGroup, TSContent.highoven, HighOvenBlock.META_PAVER);
		addBlockVariation(scorchedGroup, TSContent.highoven, HighOvenBlock.META_CRACKED);
		addBlockVariation(scorchedGroup, TSContent.highoven, HighOvenBlock.META_ROAD);
		addBlockVariation(scorchedGroup, TSContent.highoven, HighOvenBlock.META_FANCY);
		addBlockVariation(scorchedGroup, TSContent.highoven, HighOvenBlock.META_CHISELED);
		addBlockVariation(scorchedGroup, TSContent.highoven, HighOvenBlock.META_CREEPY);
		//addBlockVariation(scorchedGroup, TSContent.highoven, HighOvenBlock.META_COBBLE);
	}

	@Override
	public void postInit() {
	}

	private void addBlockVariation(String group, Block block, int metadata) {
		System.out.println(group + "|" + Block.blockRegistry.getNameForObject(block) + "|" + metadata);
		FMLInterModComms.sendMessage(getModId(),
				"variation:add", group + "|" + Block.blockRegistry.getNameForObject(block) + "|" + metadata);
	}
}
