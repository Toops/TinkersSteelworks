package tsteelworks.entity;

import tsteelworks.TSteelworks;
import tsteelworks.common.TSContent;
import tsteelworks.entity.projectile.EntityScorchedBrick;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class HighGolem extends EntityGolem implements IRangedAttackMob
{
    public HighGolem(World par1World)
    {
        super(par1World);
        this.setSize(0.4F, 1.8F);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(1, new EntityAIArrowAttack(this, 1.25D, 20, 10.0F));
        this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, true, false, IMob.mobSelector));
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    public boolean isAIEnabled()
    {
        return true;
    }

    protected void applyEntityAttributes()
    {
        this.isImmuneToFire = true;
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.20000000298023224D / 2);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute(3.0D);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (this.isWet())
        {
            this.attackEntityFrom(DamageSource.drown, 1.0F);
        }
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getDropItemId()
    {
        return TSContent.materialsTS.itemID;
    }

    /**
     * Drop 0-2 items of this living's type. @param par1 - Whether this entity has recently been hit by a player. @param
     * par2 - Level of Looting used to kill this mob.
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        this.dropItem(TSContent.materialsTS.itemID, 8);
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource par1DamageSource)
    {
        super.onDeath(par1DamageSource);
        doBreakParticleFX();
        
    }
    
    private void doBreakParticleFX ()
    {
        int i = 4;
        for (int j = 0; j < i * 8; ++j)
        {
            float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            float offset = this.rand.nextFloat() * 0.2F + 0.2F;
            float xPos = MathHelper.sin(f) * (float) i * 0.4F * offset;
            float zPos = MathHelper.cos(f) * (float) i * 0.4F * offset;
            TSteelworks.proxy.spawnParticle("scorchedbrick", this.posX + (double) xPos, this.boundingBox.minY+2, this.posZ + (double) zPos, 0.0D, 0.0D, 0.0D);
            if (j < 4) TSteelworks.proxy.spawnParticle("lava", this.posX, this.boundingBox.minY, this.posZ, 0.0D, 0.0D, 0.0D);
        }
        
    }
    
    /**
     * Attack the specified entity using a ranged attack.
     */
    public void attackEntityWithRangedAttack(EntityLivingBase targetentity, float par2)
    {
        EntityScorchedBrick entityscorchedbrick = new EntityScorchedBrick(this.worldObj, this);
        double d0 = targetentity.posX - this.posX;
        double d1 = targetentity.posY + (double)targetentity.getEyeHeight() - 1.100000023841858D - entityscorchedbrick.posY;
        double d2 = targetentity.posZ - this.posZ;
        float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * 0.2F;
        entityscorchedbrick.setThrowableHeading(d0, d1 + (double)f1, d2, 1.6F, 12.0F);
        this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(entityscorchedbrick);
    }
}
