package tsteelworks.common.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tconstruct.library.tools.AbilityHelper;
import tsteelworks.common.core.TSContent;

public class EntityLimestoneBrick extends EntityBrick {
	public EntityLimestoneBrick(World world) {
		super(world);

		setKnockbackStrength(1);
		setParticleEffect("limestonebrick");
	}

	public EntityLimestoneBrick(World world, double x, double y, double z) {
		super(world, x, y, z);

		setKnockbackStrength(1);
		setParticleEffect("limestonebrick");
	}

	public EntityLimestoneBrick(World world, EntityLivingBase entity) {
		super(world, entity);

		setKnockbackStrength(1);
		setParticleEffect("limestonebrick");
	}

	@Override
	protected void dropOnImpact() {
		AbilityHelper.spawnItemAtEntity(this, new ItemStack(TSContent.materialsTS, 1, 2), 0);
	}
}
