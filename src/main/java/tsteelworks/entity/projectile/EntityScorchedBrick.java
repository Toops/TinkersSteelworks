package tsteelworks.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.world.World;

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
}
