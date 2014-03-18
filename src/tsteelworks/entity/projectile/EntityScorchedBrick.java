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
    private int knockbackStrength = 3;
    private String particleEffect = "scorchedbrick";
    
    public EntityScorchedBrick(World par1World)
    {
        super(par1World);
    }

    public EntityScorchedBrick(World par1World, EntityLivingBase par2EntityLivingBase)
    {
        super(par1World, par2EntityLivingBase);
    }

    public EntityScorchedBrick(World par1World, double par2, double par4, double par6)
    {
        super(par1World, par2, par4, par6);
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(MovingObjectPosition targetentity)
    {
        if (targetentity.entityHit != null)
        {
            byte dmg = 1;

            if (targetentity.entityHit instanceof EntitySnowman)
            {
                dmg = 3;
            }
            
            float f3 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            targetentity.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), (float)dmg);
            if (f3 > 0.0F)
                targetentity.entityHit.addVelocity(this.motionX * (double)knockbackStrength * 0.6000000238418579D / (double)f3, 0.1D, this.motionZ * (double)this.knockbackStrength * 0.6000000238418579D / (double)f3);
        }

        doBreakParticleFX();
        
        if (targetentity != null)
        {
            int testBlockID = this.worldObj.getBlockId(targetentity.blockX, targetentity.blockY, targetentity.blockZ);
            if (canBreakBlock(testBlockID))
                this.worldObj.destroyBlock(targetentity.blockX, targetentity.blockY, targetentity.blockZ, true);
        }
        
        if (!this.worldObj.isRemote) 
            this.setDead();
    }
    
    private void doBreakParticleFX ()
    {
        int i = 2;
        for (int j = 0; j < i * 4; ++j)
        {
            float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            float offset = this.rand.nextFloat() * 0.1F + 0.1F;
            float xPos = MathHelper.sin(f) * (float) i * 0.5F * offset;
            float zPos = MathHelper.cos(f) * (float) i * 0.5F * offset;
            TSteelworks.proxy.spawnParticle(particleEffect, this.posX + (double) xPos, this.boundingBox.minY, this.posZ + (double) zPos, 0.0D, 0.0D, 0.0D);
        }
    }
    
    public boolean canBreakBlock (int blockid)
    {
      return (blockid == Block.glass.blockID || 
              blockid == Block.glowStone.blockID || 
              blockid == Block.thinGlass.blockID || 
              blockid == TContent.clearGlass.blockID || 
              blockid == TContent.glassPane.blockID ||
              blockid == TContent.stainedGlassClear.blockID ||
              blockid == TContent.stainedGlassClearPane.blockID);
    }
}
