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
	private static final double MAX_ZOOM = 10;
	private double zoomRatio = 1;
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
		return (int) ((double)height * (zoomRatio - 1));
	}

	public void scrollUp() {
		scroll += (int) zoomRatio;

		if (scroll >= maxScroll())
			scroll = maxScroll();
	}
	
	public void scrollDown() {
		scroll -= (int) zoomRatio;
		
		if (scroll < 0) scroll = 0;
	}

	public void zoomIn() {
		if (zoomRatio >= MAX_ZOOM) return;
		zoomRatio += 0.1;
	}

	public void zoomOut() {
		if (zoomRatio <= 1) return;

		zoomRatio -= 0.1;

		if (scroll >= maxScroll()) scroll = maxScroll();
	}
	
	private void drawScrollbar() {
		if (zoomRatio == 1) return;

		final int scrollWidth = 4;
		final double scrollHeight = height / zoomRatio;

		final int scrollPosX = guiLeft + width + 9;
		
		// scrollbar background
		Gui.drawRect(scrollPosX, guiTop, scrollPosX + scrollWidth, guiTop + height, 0xffffffff);

		// scrollbar
		final double yBottom = guiTop + (height - (scroll / zoomRatio));
		final double yTop = yBottom - scrollHeight;
		Gui.drawRect(scrollPosX, (int) yBottom, scrollPosX + scrollWidth, (int) yTop, 0xaa0000ff);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public void renderTank(MultiFluidTank tank, float zLevel) {
		drawScrollbar();
		
		RenderHelper.loadBlockMap();
		float yBottom = guiTop + height - 16;
		double scroll = this.scroll;
		for (int i = 0; i < tank.getNbFluids(); i++) {
			FluidStack liquid = tank.getFluid(i);
			float liquidSize = (float) liquid.amount * height / tank.getCapacity();

			{ // Handle zoom
				liquidSize *= zoomRatio;
				if (scroll > 0) {
					double toRemove = Math.min(scroll, liquidSize);

					liquidSize -= toRemove;
					scroll -= toRemove;
				}

				// I hope I'll never have to maintain this
				float sizeLeft = yBottom - guiTop + 16;
				if (liquidSize > sizeLeft) {
					liquidSize = sizeLeft;
				}
			} // Zoom done

			IIcon icon = liquid.getFluid().getStillIcon();
			if (icon != null) {
				RenderHelper.drawTexturedRect(icon, guiLeft, width, yBottom, liquidSize, zLevel);
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

		float liquidOffset = 0;
		for (int i = 0; i < tank.getNbFluids(); i++) {
			FluidStack stack = tank.getFluid(i);
			float liquidSize = (float) stack.amount  * height / tank.getCapacity();

			{ // Handle zoom
				liquidSize *= zoomRatio;
				if (scroll > 0) {
					double toRemove = Math.min(scroll, liquidSize);

					liquidSize -= toRemove;
					scroll -= toRemove;
				}

				// I hope I'll never have to maintain this
				float sizeLeft = bottomY - guiTop + 16;
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
}
