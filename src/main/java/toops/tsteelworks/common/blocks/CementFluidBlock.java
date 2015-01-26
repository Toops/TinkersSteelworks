package toops.tsteelworks.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import toops.tsteelworks.common.core.TSContent;

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
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if (entity instanceof EntityLivingBase) {
			if (world.isRemote)
				entity.setVelocity(0, 0, 0);
			
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.digSlowdown.getId(), 30, 3, true));
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		super.updateTick(world, x, y, z, rand);

		checkForHarden(world, x, y, z);
	}

	public void checkForHarden(World world, int x, int y, int z) {
		if (world.getBlockMetadata(x, y, z) == quantaPerBlock -1 ||
				isFlowingVertically(world, x, y, z) ||
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
		return world.getBlock(x, y, z).getMaterial().isSolid();
	}
}
