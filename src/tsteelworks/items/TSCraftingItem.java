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
import tsteelworks.lib.TSteelworksRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TSCraftingItem extends Item {
	public String[] textureNames;
	public String[] unlocalizedNames;
	public String folder;
	public Icon[] icons;

	public TSCraftingItem(int id, String[] names, String[] tex,
			String folder) {
		super(id);
		this.setCreativeTab(TSteelworksRegistry.Steelforge);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.textureNames = tex;
		this.unlocalizedNames = names;
		this.folder = folder;
	}

	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamage(int meta) {
		int arr = MathHelper.clamp_int(meta, 0, unlocalizedNames.length);
		return icons[arr];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.icons = new Icon[textureNames.length];

		for (int i = 0; i < this.icons.length; ++i) {
			if (!(textureNames[i].equals("")))
				this.icons[i] = iconRegister.registerIcon("tsteelworks:"
						+ folder + textureNames[i]);
		}
	}

	public String getUnlocalizedName(ItemStack stack) {
		int arr = MathHelper.clamp_int(stack.getItemDamage(), 0,
				unlocalizedNames.length);
		return getUnlocalizedName() + "." + unlocalizedNames[arr];
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getSubItems(int id, CreativeTabs tab, List list) {
		for (int i = 0; i < unlocalizedNames.length; i++)
			if (!(textureNames[i].equals("")))
				list.add(new ItemStack(id, 1, i));
	}

	@Override
	public void onCreated(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		// if(par1ItemStack.itemID == TContent.blankPattern.itemID){
		// par3EntityPlayer.addStat(TAchievements.achievements.get("tconstruct.pattern"),
		// 1);
		// }
	}
}
