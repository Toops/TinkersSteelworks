package tsteelworks.client;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import org.w3c.dom.Document;

import tconstruct.TConstruct;
import tconstruct.common.TContent;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.client.TConstructClientRegistry;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.ToolCore;
import tsteelworks.TSteelworks;
import tsteelworks.client.block.DeepTankRender;
import tsteelworks.client.block.MachineRender;
import tsteelworks.client.block.SmallFontRenderer;
import tsteelworks.client.entity.RenderHighGolem;
import tsteelworks.client.entity.RenderSteelGolem;
import tsteelworks.client.pages.TSBlankPage;
import tsteelworks.client.pages.TSBlockCastPage;
import tsteelworks.client.pages.TSBookPage;
import tsteelworks.client.pages.TSCompound;
import tsteelworks.client.pages.TSContentsTablePage;
import tsteelworks.client.pages.TSCraftingPage;
import tsteelworks.client.pages.TSFurnacePage;
import tsteelworks.client.pages.TSHighOvenPage;
import tsteelworks.client.pages.TSMaterialPage;
import tsteelworks.client.pages.TSModifierPage;
import tsteelworks.client.pages.TSPicturePage;
import tsteelworks.client.pages.TSSectionPage;
import tsteelworks.client.pages.TSSidebarPage;
import tsteelworks.client.pages.TSTextPage;
import tsteelworks.client.pages.TSTitlePage;
import tsteelworks.client.pages.TSToolPage;
import tsteelworks.common.core.TSCommonProxy;
import tsteelworks.common.core.TSContent;
import tsteelworks.entity.HighGolem;
import tsteelworks.entity.SteelGolem;
import tsteelworks.entity.projectile.EntityLimestoneBrick;
import tsteelworks.entity.projectile.EntityScorchedBrick;
import tsteelworks.lib.client.TSClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class TSClientProxy extends TSCommonProxy
{
    public static Minecraft mc;
    public static SmallFontRenderer smallFontRenderer;
    public static RenderItem itemRenderer = new RenderItem();

    public static Document highovenXml;

    public static Map<String, Class<? extends TSBookPage>> pageClasses = new HashMap<String, Class<? extends TSBookPage>>();

    public static Document getManualFromStack (ItemStack stack)
    {
        switch (stack.getItemDamage())
        {
        case 0:
            return highovenXml;
        }

        return null;
    }

    public static Class<? extends TSBookPage> getPageClass (String type)
    {
        return pageClasses.get(type);
    }

    public static void registerManualPage (String type, Class<? extends TSBookPage> clazz)
    {
        pageClasses.put(type, clazz);
    }

    public void initManualIcons ()
    {
        // Blocks
        TSClientRegistry.registerManualIcon("highovenbook", new ItemStack(TSContent.bookManual, 1, 0));
        TSClientRegistry.registerManualIcon("highoven", new ItemStack(TSContent.highoven));
        TSClientRegistry.registerManualIcon("highovendrain", new ItemStack(TSContent.highoven, 1, 1));
        TSClientRegistry.registerManualIcon("highovenduct", new ItemStack(TSContent.highoven, 12, 1));
        TSClientRegistry.registerManualIcon("deeptank", new ItemStack(TSContent.highoven, 13, 1));
        TSClientRegistry.registerManualIcon("scorchedbrickblock", new ItemStack(TSContent.highoven, 1, 2));
        // Misc Blocks
        TSClientRegistry.registerManualIcon("charcoalblock", new ItemStack(TSContent.charcoalBlock));
        TSClientRegistry.registerManualIcon("gunpowderblock", new ItemStack(TSContent.dustStorageBlock, 1, 0));
        TSClientRegistry.registerManualIcon("sugarblock", new ItemStack(TSContent.dustStorageBlock, 1, 1));
        TSClientRegistry.registerManualIcon("spongeblock", new ItemStack(Block.sponge));
        TSClientRegistry.registerManualIcon("glassBlock", new ItemStack(Block.glass));
        TSClientRegistry.registerManualIcon("clearGlassBlock", new ItemStack(TContent.clearGlass));
        // Builing Materials
        TSClientRegistry.registerManualIcon("scorchedbrick", new ItemStack(TSContent.materialsTS, 1, 0));
        TSClientRegistry.registerManualIcon("netherquartz", new ItemStack(Item.netherQuartz, 1));
        // Component Materials
        TSClientRegistry.registerManualIcon("ironingot", new ItemStack(Item.ingotIron, 1, 0));
        TSClientRegistry.registerManualIcon("charcoal", new ItemStack(Item.coal, 1, 1));
        TSClientRegistry.registerManualIcon("gunpowderdust", new ItemStack(Item.gunpowder));
        TSClientRegistry.registerManualIcon("sugardust", new ItemStack(Item.sugar));
        TSClientRegistry.registerManualIcon("bonemeal", new ItemStack(Item.dyePowder, 1, 15));

        TSClientRegistry.registerManualIcon("redstonedust", new ItemStack(Item.redstone));
        TSClientRegistry.registerManualIcon("aluminumdust", new ItemStack(TContent.materials, 1, 40));
        TSClientRegistry.registerManualIcon("essenceberry", new ItemStack(TContent.oreBerries, 1, 5));
        TSClientRegistry.registerManualIcon("emeraldgem", new ItemStack(Item.emerald));
        TSClientRegistry.registerManualIcon("clayitem", new ItemStack(Item.clay));
        TSClientRegistry.registerManualIcon("sandblock", new ItemStack(Block.sand));
        TSClientRegistry.registerManualIcon("graveyardsoil", new ItemStack(TContent.craftedSoil, 1, 3));
        TSClientRegistry.registerManualIcon("hambone", new ItemStack(TContent.meatBlock, 1, 0));

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

    @SuppressWarnings ("unused")
    public void initManualRecipes ()
    {
        final ItemStack charcoal = new ItemStack(Item.coal, 1, 1);

        final ItemStack ingotIron = new ItemStack(Item.ingotIron, 1);
        final ItemStack ingotSteel = TConstructRegistry.getItemStack("ingotSteel");
        final ItemStack dustGunpwoder = new ItemStack(Item.gunpowder, 1, 0);
        final ItemStack dustRedstone = new ItemStack(Item.redstone, 1, 0);
        final ItemStack dustAluminum = TConstructRegistry.getItemStack("dustAluminum");
        final ItemStack blockSand = new ItemStack(Block.sand, 1, 0);

        TSClientRegistry.registerManualHighOvenRecipe("steelsmelting", ingotSteel, ingotIron, dustGunpwoder, dustRedstone, blockSand);

        final ItemStack ingotPigIron = TConstructRegistry.getItemStack("ingotPigIron");
        final ItemStack dustSugar = new ItemStack(Item.sugar, 1, 0);
        final ItemStack bonemeal = new ItemStack(Item.dyePowder, 1, 15);
        final ItemStack blockHambone = new ItemStack(TContent.meatBlock, 1, 0);

        TSClientRegistry.registerManualHighOvenRecipe("pigironsmelting", ingotPigIron, ingotIron, dustSugar, bonemeal, blockHambone);

        final ItemStack scorchedbrick = new ItemStack(TSContent.materialsTS);
        final ItemStack stoneBlock = new ItemStack(Block.stone);
        final ItemStack coal = new ItemStack(Item.coal, 1, 0);

        TSClientRegistry.registerManualHighOvenRecipe("scorchedbricksmelting", scorchedbrick, stoneBlock, coal, null, new ItemStack(Block.sand, 1, 0));

        final ItemStack netherquartz = new ItemStack(Item.netherQuartz);
        final ItemStack essenceberry = new ItemStack(TContent.oreBerries, 1, 5);
        final ItemStack graveyardsoil = new ItemStack(TContent.craftedSoil, 1, 3);

        TSClientRegistry.registerManualHighOvenRecipe("netherquartzsmelting", netherquartz, new ItemStack(Block.sand, 1, 0), dustGunpwoder, essenceberry, graveyardsoil);

        // Modifier recipes
        ItemStack ironpick = ToolBuilder.instance.buildTool(new ItemStack(TContent.pickaxeHead, 1, 6), new ItemStack(TContent.toolRod, 1, 2), new ItemStack(TContent.binding, 1, 6), "");
        TSClientRegistry.registerManualIcon("ironpick", ironpick);
        TSClientRegistry.registerManualModifier("vacuousmod", ironpick.copy(), new ItemStack(Block.hopperBlock), new ItemStack(Item.enderPearl));

        final ItemStack lapis = new ItemStack(Item.dyePowder, 1, 4);

        final ItemStack charcoalBlock = new ItemStack(TSContent.charcoalBlock);
        final ItemStack gunpowderBlock = new ItemStack(TSContent.dustStorageBlock, 1, 0);
        final ItemStack sugarBlock = new ItemStack(TSContent.dustStorageBlock, 1, 1);

        final ItemStack brick = new ItemStack(Item.brick);
        final ItemStack brickBlock = new ItemStack(Block.brick);

        final ItemStack scorchedbrickBlock = new ItemStack(TSContent.highoven, 1, 2);

        TSClientRegistry.registerManualSmeltery("scorchedbrickcasting", scorchedbrick, new ItemStack(TContent.moltenStone, 1), brick);
        TSClientRegistry.registerManualSmeltery("scorchedbrickblockcasting", scorchedbrickBlock, new ItemStack(TContent.moltenStone, 1), brickBlock);

        TSClientRegistry.registerManualSmallRecipe("scorchedbrickblock", new ItemStack(TSContent.highoven, 1, 2),
                                                                            scorchedbrick, scorchedbrick,
                                                                            scorchedbrick, scorchedbrick);
        TSClientRegistry.registerManualLargeRecipe("highovencontroller", new ItemStack(TSContent.highoven, 1, 0),
                                                                            scorchedbrick, scorchedbrick, scorchedbrick,
                                                                            scorchedbrick, null, scorchedbrick,
                                                                            scorchedbrick, scorchedbrick, scorchedbrick);
        TSClientRegistry.registerManualLargeRecipe("highovenydrain", new ItemStack(TSContent.highoven, 1, 1),
                                                                        scorchedbrick, null, scorchedbrick,
                                                                        scorchedbrick, null, scorchedbrick,
                                                                        scorchedbrick, null, scorchedbrick);
        TSClientRegistry.registerManualLargeRecipe("highovenyduct", new ItemStack(TSContent.highoven, 1, 12),
                                                                        scorchedbrick, scorchedbrick, scorchedbrick,
                                                                        null, null, null,
                                                                        scorchedbrick, scorchedbrick, scorchedbrick);
        TSClientRegistry.registerManualLargeRecipe("deeptank", new ItemStack(TSContent.highoven, 1, 13),
                                                                    scorchedbrick, scorchedbrick, scorchedbrick,
                                                                    scorchedbrick, lapis, scorchedbrick,
                                                                    scorchedbrick, scorchedbrick, scorchedbrick);

        TSClientRegistry.registerManualLargeRecipe("charcoalblock", charcoalBlock,
                                                                    charcoal, charcoal, charcoal,
                                                                    charcoal, charcoal, charcoal,
                                                                    charcoal, charcoal, charcoal);
        TSClientRegistry.registerManualLargeRecipe("gunpowderblock", gunpowderBlock,
                                                                    dustGunpwoder, dustGunpwoder, dustGunpwoder,
                                                                    dustGunpwoder, dustGunpwoder, dustGunpwoder,
                                                                    dustGunpwoder, dustGunpwoder, dustGunpwoder);
        TSClientRegistry.registerManualLargeRecipe("sugarcube", sugarBlock,
                                                                    dustSugar, dustSugar, dustSugar,
                                                                    dustSugar, dustSugar, dustSugar,
                                                                    dustSugar, dustSugar, dustSugar);
    }

    void addRenderMappings ()
    {
        String[] effectTypes = { "hopper" };

        for (ToolCore tool : TConstructRegistry.getToolMapping())
        {
            for (int i = 0; i < 0 + effectTypes.length; i++)
            {
                TConstructClientRegistry.addEffectRenderMapping(tool, i + 50, "tsteelworks", effectTypes[i], true);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see tsteelworks.common.core.TSCommonProxy#postInit()
     */
    @Override
    public void postInit ()
    {
    }

    /*
     * (non-Javadoc)
     * @see tsteelworks.common.core.TSCommonProxy#readManuals()
     */
    @Override
    public void readManuals ()
    {
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        highovenXml = readManual("/assets/tsteelworks/manuals/highoven.xml", dbFactory);
        initManualIcons();
        initManualRecipes();
        initManualPages();
    }

    /*
     * (non-Javadoc)
     * @see tsteelworks.common.core.TSCommonProxy#registerRenderer()
     */
    @Override
    public void registerRenderer ()
    {
        final Minecraft mc = Minecraft.getMinecraft();
        MinecraftForge.EVENT_BUS.register(new TSClientEvents());
        smallFontRenderer = new SmallFontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.renderEngine, false);
        RenderingRegistry.registerEntityRenderingHandler(HighGolem.class, new RenderHighGolem());
        RenderingRegistry.registerEntityRenderingHandler(SteelGolem.class, new RenderSteelGolem());
        RenderingRegistry.registerEntityRenderingHandler(EntityScorchedBrick.class, new RenderSnowball(TSContent.materialsTS));
        RenderingRegistry.registerEntityRenderingHandler(EntityLimestoneBrick.class, new RenderSnowball(TSContent.materialsTS, 1));
        RenderingRegistry.registerBlockHandler(new DeepTankRender());
        RenderingRegistry.registerBlockHandler(new MachineRender());

        addRenderMappings();
    }

    /*
     * (non-Javadoc)
     * @see tsteelworks.common.core.TSCommonProxy#registerSounds()
     */
    @Override
    public void registerSounds ()
    {
    }

    /*
     * (non-Javadoc)
     * @see tsteelworks.common.core.TSCommonProxy#spawnParticle(java.lang.String, double, double, double, double, double, double)
     */
    @Override
    public void spawnParticle (String particle, double xPos, double yPos, double zPos, double velX, double velY, double velZ)
    {
        if ( "scorchedbrick".equals(particle) || "limestonebrick".equals(particle))
            doSpawnParticle(particle, xPos, yPos, zPos, velX, velY, velZ);
        else
            TConstruct.proxy.spawnParticle(particle, xPos, yPos, zPos, velX, velY, velZ);

    }

    public EntityFX doSpawnParticle (String par1Str, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        if (mc == null)
            mc = Minecraft.getMinecraft();

        if ((mc.renderViewEntity != null) && (mc.effectRenderer != null))
        {
            int i = mc.gameSettings.particleSetting;

            if ((i == 1) && (mc.theWorld.rand.nextInt(3) == 0))
                i = 2;

            final double d6 = mc.renderViewEntity.posX - par2;
            final double d7 = mc.renderViewEntity.posY - par4;
            final double d8 = mc.renderViewEntity.posZ - par6;
            EntityFX entityfx = null;

            final double d9 = 16.0D;

            if (((d6 * d6) + (d7 * d7) + (d8 * d8)) > (d9 * d9))
                return null;
            else if (i > 1)
                return null;
            else
            {
                if (par1Str.equals("scorchedbrick"))
                    entityfx = new EntityBreakingFX(mc.theWorld, par2, par4, par6, TSContent.materialsTS);
                if (par1Str.equals("limestonebrick"))
                    entityfx = new EntityBreakingFX(mc.theWorld, par2, par4, par6, TSContent.materialsTS, 1);

                if (entityfx != null)
                    mc.effectRenderer.addEffect(entityfx);

                return entityfx;
            }
        }
        else
            return null;
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
        TSClientProxy.registerManualPage("compound", TSCompound.class);
        TSClientProxy.registerManualPage("crafting", TSCraftingPage.class);
        TSClientProxy.registerManualPage("furnace", TSFurnacePage.class);
        TSClientProxy.registerManualPage("highoven", TSHighOvenPage.class);
        TSClientProxy.registerManualPage("materialstats", TSMaterialPage.class);
        TSClientProxy.registerManualPage("toolpage", TSToolPage.class);
        TSClientProxy.registerManualPage("modifier", TSModifierPage.class);
        TSClientProxy.registerManualPage("blockcast", TSBlockCastPage.class);
        TSClientProxy.registerManualPage("blank", TSBlankPage.class);
    }

    Document readManual (String location, DocumentBuilderFactory dbFactory)
    {
        try
        {
            final InputStream stream = TSteelworks.class.getResourceAsStream(location);
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            final Document doc = dBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            return doc;
        }
        catch (final Exception e)
        {
            TSteelworks.logError("an error occured", e);
            return null;
        }
    }
}
