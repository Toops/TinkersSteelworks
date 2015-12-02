package toops.tsteelworks.common.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import toops.tsteelworks.common.core.TSContent;
import toops.tsteelworks.lib.TSRepo;

public class TSArmorBasic extends ItemArmor {
	public String textureName;

	public TSArmorBasic(ArmorMaterial par2EnumArmorMaterial, int par3, String textureName) {
		super(par2EnumArmorMaterial, 0, par3);
		setCreativeTab(TSContent.creativeTab);
		this.textureName = textureName;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return TSRepo.NAMESPACE + "textures/armor/" + textureName + "_" + (slot == 2 ? 2 : 1) + ".png";
	}

	@Override
	public void registerIcons(IIconRegister par1IconRegister) {
		final String[] armorTypes = {"helmet", "chestplate", "leggings", "boots"};
		itemIcon = par1IconRegister.registerIcon(TSRepo.NAMESPACE + "armor/" + textureName + "_" + armorTypes[armorType]);
	}
}
