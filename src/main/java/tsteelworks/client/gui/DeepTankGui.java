package tsteelworks.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import nf.fr.ephys.cookiecore.helpers.RenderHelper;
import nf.fr.ephys.cookiecore.util.MultiFluidTank;
import org.lwjgl.opengl.GL11;
import tsteelworks.common.blocks.logic.DeepTankLogic;
import tsteelworks.common.container.DeepTankContainer;
import tsteelworks.common.network.PacketMoveFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class DeepTankGui extends GuiContainer {
	public static final ResourceLocation BACKGROUND = new ResourceLocation("tsteelworks", "textures/gui/deeptank.png");

	private static final int TANK_WIDTH = 104;
	private static final int TANK_HEIGHT = 104;

	public DeepTankGui(InventoryPlayer inventoryplayer, DeepTankLogic tank) {
		super(new DeepTankContainer(inventoryplayer, tank));

		xSize = 248;
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		final String title = StatCollector.translateToLocal("tank.DeepTank");

		fontRendererObj.drawString(title, ((xSize / 2) - (fontRendererObj.getStringWidth(title) / 2)), 17, 0x404040);

		FluidStack hoveredStack = getFluidAtPos(mouseX, mouseY);

		if (hoveredStack != null)
			drawFluidStackTooltip(hoveredStack, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(BACKGROUND);
		int cornerX = (width - xSize) / 2 + 20;
		int cornerY = (height - ySize) / 2 + 12;
		drawTexturedModalRect(cornerX + 46, cornerY, 0, 0, 120, ySize);

		//Liquids
		RenderHelper.loadBlockMap();

		final int leftX = cornerX + 54;

		MultiFluidTank fluidTank = ((DeepTankContainer) inventorySlots).getLogic().getTank();

		int liquidOffset = 0;
		for (int i = 0; i < fluidTank.getNbFluids(); i++) {
			FluidStack stack = fluidTank.getFluid(i);
			IIcon icon = stack.getFluid().getStillIcon();

			int liquidSize = stack.amount / fluidTank.getCapacity() * TANK_HEIGHT;

			drawTexturedRect(icon, leftX, TANK_WIDTH, cornerY + 120 + liquidOffset, liquidSize, 1);

			liquidOffset += liquidSize;
		}

		//Liquid gauge
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(BACKGROUND);
		drawTexturedModalRect(cornerX + 54, cornerY + 16, 120, 0, 104, 104);
	}

	protected void drawFluidStackTooltip(FluidStack liquid, int x, int z) {
		List<String> tooltips = new ArrayList<>();

		tooltips.add(liquid.getFluid().getLocalizedName(liquid));
		tooltips.add(EnumChatFormatting.LIGHT_PURPLE + "mB: " + liquid.amount);

		drawHoveringText(tooltips, x, z, fontRendererObj);
	}

	// TODO: move this to RenderHelper
	public static void drawTexturedRect(IIcon icon, int x, int width, int y, int height, float zIndex) {
		int nbChunksX = width / 16;
		int nbChunksY = height / 16;

		int xRemainer = width % 16;
		int yRemainer = height % 16;

		for (int i = 0; i < nbChunksX; i++) {
			int xStart = x + 16 * i;
			for (int j = 0; j < nbChunksY; j++) {
				int yStart = y - 16 * j;

				drawTexturedRectStretch(icon, xStart, 16, yStart, 16, zIndex);
			}

			// draw Y remainder (horizontal line)
			int yStart = y - 16 * nbChunksY;

			drawTexturedRectStretch(icon, xStart, 16, yStart + (16 - yRemainer), yRemainer, zIndex);
		}

		// draw X remainder (vertical line)
		int xStart = x + 16 * nbChunksX;
		for (int i = 0; i < nbChunksY; i++) {
			int yStart = y - 16 * i;

			drawTexturedRectStretch(icon, xStart, xRemainer, yStart, 16, zIndex);
		}

		// draw the corner
		int yStart = y - 16 * nbChunksY;

		drawTexturedRectStretch(icon, xStart, xRemainer, yStart + (16 - yRemainer), yRemainer, zIndex);
	}

	/**
	 * Draw a textured rectangle with a stretched texture to fit the cube
	 */
	// TODO: move this to RenderHelper
	public static void drawTexturedRectStretch(IIcon icon, int x, int width, int y, int height, float zIndex) {
		float iconMinU = icon.getMinU();
		float iconMaxU = icon.getInterpolatedU(width);
		float iconMinV = icon.getInterpolatedV(16 - height);
		float iconMaxV = icon.getMaxV();

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + height, zIndex, iconMinU, iconMaxV);
		tessellator.addVertexWithUV(x + width, y + height, zIndex, iconMaxU, iconMaxV);
		tessellator.addVertexWithUV(x + width, y, zIndex, iconMaxU, iconMinV);
		tessellator.addVertexWithUV(x, y, zIndex, iconMinU, iconMinV);
		tessellator.draw();
	}

	private FluidStack getFluidAtPos(int posX, int posY) {
		final int cornerX = (width - xSize) / 2 + 20;
		final int cornerY = (height - ySize) / 2 + 12;

		MultiFluidTank fluidTank = ((DeepTankContainer) inventorySlots).getLogic().getTank();

		if (fluidTank.getCapacity() == 0) return null;

		final int topY = cornerY + 120;
		final int leftX = cornerX + 54;

		float liquidOffset = 0;
		for (int i = 0; i < fluidTank.getNbFluids(); i++) {
			FluidStack stack = fluidTank.getFluid(i);

			float liquidSize = (float) stack.amount / fluidTank.getCapacity() * TANK_HEIGHT;

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

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		FluidStack fluid = getFluidAtPos(mouseX, mouseY);

		if (fluid == null) return;

		DeepTankLogic logic = ((DeepTankContainer) inventorySlots).getLogic();

		PacketMoveFluidHandler.moveFluidGUI(logic, fluid);
	}
}
