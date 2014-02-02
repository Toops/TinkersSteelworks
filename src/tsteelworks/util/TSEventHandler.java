package tsteelworks.util;

import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import tconstruct.blocks.LiquidMetalFinite;
import tsteelworks.common.TSContent;


public class TSEventHandler
{
	// RNG
	Random	random	= new Random();

	/**
	 * Fill bucket event
	 * 
	 * @param evt
	 */
	@ForgeSubscribe
	public void bucketFill (FillBucketEvent evt)
	{
		if ((evt.current.getItem() == Item.bucketEmpty) && (evt.target.typeOfHit == EnumMovingObjectType.TILE))
		{
			final int hitX = evt.target.blockX;
			final int hitY = evt.target.blockY;
			final int hitZ = evt.target.blockZ;
			if ((evt.entityPlayer != null) && !evt.entityPlayer.canPlayerEdit(hitX, hitY, hitZ, evt.target.sideHit, evt.current)) return;
			final int bID = evt.world.getBlockId(hitX, hitY, hitZ);
			for (int id = 0; id < TSContent.fluidBlocks.length; id++)
				if (bID == TSContent.fluidBlocks[id].blockID) if (evt.entityPlayer.capabilities.isCreativeMode) evt.world
						.setBlockToAir(hitX, hitY, hitZ);
				else
				{
					if (TSContent.fluidBlocks[id] instanceof LiquidMetalFinite) evt.world.setBlockToAir(hitX, hitY, hitZ);
					else evt.world.setBlockToAir(hitX, hitY, hitZ);
					evt.setResult(Result.ALLOW);
					evt.result = new ItemStack(TSContent.buckets, 1, id);
				}
		}
	}
}
