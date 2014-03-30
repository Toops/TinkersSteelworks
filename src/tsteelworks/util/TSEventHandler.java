package tsteelworks.util;

import java.util.Random;

import tsteelworks.TSteelworks;
import tsteelworks.common.TSContent;
import tsteelworks.lib.ConfigCore;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public class TSEventHandler
{
    Random random = new Random();
    private Object evt;
    
    @ForgeSubscribe
    public void onInteract (EntityInteractEvent event)
    {
        if (event.target.getClass() == EntityHorse.class)
        {
            EntityHorse horse = (EntityHorse) event.target;

            EntityPlayer player = (EntityPlayer) event.entityPlayer;
            ItemStack itemstack = player.inventory.getCurrentItem();

            if (itemstack != null)
            {
                boolean affected = false;
                // func_110256_cu returns undead horse types
                if (!horse.func_110256_cu())
                {

                    float heal = 0.0F;
                    short grow = 0;
                    byte temper = 0;

                    if (itemstack.itemID == ConfigCore.dustStorageBlock && itemstack.getItemDamage() == 1)
                    {
                        heal = 9.0F;
                        grow = 60;
                        temper = 4;
                        
                        if (horse.getHealth() < horse.getMaxHealth() && heal > 0.0F)
                        {
                            horse.heal(heal);
                            affected = true;
                        }

                        if (!horse.isAdultHorse() && grow > 0)
                        {
                            horse.addGrowth(grow);
                            affected = true;
                        }

                        if (temper > 0 && (affected || !horse.isTame()) && temper < horse.getMaxTemper())
                        {
                            affected = true;
                            horse.increaseTemper(temper);
                        }

                        if (affected)
                        {
                            horse.worldObj.playSoundAtEntity(horse, "eating", 1.0F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F);
                        }
                        
                        if (!horse.isTame() && !affected)
                        {
                            if (itemstack != null && itemstack.func_111282_a(player, horse))
                            {
                                return;
                            }

                            horse.makeHorseRearWithSound();
                            return;
                        }
                        if (affected)
                        {
                            if (!player.capabilities.isCreativeMode && --itemstack.stackSize == 0)
                            {
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                            }

                            return;
                        }
                    }
                }
            }
        }
    }
}
