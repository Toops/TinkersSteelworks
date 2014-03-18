package tsteelworks.client.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import tsteelworks.entity.HighGolem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHighGolem extends RenderLiving
{
    private static final ResourceLocation textures = new ResourceLocation("tsteelworks", "textures/mob/highgolem.png");
    private ModelHighGolem model;

    public RenderHighGolem()
    {
        super(new ModelHighGolem(), 0.5F);
        this.model = (ModelHighGolem)super.mainModel;
        this.setRenderPassModel(this.model);
    }

    protected ResourceLocation getHighGolemTextures(HighGolem entity)
    {
        return textures;
    }

    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getHighGolemTextures((HighGolem)entity);
    }
}
