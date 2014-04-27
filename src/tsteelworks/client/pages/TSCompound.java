package tsteelworks.client.pages;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import tsteelworks.lib.client.TSClientRegistry;

public class TSCompound extends TSBookPage
{
    String text;
    String[] iconText;
    ItemStack[] icons;

    @Override
    public void readPageFromXML (Element element)
    {
        NodeList nodes = element.getElementsByTagName("text");
        if (nodes != null)
            text = nodes.item(0).getTextContent();
        nodes = element.getElementsByTagName("item");
        iconText = new String[nodes.getLength()];
        icons = new ItemStack[nodes.getLength()];
        for (int i = 0; i < nodes.getLength(); i++)
        {
            final NodeList children = nodes.item(i).getChildNodes();
            iconText[i] = children.item(1).getTextContent();
            icons[i] = TSClientRegistry.getManualIcon(children.item(3).getTextContent());
        }
    }

    @Override
    public void renderContentLayer (int localWidth, int localHeight)
    {
        manual.fonts.drawSplitString(text, localWidth, localHeight, 178, 0);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        manual.renderitem.zLevel = 100;
        final int offset = (text.length() / 4) + 12;
        GL11.glScalef(1.0F, 1.0F, 1.0F);
        
        for (int i = 0; i < icons.length; i++)
        {
            manual.renderitem.renderItemIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[i], localWidth, localHeight + (16 * i) + offset);
            if (iconText[i].length() > 40)
            {
            }
            manual.fonts.drawSplitString(iconText[i], localWidth +  20, localHeight + 4 + (16 * i) + offset, 140, 0);
        }
        manual.renderitem.zLevel = 0;
//        GL11.glScalef(2F, 2F, 2F);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }
}
