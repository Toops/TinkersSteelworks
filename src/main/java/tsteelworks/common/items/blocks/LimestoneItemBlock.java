package tsteelworks.common.items.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

import java.util.List;

/**
 * @author Toops
 */
public class LimestoneItemBlock extends ItemBlock {
	public static final String blockType[] = {"Stone", "Cobblestone", "Brick", "Brick.Cracked", "Paver", "Road", "Brick.Fancy", "Brick.Square", "Brick.Creeper"};

	public LimestoneItemBlock(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add(StatCollector.translateToLocal("limestone.tooltip1"));
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		final int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, blockType.length - 1);
		return (new StringBuilder()).append("Limestone.").append(blockType[pos]).toString();
	}
}
