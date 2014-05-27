package tsteelworks.lib;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import tsteelworks.TSteelworks;

public class TSAbilityHelper
{
    public static Random random = new Random();
    
    // Mostly lifted from NEI
    public static void drawItemsToEntity(World world, EntityLivingBase entity, int distance)
    {
        float distancexz = distance * 2;
        float distancey = distance;
        double maxspeedxz = 0.6;
        double maxspeedy = 0.6;
        double speedxz = 0.6;
        double speedy = 0.9;
        List<EntityItem> items = entity.worldObj.getEntitiesWithinAABB(EntityItem.class, entity.boundingBox.expand(distancexz, distancey, distancexz));
        for(EntityItem item : items)
        {
            if (item.delayBeforeCanPickup > 0) continue;
            double dx = entity.posX - item.posX;
            double dy = entity.posY + entity.getEyeHeight() - item.posY;
            double dz = entity.posZ - item.posZ;
            double absxz = Math.sqrt(dx * dx + dz * dz);
            double absy = Math.abs(dy);
            
            if(absxz > distancexz) continue;
            if(absxz < 1 && entity instanceof EntityPlayerMP) item.onCollideWithPlayer((EntityPlayerMP)entity);
            
            if(absxz > 1)
            {
                dx /= absxz;
                dz /= absxz;
            }
            
            if(absy > 1) dy /= absy;

            double vx = item.motionX + speedxz * dx;
            double vy = item.motionY + speedy * dy;
            double vz = item.motionZ + speedxz * dz;
            
            double absvxz = Math.sqrt(vx * vx + vz * vz);
            double absvy = Math.abs(vy);
            
            double rationspeedxz = absvxz / maxspeedxz;
            if(rationspeedxz > 1)
            {
                vx /= rationspeedxz;
                vz /= rationspeedxz;
            }
            
            double rationspeedy = absvy / maxspeedy;
            if(rationspeedy > 1)  vy /= rationspeedy;
            
            item.motionX = vx;
            item.motionY = vy;
            item.motionZ = vz;
        }
    }
}
