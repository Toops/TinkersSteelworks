package tsteelworks.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import tsteelworks.TSteelworks;
import tsteelworks.common.TSContent;
import tsteelworks.entity.projectile.EntityScorchedBrick;

public class HighGolem extends EntityGolem implements IRangedAttackMob
{
    public HighGolem(World par1World)
    {
        super(par1World);
        setSize(0.4F, 1.8F);
        getNavigator().setAvoidsWater(true);
        tasks.addTask(1, new EntityAIArrowAttack(this, 1.25D, 20, 10.0F));
        tasks.addTask(2, new EntityAIWander(this, 1.0D));
        tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(4, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, true, false, IMob.mobSelector));
    }

    /**
     * Attack the specified entity using a ranged attack.
     */
    @Override
    public void attackEntityWithRangedAttack (EntityLivingBase targetentity, float par2)
    {
        final EntityScorchedBrick entityscorchedbrick = new EntityScorchedBrick(worldObj, this);
        final double d0 = targetentity.posX - posX;
        final double d1 = (targetentity.posY + targetentity.getEyeHeight()) - 1.100000023841858D - entityscorchedbrick.posY;
        final double d2 = targetentity.posZ - posZ;
        final float f1 = MathHelper.sqrt_double((d0 * d0) + (d2 * d2)) * 0.2F;
        entityscorchedbrick.setThrowableHeading(d0, d1 + f1, d2, 1.6F, 12.0F);
        playSound("random.bow", 1.0F, 1.0F / ((getRNG().nextFloat() * 0.4F) + 0.8F));
        worldObj.spawnEntityInWorld(entityscorchedbrick);
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    @Override
    public boolean isAIEnabled ()
    {
        return true;
    }

    /**
     * Called when the mob's health reaches 0.
     */
    @Override
    public void onDeath (DamageSource par1DamageSource)
    {
        super.onDeath(par1DamageSource);
        doBreakParticleFX();

    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
    public void onLivingUpdate ()
    {
        super.onLivingUpdate();

        if (isWet())
            attackEntityFrom(DamageSource.drown, 1.0F);
    }

    private void doBreakParticleFX ()
    {
        final int i = 4;
        for (int j = 0; j < (i * 8); ++j)
        {
            final float f = rand.nextFloat() * (float) Math.PI * 2.0F;
            final float offset = (rand.nextFloat() * 0.2F) + 0.2F;
            final float xPos = MathHelper.sin(f) * i * 0.4F * offset;
            final float zPos = MathHelper.cos(f) * i * 0.4F * offset;
            TSteelworks.proxy.spawnParticle("scorchedbrick", posX + xPos, boundingBox.minY + 2, posZ + zPos, 0.0D, 0.0D, 0.0D);
            if (j < 4)
                TSteelworks.proxy.spawnParticle("lava", posX, boundingBox.minY, posZ, 0.0D, 0.0D, 0.0D);
        }

    }
    
    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.irongolem.hit";
    }
    
    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.irongolem.death";
    }
    
    @Override
    protected void applyEntityAttributes ()
    {
        isImmuneToFire = true;
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(20.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.20000000298023224D / 2);
        getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute(3.0D);
    }

    /**
     * Drop 0-2 items of this living's type. @param par1 - Whether this entity has recently been hit by a player. @param
     * par2 - Level of Looting used to kill this mob.
     */
    @Override
    protected void dropFewItems (boolean par1, int par2)
    {
        dropItem(TSContent.materialsTS.itemID, 8);
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    @Override
    protected int getDropItemId ()
    {
        return TSContent.materialsTS.itemID;
    }
}
