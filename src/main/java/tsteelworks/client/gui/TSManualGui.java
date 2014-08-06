package tsteelworks.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tsteelworks.client.TSClientProxy;
import tsteelworks.client.TSRenderItemCopy;
import tsteelworks.client.block.SmallFontRenderer;
import tsteelworks.client.pages.TSBookPage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TSManualGui extends GuiScreen
{
    ItemStack itemstackBook;
    Document manual;
    public TSRenderItemCopy renderitem = new TSRenderItemCopy();
    int bookImageWidth = 206;
    int bookImageHeight = 200;
    int bookTotalPages = 1;
    int currentPage;
    int maxPages;

    @SuppressWarnings ("unused")
    private TSTurnPageButton buttonNextPage;
    @SuppressWarnings ("unused")
    private TSTurnPageButton buttonPreviousPage;

    TSBookPage pageLeft;
    TSBookPage pageRight;

    public SmallFontRenderer fonts = TSClientProxy.smallFontRenderer;

    private static final ResourceLocation bookRight = new ResourceLocation("tinker", "textures/gui/bookright.png");

    /*@Override
    public void setWorldAndResolution (Minecraft minecraft, int w, int h)
    {
        this.guiParticles = new GuiParticle(minecraft);
        this.mc = minecraft;
        this.width = w;
        this.height = h;
        this.buttonList.clear();
        this.initGui();
    }*/

    private static final ResourceLocation bookLeft = new ResourceLocation("tinker", "textures/gui/bookleft.png");

    public TSManualGui(ItemStack stack, Document doc)
    {
        mc = Minecraft.getMinecraft();
        itemstackBook = stack;
        currentPage = 0; //Stack page
        manual = doc;
        //renderitem.renderInFrame = true;
    }

    @Override
    public boolean doesGuiPauseGame ()
    {
        return false;
    }

    @Override
    public void drawScreen (int par1, int par2, float par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(bookRight);
        int localWidth = (width) / 2;
        final byte localHeight = 8;
        drawTexturedModalRect(localWidth, localHeight, 0, 0, bookImageWidth, bookImageHeight);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(bookLeft);
        localWidth = localWidth - bookImageWidth;
        drawTexturedModalRect(localWidth, localHeight, 256 - bookImageWidth, 0, bookImageWidth, bookImageHeight);

        super.drawScreen(par1, par2, par3); //16, 12, 220, 12

        if (pageLeft != null)
            pageLeft.renderBackgroundLayer(localWidth + 16, localHeight + 12);
        if (pageRight != null)
            pageRight.renderBackgroundLayer(localWidth + 220, localHeight + 12);

        if (pageLeft != null)
            pageLeft.renderContentLayer(localWidth + 16, localHeight + 12);
        if (pageRight != null)
            pageRight.renderContentLayer(localWidth + 220, localHeight + 12);
    }

    public Minecraft getMC ()
    {
        return mc;
    }

    @SuppressWarnings ("unchecked")
    @Override
    public void initGui ()
    {
        maxPages = manual.getElementsByTagName("page").getLength();
        updateText();
        final int xPos = (width) / 2;
        buttonList.add(buttonNextPage = new TSTurnPageButton(1, (xPos + bookImageWidth) - 50, 180, true));
        buttonList.add(buttonPreviousPage = new TSTurnPageButton(2, (xPos - bookImageWidth) + 24, 180, false));
    }

    @Override
    protected void actionPerformed (GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id == 1)
                currentPage += 2;
            if (button.id == 2)
                currentPage -= 2;

            updateText();
        }
    }

    @SuppressWarnings ("rawtypes")
    void updateText ()
    {
        if ((maxPages % 2) == 1)
        {
            if (currentPage > maxPages)
                currentPage = maxPages;
        }
        else if (currentPage >= maxPages)
            currentPage = maxPages - 2;
        if ((currentPage % 2) == 1)
            currentPage--;
        if (currentPage < 0)
            currentPage = 0;

        final NodeList nList = manual.getElementsByTagName("page");

        Node node = nList.item(currentPage);
        if (node.getNodeType() == Node.ELEMENT_NODE)
        {
            final Element element = (Element) node;
            final Class clazz = TSClientProxy.getPageClass(element.getAttribute("type"));
            if (clazz != null)
                try
                {
                    pageLeft = (TSBookPage) clazz.newInstance();
                    pageLeft.init(this, 0);
                    pageLeft.readPageFromXML(element);
                }
                catch (final Exception e)
                {
                }
            else
                pageLeft = null;
        }

        node = nList.item(currentPage + 1);
        if ((node != null) && (node.getNodeType() == Node.ELEMENT_NODE))
        {
            final Element element = (Element) node;
            final Class clazz = TSClientProxy.getPageClass(element.getAttribute("type"));
            if (clazz != null)
                try
                {
                    pageRight = (TSBookPage) clazz.newInstance();
                    pageRight.init(this, 1);
                    pageRight.readPageFromXML(element);
                }
                catch (final Exception e)
                {
                }
            else
                pageLeft = null;
        }
        else
            pageRight = null;
    }
}
