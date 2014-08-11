package tsteelworks.common.blocks;

import mantle.world.CoordTuple;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import tsteelworks.common.core.TSContent;

/*
 * Todo: implement
 *
 * If I get the idea, which might be wrong, this fluid is supposed to harden when it's in contact with air ?
 */
public class CementFluidBlock extends TSBaseFluid {
	public CoordTuple origin;

	public CementFluidBlock(Fluid fluid, Material material, String texture) {
		super(fluid, material, texture);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		origin = new CoordTuple(x, y, z);
	}

	public boolean validHardenCoords(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);

		return block.getMaterial() == Material.air || block == TSContent.cementBlock;
	}

	/**
	 * Creates fizzing sound and smoke. Used when lava flows over block or mixes with water.
	 */
	protected void triggerCementMixEffects(World world, int x, int y, int z) {
		world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
	}
}
