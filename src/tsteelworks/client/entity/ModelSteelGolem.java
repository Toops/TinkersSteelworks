package tsteelworks.client.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import tsteelworks.entity.SteelGolem;

public class ModelSteelGolem extends ModelBase
{
    ModelRenderer steelGolemHead;
    ModelRenderer steelGolemBody;
    ModelRenderer steelGolemRightArm;
    ModelRenderer steelGolemLeftArm;
    ModelRenderer steelGolemLeftLeg;
    ModelRenderer steelGolemRightLeg;
    ModelRenderer steelGolemFrontLeg;
    ModelRenderer steelGolemHindLeg;

    public ModelSteelGolem()
    {
        this(0.0F);
    }

    public ModelSteelGolem(float par1)
    {
        this(par1, -7.0F);
    }
    
    public ModelSteelGolem(float par1, float par2)
    {
        textureWidth = 128;
        textureHeight = 128;
        // Head
        setTextureOffset("steelGolemHead.head", 0, 0);
        setTextureOffset("steelGolemHead.snout", 40, 16);
        setTextureOffset("steelGolemHead.beam", 40, 0);
        steelGolemHead = new ModelRenderer(this, "steelGolemHead");
        steelGolemHead.setRotationPoint(0F, -10F, -6F);
        steelGolemHead.addBox("head",  -4F,  -8F, -4F, 8, 8, 12);
        steelGolemHead.addBox("snout", -3F,  -4F, -5F, 6, 3, 1);
        steelGolemHead.addBox("beam",  -1F, -10F, -7F, 2, 2, 14);
        // Body
        setTextureOffset("steelGolemBody.bodyupper", 0, 20);
        setTextureOffset("steelGolemBody.bodymiddle", 0, 42);
        setTextureOffset("steelGolemBody.bodylower", 64, 20);
        steelGolemBody = new ModelRenderer(this, "steelGolemBody");
        steelGolemBody.setRotationPoint(0F, -10F, 0F);
        steelGolemBody.addBox("bodyupper", -8F, 0F, -8F, 16, 8, 16);
        steelGolemBody.addBox("bodymiddle", -5F, 8F, -5F, 10, 8, 10);
        steelGolemBody.addBox("bodylower", -6F, 16F, -6F, 12, 8, 12);
        // Right Arm
        setTextureOffset("steelGolemRightArm.shoulderright", 0, 60);
        setTextureOffset("steelGolemRightArm.armupperright", 32, 60);
        setTextureOffset("steelGolemRightArm.armlowerright", 48, 60);
        steelGolemRightArm = new ModelRenderer(this, "steelGolemRightArm");
        steelGolemRightArm.setRotationPoint(-8F, -8F, 0F);
        steelGolemRightArm.addBox("shoulderright", -8F, -6F, -4F, 8, 8, 8);
        steelGolemRightArm.addBox("armupperright", -6F, 2F, -2F, 4, 6, 4);
        steelGolemRightArm.addBox("armlowerright", -7F, 8F, -3F, 6, 12, 6);
        // Left Arm
        setTextureOffset("steelGolemLeftArm.shoulderleft", 0, 60);
        setTextureOffset("steelGolemLeftArm.armupperleft", 32, 60);
        setTextureOffset("steelGolemLeftArm.armlowerleft", 48, 60);
        steelGolemLeftArm = new ModelRenderer(this, "steelGolemLeftArm");
        steelGolemLeftArm.setRotationPoint(8F, -8F, 0F);
        steelGolemLeftArm.addBox("shoulderleft", 0F, -6F, -4F, 8, 8, 8);
        steelGolemLeftArm.addBox("armupperleft", 2F, 2F, -2F, 4, 6, 4);
        steelGolemLeftArm.addBox("armlowerleft", 1F, 8F, -3F, 6, 12, 6);
        // Left Leg
        steelGolemLeftLeg = new ModelRenderer(this, 0, 78).setTextureSize(textureWidth, textureHeight);;
        steelGolemLeftLeg.addBox(-2F, 0F, -2F, 4, 10, 4);
        steelGolemLeftLeg.setRotationPoint(-6F, 14F, 0F);
        // Front Leg
        steelGolemFrontLeg = new ModelRenderer(this, 0, 78).setTextureSize(textureWidth, textureHeight);;
        steelGolemFrontLeg.addBox(-2F, 0F, -2F, 4, 10, 4);
        steelGolemFrontLeg.setRotationPoint(0F, 14F, -6F);
        // Hind Leg
        steelGolemHindLeg = new ModelRenderer(this, 0, 78).setTextureSize(textureWidth, textureHeight);;
        steelGolemHindLeg.addBox(-2F, 0F, -2F, 4, 10, 4);
        steelGolemHindLeg.setRotationPoint(0F, 14F, 6F);
        steelGolemHindLeg.mirror = true;
        // Right Leg
        steelGolemRightLeg = new ModelRenderer(this, 0, 78).setTextureSize(textureWidth, textureHeight);;
        steelGolemRightLeg.addBox(-2F, 0F, -2F, 4, 10, 4);
        steelGolemRightLeg.setRotationPoint(6F, 14F, 0F);
        steelGolemRightLeg.mirror = true;
    }

    public void render (Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        steelGolemLeftLeg.render(f5);
        steelGolemFrontLeg.render(f5);
        steelGolemHindLeg.render(f5);
        steelGolemRightLeg.render(f5);
        steelGolemHead.render(f5);
        steelGolemBody.render(f5);
        steelGolemRightArm.render(f5);
        steelGolemLeftArm.render(f5);
    }

    public void setRotationAngles (float f, float f1, float f2, float f3, float f4, float f5, Entity par7Entity)
    {
        this.steelGolemHead.rotateAngleY = f3 / (180F / (float)Math.PI);
        this.steelGolemHead.rotateAngleX = f4 / (180F / (float)Math.PI);
        this.steelGolemLeftLeg.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
        this.steelGolemHindLeg.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 1.4F * f1;
        this.steelGolemFrontLeg.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 1.4F * f1;
        this.steelGolemRightLeg.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
    }
    
    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(EntityLivingBase entity, float par2, float par3, float par4)
    {
        SteelGolem steelGolem = (SteelGolem)entity;
        int i = steelGolem.getAttackTimer();

        if (i > 0)
        {
            this.steelGolemRightArm.rotateAngleX = -2.0F + 1.5F * this.func_78172_a((float)i - par4, 10.0F);
            this.steelGolemLeftArm.rotateAngleX = -2.0F + 1.5F * this.func_78172_a((float)i - par4, 10.0F);
        }
        else
        {
            int j = steelGolem.getHoldRoseTick();

            if (j > 0)
            {
                this.steelGolemRightArm.rotateAngleX = -0.8F + 0.025F * this.func_78172_a((float)j, 70.0F);
                this.steelGolemLeftArm.rotateAngleX = 0.0F;
            }
            else
            {
                this.steelGolemRightArm.rotateAngleX = (-0.2F + 1.5F * this.func_78172_a(par2, 13.0F)) * par3;
                this.steelGolemLeftArm.rotateAngleX = (-0.2F - 1.5F * this.func_78172_a(par2, 13.0F)) * par3;
            }
        }
    }
    
    private float func_78172_a(float par1, float par2)
    {
        return (Math.abs(par1 % par2 - par2 * 0.5F) - par2 * 0.25F) / (par2 * 0.25F);
    }
}
