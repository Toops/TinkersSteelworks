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
import tsteelworks.common.container.HighOvenContainer;
import tsteelworks.common.core.TSRecipes;
import tsteelworks.common.network.PacketMoveFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class HighOvenGui extends GuiContainer {
	private static final int TANK_WIDTH = 35;
	private static final int TANK_HEIGHT = 52;

	private static final int TANK_XPOS = 179;
	private static final int TANK_YPOS = 16;

	public static final ResourceLocation BACKGROUND = new ResourceLocation("tsteelworks", "textures/gui/highoven.png");
	public static final ResourceLocation ICONS = new ResourceLocation("tsteelworks", "textures/gui/icons.png");

	public HighOvenGui(InventoryPlayer inventoryplayer, HighOvenLogic highoven) {
		super(new HighOvenContainer(inventoryplayer, highoven));

		xSize = 248;
	}

	protected void drawFluidStackTooltip(FluidStack liquid, int x, int z) {
		List<String> tooltips = getLiquidTooltip(liquid);

		drawHoveringText(tooltips, x, z, fontRendererObj);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(BACKGROUND);

		drawTexturedModalRect(guiLeft + 46, guiTop, 0, 0, 176, ySize);

		// Liquids - molten metal
		HighOvenLogic logic = getLogic();
		MultiFluidTank tank = logic.getFluidTank();

		if (tank.getCapacity() != 0) {
			mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

			float yBottom = guiTop + TANK_HEIGHT;
			for (int i = 0; i < tank.getNbFluids(); i++) {
				FluidStack liquid = tank.getFluid(i);
				IIcon icon = liquid.getFluid().getStillIcon();

				float liquidSize = (float) liquid.amount / tank.getCapacity() * TANK_HEIGHT;

				DeepTankGui.drawTexturedRect(icon, guiLeft + TANK_XPOS, TANK_WIDTH, (int) yBottom, (int) liquidSize, zLevel);

				yBottom -= liquidSize;
			}
		}

		// Liquid gauge
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(BACKGROUND);
		drawTexturedModalRect(guiLeft + TANK_XPOS, guiTop + TANK_YPOS, 176, 76, TANK_WIDTH, TANK_HEIGHT);

		// Burn progress
		if (logic.isBurning()) {
			int scale = logic.getFuelBurnTime() / 42;
			drawTexturedModalRect(guiLeft + 127, (guiTop + 36 + 12) - scale, 176, 12 - scale, 14, scale + 2);
		}

		// Side inventory
		int nbSlots = logic.getSmeltableInventory().getSizeInventory();

		if (nbSlots > 0) {
			// Draw Top
			drawTexturedModalRect(guiLeft + 16, guiTop, 176, 14, 36, 6);
			// Iterate one slot at a time and draw it. Each slot is 18 px high.
			for (int iter = 0; iter < nbSlots; iter++)
				drawTexturedModalRect(guiLeft + 16, (guiTop + 6) + (iter * 18), 176, 21, 36, 18);//(iter * 18) + 18);

			final int dy = nbSlots > 1 ? nbSlots * 18 : 18;
			// Draw Bottom
			drawTexturedModalRect(guiLeft + 16, guiTop + 6 + dy, 176, 39, 36, 7);

			// Temperatures & icons
			for (int i = 0; i < nbSlots; i++) {
				int slotTemperature = logic.getTempForSlot(i) - 20;
				int maxTemperature = logic.getMeltingPointForSlot(i) - 20;

				if (slotTemperature > 0 && maxTemperature > 0) {
					final int size = (16 * slotTemperature / maxTemperature) + 1;
					drawTexturedModalRect(guiLeft + 24, (guiTop + 7 + (i * 18) + 16) - size, 212, (14 + (15 + 16)) - size, 5, size);
				}
			}
		}

		final String temp = logic.getInternalTemperature() + "°c";
		fontRendererObj.drawString(temp, (guiLeft - (fontRendererObj.getStringWidth(temp) / 2)) + 135, guiTop + 20, getTempColor());

		// draw slot icons
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(ICONS);

		final int slotX = guiLeft + 54;
		final int slotY = guiTop + TANK_YPOS;
		for (int i = 0; i < 3; i++) {
			if (logic.getStackInSlot(i) == null)
				drawTexturedModalRect(slotX, slotY + (i * 18), i * 18, 234, 18, 18);
		}

		if (logic.getStackInSlot(3) == null)
			drawTexturedModalRect(slotX + 71, slotY + (2 * 18), 3 * 18, 234, 18, 18);

		for (int i = 0; i < nbSlots; i++) {
			if (logic.getSmeltableInventory().getStackInSlot(i) == null) {
				drawTexturedModalRect(guiLeft + 27, (guiTop + 7) + (i * 18), 4 * 18, 234, 18, 18);
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
			drawFluidStackTooltip(liquid, mouseX - guiLeft, mouseY - guiTop);
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

		if (name.contains("emerald")) {
			list.add(EnumChatFormatting.GRAY + "Emeralds: " + (liquid.amount / 640f));
		} else if (name.contains("glass")) {
			int blocks = liquid.amount / 1000;
			if (blocks > 0)
				list.add(EnumChatFormatting.GRAY + "Blocks: " + blocks);

			int panels = (liquid.amount % 1000) / 250;
			if (panels > 0)
				list.add(EnumChatFormatting.GRAY + "Panels: " + panels);

			int mB = (liquid.amount % 1000) % 250;
			if (mB > 0)
				list.add(EnumChatFormatting.GRAY + "mB: " + mB);
		} else if (name.contains("molten")) {
			int ingots = liquid.amount / TSRecipes.INGOT_LIQUID_VALUE;
			if (ingots > 0)
				list.add(EnumChatFormatting.GRAY + "Ingots: " + ingots);

			int mB = liquid.amount % TSRecipes.INGOT_LIQUID_VALUE;
			if (mB > 0) {
				int nuggets = mB / TSRecipes.NUGGET_LIQUID_VALUE;
				int junk = (mB % TSRecipes.NUGGET_LIQUID_VALUE);

				if (nuggets > 0)
					list.add(EnumChatFormatting.GRAY + "Nuggets: " + nuggets);

				if (junk > 0)
					list.add(EnumChatFormatting.GRAY + "mB: " + junk);
			}
		} else {
			list.add(EnumChatFormatting.GRAY + "mB: " + liquid.amount);
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
		MultiFluidTank fluidTank = ((HighOvenContainer) inventorySlots).getLogic().getFluidTank();

		if (fluidTank.getCapacity() == 0) return null;

		final int bottomY = guiTop + TANK_YPOS + TANK_HEIGHT;
		final int leftX = guiLeft + TANK_XPOS;

		float liquidOffset = 0;
		for (int i = 0; i < fluidTank.getNbFluids(); i++) {
			FluidStack stack = fluidTank.getFluid(i);

			float liquidSize = (float) stack.amount / fluidTank.getCapacity() * TANK_HEIGHT;

			if (posX >= leftX && posX <= leftX + TANK_WIDTH &&
				posY >= bottomY - (liquidSize + liquidOffset) && posY < bottomY - liquidOffset) {
				return stack;
			}

			liquidOffset += liquidSize;
		}

		return null;
	}
}
