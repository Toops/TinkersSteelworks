package toops.tsteelworks.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import toops.tsteelworks.lib.TSRepo;
import toops.tsteelworks.common.core.TSContent;

import java.util.List;

public class TSCraftingItem extends Item {
	public String[] textureNames;
	public String[] unlocalizedNames;
	public String folder;
	public IIcon[] icons;

	public TSCraftingItem(String[] names, String[] tex, String folder) {
		setCreativeTab(TSContent.creativeTab);

		setMaxDamage(0);
		setHasSubtypes(true);
		this.textureNames = tex;
		this.unlocalizedNames = names;
		this.folder = folder;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {
		final int arr = MathHelper.clamp_int(meta, 0, unlocalizedNames.length);
		return icons[arr];
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubItems(Item id, CreativeTabs tab, List list) {
		for (int i = 0; i < unlocalizedNames.length; i++)
			if (!(textureNames[i].equals("")))
				list.add(new ItemStack(id, 1, i));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		final int arr = MathHelper.clamp_int(stack.getItemDamage(), 0, unlocalizedNames.length);
		return getUnlocalizedName() + "." + unlocalizedNames[arr];
	}

	@Override
	public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		icons = new IIcon[textureNames.length];

		for (int i = 0; i < icons.length; ++i)
			if (!(textureNames[i].equals("")))
				icons[i] = iconRegister.registerIcon(TSRepo.NAMESPACE + folder + textureNames[i]);
	}
}
