package toops.tsteelworks.client.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderHighGolem extends RenderLiving {
	private static final ResourceLocation textures = new ResourceLocation("tsteelworks", "textures/mob/highgolem.png");
	private final ModelHighGolem model;

	public RenderHighGolem() {
		super(new ModelHighGolem(), 0.5F);
		model = (ModelHighGolem) super.mainModel;
		setRenderPassModel(model);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return textures;
	}
}
