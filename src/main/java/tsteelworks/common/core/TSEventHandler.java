package tsteelworks.common.core;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.entity.player.FillBucketEvent;

public class TSEventHandler {
	@SubscribeEvent
	public void bucketFill(FillBucketEvent evt) {
		if (evt.current.getItem() == Items.bucket && evt.target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			int hitX = evt.target.blockX;
			int hitY = evt.target.blockY;
			int hitZ = evt.target.blockZ;

			if (evt.entityPlayer != null && !evt.entityPlayer.canPlayerEdit(hitX, hitY, hitZ, evt.target.sideHit, evt.current)) {
				return;
			}

			Block fluidBlock = evt.world.getBlock(hitX, hitY, hitZ);
			ItemStack bucket = null;

			if (fluidBlock == TSContent.steamBlock) {
				bucket = TSContent.bucketSteam;
			} else if (fluidBlock == TSContent.moltenLimestone) {
				bucket = TSContent.bucketLimestone;
			} else if (fluidBlock == TSContent.liquidCement) {
				bucket = TSContent.bucketCement;
			}

			if (bucket == null)
				return;

			if (evt.entityPlayer == null || !evt.entityPlayer.capabilities.isCreativeMode) {
				evt.result = bucket.copy();
				evt.setResult(Event.Result.ALLOW);
			}

			evt.world.setBlockToAir(hitX, hitY, hitZ);
		}
	}
}
