package toops.tsteelworks.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import toops.tsteelworks.TSteelworks;
import toops.tsteelworks.common.core.GuiHandler;

import java.util.List;

public class TSManual extends TSCraftingItem {
	static String[] name = new String[]{"highoven"};
	static String[] textureName = new String[]{"tinkerbook_highoven"};

	public TSManual() {
		super(name, textureName, "");

		setUnlocalizedName("tsteelworks.manual");
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({"unchecked"})
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		switch (stack.getItemDamage()) {
			case 0:
				list.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("manual.steelworks.tooltip1"));
				break;
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		player.openGui(TSteelworks.instance, GuiHandler.MANUAL_GUI_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);

		return stack;
	}
}
