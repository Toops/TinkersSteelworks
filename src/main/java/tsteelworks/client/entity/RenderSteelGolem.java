package tsteelworks.client.entity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import tsteelworks.common.entity.SteelGolem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSteelGolem extends RenderLiving
{
    private static final ResourceLocation texture = new ResourceLocation("tsteelworks", "textures/mob/steel_golem.png");

    /** Iron Golem's Model. */
    private final ModelSteelGolem steelGolemModel;

    public RenderSteelGolem()
    {
        super(new ModelSteelGolem(), 0.5F);
        this.steelGolemModel = (ModelSteelGolem)this.mainModel;
    }

    /**
     * Renders the Steel Golem.
     */
    public void doRenderSteelGolem(SteelGolem entity, double par2, double par4, double par6, float par8, float par9)
    {
        super.doRenderLiving(entity, par2, par4, par6, par8, par9);
    }

    protected ResourceLocation getSteelGolemTextures(SteelGolem entity)
    {
        return texture;
    }

    /**
     * Rotates Steel Golem corpse.
     */
    protected void rotateSteelGolemCorpse(SteelGolem entity, float par2, float par3, float par4)
    {
        super.rotateCorpse(entity, par2, par3, par4);

        if ((double)entity.limbSwingAmount >= 0.01D)
        {
            float f3 = 13.0F;
            float f4 = entity.limbSwing - entity.limbSwingAmount * (1.0F - par4) + 6.0F;
            float f5 = (Math.abs(f4 % f3 - f3 * 0.5F) - f3 * 0.25F) / (f3 * 0.25F);
            GL11.glRotatef(6.5F * f5, 0.0F, 0.0F, 1.0F);
        }
    }

    /**
     * Renders Steel Golem Equipped items.
     */
    protected void renderSteelGolemEquippedItems(SteelGolem entity, float par2)
    {
        super.renderEquippedItems(entity, par2);

        if (entity.getHoldRoseTick() != 0)
        {
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glPushMatrix();
            GL11.glRotatef(5.0F + 180.0F * this.steelGolemModel.steelGolemRightArm.rotateAngleX / (float)Math.PI, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(-0.6875F, 1.25F, -0.9375F);
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            float f1 = 0.8F;
            GL11.glScalef(f1, -f1, f1);
            int i = entity.getBrightnessForRender(par2);
            int j = i % 65536;
            int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.bindTexture(TextureMap.locationBlocksTexture);
            this.renderBlocks.renderBlockAsItem(Block.plantRed, 0, 1.0F);
            GL11.glPopMatrix();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }
    }

    public void doRenderLiving(EntityLiving entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRenderSteelGolem((SteelGolem)entity, par2, par4, par6, par8, par9);
    }

    protected void renderEquippedItems(EntityLivingBase entity, float par2)
    {
        this.renderSteelGolemEquippedItems((SteelGolem)entity, par2);
    }

    protected void rotateCorpse(EntityLivingBase entity, float par2, float par3, float par4)
    {
        this.rotateSteelGolemCorpse((SteelGolem)entity, par2, par3, par4);
    }

    public void renderPlayer(EntityLivingBase entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRenderSteelGolem((SteelGolem)entity, par2, par4, par6, par8, par9);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getSteelGolemTextures((SteelGolem)entity);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRenderSteelGolem((SteelGolem)entity, par2, par4, par6, par8, par9);
    }
}
