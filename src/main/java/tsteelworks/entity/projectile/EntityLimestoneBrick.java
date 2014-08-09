package tsteelworks.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import tconstruct.common.TContent;
import tconstruct.library.tools.AbilityHelper;
import tsteelworks.TSteelworks;
import tsteelworks.common.core.TSContent;

public class EntityLimestoneBrick extends EntityThrowable
{
    private final int knockbackStrength = 1;
    private final String particleEffect = "limestonebrick";

    public EntityLimestoneBrick(World world)
    {
        super(world);
    }

    public EntityLimestoneBrick(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityLimestoneBrick(World world, EntityLivingBase entity)
    {
        super(world, entity);
    }

    public boolean canBreakBlock (int blockid)
    {
        return ((blockid == Block.glass.blockID) || (blockid == Block.glowStone.blockID) || (blockid == Block.thinGlass.blockID) || (blockid == TContent.clearGlass.blockID)
                || (blockid == TContent.glassPane.blockID) || (blockid == TContent.stainedGlassClear.blockID) || (blockid == TContent.stainedGlassClearPane.blockID));
    }

    private void doBreakParticleFX ()
    {
        final int i = 2;
        for (int j = 0; j < (i * 4); ++j)
        {
            final float f = rand.nextFloat() * (float) Math.PI * 2.0F;
            final float offset = (rand.nextFloat() * 0.1F) + 0.1F;
            final float xPos = MathHelper.sin(f) * i * 0.5F * offset;
            final float zPos = MathHelper.cos(f) * i * 0.5F * offset;
            TSteelworks.proxy.spawnParticle(particleEffect, posX + xPos, boundingBox.minY, posZ + zPos, 0.0D, 0.0D, 0.0D);
        }
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    @Override
    protected void onImpact (MovingObjectPosition targetentity)
    {
        if (targetentity.entityHit != null)
        {
            byte dmg = 1;

            if (targetentity.entityHit instanceof EntityCreeper)
                dmg = 4;

            final float f3 = MathHelper.sqrt_double((motionX * motionX) + (motionZ * motionZ));
            targetentity.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), dmg);
            if (f3 > 0.0F)
                targetentity.entityHit.addVelocity((motionX * knockbackStrength * 0.6000000238418579D) / f3, 0.1D, (motionZ * knockbackStrength * 0.6000000238418579D) / f3);
        }

        doBreakParticleFX();

        if (targetentity != null)
        {
            final int testBlockID = worldObj.getBlockId(targetentity.blockX, targetentity.blockY, targetentity.blockZ);
            if (canBreakBlock(testBlockID))
                worldObj.destroyBlock(targetentity.blockX, targetentity.blockY, targetentity.blockZ, true);
        }

        if (!worldObj.isRemote)
        {
            // lol, let's use this :P
            AbilityHelper.spawnItemAtEntity(this, new ItemStack(TSContent.materialsTS, 1, 2), 0);
            setDead();
        }
    }
}
