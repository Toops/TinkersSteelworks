package tsteelworks.items.blocks;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class HighOvenItemBlock extends ItemBlock
{
    public static final String blockType[] = { "Controller", "Drain", "Brick", "Furnace", "Stone", 
        "Cobblestone", "Paver", "Brick.Cracked", "Road", "Brick.Fancy", "Brick.Square", "Brick.Creeper", "Duct" };

    public HighOvenItemBlock (int id)
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
        return (new StringBuilder()).append("HighOven.").append(blockType[pos]).toString();
    }

    @Override
    @SideOnly (Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        switch (stack.getItemDamage())
        {
            case 0:
                list.add(StatCollector.translateToLocal("highoven.controller.tooltip1"));
                list.add(StatCollector.translateToLocal("highoven.controller.tooltip2"));
                break;
            case 1:
                list.add(StatCollector.translateToLocal("highoven.drain.tooltip1"));
                list.add(StatCollector.translateToLocal("highoven.drain.tooltip2"));
                break;
            case 3:
                list.add(StatCollector.translateToLocal("highoven.furnace.tooltip"));
                break;
            case 12:
                list.add(StatCollector.translateToLocal("highoven.duct.tooltip1"));
                list.add(StatCollector.translateToLocal("highoven.duct.tooltip2"));
                break;
            default:
                list.add(StatCollector.translateToLocal("highoven.brick.tooltip1"));
                list.add(StatCollector.translateToLocal("highoven.brick.tooltip2"));
                break;
        }
    }
}
