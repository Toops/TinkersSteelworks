package tsteelworks.blocks.logic;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;
import tconstruct.library.util.IFacingLogic;

public class HighOvenDuctLogic extends TSMultiServantLogic implements IFacingLogic
{
    byte direction;
    
    @Override
    public byte getRenderDirection ()
    {
        return direction;
    }

    @Override
    public ForgeDirection getForgeDirection ()
    {
        return ForgeDirection.VALID_DIRECTIONS[direction];
    }

    @Override
    public void setDirection (int side)
    {}

    @Override
    public void setDirection (float yaw, float pitch, EntityLivingBase player)
    {
        if (pitch > 45)
        {
            direction = 1;
        }
        else
            if (pitch < -45)
            {
                direction = 0;
            }
            else
            {
                final int facing = MathHelper.floor_double((yaw / 360) + 0.5D) & 3;
                switch (facing)
                {
                    case 0:
                        direction = 2;
                        break;
                    case 1:
                        direction = 5;
                        break;
                    case 2:
                        direction = 3;
                        break;
                    case 3:
                        direction = 4;
                        break;
                }
            }
    }

}
