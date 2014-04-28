package tsteelworks.modifiers.tools;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.tools.ToolCore;
import tsteelworks.lib.TSAbilityHelper;

public class TSActiveOmniMod extends ActiveToolMod
{
    Random random = new Random();

    /* Updating */
    @Override
    public void updateTool (ToolCore tool, ItemStack stack, World world, Entity entity)
    {
        // TODO: Move this off of update tool? Seems unnecessary here...
        if (!world.isRemote && entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isSwingInProgress && stack.getTagCompound() != null)
        {
            if (((EntityLivingBase)entity).getHeldItem() == null || !(((EntityLivingBase)entity).getHeldItem().equals(stack))) return;
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (tags.hasKey("Vacuous"))
                TSAbilityHelper.drawItemsToEntity(world, (EntityLivingBase)entity, tags.getInteger("Vacuous"));
        }
    }
}
