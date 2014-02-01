package tsteelworks.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class TSMetalBlock extends TSBaseBlock {
    static String[] metalTypes = new String[] { "compressed_monoatomicgold" };

	public TSMetalBlock(int id, Material material, float hardness)
	{
	    super(id, material, hardness, metalTypes);
	    this.setStepSound(Block.soundMetalFootstep);
	}
	
	public boolean isBeaconBase (World worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ)
	{
	    return true;
	}

}
