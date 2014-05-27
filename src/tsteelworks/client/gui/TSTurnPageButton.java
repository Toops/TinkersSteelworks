package tsteelworks.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TSTurnPageButton extends GuiButton
{
    /**
     * True for pointing right (next page), false for pointing left (previous page).
     */
    private final boolean nextPage;

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/bookleft.png");

    public TSTurnPageButton(int par1, int par2, int par3, boolean par4)
    {
        super(par1, par2, par3, 23, 13, "");
        nextPage = par4;
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton (Minecraft par1Minecraft, int par2, int par3)
    {
        if (drawButton)
        {
            final boolean var4 = (par2 >= xPosition) && (par3 >= yPosition) && (par2 < (xPosition + width)) && (par3 < (yPosition + height));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            par1Minecraft.getTextureManager().bindTexture(background);
            int var5 = 0;
            int var6 = 192;

            if (var4)
                var5 += 23;

            if (!nextPage)
                var6 += 13;

            drawTexturedModalRect(xPosition, yPosition, var5, var6, 23, 13);
        }
    }
}
