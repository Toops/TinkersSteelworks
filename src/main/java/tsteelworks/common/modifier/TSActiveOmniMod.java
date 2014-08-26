package tsteelworks.common.modifier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import nf.fr.ephys.cookiecore.helpers.EntityHelper;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.tools.ToolCore;

public class TSActiveOmniMod extends ActiveToolMod {
	@Override
	public void updateTool(ToolCore tool, ItemStack stack, World world, Entity entity) {
		if (!(entity instanceof EntityPlayer) || ((EntityPlayer) entity).getHeldItem() != stack)
			return;

		NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");

		if (tags.hasKey("Vacuous"))
			EntityHelper.drawItemsToEntity((EntityLivingBase) entity, tags.getInteger("Vacuous") + 1);
	}
}
