package tsteelworks.common.entity.projectile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tconstruct.library.tools.AbilityHelper;
import tsteelworks.client.core.TSClientProxy;
import tsteelworks.common.core.TSContent;

public class EntityLimestoneBrick extends EntityBrick {
	public EntityLimestoneBrick(World world) {
		super(world);

		setKnockbackStrength(1);
	}

	public EntityLimestoneBrick(World world, double x, double y, double z) {
		super(world, x, y, z);

		setKnockbackStrength(1);
	}

	public EntityLimestoneBrick(World world, EntityLivingBase entity) {
		super(world, entity);

		setKnockbackStrength(1);
	}

	@Override
	protected void dropOnImpact() {
		AbilityHelper.spawnItemAtEntity(this, new ItemStack(TSContent.materialsTS, 1, 2), 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getParticleID() {
		return TSClientProxy.PARTICLE_HANDLER.LIMESTONE_BRICK_ID;
	}
}
