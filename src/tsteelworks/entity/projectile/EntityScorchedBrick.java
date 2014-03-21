package tsteelworks.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import tconstruct.common.TContent;
import tsteelworks.TSteelworks;

public class EntityScorchedBrick extends EntityThrowable
{
    private final int knockbackStrength = 3;
    private final String particleEffect = "scorchedbrick";

    public EntityScorchedBrick(World par1World)
    {
        super(par1World);
    }

    public EntityScorchedBrick(World par1World, double par2, double par4, double par6)
    {
        super(par1World, par2, par4, par6);
    }

    public EntityScorchedBrick(World par1World, EntityLivingBase par2EntityLivingBase)
    {
        super(par1World, par2EntityLivingBase);
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

            if (targetentity.entityHit instanceof EntitySnowman)
                dmg = 3;

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
            setDead();
    }
}
