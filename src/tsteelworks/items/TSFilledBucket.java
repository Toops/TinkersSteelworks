package tsteelworks.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidFinite;
import tconstruct.items.FilledBucket;
import tsteelworks.TSteelworks;
import tsteelworks.common.TSContent;
import tsteelworks.lib.TSteelworksRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TSFilledBucket extends FilledBucket
{
    public TSFilledBucket (int id)
    {
        super(id);
        setUnlocalizedName("tsteelworks.bucket");
        setContainerItem(Item.bucketEmpty);
        setCreativeTab(TSteelworksRegistry.SteelworksCreativeTab);
        setHasSubtypes(true);
    }

    @Override
    public boolean tryPlaceContainedLiquid (World world, int clickX, int clickY, int clickZ, int type)
    {
        if (!world.isAirBlock(clickX, clickY, clickZ) && world.getBlockMaterial(clickX, clickY, clickZ).isSolid())
            return false;
        else
        {
            try
            {
                int metadata = 0;
                if (TSContent.fluidBlocks[type] instanceof BlockFluidFinite)
                {
                    metadata = 7;
                }
                world.setBlock(clickX, clickY, clickZ, TSContent.fluidBlocks[type].blockID, metadata, 3);
            }
            catch (final ArrayIndexOutOfBoundsException ex)
            {
                TSteelworks.logger.warning("AIOBE occured when placing bucket into world; " + ex);
                return false;
            }
            return true;
        }
    }

    @SuppressWarnings (
    { "unchecked", "rawtypes" })
    @Override
    public void getSubItems (int id, CreativeTabs tab, List list)
    {
        for (int i = 0; i < icons.length; i++)
        {
            list.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    @SideOnly (Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        icons = new Icon[textureNames.length];
        for (int i = 0; i < icons.length; ++i)
        {
            icons[i] = iconRegister.registerIcon("tsteelworks:materials/bucket_" + textureNames[i]);
        }
    }

    @Override
    public String getUnlocalizedName (ItemStack stack)
    {
        final int arr = MathHelper.clamp_int(stack.getItemDamage(), 0, materialNames.length);
        return getUnlocalizedName() + "." + materialNames[arr];
    }

    public static final String[] materialNames = new String[]
                                               { "MonoatomicGold" };
    public static final String[] textureNames  = new String[]
                                               { "monoatomicgold" };
}
