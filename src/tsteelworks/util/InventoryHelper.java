package tsteelworks.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryHelper
{
    /*
    * Stolen from NEIServerUtils
    * @author chickenbones
    */
   public static boolean canItemFitInInventory(EntityPlayer player, ItemStack itemstack)
   {
       for(int i = 0; i < player.inventory.getSizeInventory() - 4; i++)
       {
           if(player.inventory.getStackInSlot(i) == null)
           {
               return true;
           }
       }
       if(!itemstack.isItemDamaged())
       {
           if(itemstack.getMaxStackSize() == 1) return false;
           
           for(int i = 0; i < player.inventory.getSizeInventory(); i++)
           {
               ItemStack invstack = player.inventory.getStackInSlot(i);
               if (invstack != null && invstack.itemID == itemstack.itemID && invstack.isStackable() && invstack.stackSize < invstack.getMaxStackSize() && invstack.stackSize < player.inventory.getInventoryStackLimit() && (!invstack.getHasSubtypes() || invstack.getItemDamage() == itemstack.getItemDamage()))
               {
                   return true;
               }
           }            
       }
       return false;
   }
}
