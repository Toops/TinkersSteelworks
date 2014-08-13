package tsteelworks.client;

import cpw.mods.fml.client.registry.RenderingRegistry;
import mantle.books.BookData;
import mantle.client.MProxyClient;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import tconstruct.client.TProxyClient;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.client.TConstructClientRegistry;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.ToolCore;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;
import tconstruct.world.TinkerWorld;
import tsteelworks.client.block.DeepTankRender;
import tsteelworks.client.entity.RenderHighGolem;
import tsteelworks.client.entity.RenderSteelGolem;
import tsteelworks.client.pages.TSHighOvenPage;
import tsteelworks.common.core.TSCommonProxy;
import tsteelworks.common.core.TSContent;
import tsteelworks.common.core.TSRepo;
import tsteelworks.common.entity.HighGolem;
import tsteelworks.common.entity.SteelGolem;
import tsteelworks.common.entity.projectile.EntityLimestoneBrick;
import tsteelworks.common.entity.projectile.EntityScorchedBrick;
import tsteelworks.lib.client.TSClientRegistry;

// todo: reformat XML to match mantle & TiC pages
public class TSClientProxy extends TSCommonProxy {
	public static BookData highOvenBook;

	public static BookData getManualFromStack(ItemStack stack) {
		switch (stack.getItemDamage()) {
			case 0:
				return highOvenBook;
		}

		return null;
	}

	public void initManualIcons() {
		// Blocks
		MantleClientRegistry.registerManualIcon("highovenbook", new ItemStack(TSContent.bookManual, 1, 0));
		MantleClientRegistry.registerManualIcon("highoven", new ItemStack(TSContent.highoven));
		MantleClientRegistry.registerManualIcon("highovendrain", new ItemStack(TSContent.highoven, 1, 1));
		MantleClientRegistry.registerManualIcon("highovenduct", new ItemStack(TSContent.highoven, 12, 1));
		MantleClientRegistry.registerManualIcon("deeptank", new ItemStack(TSContent.highoven, 13, 1));
		MantleClientRegistry.registerManualIcon("scorchedbrickblock", new ItemStack(TSContent.highoven, 1, 2));

		// Misc Blocks
		MantleClientRegistry.registerManualIcon("charcoalblock", TSContent.charcoalBlock);
		MantleClientRegistry.registerManualIcon("gunpowderblock", new ItemStack(TSContent.dustStorageBlock, 1, 0));
		MantleClientRegistry.registerManualIcon("sugarblock", new ItemStack(TSContent.dustStorageBlock, 1, 1));
		MantleClientRegistry.registerManualIcon("spongeblock", new ItemStack(Blocks.sponge));
		MantleClientRegistry.registerManualIcon("glassBlock", new ItemStack(Blocks.glass));
		MantleClientRegistry.registerManualIcon("clearGlassBlock", new ItemStack(TinkerSmeltery.clearGlass));

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
		MantleClientRegistry.registerManualIcon("aluminumdust", new ItemStack(TinkerTools.materials, 1, 40));
		MantleClientRegistry.registerManualIcon("essenceberry", new ItemStack(TinkerWorld.oreBerries, 1, 5));
		MantleClientRegistry.registerManualIcon("emeraldgem", new ItemStack(Items.emerald));
		MantleClientRegistry.registerManualIcon("clayitem", new ItemStack(Items.clay_ball));
		MantleClientRegistry.registerManualIcon("sandblock", new ItemStack(Blocks.sand));
		MantleClientRegistry.registerManualIcon("graveyardsoil", new ItemStack(TinkerTools.craftedSoil, 1, 3));
		MantleClientRegistry.registerManualIcon("hambone", new ItemStack(TinkerWorld.meatBlock, 1, 0));
	}

	public void initManualRecipes() {
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
		ItemStack ironpick = ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.pickaxeHead, 1, 6), new ItemStack(TinkerTools.toolRod, 1, 2), new ItemStack(TinkerTools.binding, 1, 6), "");

		MantleClientRegistry.registerManualIcon("ironpick", ironpick);
		TConstructClientRegistry.registerManualModifier("vacuousmod", ironpick.copy(), new ItemStack(Blocks.hopper), new ItemStack(Items.ender_pearl));

		final ItemStack lapis = new ItemStack(Items.dye, 1, 4);

		final ItemStack charcoalBlock = TSContent.charcoalBlock;
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

	void addRenderMappings() {
		String[] effectTypes = { "hopper" };

		for (ToolCore tool : TConstructRegistry.getToolMapping()) {
			for (int i = 0; i < effectTypes.length; i++) {
				TConstructClientRegistry.addEffectRenderMapping(tool, i + 50, "tsteelworks", effectTypes[i], true);
			}
		}
	}

	@Override
	public void readManuals() {
		highOvenBook = new tsteelworks.lib.BookData("/assets/tsteelworks/manuals/highoven.xml");
		highOvenBook.unlocalizedName = "high_oven_manual";
		highOvenBook.font = TProxyClient.smallFontRenderer;
		highOvenBook.modID = TSRepo.MOD_ID;

		initManualIcons();
		initManualRecipes();
		initManualPages();
	}

	@Override
	public void registerRenderer() {
		RenderingRegistry.registerEntityRenderingHandler(HighGolem.class, new RenderHighGolem());
		RenderingRegistry.registerEntityRenderingHandler(SteelGolem.class, new RenderSteelGolem());
		RenderingRegistry.registerEntityRenderingHandler(EntityScorchedBrick.class, new RenderSnowball(TSContent.materialsTS));
		RenderingRegistry.registerEntityRenderingHandler(EntityLimestoneBrick.class, new RenderSnowball(TSContent.materialsTS, 1));
		RenderingRegistry.registerBlockHandler(new DeepTankRender());

		addRenderMappings();
	}

	public void spawnParticle(String particle, double xPos, double yPos, double zPos, double velX, double velY, double velZ) {
		// todo: rollback changes but move this to a particle handler thing
		if ("scorchedbrick".equals(particle) || "limestonebrick".equals(particle))
			doSpawnParticle(particle, xPos, yPos, zPos, velX, velY, velZ);
		else
			TinkerWorld.proxy.spawnParticle(particle, xPos, yPos, zPos, velX, velY, velZ);

	}

	// todo: move this to lib & add a particle registry
	public EntityFX doSpawnParticle(String par1Str, double par2, double par4, double par6, double par8, double par10, double par12) {
		Minecraft mc = Minecraft.getMinecraft();

		if ((mc.renderViewEntity != null) && (mc.effectRenderer != null)) {
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
			else {
				if (par1Str.equals("scorchedbrick"))
					entityfx = new EntityBreakingFX(mc.theWorld, par2, par4, par6, TSContent.materialsTS);
				if (par1Str.equals("limestonebrick"))
					entityfx = new EntityBreakingFX(mc.theWorld, par2, par4, par6, TSContent.materialsTS, 1);

				if (entityfx != null)
					mc.effectRenderer.addEffect(entityfx);

				return entityfx;
			}
		}

		return null;
	}

	private void initManualPages() {
		MProxyClient.registerManualPage("highoven", TSHighOvenPage.class);
	}
}
