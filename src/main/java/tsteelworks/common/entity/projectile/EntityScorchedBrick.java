package tsteelworks.common.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tconstruct.library.tools.AbilityHelper;
import tsteelworks.common.core.TSContent;

public class EntityScorchedBrick extends EntityBrick {
	public EntityScorchedBrick(World world) {
		super(world);

		setKnockbackStrength(3);
		setParticleEffect("scorchedbrick");
	}

	public EntityScorchedBrick(World world, double x, double y, double z) {
		super(world, x, y, z);

		setKnockbackStrength(3);
		setParticleEffect("scorchedbrick");
	}

	public EntityScorchedBrick(World world, EntityLivingBase entity) {
		super(world, entity);

		setKnockbackStrength(3);
		setParticleEffect("scorchedbrick");
	}

	@Override
	protected void dropOnImpact() {
		AbilityHelper.spawnItemAtEntity(this, new ItemStack(TSContent.materialsTS, 1, 0), 0);
	}
}
