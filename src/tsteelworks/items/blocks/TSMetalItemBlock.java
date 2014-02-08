package tsteelworks.items.blocks;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import tconstruct.items.blocks.MetalItemBlock;

public class TSMetalItemBlock extends MetalItemBlock
{
    public static final String blockType[] = { "MonoatomicGold" };

    public TSMetalItemBlock (int id)
    {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata (int meta)
    {
        return meta;
    }

    @Override
    public String getUnlocalizedName (ItemStack itemstack)
    {
        final int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, blockType.length - 1);
        return (new StringBuilder()).append("StorageMetals.").append(blockType[pos]).toString();
    }

    @Override
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        list.add(StatCollector.translateToLocal("metalblock.tooltip"));
    }
}
