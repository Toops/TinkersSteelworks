package tsteelworks.items.blocks;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DustStorageItemBlock extends ItemBlock
{
    public static final String blockType[] = { "gunpowder", "sugar" };

    public DustStorageItemBlock(int id)
    {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @SuppressWarnings ({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        switch (stack.getItemDamage())
        {
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
    public int getMetadata (int meta)
    {
        return meta;
    }

    @Override
    public String getUnlocalizedName (ItemStack itemstack)
    {
        final int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, blockType.length - 1);
        return (new StringBuilder()).append("block.storage.dust.").append(blockType[pos]).toString();
    }
}
