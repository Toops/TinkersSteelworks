package toops.tsteelworks.common.plugins.tconstruct;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.ShapedOreRecipe;
import nf.fr.ephys.cookiecore.helpers.RegistryHelper;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.Detailing;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.modifiers.tools.ModInteger;
import tconstruct.tools.TinkerTools;
import toops.tsteelworks.TSteelworks;
import toops.tsteelworks.common.core.ConfigCore;
import toops.tsteelworks.common.core.TSContent;
import toops.tsteelworks.common.modifier.TSActiveOmniMod;

class TCTools {
	public void preInit() {
		ItemStack hopper = new ItemStack(Blocks.hopper);
		ItemStack enderpearl = new ItemStack(Items.ender_pearl);
		String modifierName = StatCollector.translateToLocal("modifier.tool.vacuous");

		ModifyBuilder.registerModifier(new ModInteger(new ItemStack[] { hopper, enderpearl }, 50, "Vacuous", 5, EnumChatFormatting.GREEN.toString(), modifierName));

		TConstructRegistry.registerActiveToolMod(new TSActiveOmniMod());

		changePiston();
		registerChisel();
	}

	private void registerChisel() {
		// Chiseling
		final Detailing chiseling = TConstructRegistry.getChiselDetailing();
		chiseling.addDetailing(TSContent.highoven, 4, TSContent.highoven, 6, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.highoven, 6, TSContent.highoven, 11, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.highoven, 11, TSContent.highoven, 2, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.highoven, 2, TSContent.highoven, 8, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.highoven, 8, TSContent.highoven, 9, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.highoven, 9, TSContent.highoven, 10, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.highoven, 10, TSContent.highoven, 4, TinkerTools.chisel);

		chiseling.addDetailing(TSContent.limestoneBlock, 2, TSContent.limestoneBlock, 3, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.limestoneBlock, 3, TSContent.limestoneBlock, 4, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.limestoneBlock, 4, TSContent.limestoneBlock, 5, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.limestoneBlock, 5, TSContent.limestoneBlock, 6, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.limestoneBlock, 6, TSContent.limestoneBlock, 7, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.limestoneBlock, 7, TSContent.limestoneBlock, 8, TinkerTools.chisel);
		chiseling.addDetailing(TSContent.limestoneBlock, 8, TSContent.limestoneBlock, 3, TinkerTools.chisel);
	}

	private void changePiston() {
		if (!ConfigCore.hardcorePiston) return;

		final ItemStack rod = new ItemStack(TinkerTools.toughRod, 1, 2);

		ItemStack pison = new ItemStack(Blocks.piston);
		RegistryHelper.removeItemRecipe(pison);
		GameRegistry.addRecipe(new ShapedOreRecipe(pison, "WWW", "CTC", "CRC", 'C', "cobblestone", 'T', rod, 'R', "dustRedstone", 'W', "plankWood"));
	}
}
