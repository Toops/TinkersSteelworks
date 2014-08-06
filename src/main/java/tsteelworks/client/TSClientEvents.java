package tsteelworks.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.ForgeSubscribe;
import tsteelworks.common.TSContent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TSClientEvents
{
    Minecraft mc = Minecraft.getMinecraft();
    
    @ForgeSubscribe
    public void postStitch (TextureStitchEvent.Post event)
    {
        TSContent.steamFluid.setIcons(TSContent.steamBlock.getIcon(0,0), TSContent.steamBlock.getIcon(2, 0));
        TSContent.moltenLimestoneFluid.setIcons(TSContent.moltenLimestone.getIcon(0,0), TSContent.moltenLimestone.getIcon(2, 0));
        TSContent.liquidCementFluid.setIcons(TSContent.liquidCement.getIcon(0,0), TSContent.liquidCement.getIcon(2, 0));
    }
}
