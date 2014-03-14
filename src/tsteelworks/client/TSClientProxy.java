package tsteelworks.client;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.w3c.dom.Document;

import tconstruct.common.TContent;
import tconstruct.library.client.TConstructClientRegistry;
import tsteelworks.TSteelworks;
import tsteelworks.blocks.logic.HighOvenDuctLogic;
import tsteelworks.blocks.logic.HighOvenLogic;
import tsteelworks.client.block.SmallFontRenderer;
import tsteelworks.client.gui.HighOvenDuctGui;
import tsteelworks.client.gui.HighOvenGui;
import tsteelworks.client.gui.TSManualGui;
import tsteelworks.client.pages.TSBlankPage;
import tsteelworks.client.pages.TSBlockCastPage;
import tsteelworks.client.pages.TSBookPage;
import tsteelworks.client.pages.TSContentsTablePage;
import tsteelworks.client.pages.TSCraftingPage;
import tsteelworks.client.pages.TSFurnacePage;
import tsteelworks.client.pages.TSMaterialPage;
import tsteelworks.client.pages.TSModifierPage;
import tsteelworks.client.pages.TSPicturePage;
import tsteelworks.client.pages.TSSectionPage;
import tsteelworks.client.pages.TSSidebarPage;
import tsteelworks.client.pages.TSTextPage;
import tsteelworks.client.pages.TSTitlePage;
import tsteelworks.client.pages.TSToolPage;
import tsteelworks.common.TSCommonProxy;
import tsteelworks.common.TSContent;
import tsteelworks.lib.client.TSClientRegistry;

