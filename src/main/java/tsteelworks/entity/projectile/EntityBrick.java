package tsteelworks.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.smeltery.TinkerSmeltery;
import tsteelworks.TSteelworks;
import tsteelworks.common.core.TSContent;

public class EntityBrick extends EntityThrowable {
	public static final Block[] BREAKABLE_DEFAULT = new Block[] {
			Blocks.glass, Blocks.glass_pane, Blocks.stained_glass, Blocks.stained_glass_pane, Blocks.glowstone,
			TinkerSmeltery.glassPane, TinkerSmeltery.clearGlass, TinkerSmeltery.stainedGlassClear, TinkerSmeltery.stainedGlassClearPane
	};

	private int knockbackStrength = 1;
	private String particleEffect = null;
	private Block[] breakables = BREAKABLE_DEFAULT;

	public EntityBrick(World world) {
		super(world);
	}

	public EntityBrick(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityBrick(World world, EntityLivingBase entity) {
		super(world, entity);
	}

	public void setKnockbackStrength(int strength) {
		this.knockbackStrength = strength;
	}

	public void setParticleEffect(String effect) {
		this.particleEffect = effect;
	}

	public void setBreakableList(Block[] list) {
		this.breakables = list;
	}

	public boolean canBreakBlock(Block block) {
		if (breakables == null) return false;

		for (Block breakable : breakables) {
			if (breakable.equals(block)) return true;
		}

		return false;
	}

	public void doBreakParticleFX() {
		if (!worldObj.isRemote || particleEffect == null) return;

		final int i = 2;
		for (int j = 0; j < (i * 4); ++j) {
			final float f = rand.nextFloat() * (float) Math.PI * 2.0F;
			final float offset = (rand.nextFloat() * 0.1F) + 0.1F;
			final float xPos = MathHelper.sin(f) * i * 0.5F * offset;
			final float zPos = MathHelper.cos(f) * i * 0.5F * offset;

			worldObj.spawnParticle(particleEffect, posX + xPos, boundingBox.minY, posZ + zPos, 0.0D, 0.0D, 0.0D);
		}
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	@Override
	protected void onImpact(MovingObjectPosition targetentity) {
		if (targetentity.entityHit != null) {
			final int dmg = targetentity.entityHit instanceof EntityCreeper ? 4 : 1;
			targetentity.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), dmg);

			final float speed = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
			targetentity.entityHit.addVelocity((motionX * knockbackStrength * 0.6000000238418579D) / speed, 0.1D, (motionZ * knockbackStrength * 0.6000000238418579D) / speed);
		}

		doBreakParticleFX();

		if (canBreakBlock(worldObj.getBlock(targetentity.blockX, targetentity.blockY, targetentity.blockZ)))
			worldObj.destroyBlockInWorldPartially(getEntityId(), targetentity.blockX, targetentity.blockY, targetentity.blockZ, 10); // todo: check this method

		if (!worldObj.isRemote)
			AbilityHelper.spawnItemAtEntity(this, new ItemStack(TSContent.materialsTS, 1, 2), 0);

		setDead();
	}
}
