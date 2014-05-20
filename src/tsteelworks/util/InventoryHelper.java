package tsteelworks.util;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class InventoryHelper
{
    /*
    * Stolen from NEIServerUtils
    * @author chickenbones
    */
   public static boolean canItemFitInPlayerInventory(EntityPlayer player, ItemStack itemstack)
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
   
   public static boolean areItemStacksEqualItem (ItemStack stack1, ItemStack stack2)
   {
       return stack1.itemID != stack2.itemID ? false : (stack1.getItemDamage() != stack2.getItemDamage() ? false : (stack1.stackSize > stack1.getMaxStackSize() ? false : ItemStack
               .areItemStackTagsEqual(stack1, stack2)));
   }
   
   public static boolean canInsertItemToInventory (IInventory iiventory, ItemStack stack, int slot, int side)
   {
       return !iiventory.isItemValidForSlot(slot, stack) ? false : !(iiventory instanceof ISidedInventory) || ((ISidedInventory) iiventory).canInsertItem(slot, stack, side);
   }

   public static boolean canExtractItemFromInventory (IInventory iiventory, ItemStack stack, int slot, int side)
   {
       return !(iiventory instanceof ISidedInventory) || ((ISidedInventory) iiventory).canExtractItem(slot, stack, side);
   }
   
   public static EntityItem getItemEntityAtLocation (World world, double minX, double minY, double minZ, byte facing)
   {
       double x = minX;
       double maxX = minX;
       double y = minY;
       double maxY = minY;
       double z = minZ;
       double maxZ = minZ;
       switch (facing)
       {
       case 0: // Down
           y = minY - 1.0D;
           maxY = minY - 1.0D;
           break;
       case 1: // Up
           maxY = minY + 1.0D;
           break;
       case 2: // North
           z = minZ - 1.0D;
           maxZ = minZ - 1.0D;
           break;
       case 3: // South
           maxZ = minZ + 1.0D;
           break;
       case 4: // West
           x = minX - 1.0D;
           maxX = minX - 1.0D;
           break;
       case 5: // East
           maxX = minX + 1.0D;
           break;
       default:
           break;
       }
       final List<EntityItem> list = world.selectEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getAABBPool().getAABB(x, y, z, maxX + 1.0D, maxY + 1.0D, maxZ + 1.0D), IEntitySelector.selectAnything);
       return list.size() > 0 ? (EntityItem) list.get(0) : null;
   }

   public static IInventory getInventoryAtLocation (World world, double minX, double minY, double maxX)
   {
       IInventory iinventory = null;
       final int i = MathHelper.floor_double(minX);
       final int j = MathHelper.floor_double(minY);
       final int k = MathHelper.floor_double(maxX);
       final TileEntity tileentity = world.getBlockTileEntity(i, j, k);

       if ((tileentity != null) && (tileentity instanceof IInventory))
       {
           iinventory = (IInventory) tileentity;
           if (iinventory instanceof TileEntityChest)
           {
               final int l = world.getBlockId(i, j, k);
               final Block block = Block.blocksList[l];

               if (block instanceof BlockChest)
                   iinventory = ((BlockChest) block).getInventory(world, i, j, k);
           }
       }
       if (iinventory == null)
       {
           final List<IInventory> list = world.getEntitiesWithinAABBExcludingEntity((Entity) null, AxisAlignedBB.getAABBPool().getAABB(minX, minY, maxX, minX + 1.0D, minY + 1.0D, maxX + 1.0D),
                   IEntitySelector.selectInventories);
           if ((list != null) && (list.size() > 0))
               iinventory = (IInventory) list.get(world.rand.nextInt(list.size()));
       }

       return iinventory;
   }
   
   public static boolean matchBlockAtLocation (World world, int x, int y, int z, int blockID)
   {
       return world.getBlockId(x, y, z) == blockID;
   }
   
   public static boolean matchBlockAtLocationWithMeta (World world, int x, int y, int z, int blockID, int meta)
   {
       return world.getBlockId(x, y, z) == blockID && world.getBlockMetadata(x, y, z) == meta;
   }
   
   public static boolean matchBlockAtLocationWithMeta (World world, int x, int y, int z, ItemStack itemstack)
   {
       if (itemstack == null) return false;
       return world.getBlockId(x, y, z) == itemstack.itemID && world.getBlockMetadata(x, y, z) == itemstack.getItemDamage();
   }
   
   /**
    * Obtains a OreDictionary name of a given item.
    * 
    * @param itemstack
    * @return String name if valid, null if no such item exists
    */
   public static String getOreDictionaryName (ItemStack itemstack)
   {
       int oreID = OreDictionary.getOreID(itemstack);
       return (oreID != -1) ?  OreDictionary.getOreName(oreID) : null;
   }
}
