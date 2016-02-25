package toops.tsteelworks.client.core;

import cpw.mods.fml.client.registry.RenderingRegistry;
import mantle.books.BookData;
import mantle.client.MProxyClient;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.client.TProxyClient;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.client.TConstructClientRegistry;
import tconstruct.library.tools.ToolCore;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;
import tconstruct.world.TinkerWorld;
import toops.tsteelworks.client.block.DeepTankRender;
import toops.tsteelworks.client.entity.RenderHighGolem;
import toops.tsteelworks.client.entity.RenderSteelGolem;
import toops.tsteelworks.client.pages.TSHighOvenPage;
import toops.tsteelworks.client.pages.TSPicturePage;
import toops.tsteelworks.client.particle.TSParticle;
import toops.tsteelworks.common.core.TSCommonProxy;
import toops.tsteelworks.common.core.TSContent;
import toops.tsteelworks.common.entity.HighGolem;
import toops.tsteelworks.common.entity.SteelGolem;
import toops.tsteelworks.common.entity.projectile.EntityLimestoneBrick;
import toops.tsteelworks.common.entity.projectile.EntityScorchedBrick;
import toops.tsteelworks.lib.TSRepo;
import toops.tsteelworks.lib.registry.TSClientRegistry;

public class TSClientProxy extends TSCommonProxy {
	public static final TSParticle PARTICLE_HANDLER = new TSParticle();
	public static BookData highOvenBook;

	public static BookData getManualFromStack(ItemStack stack) {
		switch (stack.getItemDamage()) {
			case 0:
				return highOvenBook;
		}

		return null;
	}

	public static void readManuals() {
		String currentLanguage = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();

		highOvenBook = toops.tsteelworks.lib.util.BookData.newInstance("/assets/tsteelworks/manuals/" + currentLanguage + "/highoven.xml");
		if (highOvenBook == null)
			highOvenBook = toops.tsteelworks.lib.util.BookData.newInstance("/assets/tsteelworks/manuals/en_US/highoven.xml");

		highOvenBook.unlocalizedName = "high_oven_manual";
		highOvenBook.font = TProxyClient.smallFontRenderer;
		highOvenBook.modID = TSRepo.MOD_ID;
	}

	@Override
	public void preInit() {
		super.preInit();

		TSClientProxy.readManuals();
	}

	@Override
	public void init() {
		super.init();

		registerRenderer();
	}

	@Override
	public void postInit() {
		super.postInit();

		initManualIcons();
		initManualRecipes();
		initManualPages();

		MinecraftForge.EVENT_BUS.register(new TSEventHandler());
	}

	private void initManualIcons() {
		// Blocks
		MantleClientRegistry.registerManualIcon("highovenbook", new ItemStack(TSContent.bookManual, 1, 0));
		MantleClientRegistry.registerManualIcon("highoven", new ItemStack(TSContent.highoven));
		MantleClientRegistry.registerManualIcon("highovendrain", new ItemStack(TSContent.highoven, 1, 1));
		MantleClientRegistry.registerManualIcon("highovenduct", new ItemStack(TSContent.highoven, 12, 1));
		MantleClientRegistry.registerManualIcon("deeptank", new ItemStack(TSContent.highoven, 13, 1));
		MantleClientRegistry.registerManualIcon("scorchedbrickblock", new ItemStack(TSContent.highoven, 1, 2));

		// Misc Blocks
		MantleClientRegistry.registerManualIcon("charcoalblock", new ItemStack(TSContent.tsCharcoalBlock));
		MantleClientRegistry.registerManualIcon("gunpowderblock", new ItemStack(TSContent.dustStorageBlock, 1, 0));
		MantleClientRegistry.registerManualIcon("sugarblock", new ItemStack(TSContent.dustStorageBlock, 1, 1));
		MantleClientRegistry.registerManualIcon("spongeblock", new ItemStack(Blocks.sponge));
		MantleClientRegistry.registerManualIcon("glassBlock", new ItemStack(Blocks.glass));

		// Builing Materials
		MantleClientRegistry.registerManualIcon("scorchedbrick", new ItemStack(TSContent.materialsTS, 1, 0));
		MantleClientRegistry.registerManualIcon("netherquartz", new ItemStack(Items.quartz, 1));

		// Component Materials
		MantleClientRegistry.registerManualIcon("ironingot", new ItemStack(Items.iron_ingot, 1, 0));
		MantleClientRegistry.registerManualIcon("charcoal", new ItemStack(Items.coal, 1, 1));
		MantleClientRegistry.registerManualIcon("gunpowderdust", new ItemStack(Items.gunpowder));
		MantleClientRegistry.registerManualIcon("sugardust", new ItemStack(Items.sugar));
		MantleClientRegistry.registerManualIcon("bonemeal", new ItemStack(Items.dye, 1, 15));

		MantleClientRegistry.registerManualIcon("redstonedust", new ItemStack(Items.redstone));
		MantleClientRegistry.registerManualIcon("emeraldgem", new ItemStack(Items.emerald));
		MantleClientRegistry.registerManualIcon("clayitem", new ItemStack(Items.clay_ball));
		MantleClientRegistry.registerManualIcon("sandblock", new ItemStack(Blocks.sand));

		MantleClientRegistry.registerManualIcon("clearGlassBlock", new ItemStack(TinkerSmeltery.clearGlass));

		MantleClientRegistry.registerManualIcon("graveyardsoil", new ItemStack(TinkerTools.craftedSoil, 1, 3));
		MantleClientRegistry.registerManualIcon("aluminumdust", new ItemStack(TinkerTools.materials, 1, 40));

		MantleClientRegistry.registerManualIcon("essenceberry", new ItemStack(TinkerWorld.oreBerries, 1, 5));
		MantleClientRegistry.registerManualIcon("hambone", new ItemStack(TinkerWorld.meatBlock, 1, 0));
	}

