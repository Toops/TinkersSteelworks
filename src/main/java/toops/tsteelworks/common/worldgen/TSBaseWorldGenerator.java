package toops.tsteelworks.common.worldgen;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import tconstruct.util.config.PHConstruct;
import toops.tsteelworks.common.core.ConfigCore;
import toops.tsteelworks.common.core.TSContent;

import java.util.Random;

public class TSBaseWorldGenerator implements IWorldGenerator {
	WorldGenLimestone limestone;

	public TSBaseWorldGenerator() {
		limestone = new WorldGenLimestone(32, TSContent.limestoneBlock, 0);
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.terrainType != WorldType.FLAT) {
			if (world.provider.dimensionId == 0)
				generateSurface(random, chunkX * 16, chunkZ * 16, world);
		}
	}

	void generateSurface(Random random, int xChunk, int zChunk, World world) {
		if (ConfigCore.enableLimestoneWorldgen)
			generateLimestone(random, xChunk, zChunk, world);
	}

	//TODO: Generate in underground ponds
	// This currently generates under oceans, rivers, etc, around the same places you find sandstone
	void generateLimestone(Random random, int xChunk, int zChunk, World world) {
		int xPos, yPos, zPos;
		int maxamt = 32;
		int miny = 12;
		int maxy = PHConstruct.seaLevel;
		for (int q = 0; q <= maxamt; q++) {
			xPos = xChunk + random.nextInt(16);
			yPos = miny + random.nextInt(maxy - miny);
			zPos = zChunk + random.nextInt(16);
			limestone.generate(world, random, xPos, yPos, zPos);
		}
	}
}
