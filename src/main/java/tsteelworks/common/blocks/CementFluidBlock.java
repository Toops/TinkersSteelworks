package tsteelworks.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import tsteelworks.common.core.TSContent;

import java.util.Random;

public class CementFluidBlock extends TSFluidBlock {
	public CementFluidBlock(Fluid fluid, Material material, String texture) {
		super(fluid, material, texture);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		super.updateTick(world, x, y, z, rand);

		checkForHarden(world, x, y, z);
	}

	public void checkForHarden(World world, int x, int y, int z) {
		System.out.println(world.getBlockMetadata(x, y, z) + ", " + quantaPerBlock);
		if (world.getBlockMetadata(x, y, z) == quantaPerBlock -1 ||
				validHardenCoords(world, x, y, z - 1) ||
				validHardenCoords(world, x, y, z + 1) ||
				validHardenCoords(world, x - 1, y, z) ||
				validHardenCoords(world, x + 1, y, z) ||
				validHardenCoords(world, x + 1, y, z + 1) ||
				validHardenCoords(world, x + 1, y, z - 1) ||
				validHardenCoords(world, x - 1, y, z + 1) ||
				validHardenCoords(world, x - 1, y, z - 1)) {
			world.setBlock(x, y, z, TSContent.cementBlock);
			world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
		}
	}

	public boolean validHardenCoords(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);

		return block == TSContent.cementBlock;
	}
}
