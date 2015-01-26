package toops.tsteelworks.client.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import nf.fr.ephys.cookiecore.helpers.RenderHelper;
import nf.fr.ephys.cookiecore.util.MultiFluidTank;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ScalableTankGUI {
	private final int guiLeft;
	private final int guiTop;
	private final int width;
	private final int height;
	private GuiContainer owner;
	private ResourceLocation gauge;
	private int zoomRatioVal = 10;
	private int scroll;

	public ScalableTankGUI(int guiLeft, int guiTop, int width, int height, ResourceLocation gauge, GuiContainer owner) {
		this.guiLeft = guiLeft;
		this.guiTop = guiTop;
		this.width = width;
		this.height = height;
		this.owner = owner;
		this.gauge = gauge;
	}

	private int maxScroll() {
		return (int) ((double)height * (getZoomRatio() - 1));
	}

	public double getZoomRatio() {
		return zoomRatioVal / 10.0D;
	}

	public void scrollUp() {
		scroll += (zoomRatioVal / 10) + 1;

		if (scroll >= maxScroll())
			scroll = maxScroll();
	}

	public void scrollDown() {
		scroll -= (zoomRatioVal / 10) + 1;

		if (scroll < 0) scroll = 0;
	}

	public void zoomIn() {
		zoomRatioVal++;
	}

	public void zoomOut() {
		if (zoomRatioVal == 10) return;
		zoomRatioVal--;

		if (scroll >= maxScroll()) scroll = maxScroll();
	}

	private void drawScrollbar() {
		if (zoomRatioVal == 10) return;

		final int scrollWidth = 4;
		final double scrollHeight = height / getZoomRatio();

		final int scrollPosX = guiLeft + width + 9;

		// scrollbar background
		Gui.drawRect(scrollPosX, guiTop, scrollPosX + scrollWidth, guiTop + height, 0xffffffff);

		// scrollbar
		final double yBottom = guiTop + (height - (scroll / getZoomRatio()));
		final double yTop = yBottom - scrollHeight;
		Gui.drawRect(scrollPosX, (int) yBottom, scrollPosX + scrollWidth, (int) yTop, 0xaa0000ff);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public void renderTank(MultiFluidTank tank, float zLevel) {
		drawScrollbar();

		if (index == 4) {
			GL11.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
		}

		RenderHelper.loadBlockMap();
		float yBottom = guiTop + height - 16;
		double scroll = this.scroll;
		for (int i = 0; i < tank.getNbFluids(); i++) {
			FluidStack liquid = tank.getFluid(i);
			double liquidSize = (double) liquid.amount * height / tank.getCapacity();

			{ // Handle zoom
				liquidSize *= getZoomRatio();
				if (scroll > 0) {
					double toRemove = Math.min(scroll, liquidSize);

					liquidSize -= toRemove;
					scroll -= toRemove;
				}

				// I hope I'll never have to maintain this
				double sizeLeft = yBottom - guiTop + 16;
				if (liquidSize > sizeLeft) {
					liquidSize = sizeLeft;
				}
			} // Zoom done

			if (index == 4) { // easter egg
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

				int col = i % LGBT_COLOR.length;
				GL11.glColor4f(LGBT_COLOR[col][0], LGBT_COLOR[col][1], LGBT_COLOR[col][2], 0xff);
			}

			IIcon icon = liquid.getFluid().getStillIcon();
			if (icon != null) {
				RenderHelper.drawTexturedRect(icon, guiLeft, width, yBottom, (float) liquidSize, zLevel);
			}

			if (index == 4) { // easter egg cleanum
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}

			yBottom -= liquidSize;
		}

		//Liquid gauge
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		owner.mc.getTextureManager().bindTexture(gauge);
		owner.drawTexturedModalRect(guiLeft, guiTop + 1, 120, 0, width, height);
	}

	public FluidStack getFluidAtPos(MultiFluidTank tank, int posX, int posY) {
		final int leftX = guiLeft;

		if (posX < leftX || posX > leftX + width)
			return null;

		if (tank.getCapacity() == 0) return null;

		final int bottomY = guiTop + height;

		double liquidOffset = 0;
		double scroll = this.scroll;
		for (int i = 0; i < tank.getNbFluids(); i++) {
			FluidStack stack = tank.getFluid(i);
			double liquidSize = (double) stack.amount  * height / tank.getCapacity();

			{ // Handle zoom
				liquidSize *= getZoomRatio();
				if (scroll > 0) {
					double toRemove = Math.min(scroll, liquidSize);

					liquidSize -= toRemove;
					scroll -= toRemove;
				}

				// I hope I'll never have to maintain this
				double sizeLeft = bottomY - guiTop + 16;
				if (liquidSize > sizeLeft) {
					liquidSize = sizeLeft;
				}
			} // Zoom done

			if (posY >= bottomY - (liquidSize + liquidOffset) && posY <= bottomY - liquidOffset) {
				return stack;
			}

			liquidOffset += liquidSize;
		}

		return null;
	}

	public List<String> getTooltips(MultiFluidTank fluidTank, int mouseX, int mouseY) {
		FluidStack hoveredStack = getFluidAtPos(fluidTank, mouseX, mouseY);

		return (hoveredStack == null) ? null : getFluidStackTooltip(hoveredStack);
	}

	protected List<String> getFluidStackTooltip(FluidStack liquid) {
		List<String> tooltips = new ArrayList<>();

		tooltips.add(liquid.getFluid().getLocalizedName(liquid));
		tooltips.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("quantity.mb") + ": " + liquid.amount);

		return tooltips;
	}

	private static final float[][] LGBT_COLOR = {
			{228/255f, 3/255f, 3/255f},
			{1, 140/255f, 3/255f},
			{1, 237/255f, 0},
			{0, 128/255f, 38/255f},
			{0, 77/255f,1},
			{117/255f,7/255f,135/255f}
	};
	private char[] keys = {'l', 'g', 'b', 't'};
	private int index = 0;
	/**
	 * this is a stupid easter egg
	 * @param key a character
	 */
	public void sshhhdonttellanyoneaboutthis(char key) {
		if (index == 4) return;

		if (key != keys[index]) {
			index = 0;
			return;
		}

		index++;
	}
}
