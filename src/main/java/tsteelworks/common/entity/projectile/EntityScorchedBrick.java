package tsteelworks.common.entity.projectile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tconstruct.library.tools.AbilityHelper;
import tsteelworks.client.core.TSClientProxy;
import tsteelworks.common.core.TSContent;

public class EntityScorchedBrick extends EntityBrick {
	public EntityScorchedBrick(World world) {
		super(world);

		setKnockbackStrength(3);
	}

	public EntityScorchedBrick(World world, double x, double y, double z) {
		super(world, x, y, z);

		setKnockbackStrength(3);
	}

	public EntityScorchedBrick(World world, EntityLivingBase entity) {
		super(world, entity);

		setKnockbackStrength(3);
	}

	@Override
	protected void dropOnImpact() {
		AbilityHelper.spawnItemAtEntity(this, new ItemStack(TSContent.materialsTS, 1, 0), 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getParticleID() {
		return TSClientProxy.PARTICLE_HANDLER.SCORCHED_BRICK_ID;
	}
}