	private void initManualRecipes() {
		// todo: store names in the AdvancedSmelting registry and fetch from here

		final ItemStack ingotIron = new ItemStack(Items.iron_ingot, 1);
		final ItemStack ingotSteel = TConstructRegistry.getItemStack("ingotSteel");
		final ItemStack dustGunpwoder = new ItemStack(Items.gunpowder, 1, 0);
		final ItemStack dustRedstone = new ItemStack(Items.redstone, 1, 0);
		final ItemStack blockSand = new ItemStack(Blocks.sand, 1, 0);

		TSClientRegistry.registerManualHighOvenRecipe("steelsmelting", ingotSteel, ingotIron, dustGunpwoder, dustRedstone, blockSand);

		final ItemStack ingotPigIron = TConstructRegistry.getItemStack("ingotPigIron");
		final ItemStack dustSugar = new ItemStack(Items.sugar, 1, 0);
		final ItemStack bonemeal = new ItemStack(Items.dye, 1, 15);
		final ItemStack blockHambone = new ItemStack(TinkerWorld.meatBlock, 1, 0);

		TSClientRegistry.registerManualHighOvenRecipe("pigironsmelting", ingotPigIron, ingotIron, dustSugar, bonemeal, blockHambone);

		final ItemStack scorchedbrick = new ItemStack(TSContent.materialsTS);
		final ItemStack stoneBlock = new ItemStack(Blocks.stone);
		final ItemStack coal = new ItemStack(Items.coal, 1, 0);

		TSClientRegistry.registerManualHighOvenRecipe("scorchedbricksmelting", scorchedbrick, stoneBlock, coal, null, blockSand);

		final ItemStack netherquartz = new ItemStack(Items.quartz);
		final ItemStack essenceberry = new ItemStack(TinkerWorld.oreBerries, 1, 5);
		final ItemStack graveyardsoil = new ItemStack(TinkerTools.craftedSoil, 1, 3);

		TSClientRegistry.registerManualHighOvenRecipe("netherquartzsmelting", netherquartz, blockSand, dustGunpwoder, essenceberry, graveyardsoil);

		// end todo

		// Modifier recipes
		TConstructClientRegistry.registerManualModifier("vacuousmod", MantleClientRegistry.getManualIcon("pickicon"), new ItemStack(Blocks.hopper), new ItemStack(Items.ender_pearl));

		final ItemStack lapis = new ItemStack(Items.dye, 1, 4);

		final ItemStack charcoalBlock = new ItemStack(TSContent.tsCharcoalBlock);
		final ItemStack gunpowderBlock = new ItemStack(TSContent.dustStorageBlock, 1, 0);
		final ItemStack sugarBlock = new ItemStack(TSContent.dustStorageBlock, 1, 1);

		final ItemStack brick = new ItemStack(Items.brick);
		final ItemStack brickBlock = new ItemStack(Blocks.brick_block);

		final ItemStack scorchedbrickBlock = new ItemStack(TSContent.highoven, 1, 2);

		final ItemStack charcoal = new ItemStack(Items.coal, 1, 1);

		TConstructClientRegistry.registerManualSmeltery("scorchedbrickcasting", scorchedbrick, new ItemStack(TinkerSmeltery.moltenStone, 1), brick);
		TConstructClientRegistry.registerManualSmeltery("scorchedbrickblockcasting", scorchedbrickBlock, new ItemStack(TinkerSmeltery.moltenStone, 1), brickBlock);

		MantleClientRegistry.registerManualSmallRecipe("scorchedbrickblock", new ItemStack(TSContent.highoven, 1, 2), scorchedbrick, scorchedbrick, scorchedbrick, scorchedbrick);
		MantleClientRegistry.registerManualLargeRecipe("highovencontroller", new ItemStack(TSContent.highoven, 1, 0), scorchedbrick, scorchedbrick, scorchedbrick, scorchedbrick, null, scorchedbrick, scorchedbrick, scorchedbrick, scorchedbrick);
		MantleClientRegistry.registerManualLargeRecipe("highovenydrain", new ItemStack(TSContent.highoven, 1, 1), scorchedbrick, null, scorchedbrick, scorchedbrick, null, scorchedbrick, scorchedbrick, null, scorchedbrick);
		MantleClientRegistry.registerManualLargeRecipe("highovenyduct", new ItemStack(TSContent.highoven, 1, 12), scorchedbrick, scorchedbrick, scorchedbrick, null, null, null, scorchedbrick, scorchedbrick, scorchedbrick);
		MantleClientRegistry.registerManualLargeRecipe("deeptank", new ItemStack(TSContent.highoven, 1, 13), scorchedbrick, scorchedbrick, scorchedbrick, scorchedbrick, lapis, scorchedbrick, scorchedbrick, scorchedbrick, scorchedbrick);

		MantleClientRegistry.registerManualLargeRecipe("charcoalblock", charcoalBlock, charcoal, charcoal, charcoal, charcoal, charcoal, charcoal, charcoal, charcoal, charcoal);
		MantleClientRegistry.registerManualLargeRecipe("gunpowderblock", gunpowderBlock, dustGunpwoder, dustGunpwoder, dustGunpwoder, dustGunpwoder, dustGunpwoder, dustGunpwoder, dustGunpwoder, dustGunpwoder, dustGunpwoder);
		MantleClientRegistry.registerManualLargeRecipe("sugarcube", sugarBlock, dustSugar, dustSugar, dustSugar, dustSugar, dustSugar, dustSugar, dustSugar, dustSugar, dustSugar);
	}

