package tsteelworks.client.particle;

import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import nf.fr.ephys.cookiecore.client.IParticleHandler;
import nf.fr.ephys.cookiecore.client.ParticleRegistry;
import tsteelworks.common.core.TSContent;

public class TSParticle implements IParticleHandler {
	public final int SCORCHED_BRICK_ID = ParticleRegistry.registerParticleHandler(this);
	public final int LIMESTONE_BRICK_ID = ParticleRegistry.registerParticleHandler(this);

	@Override
	public EntityFX getParticle(int id, World world, double x, double y, double z, double v4, double v5, double v6) {
		return new EntityBreakingFX(world, x, y, z, TSContent.materialsTS, id == SCORCHED_BRICK_ID ? 0 : 1);
	}

	@Override
	public double getRenderDistance(int id) {
		return 16;
	}

	@Override
	public int getMaxParticleSetting(int id) {
		return 3;
	}
}
