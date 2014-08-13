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
import tsteelworks.common.network.PacketMoveFluidHandler;

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
		HighOvenLogic logic = getLogic();
		MultiFluidTank tank = logic.getFluidTank();

		if (tank.getCapacity() != 0) {
			mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

			int xLeft = cornerX + 179;
			int yBottom = cornerY;
			for (int i = 0; i < tank.getNbFluids(); i++) {
				FluidStack liquid = tank.getFluid(i);
				IIcon icon = liquid.getFluid().getStillIcon();

				int liquidSize = liquid.amount / tank.getCapacity() * TANK_HEIGHT;

				DeepTankGui.drawTexturedRect(icon, xLeft, TANK_WIDTH, yBottom, liquidSize, zLevel);

				yBottom += liquidSize;
			}
		}

		// Liquid gague
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(BACKGROUND);
		drawTexturedModalRect(cornerX + 179, cornerY + 16, 176, 76, 35, 52);

		// Burn progress
		if (logic.isBurning()) {
			int scale = logic.getFuelBurnTime() / 42;
			drawTexturedModalRect(cornerX + 127, (cornerY + 36 + 12) - scale, 176, 12 - scale, 14, scale + 2);
		}

		// Side inventory
		int nbSlots = logic.getSmeltableInventory().getSizeInventory();

		if (nbSlots > 0) {
			// Draw Top
			drawTexturedModalRect(cornerX + 16, cornerY, 176, 14, 36, 6);
			// Iterate one slot at a time and draw it. Each slot is 18 px high.
			for (int iter = 0; iter < nbSlots; iter++)
				drawTexturedModalRect(cornerX + 16, (cornerY + 6) + (iter * 18), 176, 21, 36, 18);//(iter * 18) + 18);

			final int dy = nbSlots > 1 ? nbSlots * 18 : 18;
			// Draw Bottom
			drawTexturedModalRect(cornerX + 16, cornerY + 6 + dy, 176, 39, 36, 7);

			// Temperatures & icons
			for (int i = 0; i < nbSlots; i++) {
				int slotTemperature = logic.getTempForSlot(i + HighOvenLogic.SLOT_FIRST_MELTABLE) - 20;
				int maxTemperature = logic.getMeltingPointForSlot(i + HighOvenLogic.SLOT_FIRST_MELTABLE) - 20;

				if (slotTemperature > 0 && maxTemperature > 0) {
					final int size = (16 * slotTemperature / maxTemperature) + 1;
					drawTexturedModalRect(cornerX + 24, (cornerY + 7 + ((i - 4) * 18) + 16) - size, 212, (14 + (15 + 16)) - size, 5, size);
				}

				if (logic.getStackInSlot(i + HighOvenLogic.SLOT_FIRST_MELTABLE) == null) {
					drawTexturedModalRect(cornerX + 27, (cornerY + 7) + (i * 18), 4 * 18, 234, 18, 18);
				}
			}
		}

		final String temp = logic.getInternalTemperature() + "°c";
		fontRendererObj.drawString(temp, (cornerX - (fontRendererObj.getStringWidth(temp) / 2)) + 135, cornerY + 20, getTempColor());

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(ICONS);

		final int slotX = cornerX + 54;
		final int slotY = cornerY + 16;
		for (int i = 0; i < 3; i++) {
			if (logic.getStackInSlot(i) == null)
				drawTexturedModalRect(slotX, slotY + (i * 18), i * 18, 234, 18, 18);
		}

		if (logic.getStackInSlot(3) == null)
			drawTexturedModalRect(slotX + 71, slotY + (2 * 18), 3 * 18, 234, 18, 18);
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
		super.mouseClicked(mouseX, mouseY, mouseButton);

		FluidStack fluid = getFluidAtPos(mouseX, mouseY);

		if (fluid != null) {
			PacketMoveFluidHandler.moveFluidGUI(getLogic(), fluid);
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