	private void addRenderMappings() {
		String[] effectTypes = {"hopper"};

		for (ToolCore tool : TConstructRegistry.getToolMapping()) {
			for (int i = 0; i < effectTypes.length; i++) {
				TConstructClientRegistry.addEffectRenderMapping(tool, i + 50, "tsteelworks", effectTypes[i], true);
			}
		}
	}

	private void registerRenderer() {
		RenderingRegistry.registerEntityRenderingHandler(HighGolem.class, new RenderHighGolem());
		RenderingRegistry.registerEntityRenderingHandler(SteelGolem.class, new RenderSteelGolem());
		RenderingRegistry.registerEntityRenderingHandler(EntityScorchedBrick.class, new RenderSnowball(TSContent.materialsTS));
		RenderingRegistry.registerEntityRenderingHandler(EntityLimestoneBrick.class, new RenderSnowball(TSContent.materialsTS, 1));
		RenderingRegistry.registerBlockHandler(new DeepTankRender());

		addRenderMappings();
	}

	private void initManualPages() {
		MProxyClient.registerManualPage("highoven", TSHighOvenPage.class);

		// for some weird reason, it doesn't work with PiturePage. I don't understand, it's the same class.
		MProxyClient.registerManualPage("picture2", TSPicturePage.class);
	}
}
