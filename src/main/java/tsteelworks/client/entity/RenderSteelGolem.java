package tsteelworks.client.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import tsteelworks.common.entity.SteelGolem;

@SideOnly(Side.CLIENT)
public class RenderSteelGolem extends RenderLiving {
	private static final ResourceLocation TEXTURE = new ResourceLocation("tsteelworks", "textures/mob/steel_golem.png");

	/**
	 * Iron Golem's Model.
	 */
	private final ModelSteelGolem steelGolemModel;

	public RenderSteelGolem() {
		super(new ModelSteelGolem(), 0.5F);
		this.steelGolemModel = (ModelSteelGolem) this.mainModel;
	}

	@Override
	protected void renderEquippedItems(EntityLivingBase entity, float par2) {
		SteelGolem golem = (SteelGolem) entity;

		if (golem.getHoldRoseTick() != 0) {
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glPushMatrix();
			GL11.glRotatef(5.0F + 180.0F * this.steelGolemModel.steelGolemRightArm.rotateAngleX / (float) Math.PI, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(-0.6875F, 1.25F, -0.9375F);
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			float f1 = 0.8F;
			GL11.glScalef(f1, -f1, f1);
			int i = entity.getBrightnessForRender(par2);
			int j = i % 65536;
			int k = i / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.bindTexture(TextureMap.locationBlocksTexture);
			RenderBlocks.getInstance().renderBlockAsItem(Blocks.yellow_flower, 0, 1.0F);
			GL11.glPopMatrix();
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		}
	}

	@Override
	protected void rotateCorpse(EntityLivingBase entity, float par2, float par3, float par4) {
		super.rotateCorpse(entity, par2, par3, par4);

		if ((double) entity.limbSwingAmount >= 0.01D) {
			float f3 = 13.0F;
			float f4 = entity.limbSwing - entity.limbSwingAmount * (1.0F - par4) + 6.0F;
			float f5 = (Math.abs(f4 % f3 - f3 * 0.5F) - f3 * 0.25F) / (f3 * 0.25F);
			GL11.glRotatef(6.5F * f5, 0.0F, 0.0F, 1.0F);
		}
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}
}
