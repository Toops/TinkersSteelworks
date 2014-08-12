package tsteelworks.client.pages;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import tsteelworks.lib.client.TSClientRegistry;

public class TSHighOvenPage extends TSBookPage
{
    String text;
    ItemStack[] icons;

    private static final ResourceLocation background = new ResourceLocation("tsteelworks", "textures/gui/bookhighoven.png");

    @Override
    public void readPageFromXML (Element element)
    {
        NodeList nodes = element.getElementsByTagName("text");
        if (nodes != null)
            text = nodes.item(0).getTextContent();

        nodes = element.getElementsByTagName("recipe");
        if (nodes != null)
            icons = TSClientRegistry.getRecipeIcons(nodes.item(0).getTextContent());
    }

    @Override
    public void renderBackgroundLayer (int localWidth, int localHeight)
    {
        manual.getMC().getTextureManager().bindTexture(background);
        manual.drawTexturedModalRect(localWidth, localHeight + 32, 0, 0, 156, 116);
    }

    @Override
    public void renderContentLayer (int localWidth, int localHeight)
    {
        //TODO: Cycle through recipe ICONS
        if (text != null) manual.fonts.drawString("\u00a7n" + text, localWidth + 50, localHeight + 4, 0);

        GL11.glScalef(2f, 2f, 2f);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        manual.renderitem.zLevel = 100;
        manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, TSClientRegistry.getManualIcon("charcoal"), (localWidth + 50) / 2, (localHeight + 110) / 2);
        if (icons[0] != null)
        {
            manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[0], (localWidth + 120) / 2, (localHeight + 72) / 2);
            if (icons[0].stackSize > 1)
                manual.renderitem.renderItemOverlayIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[0], (localWidth + 120) / 2, (localHeight + 72) / 2, String.valueOf(icons[0].stackSize));
        }
        if (icons[1] != null)
        {
            manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[1], (localWidth + 52) / 2, (localHeight + 36) / 2);
            if (icons[1].stackSize > 1)
                manual.renderitem.renderItemOverlayIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[1], (localWidth + 52) / 2, (localHeight + 36) / 2, String.valueOf(icons[1].stackSize));
        }
        if (icons[2] != null)
        {
            manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[2], (localWidth + 6) / 2, (localHeight + 36) / 2);
            if (icons[2].stackSize > 1)
                manual.renderitem.renderItemOverlayIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[2], (localWidth + 6) / 2, (localHeight + 36) / 2, String.valueOf(icons[2].stackSize));
        }
        if (icons[3] != null)
        {
            manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[3], (localWidth + 6) / 2, (localHeight + 74) / 2);
            if (icons[3].stackSize > 1)
                manual.renderitem.renderItemOverlayIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[3], (localWidth + 6) / 2, (localHeight + 74) / 2, String.valueOf(icons[3].stackSize));
        }
        if (icons[4] != null)
        {
            manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[4], (localWidth + 6) / 2, (localHeight + 112) / 2);
            if (icons[4].stackSize > 1)
                manual.renderitem.renderItemOverlayIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[4], (localWidth + 6) / 2, (localHeight + 112) / 2, String.valueOf(icons[4].stackSize));
        }
        manual.renderitem.zLevel = 0;
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }
}
