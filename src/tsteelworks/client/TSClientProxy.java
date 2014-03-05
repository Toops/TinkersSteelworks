package tsteelworks.client;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import tsteelworks.TSteelworks;
import tsteelworks.blocks.logic.HighOvenLogic;
import tsteelworks.client.block.SmallFontRenderer;
import tsteelworks.client.gui.HighOvenGui;
import tsteelworks.client.gui.TSManualGui;
import tsteelworks.client.pages.TSBookPage;
import tsteelworks.common.TSCommonProxy;

public class TSClientProxy extends TSCommonProxy
{
    public static SmallFontRenderer smallFontRenderer;
    public static RenderItem itemRenderer = new RenderItem();
    
    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == highOvenGuiID)
            return new HighOvenGui(player.inventory, (HighOvenLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
        if (ID == manualGuiID)
        {
            ItemStack stack = player.getCurrentEquippedItem();
            return new TSManualGui(stack, TSClientProxy.getManualFromStack(stack));
        }
        return null;
    }

    @Override
    public void registerRenderer ()
    {
        Minecraft mc = Minecraft.getMinecraft();
        smallFontRenderer = new SmallFontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.renderEngine, false);
    }
    
    public static Document bookHighoOen;

    public void readManuals ()
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        bookHighoOen = readManual("/assets/tsteelworks/manuals/highoven.xml", dbFactory);
        initManualIcons();
        initManualRecipes();
        initManualPages();
    }
    
    Document readManual (String location, DocumentBuilderFactory dbFactory)
    {
        try
        {
            InputStream stream = TSteelworks.class.getResourceAsStream(location);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public void initManualIcons ()
    {
        
    }
    
    public void initManualRecipes ()
    {
        
    }
    
    public static Map<String, Class<? extends TSBookPage>> pageClasses = new HashMap<String, Class<? extends TSBookPage>>();

    public static void registerManualPage (String type, Class<? extends TSBookPage> clazz)
    {
        pageClasses.put(type, clazz);
    }

    public static Class<? extends TSBookPage> getPageClass (String type)
    {
        return pageClasses.get(type);
    }

    void initManualPages ()
    {
        
    }
    
    public static Document getManualFromStack (ItemStack stack)
    {
        switch (stack.getItemDamage())
        {
        case 0:
            return bookHighoOen;
        }

        return null;
    }
    
    
    
    @Override
    public void registerSounds ()
    {}
}