public class TSClientProxy extends TSCommonProxy
{
    public static SmallFontRenderer smallFontRenderer;
    public static RenderItem itemRenderer = new RenderItem();
    
    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == highovenGuiID)
            return new HighOvenGui(player.inventory, (HighOvenLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
        if (ID == highovenDuctGuiID)
            return new HighOvenDuctGui(player.inventory, (HighOvenDuctLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
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
    
    public static Document highovenXml;

    public void readManuals ()
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        highovenXml = readManual("/assets/tsteelworks/manuals/highoven.xml", dbFactory);
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
        // TSTL
        TSClientRegistry.registerManualIcon("highovenbook", new ItemStack(TSContent.bookManual, 1, 0));
        TSClientRegistry.registerManualIcon("highoven", new ItemStack(TSContent.highoven));
        TSClientRegistry.registerManualIcon("highovendrain", new ItemStack(TSContent.highoven, 1, 1));
        TSClientRegistry.registerManualIcon("scorchedbrick", new ItemStack(TSContent.materialsTS, 1, 0));
        TSClientRegistry.registerManualIcon("scorchedbrickblock", new ItemStack(TSContent.highoven, 1, 2));
        TSClientRegistry.registerManualIcon("redstonedust", new ItemStack(Item.redstone));
        TSClientRegistry.registerManualIcon("emeraldgem", new ItemStack(Item.emerald));
        TSClientRegistry.registerManualIcon("gunpowderdust", new ItemStack(Item.gunpowder));
        TSClientRegistry.registerManualIcon("sugardust", new ItemStack(Item.sugar));
        TSClientRegistry.registerManualIcon("charcoal", new ItemStack(Item.coal, 1, 1));
        TSClientRegistry.registerManualIcon("charcoalblock", new ItemStack(TSContent.charcoalBlock));
        TSClientRegistry.registerManualIcon("sandblock", new ItemStack(Block.sand));
        TSClientRegistry.registerManualIcon("gunpowderblock", new ItemStack(TSContent.dustStorageBlock, 1, 0));
        TSClientRegistry.registerManualIcon("sugarblock", new ItemStack(TSContent.dustStorageBlock, 1, 1));
        TSClientRegistry.registerManualIcon("spongeblock", new ItemStack(Block.sponge));
        
        // TCON
        TSClientRegistry.registerManualIcon("smelterybook", TConstructClientRegistry.getManualIcon("smelterybook"));
        TSClientRegistry.registerManualIcon("smeltery", TConstructClientRegistry.getManualIcon("smeltery"));
        TSClientRegistry.registerManualIcon("blankcast", TConstructClientRegistry.getManualIcon("blankcast"));
        TSClientRegistry.registerManualIcon("castingtable", TConstructClientRegistry.getManualIcon("castingtable"));

        TSClientRegistry.registerManualIcon("searedbrick", TConstructClientRegistry.getManualIcon("searedbrick"));
        TSClientRegistry.registerManualIcon("drain", TConstructClientRegistry.getManualIcon("drain"));
        TSClientRegistry.registerManualIcon("faucet", TConstructClientRegistry.getManualIcon("faucet"));
        
        TSClientRegistry.registerManualIcon("blankpattern", TConstructClientRegistry.getManualIcon("blankpattern"));
        TSClientRegistry.registerManualIcon("toolstation", TConstructClientRegistry.getManualIcon("toolstation"));
        TSClientRegistry.registerManualIcon("partcrafter", TConstructClientRegistry.getManualIcon("partcrafter"));
        TSClientRegistry.registerManualIcon("patternchest", TConstructClientRegistry.getManualIcon("patternchest"));
        TSClientRegistry.registerManualIcon("stenciltable", TConstructClientRegistry.getManualIcon("stenciltable"));

        TSClientRegistry.registerManualIcon("workbench", TConstructClientRegistry.getManualIcon("workbench"));
        TSClientRegistry.registerManualIcon("coal", TConstructClientRegistry.getManualIcon("coal"));

        // Tool Materials
        TSClientRegistry.registerManualIcon("steelingot", TConstructClientRegistry.getManualIcon("steelingot"));
        TSClientRegistry.registerManualIcon("pigironingot", TConstructClientRegistry.getManualIcon("pigironingot"));

        // Tool parts
        TSClientRegistry.registerManualIcon("pickhead", TConstructClientRegistry.getManualIcon("pickhead"));
        TSClientRegistry.registerManualIcon("shovelhead", TConstructClientRegistry.getManualIcon("shovelhead"));
        TSClientRegistry.registerManualIcon("axehead", TConstructClientRegistry.getManualIcon("axehead"));
        TSClientRegistry.registerManualIcon("swordblade", TConstructClientRegistry.getManualIcon("swordblade"));
        TSClientRegistry.registerManualIcon("pan", TConstructClientRegistry.getManualIcon("pan"));
        TSClientRegistry.registerManualIcon("board", TConstructClientRegistry.getManualIcon("board"));
        TSClientRegistry.registerManualIcon("knifeblade", TConstructClientRegistry.getManualIcon("knifeblade"));
        TSClientRegistry.registerManualIcon("chiselhead", TConstructClientRegistry.getManualIcon("chiselhead"));

        TSClientRegistry.registerManualIcon("hammerhead", TConstructClientRegistry.getManualIcon("hammerhead"));
        TSClientRegistry.registerManualIcon("excavatorhead", TConstructClientRegistry.getManualIcon("excavatorhead"));
        TSClientRegistry.registerManualIcon("scythehead", TConstructClientRegistry.getManualIcon("scythehead"));
        TSClientRegistry.registerManualIcon("broadaxehead", TConstructClientRegistry.getManualIcon("broadaxehead"));
        TSClientRegistry.registerManualIcon("largeswordblade", TConstructClientRegistry.getManualIcon("largeswordblade"));

        TSClientRegistry.registerManualIcon("toolrod", TConstructClientRegistry.getManualIcon("toolrod"));

        TSClientRegistry.registerManualIcon("binding", TConstructClientRegistry.getManualIcon("binding"));
        TSClientRegistry.registerManualIcon("wideguard", TConstructClientRegistry.getManualIcon("wideguard"));
        TSClientRegistry.registerManualIcon("handguard", TConstructClientRegistry.getManualIcon("handguard"));
        TSClientRegistry.registerManualIcon("crossbar", TConstructClientRegistry.getManualIcon("crossbar"));

        TSClientRegistry.registerManualIcon("toughrod", TConstructClientRegistry.getManualIcon("toughrod"));
        TSClientRegistry.registerManualIcon("toughbinding", TConstructClientRegistry.getManualIcon("toughbinding"));
        TSClientRegistry.registerManualIcon("largeplate", TConstructClientRegistry.getManualIcon("largeplate"));

        TSClientRegistry.registerManualIcon("bowstring", TConstructClientRegistry.getManualIcon("bowstring"));
        TSClientRegistry.registerManualIcon("arrowhead", TConstructClientRegistry.getManualIcon("arrowhead"));
        TSClientRegistry.registerManualIcon("fletching", TConstructClientRegistry.getManualIcon("fletching"));
        
        TSClientRegistry.registerManualIcon("bloodbucket", TConstructClientRegistry.getManualIcon("bloodbucket"));
        TSClientRegistry.registerManualIcon("emeraldbucket", TConstructClientRegistry.getManualIcon("emeraldbucket"));
        TSClientRegistry.registerManualIcon("gluebucket", TConstructClientRegistry.getManualIcon("gluebucket"));
        TSClientRegistry.registerManualIcon("slimebucket", TConstructClientRegistry.getManualIcon("slimebucket"));
        TSClientRegistry.registerManualIcon("enderbucket", TConstructClientRegistry.getManualIcon("enderbucket"));

        // ToolIcons
        TSClientRegistry.registerManualIcon("pickicon", TConstructClientRegistry.getManualIcon("pickicon"));
        TSClientRegistry.registerManualIcon("shovelicon", TConstructClientRegistry.getManualIcon("shovelicon"));
        TSClientRegistry.registerManualIcon("axeicon", TConstructClientRegistry.getManualIcon("axeicon"));
        TSClientRegistry.registerManualIcon("mattockicon", TConstructClientRegistry.getManualIcon("mattockicon"));
        TSClientRegistry.registerManualIcon("swordicon", TConstructClientRegistry.getManualIcon("swordicon"));
        TSClientRegistry.registerManualIcon("longswordicon", TConstructClientRegistry.getManualIcon("longswordicon"));
        TSClientRegistry.registerManualIcon("rapiericon", TConstructClientRegistry.getManualIcon("rapiericon"));
        TSClientRegistry.registerManualIcon("daggerIcon", TConstructClientRegistry.getManualIcon("daggerIcon"));
        TSClientRegistry.registerManualIcon("frypanicon", TConstructClientRegistry.getManualIcon("frypanicon"));
        TSClientRegistry.registerManualIcon("battlesignicon", TConstructClientRegistry.getManualIcon("battlesignicon"));
        TSClientRegistry.registerManualIcon("chiselicon", TConstructClientRegistry.getManualIcon("chiselicon"));
        TSClientRegistry.registerManualIcon("shortbowIcon", TConstructClientRegistry.getManualIcon("shortbowIcon"));
        TSClientRegistry.registerManualIcon("arrowIcon", TConstructClientRegistry.getManualIcon("arrowIcon"));

        TSClientRegistry.registerManualIcon("hammericon", TConstructClientRegistry.getManualIcon("hammericon"));
        TSClientRegistry.registerManualIcon("lumbericon", TConstructClientRegistry.getManualIcon("lumbericon"));
        TSClientRegistry.registerManualIcon("excavatoricon", TConstructClientRegistry.getManualIcon("excavatoricon"));
        TSClientRegistry.registerManualIcon("scytheicon", TConstructClientRegistry.getManualIcon("scytheicon"));
        TSClientRegistry.registerManualIcon("cleavericon", TConstructClientRegistry.getManualIcon("cleavericon"));
        TSClientRegistry.registerManualIcon("battleaxeicon", TConstructClientRegistry.getManualIcon("battleaxeicon"));
    }
    
    public void initManualRecipes ()
    {
        ItemStack charcoal = new ItemStack(Item.coal, 1, 1);
        ItemStack sand = new ItemStack(Block.sand, 1, 0);
        ItemStack redstoneDust = new ItemStack(Item.redstone);
        ItemStack gunpowderDust = new ItemStack(Item.gunpowder);
        ItemStack charcoalBlock = new ItemStack(TSContent.charcoalBlock);
        ItemStack gunpowderBlock = new ItemStack(TSContent.dustStorageBlock, 1, 0);
        ItemStack sugarBlock = new ItemStack(TSContent.dustStorageBlock, 1, 1);
        
        ItemStack brick = new ItemStack(Item.brick);
        ItemStack brickBlock = new ItemStack(Block.brick);
        
        ItemStack scorchedbrick = new ItemStack(TSContent.materialsTS);
        ItemStack scorchedbrickBlock = new ItemStack(TSContent.highoven, 1, 2);
        
        TSClientRegistry.registerManualSmeltery("scorchedbrickcasting", scorchedbrick, 
                                                new ItemStack(TContent.moltenStone, 1), brick);
        TSClientRegistry.registerManualSmeltery("scorchedbrickblockcasting", scorchedbrickBlock, 
                                                new ItemStack(TContent.moltenStone, 1), brickBlock);
        
        TSClientRegistry.registerManualSmallRecipe("scorchedbrickblock", new ItemStack(TSContent.highoven, 1, 2), 
                                                   scorchedbrick, scorchedbrick, scorchedbrick, scorchedbrick);
        
        TSClientRegistry.registerManualLargeRecipe("highovencontroller", new ItemStack(TSContent.highoven, 1, 0), 
                                                   scorchedbrick, scorchedbrick, scorchedbrick, 
                                                   scorchedbrick, null, scorchedbrick, 
                                                   scorchedbrick, scorchedbrick, scorchedbrick);
        TSClientRegistry.registerManualLargeRecipe("highovenydrain", new ItemStack(TSContent.highoven, 1, 1), 
                                                   scorchedbrick, null, scorchedbrick, 
                                                   scorchedbrick, null, scorchedbrick, 
                                                   scorchedbrick, null, scorchedbrick);
        TSClientRegistry.registerManualLargeRecipe("charcoalblock", charcoalBlock, 
                                                   charcoal, charcoal, charcoal, 
                                                   charcoal, charcoal, charcoal, 
                                                   charcoal, charcoal, charcoal);
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
        TSClientProxy.registerManualPage("picture", TSPicturePage.class);
        TSClientProxy.registerManualPage("text", TSTextPage.class);
        TSClientProxy.registerManualPage("intro", TSTextPage.class);
        TSClientProxy.registerManualPage("sectionpage", TSSectionPage.class);
        TSClientProxy.registerManualPage("intro", TSTitlePage.class);
        TSClientProxy.registerManualPage("contents", TSContentsTablePage.class);
        TSClientProxy.registerManualPage("sidebar", TSSidebarPage.class);
        TSClientProxy.registerManualPage("crafting", TSCraftingPage.class);
        TSClientProxy.registerManualPage("furnace", TSFurnacePage.class);
        TSClientProxy.registerManualPage("materialstats", TSMaterialPage.class);
        TSClientProxy.registerManualPage("toolpage", TSToolPage.class);
        TSClientProxy.registerManualPage("modifier", TSModifierPage.class);
        TSClientProxy.registerManualPage("blockcast", TSBlockCastPage.class);
        TSClientProxy.registerManualPage("blank", TSBlankPage.class);
    }
    
    public static Document getManualFromStack (ItemStack stack)
    {
        switch (stack.getItemDamage())
        {
        case 0:
            return highovenXml;
        }

        return null;
    }
    
    @Override
    public void registerSounds ()
    {}
}
