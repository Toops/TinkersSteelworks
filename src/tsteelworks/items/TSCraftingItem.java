package tsteelworks.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import tsteelworks.lib.Repo;
import tsteelworks.lib.TSteelworksRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TSCraftingItem extends Item
{
    public String[] textureNames;
    public String[] unlocalizedNames;
    public String folder;
    public Icon[] icons;

    public TSCraftingItem(int id, String[] names, String[] tex, String folder)
    {
        super(id);
        setCreativeTab(TSteelworksRegistry.SteelworksCreativeTab);
        setMaxDamage(0);
        setHasSubtypes(true);
        textureNames = tex;
        unlocalizedNames = names;
        this.folder = folder;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamage (int meta)
    {
        final int arr = MathHelper.clamp_int(meta, 0, unlocalizedNames.length);
        return icons[arr];
    }

    @SuppressWarnings ({ "rawtypes", "unchecked" })
    @Override
    public void getSubItems (int id, CreativeTabs tab, List list)
    {
        for (int i = 0; i < unlocalizedNames.length; i++)
            if (!(textureNames[i].equals("")))
                list.add(new ItemStack(id, 1, i));
    }

    @Override
    public String getUnlocalizedName (ItemStack stack)
    {
        final int arr = MathHelper.clamp_int(stack.getItemDamage(), 0, unlocalizedNames.length);
        return getUnlocalizedName() + "." + unlocalizedNames[arr];
    }

    @Override
    public void onCreated (ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        icons = new Icon[textureNames.length];
        for (int i = 0; i < icons.length; ++i)
            if (!(textureNames[i].equals("")))
                icons[i] = iconRegister.registerIcon(Repo.textureDir + folder + textureNames[i]);
    }
}
