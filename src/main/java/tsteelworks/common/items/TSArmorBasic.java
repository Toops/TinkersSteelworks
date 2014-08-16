package tsteelworks.common.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import tsteelworks.common.core.TSContent;
import tsteelworks.lib.TSRepo;

public class TSArmorBasic extends ItemArmor {
	public String textureName;

	public TSArmorBasic(ArmorMaterial par2EnumArmorMaterial, int par3, String textureName) {
		super(par2EnumArmorMaterial, 0, par3);
		setCreativeTab(TSContent.creativeTab);
		this.textureName = textureName;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return TSRepo.TEXTURE_DIR + "textures/armor/" + textureName + "_" + type + ".png";
	}

	@Override
	public void registerIcons(IIconRegister par1IconRegister) {
		itemIcon = par1IconRegister.registerIcon(TSRepo.TEXTURE_DIR + "armor/" + textureName + "_" + (armorType == 0 ? "helmet" : armorType == 1 ? "chestplate" : armorType == 2 ? "leggings" : armorType == 3 ? "boots" : "helmet"));
	}
}
