package tsteelworks.common.modifier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.tools.ToolCore;
import tsteelworks.lib.TSAbilityHelper;

public class TSActiveOmniMod extends ActiveToolMod {
	// todo: change draw distance depending on tool attributes ?
	@Override
	public void updateTool(ToolCore tool, ItemStack stack, World world, Entity entity) {
		NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");

		if (tags.hasKey("Vacuous"))
			TSAbilityHelper.drawItemsToEntity(world, (EntityLivingBase) entity, tags.getInteger("Vacuous"));
	}
}
