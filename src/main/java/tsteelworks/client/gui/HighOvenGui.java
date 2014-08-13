package tsteelworks.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import nf.fr.ephys.cookiecore.util.MultiFluidTank;
import org.lwjgl.opengl.GL11;
import tsteelworks.common.blocks.logic.HighOvenLogic;
import tsteelworks.common.container.DeepTankContainer;
import tsteelworks.common.container.HighOvenContainer;
import tsteelworks.common.core.TSRecipes;

import java.util.ArrayList;
import java.util.List;

public class HighOvenGui extends GuiContainer {
	private static final int TANK_WIDTH = 35;
	private static final int TANK_HEIGHT = 52;

	public static final ResourceLocation BACKGROUND = new ResourceLocation("tsteelworks", "textures/gui/highoven.png");
	public static final ResourceLocation ICONS = new ResourceLocation("tsteelworks", "textures/gui/icons.png");

	public HighOvenGui(InventoryPlayer inventoryplayer, HighOvenLogic highoven) {
		super(new HighOvenContainer(inventoryplayer, highoven));

		xSize = 248;

		highoven.updateFuelDisplay();
	}

	protected void drawFluidStackTooltip(FluidStack liquid, int x, int z) {
		List<String> tooltips = getLiquidTooltip(liquid);

		drawHoveringText(tooltips, x, z, fontRendererObj);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(BACKGROUND);
		final int cornerX = ((width - xSize) / 2);
		final int cornerY = (height - ySize) / 2;
		drawTexturedModalRect(cornerX + 46, cornerY, 0, 0, 176, ySize);
		// Liquids - molten metal
		mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		int base = 0;
		for (final FluidStack liquid : logic.getFluidlist()) {
			final IIcon renderIndex = liquid.getFluid().getStillIcon();
			final int basePos = 179;
			if (logic.getCapacity() > 0) {
				final int total = logic.getTotalLiquid();
				final int liquidLayers = ((total / 20000) + 1) * 20000;
				if (liquidLayers > 0) {
					int liquidSize = (liquid.amount * 52) / liquidLayers;
					if (liquidSize == 0)
						liquidSize = 1;
					while (liquidSize > 0) {
						final int size = liquidSize >= 16 ? 16 : liquidSize;
						if (renderIndex != null) {
							drawLiquidRect(cornerX + basePos, (cornerY + 68) - size - base, renderIndex, 16, size);
							drawLiquidRect(cornerX + basePos + 16, (cornerY + 68) - size - base, renderIndex, 16, size);
							drawLiquidRect(cornerX + basePos + 32, (cornerY + 68) - size - base, renderIndex, 3, size);
						}

						liquidSize -= size;
						base += size;
					}
				}
			}
		}

		// Liquid gague
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(BACKGROUND);
		drawTexturedModalRect(cornerX + 179, cornerY + 16, 176, 76, 35, 52);

		int scale;
		// Burn progress
		if (logic.isBurning()) {
			scale = logic.getScaledFuelGauge(42);
			drawTexturedModalRect(cornerX + 127, (cornerY + 36 + 12) - scale, 176, 12 - scale, 14, scale + 2);
		}

		// Side inventory
		int slotSize = logic.getLayers();
		if (slotSize > 6)
			slotSize = 6;
		if (slotSize > 0) {
			// Draw Top
			drawTexturedModalRect(cornerX + 16, cornerY, 176, 14, 36, 6);
			// Iterate one slot at a time and draw it. Each slot is 18 px high.
			for (int iter = 0; iter < slotSize; iter++)
				drawTexturedModalRect(cornerX + 16, (cornerY + 6) + (iter * 18), 176, 21, 36, 18);//(iter * 18) + 18);
			final int dy = slotSize > 1 ? slotSize * 18 : 18;
			// Draw Bottom
			drawTexturedModalRect(cornerX + 16, cornerY + 6 + dy, 176, 39, 36, 7);
		}
		// Temperatures
		for (int iter = 0; iter < (slotSize + 4); iter++) {
			final int slotTemp = logic.getTempForSlot(iter + slotPos) - 20;
			final int maxTemp = logic.getMeltingPointForSlot(iter + slotPos) - 20;
			if ((slotTemp > 0) && (maxTemp > 0)) {
				final int size = ((16 * slotTemp) / maxTemp) + 1;
				drawTexturedModalRect(cornerX + 24, (cornerY + 7 + ((iter - 4) * 18) + 16) - size, 212, (14 + (15 + 16)) - size, 5, size);
			}
		}

		final String s = new String("\u00B0".toCharArray());
		final String temp = new String(logic.getInternalTemperature() + s + "c");
		fontRenderer.drawString(temp, (cornerX - (fontRenderer.getStringWidth(temp) / 2)) + 135, cornerY + 20, getTempColor());

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(ICONS);
		final int slotX = cornerX + 54;
		final int slotY = cornerY + 16;
		for (int i = 0; i < 3; i++)
			if (!logic.isStackInSlot(i))
				drawTexturedModalRect(slotX, slotY + (i * 18), i * 18, 234, 18, 18);
		if (!logic.isStackInSlot(3))
			drawTexturedModalRect(slotX + 71, slotY + (2 * 18), 3 * 18, 234, 18, 18);
		if (slotSize > 0) {
			for (int i = 0; i < slotSize; i++) {
				if (!logic.isStackInSlot(i + 4)) {
					drawTexturedModalRect(cornerX + 27, (cornerY + 7) + (i * 18), 4 * 18, 234, 18, 18);
				}
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		final String title = StatCollector.translateToLocal("crafters.HighOven");

		fontRendererObj.drawString(title, ((xSize / 2) - (fontRendererObj.getStringWidth(title) / 2)) + 10, 5, 0x404040);

		// Player Inventory Caption
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 56, (ySize - 96) + 2, 0x404040);

		FluidStack liquid = getFluidAtPos(mouseX, mouseY);

		if (liquid != null)
			drawFluidStackTooltip(liquid, mouseX , mouseY);
	}

	private HighOvenLogic getLogic() {
		return ((HighOvenContainer) inventorySlots).getLogic();
	}

	// todo: Gradient this
	protected int getTempColor() {
		int temp = getLogic().getInternalTemperature();

		if (temp == 20)
			return 0x404040;
		else if (temp < 1000)
			return 0xFFFF00;
		else if ((temp >= 1000) && (temp <= 2000))
			return 0xFFA500;
		else
			return 0xFF0000;
	}

	// todo: localize
	public static List<String> getLiquidTooltip(FluidStack liquid) {
		List<String> list = new ArrayList<>();

		list.add(EnumChatFormatting.WHITE + liquid.getFluid().getLocalizedName(liquid));

		String name = liquid.getFluid().getName();
		if (name.contains("Emerald")) {
			list.add("Emeralds: " + (liquid.amount / 640f));

			return list;
		}

		if (name.contains("Glass")) {
			int blocks = liquid.amount / 1000;
			if (blocks > 0)
				list.add("Blocks: " + blocks);

			int panels = (liquid.amount % 1000) / 250;
			if (panels > 0)
				list.add("Panels: " + panels);

			int mB = (liquid.amount % 1000) % 250;
			if (mB > 0)
				list.add("mB: " + mB);
		}

		if (name.contains("Molten")) {
			int ingots = liquid.amount / TSRecipes.INGOT_LIQUID_VALUE;
			if (ingots > 0)
				list.add("Ingots: " + ingots);

			int mB = liquid.amount % TSRecipes.INGOT_LIQUID_VALUE;
			if (mB > 0) {
				int nuggets = mB / TSRecipes.NUGGET_LIQUID_VALUE;
				int junk = (mB % TSRecipes.NUGGET_LIQUID_VALUE);

				if (nuggets > 0)
					list.add("Nuggets: " + nuggets);

				if (junk > 0)
					list.add("mB: " + junk);
			}
		}

		return list;
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		FluidStack fluid = getFluidAtPos(mouseX, mouseY);

		if (fluid != null) {
			// todo: send packet fluid down (world, coords, fluid id, isShiftDown)
		}
	}

	private FluidStack getFluidAtPos(int posX, int posY) {
		final int cornerX = (width - xSize) / 2;
		final int cornerY = (height - ySize) / 2;

		MultiFluidTank fluidTank = ((DeepTankContainer) inventorySlots).getLogic().getTank();

		if (fluidTank.getCapacity() == 0) return null;

		final int topY = cornerY + 179;
		final int leftX = cornerX + 68;

		int liquidOffset = 0;
		for (int i = 0; i < fluidTank.getNbFluids(); i++) {
			FluidStack stack = fluidTank.getFluid(i);

			int liquidSize = stack.amount / fluidTank.getCapacity() * TANK_HEIGHT;

			if (posX >= leftX
					&& posX <= leftX + TANK_WIDTH
					&& posY >= topY + liquidOffset
					&& posY < topY + liquidOffset + liquidSize) {
				return stack;
			}

			liquidOffset += liquidSize;
		}

		return null;
	}
}
