package tsteelworks.items.blocks;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SteelforgeItemBlock extends ItemBlock {
	public static final String blockType[] = { "HighOven", "Drain", "Brick" };

	public SteelforgeItemBlock(int id) {
		super(id);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	public int getMetadata(int meta) {
		return meta;
	}

	public String getUnlocalizedName(ItemStack itemstack) {
		int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0,
				blockType.length - 1);
		return (new StringBuilder()).append("Steelforge.")
				.append(blockType[pos]).toString();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list,
			boolean par4) {
		switch (stack.getItemDamage()) {
		case 0:
			list.add(StatCollector.translateToLocal("steelforge.highoven.tooltip"));
			break;
        case 1:
            list.add(StatCollector.translateToLocal("steelforge.drain.tooltip1"));
            list.add(StatCollector.translateToLocal("steelforge.drain.tooltip2"));
            break;
        case 2:
			list.add(StatCollector.translateToLocal("steelforge.brick.tooltip1"));
			list.add(StatCollector.translateToLocal("steelforge.brick.tooltip2"));
			break;
		default:
			break;
		}
	}
}
