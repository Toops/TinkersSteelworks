package tsteelworks.client.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelHighGolem extends ModelBase {
	public ModelRenderer body;
	public ModelRenderer bottomBody;
	public ModelRenderer head;
	public ModelRenderer rightHand;
	public ModelRenderer leftHand;

	public ModelHighGolem() {
		final float f = 4.0F;
		final float f1 = 0.0F;
		head = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
		head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f1 - 0.5F);
		head.setRotationPoint(0.0F, 0.0F + f, 0.0F);
		rightHand = (new ModelRenderer(this, 32, 0)).setTextureSize(64, 64);
		rightHand.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, f1 - 0.5F);
		rightHand.setRotationPoint(0.0F, (0.0F + f + 9.0F) - 7.0F, 0.0F);
		leftHand = (new ModelRenderer(this, 32, 0)).setTextureSize(64, 64);
		leftHand.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, f1 - 0.5F);
		leftHand.setRotationPoint(0.0F, (0.0F + f + 9.0F) - 7.0F, 0.0F);
		body = (new ModelRenderer(this, 0, 16)).setTextureSize(64, 64);
		body.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, f1 - 0.5F);
		body.setRotationPoint(0.0F, 0.0F + f + 9.0F, 0.0F);
		bottomBody = (new ModelRenderer(this, 0, 36)).setTextureSize(64, 64);
		bottomBody.addBox(-6.0F, -12.0F, -6.0F, 12, 12, 12, f1 - 0.5F);
		bottomBody.setRotationPoint(0.0F, 0.0F + f + 20.0F, 0.0F);
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	@Override
	public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
		setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
		body.render(par7);
		bottomBody.render(par7);
		head.render(par7);
		rightHand.render(par7);
		leftHand.render(par7);
	}

	/**
	 * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
	 * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
	 * "far" arms and legs can swing at most.
	 */
	@Override
	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {
		super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);
		head.rotateAngleY = par4 / (180F / (float) Math.PI);
		head.rotateAngleX = par5 / (180F / (float) Math.PI);
		body.rotateAngleY = (par4 / (180F / (float) Math.PI)) * 0.25F;
		final float f6 = MathHelper.sin(body.rotateAngleY);
		final float f7 = MathHelper.cos(body.rotateAngleY);
		rightHand.rotateAngleZ = 1.0F;
		leftHand.rotateAngleZ = -1.0F;
		rightHand.rotateAngleY = 0.0F + body.rotateAngleY;
		leftHand.rotateAngleY = (float) Math.PI + body.rotateAngleY;
		rightHand.rotationPointX = f7 * 5.0F;
		rightHand.rotationPointZ = -f6 * 5.0F;
		leftHand.rotationPointX = -f7 * 5.0F;
		leftHand.rotationPointZ = f6 * 5.0F;
	}
}
