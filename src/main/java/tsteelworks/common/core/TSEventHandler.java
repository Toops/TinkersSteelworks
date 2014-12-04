package tsteelworks.common.core;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import tsteelworks.lib.ModsData;

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

			if (fluidBlock == ModsData.Fluids.steamBlock) {
				bucket = ModsData.Fluids.bucketSteam;
			} else if (fluidBlock == ModsData.Fluids.moltenLimestone) {
				bucket = ModsData.Fluids.bucketLimestone;
			} else if (fluidBlock == ModsData.Fluids.liquidCement) {
				bucket = ModsData.Fluids.bucketCement;
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
