package toops.tsteelworks.common.items.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

import java.util.List;
import java.util.Random;

public class DustStorageItemBlock extends ItemBlock {
	public static final String blockType[] = {"gunpowder", "sugar"};
	private static final Random rand = new Random();

	public DustStorageItemBlock(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({"unchecked"})
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		switch (stack.getItemDamage()) {
			case 0:
				list.add(StatCollector.translateToLocal("dustblock.gunpowder.tooltip"));
				break;
			case 1:
				list.add(StatCollector.translateToLocal("dustblock.sugar.tooltip"));
				break;
			default:
				list.add(StatCollector.translateToLocal("highoven.brick.tooltip2"));
				break;
		}
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		final int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, blockType.length - 1);
		return (new StringBuilder()).append("block.storage.dust.").append(blockType[pos]).toString();
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity) {
		if (!(entity instanceof EntityHorse) || itemstack.getItemDamage() != 1)
			return super.itemInteractionForEntity(itemstack, player, entity);

		EntityHorse horse = (EntityHorse) entity;

		// func_110256_cu returns undead horse types
		if (horse.func_110256_cu()) return super.itemInteractionForEntity(itemstack, player, entity);

		final float heal = 9.0F;
		final short grow = 60;
		final byte temper = 4;

		boolean affected = false;
		if (horse.getHealth() < horse.getMaxHealth()) {
			horse.heal(heal);
			affected = true;
		}

		if (!horse.isAdultHorse()) {
			horse.addGrowth(grow);
			affected = true;
		}

		if (!horse.isTame() && !affected && temper < horse.getMaxTemper()) {
			affected = true;
			horse.increaseTemper(temper);
		}

		if (affected) {
			horse.worldObj.playSoundAtEntity(horse, "eating", 1.0F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F);

			if (!player.capabilities.isCreativeMode)
				--itemstack.stackSize;
		}

		return affected;
	}
}
